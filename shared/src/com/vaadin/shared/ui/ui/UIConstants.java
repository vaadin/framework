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
package com.vaadin.shared.ui.ui;

import java.io.Serializable;

public class UIConstants implements Serializable {
    /**
     * Attribute name for the lazy resize setting .
     */
    @Deprecated
    public static final String RESIZE_LAZY = "rL";

    @Deprecated
    public static final String NOTIFICATION_HTML_CONTENT_NOT_ALLOWED = "useplain";

    @Deprecated
    public static final String LOCATION_VARIABLE = "location";

    @Deprecated
    public static final String ATTRIBUTE_NOTIFICATION_STYLE = "style";
    @Deprecated
    public static final String ATTRIBUTE_NOTIFICATION_CAPTION = "caption";
    @Deprecated
    public static final String ATTRIBUTE_NOTIFICATION_MESSAGE = "message";
    @Deprecated
    public static final String ATTRIBUTE_NOTIFICATION_ICON = "icon";
    @Deprecated
    public static final String ATTRIBUTE_NOTIFICATION_POSITION = "position";
    @Deprecated
    public static final String ATTRIBUTE_NOTIFICATION_DELAY = "delay";

    /**
     * Name of the parameter used to transmit UI ids back and forth
     */
    public static final String UI_ID_PARAMETER = "v-uiId";

}
