/*
 * Copyright 2000-2021 Vaadin Ltd.
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

    /**
     * The object type.
     *
     * @since 8.2
     */
    public int type;

    /**
     * The MIME-type of the object.
     *
     * @since 8.2
     */
    public String mimeType;

    /**
     * Specifies the base path used to resolve relative URIs specified by the
     * classid, data, and archive attributes.
     *
     * @since 8.2
     */
    public String codebase;

    /**
     * The MIME-Type of the code.
     *
     * @since 8.2
     */
    public String codetype;

    /**
     * May be used to specify the location of an object's implementation via a
     * URI.
     *
     * @since 8.2
     */
    public String classId;

    /**
     * May be used to specify a space-separated list of URIs for archives
     * containing resources relevant to the object.
     *
     * @since 8.2
     */
    public String archive;

    /**
     * The component's "alt-text".
     *
     * @since 8.2
     */
    public String altText;

    /**
     * Specifies a message that a user agent may render while loading the
     * object's implementation and data.
     *
     * @since 8.2
     */
    public String standby;

    /**
     * Object parameters. Parameters are optional information, and they are
     * passed to the instantiated object.
     *
     * @since 8.2
     */
    public final Map<String, String> parameters = new HashMap<>();

}
