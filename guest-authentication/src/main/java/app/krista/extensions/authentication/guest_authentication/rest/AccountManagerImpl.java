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
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;
import javax.inject.Inject;

@Service
@ContractsProvided(AccountManager.class)
public class AccountManagerImpl implements AccountManager {


    private final KeyValueStore keyValueStore;
    private final Gson gson;
    @Inject
    public AccountManagerImpl(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
        this.gson = new Gson();
    }

    @Override
    public void put(String sessionId, AuthenticationResponse authenticationResponse) {
        String authResponse = gson.toJson(authenticationResponse);
        keyValueStore.put(sessionId, authResponse);
    }

    @Override
    public AuthenticationResponse get(String sessionId) {
        String authResponse = (String) keyValueStore.get(sessionId);
        return gson.fromJson(authResponse, AuthenticationResponse.class);
    }

    @Override
    public void delete(String sessionId) {
        keyValueStore.remove(sessionId);
    }

}
