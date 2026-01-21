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

package app.krista.extensions.authentication.guest_authentication.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IntegrationArea catalog requests.
 * 
 * Tests the "Get Script Element" catalog request.
 */
@DisplayName("Integration Area Catalog Request Tests")
class IntegrationAreaTest {

    private IntegrationArea integrationArea;

    @BeforeEach
    void setUp() {
        integrationArea = new IntegrationArea();
    }

    @Test
    @DisplayName("Should return script element with expected content")
    void testGetScriptElement_ReturnsValidScript() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertNotNull(scriptElement, "Script element should not be null");
        assertFalse(scriptElement.isEmpty(), "Script element should not be empty");
    }

    @Test
    @DisplayName("Script element should start with <script> tag")
    void testGetScriptElement_StartsWithScriptTag() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.trim().startsWith("<script>"),
            "Script element should start with <script> tag");
    }

    @Test
    @DisplayName("Script element should end with </script> tag")
    void testGetScriptElement_EndsWithScriptTag() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.trim().endsWith("</script>"),
            "Script element should end with </script> tag");
    }

    @Test
    @DisplayName("Script element should contain prepareUserInterfaceClient function")
    void testGetScriptElement_ContainsPrepareUserInterfaceClientFunction() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("function prepareUserInterfaceClient"),
            "Script should contain prepareUserInterfaceClient function");
        assertTrue(scriptElement.contains("predicate(args)"),
            "prepareUserInterfaceClient should call predicate with args");
    }

    @Test
    @DisplayName("Script element should contain updateLoginText function")
    void testGetScriptElement_ContainsUpdateLoginTextFunction() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("function updateLoginText"),
            "Script should contain updateLoginText function");
        assertTrue(scriptElement.contains("ref.loggedUserText.text('Guest')"),
            "updateLoginText should set text to 'Guest'");
    }

    @Test
    @DisplayName("Script element should contain template-form element reference")
    void testGetScriptElement_ContainsTemplateFormReference() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("getElementById(\"template-form\")"),
            "Script should reference template-form element");
    }

    @Test
    @DisplayName("Script element should contain __hosted__container__ element reference")
    void testGetScriptElement_ContainsHostedContainerReference() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("getElementById(\"__hosted__container__\")"),
            "Script should reference __hosted__container__ element");
    }

    @Test
    @DisplayName("Script element should clone template content")
    void testGetScriptElement_ClonesTemplateContent() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("template.content.cloneNode(true)"),
            "Script should clone template content");
    }

    @Test
    @DisplayName("Script element should append cloned template to container")
    void testGetScriptElement_AppendsToContainer() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("appendChild(templateClone)"),
            "Script should append cloned template to container");
    }

    @Test
    @DisplayName("Multiple calls should return same script content")
    void testGetScriptElement_ConsistentResults() {
        // Act
        String scriptElement1 = integrationArea.getScriptElement();
        String scriptElement2 = integrationArea.getScriptElement();

        // Assert
        assertEquals(scriptElement1, scriptElement2,
            "Multiple calls should return identical script content");
    }

    @Test
    @DisplayName("Script element should be valid JavaScript syntax")
    void testGetScriptElement_ValidJavaScriptSyntax() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert - Basic syntax checks
        int openBraces = scriptElement.length() - scriptElement.replace("{", "").length();
        int closeBraces = scriptElement.length() - scriptElement.replace("}", "").length();
        assertEquals(openBraces, closeBraces, "Opening and closing braces should match");

        int openParens = scriptElement.length() - scriptElement.replace("(", "").length();
        int closeParens = scriptElement.length() - scriptElement.replace(")", "").length();
        assertEquals(openParens, closeParens, "Opening and closing parentheses should match");
    }

    @Test
    @DisplayName("Script element should contain const declarations")
    void testGetScriptElement_ContainsConstDeclarations() {
        // Act
        String scriptElement = integrationArea.getScriptElement();

        // Assert
        assertTrue(scriptElement.contains("const template"),
            "Script should declare template constant");
        assertTrue(scriptElement.contains("const templateClone"),
            "Script should declare templateClone constant");
    }
}

