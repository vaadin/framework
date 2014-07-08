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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.server.KeyMapper;
import com.vaadin.shared.ui.grid.ColumnGroupRowState;
import com.vaadin.shared.ui.grid.ColumnGroupState;

/**
 * A column group row represents an auxiliary header or footer row added to the
 * grid. A column group row includes column groups that group columns together.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ColumnGroupRow implements Serializable {

    /**
     * The common state shared between the client and server
     */
    private final ColumnGroupRowState state;

    /**
     * The column groups in this row
     */
    private List<ColumnGroup> groups = new ArrayList<ColumnGroup>();

    /**
     * Grid that the group row belongs to
     */
    private final Grid grid;

    /**
     * The column keys used to identify the column on the client side
     */
    private final KeyMapper<Object> columnKeys;

    /**
     * Constructs a new column group
     * 
     * @param grid
     *            The grid that the column group is associated to
     * @param state
     *            The shared state which contains the data shared between server
     *            and client
     * @param columnKeys
     *            The column key mapper for converting property ids to client
     *            side column identifiers
     */
    ColumnGroupRow(Grid grid, ColumnGroupRowState state,
            KeyMapper<Object> columnKeys) {
        this.grid = grid;
        this.columnKeys = columnKeys;
        this.state = state;
    }

    /**
     * Gets the shared state for the column group row. Used internally to send
     * the group row to the client.
     * 
     * @return The current state of the row
     */
    ColumnGroupRowState getState() {
        return state;
    }

    /**
     * Add a new group to the row by using property ids for the columns.
     * 
     * @param propertyIds
     *            The property ids of the columns that should be included in the
     *            group. A column can only belong in group on a row at a time.
     * @return a column group representing the collection of columns added to
     *         the group
     */
    public ColumnGroup addGroup(Object... propertyIds)
            throws IllegalArgumentException {
        assert propertyIds != null : "propertyIds cannot be null.";

        for (Object propertyId : propertyIds) {
            if (hasColumnBeenGrouped(propertyId)) {
                throw new IllegalArgumentException("Column "
                        + String.valueOf(propertyId)
                        + " already belongs to another group.");
            }
        }

        validateNewGroupProperties(Arrays.asList(propertyIds));

        ColumnGroupState state = new ColumnGroupState();
        for (Object propertyId : propertyIds) {
            assert propertyId != null : "null items in columns array not supported.";
            state.columns.add(columnKeys.key(propertyId));
        }
        this.state.groups.add(state);

        ColumnGroup group = new ColumnGroup(grid, this, state,
                Arrays.asList(propertyIds));
        groups.add(group);

        grid.markAsDirty();
        return group;
    }

    private void validateNewGroupProperties(List<Object> propertyIds)
            throws IllegalArgumentException {

        /*
         * Validate parent grouping
         */
        int rowIndex = grid.getColumnGroupRows().indexOf(this);
        int parentRowIndex = rowIndex - 1;

        // Get the parent row of this row.
        ColumnGroupRow parentRow = null;
        if (parentRowIndex > -1) {
            parentRow = grid.getColumnGroupRows().get(parentRowIndex);
        }

        if (parentRow == null) {
            // A parentless row is always valid and is usually the first row
            // added to the grid
            return;
        }

        for (Object id : propertyIds) {
            if (parentRow.hasColumnBeenGrouped(id)) {
                /*
                 * If a property has been grouped in the parent row then all of
                 * the properties in the parent group also needs to be included
                 * in the child group for the groups to be valid
                 */
                ColumnGroup parentGroup = parentRow.getGroupForProperty(id);
                if (!propertyIds.containsAll(parentGroup.getColumns())) {
                    throw new IllegalArgumentException(
                            "Grouped properties overlaps previous grouping bounderies");
                }
            }
        }
    }

    /**
     * Add a new group to the row by using column instances.
     * 
     * @param columns
     *            the columns that should belong to the group
     * @return a column group representing the collection of columns added to
     *         the group
     */
    public ColumnGroup addGroup(GridColumn... columns)
            throws IllegalArgumentException {
        assert columns != null : "columns cannot be null";

        List<Object> propertyIds = new ArrayList<Object>();
        for (GridColumn column : columns) {
            assert column != null : "null items in columns array not supported.";

            String columnId = column.getState().id;
            Object propertyId = grid.getPropertyIdByColumnId(columnId);
            propertyIds.add(propertyId);
        }
        return addGroup(propertyIds.toArray());
    }

    /**
     * Add a new group to the row by using other already greated groups
     * 
     * @param groups
     *            the subgroups of the group
     * @return a column group representing the collection of columns added to
     *         the group
     * 
     */
    public ColumnGroup addGroup(ColumnGroup... groups)
            throws IllegalArgumentException {
        assert groups != null : "groups cannot be null";

        // Gather all groups columns into one list
        List<Object> propertyIds = new ArrayList<Object>();
        for (ColumnGroup group : groups) {
            propertyIds.addAll(group.getColumns());
        }

        validateNewGroupProperties(propertyIds);

        ColumnGroupState state = new ColumnGroupState();
        ColumnGroup group = new ColumnGroup(grid, this, state, propertyIds);
        this.groups.add(group);

        // Update state
        for (Object propertyId : group.getColumns()) {
            state.columns.add(columnKeys.key(propertyId));
        }
        this.state.groups.add(state);

        grid.markAsDirty();
        return group;
    }

    /**
     * Removes a group from the row. Does not remove the group from subgroups,
     * to remove it from the subgroup invoke removeGroup on the subgroup.
     * 
     * @param group
     *            the group to remove
     */
    public void removeGroup(ColumnGroup group) {
        int index = groups.indexOf(group);
        groups.remove(index);
        state.groups.remove(index);
        grid.markAsDirty();
    }

    /**
     * Get the groups in the row.
     * 
     * @return unmodifiable list of groups in this row
     */
    public List<ColumnGroup> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    /**
     * Checks if a property id has been added to a group in this row.
     * 
     * @param propertyId
     *            the property id to check for
     * @return <code>true</code> if the column is included in a group
     */
    private boolean hasColumnBeenGrouped(Object propertyId) {
        return getGroupForProperty(propertyId) != null;
    }

    private ColumnGroup getGroupForProperty(Object propertyId) {
        for (ColumnGroup group : groups) {
            if (group.isColumnInGroup(propertyId)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Is the header visible for the row.
     * 
     * @return <code>true</code> if header is visible
     */
    public boolean isHeaderVisible() {
        return state.headerVisible;
    }

    /**
     * Sets the header visible for the row.
     * 
     * @param visible
     *            should the header be shown
     */
    public void setHeaderVisible(boolean visible) {
        state.headerVisible = visible;
        grid.markAsDirty();
    }

    /**
     * Is the footer visible for the row.
     * 
     * @return <code>true</code> if footer is visible
     */
    public boolean isFooterVisible() {
        return state.footerVisible;
    }

    /**
     * Sets the footer visible for the row.
     * 
     * @param visible
     *            should the footer be shown
     */
    public void setFooterVisible(boolean visible) {
        state.footerVisible = visible;
        grid.markAsDirty();
    }

}
