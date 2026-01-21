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

package app.krista.extensions.authentication.guest_authentication;

import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.ksdk.accounts.AccountProvider;
import app.krista.ksdk.accounts.AttributeManager;
import app.krista.ksdk.accounts.ModifiableAttribute;
import app.krista.ksdk.authentication.AuthenticationSettings;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.AuthorizationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GuestAuthenticationExtension.
 * 
 * Tests cover:
 * - Authenticator creation
 * - Attribute validation
 * - Custom tab configuration
 * - Error handling
 */
@DisplayName("Guest Authentication Extension Tests")
class GuestAuthenticationExtensionTest {

    @Mock
    private AuthorizationContext authorizationContext;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private AuthenticationSettings authenticationSettings;

    @Mock
    private AccountProvider accountProvider;

    @Mock
    private AttributeManager attributeManager;

    private GuestAuthenticationExtension extension;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        extension = new GuestAuthenticationExtension(
            authorizationContext,
            sessionManager,
            authenticationSettings,
            accountProvider,
            attributeManager
        );
    }

    @Test
    @DisplayName("Should create RequestAuthenticator successfully")
    void testGetAuthenticatedAccountId() {
        // Act
        RequestAuthenticator authenticator = extension.getAuthenticatedAccountId();

        // Assert
        assertNotNull(authenticator, "RequestAuthenticator should not be null");
        assertInstanceOf(GuestAuthenticationRequestAuthenticator.class, authenticator,
            "Should return GuestAuthenticationRequestAuthenticator instance");
    }

    @Test
    @DisplayName("Should return custom tab with documentation link")
    void testCustomTab() {
        // Act
        Map<String, String> tabs = extension.customTab();

        // Assert
        assertNotNull(tabs, "Custom tabs should not be null");
        assertEquals(1, tabs.size(), "Should have exactly one custom tab");
        assertTrue(tabs.containsKey("Documentation"), "Should contain Documentation tab");
        assertEquals("static/docs", tabs.get("Documentation"), "Documentation tab should point to static/docs");
    }

    @Test
    @DisplayName("Should validate attributes successfully when JSON is valid and attributes exist")
    void testValidateAttributes_ValidJson_AttributesExist() {
        // Arrange
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute_parameters", "{\"GUEST_SOURCE\":\"Portal\",\"ACCESS_LEVEL\":\"Read-Only\"}");

        ModifiableAttribute attr1 = mock(ModifiableAttribute.class);
        when(attr1.getName()).thenReturn("GUEST_SOURCE");
        ModifiableAttribute attr2 = mock(ModifiableAttribute.class);
        when(attr2.getName()).thenReturn("ACCESS_LEVEL");

        when(attributeManager.getAttributes()).thenReturn(Arrays.asList(attr1, attr2));

        // Act & Assert
        assertDoesNotThrow(() -> extension.validateAttributes(attributes),
            "Should not throw exception for valid attributes");
    }

    @Test
    @DisplayName("Should not throw exception when attribute_parameters is blank")
    void testValidateAttributes_BlankAttributeParameters() {
        // Arrange
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute_parameters", "");

        // Act & Assert
        assertDoesNotThrow(() -> extension.validateAttributes(attributes),
            "Should not throw exception for blank attribute_parameters");
    }

    @Test
    @DisplayName("Should not throw exception when attribute_parameters is missing")
    void testValidateAttributes_MissingAttributeParameters() {
        // Arrange
        Map<String, String> attributes = new HashMap<>();

        // Act & Assert
        assertDoesNotThrow(() -> extension.validateAttributes(attributes),
            "Should not throw exception when attribute_parameters is missing");
    }

    @Test
    @DisplayName("Should throw exception when JSON is invalid")
    void testValidateAttributes_InvalidJson() {
        // Arrange
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute_parameters", "{invalid json}");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> extension.validateAttributes(attributes),
            "Should throw IllegalArgumentException for invalid JSON");

        assertTrue(exception.getMessage().contains("Invalid Json format"),
            "Exception message should indicate invalid JSON format");
    }

    @Test
    @DisplayName("Should throw exception when attribute does not exist in workspace")
    void testValidateAttributes_AttributeDoesNotExist() {
        // Arrange
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute_parameters", "{\"NON_EXISTENT_ATTR\":\"value\"}");

        ModifiableAttribute attr1 = mock(ModifiableAttribute.class);
        when(attr1.getName()).thenReturn("EXISTING_ATTR");

        when(attributeManager.getAttributes()).thenReturn(Collections.singletonList(attr1));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> extension.validateAttributes(attributes),
            "Should throw RuntimeException when attribute doesn't exist");

        assertTrue(exception.getMessage().contains("Invalid input"),
            "Exception message should indicate invalid input");
        assertTrue(exception.getMessage().contains("Missing attribute"),
            "Exception message should indicate missing attribute");
        assertTrue(exception.getMessage().contains("NON_EXISTENT_ATTR"),
            "Exception message should contain the missing attribute name");
    }
}

