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

import app.krista.extension.common.AuthenticationResponse;
import app.krista.extensions.util.KeyValueStore;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountManagerImpl.
 * 
 * Tests cover:
 * - Storing authentication responses
 * - Retrieving authentication responses
 * - Deleting authentication responses
 * - JSON serialization/deserialization
 */
@DisplayName("Account Manager Implementation Tests")
class AccountManagerImplTest {

    @Mock
    private KeyValueStore keyValueStore;

    private AccountManagerImpl accountManager;
    private Gson gson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountManager = new AccountManagerImpl(keyValueStore);
        gson = new Gson();
    }

    @Test
    @DisplayName("Should store authentication response successfully")
    void testPut_StoresAuthenticationResponse() {
        // Arrange
        String sessionId = "test-session-123";
        AuthenticationResponse authResponse = new AuthenticationResponse(
            sessionId, "Test User", "avatar.png", "account-456",
            "krista-account-123", "person-789", List.of("role1"),
            "inbox-001", false, false,
            Map.of("email", "test@example.com"), Map.of()
        );

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        accountManager.put(sessionId, authResponse);

        // Assert
        verify(keyValueStore, times(1)).put(keyCaptor.capture(), valueCaptor.capture());
        assertEquals(sessionId, keyCaptor.getValue(), "Session ID should match");

        // Verify JSON serialization
        String storedJson = valueCaptor.getValue();
        assertNotNull(storedJson, "Stored JSON should not be null");
        assertTrue(storedJson.contains("account-456"), "JSON should contain account ID");
    }

    @Test
    @DisplayName("Should retrieve authentication response successfully")
    void testGet_RetrievesAuthenticationResponse() {
        // Arrange
        String sessionId = "test-session-789";
        AuthenticationResponse expectedResponse = new AuthenticationResponse(
            sessionId, "Test User", "avatar.png", "account-999",
            "krista-account-456", "person-123", List.of("role1", "role2"),
            "inbox-002", true, false,
            Map.of("email", "user@example.com"), Map.of("extra", "data")
        );

        String jsonResponse = gson.toJson(expectedResponse);
        when(keyValueStore.get(sessionId)).thenReturn(jsonResponse);

        // Act
        AuthenticationResponse actualResponse = accountManager.get(sessionId);

        // Assert
        assertNotNull(actualResponse, "Retrieved response should not be null");
        assertEquals(expectedResponse.getAccountId(), actualResponse.getAccountId(),
            "Account ID should match");
        assertEquals(expectedResponse.getClientSessionId(), actualResponse.getClientSessionId(),
            "Session ID should match");
        verify(keyValueStore, times(1)).get(sessionId);
    }

    @Test
    @DisplayName("Should return null when session does not exist")
    void testGet_ReturnsNullForNonExistentSession() {
        // Arrange
        String sessionId = "non-existent-session";
        when(keyValueStore.get(sessionId)).thenReturn(null);

        // Act
        AuthenticationResponse response = accountManager.get(sessionId);

        // Assert
        assertNull(response, "Response should be null for non-existent session");
        verify(keyValueStore, times(1)).get(sessionId);
    }

    @Test
    @DisplayName("Should delete authentication response successfully")
    void testDelete_RemovesAuthenticationResponse() {
        // Arrange
        String sessionId = "session-to-delete";

        // Act
        accountManager.delete(sessionId);

        // Assert
        verify(keyValueStore, times(1)).remove(sessionId);
    }

    @Test
    @DisplayName("Should handle multiple put operations")
    void testPut_MultipleOperations() {
        // Arrange
        String sessionId1 = "session-1";
        String sessionId2 = "session-2";

        AuthenticationResponse response1 = new AuthenticationResponse(
            sessionId1, "User 1", "avatar1.png", "account-1",
            "krista-1", "person-1", List.of("role1"),
            "inbox-1", false, false,
            Map.of("email", "user1@example.com"), Map.of()
        );

        AuthenticationResponse response2 = new AuthenticationResponse(
            sessionId2, "User 2", "avatar2.png", "account-2",
            "krista-2", "person-2", List.of("role2"),
            "inbox-2", false, false,
            Map.of("email", "user2@example.com"), Map.of()
        );

        // Act
        accountManager.put(sessionId1, response1);
        accountManager.put(sessionId2, response2);

        // Assert
        verify(keyValueStore, times(2)).put(anyString(), anyString());
    }

    @Test
    @DisplayName("Should serialize and deserialize complex authentication response")
    void testPutAndGet_ComplexAuthenticationResponse() {
        // Arrange
        String sessionId = "complex-session";
        AuthenticationResponse originalResponse = new AuthenticationResponse(
            sessionId, "Complex User", "complex-avatar.png", "complex-account",
            "krista-complex", "person-complex", List.of("admin", "user"),
            "inbox-complex", true, true,
            Map.of("email", "complex@example.com", "domain", "example.com"),
            Map.of("loginTime", "2024-01-01", "source", "web")
        );

        String jsonResponse = gson.toJson(originalResponse);

        // Mock the put operation
        doNothing().when(keyValueStore).put(anyString(), anyString());

        // Mock the get operation to return the JSON
        when(keyValueStore.get(sessionId)).thenReturn(jsonResponse);

        // Act
        accountManager.put(sessionId, originalResponse);
        AuthenticationResponse retrievedResponse = accountManager.get(sessionId);

        // Assert
        assertNotNull(retrievedResponse, "Retrieved response should not be null");
        assertEquals(originalResponse.getAccountId(), retrievedResponse.getAccountId(),
            "Account ID should be preserved through serialization");
        assertEquals(originalResponse.getClientSessionId(), retrievedResponse.getClientSessionId(),
            "Session ID should be preserved through serialization");
    }

    @Test
    @DisplayName("Should handle empty session ID")
    void testPut_EmptySessionId() {
        // Arrange
        String emptySessionId = "";
        AuthenticationResponse response = new AuthenticationResponse(
            emptySessionId, "Test User", "avatar.png", "account-123",
            "krista-123", "person-123", List.of("role1"),
            "inbox-123", false, false,
            Map.of("email", "test@example.com"), Map.of()
        );

        // Act
        accountManager.put(emptySessionId, response);

        // Assert
        verify(keyValueStore, times(1)).put(eq(emptySessionId), anyString());
    }
}

