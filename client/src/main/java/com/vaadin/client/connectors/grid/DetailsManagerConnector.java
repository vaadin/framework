/*
 * Copyright 2000-2021 Vaadin Ltd.
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
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.connectors.data.DataCommunicatorConnector;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.client.widget.escalator.events.SpacerIndexChangedEvent;
import com.vaadin.client.widget.escalator.events.SpacerIndexChangedHandler;
import com.vaadin.client.widget.grid.HeightAwareDetailsGenerator;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
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
@SuppressWarnings("deprecation")
@Connect(DetailsManager.class)
public class DetailsManagerConnector extends AbstractExtensionConnector {

    /* Map for tracking which details are open on which row */
    private Map<Integer, String> indexToDetailConnectorId = new TreeMap<>();
    /* For listening data changes that originate from DataSource. */
    private Registration dataChangeRegistration;
    /* For listening spacer index changes that originate from Escalator. */
    private HandlerRegistration spacerIndexChangedHandlerRegistration;
    /* For listening when Escalator's visual range is changed. */
    private HandlerRegistration rowVisibilityChangeHandlerRegistration;

    private final Map<Element, ScheduledCommand> elementToResizeCommand = new HashMap<Element, Scheduler.ScheduledCommand>();
    private final ElementResizeListener detailsRowResizeListener = event -> {
        if (elementToResizeCommand.containsKey(event.getElement())) {
            Scheduler.get().scheduleFinally(
                    elementToResizeCommand.get(event.getElement()));
        }
    };

    /* for delayed alert if Grid needs to run or cancel pending operations */
    private boolean delayedDetailsAddedOrUpdatedAlertTriggered = false;
    private boolean delayedDetailsAddedOrUpdated = false;

    /* for delayed re-positioning of Escalator contents to prevent gaps */
    /* -1 is a possible spacer index in Escalator so can't be used as default */
    private boolean delayedRepositioningTriggered = false;
    private Integer delayedRepositioningStart = null;
    private Integer delayedRepositioningEnd = null;

    /* calculated when the first details row is opened */
    private Double spacerCellBorderHeights = null;

    private Range availableRowRange = Range.emptyRange();
    private Range latestVisibleRowRange = Range.emptyRange();

    /**
     * DataChangeHandler for updating the visibility of detail widgets.
     */
    private final class DetailsChangeHandler implements DataChangeHandler {

        @Override
        public void resetDataAndSize(int estimatedNewDataSize) {
            // No need to do anything, dataUpdated and dataAvailable take care
            // of cleanup.
        }

        @Override
        public void dataUpdated(int firstRowIndex, int numberOfRows) {
            if (!getState().hasDetailsGenerator) {
                markDetailsAddedOrUpdatedForDelayedAlertToGrid(false);
                return;
            }
            Range updatedRange = Range.withLength(firstRowIndex, numberOfRows);

            // NOTE: this relies on Escalator getting updated first
            Range newVisibleRowRange = getWidget().getEscalator()
                    .getVisibleRowRange();

            if (updatedRange.partitionWith(availableRowRange)[1]
                    .length() != updatedRange.length()
                    || availableRowRange.partitionWith(newVisibleRowRange)[1]
                            .length() != newVisibleRowRange.length()) {
                // full visible range not available yet or full refresh coming
                // up anyway, leave updating to dataAvailable
                if (numberOfRows == 1
                        && latestVisibleRowRange.contains(firstRowIndex)) {
                    // A single details row has been opened or closed within
                    // visual range, trigger scrollTo after dataAvailable has
                    // done its thing. Do not attempt to scroll to details rows
                    // that are opened outside of the visual range.
                    Scheduler.get().scheduleFinally(() -> {
                        getParent().singleDetailsOpened(firstRowIndex);
                        // we don't know yet whether there are details or not,
                        // mark them added or updated just in case, so that
                        // the potential scrolling attempt gets triggered after
                        // another layout phase is finished
                        markDetailsAddedOrUpdatedForDelayedAlertToGrid(true);
                    });
                }
                return;
            }

            // only trigger scrolling attempt if the single updated row is
            // within existing visual range
            boolean scrollToFirst = numberOfRows == 1
                    && latestVisibleRowRange.contains(firstRowIndex);

            if (!newVisibleRowRange.equals(latestVisibleRowRange)) {
                // update visible range
                latestVisibleRowRange = newVisibleRowRange;

                // do full refresh
                detachOldAndRefreshCurrentDetails();
            } else {
                // refresh only the updated range
                refreshDetailsVisibilityWithRange(updatedRange);

                // the update may have affected details row contents and size,
                // recalculation and triggering of any pending navigation
                // confirmations etc. is needed
                triggerDelayedRepositioning(firstRowIndex, numberOfRows);
            }

            if (scrollToFirst) {
                // scroll to opened row (if it got closed instead, nothing
                // happens)
                getParent().singleDetailsOpened(firstRowIndex);
                markDetailsAddedOrUpdatedForDelayedAlertToGrid(true);
            }
        }

        @Override
        public void dataRemoved(int firstRowIndex, int numberOfRows) {
            if (!getState().hasDetailsGenerator) {
                markDetailsAddedOrUpdatedForDelayedAlertToGrid(false);
                return;
            }
            Range removing = Range.withLength(firstRowIndex, numberOfRows);

            // update the handled range to only contain rows that fall before
            // the removed range
            latestVisibleRowRange = Range
                    .between(latestVisibleRowRange.getStart(),
                            Math.max(latestVisibleRowRange.getStart(),
                                    Math.min(firstRowIndex,
                                            latestVisibleRowRange.getEnd())));

            // reduce the available range accordingly
            Range[] partitions = availableRowRange.partitionWith(removing);
            Range removedAbove = partitions[0];
            Range removedAvailable = partitions[1];
            availableRowRange = Range.withLength(
                    Math.max(0,
                            availableRowRange.getStart()
                                    - removedAbove.length()),
                    Math.max(0, availableRowRange.length()
                            - removedAvailable.length()));

            for (int i = 0; i < numberOfRows; ++i) {
                int rowIndex = firstRowIndex + i;
                if (indexToDetailConnectorId.containsKey(rowIndex)) {
                    String id = indexToDetailConnectorId.get(rowIndex);

                    ComponentConnector connector = (ComponentConnector) ConnectorMap
                            .get(getConnection()).getConnector(id);
                    if (connector != null) {
                        Element element = connector.getWidget().getElement();
                        elementToResizeCommand.remove(element);
                        getLayoutManager().removeElementResizeListener(element,
                                detailsRowResizeListener);
                    }
                    indexToDetailConnectorId.remove(rowIndex);
                }
            }
            // Grid and Escalator take care of their own cleanup at removal, no
            // need to clear details from those. Because this removal happens
            // instantly any pending scroll to row or such should not need
            // another attempt and unless something else causes such need the
            // pending operations should be cleared out.
            markDetailsAddedOrUpdatedForDelayedAlertToGrid(false);
        }

        @Override
        public void dataAvailable(int firstRowIndex, int numberOfRows) {
            if (!getState().hasDetailsGenerator) {
                markDetailsAddedOrUpdatedForDelayedAlertToGrid(false);
                return;
            }

            // update available range
            availableRowRange = Range.withLength(firstRowIndex, numberOfRows);

            // NOTE: this relies on Escalator getting updated first
            Range newVisibleRowRange = getWidget().getEscalator()
                    .getVisibleRowRange();
            // only process the section that is actually available
            newVisibleRowRange = availableRowRange
                    .partitionWith(newVisibleRowRange)[1];
            if (newVisibleRowRange.equals(latestVisibleRowRange)) {
                // no need to update
                return;
            }

            // check whether the visible range has simply got shortened
            // (e.g. by changing the default row height)
            boolean subsectionOfOld = latestVisibleRowRange
                    .partitionWith(newVisibleRowRange)[1]
                            .length() == newVisibleRowRange.length();

            // update visible range
            latestVisibleRowRange = newVisibleRowRange;

            if (subsectionOfOld) {
                // only detach extra rows
                detachExcludingRange(latestVisibleRowRange);
            } else {
                // there are completely new visible rows, full refresh
                detachOldAndRefreshCurrentDetails();
            }
        }

        @Override
        public void dataAdded(int firstRowIndex, int numberOfRows) {
            refreshDetailsVisibilityWithRange(
                    Range.withLength(firstRowIndex, numberOfRows));
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
                detachIfNeeded(rowIndex, id);
                return null;
            }
            String oldId = indexToDetailConnectorId.get(rowIndex);
            if (oldId != null && !oldId.equals(id)) {
                // remove outdated connector
                ComponentConnector connector = (ComponentConnector) ConnectorMap
                        .get(getConnection()).getConnector(oldId);
                if (connector != null) {
                    Element element = connector.getWidget().getElement();
                    elementToResizeCommand.remove(element);
                    getLayoutManager().removeElementResizeListener(element,
                            detailsRowResizeListener);
                }
            }

            indexToDetailConnectorId.put(rowIndex, id);
            getWidget().setDetailsVisible(rowIndex, true);

            Widget widget = getConnector(id).getWidget();
            getLayoutManager().addElementResizeListener(widget.getElement(),
                    detailsRowResizeListener);
            elementToResizeCommand.put(widget.getElement(),
                    createResizeCommand(rowIndex, widget.getElement()));

            return widget;
        }

        private ScheduledCommand createResizeCommand(final int rowIndex,
                final Element element) {
            return () -> {
                // It should not be possible to get here without calculating
                // the spacerCellBorderHeights or without having the details
                // row open, nor for this command to be triggered while
                // layout is running, but it's safer to check anyway.
                if (spacerCellBorderHeights != null
                        && !getLayoutManager().isLayoutRunning()
                        && getDetailsComponentConnectorId(rowIndex) != null) {
                    // Measure and set details height if element is visible
                    if (WidgetUtil.isDisplayed(element)) {
                        double height = getLayoutManager().getOuterHeightDouble(
                                element) + spacerCellBorderHeights;
                        getWidget().setDetailsHeight(rowIndex, height);
                    }
                }
            };
        }

        @Override
        public double getDetailsHeight(int rowIndex) {
            // Case of null is handled in the getDetails method and this method
            // will not called if it returns null.
            String id = getDetailsComponentConnectorId(rowIndex);
            ComponentConnector componentConnector = getConnector(id);

            getLayoutManager().setNeedsMeasureRecursively(componentConnector);
            if (!getLayoutManager().isLayoutRunning()
                    && !getConnection().getMessageHandler().isUpdatingState()) {
                getLayoutManager().layoutNow();
            }

            Element element = componentConnector.getWidget().getElement();
            if (spacerCellBorderHeights == null) {
                // If theme is changed, new details generator is created from
                // scratch, so this value doesn't need to be updated elsewhere.
                spacerCellBorderHeights = WidgetUtil
                        .getBorderTopAndBottomThickness(
                                element.getParentElement());
            }

            return getLayoutManager().getOuterHeightDouble(element);
        }

        private ComponentConnector getConnector(String id) {
            return (ComponentConnector) ConnectorMap.get(getConnection())
                    .getConnector(id);
        }
    }

    @Override
    protected void extend(ServerConnector target) {
        getWidget().setDetailsGenerator(new CustomDetailsGenerator());
        spacerIndexChangedHandlerRegistration = getWidget()
                .addSpacerIndexChangedHandler(new SpacerIndexChangedHandler() {
                    @Override
                    public void onSpacerIndexChanged(
                            SpacerIndexChangedEvent event) {
                        // Move spacer from old index to new index. Escalator is
                        // responsible for making sure the new index doesn't
                        // already contain a spacer.
                        String connectorId = indexToDetailConnectorId
                                .remove(event.getOldIndex());
                        indexToDetailConnectorId.put(event.getNewIndex(),
                                connectorId);
                    }
                });
        dataChangeRegistration = getWidget().getDataSource()
                .addDataChangeHandler(new DetailsChangeHandler());

        rowVisibilityChangeHandlerRegistration = getWidget()
                .addRowVisibilityChangeHandler(event -> {
                    if (getConnection().getMessageHandler().isUpdatingState()) {
                        // don't update in the middle of state changes,
                        // leave to dataAvailable
                        return;
                    }
                    Range newVisibleRowRange = event.getVisibleRowRange();
                    if (newVisibleRowRange.equals(latestVisibleRowRange)) {
                        // no need to update
                        return;
                    }
                    Range availableAndVisible = availableRowRange
                            .partitionWith(newVisibleRowRange)[1];
                    if (availableAndVisible.isEmpty()) {
                        // nothing to update yet, leave to dataAvailable
                        return;
                    }

                    if (!availableAndVisible.equals(latestVisibleRowRange)) {
                        // check whether the visible range has simply got
                        // shortened
                        // (e.g. by changing the default row height)
                        boolean subsectionOfOld = latestVisibleRowRange
                                .partitionWith(newVisibleRowRange)[1]
                                        .length() == newVisibleRowRange
                                                .length();

                        // update visible range
                        latestVisibleRowRange = availableAndVisible;

                        if (subsectionOfOld) {
                            // only detach extra rows
                            detachExcludingRange(latestVisibleRowRange);
                        } else {
                            // there are completely new visible rows, full
                            // refresh
                            detachOldAndRefreshCurrentDetails();
                        }
                    } else {
                        // refresh only the visible range, nothing to detach
                        refreshDetailsVisibilityWithRange(availableAndVisible);

                        // the update may have affected details row contents and
                        // size, recalculation is needed
                        triggerDelayedRepositioning(
                                availableAndVisible.getStart(),
                                availableAndVisible.length());
                    }
                });
    }

    /**
     * Triggers repositioning of the the contents from the first affected row
     * downwards if any of the rows fall within the visual range. If any other
     * delayed repositioning has been triggered within this round trip the
     * affected range is expanded as needed. The processing is delayed to make
     * sure all updates have time to get in, otherwise the repositioning will be
     * calculated separately for each details row addition or removal from the
     * server side (see
     * {@link DataCommunicatorClientRpc#updateData(elemental.json.JsonArray)}
     * implementation within {@link DataCommunicatorConnector}).
     *
     * @param firstRowIndex
     *            the index of the first changed row
     * @param numberOfRows
     *            the number of changed rows
     */
    private void triggerDelayedRepositioning(int firstRowIndex,
            int numberOfRows) {
        if (delayedRepositioningStart == null
                || delayedRepositioningStart > firstRowIndex) {
            delayedRepositioningStart = firstRowIndex;
        }
        if (delayedRepositioningEnd == null
                || delayedRepositioningEnd < firstRowIndex + numberOfRows) {
            delayedRepositioningEnd = firstRowIndex + numberOfRows;
        }
        if (!delayedRepositioningTriggered) {
            delayedRepositioningTriggered = true;

            Scheduler.get().scheduleFinally(() -> {
                // refresh the positions of all affected rows and those
                // below them, unless all affected rows are outside of the
                // visual range
                if (getWidget().getEscalator().getVisibleRowRange()
                        .intersects(Range.between(delayedRepositioningStart,
                                delayedRepositioningEnd))) {
                    getWidget().getEscalator().getBody().updateRowPositions(
                            delayedRepositioningStart,
                            getWidget().getEscalator().getBody().getRowCount()
                                    - delayedRepositioningStart);
                }
                delayedRepositioningTriggered = false;
                delayedRepositioningStart = null;
                delayedRepositioningEnd = null;
            });
        }
    }

    /**
     * Makes sure that after the layout phase has finished Grid will be informed
     * whether any details rows were added or updated. This delay is needed to
     * allow the details row(s) to get their final size, and it's possible that
     * more than one operation that might affect that size or details row
     * existence will be performed (and consequently this method called) before
     * the check can actually be made.
     * <p>
     * If this method is called with value {@code true} at least once within the
     * delay phase Grid will be told to run any pending position-sensitive
     * operations it might have in store.
     * <p>
     * If this method is only called with value {@code false} within the delay
     * period Grid will be told to cancel the pending operations.
     * <p>
     * If this method isn't called at all, Grid won't be instructed to either
     * trigger the pending operations or cancel them and hence they remain in a
     * pending state.
     *
     * @param newOrUpdatedDetails
     *            {@code true} if the calling operation added or updated
     *            details, {@code false} otherwise
     */
    private void markDetailsAddedOrUpdatedForDelayedAlertToGrid(
            boolean newOrUpdatedDetails) {
        if (newOrUpdatedDetails) {
            delayedDetailsAddedOrUpdated = true;
        }
        if (!delayedDetailsAddedOrUpdatedAlertTriggered) {
            delayedDetailsAddedOrUpdatedAlertTriggered = true;
            Scheduler.get().scheduleFinally(() -> {
                getParent().detailsRefreshed(delayedDetailsAddedOrUpdated);
                delayedDetailsAddedOrUpdatedAlertTriggered = false;
                delayedDetailsAddedOrUpdated = false;
            });
        }
    }

    private void detachIfNeeded(int rowIndex, String id) {
        if (indexToDetailConnectorId.containsKey(rowIndex)) {
            if (indexToDetailConnectorId.get(rowIndex).equals(id)) {
                return;
            }

            if (id == null) {
                // Details have been hidden, listeners attached to the old
                // component need to be removed
                id = indexToDetailConnectorId.get(rowIndex);
            }

            // New or removed Details component, hide old one
            ComponentConnector connector = (ComponentConnector) ConnectorMap
                    .get(getConnection()).getConnector(id);
            if (connector != null) {
                Element element = connector.getWidget().getElement();
                elementToResizeCommand.remove(element);
                getLayoutManager().removeElementResizeListener(element,
                        detailsRowResizeListener);
            }
            getWidget().setDetailsVisible(rowIndex, false);
            indexToDetailConnectorId.remove(rowIndex);
        }
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        dataChangeRegistration.remove();
        dataChangeRegistration = null;

        spacerIndexChangedHandlerRegistration.removeHandler();
        rowVisibilityChangeHandlerRegistration.removeHandler();

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
        JsonObject row = getWidget().getDataSource().getRow(rowIndex);

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
     * Refreshes the existence of details components within the given range, and
     * gives a delayed notice to Grid if any got added or updated.
     */
    private void refreshDetailsVisibilityWithRange(Range rangeToRefresh) {
        if (!getState().hasDetailsGenerator) {
            markDetailsAddedOrUpdatedForDelayedAlertToGrid(false);
            return;
        }
        boolean newOrUpdatedDetails = false;

        // Don't update the latestVisibleRowRange class variable here, the
        // calling method should take care of that if relevant.
        Range currentVisibleRowRange = getWidget().getEscalator()
                .getVisibleRowRange();

        Range[] partitions = currentVisibleRowRange
                .partitionWith(rangeToRefresh);

        // only inspect the range where visible and refreshed rows overlap
        Range intersectingRange = partitions[1];

        for (int i = intersectingRange.getStart(); i < intersectingRange
                .getEnd(); ++i) {
            String id = getDetailsComponentConnectorId(i);

            detachIfNeeded(i, id);

            if (id == null) {
                continue;
            }

            indexToDetailConnectorId.put(i, id);
            getWidget().setDetailsVisible(i, true);
            newOrUpdatedDetails = true;
        }

        markDetailsAddedOrUpdatedForDelayedAlertToGrid(newOrUpdatedDetails);
    }

    private void detachOldAndRefreshCurrentDetails() {
        Range[] partitions = availableRowRange
                .partitionWith(latestVisibleRowRange);
        Range availableAndVisible = partitions[1];

        detachExcludingRange(availableAndVisible);

        boolean newOrUpdatedDetails = refreshRange(availableAndVisible);

        // the update may have affected details row contents and size,
        // recalculation and triggering of any pending navigation
        // confirmations etc. is needed
        triggerDelayedRepositioning(availableAndVisible.getStart(),
                availableAndVisible.length());

        markDetailsAddedOrUpdatedForDelayedAlertToGrid(newOrUpdatedDetails);
    }

    private void detachExcludingRange(Range keep) {
        // remove all spacers that are no longer in range
        for (Integer existingIndex : indexToDetailConnectorId.keySet()) {
            if (!keep.contains(existingIndex)) {
                detachDetails(existingIndex);
            }
        }
    }

    private boolean refreshRange(Range rangeToRefresh) {
        // make sure all spacers that are currently in range are up to date
        boolean newOrUpdatedDetails = false;
        for (int i = rangeToRefresh.getStart(); i < rangeToRefresh
                .getEnd(); ++i) {
            int rowIndex = i;
            if (refreshDetails(rowIndex)) {
                newOrUpdatedDetails = true;
            }
        }
        return newOrUpdatedDetails;
    }

    private void detachDetails(int rowIndex) {
        String id = indexToDetailConnectorId.remove(rowIndex);
        if (id != null) {
            ComponentConnector connector = (ComponentConnector) ConnectorMap
                    .get(getConnection()).getConnector(id);
            if (connector != null) {
                Element element = connector.getWidget().getElement();
                elementToResizeCommand.remove(element);
                getLayoutManager().removeElementResizeListener(element,
                        detailsRowResizeListener);
            }
        }
        getWidget().setDetailsVisible(rowIndex, false);
    }

    private void detachDetailsIfFound(String connectorId) {
        if (indexToDetailConnectorId.containsValue(connectorId)) {
            for (Entry<Integer, String> entry : indexToDetailConnectorId
                    .entrySet()) {
                if (connectorId.equals(entry.getValue())) {
                    detachDetails(entry.getKey());
                    return;
                }
            }
        }
    }

    private boolean refreshDetails(int rowIndex) {
        String id = getDetailsComponentConnectorId(rowIndex);
        String oldId = indexToDetailConnectorId.get(rowIndex);
        if ((oldId == null && id == null)
                || (oldId != null && oldId.equals(id))) {
            // nothing to update, move along
            return false;
        }
        boolean newOrUpdatedDetails = false;
        if (oldId != null) {
            // Details have been hidden or updated, listeners attached
            // to the old component need to be removed
            ComponentConnector connector = (ComponentConnector) ConnectorMap
                    .get(getConnection()).getConnector(oldId);
            if (connector != null) {
                Element element = connector.getWidget().getElement();
                elementToResizeCommand.remove(element);
                getLayoutManager().removeElementResizeListener(element,
                        detailsRowResizeListener);
            }
            if (id == null) {
                // hidden, clear reference
                getWidget().setDetailsVisible(rowIndex, false);
                indexToDetailConnectorId.remove(rowIndex);
            } else {
                // updated, replace reference

                // ensure that the detail contents aren't still attached to some
                // other row that hasn't been refreshed yet
                detachDetailsIfFound(id);

                indexToDetailConnectorId.put(rowIndex, id);
                newOrUpdatedDetails = true;
                getWidget().resetVisibleDetails(rowIndex);
            }
        } else {
            // new Details content, listeners will get attached to the connector
            // when Escalator requests for the Details through
            // CustomDetailsGenerator#getDetails(int)

            // ensure that the detail contents aren't still attached to some
            // other row that hasn't been refreshed yet
            detachDetailsIfFound(id);

            indexToDetailConnectorId.put(rowIndex, id);
            newOrUpdatedDetails = true;
            getWidget().setDetailsVisible(rowIndex, true);
        }
        return newOrUpdatedDetails;
    }
}
