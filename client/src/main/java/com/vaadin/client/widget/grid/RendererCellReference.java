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
package com.vaadin.client.widget.grid;

import com.google.gwt.dom.client.TableCellElement;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widgets.Grid;

/**
 * A data class which contains information which identifies a cell being
 * rendered in a {@link Grid}.
 * <p>
 * Since this class follows the <code>Flyweight</code>-pattern any instance of
 * this object is subject to change without the user knowing it and so should
 * not be stored anywhere outside of the method providing these instances.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class RendererCellReference extends CellReference<Object> {

    /**
     * Creates a new renderer cell reference bound to a row reference.
     * 
     * @param rowReference
     *            the row reference to bind to
     */
    public RendererCellReference(RowReference<Object> rowReference) {
        super(rowReference);
    }

    private FlyweightCell cell;

    /**
     * Sets the identifying information for this cell.
     * 
     * @param cell
     *            the flyweight cell to reference
     * @param columnIndex
     *            the index of the column in the grid, including hidden cells
     * @param column
     *            the column to reference
     */
    public void set(FlyweightCell cell, int columnIndex,
            Grid.Column<?, ?> column) {
        this.cell = cell;
        super.set(cell.getColumn(), columnIndex,
                (Grid.Column<?, Object>) column);
    }

    /**
     * Returns the element of the cell. Can be either a <code>TD</code> element
     * or a <code>TH</code> element.
     * 
     * @return the element of the cell
     */
    @Override
    public TableCellElement getElement() {
        return cell.getElement();
    }

    /**
     * Sets the colspan attribute of the element of this cell.
     * 
     * @param numberOfCells
     *            the number of columns that the cell should span
     */
    public void setColSpan(int numberOfCells) {
        cell.setColSpan(numberOfCells);
    }

    /**
     * Gets the colspan attribute of the element of this cell.
     * 
     * @return the number of columns that the cell should span
     */
    public int getColSpan() {
        return cell.getColSpan();
    }
}
