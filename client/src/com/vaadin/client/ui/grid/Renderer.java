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
package com.vaadin.client.ui.grid;

import java.util.Collection;

import com.google.gwt.dom.client.NativeEvent;

/**
 * Renderer for rending a value &lt;T&gt; into cell.
 * <p>
 * You can add a renderer to any column by overring the
 * {@link GridColumn#getRenderer()} method and returning your own renderer. You
 * can retrieve the cell element using {@link Cell#getElement()}.
 * 
 * @param <T>
 *            The column type
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface Renderer<T> {

    /**
     * Called at initialization stage. Perform any initialization here e.g.
     * attach handlers, attach widgets etc.
     * 
     * @param cell
     *            The cell. Note that the cell is a flyweight and should not be
     *            stored outside of the method as it will change.
     */
    void init(Cell cell);

    /**
     * Returns the events that the renderer should consume. These are also the
     * events that the Grid will pass to
     * {@link #onBrowserEvent(CellInfo, NativeEvent)} when they occur.
     * <code>null</code> if no events are consumed
     * 
     * @return the consumed events, or null if no events are consumed
     * 
     * @see com.google.gwt.dom.client.BrowserEvents
     */
    Collection<String> getConsumedEvents();

    /**
     * Called whenever a registered event is triggered in the column the
     * renderer renders.
     * <p>
     * The events that triggers this needs to be returned by the
     * {@link #getConsumedEvents()} method.
     * 
     * @param cellInfo
     *            Object containing information about the cell the event was
     *            triggered on.
     * 
     * @param event
     *            The original DOM event
     */
    void onBrowserEvent(CellInfo cell, NativeEvent event);

    /**
     * Called whenever the {@link Grid} updates a cell
     * 
     * @param cell
     *            The cell. Note that the cell is a flyweight and should not be
     *            stored outside of the method as it will change.
     * 
     * @param data
     *            The column data object
     */
    void render(Cell cell, T data);

    /**
     * Called when the cell is "activated" by pressing <code>enter</code> or
     * double clicking
     * 
     * @return <code>true</code> if event was handled and should not be
     *         interpreted as a generic gesture by Grid.
     */
    boolean onActivate();
}
