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

import com.vaadin.shared.ui.grid.renderers.TextRendererState;

import elemental.json.Json;
import elemental.json.JsonValue;

/**
 * A renderer for presenting a plain text representation of any value.
 * {@link Object#toString()} is used for determining the text to show.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextRenderer extends AbstractRenderer<Object, Object> {

    /**
     * Creates a new text renderer that uses <code>""</code> to represent null
     * values.
     */
    public TextRenderer() {
        this("");
    }

    /**
     * Creates a new text renderer with the given string to represent null
     * values.
     *
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     */
    public TextRenderer(String nullRepresentation) {
        super(Object.class, nullRepresentation);
    }

    @Override
    public JsonValue encode(Object value) {
        if (value == null) {
            return super.encode(null);
        } else {
            return Json.create(value.toString());
        }
    }

    @Override
    public String getNullRepresentation() {
        return super.getNullRepresentation();
    }

    @Override
    protected TextRendererState getState() {
        return (TextRendererState) super.getState();
    }

    @Override
    protected TextRendererState getState(boolean markAsDirty) {
        return (TextRendererState) super.getState(markAsDirty);
    }
}
