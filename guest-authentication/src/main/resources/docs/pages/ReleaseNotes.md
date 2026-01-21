# Release Notes

## Guest Authentication Extension

This page contains the version history and release notes for the Guest Authentication Extension.

---

## Version 3.4.7

**Release Date**: 2024-07-25  
**Developer**: Vrushali Gaikwad  
**Krista Service APIs Java**: 1.0.113

### Resolved Bugs

#### KR-15255: Cannot create two chatbot extensions with different users from guest authentication

**Issue**: When attempting to create multiple chatbot extensions using guest authentication with different user configurations, the system would fail to create the second chatbot instance.

**Impact**: 
- Limited ability to run multiple chatbot instances with different guest users
- Prevented multi-tenant chatbot deployments using guest authentication
- Caused deployment failures for subsequent chatbot extensions

**Resolution**:
- Fixed user creation logic to support multiple guest user instances
- Improved user account isolation between chatbot extensions
- Enhanced configuration handling for multiple guest authentication instances

**Reference**: [KR-15255](https://antbrains.atlassian.net/browse/KR-15255)

### Improvements

- Enhanced error handling for user creation
- Improved logging for debugging authentication issues
- Better validation of configuration parameters

---

## Version 3.4.0

**Release Date**: 2024-06-01  
**Developer**: Krista Development Team  
**Krista Service APIs Java**: 1.0.100

### New Features

#### Initial Release

**Features**:
- Guest authentication without credentials
- Configurable default user email
- Configurable default role assignment
- Custom person attributes support
- Automatic user and domain provisioning
- CORS support for web applications
- UI customization script injection
- Integration with Krista Chatbot and Portal

### Capabilities

- **Zero-Configuration Access**: Enable guest access with minimal setup
- **Flexible Configuration**: Customize guest user email, role, and attributes
- **Automatic Provisioning**: Automatically create users and domains
- **UI Integration**: Provide script elements for UI customization
- **Workspace Integration**: Seamless integration with Krista workspaces

---

## Upgrade Guide

### Upgrading from 3.4.0 to 3.4.6

**Breaking Changes**: None

**Steps**:
1. Backup your current configuration
2. Deploy version 3.4.6 of the Guest Authentication Extension
3. Verify existing guest users still function correctly
4. Test multiple chatbot instances if applicable
5. Review logs for any warnings or errors

**Configuration Changes**: No configuration changes required

**Compatibility**: Fully backward compatible with version 3.4.0

---

## Known Issues

### Current Version (3.4.6)

No known issues at this time.

### Reporting Issues

If you encounter any issues with the Guest Authentication Extension:

1. **Check Documentation**: Review the troubleshooting guide
2. **Check Logs**: Examine extension logs for error messages
3. **Contact Support**: Email support@kristasoft.com with:
   - Extension version
   - Krista platform version
   - Error messages and logs
   - Steps to reproduce the issue

---

## Deprecation Notices

No features are currently deprecated.

---

## Future Roadmap

### Planned Features

The following features are under consideration for future releases:

- **Custom Script Templates**: Allow customization of the UI script element
- **Multiple Guest Users**: Support for multiple guest user profiles
- **Enhanced Attribute Support**: Support for non-text attribute types
- **Advanced CORS Configuration**: Customizable CORS headers
- **Guest User Analytics**: Track guest user behavior and usage patterns
- **Time-Limited Access**: Automatic guest user expiration
- **IP-Based Restrictions**: Limit guest access by IP address

> **📝 Note**: Planned features are subject to change based on customer feedback and business priorities.

---

## Version Compatibility Matrix

| Guest Authentication Version | Krista Platform Version | Krista Service APIs | Java Version |
|------------------------------|-------------------------|---------------------|--------------|
| 3.4.6 | 3.4.0 - 3.5.x | 1.0.113+ | 11+ |
| 3.4.0 | 3.4.0 - 3.5.x | 1.0.100+ | 11+ |

---

## Migration Notes

### Migrating from Other Authentication Extensions

If you're migrating from another authentication extension to Guest Authentication:

#### From OAuth2 Authentication

**Considerations**:
- Guest authentication provides less security than OAuth2
- All users will share the same guest identity
- No user-specific data isolation
- Suitable for demos and public access only

**Steps**:
1. Document current OAuth2 configuration
2. Add Guest Authentication as dependency
3. Configure default user and role
4. Test guest access thoroughly
5. Remove OAuth2 dependency if no longer needed

#### From Email/Password Authentication

**Considerations**:
- Loss of individual user accounts
- No password protection
- Simplified user experience
- Reduced security

**Steps**:
1. Export user data if needed
2. Add Guest Authentication as dependency
3. Configure appropriate guest role with limited permissions
4. Test access controls
5. Remove Email/Password authentication if no longer needed

---

## Support and Resources

### Documentation

- [Extension Configuration](ExtensionConfiguration.md)
- [Authentication](Authentication.md)
- [Dependencies](Dependencies.md)
- [Get Script Element](GetScriptElement.md)
- [Troubleshooting](Troubleshooting.md)

### Support Channels

- **Email**: support@kristasoft.com
- **Documentation**: https://docs.kristasoft.com
- **Community Forum**: https://community.kristasoft.com

### Training and Resources

- Krista Extension Development Guide
- Krista Authentication Best Practices
- Krista Security Guidelines

---

## Changelog Summary

### Version 3.4.6 (Current)
- ✅ Fixed: Multiple chatbot instances with different guest users (KR-15255)
- ✅ Improved: Error handling and logging
- ✅ Enhanced: Configuration validation

### Version 3.4.0
- ✅ Initial release
- ✅ Guest authentication support
- ✅ Configurable user and role
- ✅ Person attributes support
- ✅ UI customization script

---

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](pages/Authentication.md) - Authentication flow details
- [Troubleshooting](pages/Troubleshooting.md) - Common issues and solutions
- [Dependencies](pages/Dependencies.md) - Integration with other extensions

