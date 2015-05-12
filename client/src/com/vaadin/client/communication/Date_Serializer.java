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

import java.util.Date;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.metadata.Type;

import elemental.json.Json;
import elemental.json.JsonValue;

/**
 * Client side serializer/deserializer for java.util.Date
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class Date_Serializer implements JSONSerializer<Date> {

    @Override
    public Date deserialize(Type type, JsonValue jsonValue,
            ApplicationConnection connection) {
        return new Date((long) jsonValue.asNumber());
    }

    @Override
    public JsonValue serialize(Date value, ApplicationConnection connection) {
        return Json.create(value.getTime());
    }

}
