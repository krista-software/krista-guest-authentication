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
import app.krista.extension.authorization.MustAuthenticateException;
import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.common.CommonUtils;
import app.krista.extension.common.EmailAddresses;
import app.krista.extension.request.ProtoRequest;
import app.krista.extension.request.ProtoResponse;
import app.krista.extension.request.protos.http.HttpRequest;
import app.krista.extensions.authentication.guest_authentication.util.Constants;
import app.krista.ksdk.accounts.AccountProvider;
import app.krista.ksdk.authentication.AuthenticationSettings;
import app.krista.ksdk.authentication.SessionManager;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.model.field.NamedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.MultivaluedMap;

@SuppressWarnings("deprecation")
public class GuestAuthenticationRequestAuthenticator implements RequestAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuestAuthenticationRequestAuthenticator.class);

    private final AuthorizationContext authorizationContext;
    private final SessionManager sessionManager;
    private final AuthenticationSettings authenticationSettings;
    private final AccountProvider accountProvider;

    public GuestAuthenticationRequestAuthenticator(AuthorizationContext authorizationContext,
            SessionManager sessionManager, AuthenticationSettings authenticationSettings,
            AccountProvider accountProvider) {
        this.authorizationContext = authorizationContext;
        this.sessionManager = sessionManager;
        this.authenticationSettings = authenticationSettings;
        this.accountProvider = accountProvider;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return null;
    }

    @Override
    public String getAuthenticatedAccountId(ProtoRequest protoRequest) {
        try {
            HttpRequest httpRequest = (HttpRequest) protoRequest;
            if (Objects.equals("/upsertPersonAttributes", httpRequest.getUri().getPath())) {
                // Because to update user attributes, we need Krista Appliance's account
                return authorizationContext.getAuthorizedAccount().getAccountId();
            }

            httpRequest.bufferBody();
            String cookie = httpRequest.getHeader(Constants.COOKIE);
            String clientSessionId = getCookie(cookie, Constants.CHATBOT_SESSION_ID);
            if (clientSessionId == null) {
                clientSessionId = getClientSessionIdFromHeaderOrQueryParams(httpRequest);
            }
            if (!Objects.isNull(clientSessionId)) {
                String authenticatedAccountId = sessionManager.lookupAccountId(clientSessionId);
                if (!Objects.isNull(authenticatedAccountId)) {
                    try {
                        accountProvider.getAccount(authenticatedAccountId);
                        LOGGER.info("Got Authenticated AccountId: {}", authenticatedAccountId);
                        return authenticatedAccountId;
                    } catch (NoSuchElementException cause) {
                        LOGGER.error("No such account Id exist: {} with exception: {}", authenticatedAccountId, cause);
                    }
                }
                LOGGER.info("No account found for clientSessionId: {}", clientSessionId);
            }
            return handleLoginRequest((HttpRequest) protoRequest);
        } catch (Exception cause) {
            throw new IllegalStateException("Failed to authenticate : Reason: " + cause.getMessage(), cause);
        }

    }

    private static String getClientSessionIdFromHeaderOrQueryParams(HttpRequest httpRequest) {
        String clientSessionId;
        clientSessionId = httpRequest.getHeader(Constants.CHATBOT_SESSION_ID_HEADER);
        LOGGER.info("Client Session Id from query param: {}", clientSessionId);
        if (Objects.isNull(clientSessionId)) {
            MultivaluedMap<String, String> queryParameters = httpRequest.getQueryParameters();
            LOGGER.info("queryParams: {}", queryParameters);
            if (!queryParameters.isEmpty()) {
                List<String> queryParamSessionId = queryParameters.get("clientSessionId");
                clientSessionId = (Objects.isNull(queryParamSessionId) || queryParamSessionId.isEmpty()) ? null
                        : queryParamSessionId.get(0);
            }
        }
        return clientSessionId;
    }

    private String getCookie(String cookie, String key) {
        if (!Objects.isNull(cookie)) {
            String[] cookies = cookie.split(";");
            for (String c : cookies) {
                if (c.trim().startsWith(key)) {
                    return c.split("=")[1];
                }
            }
        }
        return null;
    }

    @Override
    public boolean setServiceAuthorization(String s) {
        return false;
    }

    @Override
    public Map<String, NamedField> getAttributeFields() {
        return null;
    }

    @Override
    public ProtoResponse getMustAuthenticateResponse(MustAuthenticateException e, ProtoRequest protoRequest) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthenticateResponse(MustAuthenticateException e) {
        return null;
    }

    @Override
    public ProtoResponse getMustAuthorizeResponse(MustAuthorizeException e, ProtoRequest protoRequest) {
        return null;
    }

    @Override
    public AuthorizationResponse getMustAuthorizeResponse(MustAuthorizeException e) {
        return null;
    }

    private String handleLoginRequest(HttpRequest protoRequest) {
        if (Objects.equals("/login", protoRequest.getUri().getPath())) {
            LOGGER.info("Authorization Context: {}", authorizationContext.getAuthorizedAccount().getAccountId());
            return authorizationContext.getAuthorizedAccount().getAccountId();
        }
        return null;
    }

    private void addDomainToWorkspaceIfNotPresent(String emailAddress) {
        List<String> supportedDomainsForWorkspace = authenticationSettings.getSupportedDomains();
        String supportedDomain = String.join(",", supportedDomainsForWorkspace);
        String invokerDomains = EmailAddresses.getDomainName(emailAddress);
        CommonUtils.validateIfSupportedDomain(emailAddress, supportedDomain, invokerDomains);
        CommonUtils.addSupportedDomainsToWorkspace(emailAddress, supportedDomainsForWorkspace, authenticationSettings);
    }

}
