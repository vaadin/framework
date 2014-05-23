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

import com.google.gwt.dom.client.Element;

/**
 * Describes a cell
 * 
 * TODO The description is still very vague since the content and naming of this
 * class is still under debate and the API is not final. Improve the description
 * when API has been finalized.
 * 
 * @author Vaadin Ltd
 */
public class Cell {

    private final int row;

    private final int column;

    private final Element element;

    /**
     * Constructs a new {@link Cell}
     * 
     * @param row
     *            The index of the row
     * @param column
     *            The index of the column
     * @param element
     *            The cell element
     */
    public Cell(int row, int column, Element element) {
        super();
        this.row = row;
        this.column = column;
        this.element = element;
    }

    /**
     * Returns the index of the row the cell resides on
     * 
     * @return the row index
     * 
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the index of the column the cell resides on
     * 
     * @return the column index
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the element of the cell
     * 
     * @return the element
     */
    public Element getElement() {
        return element;
    }

}
