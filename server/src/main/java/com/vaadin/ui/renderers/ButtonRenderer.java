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

import com.vaadin.shared.ui.grid.renderers.ButtonRendererState;

/**
 * A Renderer that displays a button with a textual caption. The value of the
 * corresponding property is used as the caption. Click listeners can be added
 * to the renderer, invoked when any of the rendered buttons is clicked.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ButtonRenderer<T> extends ClickableRenderer<T, String> {

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
    public ButtonRenderer(RendererClickListener<T> listener,
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
    public ButtonRenderer(RendererClickListener<T> listener) {
        this(listener, "");
    }

    @Override
    public String getNullRepresentation() {
        return super.getNullRepresentation();
    }

    @Override
    protected ButtonRendererState getState() {
        return (ButtonRendererState) super.getState();
    }

    @Override
    protected ButtonRendererState getState(boolean markAsDirty) {
        return (ButtonRendererState) super.getState(markAsDirty);
    }

    /**
     * Sets whether the data should be rendered as HTML (instead of text).
     * <p>
     * By default everything is rendered as text.
     *
     * @param htmlContentAllowed
     *            <code>true</code> to render as HTML, <code>false</code> to
     *            render as text
     *
     * @since 8.0.3
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Gets whether the data should be rendered as HTML (instead of text).
     * <p>
     * By default everything is rendered as text.
     *
     * @return <code>true</code> if the renderer renders a HTML,
     *         <code>false</code> if the content is rendered as text
     *
     * @since 8.0.3
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).htmlContentAllowed;
    }
}
