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
package com.vaadin.client.widget.escalator;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.vaadin.client.widget.escalator.FlyweightRow.CellIterator;
import com.vaadin.client.widgets.Escalator;

/**
 * A {@link FlyweightCell} represents a cell in the {@link Grid} or
 * {@link Escalator} at a certain point in time.
 * 
 * <p>
 * Since the {@link FlyweightCell} follows the <code>Flyweight</code>-pattern
 * any instance of this object is subject to change without the user knowing it
 * and so should not be stored anywhere outside of the method providing these
 * instances.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class FlyweightCell {
    public static final String COLSPAN_ATTR = "colSpan";

    private final int column;
    private final FlyweightRow row;

    private TableCellElement element = null;
    private CellIterator currentIterator = null;

    public FlyweightCell(final FlyweightRow row, final int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row index of the cell
     */
    public int getRow() {
        assertSetup();
        return row.getRow();
    }

    /**
     * Returns the column index of the cell
     */
    public int getColumn() {
        assertSetup();
        return column;
    }

    /**
     * Returns the element of the cell. Can be either a <code>TD</code> element
     * or a <code>TH</code> element.
     */
    public TableCellElement getElement() {
        assertSetup();
        return element;
    }

    /**
     * Return the colspan attribute of the element of the cell.
     */
    public int getColSpan() {
        assertSetup();
        return element.getPropertyInt(COLSPAN_ATTR);
    }

    /**
     * Sets the DOM element for this FlyweightCell, either a <code>TD</code> or
     * a <code>TH</code>. It is the caller's responsibility to actually insert
     * the given element to the document when needed.
     * 
     * @param element
     *            the element corresponding to this cell, cannot be null
     */
    public void setElement(TableCellElement element) {
        assert element != null;
        assertSetup();
        this.element = element;
    }

    void setup(final CellIterator iterator) {
        currentIterator = iterator;

        if (iterator.areCellsAttached()) {
            final TableCellElement e = row.getElement().getCells()
                    .getItem(column);

            assert e != null : "Cell " + column + " for logical row "
                    + row.getRow() + " doesn't exist in the DOM!";

            e.setPropertyInt(COLSPAN_ATTR, 1);
            if (row.getColumnWidth(column) >= 0) {
                e.getStyle().setWidth(row.getColumnWidth(column), Unit.PX);
            }
            e.getStyle().clearDisplay();
            setElement(e);
        }
    }

    /**
     * Tear down the state of the Cell.
     * <p>
     * This is an internal check method, to prevent retrieving uninitialized
     * data by calling {@link #getRow()}, {@link #getColumn()} or
     * {@link #getElement()} at an improper time.
     * <p>
     * This should only be used with asserts ("
     * <code>assert flyweightCell.teardown()</code> ") so that the code is never
     * run when asserts aren't enabled.
     * 
     * @return always <code>true</code>
     * @see FlyweightRow#teardown()
     */
    boolean teardown() {
        currentIterator = null;
        element = null;
        return true;
    }

    /**
     * Asserts that the flyweight cell has properly been set up before trying to
     * access any of its data.
     */
    private void assertSetup() {
        assert currentIterator != null : "FlyweightCell was not properly "
                + "initialized. This is either a bug in Grid/Escalator "
                + "or a Cell reference has been stored and reused "
                + "inappropriately.";
    }

    public void setColSpan(final int numberOfCells) {
        if (numberOfCells < 1) {
            throw new IllegalArgumentException(
                    "Number of cells should be more than 0");
        }

        /*-
         * This will default to 1 if unset, as per DOM specifications:
         * http://www.w3.org/TR/html5/tabular-data.html#attributes-common-to-td-and-th-elements
         */
        final int prevColSpan = getElement().getPropertyInt(COLSPAN_ATTR);
        if (numberOfCells == 1 && prevColSpan == 1) {
            return;
        }

        getElement().setPropertyInt(COLSPAN_ATTR, numberOfCells);
        adjustCellWidthForSpan(numberOfCells);
        hideOrRevealAdjacentCellElements(numberOfCells, prevColSpan);
        currentIterator.setSkipNext(numberOfCells - 1);
    }

    private void adjustCellWidthForSpan(final int numberOfCells) {
        final int cellsToTheRight = currentIterator.rawPeekNext(
                numberOfCells - 1).size();

        final double selfWidth = row.getColumnWidth(column);
        double widthsOfColumnsToTheRight = 0;
        for (int i = 0; i < cellsToTheRight; i++) {
            widthsOfColumnsToTheRight += row.getColumnWidth(column + i + 1);
        }
        getElement().getStyle().setWidth(selfWidth + widthsOfColumnsToTheRight,
                Unit.PX);
    }

    private void hideOrRevealAdjacentCellElements(final int numberOfCells,
            final int prevColSpan) {
        final int affectedCellsNumber = Math.max(prevColSpan, numberOfCells);
        final List<FlyweightCell> affectedCells = currentIterator
                .rawPeekNext(affectedCellsNumber - 1);
        if (prevColSpan < numberOfCells) {
            for (int i = 0; i < affectedCells.size(); i++) {
                affectedCells.get(prevColSpan + i - 1).getElement().getStyle()
                        .setDisplay(Display.NONE);
            }
        } else if (prevColSpan > numberOfCells) {
            for (int i = 0; i < affectedCells.size(); i++) {
                affectedCells.get(numberOfCells + i - 1).getElement()
                        .getStyle().clearDisplay();
            }
        }
    }
}
