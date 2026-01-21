# Extension Configuration

## Overview

The Guest Authentication Extension requires minimal configuration to enable guest access to your Krista applications. This page provides detailed information about all configuration parameters, setup steps, and best practices for configuring the extension.

## Configuration Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| default_role | Text | No | Role assigned to the guest user. If not provided, defaults to `Krista Client User` | `Guest User` |
| Session Timeout in minutes | Number | No | Session timeout duration in minutes. Controls how long a guest session remains active | `60` |
| attribute_parameters | JSON | No | Custom person attributes assigned to the guest user upon creation. Must be valid JSON format | `{"ABAC_SOURCE": "Demo", "GUEST_SOURCE": "Portal"}` |

> **📝 Note**: The default user email is configured through the extension's authentication settings and defaults to `guest@kristasoft.com`.

## Step-by-Step Setup

### Step 1: Add Extension as Dependency

1. Navigate to your target extension (e.g., Krista Chatbot, Krista Portal)
2. Open the extension configuration in Krista Studio
3. Add **Guest Authentication** as a dependency extension
4. Save the configuration

### Step 2: Configure Default User (Optional)

1. In the extension configuration, locate the **Default User** parameter
2. Enter the email address you want to use for guest authentication
3. If left blank, the system will use `guest@kristasoft.com`

**Example**:
```
demo@mycompany.com
```

> **📝 Note**: The domain portion of the email (e.g., `mycompany.com`) will be automatically added to the workspace if it doesn't exist.

### Step 3: Configure Default Role (Optional)

1. Locate the **Default Role** parameter
2. Enter the name of the role you want to assign to guest users
3. If left blank, the system will use `Krista Client User`

**Example**:
```
Public Portal User
```

> **⚠️ Warning**: Ensure the role exists in your workspace before configuring it. The extension will fail if the specified role doesn't exist.

### Step 4: Configure Attribute Parameters (Optional)

1. Locate the **Attribute Parameters** field
2. Enter custom person attributes in JSON format
3. Each attribute must be a key-value pair where the value is text

**Single Attribute Example**:
```json
{
  "GUEST_SOURCE": "Portal"
}
```

**Multiple Attributes Example**:
```json
{
  "ABAC_SOURCE": "Demo Environment",
  "GUEST_SOURCE": "Public Portal",
  "ACCESS_LEVEL": "Read-Only"
}
```

> **📝 Note**: Person attributes must be created in the workspace before using them. Navigate to **People → Person Attributes** in Krista Studio to create attributes.

### Step 5: Deploy Extension

1. Save all configuration changes
2. Deploy the extension to your workspace
3. Verify the extension is running successfully

## Configuration Scenarios

### Scenario 1: Basic Guest Access

**Use Case**: Simple guest access with default settings

**Configuration**:
- Default User: *(leave blank)*
- Default Role: *(leave blank)*
- Attribute Parameters: *(leave blank)*

**Result**: Guest user `guest@kristasoft.com` with role `Krista Client User`

### Scenario 2: Custom Guest User

**Use Case**: Branded guest access with custom email

**Configuration**:
- Default User: `visitor@acmecorp.com`
- Default Role: `ACME Guest`
- Attribute Parameters: *(leave blank)*

**Result**: Guest user `visitor@acmecorp.com` with role `ACME Guest`

### Scenario 3: Guest with Custom Attributes

**Use Case**: Guest access with tracking attributes

**Configuration**:
- Default User: `demo@company.com`
- Default Role: `Demo User`
- Attribute Parameters:
```json
{
  "SOURCE": "Product Demo",
  "CAMPAIGN": "Q4-2024",
  "ACCESS_TYPE": "Limited"
}
```

**Result**: Guest user with custom attributes for tracking and access control

## Security Considerations

### Role Permissions

> **⚠️ Warning**: Carefully configure the default role to ensure guest users have appropriate permissions.

**Best Practices**:
1. Create a dedicated role for guest users with limited permissions
2. Restrict access to sensitive data and operations
3. Enable read-only access where possible
4. Regularly audit guest user activities

### Email Domain Management

The extension automatically adds the email domain to the workspace if it doesn't exist. Consider the following:

- Use a dedicated domain for guest users (e.g., `guest.yourcompany.com`)
- Avoid using production email domains
- Monitor domain additions for security purposes

### Attribute Security

When using custom person attributes:

- Avoid storing sensitive information in attributes
- Use attributes for categorization and access control only
- Validate attribute values in your application logic
- Document all custom attributes for maintenance

## Troubleshooting

### Issue: Extension Fails to Start

**Cause**: Invalid JSON in Attribute Parameters

**Resolution**:
1. Validate your JSON using a JSON validator
2. Ensure all keys and values are properly quoted
3. Check for trailing commas or syntax errors

**Example of Invalid JSON**:
```json
{
  "KEY": "value",  // Trailing comma causes error
}
```

**Corrected JSON**:
```json
{
  "KEY": "value"
}
```

### Issue: Guest User Not Created

**Cause**: Specified role doesn't exist in workspace

**Resolution**:
1. Navigate to **People → Roles** in Krista Studio
2. Verify the role name matches exactly (case-sensitive)
3. Create the role if it doesn't exist
4. Redeploy the extension

### Issue: Person Attributes Not Applied

**Cause**: Attributes don't exist in workspace

**Resolution**:
1. Navigate to **People → Person Attributes**
2. Create all attributes specified in the configuration
3. Ensure attribute names match exactly
4. Redeploy the extension

### Issue: CORS Errors in Browser

**Cause**: Browser blocking cross-origin requests

**Resolution**:
The extension automatically sets the following CORS headers:
- `Access-Control-Allow-Origin`: `*`
- `Access-Control-Allow-Methods`: `POST, GET, OPTIONS`
- `Access-Control-Allow-Headers`: `Content-Type`

If issues persist:
1. Check browser console for specific CORS errors
2. Verify your application is making requests to the correct endpoint
3. Contact support if custom CORS configuration is needed

## Advanced Configuration

### Using Environment Variables

For different environments (dev, staging, production), consider using environment-specific configurations:

**Development**:
```json
{
  "Default User": "dev-guest@company.com",
  "Default Role": "Dev Guest User",
  "Attribute Parameters": "{\"ENV\": \"Development\"}"
}
```

**Production**:
```json
{
  "Default User": "guest@company.com",
  "Default Role": "Guest User",
  "Attribute Parameters": "{\"ENV\": \"Production\"}"
}
```

### Integration with Other Extensions

When using Guest Authentication with other extensions:

1. **Krista Chatbot**: Guest users can interact with chatbot without login
2. **Krista Portal**: Provides public access to portal content
3. **Custom Extensions**: Use guest authentication for public-facing features

## Validation Rules

The extension validates configuration parameters as follows:

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Invalid JSON format in Attribute Parameters | "Invalid JSON format for attribute parameters" | Correct JSON syntax errors |
| Role doesn't exist | "Specified role not found in workspace" | Create the role or use existing role name |
| Invalid email format | "Invalid email address format" | Use valid email format (user@domain.com) |

## See Also

- [Authentication](pages/Authentication.md) - Learn about the authentication flow
- [Dependencies](pages/Dependencies.md) - Required dependencies
- [Get Script Element](pages/GetScriptElement.md) - UI customization catalog request
- [Troubleshooting](pages/Troubleshooting.md) - Common issues and solutions

