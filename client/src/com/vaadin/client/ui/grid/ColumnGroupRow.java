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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A column group row represents an additional header or footer row in a
 * {@link Grid}. A column group row contains {@link ColumnGroup ColumnGroups}.
 * 
 * @param <T>
 *            Row type
 * @since
 * @author Vaadin Ltd
 */
public class ColumnGroupRow<T> {

    /**
     * The column groups in this row
     */
    private List<ColumnGroup<T>> groups = new ArrayList<ColumnGroup<T>>();

    /**
     * The grid associated with the column row
     */
    private final Grid<T> grid;

    /**
     * Is the header shown
     */
    private boolean headerVisible = true;

    /**
     * Is the footer shown
     */
    private boolean footerVisible = false;

    /**
     * Constructs a new column group row
     * 
     * @param grid
     *            Grid associated with this column
     * 
     */
    ColumnGroupRow(Grid<T> grid) {
        this.grid = grid;
    }

    /**
     * Adds a new group of columns to the column group row.
     * 
     * @param columns
     *            The columns that should be added to the group
     * @return the added columns as a column group
     * @throws IllegalArgumentException
     *             if any of {@code columns} already belongs to a group, or if
     *             {@code columns} or any of its elements are {@code null}
     */
    public ColumnGroup<T> addGroup(GridColumn<?, T>... columns)
            throws IllegalArgumentException {

        if (columns == null) {
            throw new IllegalArgumentException("columns may not be null");
        }

        for (GridColumn<?, T> column : columns) {
            if (column == null) {
                throw new IllegalArgumentException(
                        "none of the given columns may be null");
            }

            if (isColumnGrouped(column)) {
                throw new IllegalArgumentException("Column "
                        + String.valueOf(column.getHeaderCaption())
                        + " already belongs to another group.");
            }
        }

        validateNewGroupProperties(Arrays.asList(columns));

        ColumnGroup<T> group = new ColumnGroup<T>(grid, Arrays.asList(columns));
        groups.add(group);
        grid.refreshHeader();
        grid.refreshFooter();
        return group;
    }

    private void validateNewGroupProperties(Collection<GridColumn<?, T>> columns) {

        int rowIndex = grid.getColumnGroupRows().indexOf(this);
        int parentRowIndex = rowIndex - 1;

        // Get the parent row of this row.
        ColumnGroupRow<T> parentRow = null;
        if (parentRowIndex > -1) {
            parentRow = grid.getColumnGroupRows().get(parentRowIndex);
        }

        if (parentRow == null) {
            // A parentless row is always valid and is usually the first row
            // added to the grid
            return;
        }

        for (GridColumn<?, T> column : columns) {
            if (parentRow.hasColumnBeenGrouped(column)) {
                /*
                 * If a property has been grouped in the parent row then all of
                 * the properties in the parent group also needs to be included
                 * in the child group for the groups to be valid
                 */
                ColumnGroup parentGroup = parentRow.getGroupForColumn(column);
                if (!columns.containsAll(parentGroup.getColumns())) {
                    throw new IllegalArgumentException(
                            "Grouped properties overlaps previous grouping bounderies");
                }
            }
        }
    }

    private boolean hasColumnBeenGrouped(GridColumn<?, T> column) {
        return getGroupForColumn(column) != null;
    }

    private ColumnGroup<T> getGroupForColumn(GridColumn<?, T> column) {
        for (ColumnGroup<T> group : groups) {
            if (group.getColumns().contains(column)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Add a new group to the row by using other already created groups.
     * 
     * @param groups
     *            the column groups to be further grouped together
     * @return the added column groups as a new single group
     * @throws IllegalArgumentException
     *             if any column group already belongs to another group, or if
     *             {@code groups} or any of its elements are null
     */
    public ColumnGroup<T> addGroup(ColumnGroup<T>... groups)
            throws IllegalArgumentException {

        if (groups == null) {
            throw new IllegalArgumentException("groups may not be null");
        }

        Set<GridColumn<?, T>> columns = new HashSet<GridColumn<?, T>>();
        for (ColumnGroup<T> group : groups) {
            if (group == null) {
                throw new IllegalArgumentException(
                        "none of the given group may be null");
            }

            columns.addAll(group.getColumns());
        }

        validateNewGroupProperties(columns);

        ColumnGroup<T> group = new ColumnGroup<T>(grid, columns);
        this.groups.add(group);
        grid.refreshHeader();
        grid.refreshFooter();
        return group;
    }

    /**
     * Removes a group from the row.
     * <p>
     * <em>Note:</em> this removes only a group in the immediate hierarchy
     * level, and does not search recursively.
     * 
     * @param group
     *            The group to remove
     * @return {@code true} iff the group was successfully removed
     */
    public boolean removeGroup(ColumnGroup<T> group) {
        boolean removed = groups.remove(group);
        if (removed) {
            grid.refreshHeader();
            grid.refreshFooter();
        }
        return removed;
    }

    /**
     * Gets the groups in this row.
     * 
     * @return an unmodifiable list of groups in this row
     */
    public List<ColumnGroup<T>> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    /**
     * Checks whether the header is visible for the row group or not.
     * 
     * @return <code>true</code> iff the header is visible
     */
    public boolean isHeaderVisible() {
        return headerVisible;
    }

    /**
     * Sets the visibility of the row group's header.
     * 
     * @param visible
     *            {@code true} iff the header should be shown
     */
    public void setHeaderVisible(boolean visible) {
        headerVisible = visible;
        grid.refreshHeader();
    }

    /**
     * Checks whether the footer is visible for the row or not.
     * 
     * @return <code>true</code> iff footer is visible
     */
    public boolean isFooterVisible() {
        return footerVisible;
    }

    /**
     * Sets the visibility of the row group's footer.
     * 
     * @param visible
     *            {@code true} iff the footer should be shown
     */
    public void setFooterVisible(boolean visible) {
        footerVisible = visible;
        grid.refreshFooter();
    }

    /**
     * Iterates through all the column groups and checks if the columns already
     * has been added to a group.
     */
    private boolean isColumnGrouped(GridColumn<?, T> column) {
        for (ColumnGroup<T> group : groups) {
            if (group.getColumns().contains(column)) {
                return true;
            }
        }
        return false;
    }
}
