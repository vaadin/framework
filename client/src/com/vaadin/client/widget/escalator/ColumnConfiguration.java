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

import java.util.Map;

/**
 * A representation of the columns in an instance of {@link Escalator}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @see Escalator#getColumnConfiguration()
 */
public interface ColumnConfiguration {

    /**
     * Removes columns at certain indices.
     * <p>
     * If any of the removed columns were frozen, the number of frozen columns
     * will be reduced by the number of the removed columns that were frozen.
     * <p>
     * <em>Note:</em> This method simply removes the given columns, and does not
     * do much of anything else. Especially if you have column spans, you
     * probably need to run {@link #refreshColumns(int, int)} or
     * {@link RowContainer#refreshRows(int, int)}
     * 
     * @param index
     *            the index of the first column to be removed
     * @param numberOfColumns
     *            the number of rows to remove, starting from {@code index}
     * @throws IndexOutOfBoundsException
     *             if the entire range of removed columns is not currently
     *             present in the escalator
     * @throws IllegalArgumentException
     *             if <code>numberOfColumns</code> is less than 1.
     */
    public void removeColumns(int index, int numberOfColumns)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Adds columns at a certain index.
     * <p>
     * The new columns will be inserted between the column at the index, and the
     * column before (an index of 0 means that the columns are inserted at the
     * beginning). Therefore, the columns at the index and afterwards will be
     * moved to the right.
     * <p>
     * The contents of the inserted columns will be queried from the respective
     * cell renderers in the header, body and footer.
     * <p>
     * If there are frozen columns and the first added column is to the left of
     * the last frozen column, the number of frozen columns will be increased by
     * the number of inserted columns.
     * <p>
     * <em>Note:</em> Only the contents of the inserted columns will be
     * rendered. If inserting new columns affects the contents of existing
     * columns (e.g. you have column spans),
     * {@link RowContainer#refreshRows(int, int)} or
     * {@link #refreshColumns(int, int)} needs to be called as appropriate.
     * 
     * @param index
     *            the index of the column before which new columns are inserted,
     *            or {@link #getColumnCount()} to add new columns at the end
     * @param numberOfColumns
     *            the number of columns to insert after the <code>index</code>
     * @throws IndexOutOfBoundsException
     *             if <code>index</code> is not an integer in the range
     *             <code>[0..{@link #getColumnCount()}]</code>
     * @throws IllegalArgumentException
     *             if {@code numberOfColumns} is less than 1.
     */
    public void insertColumns(int index, int numberOfColumns)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Returns the number of columns in the escalator.
     * 
     * @return the number of columns in the escalator
     */
    public int getColumnCount();

    /**
     * Sets the number of leftmost columns that are not affected by horizontal
     * scrolling.
     * 
     * @param count
     *            the number of columns to freeze
     * 
     * @throws IllegalArgumentException
     *             if the column count is &lt; 0 or &gt; the number of columns
     * 
     */
    public void setFrozenColumnCount(int count) throws IllegalArgumentException;

    /**
     * Get the number of leftmost columns that are not affected by horizontal
     * scrolling.
     * 
     * @return the number of frozen columns
     */
    public int getFrozenColumnCount();

    /**
     * Sets (or unsets) an explicit width for a column.
     * 
     * @param index
     *            the index of the column for which to set a width
     * @param px
     *            the number of pixels the indicated column should be, or a
     *            negative number to let the escalator decide
     * @throws IllegalArgumentException
     *             if <code>index</code> is not a valid column index
     */
    public void setColumnWidth(int index, double px)
            throws IllegalArgumentException;

    /**
     * Returns the user-defined width of a column.
     * 
     * @param index
     *            the index of the column for which to retrieve the width
     * @return the column's width in pixels, or a negative number if the width
     *         is implicitly decided by the escalator
     * @throws IllegalArgumentException
     *             if <code>index</code> is not a valid column index
     */
    public double getColumnWidth(int index) throws IllegalArgumentException;

    /**
     * Sets widths for a set of columns.
     * 
     * @param indexWidthMap
     *            a map from column index to its respective width to be set. If
     *            the given width for a column index is negative, the column is
     *            resized-to-fit.
     * @throws IllegalArgumentException
     *             if {@code indexWidthMap} is {@code null}
     * @throws IllegalArgumentException
     *             if any column index in {@code indexWidthMap} is invalid
     * @throws NullPointerException
     *             If any value in the map is <code>null</code>
     */
    public void setColumnWidths(Map<Integer, Double> indexWidthMap)
            throws IllegalArgumentException;

    /**
     * Returns the actual width of a column.
     * 
     * @param index
     *            the index of the column for which to retrieve the width
     * @return the column's actual width in pixels
     * @throws IllegalArgumentException
     *             if <code>index</code> is not a valid column index
     */
    public double getColumnWidthActual(int index)
            throws IllegalArgumentException;

    /**
     * Refreshes a range of rows in the current row containers in each Escalator
     * section.
     * <p>
     * The data for the refreshed columns is queried from the current cell
     * renderer.
     * 
     * @param index
     *            the index of the first row that will be updated
     * @param numberOfRows
     *            the number of rows to update, starting from the index
     * @throws IndexOutOfBoundsException
     *             if any integer number in the range
     *             <code>[index..(index+numberOfColumns)]</code> is not an
     *             existing column index.
     * @throws IllegalArgumentException
     *             if {@code numberOfColumns} is less than 1.
     * @see RowContainer#setEscalatorUpdater(EscalatorUpdater)
     * @see Escalator#getHeader()
     * @see Escalator#getBody()
     * @see Escalator#getFooter()
     */
    public void refreshColumns(int index, int numberOfColumns)
            throws IndexOutOfBoundsException, IllegalArgumentException;
}
