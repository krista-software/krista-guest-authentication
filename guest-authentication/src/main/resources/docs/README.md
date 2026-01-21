# Guest Authentication Extension

## Overview

The Guest Authentication Extension enables seamless guest access to Krista applications without requiring traditional authentication methods like OAuth2 or LDAP. This extension automatically creates and authenticates guest users with configurable email addresses, roles, and custom person attributes, making it ideal for demos, public-facing applications, and use cases where immediate access is required without user registration.

## Key Features

- ✅ **Zero-Configuration Guest Access** - Authenticate users instantly without credentials
- ✅ **Automatic User Provisioning** - Creates users and domains automatically if they don't exist
- ✅ **Configurable Default User** - Set custom email addresses for guest accounts
- ✅ **Role-Based Access Control** - Assign specific roles to guest users
- ✅ **Custom Person Attributes** - Add custom attributes to guest user profiles via JSON configuration
- ✅ **CORS Support** - Built-in CORS headers for cross-origin requests
- ✅ **Script Injection** - Provides UI customization through script elements
- ✅ **Workspace Integration** - Seamlessly integrates with existing Krista workspaces
- ✅ **Session Management** - Configurable session timeout

## Quick Start Guide

### 1. Add Extension Dependency

Add the Guest Authentication extension as a dependency to your target extension (e.g., Krista Chatbot, Krista Portal).

```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication extension dependency"
)
```

### 2. Configure Extension

Set up the following configuration parameters:

- **Default User**: Email address for the guest account (default: `guest@kristasoft.com`)
- **Default Role**: Role assigned to guest users (default: `Krista Client User`)
- **Attribute Parameters**: Custom person attributes in JSON format (optional)
- **Session Timeout in minutes**: Session duration (optional)

### 3. Deploy and Test

Deploy your extension and test guest authentication by accessing your application. The extension will automatically create and authenticate the guest user.

## Documentation Structure

### Getting Started
- [Extension Configuration](pages/pages/ExtensionConfiguration.md) - Setup and configuration parameters
- [Authentication](pages/pages/Authentication.md) - Authentication flow and mechanisms
- [Dependencies](pages/pages/Dependencies.md) - Required dependencies and setup

### Catalog Requests

#### Integration
- [Get Script Element](pages/pages/GetScriptElement.md) - Retrieve UI customization script

### Additional Resources
- [Release Notes](pages/pages/ReleaseNotes.md) - Version history and changes
- [Troubleshooting](pages/pages/Troubleshooting.md) - Common issues and solutions

## Support & Resources

**Extension Version**: 1.0.0
**Krista Version**: 3.5+
**Domain**: Authentication
**Ecosystem**: Krista
**Support**: support@kristasoft.com

## Use Cases

### Public Demos
Provide instant access to demo applications without requiring user registration or authentication setup.

### Proof of Concepts
Quickly showcase Krista capabilities to stakeholders without authentication overhead.

### Public Portals
Enable public access to information portals with controlled permissions through role-based access.

### Testing Environments
Simplify testing by using guest authentication for automated tests and QA environments.

### Chatbot Applications
Provide immediate chatbot access for users without login requirements.

## Security Considerations

> **⚠️ Warning**: Guest Authentication is designed for controlled environments and should not be used for production applications requiring user accountability or data privacy.

- Guest users share the same identity if using default configuration
- All actions are attributed to the guest user account
- Consider using role-based restrictions to limit guest user capabilities
- For production use, implement proper authentication extensions (OAuth2, LDAP, SAML, etc.)
- Use custom person attributes to track guest user sources and access patterns

## Architecture

The Guest Authentication Extension implements the Krista Authentication SPI and provides:

1. **Request Authenticator**: Handles authentication requests and session creation
2. **Account Provisioner**: Automatically creates guest accounts and domains
3. **Role Manager**: Assigns roles to guest users
4. **Attribute Manager**: Manages custom person attributes
5. **CORS Handler**: Provides cross-origin resource sharing support

## Next Steps

1. Review [Extension Configuration](pages/ExtensionConfiguration.md) for detailed setup instructions
2. Understand the [Authentication Flow](pages/Authentication.md)
3. Explore the [Get Script Element](pages/GetScriptElement.md) catalog request
4. Check [Dependencies](pages/Dependencies.md) for integration requirements
5. Review [Troubleshooting](pages/Troubleshooting.md) for common issues

## Related Extensions

- **Email Password Authentication** - For email/password based authentication
- **OAuth2 Authentication** - For OAuth2 provider integration
- **LDAP Authentication** - For enterprise directory integration
- **SAML Authentication** - For SAML-based SSO

