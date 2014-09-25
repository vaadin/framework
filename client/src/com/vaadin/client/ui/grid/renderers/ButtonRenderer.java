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
package com.vaadin.client.ui.grid.renderers;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.Util;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;

/**
 * A Renderer that displays buttons with textual captions. The values of the
 * corresponding column are used as the captions. Click handlers can be added to
 * the renderer, invoked when any of the rendered buttons is clicked.
 * 
 * @param <T>
 *            the row type
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ButtonRenderer<T> extends WidgetRenderer<String, Button> implements
        ClickHandler {

    /**
     * A handler for {@link RendererClickEvent renderer click events}.
     * 
     * @see {@link ButtonRenderer#addClickHandler(RendererClickHandler)}
     */
    public interface RendererClickHandler<T> extends EventHandler {

        /**
         * Called when a rendered button is clicked.
         * 
         * @param event
         *            the event representing the click
         */
        void onClick(RendererClickEvent<T> event);
    }

    /**
     * An event fired when a button rendered by a ButtonRenderer is clicked.
     */
    @SuppressWarnings("rawtypes")
    public static class RendererClickEvent<T> extends
            MouseEvent<RendererClickHandler> {

        @SuppressWarnings("unchecked")
        private static final Type<RendererClickHandler> TYPE = new Type<RendererClickHandler>(
                BrowserEvents.CLICK, new RendererClickEvent());

        private Cell cell;

        private T row;

        private RendererClickEvent() {
        }

        /**
         * Returns the cell of the clicked button.
         * 
         * @return the cell
         */
        public Cell getCell() {
            return cell;
        }

        /**
         * Returns the data object corresponding to the row of the clicked
         * button.
         * 
         * @return the row data object
         */
        public T getRow() {
            return row;
        }

        @Override
        public Type<RendererClickHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void dispatch(RendererClickHandler handler) {
            cell = WidgetRenderer.getCell(getNativeEvent());
            assert cell != null;
            Grid<T> grid = Util.findWidget(cell.getElement(), Grid.class);
            row = grid.getDataSource().getRow(cell.getRow());
            handler.onClick(this);
        }
    }

    private HandlerManager handlerManager;

    @Override
    public Button createWidget() {
        Button b = GWT.create(Button.class);
        b.addClickHandler(this);
        return b;
    }

    @Override
    public void render(FlyweightCell cell, String text, Button button) {
        button.setText(text);
    }

    /**
     * Adds a click handler to this button renderer. The handler is invoked
     * every time one of the buttons rendered by this renderer is clicked.
     * 
     * @param handler
     *            the click handler to be added
     */
    public HandlerRegistration addClickHandler(RendererClickHandler<T> handler) {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager.addHandler(RendererClickEvent.TYPE, handler);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (handlerManager != null) {
            DomEvent.fireNativeEvent(event.getNativeEvent(), handlerManager);
        }
    }
}
