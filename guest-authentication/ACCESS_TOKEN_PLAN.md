# Plan: Add `X-Krista-Access-Token` Header to `doDecorateRequest`

## Goal

Add the `X-Krista-Access-Token` header inside `doDecorateRequest()` in `authenticator.js`
so that Dependent Extensions (media calls) can authenticate using it.

`accessToken` is produced on the Java side via `JwtSessionManager.createAccessToken(refreshToken)`,
where the existing `clientSessionId` (session token) acts as the refresh token.
The token is piggybacked on the login response through the already-present `extras` map
(no structural change to `AuthenticationResponse.java`), then read by JS from `context.extras.accessToken`.

---

## Decisions (all confirmed)

| # | Question | Decision |
|---|----------|----------|
| 1 | JwtSessionManager import path | `app.krista.ksdk.authentication.JwtSessionManager` (same package as `SessionManager`) |
| 2 | Method signature | `String createAccessToken(String refreshToken)` — returns `String` directly |
| 3 | Token expiry | 60-minute default; **refresh mechanism deferred** — see Future Tasks below |
| 4 | Response shape | Use existing `extras` map — **no structural change** to `AuthenticationResponse.java` |

---

## Data Flow

```
ExtensionResource.login()
  1. sessionManager.createSession(accountId)       →  sessionId  (= clientSessionId)
  2. jwtSessionManager.createAccessToken(sessionId) →  accessToken
  3. createAuthenticationResponse(account, sessionId, accessToken)
       puts accessToken into extras map
  4. Return JSON: { ..., extras: { creationTime, newSession, accessToken } }

authenticator.js  doLogin()
  context = data                                    →  context.extras.accessToken is available

doDecorateRequest()
  headers["X-Krista-Access-Token"] = context.extras.accessToken
```

---

## Changes

### 1. `util/Constants.java`
Add constant for the new header name:
```java
public static final String X_KRISTA_ACCESS_TOKEN = "X-Krista-Access-Token";
```

---

### 2. `rest/ExtensionResource.java`

**Import:**
```java
import app.krista.ksdk.authentication.JwtSessionManager;
```

**Field + constructor:**
```java
private final JwtSessionManager jwtSessionManager;

@Inject
public ExtensionResource(..., JwtSessionManager jwtSessionManager, ...) {
    ...
    this.jwtSessionManager = jwtSessionManager;
}
```

**In `login()` — after session creation:**
```java
String sessionId = sessionManager.createSession(account.getAccountId());
String accessToken = jwtSessionManager.createAccessToken(sessionId);   // ← new
```

**Updated `createAuthenticationResponse()` — accepts and embeds accessToken:**
```java
private AuthenticationResponse createAuthenticationResponse(
        Account account, String sessionId, String accessToken) throws AuthorizationException {
    return new AuthenticationResponse(
            sessionId, ...,
            Map.of("creationTime", getCurrentDateTime(),
                   "newSession",   true,
                   "accessToken",  accessToken));
}
```

---

### 3. `src/main/resources/authenticator.js`

Fix two bugs on the existing line 54 — syntax error (`;` inside object) and undefined `accessToken`:

```js
// BEFORE (broken)
"X-Krista-Access-Token" : accessToken;

// AFTER (fixed)
"X-Krista-Access-Token": context.extras && context.extras.accessToken
```

Full corrected `doDecorateRequest`:
```js
const doDecorateRequest = () => {
    let headers = {};
    if (context && context.clientSessionId) {
        headers = {
            "X-Krista-Context": encodeURIComponent(
                JSON.stringify({ clientSessionId: context.clientSessionId })
            ),
            "X-Krista-Access-Token": context.extras && context.extras.accessToken
        };
    }
    return headers;
};
```

---

## Files Changed

| File | What changes |
|------|-------------|
| `util/Constants.java` | Add `X_KRISTA_ACCESS_TOKEN` constant |
| `rest/ExtensionResource.java` | Inject `JwtSessionManager`; generate `accessToken` in `login()`; pass to extras |
| `src/main/resources/authenticator.js` | Fix syntax error; reference `context.extras.accessToken` |
| `AuthenticationResponse.java` | **No change** — `extras` map already supports arbitrary keys |

---

## Future Tasks

### FT-1: Access Token Refresh Mechanism
- **Context**: `JwtSessionManager.createAccessToken()` produces tokens with a default 60-minute expiry.
  Currently the JS client holds the token obtained at login time with no refresh.
- **What needs to be done**:
  1. Add a `POST /v1/refreshAccessToken` endpoint in `ExtensionResource` that accepts the
     `clientSessionId` (from cookie/header) and returns a fresh `accessToken` string.
  2. In `authenticator.js`, track `accessTokenExpiry` alongside `accessToken` in `context.extras`.
  3. In `doDecorateRequest()` (or a wrapper called before each request), check if the token
     is near expiry and call the refresh endpoint proactively.
  4. Alternatively, intercept 401 responses from Dependent Extensions and trigger a refresh
     then retry.
- **Dependency**: Confirm whether `JwtSessionManager` exposes a method to inspect token expiry
  (e.g., `getAccessTokenExpiry(token)`) or whether expiry must be parsed from the JWT itself.