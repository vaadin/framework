/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.shared.data;

import java.io.Serializable;

/**
 * Set of contants used by DataCommunicator. These are commonly used JsonObject
 * keys which are considered to be reserved for internal use.
 *
 * @since
 */
public final class DataCommunicatorConstants implements Serializable {
    public static final String KEY = "k";
    public static final String SELECTED = "s";
    public static final String NAME = "n";
    public static final String DATA = "d";
}