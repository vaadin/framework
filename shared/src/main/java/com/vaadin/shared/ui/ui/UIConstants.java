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
package com.vaadin.shared.ui.ui;

import java.io.Serializable;

public class UIConstants implements Serializable {
    /**
     * Attribute name for the lazy resize setting .
     */
    @Deprecated
    public static final String RESIZE_LAZY = "rL";

    @Deprecated
    public static final String ATTRIBUTE_PUSH_STATE = "ps";

    @Deprecated
    public static final String ATTRIBUTE_REPLACE_STATE = "rs";

    /**
     * Name of the parameter used to transmit UI ids back and forth.
     */
    public static final String UI_ID_PARAMETER = "v-uiId";

}
