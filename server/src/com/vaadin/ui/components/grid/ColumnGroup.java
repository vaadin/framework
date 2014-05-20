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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.shared.ui.grid.ColumnGroupState;

/**
 * Column groups are used to group columns together for adding common auxiliary
 * headers and footers. Columns groups are added to {@link ColumnGroupRow}'s.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ColumnGroup implements Serializable {

    /**
     * List of property ids belonging to this group
     */
    private List<Object> columns;

    /**
     * The grid the column group is associated with
     */
    private final Grid grid;

    /**
     * The column group row the column group is attached to
     */
    private final ColumnGroupRow row;

    /**
     * The common state between the server and the client
     */
    private final ColumnGroupState state;

    /**
     * Constructs a new column group
     * 
     * @param grid
     *            the grid the column group is associated with
     * @param state
     *            the state representing the data of the grid. Sent to the
     *            client
     * @param propertyIds
     *            the property ids of the columns that belongs to the group
     * @param groups
     *            the sub groups who should be included in this group
     * 
     */
    ColumnGroup(Grid grid, ColumnGroupRow row, ColumnGroupState state,
            List<Object> propertyIds) {
        if (propertyIds == null) {
            throw new IllegalArgumentException(
                    "propertyIds cannot be null. Use empty list instead.");
        }

        this.state = state;
        this.row = row;
        columns = Collections.unmodifiableList(new ArrayList<Object>(
                propertyIds));
        this.grid = grid;
    }

    /**
     * Sets the text displayed in the header of the column group.
     * 
     * @param header
     *            the text displayed in the header of the column
     */
    public void setHeaderCaption(String header) {
        checkGroupIsAttached();
        state.header = header;
        grid.markAsDirty();
    }

    /**
     * Sets the text displayed in the header of the column group.
     * 
     * @return the text displayed in the header of the column
     */
    public String getHeaderCaption() {
        checkGroupIsAttached();
        return state.header;
    }

    /**
     * Sets the text displayed in the footer of the column group.
     * 
     * @param footer
     *            the text displayed in the footer of the column
     */
    public void setFooterCaption(String footer) {
        checkGroupIsAttached();
        state.footer = footer;
        grid.markAsDirty();
    }

    /**
     * The text displayed in the footer of the column group.
     * 
     * @return the text displayed in the footer of the column
     */
    public String getFooterCaption() {
        checkGroupIsAttached();
        return state.footer;
    }

    /**
     * Is a property id in this group or in some sub group of this group.
     * 
     * @param propertyId
     *            the property id to check for
     * @return <code>true</code> if the property id is included in this group.
     */
    public boolean isColumnInGroup(Object propertyId) {
        if (columns.contains(propertyId)) {
            return true;
        }
        return false;
    }

    /**
     * Returns a list of property ids where all also the child groups property
     * ids are included.
     * 
     * @return a unmodifiable list with all the columns in the group. Includes
     *         any subgroup columns as well.
     */
    public List<Object> getColumns() {
        return columns;
    }

    /**
     * Checks if column group is attached to a row and throws an
     * {@link IllegalStateException} if it is not.
     * 
     * @throws IllegalStateException
     *             if the column is no longer attached to any grid
     */
    protected void checkGroupIsAttached() throws IllegalStateException {
        if (!row.getState().groups.contains(state)) {
            throw new IllegalStateException(
                    "Column Group has been removed from the row.");
        }
    }
}
