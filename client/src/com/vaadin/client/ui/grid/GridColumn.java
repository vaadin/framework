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

/**
 * Represents a column in the {@link Grid}.
 * 
 * @param <C>
 *            The column type
 * 
 * @param <T>
 *            The row type
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract class GridColumn<C, T> extends Grid.AbstractGridColumn<C, T> {

    /*
     * This class is a convenience class so you do not have to reference
     * Grid.AbstractGridColumn in your production code. The real implementation
     * should be in the abstract class.
     */

    /**
     * Constructs a new column.
     */
    public GridColumn() {
        super();
    }

    /**
     * Constructs a new column with a custom renderer.
     * 
     * @param renderer
     *            The renderer to use for rendering the cells
     */
    public GridColumn(Renderer<C> renderer) {
        super(renderer);
    }
}
