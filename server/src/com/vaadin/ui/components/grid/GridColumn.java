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

package com.vaadin.ui.components.grid;

import java.io.Serializable;

import com.vaadin.shared.ui.grid.GridColumnState;

/**
 * A column in the grid. Can be obtained by calling
 * {@link Grid#getColumn(Object propertyId)}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridColumn implements Serializable {

    /**
     * The state of the column shared to the client
     */
    private final GridColumnState state;

    /**
     * The grid this column is associated with
     */
    private final Grid grid;

    /**
     * Internally used constructor.
     * 
     * @param grid
     *            The grid this column belongs to. Should not be null.
     * @param state
     *            the shared state of this column
     */
    GridColumn(Grid grid, GridColumnState state) {
        this.grid = grid;
        this.state = state;
    }

    /**
     * Returns the serializable state of this column that is sent to the client
     * side connector.
     * 
     * @return the internal state of the column
     */
    GridColumnState getState() {
        return state;
    }

    /**
     * Returns the caption of the header. By default the header caption is the
     * property id of the column.
     * 
     * @return the text in the header
     * 
     * @throws IllegalStateException
     *             if the column no longer is attached to the grid
     */
    public String getHeaderCaption() throws IllegalStateException {
        checkColumnIsAttached();
        return state.header;
    }

    /**
     * Sets the caption of the header.
     * 
     * @param caption
     *            the text to show in the caption
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public void setHeaderCaption(String caption) throws IllegalStateException {
        checkColumnIsAttached();
        state.header = caption;
        grid.markAsDirty();
    }

    /**
     * Returns the caption of the footer. By default the captions are
     * <code>null</code>.
     * 
     * @return the text in the footer
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public String getFooterCaption() throws IllegalStateException {
        checkColumnIsAttached();
        return state.footer;
    }

    /**
     * Sets the caption of the footer.
     * 
     * @param caption
     *            the text to show in the caption
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public void setFooterCaption(String caption) throws IllegalStateException {
        checkColumnIsAttached();
        state.footer = caption;
        grid.markAsDirty();
    }

    /**
     * Returns the width (in pixels). By default a column is 100px wide.
     * 
     * @return the width in pixels of the column
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public int getWidth() throws IllegalStateException {
        checkColumnIsAttached();
        return state.width;
    }

    /**
     * Sets the width (in pixels).
     * 
     * @param pixelWidth
     *            the new pixel width of the column
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     * @throws IllegalArgumentException
     *             thrown if pixel width is less than zero
     */
    public void setWidth(int pixelWidth) throws IllegalStateException,
            IllegalArgumentException {
        checkColumnIsAttached();
        if (pixelWidth < 0) {
            throw new IllegalArgumentException(
                    "Pixel width should be greated than 0");
        }
        state.width = pixelWidth;
        grid.markAsDirty();
    }

    /**
     * Marks the column width as undefined meaning that the grid is free to
     * resize the column based on the cell contents and available space in the
     * grid.
     */
    public void setWidthUndefined() {
        checkColumnIsAttached();
        state.width = -1;
        grid.markAsDirty();
    }

    /**
     * Is this column visible in the grid. By default all columns are visible.
     * 
     * @return <code>true</code> if the column is visible
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public boolean isVisible() throws IllegalStateException {
        checkColumnIsAttached();
        return state.visible;
    }

    /**
     * Set the visibility of this column
     * 
     * @param visible
     *            is the column visible
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    public void setVisible(boolean visible) throws IllegalStateException {
        checkColumnIsAttached();
        state.visible = visible;
        grid.markAsDirty();
    }

    /**
     * Checks if column is attached and throws an {@link IllegalStateException}
     * if it is not
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    protected void checkColumnIsAttached() throws IllegalStateException {
        if (grid.getColumnByColumnId(state.id) == null) {
            throw new IllegalStateException("Column no longer exists.");
        }
    }

    /**
     * Sets this column as the last frozen column in its grid.
     * 
     * @throws IllegalArgumentException
     *             if the column is no longer attached to any grid
     * @see Grid#setLastFrozenColumn(GridColumn)
     */
    public void setLastFrozenColumn() {
        checkColumnIsAttached();
        grid.setLastFrozenColumn(this);
    }
}
