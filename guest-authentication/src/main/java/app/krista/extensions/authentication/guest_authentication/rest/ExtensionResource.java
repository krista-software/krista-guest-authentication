/*
 * Guest Authentication Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.authentication.guest_authentication.rest;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import app.krista.extension.authorization.AuthorizationException;
import app.krista.extension.common.AuthenticationResponse;
import app.krista.extension.common.CommonUtils;
import app.krista.extension.common.EmailAddresses;
import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extension.util.InvokerAttributeProvider;
import app.krista.extensions.authentication.guest_authentication.util.Constants;
import app.krista.ksdk.accounts.Account;
import app.krista.ksdk.accounts.ModifiableAccount;
import app.krista.ksdk.authentication.AuthenticationSettings;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.authorization.Role;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RuntimeContext;
import app.krista.omni.sdk.spi.impl.services.AccountService;
import app.krista.omni.sdk.spi.impl.util.Strings;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static app.krista.extensions.authentication.guest_authentication.rest.GuestAuthenticationExtensionConstants.*;
import static javax.ws.rs.core.Cookie.*;
import static javax.ws.rs.core.HttpHeaders.*;

@SuppressWarnings("deprecation")
@Path("/")
public class ExtensionResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionResource.class);

    private final SessionManager sessionManager;
    private final AccountProvisioner accountProvisioner;
    private final RuntimeContext runtimeContext;
    private final GuestAuthenticationRoleManager guestAuthenticationRoleProvisioner;
    private final AuthenticationSettings authenticationSettings;
    private final Invoker invoker;
    private final InvokerAttributeProvider<Double> sessionTimeout;
    private final AccountManager accountManager;
    private static final String EMAIL_PREFIX = "guest";
    private static final String UNDERSCORE = "_";
    private final AuthorizationContext authorizationContext;

    @Inject
    public ExtensionResource(SessionManager sessionManager, AccountProvisioner accountProvisioner,
                             RuntimeContext runtimeContext, GuestAuthenticationRoleManager guestAuthenticationRoleProvisioner,
                             AuthenticationSettings authenticationSettings,
                             @Named("self") Invoker invoker, InvokerAttributeProvider<Double> sessionTimeout,
                             AccountManager accountManager, AuthorizationContext authorizationContext) {
        this.sessionManager = sessionManager;
        this.accountProvisioner = accountProvisioner;
        this.runtimeContext = runtimeContext;
        this.guestAuthenticationRoleProvisioner = guestAuthenticationRoleProvisioner;
        this.authenticationSettings = authenticationSettings;
        this.invoker = invoker;
        this.sessionTimeout = sessionTimeout;
        this.accountManager = accountManager;
        this.authorizationContext = authorizationContext;
    }

    @GET
    @Path("authenticator.js")
    public InputStream getAuthenticator() {
        return getClass().getClassLoader().getResourceAsStream("authenticator.js");
    }

    @GET
    @Path("/type")
    public String getAuthType() {
        return AUTHENTICATION_TYPE;
    }

    @OPTIONS
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginOptions(@Context HttpHeaders headers, String email) {
        return getResponseBuilderWithCORSHeaders().build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam(Constants.SOURCE) String source,
                          @CookieParam(Constants.CHATBOT_SESSION_ID) Cookie clientSessionId,
                          @HeaderParam(Constants.CALLER_URI) String callerUri,
                          Map<String, String> loginInput)
            throws AuthorizationException, URISyntaxException {

        System.out.println("#####-LOGIN INPUT: " + loginInput);
        System.out.println("#####-runtimeContext.getKristaAccount().getAccountId() = " + runtimeContext.getKristaAccount().getAccountId());
        System.out.println("#####-authorizationContext.getAuthorizedAccount().getAccountId() = " + authorizationContext.getAuthorizedAccount().getAccountId());

        if (!runtimeContext.getKristaAccount().getAccountId()
                .equals(authorizationContext.getAuthorizedAccount().getAccountId())) {
            AuthenticationResponse authenticationResponse = accountManager.get(clientSessionId.getValue());
            if (authenticationResponse == null) {
                LOGGER.error("#####-AUTHENTICATION RESPONSE IS NULL FOR CLIENT SESSION ID: {}", clientSessionId.getValue());
            } else {
                LOGGER.warn("******* authentication response:{} and account Id:{}", authenticationResponse,
                        authorizationContext.getAuthorizedAccount().getAccountId());
                authenticationResponse.getExtras().remove("newSession");
                return getResponseBuilderWithCORSHeaders()
                        .entity(GSON_JSON_MAPPER.toString(authenticationResponse))
                        .build();
            }
        }
        if (!Objects.isNull(clientSessionId) && !clientSessionId.getValue().isBlank()) {
            accountManager.delete(clientSessionId.getValue());
        }
        String email;
        if (!Objects.isNull(source) && !source.isBlank()) {
            email = getEmail(true);
        } else {
            email = getEmail(false);
        }
        addDomainToWorkspaceIfNotPresent(email);
        Account account = accountProvisioner.provisionAccount(email);

        String sessionId = sessionManager.createSession(account.getAccountId());
        URI httpURI = resolveCallerUri(callerUri);
        // Safely get last 3 segments and join them with "/"
        String cookiePath = getCookiePath(httpURI);
        AuthenticationResponse authenticationResponse = createAuthenticationResponse(account, sessionId);
        accountManager.put(sessionId, authenticationResponse);

        // final boolean secure = true; // Change to false if not using HTTPS
        int maxAgeInSeconds = getMaxAgeInSeconds();
        Date expiry = new Date(System.currentTimeMillis() + maxAgeInSeconds * 1000L);

        String chatbotSessionCookie = createCookieString(
                Constants.CHATBOT_SESSION_ID, sessionId, cookiePath + "/",
                httpURI.getHost(), maxAgeInSeconds, expiry.toString());

        String kristaContextCookie = createCookieString(
                Constants.X_KRISTA_CONTEXT, getEncodedClientSessionId(sessionId), "/",
                httpURI.getHost(), maxAgeInSeconds, expiry.toString());
        // Building the response with CORS headers and cookie
        return getResponseBuilderWithCORSHeaders()
                .header(SET_COOKIE, chatbotSessionCookie)
                .header(SET_COOKIE, kristaContextCookie)
                .header(Constants.CHATBOT_SESSION_ID, sessionId)
                .entity(GSON_JSON_MAPPER.toString(authenticationResponse))
                .build();
    }

    @POST
    @Path("/upsertPersonAttributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void upsertPersonAttributes(@HeaderParam(Constants.SOURCE) String source,
                                       @CookieParam(Constants.CHATBOT_SESSION_ID) Cookie clientSessionId,
                                       @HeaderParam(Constants.CHATBOT_SESSION_ID_HEADER) String clientSessionIdHeader,
                                       @HeaderParam(Constants.CALLER_URI) String callerUri,
                                       Map<String, String> loginInput) throws AuthorizationException {
        final String sessionId = getSessionId(clientSessionId, clientSessionIdHeader);
        String accountId = accountManager.get(sessionId).getAccountId();
        this.upsertPersonAttributes(accountId, loginInput);
    }

    private static String getSessionId(Cookie clientSessionId, String clientSessionIdHeader) {
        String sessionId;
        if (clientSessionId != null) {
            sessionId = clientSessionId.getValue();
        } else {
            sessionId = clientSessionIdHeader;
        }
        return sessionId;
    }

    private void upsertPersonAttributes(String accountId, Map<String, String> attributesMap) {
        if (attributesMap != null && !attributesMap.isEmpty()) {
            final ModifiableAccount modifiableAccount = accountProvisioner.getAccountManager().getAccount(accountId);
            System.out.println("#####-TRYING TO ADD ATTRIBUTES...");
            attributesMap.forEach((key, value) -> {
                if (value != null) {
                    modifiableAccount.updateAttributeValue(key, value);
                    System.out.println("#####-ATTRIBUTE ADDED " + key);
                }
            });
        }
    }

    private URI resolveCallerUri(String callerUri) throws URISyntaxException {
        String routingURL =
                invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE);
        return new URI((!Objects.isNull(callerUri) && !callerUri.isBlank()) ? callerUri : routingURL);
    }

    private String getCookiePath(URI httpURI) {
        String path = httpURI.getPath();
        String[] parts = path.split("/");
        int len = parts.length;

        return "/" + parts[len - 3] + "/" + parts[len - 2] + "/" + parts[len - 1];
    }

    private String getEmail(boolean isOmni) {
        return EMAIL_PREFIX + UNDERSCORE + UUID.randomUUID() + Strings.AT
                + (isOmni ? AccountService.SUPPORTED_DOMAIN : EmailAddresses.DEFAULT_DOMAIN);
    }

    @OPTIONS
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logoutOptions(@Context javax.ws.rs.core.HttpHeaders headers, LogoutDTO logoutDTO) {
        try {
            return getResponseBuilderWithCORSHeaders().build();
        } catch (Exception cause) {
            throw new IllegalStateException("Failed to logout.", cause);
        }
    }

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@Context javax.ws.rs.core.HttpHeaders headers, LogoutDTO logoutDTO) {
        try {
            String clientSessionId = logoutDTO.getClientSessionId();
            if (clientSessionId == null || clientSessionId.isEmpty()) {
                throw new IllegalArgumentException("Missing client session id.");
            }
            sessionManager.deleteSession(clientSessionId);
            Response.ResponseBuilder responseBuilderWithCORSHeaders =
                    getResponseBuilderWithCORSHeaders();
            return responseBuilderWithCORSHeaders.entity("Successfully logged out.").build();
        } catch (Exception cause) {
            throw new IllegalStateException("Failed to logout.", cause);
        }
    }

    @POST
    @Path("/v1/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("chatbotSessionId") Cookie chatbotSessionCookie,
                           @CookieParam("X-Krista-Context") Cookie kristaContextCookie,
                           @HeaderParam(Constants.CALLER_URI) String callerUri,
                           @HeaderParam(Constants.CHATBOT_SESSION_ID_HEADER) String clientSessionIdHeader) throws URISyntaxException {
        final String sessionId = getSessionId(chatbotSessionCookie, clientSessionIdHeader);

        // Delete the session if cookie exists
        if (sessionId != null) {
            accountManager.delete(sessionId);
        }

        URI httpURI = resolveCallerUri(callerUri);
        String cookiePath = getCookiePath(httpURI);

        // Build response to clear cookies
        return getResponseBuilderWithCORSHeaders()
                .header(SET_COOKIE, createExpiredCookie(Constants.CHATBOT_SESSION_ID, cookiePath + "/", httpURI.getHost()))
                .header(SET_COOKIE, createExpiredCookie(Constants.X_KRISTA_CONTEXT, "/", httpURI.getHost()))
                .entity("Logout successful from guest authentication.") // Or null, or other logout message
                .build();
    }

    private void addDomainToWorkspaceIfNotPresent(String email) throws AuthorizationException {
        List<String> supportedDomainsForWorkspace = authenticationSettings.getSupportedDomains();
        String supportedDomain = String.join(",", supportedDomainsForWorkspace);
        String invokerDomains = EmailAddresses.getDomainName(email);
        CommonUtils.validateIfSupportedDomain(email, supportedDomain, invokerDomains);
        CommonUtils.addSupportedDomainsToWorkspace(email, supportedDomainsForWorkspace, authenticationSettings);
    }

    private AuthenticationResponse createAuthenticationResponse(Account account, String sessionId)
            throws AuthorizationException {
        return new AuthenticationResponse(sessionId, account.getPerson().getPersonName(),
                account.getPerson().getAvatarUrl(), account.getAccountId(),
                runtimeContext.getKristaAccount().getAccountId(), account.getPerson().getPersonId(),
                getListOfRoles(account.getRoles()), account.getInboxId(),
                guestAuthenticationRoleProvisioner.hasAdminRole(account),
                false,
                Map.of("email", account.getPrimaryEmailAddress()),
                Map.of("creationTime", getCurrentDateTime(), "newSession", true));
    }

    private List<String> getListOfRoles(Set<Role> roles) {
        List<String> allRoles = new ArrayList<>();
        for (Role role : roles) {
            allRoles.add(role.getRoleId());
        }
        return allRoles;
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private Response.ResponseBuilder getResponseBuilderWithCORSHeaders() {
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .header("Access-Control-Allow-Credentials", true);
    }

    private int getMaxAgeInSeconds() {
        Double sessionTimeOut = sessionTimeout.get();
        int maxAgeInSeconds;
        if (sessionTimeOut == null) {
            maxAgeInSeconds = 24 * 60 * 60; // 1 day
        } else {
            long timeout = (long) sessionTimeOut.doubleValue();
            maxAgeInSeconds = (int) TimeUnit.MINUTES.toSeconds(timeout);
        }
        return maxAgeInSeconds;
    }

    private String getEncodedClientSessionId(String sessionId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clientSessionId", sessionId);
        return URLEncoder.encode(jsonObject.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Creates a formatted cookie string with specified attributes.
     * This method can be used for both setting and expiring cookies.
     *
     * @param cookieName the name of the cookie
     * @param cookieValue the value of the cookie (empty string for expired cookies)
     * @param path the path for the cookie
     * @param domain the domain for the cookie
     * @param maxAge the max age in seconds (0 for immediate expiration)
     * @param expires the expiration date string
     * @return formatted cookie string
     */
    private String createCookieString(String cookieName, String cookieValue, String path,
                                    String domain, int maxAge, String expires) {
        return cookieName + "=" + cookieValue + "; " +
                "Version=" + DEFAULT_VERSION + "; " +
                "Comment=" + Constants.COMMENT + "; " +
                "Path=" + path + "; " +
                "Domain=" + domain + "; " +
                "Max-Age=" + maxAge + "; " +
                "Expires=" + expires + "; " +
                "Secure; HttpOnly; SameSite=None;";
    }

    /**
     * Creates an expired cookie string for clearing client-side authentication data.
     *
     * @param cookieName the name of the cookie to expire
     * @param path the path for the cookie
     * @param domain the domain for the cookie
     * @return formatted expired cookie string
     */
    private String createExpiredCookie(String cookieName, String path, String domain) {
        String expiredDate = "Thu, 01 Jan 1970 00:00:00 GMT";
        return createCookieString(cookieName, "", path, domain, 0, expiredDate);
    }
}
