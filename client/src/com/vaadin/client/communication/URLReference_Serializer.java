/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.communication;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.metadata.Type;
import com.vaadin.shared.communication.URLReference;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class URLReference_Serializer implements JSONSerializer<URLReference> {

    // setURL() -> uRL as first char becomes lower case...
    private static final String URL_FIELD = "uRL";

    @Override
    public URLReference deserialize(Type type, JsonValue jsonValue,
            ApplicationConnection connection) {
        TranslatedURLReference reference = GWT
                .create(TranslatedURLReference.class);
        reference.setConnection(connection);
        JsonObject json = (JsonObject) jsonValue;
        if (json.hasKey(URL_FIELD)) {
            JsonValue jsonURL = json.get(URL_FIELD);
            String URL = (String) JsonDecoder.decodeValue(
                    new Type(String.class.getName(), null), jsonURL, null,
                    connection);
            reference.setURL(URL);
        }
        return reference;
    }

    @Override
    public JsonValue serialize(URLReference value,
            ApplicationConnection connection) {
        JsonObject json = Json.createObject();
        // No type info required for encoding a String...
        json.put(URL_FIELD,
                JsonEncoder.encode(value.getURL(), null, connection));
        return json;
    }

}
