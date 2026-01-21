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

import java.util.*;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.impl.anno.*;
import app.krista.ksdk.accounts.*;
import app.krista.ksdk.authentication.AuthenticationSettings;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.omni.sdk.medium.text.util.CommonInvokerParameters;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
@Field(name = GuestAuthenticationExtension.DEFAULT_ROLE_KEY, type = "Text", required = false)
@Field(name = CommonInvokerParameters.SESSION_TIMEOUT, type = "Number", required = false)
@Field(name = GuestAuthenticationExtension.ATTRIBUTE_PARAMETERS, type = "Text", required = false)
@Java(version = Java.Version.JAVA_21)
@Domain(id = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
        name = "Authentication",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "3e7e09ed-688f-41fa-ab7c-ff879e750011")
@Extension(implementingDomainIds = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7", jaxrsId = "authn", requireWorkspaceAdminRights = true, name="Guest Authentication", version="3.5.7")
@StaticResource(path = "docs", file = "docs")
public final class GuestAuthenticationExtension {

    private final Logger logger = LoggerFactory.getLogger(GuestAuthenticationExtension.class);

    public static final String DEFAULT_ROLE_KEY = "default_role";
    public static final String ATTRIBUTE_PARAMETERS = "attribute_parameters";
    private final AuthorizationContext authorizationContext;
    private final SessionManager sessionManager;
    private final AuthenticationSettings authenticationSettings;
    private final AccountProvider accountProvider;
    private final AttributeManager attributeManager;

    @Inject
    public GuestAuthenticationExtension(AuthorizationContext authorizationContext, SessionManager sessionManager,
            AuthenticationSettings authenticationSettings, AccountProvider accountProvider,
            AttributeManager attributeManager) {
        this.authorizationContext = authorizationContext;
        this.sessionManager = sessionManager;
        this.authenticationSettings = authenticationSettings;
        this.accountProvider = accountProvider;
        this.attributeManager = attributeManager;
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getAuthenticatedAccountId() {
        return new GuestAuthenticationRequestAuthenticator(authorizationContext, sessionManager, authenticationSettings,
                accountProvider);
    }

    @InvokerRequest(InvokerRequest.Type.PREPARE_CHANGE_ROUTING_ID)
    public void prepareChangeRoutingId(String routingId) {

    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, String> attributes) {
        String userAttributesValues = attributes.getOrDefault(ATTRIBUTE_PARAMETERS, "");
        if (userAttributesValues.isBlank()) {
            return;
        }

        List<String> invalidAttributes = new ArrayList<>();

        try {
            JsonObject jsonObject = JsonParser.parseString(userAttributesValues).getAsJsonObject();
            for (String attributesName : jsonObject.keySet()) {
                if (!verifyAttributeExists(attributesName)) {
                    invalidAttributes.add(attributesName);
                }
            }
            if (!invalidAttributes.isEmpty()) {
                throw new IllegalArgumentException("Missing attribute(s). Please create the following attribute(s): " +
                        String.join(", ", invalidAttributes));
            }
        } catch (JsonSyntaxException e) {
            logger.error("Invalid JSON Found : {}", e.getMessage(), e);
            throw new IllegalArgumentException(
                    "Invalid Json format found please verify Json entered for Attributes Parameter");

        } catch (Exception e) {
            logger.error("Invalid Input : {}", e.getMessage(), e);
            throw new RuntimeException("Invalid input : " + e.getMessage());
        }
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTab() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Documentation", "static/docs");
        return map;
    }

    private boolean verifyAttributeExists(String attribute) {
        try {
            return StreamSupport.stream(attributeManager.getAttributes().spliterator(), false)
                    .anyMatch(attr -> attribute.equals(attr.getName()));
        } catch (RuntimeException cause) {
            logger.error("Error while verifying attribute existence. Attribute Name: {} : {}", attribute, cause.getMessage(), cause);
            return false;
        }
    }
}
