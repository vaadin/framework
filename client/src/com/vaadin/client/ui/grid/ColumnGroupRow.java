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
 * A column group row represents an auxiliary header or footer row added to the
 * grid. A column group row includes column groups that group columns together.
 * 
 * @param <T>
 *            Row type
 * @since 7.4
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
     * Add a new group to the row by using column instances.
     * 
     * @param columns
     *            The columns that should belong to the group
     * @return a column group representing the collection of columns added to
     *         the group.
     */
    public ColumnGroup<T> addGroup(GridColumn<?, T>... columns)
            throws IllegalArgumentException {

        for (GridColumn<?, T> column : columns) {
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
     * Add a new group to the row by using other already greated groups
     * 
     * @param groups
     *            The subgroups of the group.
     * @return a column group representing the collection of columns added to
     *         the group.
     * 
     */
    public ColumnGroup<T> addGroup(ColumnGroup<T>... groups)
            throws IllegalArgumentException {
        assert groups != null : "groups cannot be null";

        Set<GridColumn<?, T>> columns = new HashSet<GridColumn<?, T>>();
        for (ColumnGroup<T> group : groups) {
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
     * 
     * @param group
     *            The group to remove
     */
    public void removeGroup(ColumnGroup<T> group) {
        groups.remove(group);
        grid.refreshHeader();
        grid.refreshFooter();
    }

    /**
     * Get the groups in the row
     * 
     * @return unmodifiable list of groups in this row
     */
    public List<ColumnGroup<T>> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    /**
     * Is the header visible for the row.
     * 
     * @return <code>true</code> if header is visible
     */
    public boolean isHeaderVisible() {
        return headerVisible;
    }

    /**
     * Sets the header visible for the row.
     * 
     * @param visible
     *            should the header be shown
     */
    public void setHeaderVisible(boolean visible) {
        headerVisible = visible;
        grid.refreshHeader();
    }

    /**
     * Is the footer visible for the row.
     * 
     * @return <code>true</code> if footer is visible
     */
    public boolean isFooterVisible() {
        return footerVisible;
    }

    /**
     * Sets the footer visible for the row.
     * 
     * @param visible
     *            should the footer be shown
     */
    public void setFooterVisible(boolean visible) {
        footerVisible = visible;
        grid.refreshFooter();
    }

    /**
     * Iterates all the column groups and checks if the columns alread has been
     * added to a group.
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
