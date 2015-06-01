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
package com.vaadin.ui.renderers;

import com.vaadin.ui.Grid.AbstractRenderer;

import elemental.json.JsonValue;

/**
 * A renderer that represents a double values as a graphical progress bar.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class ProgressBarRenderer extends AbstractRenderer<Double> {

    /**
     * Creates a new text renderer
     */
    public ProgressBarRenderer() {
        super(Double.class, null);
    }

    @Override
    public JsonValue encode(Double value) {
        if (value != null) {
            value = Math.max(Math.min(value, 1), 0);
        } else {
            value = 0d;
        }
        return super.encode(value);
    }
}
