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
package com.vaadin.server.communication;

import java.lang.reflect.Type;
import java.util.Date;

import com.vaadin.ui.ConnectorTracker;

/**
 * Server side serializer/deserializer for java.util.Date
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class DateSerializer implements JSONSerializer<Date> {

    @Override
    public Date deserialize(Type type, Object jsonValue,
            ConnectorTracker connectorTracker) {
        return new Date(Long.valueOf(String.valueOf(jsonValue)));
    }

    @Override
    public Object serialize(Date value, ConnectorTracker connectorTracker) {
        return value.getTime();
    }

}
