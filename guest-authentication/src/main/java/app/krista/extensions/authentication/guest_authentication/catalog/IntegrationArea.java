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

import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;

@Domain(id = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
        name = "Authentication",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "c3544170-0310-4bdc-9130-43dab2ad8a8a")
public class IntegrationArea {

    @CatalogRequest(description = "Get Script Element",
            id = "localDomainRequest_723f4e2c-4f16-4e21-98d4-35184ce1cefb",
            name = "Get Script Element",
            area = "Integration",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field(name = "Script Element", type = "Text", attributes = {}, options = {})
    public String getScriptElement() {
        return "<script>\n" +
                "    function prepareUserInterfaceClient(predicate, args) {\n" +
                "      predicate(args);\n" +
                "    }\n" +
                "    function updateLoginText(ref, data) {\n" +
                "      ref.loggedUserText.text('Guest');\n" +
                "    }\n" +
                "    const template = document.getElementById(\"template-form\");\n" +
                "    const templateClone = template.content.cloneNode(true);\n" +
                "    document.getElementById(\"__hosted__container__\").appendChild(templateClone);\n" +
                "  </script>";
    }

}