/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.google.gwt.user.client.Element;

/**
 * A representation of a single cell.
 * <p>
 * A Cell instance will be provided to the {@link CellRenderer} responsible for
 * rendering the cells in a certain {@link RowContainer}.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see CellRenderer#renderCell(Cell)
 */
public interface Cell {
    /**
     * Returns the index of the row this cell is in.
     * 
     * @return the index of the row this cell is in
     */
    public int getRow();

    /**
     * Returns the index of the column this cell is in.
     * 
     * @return the index of the column this cell is in
     */
    public int getColumn();

    /**
     * Returns the root element for this cell. The {@link CellRenderer} may
     * update the class names of the element, add inline styles and freely
     * modify the contents.
     * <p>
     * Avoid modifying the dimensions or positioning of the cell element.
     * 
     * @return The root element for this cell. Never <code>null</code>.
     */
    public Element getElement();
}