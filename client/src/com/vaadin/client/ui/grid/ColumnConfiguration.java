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

/**
 * A representation of the columns in an instance of {@link Escalator}.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see Escalator#getColumnConfiguration()
 */
public interface ColumnConfiguration {

    /**
     * Removes columns at a certain offset.
     * 
     * @param offset
     *            the index of the first column to be removed
     * @param numberOfColumns
     *            the number of rows to remove, starting from the offset
     * @throws IndexOutOfBoundsException
     *             if any integer in the range
     *             <code>[offset..(offset+numberOfColumns)]</code> is not an
     *             existing column index.
     * @throws IllegalArgumentException
     *             if <code>numberOfColumns</code> is less than 1.
     */
    public void removeColumns(int offset, int numberOfColumns)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Adds columns at a certain offset.
     * <p>
     * The new columns will be inserted between the column at the offset, and
     * the column before (an offset of 0 means that the columns are inserted at
     * the beginning). Therefore, the columns at the offset and afterwards will
     * be moved to the right.
     * <p>
     * The contents of the inserted columns will be queried from the respective
     * cell renderers in the header, body and footer.
     * <p>
     * <em>Note:</em> Only the contents of the inserted columns will be
     * rendered. If inserting new columns affects the contents of existing
     * columns, {@link RowContainer#refreshRows(int, int)} needs to be called as
     * appropriate.
     * 
     * @param offset
     *            the index of the column before which new columns are inserted,
     *            or {@link #getColumnCount()} to add new columns at the end
     * @param numberOfColumns
     *            the number of columns to insert after the <code>offset</code>
     * @throws IndexOutOfBoundsException
     *             if <code>offset</code> is not an integer in the range
     *             <code>[0..{@link #getColumnCount()}]</code>
     * @throws IllegalArgumentException
     *             if {@code numberOfColumns} is less than 1.
     */
    public void insertColumns(int offset, int numberOfColumns)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Returns the number of columns in the escalator.
     * 
     * @return the number of columns in the escalator
     */
    public int getColumnCount();
}