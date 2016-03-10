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
 * A renderer for presenting HTML content.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class HtmlRenderer extends AbstractRenderer<String> {
    /**
     * Creates a new HTML renderer.
     *
     * @param nullRepresentation
     *            the html representation of {@code null} value
     */
    public HtmlRenderer(String nullRepresentation) {
        super(String.class, nullRepresentation);
    }

    /**
     * Creates a new HTML renderer.
     */
    public HtmlRenderer() {
        this("");
    }

    @Override
    public String getNullRepresentation() {
        return super.getNullRepresentation();
    }
}
