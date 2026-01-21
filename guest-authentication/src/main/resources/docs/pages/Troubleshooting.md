# Troubleshooting

## Overview

This page provides solutions to common issues encountered when using the Guest Authentication Extension. Issues are organized by category for easy reference.

---

## Extension Deployment Issues

### Issue: Extension Fails to Deploy

**Symptoms**:
- Extension shows "Failed" status in Krista Studio
- Deployment error messages in logs
- Extension not available in workspace

**Possible Causes**:
1. Invalid configuration parameters
2. Missing required dependencies
3. Incompatible Krista platform version
4. Workspace connectivity issues

**Resolution Steps**:

1. **Check Extension Logs**:
   ```
   Navigate to: Extensions → Guest Authentication → Logs
   Look for: ERROR or WARN messages
   ```

2. **Verify Configuration**:
   - Check Default User email format is valid
   - Verify Default Role exists in workspace
   - Validate Attribute Parameters JSON syntax

3. **Check Platform Version**:
   - Minimum required: Krista 3.4.0
   - Verify: Help → About in Krista Studio

4. **Redeploy Extension**:
   - Stop the extension
   - Wait 30 seconds
   - Start the extension
   - Monitor logs for errors

---

## Configuration Issues

### Issue: Invalid JSON in Attribute Parameters

**Symptoms**:
- Extension fails to start
- Error message: "Invalid JSON format"
- Configuration validation errors

**Cause**: Malformed JSON in Attribute Parameters field

**Resolution**:

1. **Validate JSON Syntax**:
   ```json
   // ❌ INCORRECT (trailing comma)
   {
     "KEY1": "value1",
     "KEY2": "value2",
   }
   
   // ✅ CORRECT
   {
     "KEY1": "value1",
     "KEY2": "value2"
   }
   ```

2. **Use JSON Validator**:
   - Copy your JSON to https://jsonlint.com
   - Fix any syntax errors
   - Paste corrected JSON back to configuration

3. **Common JSON Errors**:
   - Trailing commas
   - Missing quotes around keys
   - Single quotes instead of double quotes
   - Unescaped special characters

### Issue: Role Not Found

**Symptoms**:
- Extension starts but authentication fails
- Error message: "Specified role not found"
- Guest users not created

**Cause**: Configured Default Role doesn't exist in workspace

**Resolution**:

1. **Verify Role Exists**:
   - Navigate to: People → Roles in Krista Studio
   - Search for the role name
   - Note: Role names are case-sensitive

2. **Create Missing Role**:
   - Click "Add Role"
   - Enter exact role name from configuration
   - Set appropriate permissions
   - Save the role

3. **Update Configuration**:
   - Use exact role name (case-sensitive)
   - Redeploy extension

### Issue: Person Attributes Not Applied

**Symptoms**:
- Guest user created but attributes missing
- No error messages
- Attributes not visible in user profile

**Cause**: Person attributes don't exist in workspace

**Resolution**:

1. **Create Person Attributes**:
   ```
   Navigate to: People → Person Attributes
   For each attribute in your JSON:
     1. Click "Add Person Attribute"
     2. Enter attribute name (exact match to JSON key)
     3. Set type to "Text"
     4. Save attribute
   ```

2. **Verify Attribute Names**:
   - Attribute names must match JSON keys exactly
   - Names are case-sensitive
   - No spaces or special characters unless in JSON

3. **Redeploy Extension**:
   - After creating attributes, redeploy extension
   - Verify attributes are applied to guest user

---

## Authentication Issues

### Issue: Guest User Not Created

**Symptoms**:
- Authentication fails
- No guest user in workspace
- Access denied errors

**Possible Causes**:
1. Extension not running
2. Invalid email format
3. Role doesn't exist
4. Workspace permissions

**Resolution**:

1. **Verify Extension Status**:
   ```
   Extensions → Guest Authentication → Status should be "Running"
   ```

2. **Check Email Format**:
   ```
   ✅ Valid: guest@example.com
   ✅ Valid: demo.user@company.com
   ❌ Invalid: guest@
   ❌ Invalid: @example.com
   ❌ Invalid: guest
   ```

3. **Verify Role Exists**:
   - Check People → Roles
   - Create role if missing

4. **Check Workspace Permissions**:
   - Verify extension has permission to create users
   - Check workspace admin settings

### Issue: Multiple Guest Users Created

**Symptoms**:
- Multiple users with similar names
- Duplicate guest accounts
- Confusion about which user is active

**Cause**: Configuration changed between deployments

**Resolution**:

1. **Identify Active Guest User**:
   - Navigate to: People → Users
   - Search for guest users
   - Check "Last Login" to find active user

2. **Delete Duplicate Users**:
   - Select duplicate/inactive users
   - Click "Delete User"
   - Confirm deletion

3. **Standardize Configuration**:
   - Use consistent email across environments
   - Document configuration in version control
   - Avoid changing email unless necessary

### Issue: Guest User Has No Access

**Symptoms**:
- User authenticated but cannot access resources
- Permission denied errors
- Empty workspace view

**Cause**: Role has insufficient permissions

**Resolution**:

1. **Review Role Permissions**:
   ```
   Navigate to: People → Roles → [Guest Role]
   Check permissions for:
     - Conversations
     - Data access
     - Feature access
   ```

2. **Grant Necessary Permissions**:
   - Enable required permissions for guest role
   - Test access with guest user
   - Adjust permissions as needed

3. **Verify User Role Assignment**:
   - Navigate to: People → Users → [Guest User]
   - Verify correct role is assigned
   - Reassign role if necessary

---

## UI Integration Issues

### Issue: Script Element Not Working

**Symptoms**:
- UI not customized for guest users
- JavaScript errors in browser console
- Login text not showing "Guest"

**Possible Causes**:
1. Template element missing
2. Container element missing
3. Script not injected properly
4. JavaScript errors

**Resolution**:

1. **Check Browser Console**:
   ```
   Open browser developer tools (F12)
   Check Console tab for errors
   Look for: "template-form" or "__hosted__container__" errors
   ```

2. **Verify Required Elements**:
   ```html
   <!-- Required template element -->
   <template id="template-form">
     <!-- Template content -->
   </template>
   
   <!-- Required container element -->
   <div id="__hosted__container__"></div>
   ```

3. **Verify Script Injection**:
   - Check that script is retrieved from extension
   - Verify script is injected into page
   - Check script execution timing (after DOM load)

4. **Test Script Manually**:
   ```javascript
   // In browser console
   const template = document.getElementById("template-form");
   console.log("Template:", template);
   
   const container = document.getElementById("__hosted__container__");
   console.log("Container:", container);
   ```

### Issue: CORS Errors in Browser

**Symptoms**:
- Browser console shows CORS errors
- Requests blocked by browser
- "Access-Control-Allow-Origin" errors

**Cause**: Browser blocking cross-origin requests

**Resolution**:

1. **Verify CORS Headers**:
   ```
   The extension automatically sets:
   - Access-Control-Allow-Origin: *
   - Access-Control-Allow-Methods: POST, GET, OPTIONS
   - Access-Control-Allow-Headers: Content-Type
   ```

2. **Check Request Origin**:
   - Verify request is coming from expected origin
   - Check browser network tab for request details

3. **Test with Browser Extensions Disabled**:
   - Disable ad blockers and security extensions
   - Test in incognito/private mode
   - Try different browser

4. **Contact Support**:
   - If CORS issues persist, contact support
   - Provide browser console logs
   - Include network request details

---

## Performance Issues

### Issue: Slow Authentication

**Symptoms**:
- Long delay before guest user is authenticated
- Timeout errors
- Poor user experience

**Possible Causes**:
1. Workspace performance issues
2. Network latency
3. Database performance
4. Extension resource constraints

**Resolution**:

1. **Check Workspace Performance**:
   - Monitor workspace resource usage
   - Check database performance
   - Review system logs

2. **Optimize Configuration**:
   - Minimize person attributes
   - Use simple role structure
   - Avoid complex validation rules

3. **Monitor Extension Logs**:
   - Check for slow queries
   - Look for timeout warnings
   - Identify bottlenecks

4. **Contact Support**:
   - If performance issues persist
   - Provide performance metrics
   - Share extension logs

---

## Dependency Issues

### Issue: Dependent Extension Fails to Deploy

**Symptoms**:
- Extension that depends on Guest Authentication fails
- Error: "Dependency not found"
- Deployment errors

**Cause**: Guest Authentication not deployed or incorrect domain ID

**Resolution**:

1. **Verify Guest Authentication is Deployed**:
   ```
   Extensions → Guest Authentication → Status: "Running"
   ```

2. **Check Domain ID**:
   ```java
   // Correct domain ID
   @Dependency(
       name = "Authentication",
       domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
       description = "Guest Authentication"
   )
   ```

3. **Deployment Order**:
   - Deploy Guest Authentication first
   - Wait for successful deployment
   - Then deploy dependent extension

4. **Rebuild Dependent Extension**:
   - Clean build
   - Rebuild with correct dependency
   - Redeploy

---

## Common Error Messages

### "Invalid JSON format for attribute parameters"

**Cause**: Malformed JSON in Attribute Parameters

**Fix**: Validate and correct JSON syntax

**See**: [Invalid JSON in Attribute Parameters](#issue-invalid-json-in-attribute-parameters)

### "Specified role not found in workspace"

**Cause**: Default Role doesn't exist

**Fix**: Create the role or use existing role name

**See**: [Role Not Found](#issue-role-not-found)

### "Invalid email address format"

**Cause**: Malformed email in Default User

**Fix**: Use valid email format (user@domain.com)

**See**: [Guest User Not Created](#issue-guest-user-not-created)

### "Dependency domain not found"

**Cause**: Guest Authentication not deployed

**Fix**: Deploy Guest Authentication first

**See**: [Dependent Extension Fails to Deploy](#issue-dependent-extension-fails-to-deploy)

### "Person attribute [NAME] does not exist"

**Cause**: Attribute in JSON not created in workspace

**Fix**: Create person attribute in workspace

**See**: [Person Attributes Not Applied](#issue-person-attributes-not-applied)

---

## Getting Help

### Before Contacting Support

1. **Check This Troubleshooting Guide**
2. **Review Extension Logs**
3. **Verify Configuration**
4. **Test in Clean Environment**

### Information to Provide

When contacting support, include:

- **Extension Version**: (e.g., 3.4.6)
- **Krista Platform Version**: (e.g., 3.5.0)
- **Error Messages**: Exact error text
- **Extension Logs**: Recent log entries
- **Configuration**: Sanitized configuration (remove sensitive data)
- **Steps to Reproduce**: Detailed steps
- **Expected vs Actual Behavior**

### Support Channels

- **Email**: support@kristasoft.com
- **Documentation**: https://docs.kristasoft.com
- **Community Forum**: https://community.kristasoft.com

---

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configuration parameters
- [Authentication](pages/Authentication.md) - Authentication flow
- [Dependencies](pages/Dependencies.md) - Dependency setup
- [Get Script Element](pages/GetScriptElement.md) - UI customization
- [Release Notes](pages/ReleaseNotes.md) - Version history

