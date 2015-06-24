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

import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.grid.renderers.RendererClickRpc;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractRenderer;
import com.vaadin.ui.Grid.Column;
import com.vaadin.util.ReflectTools;

/**
 * An abstract superclass for Renderers that render clickable items. Click
 * listeners can be added to a renderer to be notified when any of the rendered
 * items is clicked.
 * 
 * @param <T>
 *            the type presented by the renderer
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ClickableRenderer<T> extends AbstractRenderer<T> {

    /**
     * An interface for listening to {@link RendererClickEvent renderer click
     * events}.
     * 
     * @see {@link ButtonRenderer#addClickListener(RendererClickListener)}
     */
    public interface RendererClickListener extends ConnectorEventListener {

        static final Method CLICK_METHOD = ReflectTools.findMethod(
                RendererClickListener.class, "click", RendererClickEvent.class);

        /**
         * Called when a rendered button is clicked.
         * 
         * @param event
         *            the event representing the click
         */
        void click(RendererClickEvent event);
    }

    /**
     * An event fired when a button rendered by a ButtonRenderer is clicked.
     */
    public static class RendererClickEvent extends ClickEvent {

        private Object itemId;
        private Column column;

        protected RendererClickEvent(Grid source, Object itemId, Column column,
                MouseEventDetails mouseEventDetails) {
            super(source, mouseEventDetails);
            this.itemId = itemId;
            this.column = column;
        }

        /**
         * Returns the item ID of the row where the click event originated.
         * 
         * @return the item ID of the clicked row
         */
        public Object getItemId() {
            return itemId;
        }

        /**
         * Returns the {@link Column} where the click event originated.
         * 
         * @return the column of the click event
         */
        public Column getColumn() {
            return column;
        }

        /**
         * Returns the property ID where the click event originated.
         * 
         * @return the property ID of the clicked cell
         */
        public Object getPropertyId() {
            return column.getPropertyId();
        }
    }

    protected ClickableRenderer(Class<T> presentationType) {
        this(presentationType, null);
    }

    protected ClickableRenderer(Class<T> presentationType,
            String nullRepresentation) {
        super(presentationType, nullRepresentation);
        registerRpc(new RendererClickRpc() {
            @Override
            public void click(String rowKey, String columnId,
                    MouseEventDetails mouseDetails) {
                fireEvent(new RendererClickEvent(getParentGrid(),
                        getItemId(rowKey), getColumn(columnId), mouseDetails));
            }
        });
    }

    /**
     * Adds a click listener to this button renderer. The listener is invoked
     * every time one of the buttons rendered by this renderer is clicked.
     * 
     * @param listener
     *            the click listener to be added
     */
    public void addClickListener(RendererClickListener listener) {
        addListener(RendererClickEvent.class, listener,
                RendererClickListener.CLICK_METHOD);
    }

    /**
     * Removes the given click listener from this renderer.
     * 
     * @param listener
     *            the click listener to be removed
     */
    public void removeClickListener(RendererClickListener listener) {
        removeListener(RendererClickEvent.class, listener);
    }
}
