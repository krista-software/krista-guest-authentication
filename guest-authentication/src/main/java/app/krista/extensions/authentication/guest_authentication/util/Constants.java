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

package app.krista.extensions.authentication.guest_authentication.util;

public final class Constants {

    public static final String CHATBOT_SESSION_ID_HEADER = "Chatbot-Session-Id";

    private Constants() {
        throw new IllegalStateException("Invalid access to utility class");
    }

    public static final String SOURCE = "source";
    public static final String CHATBOT_SESSION_ID = "chatbotSessionId";
    public static final String X_KRISTA_CONTEXT = "X-Krista-Context";
    public static final String COMMENT = "comment";
    public static final String COOKIE = "Cookie";
    public static final String EQUAL_TO = "=";
    public static final String CALLER_URI = "caller_uri";

}
