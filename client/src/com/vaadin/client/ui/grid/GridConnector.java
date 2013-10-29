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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
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

    private class CustomGridColumn extends GridColumn<String[]> {

        @Override
        public String getValue(String[] obj) {
            // FIXME Should return something from the data source.
            return null;
        }
    }

    // Maps a generated column id -> A grid column instance
    private Map<String, CustomGridColumn> columnIdToColumn = new HashMap<String, CustomGridColumn>();

    @Override
    protected void init() {

        // FIXME Escalator bug requires to do this when running compiled. Not
        // required when in devmode. Most likely Escalator.setWidth() is called
        // before attach and measuring from DOM does not work then.
        getWidget().setWidth(getState().width);
        getWidget().setHeight(getState().height);

    }

    @Override
    protected Grid<String[]> createWidget() {
        // FIXME Shouldn't be needed after #12873 has been fixed.
        return new Grid<String[]>();
    }

    @Override
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

        // Header
        if (stateChangeEvent.hasPropertyChanged("headerVisible")) {
            getWidget().setHeaderVisible(getState().headerVisible);
        }

        // Footer
        if (stateChangeEvent.hasPropertyChanged("footerVisible")) {
            getWidget().setFooterVisible(getState().footerVisible);
        }

        // Column updates
        if (stateChangeEvent.hasPropertyChanged("columns")) {

            int totalColumns = getState().columns.size();
            int currentColumns = getWidget().getColumnCount();

            // Remove old columns
            purgeRemovedColumns();

            // Add new columns
            for (int columnIndex = currentColumns; columnIndex < totalColumns; columnIndex++) {
                addColumnFromStateChangeEvent(columnIndex, stateChangeEvent);
            }

            // Update old columns
            for (int columnIndex = 0; columnIndex < currentColumns; columnIndex++) {
                // FIXME Currently updating all column header / footers when a
                // change in made in one column. When the framework supports
                // quering a specific item in a list then it should do so here.
                updateColumnFromStateChangeEvent(columnIndex, stateChangeEvent);
            }
        }
    }

    /**
     * Updates a column from a state change event.
     * 
     * @param columnIndex
     *            The index of the column to update
     * @param stateChangeEvent
     *            The state change event that contains the changes for the
     *            column
     */
    private void updateColumnFromStateChangeEvent(int columnIndex,
            StateChangeEvent stateChangeEvent) {
        GridColumn<String[]> column = getWidget().getColumn(columnIndex);
        GridColumnState columnState = getState().columns.get(columnIndex);
        updateColumnFromState(column, columnState);
    }

    /**
     * Adds a new column to the grid widget from a state change event
     * 
     * @param columnIndex
     *            The index of the column, according to how it
     * @param stateChangeEvent
     */
    private void addColumnFromStateChangeEvent(int columnIndex,
            StateChangeEvent stateChangeEvent) {
        GridColumnState state = getState().columns.get(columnIndex);
        CustomGridColumn column = new CustomGridColumn();
        updateColumnFromState(column, state);
        columnIdToColumn.put(state.id, column);
        getWidget().addColumn(column, columnIndex);
    }

    /**
     * Updates fields in column from a {@link GridColumnState} DTO
     * 
     * @param column
     *            The column to update
     * @param state
     *            The state to update from
     */
    private void updateColumnFromState(GridColumn<String[]> column,
            GridColumnState state) {
        column.setHeaderCaption(state.header);
        column.setFooterCaption(state.footer);
        column.setVisible(state.visible);
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
}
