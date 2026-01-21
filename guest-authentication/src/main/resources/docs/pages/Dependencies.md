# Dependencies

## Overview

The Guest Authentication Extension is designed to be used as a dependency for other Krista extensions that require authentication capabilities. This page explains how to add the Guest Authentication Extension as a dependency and configure it properly.

## Adding as Authentication Extension Dependency

### Using @Dependency Annotation

To add the Guest Authentication Extension as a dependency in your extension, use the `@Dependency` annotation in your extension's Area class.

**Annotation Syntax**:
```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication extension dependency"
)
```

### Complete Example

```java
package com.example.myextension.catalog;

import app.krista.extension.impl.anno.Dependency;
import app.krista.extension.impl.anno.Domain;

@Domain(
    id = "catEntryDomain_your-domain-id",
    name = "My Extension",
    ecosystemId = "catEntryEcosystem_your-ecosystem-id",
    ecosystemName = "My Ecosystem",
    ecosystemVersion = "your-version-id"
)
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication extension dependency"
)
public class MyExtensionArea {
    // Your catalog requests here
}
```

## Dependency Configuration

### Required Information

| Parameter | Value | Description |
|-----------|-------|-------------|
| name | `Authentication` | The domain name of the Guest Authentication Extension |
| domainId | `catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7` | Unique identifier for the Authentication domain |
| description | `Guest Authentication extension dependency` | Human-readable description of the dependency |

> **📝 Note**: The `domainId` must match exactly as shown above. This is the unique identifier for the Guest Authentication Extension's domain.

## Compatible Extensions

The Guest Authentication Extension can be used as a dependency for the following types of extensions:

### 1. Krista Chatbot

**Use Case**: Provide guest access to chatbot without requiring user login

**Configuration**:
```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication for Chatbot"
)
```

**Benefits**:
- Instant chatbot access for demos
- No user registration required
- Simplified testing

### 2. Krista Portal

**Use Case**: Enable public access to portal content

**Configuration**:
```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication for Portal"
)
```

**Benefits**:
- Public-facing portal access
- Controlled guest permissions
- Easy demo setup

### 3. Custom Client Applications

**Use Case**: Provide guest authentication for custom applications

**Configuration**:
```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication for Custom App"
)
```

**Benefits**:
- Flexible authentication for custom apps
- Rapid prototyping
- Simplified user onboarding

## Dependency Requirements

### Krista Platform Version

- **Minimum Version**: Krista 3.4.0
- **Recommended Version**: Krista 3.5.0 or higher

### Java Version

- **Minimum Version**: Java 11
- **Recommended Version**: Java 17

### Krista Service APIs

- **Minimum Version**: 1.0.113
- **Recommended Version**: Latest stable release

## Deployment Considerations

### Deployment Order

When deploying extensions with dependencies:

1. **Deploy Guest Authentication Extension First**
   - Ensure the Guest Authentication Extension is deployed to the workspace
   - Verify the extension is running successfully

2. **Configure Guest Authentication**
   - Set up Default User, Default Role, and Attribute Parameters
   - Test the configuration

3. **Deploy Dependent Extension**
   - Deploy your extension that depends on Guest Authentication
   - The platform will automatically link the dependency

### Verification Steps

After deployment, verify the dependency is correctly configured:

1. **Check Extension Status**
   - Navigate to Extensions in Krista Studio
   - Verify both extensions show as "Running"

2. **Verify Dependency Link**
   - Open your extension's details
   - Check that Guest Authentication is listed as a dependency

3. **Test Authentication**
   - Access your application
   - Verify guest authentication works correctly

## Troubleshooting Dependencies

### Issue: Dependency Not Found

**Symptoms**:
- Extension fails to deploy
- Error message: "Dependency domain not found"

**Cause**: Guest Authentication Extension not deployed

**Resolution**:
1. Deploy Guest Authentication Extension first
2. Wait for deployment to complete
3. Redeploy your extension

### Issue: Incorrect Domain ID

**Symptoms**:
- Extension deploys but authentication doesn't work
- Dependency link not established

**Cause**: Incorrect `domainId` in @Dependency annotation

**Resolution**:
1. Verify the `domainId` matches exactly:
   ```
   catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7
   ```
2. Update the annotation
3. Rebuild and redeploy your extension

### Issue: Version Incompatibility

**Symptoms**:
- Extension fails to start
- ClassNotFoundException or NoSuchMethodError

**Cause**: Incompatible Krista platform or API versions

**Resolution**:
1. Check Krista platform version (minimum 3.4.0)
2. Update Krista Service APIs to version 1.0.113 or higher
3. Rebuild and redeploy

## Multiple Authentication Dependencies

### Using Multiple Authentication Extensions

You can configure your extension to support multiple authentication methods:

```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication"
)
@Dependency(
    name = "OAuth2 Authentication",
    domainId = "catEntryDomain_oauth2-domain-id",
    description = "OAuth2 Authentication"
)
public class MyExtensionArea {
    // Your catalog requests here
}
```

**Use Cases**:
- Provide guest access for demos and OAuth2 for production
- Support multiple authentication methods in the same application
- Gradual migration from guest to authenticated users

## Best Practices

### 1. Use Descriptive Dependency Descriptions

Provide clear descriptions for your dependencies:

```java
@Dependency(
    name = "Authentication",
    domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
    description = "Guest Authentication for public demo access"
)
```

### 2. Document Authentication Requirements

Document which authentication extension your extension requires:

```java
/**
 * My Extension Area
 * 
 * Requires: Guest Authentication Extension
 * Purpose: Provides public access to demo features
 */
@Dependency(...)
public class MyExtensionArea {
    // ...
}
```

### 3. Test with Dependency

Always test your extension with the Guest Authentication dependency:
- Test guest user creation
- Verify role assignment
- Test person attributes
- Verify UI customization

### 4. Version Compatibility

Maintain compatibility with Guest Authentication versions:
- Document minimum required version
- Test with multiple versions
- Handle version-specific features gracefully

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure Guest Authentication
- [Authentication](pages/Authentication.md) - Understand authentication flow
- [Get Script Element](pages/GetScriptElement.md) - UI customization catalog request
- [Troubleshooting](pages/Troubleshooting.md) - Common issues and solutions

