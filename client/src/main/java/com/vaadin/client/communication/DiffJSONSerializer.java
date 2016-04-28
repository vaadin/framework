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

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.metadata.Type;
import elemental.json.JsonValue;

public interface DiffJSONSerializer<T> extends JSONSerializer<T> {
    /**
     * Update the target object in place based on the passed JSON data.
     * 
     * @param target
     * @param jsonValue
     * @param connection
     */
    public void update(T target, Type type, JsonValue jsonValue,
            ApplicationConnection connection);
}
