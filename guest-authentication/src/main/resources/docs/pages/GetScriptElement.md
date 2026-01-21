# Get Script Element

## Overview

Retrieves a JavaScript script element that customizes the user interface for guest authentication, including updating login text and preparing the UI client.

## Request Details

- **Area**: Integration
- **Type**: QUERY_SYSTEM
- **Retry Support**: ❌ No (Query operations are idempotent and don't require retry logic)

## Input Parameters

This catalog request does not require any input parameters.

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| _(None)_ | - | - | No input parameters required | - |

## Output Parameters

| Parameter Name | Type | Description | Example |
|----------------|------|-------------|---------|
| Script Element | Text | JavaScript code that customizes the UI for guest authentication | `<script>...</script>` |

### Script Element Details

The returned script element contains JavaScript code that:

1. **prepareUserInterfaceClient(predicate, args)**: Initializes the UI client with custom configuration
2. **updateLoginText(ref, data)**: Updates the login display text to show "Guest"
3. **DOM Manipulation**: Clones and appends template elements to the hosted container

## Validation Rules

This catalog request does not perform validation as it has no input parameters.

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| _(None)_ | - | No validation required |

## Error Handling

### System Errors (SYSTEM_ERROR)

**Cause**: Unexpected system-level failures during script generation

**Common Scenarios**:
- Internal server errors
- Extension service unavailability

**Resolution**:
1. Check extension deployment status
2. Review system logs for errors
3. Verify extension is properly configured
4. Contact support if issue persists

## Usage Examples

### Example 1: Basic Script Retrieval

**Input**:
```
(No input parameters required)
```

**Output**:
```html
<script>
    function prepareUserInterfaceClient(predicate, args) {
      predicate(args);
    }
    function updateLoginText(ref, data) {
      ref.loggedUserText.text('Guest');
    }
    const template = document.getElementById("template-form");
    const templateClone = template.content.cloneNode(true);
    document.getElementById("__hosted__container__").appendChild(templateClone);
  </script>
```

**Result**: JavaScript code ready to be injected into the UI

### Example 2: Integration with Krista Chatbot

**Scenario**: Retrieve script element for chatbot UI customization

**Input**:
```
(No input parameters required)
```

**Output**:
```html
<script>
    function prepareUserInterfaceClient(predicate, args) {
      predicate(args);
    }
    function updateLoginText(ref, data) {
      ref.loggedUserText.text('Guest');
    }
    const template = document.getElementById("template-form");
    const templateClone = template.content.cloneNode(true);
    document.getElementById("__hosted__container__").appendChild(templateClone);
  </script>
```

**Result**: Script injected into chatbot UI, displaying "Guest" as the logged-in user

### Example 3: Integration with Krista Portal

**Scenario**: Customize portal UI for guest users

**Input**:
```
(No input parameters required)
```

**Output**:
```html
<script>
    function prepareUserInterfaceClient(predicate, args) {
      predicate(args);
    }
    function updateLoginText(ref, data) {
      ref.loggedUserText.text('Guest');
    }
    const template = document.getElementById("template-form");
    const templateClone = template.content.cloneNode(true);
    document.getElementById("__hosted__container__").appendChild(templateClone);
  </script>
```

**Result**: Portal UI updated to show guest user status

## Business Rules

1. **Static Script Content**: The script content is static and does not change based on configuration
2. **UI Customization Only**: The script only affects UI presentation, not authentication logic
3. **Template Dependency**: Requires `template-form` element to exist in the DOM
4. **Container Dependency**: Requires `__hosted__container__` element to exist in the DOM
5. **Guest Text Display**: Always displays "Guest" as the logged-in user text
6. **Idempotent Operation**: Can be called multiple times without side effects

## Limitations

1. **Static Content**: Script content cannot be customized per request
2. **DOM Dependencies**: Requires specific DOM elements (`template-form`, `__hosted__container__`)
3. **No Parameterization**: Cannot pass parameters to customize script behavior
4. **Client-Side Only**: Script executes only on the client side
5. **No Validation**: No input validation as there are no input parameters

## Best Practices

### 1. Verify DOM Elements Exist
Ensure the required DOM elements (`template-form` and `__hosted__container__`) exist before injecting the script.

### 2. Inject Script Early
Inject the script element early in the page lifecycle to ensure UI customization occurs before user interaction.

### 3. Handle Script Errors
Implement error handling for script execution failures on the client side.

### 4. Cache Script Content
Consider caching the script content on the client side to reduce repeated catalog request calls.

### 5. Test UI Customization
Thoroughly test UI customization across different browsers and devices.

## Common Use Cases

### 1. Chatbot UI Customization
```
Scenario: Customize chatbot interface for guest users
Action: Call "Get Script Element" and inject returned script into chatbot HTML
Result: Chatbot displays "Guest" as the logged-in user
```

### 2. Portal Guest Access
```
Scenario: Enable guest access to portal with custom UI
Action: Retrieve script element and add to portal page
Result: Portal UI shows guest user status and customized interface
```

### 3. Demo Application Setup
```
Scenario: Prepare demo application with guest authentication
Action: Fetch script element during application initialization
Result: Demo application ready with guest-friendly UI
```

## Related Catalog Requests

This is the only catalog request in the Guest Authentication Extension. Related functionality is provided through:

- **Authentication Flow**: Handled by the extension's authentication SPI implementation
- **User Provisioning**: Automatic through the extension's account provisioner
- **Session Management**: Managed by the extension's session manager

## Technical Implementation

### Helper Class
- **Class**: IntegrationArea
- **Package**: app.krista.extensions.authentication.guest_authentication.catalog
- **Method**: getScriptElement()
- **Return Type**: String

### Implementation Details

The catalog request is implemented as a simple method that returns a hardcoded JavaScript string:

```java
@CatalogRequest(
    description = "Get Script Element",
    name = "Get Script Element",
    area = "Integration",
    type = CatalogRequest.Type.QUERY_SYSTEM
)
@Field(name = "Script Element", type = "Text", attributes = {}, options = {})
public String getScriptElement() {
    return "<script>...</script>";
}
```

### Telemetry Metrics

No specific telemetry metrics are tracked for this catalog request as it's a simple query operation.

## Troubleshooting

### Issue: Script Not Executing

**Cause**: Script element not properly injected into DOM

**Solution**:
1. Verify script element is added to the page
2. Check browser console for JavaScript errors
3. Ensure script is not blocked by Content Security Policy (CSP)
4. Verify DOM elements exist before script execution

### Issue: UI Not Updating

**Cause**: Required DOM elements (`template-form`, `__hosted__container__`) not found

**Solution**:
1. Verify `template-form` element exists in the DOM
2. Verify `__hosted__container__` element exists in the DOM
3. Check element IDs match exactly (case-sensitive)
4. Ensure elements are loaded before script execution

### Issue: "Guest" Text Not Displaying

**Cause**: `updateLoginText` function not being called or reference object structure mismatch

**Solution**:
1. Verify the calling code invokes `updateLoginText` with correct parameters
2. Check that `ref.loggedUserText` exists and has a `text()` method
3. Review browser console for JavaScript errors
4. Ensure jQuery or equivalent library is loaded

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure guest authentication settings
- [Authentication](pages/Authentication.md) - Understand the authentication flow
- [Dependencies](pages/Dependencies.md) - Learn how to add this extension as a dependency
- [Troubleshooting](pages/Troubleshooting.md) - Common issues and solutions
