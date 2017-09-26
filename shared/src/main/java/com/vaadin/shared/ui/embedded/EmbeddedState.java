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
package com.vaadin.shared.ui.embedded;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ui.AbstractEmbeddedState;

public class EmbeddedState extends AbstractEmbeddedState {
    {
        primaryStyleName = "v-embedded";
    }

    public int type;

    /**
     * The MIME type of the Embedded component.
     */
    public String mimeType;

    /**
     * Applet or other client side runnable properties.
     */
    public String codebase;

    public String codetype;

    public String classId;

    public String archive;

    public String altText;

    public String standby;

    /**
     * Object parameters.
     */
    public final Map<String, String> parameters = new HashMap<>();

}
