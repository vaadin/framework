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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.ColumnGroupRowState;
import com.vaadin.shared.ui.grid.ColumnGroupState;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridState;

/**
 * Connects the client side {@link Grid} widget with the server side
 * {@link com.vaadin.ui.components.grid.Grid} component.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.components.grid.Grid.class)
public class GridConnector extends AbstractComponentConnector {

    /**
     * Custom implementation of the custom grid column using a String[] to
     * represent the cell value
     */
    private class CustomGridColumn extends GridColumn<String[]> {

        @Override
        public String getValue(String[] obj) {
            // FIXME Should return something from the data source.
            return null;
        }
    }

    /**
     * Maps a generated column id to a grid column instance
     */
    private Map<String, CustomGridColumn> columnIdToColumn = new HashMap<String, CustomGridColumn>();

    @Override
    protected Grid<String[]> createWidget() {
        // FIXME Shouldn't be needed after #12873 has been fixed.
        return new Grid<String[]>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Grid<String[]> getWidget() {
        return (Grid<String[]>) super.getWidget();
    }

    @Override
    public GridState getState() {
        return (GridState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Column updates
        if (stateChangeEvent.hasPropertyChanged("columns")) {

            int totalColumns = getState().columns.size();
            int currentColumns = getWidget().getColumnCount();

            // Remove old columns
            purgeRemovedColumns();

            // Add new columns
            for (int columnIndex = currentColumns; columnIndex < totalColumns; columnIndex++) {
                addColumnFromStateChangeEvent(columnIndex);
            }

            // Update old columns
            for (int columnIndex = 0; columnIndex < currentColumns; columnIndex++) {
                // FIXME Currently updating all column header / footers when a
                // change in made in one column. When the framework supports
                // quering a specific item in a list then it should do so here.
                updateColumnFromStateChangeEvent(columnIndex);
            }
        }

        // Header
        if (stateChangeEvent.hasPropertyChanged("columnHeadersVisible")) {
            getWidget()
                    .setColumnHeadersVisible(getState().columnHeadersVisible);
        }

        // Footer
        if (stateChangeEvent.hasPropertyChanged("columnFootersVisible")) {
            getWidget()
                    .setColumnFootersVisible(getState().columnFootersVisible);
        }

        // Column row groups
        if (stateChangeEvent.hasPropertyChanged("columnGroupRows")) {
            updateColumnGroupsFromStateChangeEvent();
        }
    }

    /**
     * Updates a column from a state change event.
     * 
     * @param columnIndex
     *            The index of the column to update
     */
    private void updateColumnFromStateChangeEvent(int columnIndex) {
        GridColumn<String[]> column = getWidget().getColumn(columnIndex);
        GridColumnState columnState = getState().columns.get(columnIndex);
        updateColumnFromState(column, columnState);
    }

    /**
     * Adds a new column to the grid widget from a state change event
     * 
     * @param columnIndex
     *            The index of the column, according to how it
     */
    private void addColumnFromStateChangeEvent(int columnIndex) {
        GridColumnState state = getState().columns.get(columnIndex);
        CustomGridColumn column = new CustomGridColumn();
        updateColumnFromState(column, state);

        columnIdToColumn.put(state.id, column);

        getWidget().addColumn(column, columnIndex);
    }

    /**
     * Updates the column values from a state
     * 
     * @param column
     *            The column to update
     * @param state
     *            The state to get the data from
     */
    private static void updateColumnFromState(GridColumn<String[]> column,
            GridColumnState state) {
        column.setVisible(state.visible);
        column.setHeaderCaption(state.header);
        column.setFooterCaption(state.footer);
    }

    /**
     * Removes any orphan columns that has been removed from the state from the
     * grid
     */
    private void purgeRemovedColumns() {

        // Get columns still registered in the state
        Set<String> columnsInState = new HashSet<String>();
        for (GridColumnState columnState : getState().columns) {
            columnsInState.add(columnState.id);
        }

        // Remove column no longer in state
        Iterator<String> columnIdIterator = columnIdToColumn.keySet()
                .iterator();
        while (columnIdIterator.hasNext()) {
            String id = columnIdIterator.next();
            if (!columnsInState.contains(id)) {
                CustomGridColumn column = columnIdToColumn.get(id);
                columnIdIterator.remove();
                getWidget().removeColumn(column);
            }
        }
    }

    /**
     * Updates the column groups from a state change
     */
    private void updateColumnGroupsFromStateChangeEvent() {

        // FIXME When something changes the header/footer rows will be
        // re-created. At some point we should optimize this so partial updates
        // can be made on the header/footer.
        for (ColumnGroupRow row : getWidget().getColumnGroupRows()) {
            getWidget().removeColumnGroupRow(row);
        }

        for (ColumnGroupRowState rowState : getState().columnGroupRows) {
            ColumnGroupRow row = getWidget().addColumnGroupRow();
            row.setFooterVisible(rowState.footerVisible);
            row.setHeaderVisible(rowState.headerVisible);

            for (ColumnGroupState groupState : rowState.groups) {
                List<GridColumn> columns = new ArrayList<GridColumn>();
                for (String columnId : groupState.columns) {
                    CustomGridColumn column = columnIdToColumn.get(columnId);
                    columns.add(column);
                }
                ColumnGroup group = row.addGroup(columns
                        .toArray(new GridColumn[columns.size()]));
                group.setFooterCaption(groupState.footer);
                group.setHeaderCaption(groupState.header);
            }
        }
    }
}
