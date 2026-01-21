# Authentication

## Overview

The Guest Authentication Extension provides a simplified authentication mechanism that allows users to access Krista applications without traditional login credentials. This page explains the authentication flow, user provisioning, and security considerations.

## Authentication Modes

### Guest Authentication Mode

The Guest Authentication Extension operates in a single authentication mode:

**Automatic Guest Authentication**
- No user credentials required
- Automatic user creation and provisioning
- Immediate access to configured resources
- Session-based authentication

**When to Use**:
- Public demos and proof-of-concepts
- Public-facing portals with limited functionality
- Testing and development environments
- Applications where user identity tracking is not critical

**When NOT to Use**:
- Production applications requiring user accountability
- Applications handling sensitive or personal data
- Multi-tenant environments requiring user isolation
- Applications requiring audit trails of individual user actions

## Authentication Flow

### Step-by-Step Authentication Process

```
1. User Access Request
   ↓
2. Extension Checks Configuration
   ↓
3. Retrieve/Create Guest User
   ↓
4. Assign Default Role
   ↓
5. Apply Person Attributes
   ↓
6. Create Session
   ↓
7. Grant Access
```

### Detailed Flow Description

#### Step 1: User Access Request

When a user attempts to access an application using Guest Authentication:
- The application initiates an authentication request
- No credentials are required from the user
- The request is routed to the Guest Authentication Extension

#### Step 2: Configuration Retrieval

The extension retrieves configuration parameters:
- **Default User**: Email address for the guest account
- **Default Role**: Role to be assigned
- **Attribute Parameters**: Custom person attributes

#### Step 3: User Provisioning

The extension checks if the guest user exists:

**If User Exists**:
- Retrieve existing user account
- Verify user is active
- Proceed to role assignment

**If User Doesn't Exist**:
- Create new user account with configured email
- Add email domain to workspace if not present
- Set user status to active
- Proceed to role assignment

#### Step 4: Role Assignment

The extension assigns the configured default role:
- Validates role exists in workspace
- Assigns role to guest user
- Grants associated permissions

#### Step 5: Person Attributes Application

If custom person attributes are configured:
- Parse JSON attribute configuration
- Validate attributes exist in workspace
- Apply attributes to user profile
- Store attribute values

#### Step 6: Session Creation

The extension creates an authenticated session:
- Generate session token
- Associate session with guest user
- Set session expiration (based on workspace settings)
- Return session credentials to application

#### Step 7: Access Granted

The user is granted access:
- Application receives authentication confirmation
- User can access resources based on role permissions
- Session is maintained for subsequent requests

## User Provisioning

### Automatic User Creation

The Guest Authentication Extension automatically creates users when they don't exist:

**User Creation Process**:
1. Check if user with configured email exists
2. If not, create new user account
3. Set user properties:
   - Email: From configuration
   - Status: Active
   - Type: Guest User
4. Add user to workspace

**Domain Provisioning**:
- Extract domain from email address (e.g., `example.com` from `guest@example.com`)
- Check if domain exists in workspace
- If not, automatically add domain to workspace
- Associate user with domain

### User Account Properties

Guest users are created with the following properties:

| Property | Value | Description |
|----------|-------|-------------|
| Email | Configured email or `guest@kristasoft.com` | User's email address |
| Status | Active | User account status |
| Type | Guest | User type identifier |
| Role | Configured role or `Krista Client User` | Assigned role |
| Person Attributes | From configuration | Custom attributes |
| Domain | Extracted from email | User's domain |

## CORS Support

The Guest Authentication Extension includes built-in CORS (Cross-Origin Resource Sharing) support for web applications.

### CORS Headers

The extension automatically sets the following CORS headers:

| Header | Value | Purpose |
|--------|-------|---------|
| `Access-Control-Allow-Origin` | `*` | Allows requests from any origin |
| `Access-Control-Allow-Methods` | `POST, GET, OPTIONS` | Allowed HTTP methods |
| `Access-Control-Allow-Headers` | `Content-Type` | Allowed request headers |

### CORS Flow

```
1. Browser sends OPTIONS preflight request
   ↓
2. Extension responds with CORS headers
   ↓
3. Browser validates CORS policy
   ↓
4. Browser sends actual request (GET/POST)
   ↓
5. Extension processes request with CORS headers
```

### CORS Configuration

> **📝 Note**: CORS headers are automatically configured and cannot be customized through extension configuration.

**Default Behavior**:
- All origins are allowed (`*`)
- GET, POST, and OPTIONS methods are supported
- Content-Type header is allowed

**Security Considerations**:
- The wildcard origin (`*`) allows any website to make requests
- Consider network-level restrictions for production environments
- Use API gateways or reverse proxies for additional CORS control

## Session Management

### Session Creation

When a guest user is authenticated:
- A new session is created
- Session token is generated
- Session is associated with the guest user account
- Session expiration is set based on workspace configuration

### Session Lifecycle

```
Authentication → Session Created → Active Session → Session Expires → Re-authentication
```

**Session Duration**:
- Determined by workspace session timeout settings
- Typically 30 minutes to 24 hours
- Configurable in workspace settings

**Session Renewal**:
- Sessions are automatically renewed on activity
- Inactive sessions expire based on timeout
- Expired sessions require re-authentication

### Session Termination

Sessions can be terminated by:
- User logout (if implemented in application)
- Session timeout due to inactivity
- Workspace administrator action
- Extension redeployment

## Security Best Practices

### Access Control

> **⚠️ Warning**: Guest users share the same identity when using default configuration.

**Recommendations**:
1. **Create Dedicated Guest Role**
   - Define specific permissions for guest users
   - Limit access to sensitive data
   - Enable read-only access where possible

2. **Use Custom Email Addresses**
   - Configure unique email for each environment
   - Use descriptive emails (e.g., `demo-guest@company.com`)
   - Avoid using production email domains

3. **Implement Rate Limiting**
   - Limit API requests from guest users
   - Prevent abuse of guest access
   - Monitor guest user activity

4. **Regular Audits**
   - Review guest user permissions regularly
   - Monitor guest user activities
   - Update role permissions as needed

### Data Protection

**Best Practices**:
- Never expose sensitive data to guest users
- Implement data filtering based on user role
- Use workspace-level data isolation
- Encrypt data in transit and at rest

### Monitoring and Logging

**Recommended Monitoring**:
- Track guest user authentication attempts
- Log guest user actions
- Monitor for unusual activity patterns
- Set up alerts for suspicious behavior

## Person Attributes

### Overview

Person attributes allow you to attach custom metadata to guest user profiles for categorization, tracking, and access control.

### Attribute Configuration

Attributes are configured in JSON format:

```json
{
  "ATTRIBUTE_NAME": "attribute_value"
}
```

### Common Use Cases

#### 1. Source Tracking

Track where guest users are coming from:

```json
{
  "SOURCE": "Product Demo",
  "CAMPAIGN": "Q4-2024",
  "REFERRER": "Partner Website"
}
```

#### 2. Access Control

Control access based on attributes:

```json
{
  "ACCESS_LEVEL": "Read-Only",
  "FEATURE_FLAGS": "basic-features",
  "DATA_SCOPE": "public-data"
}
```

#### 3. User Categorization

Categorize guest users:

```json
{
  "USER_TYPE": "Demo User",
  "INDUSTRY": "Healthcare",
  "COMPANY_SIZE": "Enterprise"
}
```

### Attribute Requirements

> **📝 Note**: All person attributes must be created in the workspace before use.

**Creating Person Attributes**:
1. Navigate to **People → Person Attributes** in Krista Studio
2. Click **Add Person Attribute**
3. Enter attribute name (must match JSON key exactly)
4. Set attribute type to **Text**
5. Save the attribute

### Attribute Validation

The extension validates person attributes:

| Validation | Error | Resolution |
|------------|-------|------------|
| Attribute doesn't exist | Extension fails to start | Create attribute in workspace |
| Invalid JSON format | Configuration error | Fix JSON syntax |
| Non-text value | Type mismatch error | Ensure all values are text strings |

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configuration parameters and setup
- [Get Script Element](pages/GetScriptElement.md) - UI customization
- [Dependencies](pages/Dependencies.md) - Required dependencies
- [Troubleshooting](pages/Troubleshooting.md) - Common issues and solutions

