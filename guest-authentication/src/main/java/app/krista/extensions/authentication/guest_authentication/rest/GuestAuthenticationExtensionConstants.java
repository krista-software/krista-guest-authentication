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

import app.krista.model.field.util.GsonJsonMapper;

public final class GuestAuthenticationExtensionConstants {

    public final static GsonJsonMapper GSON_JSON_MAPPER = GsonJsonMapper.create();
    public final static String DEFAULT_ROLE_FOR_NEW_ACCOUNT = "Krista Guest User";
    public final static String DEFAULT_ACCOUNT_EMAIL = "guest@kristasoft.com";
    public static final String AUTHENTICATION_TYPE = "Guest Authentication";

}
