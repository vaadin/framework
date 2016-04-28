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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;

/**
 * A representation of the rows in each of the sections (header, body and
 * footer) in an {@link com.vaadin.client.widgets.Escalator}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @see com.vaadin.client.widgets.Escalator#getHeader()
 * @see com.vaadin.client.widgets.Escalator#getBody()
 * @see com.vaadin.client.widgets.Escalator#getFooter()
 * @see SpacerContainer
 */
public interface RowContainer {

    /**
     * The row container for the body section in an
     * {@link com.vaadin.client.widgets.Escalator}.
     * <p>
     * The body section can contain both rows and spacers.
     * 
     * @since 7.5.0
     * @author Vaadin Ltd
     * @see com.vaadin.client.widgets.Escalator#getBody()
     */
    public interface BodyRowContainer extends RowContainer {

        /**
         * Marks a spacer and its height.
         * <p>
         * If a spacer is already registered with the given row index, that
         * spacer will be updated with the given height.
         * <p>
         * <em>Note:</em> The row index for a spacer will change if rows are
         * inserted or removed above the current position. Spacers will also be
         * removed alongside their associated rows
         * 
         * @param rowIndex
         *            the row index for the spacer to modify. The affected
         *            spacer is underneath the given index. Use -1 to insert a
         *            spacer before the first row
         * @param height
         *            the pixel height of the spacer. If {@code height} is
         *            negative, the affected spacer (if exists) will be removed
         * @throws IllegalArgumentException
         *             if {@code rowIndex} is not a valid row index
         * @see #insertRows(int, int)
         * @see #removeRows(int, int)
         */
        void setSpacer(int rowIndex, double height)
                throws IllegalArgumentException;

        /**
         * Sets a new spacer updater.
         * <p>
         * Spacers that are currently visible will be updated, i.e.
         * {@link SpacerUpdater#destroy(Spacer) destroyed} with the previous
         * one, and {@link SpacerUpdater#init(Spacer) initialized} with the new
         * one.
         * 
         * @param spacerUpdater
         *            the new spacer updater
         * @throws IllegalArgumentException
         *             if {@code spacerUpdater} is {@code null}
         */
        void setSpacerUpdater(SpacerUpdater spacerUpdater)
                throws IllegalArgumentException;

        /**
         * Gets the spacer updater currently in use.
         * <p>
         * {@link SpacerUpdater#NULL} is the default.
         * 
         * @return the spacer updater currently in use. Never <code>null</code>
         */
        SpacerUpdater getSpacerUpdater();

        /**
         * {@inheritDoc}
         * <p>
         * Any spacers underneath {@code index} will be offset and "pushed"
         * down. This also modifies the row index they are associated with.
         */
        @Override
        public void insertRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException;

        /**
         * {@inheritDoc}
         * <p>
         * Any spacers underneath {@code index} will be offset and "pulled" up.
         * This also modifies the row index they are associated with. Any
         * spacers in the removed range will also be closed and removed.
         */
        @Override
        public void removeRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException;
    }

    /**
     * An arbitrary pixel height of a row, before any autodetection for the row
     * height has been made.
     * */
    public static final double INITIAL_DEFAULT_ROW_HEIGHT = 20;

    /**
     * Returns the current {@link EscalatorUpdater} used to render cells.
     * 
     * @return the current escalator updater
     */
    public EscalatorUpdater getEscalatorUpdater();

    /**
     * Sets the {@link EscalatorUpdater} to use when displaying data in the
     * escalator.
     * 
     * @param escalatorUpdater
     *            the escalator updater to use to render cells. May not be
     *            <code>null</code>
     * @throws IllegalArgumentException
     *             if {@code cellRenderer} is <code>null</code>
     * @see EscalatorUpdater#NULL
     */
    public void setEscalatorUpdater(EscalatorUpdater escalatorUpdater)
            throws IllegalArgumentException;

    /**
     * Removes rows at a certain index in the current row container.
     * 
     * @param index
     *            the index of the first row to be removed
     * @param numberOfRows
     *            the number of rows to remove, starting from the index
     * @throws IndexOutOfBoundsException
     *             if any integer number in the range
     *             <code>[index..(index+numberOfRows)]</code> is not an existing
     *             row index
     * @throws IllegalArgumentException
     *             if {@code numberOfRows} is less than 1.
     */
    public void removeRows(int index, int numberOfRows)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Adds rows at a certain index in this row container.
     * <p>
     * The new rows will be inserted between the row at the index, and the row
     * before (an index of 0 means that the rows are inserted at the beginning).
     * Therefore, the rows currently at the index and afterwards will be moved
     * downwards.
     * <p>
     * The contents of the inserted rows will subsequently be queried from the
     * escalator updater.
     * <p>
     * <em>Note:</em> Only the contents of the inserted rows will be rendered.
     * If inserting new rows affects the contents of existing rows,
     * {@link #refreshRows(int, int)} needs to be called for those rows
     * separately.
     * 
     * @param index
     *            the index of the row before which new rows are inserted, or
     *            {@link #getRowCount()} to add rows at the end
     * @param numberOfRows
     *            the number of rows to insert after the <code>index</code>
     * @see #setEscalatorUpdater(EscalatorUpdater)
     * @throws IndexOutOfBoundsException
     *             if <code>index</code> is not an integer in the range
     *             <code>[0..{@link #getRowCount()}]</code>
     * @throws IllegalArgumentException
     *             if {@code numberOfRows} is less than 1.
     */
    public void insertRows(int index, int numberOfRows)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Refreshes a range of rows in the current row container.
     * <p>
     * The data for the refreshed rows is queried from the current cell
     * renderer.
     * 
     * @param index
     *            the index of the first row that will be updated
     * @param numberOfRows
     *            the number of rows to update, starting from the index
     * @see #setEscalatorUpdater(EscalatorUpdater)
     * @throws IndexOutOfBoundsException
     *             if any integer number in the range
     *             <code>[index..(index+numberOfColumns)]</code> is not an
     *             existing column index.
     * @throws IllegalArgumentException
     *             if {@code numberOfRows} is less than 1.
     */
    public void refreshRows(int index, int numberOfRows)
            throws IndexOutOfBoundsException, IllegalArgumentException;

    /**
     * Gets the number of rows in the current row container.
     * 
     * @return the number of rows in the current row container
     */
    public int getRowCount();

    /**
     * The default height of the rows in this RowContainer.
     * 
     * @param px
     *            the default height in pixels of the rows in this RowContainer
     * @throws IllegalArgumentException
     *             if <code>px &lt; 1</code>
     * @see #getDefaultRowHeight()
     */
    public void setDefaultRowHeight(double px) throws IllegalArgumentException;

    /**
     * Returns the default height of the rows in this RowContainer.
     * <p>
     * This value will be equal to {@link #INITIAL_DEFAULT_ROW_HEIGHT} if the
     * {@link Escalator} has not yet had a chance to autodetect the row height,
     * or no explicit value has yet given via {@link #setDefaultRowHeight(int)}
     * 
     * @return the default height of the rows in this RowContainer, in pixels
     * @see #setDefaultRowHeight(int)
     */
    public double getDefaultRowHeight();

    /**
     * Returns the cell object which contains information about the cell the
     * element is in.
     * 
     * @param element
     *            The element to get the cell for. If element is not present in
     *            row container then <code>null</code> is returned.
     * 
     * @return the cell of the element, or <code>null</code> if element is not
     *         present in the {@link RowContainer}.
     */
    public Cell getCell(Element element);

    /**
     * Gets the row element with given logical index. For lazy loaded containers
     * such as Escalators BodyRowContainer visibility should be checked before
     * calling this function. See {@link Escalator#getVisibleRowRange()}.
     * 
     * @param index
     *            the logical index of the element to retrieve
     * @return the element at position {@code index}
     * @throws IndexOutOfBoundsException
     *             if {@code index} is not valid within container
     * @throws IllegalStateException
     *             if {@code index} is currently not available in the DOM
     */
    public TableRowElement getRowElement(int index)
            throws IndexOutOfBoundsException, IllegalStateException;

    /**
     * Returns the root element of RowContainer
     * 
     * @return RowContainer root element
     */
    public TableSectionElement getElement();
}
