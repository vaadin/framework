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

import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.renderers.ClickableRendererState;
import com.vaadin.shared.ui.grid.renderers.RendererClickRpc;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.util.ReflectTools;

/**
 * An abstract superclass for {@link Renderer}s that render clickable items.
 * Click listeners can be added to a renderer to be notified when any of the
 * rendered items is clicked.
 *
 * @param <T>
 *            the type of the parent {@link Grid}
 * @param <V>
 *            the type presented by the renderer
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract class ClickableRenderer<T, V> extends AbstractRenderer<T, V> {

    /**
     * An interface for listening to {@link RendererClickEvent renderer click
     * events}.
     *
     * @see ButtonRenderer#addClickListener(RendererClickListener)
     */
    @FunctionalInterface
    public interface RendererClickListener<T> extends ConnectorEventListener {

        static final Method CLICK_METHOD = ReflectTools.findMethod(
                RendererClickListener.class, "click", RendererClickEvent.class);

        /**
         * Called when a rendered button is clicked.
         *
         * @param event
         *            the event representing the click
         */
        void click(RendererClickEvent<T> event);
    }

    /**
     * An event fired when a clickable widget rendered by a ClickableRenderer is
     * clicked.
     *
     * @param <T>
     *            the item type associated with this click event
     */
    public static class RendererClickEvent<T> extends ClickEvent {

        private final T item;
        private final Column<T, ?> column;

        protected RendererClickEvent(Grid<T> source, T item,
                Column<T, ?> column, MouseEventDetails mouseEventDetails) {
            super(source, mouseEventDetails);
            this.item = item;
            this.column = column;
        }

        /**
         * Returns the item of the row where the click event originated.
         *
         * @return the item of the clicked row
         * @since 8.0
         */
        public T getItem() {
            return item;
        }

        /**
         * Returns the {@link Column} where the click event originated.
         *
         * @return the column of the click event
         */
        public Column<T, ?> getColumn() {
            return column;
        }
    }

    /**
     * Creates a new clickable renderer with the given presentation type. No
     * null representation will be used.
     *
     * @param presentationType
     *            the data type that this renderer displays, not
     *            <code>null</code>
     */
    protected ClickableRenderer(Class<V> presentationType) {
        this(presentationType, null);
    }

    /**
     * Creates a new clickable renderer with the given presentation type and
     * null representation.
     *
     * @param presentationType
     *            the data type that this renderer displays, not
     *            <code>null</code>
     * @param nullRepresentation
     *            a string that will be sent to the client instead of a regular
     *            value in case the actual cell value is <code>null</code>. May
     *            be <code>null</code>.
     */
    protected ClickableRenderer(Class<V> presentationType,
            String nullRepresentation) {
        super(presentationType, nullRepresentation);
        registerRpc((RendererClickRpc) (String rowKey, String columnId,
                MouseEventDetails mouseDetails) -> {
            Grid<T> grid = getParentGrid();
            T item = grid.getDataCommunicator().getKeyMapper().get(rowKey);
            Column<T, V> column = getParent();

            fireEvent(
                    new RendererClickEvent<>(grid, item, column, mouseDetails));
        });
    }

    /**
     * Adds a click listener to this button renderer. The listener is invoked
     * every time one of the buttons rendered by this renderer is clicked.
     *
     * @param listener
     *            the click listener to be added, not null
     * @since 8.0
     */
    public Registration addClickListener(RendererClickListener<T> listener) {
        return addListener(RendererClickEvent.class, listener,
                RendererClickListener.CLICK_METHOD);
    }

    /**
     * Removes the given click listener from this renderer.
     *
     * @param listener
     *            the click listener to be removed
     */
    @Deprecated
    public void removeClickListener(RendererClickListener<T> listener) {
        removeListener(RendererClickEvent.class, listener);
    }

    @Override
    protected ClickableRendererState getState() {
        return (ClickableRendererState) super.getState();
    }

    @Override
    protected ClickableRendererState getState(boolean markAsDirty) {
        return (ClickableRendererState) super.getState(markAsDirty);
    }
}
