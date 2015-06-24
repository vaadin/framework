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

import elemental.json.JsonValue;

/**
 * A Renderer that displays a button with a textual caption. The value of the
 * corresponding property is used as the caption. Click listeners can be added
 * to the renderer, invoked when any of the rendered buttons is clicked.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ButtonRenderer extends ClickableRenderer<String> {

    /**
     * Creates a new button renderer.
     *
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     */
    public ButtonRenderer(String nullRepresentation) {
        super(String.class, nullRepresentation);
    }

    /**
     * Creates a new button renderer and adds the given click listener to it.
     * 
     * @param listener
     *            the click listener to register
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     */
    public ButtonRenderer(RendererClickListener listener,
            String nullRepresentation) {
        this(nullRepresentation);
        addClickListener(listener);
    }

    /**
     * Creates a new button renderer.
     */
    public ButtonRenderer() {
        this("");
    }

    /**
     * Creates a new button renderer and adds the given click listener to it.
     *
     * @param listener
     *            the click listener to register
     */
    public ButtonRenderer(RendererClickListener listener) {
        this(listener, "");
    }

    @Override
    public String getNullRepresentation() {
        return super.getNullRepresentation();
    }

}
