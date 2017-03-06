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
package com.vaadin.ui.renderers;

import com.vaadin.ui.Component;

import elemental.json.Json;
import elemental.json.JsonValue;

/**
 * A renderer for presenting Components.
 * <p>
 * <strong>Note:</strong> The use of ComponentRenderer causes the Grid to
 * generate components for all items currently available in the client-side.
 * This means that a number of components is always generated and sent to the
 * client. Using complex structures of many nested components might be heavy to
 * generate and store, which will lead to performance problems.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class ComponentRenderer extends AbstractRenderer<Object, Component> {

    /**
     * Constructor for ComponentRenderer.
     */
    public ComponentRenderer() {
        super(Component.class);
    }

    @Override
    public JsonValue encode(Component value) {
        return Json.create(value.getConnectorId());
    }
}
