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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.TableRowElement;

/**
 * An internal implementation of the {@link Row} interface.
 * <p>
 * There is only one instance per Escalator. This is designed to be re-used when
 * rendering rows.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @see Escalator.AbstractRowContainer#refreshRow(Node, int)
 */
public class FlyweightRow implements Row {

    static class CellIterator implements Iterator<FlyweightCell> {
        /** A defensive copy of the cells in the current row. */
        private final ArrayList<FlyweightCell> cells;
        private final boolean cellsAttached;
        private int cursor = 0;
        private int skipNext = 0;

        /**
         * Creates a new iterator of attached flyweight cells. A cell is
         * attached if it has a corresponding {@link FlyweightCell#getElement()
         * DOM element} attached to the row element.
         * 
         * @param cells
         *            the collection of cells to iterate
         */
        public static CellIterator attached(
                final Collection<FlyweightCell> cells) {
            return new CellIterator(cells, true);
        }

        /**
         * Creates a new iterator of unattached flyweight cells. A cell is
         * unattached if it does not have a corresponding
         * {@link FlyweightCell#getElement() DOM element} attached to the row
         * element.
         * 
         * @param cells
         *            the collection of cells to iterate
         */
        public static CellIterator unattached(
                final Collection<FlyweightCell> cells) {
            return new CellIterator(cells, false);
        }

        private CellIterator(final Collection<FlyweightCell> cells,
                final boolean attached) {
            this.cells = new ArrayList<FlyweightCell>(cells);
            cellsAttached = attached;
        }

        @Override
        public boolean hasNext() {
            return cursor + skipNext < cells.size();
        }

        @Override
        public FlyweightCell next() {
            // if we needed to skip some cells since the last invocation.
            for (int i = 0; i < skipNext; i++) {
                cells.remove(cursor);
            }
            skipNext = 0;

            final FlyweightCell cell = cells.get(cursor++);
            cell.setup(this);
            return cell;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "Cannot remove cells via iterator");
        }

        /**
         * Sets the number of cells to skip when {@link #next()} is called the
         * next time. Cell hiding is also handled eagerly in this method.
         * 
         * @param colspan
         *            the number of cells to skip on next invocation of
         *            {@link #next()}
         */
        public void setSkipNext(final int colspan) {
            assert colspan > 0 : "Number of cells didn't make sense: "
                    + colspan;
            skipNext = colspan;
        }

        /**
         * Gets the next <code>n</code> cells in the iterator, ignoring any
         * possibly spanned cells.
         * 
         * @param n
         *            the number of next cells to retrieve
         * @return A list of next <code>n</code> cells, or less if there aren't
         *         enough cells to retrieve
         */
        public List<FlyweightCell> rawPeekNext(final int n) {
            final int from = Math.min(cursor, cells.size());
            final int to = Math.min(cursor + n, cells.size());
            List<FlyweightCell> nextCells = cells.subList(from, to);
            for (FlyweightCell cell : nextCells) {
                cell.setup(this);
            }
            return nextCells;
        }

        public boolean areCellsAttached() {
            return cellsAttached;
        }
    }

    private static final int BLANK = Integer.MIN_VALUE;

    private int row;
    private TableRowElement element;
    private double[] columnWidths = null;
    private final List<FlyweightCell> cells = new ArrayList<FlyweightCell>();

    public void setup(final TableRowElement e, final int row,
            double[] columnWidths) {
        element = e;
        this.row = row;
        this.columnWidths = columnWidths;
    }

    /**
     * Tear down the state of the Row.
     * <p>
     * This is an internal check method, to prevent retrieving uninitialized
     * data by calling {@link #getRow()}, {@link #getElement()} or
     * {@link #getCells()} at an improper time.
     * <p>
     * This should only be used with asserts ("
     * <code>assert flyweightRow.teardown()</code> ") so that the code is never
     * run when asserts aren't enabled.
     * 
     * @return always <code>true</code>
     */
    public boolean teardown() {
        element = null;
        row = BLANK;
        columnWidths = null;
        for (final FlyweightCell cell : cells) {
            assert cell.teardown();
        }
        return true;
    }

    @Override
    public int getRow() {
        assertSetup();
        return row;
    }

    @Override
    public TableRowElement getElement() {
        assertSetup();
        return element;
    }

    public void addCells(final int index, final int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            final int col = index + i;
            cells.add(col, new FlyweightCell(this, col));
        }
        updateRestOfCells(index + numberOfColumns);
    }

    public void removeCells(final int index, final int numberOfColumns) {
        cells.subList(index, index + numberOfColumns).clear();
        updateRestOfCells(index);
    }

    private void updateRestOfCells(final int startPos) {
        // update the column number for the cells to the right
        for (int col = startPos; col < cells.size(); col++) {
            cells.set(col, new FlyweightCell(this, col));
        }
    }

    /**
     * Returns flyweight cells for the client code to render. The cells get
     * their associated {@link FlyweightCell#getElement() elements} from the row
     * element.
     * <p>
     * Precondition: each cell has a corresponding element in the row
     * 
     * @return an iterable of flyweight cells
     * 
     * @see #setup(Element, int, int[])
     * @see #teardown()
     */
    public Iterable<FlyweightCell> getCells() {
        return getCells(0, cells.size());
    }

    /**
     * Returns a subrange of flyweight cells for the client code to render. The
     * cells get their associated {@link FlyweightCell#getElement() elements}
     * from the row element.
     * <p>
     * Precondition: each cell has a corresponding element in the row
     * 
     * @param offset
     *            the index of the first cell to return
     * @param numberOfCells
     *            the number of cells to return
     * @return an iterable of flyweight cells
     */
    public Iterable<FlyweightCell> getCells(final int offset,
            final int numberOfCells) {
        assertSetup();
        assert offset >= 0 && offset + numberOfCells <= cells.size() : "Invalid range of cells";
        return new Iterable<FlyweightCell>() {
            @Override
            public Iterator<FlyweightCell> iterator() {
                return CellIterator.attached(cells.subList(offset, offset
                        + numberOfCells));
            }
        };
    }

    /**
     * Returns a subrange of unattached flyweight cells. Unattached cells do not
     * have {@link FlyweightCell#getElement() elements} associated. Note that
     * FlyweightRow does not keep track of whether cells in actuality have
     * corresponding DOM elements or not; it is the caller's responsibility to
     * invoke this method with correct parameters.
     * <p>
     * Precondition: the range [offset, offset + numberOfCells) must be valid
     * 
     * @param offset
     *            the index of the first cell to return
     * @param numberOfCells
     *            the number of cells to return
     * @return an iterable of flyweight cells
     */
    public Iterable<FlyweightCell> getUnattachedCells(final int offset,
            final int numberOfCells) {
        assertSetup();
        assert offset >= 0 && offset + numberOfCells <= cells.size() : "Invalid range of cells";
        return new Iterable<FlyweightCell>() {
            @Override
            public Iterator<FlyweightCell> iterator() {
                return CellIterator.unattached(cells.subList(offset, offset
                        + numberOfCells));
            }
        };
    }

    /**
     * Asserts that the flyweight row has properly been set up before trying to
     * access any of its data.
     */
    private void assertSetup() {
        assert element != null && row != BLANK && columnWidths != null : "Flyweight row was not "
                + "properly initialized. Make sure the setup-method is "
                + "called before retrieving data. This is either a bug "
                + "in Escalator, or the instance of the flyweight row "
                + "has been stored and accessed.";
    }

    double getColumnWidth(int column) {
        assertSetup();
        return columnWidths[column];
    }
}
