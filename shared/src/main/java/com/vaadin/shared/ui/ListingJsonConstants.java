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
package com.vaadin.shared.ui;

import java.io.Serializable;

/**
 * JSON key constants for common listing item attributes in server-client
 * communication.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class ListingJsonConstants implements Serializable {
    public static final String JSONKEY_ITEM_DISABLED = "d";

    public static final String JSONKEY_ITEM_ICON = "i";

    public static final String JSONKEY_ITEM_VALUE = "v";

    public static final String JSONKEY_ITEM_SELECTED = "s";
}
