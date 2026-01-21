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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.inject.Inject;
import app.krista.extension.authorization.AuthorizationException;
import app.krista.extension.common.EmailAddresses;
import app.krista.extension.executor.Invoker;
import app.krista.ksdk.accounts.Account;
import app.krista.ksdk.accounts.AccountManager;
import app.krista.ksdk.accounts.ModifiableAccount;
import app.krista.ksdk.authorization.ModifiableRole;
import app.krista.ksdk.authorization.Role;
import app.krista.ksdk.authorization.RoleManager;
import app.krista.omni.sdk.spi.impl.services.AccountService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jvnet.hk2.annotations.Service;

import static app.krista.extensions.authentication.guest_authentication.GuestAuthenticationExtension.*;
import static app.krista.extensions.authentication.guest_authentication.rest.GuestAuthenticationExtensionConstants.*;

/**
 * Create or fetch workspace account with given IdentificationToken.
 */
@Service
public final class AccountProvisioner {

    private final AccountManager accountManager;
    private final RoleManager roleManager;
    private final Invoker invoker;

    private final Map<String, Object> defaultAttributes = Map.of("ORG", "KristaSoft",
            "KRISTA_SOURCE", "Omni Chatbot",
            "SOURCE", "OMNI");

    @Inject
    public AccountProvisioner(AccountManager accountManager, RoleManager roleManager, Invoker invoker) {
        this.accountManager = accountManager;
        this.roleManager = roleManager;
        this.invoker = invoker;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public Account provisionAccount(String emailAddress) {
        String assignedRole = createRoleIfNotPresent();
        ModifiableAccount modifiableAccount = accountManager.lookupAccount(emailAddress);
        List<String> roleNames = ensureHasAllRoles(modifiableAccount, assignedRole);
        if (modifiableAccount == null) {
            modifiableAccount = accountManager.createAccount(EmailAddresses.getLocalPart(emailAddress),
                    EmailAddresses.normalizeEmailAddress(emailAddress),
                    new LinkedHashSet<>(roleNames), getAttributes());
        }
        return modifiableAccount;
    }

    private Map<String, Object> getAttributes() {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("KRISTA_LAST_LOGIN", getCurrentDateTime());
        userAttributes.putAll(defaultAttributes);

        Map<String, Object> attributes = invoker.getAttributes();
        String customAttributes = (String)attributes.getOrDefault(ATTRIBUTE_PARAMETERS, "");

        if(!customAttributes.isBlank()){
            JsonObject jsonObject = JsonParser.parseString(customAttributes).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonElement value = entry.getValue();
                if(value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || value.getAsString().isBlank()){
                    continue;
                }
               userAttributes.put(entry.getKey(), value.getAsString());
            }
        }
        return userAttributes;
    }

    private List<String> ensureHasAllRoles(ModifiableAccount modifiableAccount, String roles)
            throws AuthorizationException {
        List<String> allRoles = new ArrayList<>();
        Iterable<ModifiableRole> workspaceRoles = roleManager.getRoles();
        boolean roleExit = false;
        for (ModifiableRole workspaceRole : workspaceRoles) {
            if (Objects.equals(roles, workspaceRole.getName())) {
                allRoles.add(workspaceRole.getRoleId());
                roleExit = true;
                break;
            }
        }
        if (!roleExit) {
            ModifiableRole role = roleManager.createRole(roles);
            allRoles.add(role.getRoleId());
        }
        if (modifiableAccount != null) {
            for (Role role : modifiableAccount.getRoles()) {
                allRoles.add(role.getRoleId());
            }
            modifiableAccount.addRole(allRoles.toArray(String[]::new));
        }
        return allRoles;
    }

    private String createRoleIfNotPresent() throws AuthorizationException {
        String defaultRole = getDefaultRole();
        boolean isRolePresent = isDefaultRolePresentInWorkspace(defaultRole);
        if (!isRolePresent) {
            roleManager.createRole(defaultRole);
        }
        return defaultRole;
    }

    private boolean isDefaultRolePresentInWorkspace(String defaultRole) throws AuthorizationException {
        Iterable<ModifiableRole> roles = roleManager.getRoles();
        for (ModifiableRole role : roles) {
            if (Objects.equals(defaultRole, role.getName())) {
                return true;
            }
        }
        return false;
    }

    private String getDefaultRole() {
        String defaultRole = DEFAULT_ROLE_FOR_NEW_ACCOUNT;
        Object defaultRoleObject = invoker.getAttributes().get(DEFAULT_ROLE_KEY);
        if (defaultRoleObject instanceof String && !((String) defaultRoleObject).trim().isEmpty()) {
            defaultRole = (String) defaultRoleObject;
        }
        return defaultRole;
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(AccountService.DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(AccountService.GMT));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

}