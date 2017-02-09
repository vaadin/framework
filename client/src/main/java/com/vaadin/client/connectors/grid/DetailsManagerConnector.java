/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.connectors.grid;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Widget;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.HeightAwareDetailsGenerator;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.DetailsManagerState;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid.DetailsManager;

import elemental.json.JsonObject;

/**
 * Connector class for {@link DetailsManager} of the Grid component.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(DetailsManager.class)
public class DetailsManagerConnector extends AbstractExtensionConnector {

    /* Map for tracking which details are open on which row */
    private Map<Integer, String> indexToDetailConnectorId = new HashMap<>();
    /* Boolean flag to avoid multiple refreshes */
    private boolean refreshing;
    /* Registration for data change handler. */
    private Registration dataChangeRegistration;

    /**
     * DataChangeHandler for updating the visibility of detail widgets.
     */
    private final class DetailsChangeHandler implements DataChangeHandler {
        @Override
        public void resetDataAndSize(int estimatedNewDataSize) {
            // Full clean up
            indexToDetailConnectorId.clear();
        }

        @Override
        public void dataUpdated(int firstRowIndex, int numberOfRows) {
            for (int i = 0; i < numberOfRows; ++i) {
                int index = firstRowIndex + i;
                detachIfNeeded(index, getDetailsComponentConnectorId(index));
            }
            if (numberOfRows == 1) {
                getParent().singleDetailsOpened(firstRowIndex);
            }
            // Deferred opening of new ones.
            refreshDetails();
        }

        /* The remaining methods will do a full refresh for now */

        @Override
        public void dataRemoved(int firstRowIndex, int numberOfRows) {
            refreshDetails();
        }

        @Override
        public void dataAvailable(int firstRowIndex, int numberOfRows) {
            refreshDetails();
        }

        @Override
        public void dataAdded(int firstRowIndex, int numberOfRows) {
            refreshDetails();
        }
    }

    /**
     * Height aware details generator for client-side Grid.
     */
    private class CustomDetailsGenerator
            implements HeightAwareDetailsGenerator {

        @Override
        public Widget getDetails(int rowIndex) {
            String id = getDetailsComponentConnectorId(rowIndex);
            if (id == null) {
                return null;
            }

            return getConnector(id).getWidget();
        }

        @Override
        public double getDetailsHeight(int rowIndex) {
            // Case of null is handled in the getDetails method and this method
            // will not called if it returns null.
            String id = getDetailsComponentConnectorId(rowIndex);
            ComponentConnector componentConnector = getConnector(id);

            getLayoutManager().setNeedsMeasureRecursively(componentConnector);
            getLayoutManager().layoutNow();

            return getLayoutManager().getOuterHeightDouble(
                    componentConnector.getWidget().getElement());
        }

        private ComponentConnector getConnector(String id) {
            return (ComponentConnector) ConnectorMap.get(getConnection())
                    .getConnector(id);
        }
    }

    @Override
    protected void extend(ServerConnector target) {
        getWidget().setDetailsGenerator(new CustomDetailsGenerator());
        dataChangeRegistration = getWidget().getDataSource()
                .addDataChangeHandler(new DetailsChangeHandler());
    }

    private void detachIfNeeded(int rowIndex, String id) {
        if (indexToDetailConnectorId.containsKey(rowIndex)) {
            if (indexToDetailConnectorId.get(rowIndex).equals(id)) {
                return;
            }

            // New Details component, hide old one
            getWidget().setDetailsVisible(rowIndex, false);
            indexToDetailConnectorId.remove(rowIndex);
        }
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        dataChangeRegistration.remove();
        dataChangeRegistration = null;

        indexToDetailConnectorId.clear();
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    @Override
    public DetailsManagerState getState() {
        return (DetailsManagerState) super.getState();
    }

    private Grid<JsonObject> getWidget() {
        return getParent().getWidget();
    }

    /**
     * Returns the connector id for a details component.
     *
     * @param rowIndex
     *            the row index of details component
     * @return connector id; {@code null} if row or id is not found
     */
    private String getDetailsComponentConnectorId(int rowIndex) {
        JsonObject row = getParent().getWidget().getDataSource()
                .getRow(rowIndex);

        if (row == null || !row.hasKey(GridState.JSONKEY_DETAILS_VISIBLE)
                || row.getString(GridState.JSONKEY_DETAILS_VISIBLE).isEmpty()) {
            return null;
        }

        return row.getString(GridState.JSONKEY_DETAILS_VISIBLE);
    }

    private LayoutManager getLayoutManager() {
        return LayoutManager.get(getConnection());
    }

    /**
     * Schedules a deferred opening for new details components.
     */
    private void refreshDetails() {
        if (refreshing) {
            return;
        }

        refreshing = true;
        Scheduler.get().scheduleFinally(this::refreshDetailsVisibility);
    }

    private void refreshDetailsVisibility() {
        boolean shownDetails = false;
        for (int i = 0; i < getWidget().getDataSource().size(); ++i) {
            String id = getDetailsComponentConnectorId(i);

            detachIfNeeded(i, id);

            if (id == null) {
                continue;
            }

            indexToDetailConnectorId.put(i, id);
            getWidget().setDetailsVisible(i, true);
            shownDetails = true;
        }
        refreshing = false;
        getParent().detailsRefreshed(shownDetails);
    }
}
