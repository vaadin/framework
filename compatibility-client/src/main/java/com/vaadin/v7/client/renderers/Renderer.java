/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.client.renderers;

import com.vaadin.v7.client.widget.escalator.Cell;
import com.vaadin.v7.client.widget.grid.RendererCellReference;
import com.vaadin.v7.client.widgets.Grid;

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
     * Called whenever the {@link Grid} updates a cell.
     * <p>
     * For optimal performance, work done in this method should be kept to a
     * minimum since it will be called continuously while the user is scrolling.
     * It is recommended to set up the cell's DOM structure in
     * {@link ComplexRenderer#init(RendererCellReference)} and only make
     * incremental updates based on cell data in this method.
     *
     * @param cell
     *            The cell. Note that the cell is a flyweight and should not be
     *            stored outside of the method as it will change.
     *
     * @param data
     *            The column data object
     */
    void render(RendererCellReference cell, T data);
}
