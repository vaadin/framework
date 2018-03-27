/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client.metadata;

public class TypeData {

    public static Type getType(Class<?> type) {
        return TypeDataStore.getType(type);
    }

    public static Class<?> getClass(String identifier) throws NoDataException {
        return TypeDataStore.getClass(identifier);
    }

    public static boolean hasIdentifier(String identifier) {
        return TypeDataStore.hasIdentifier(identifier);
    }
}
