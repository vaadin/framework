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

import com.vaadin.server.JsonCodec;
import com.vaadin.shared.ui.grid.renderers.ObjectRendererState;

import elemental.json.JsonValue;

/**
 * A renderer for representing any type encoded as a string.
 *
 * @since 8.0
 */
public class ObjectRenderer extends AbstractRenderer<Object, Object> {

    /**
     * Creates a new object renderer, with the empty string as its
     * representation for {@code null} values.
     */
    public ObjectRenderer() {
        this("");
    }

    /**
     * Creates a new object renderer.
     *
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     */
    public ObjectRenderer(String nullRepresentation) {
        super(Object.class, nullRepresentation);
    }

    @Override
    protected ObjectRendererState getState() {
        return (ObjectRendererState) super.getState();
    }

    @Override
    protected ObjectRendererState getState(boolean markAsDirty) {
        return (ObjectRendererState) super.getState(markAsDirty);
    }

    @Override
    protected <U> JsonValue encode(U value, Class<U> type) {
        return JsonCodec.encode(String.valueOf(value), null, String.class,
                getUI().getConnectorTracker()).getEncodedValue();
    }
}
