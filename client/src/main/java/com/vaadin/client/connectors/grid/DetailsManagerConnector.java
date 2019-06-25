/*
 * Copyright 2000-2018 Vaadin Ltd.
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
@Connect(DetailsManager.class)
public class DetailsManagerConnector extends AbstractExtensionConnector {

    /* Map for tracking which details are open on which row */
    private TreeMap<Integer, String> indexToDetailConnectorId = new TreeMap<>();
    /* For listening data changes that originate from DataSource. */
    private Registration dataChangeRegistration;
    /* For listening spacer index changes that originate from Escalator. */
    private HandlerRegistration spacerIndexChangedHandlerRegistration;
    /* Registration for spacer visibility change handler. */
    private HandlerRegistration spacerVisibilityChangeRegistration;
    /* Registration for spacer visibility change handler. */
    private HandlerRegistration rowVisibilityChangeHandlerRegistration;

    private final Map<Element, ScheduledCommand> elementToResizeCommand = new HashMap<Element, Scheduler.ScheduledCommand>();
    private final ElementResizeListener detailsRowResizeListener = event -> {
        if (elementToResizeCommand.containsKey(event.getElement())) {
            Scheduler.get().scheduleFinally(
                    elementToResizeCommand.get(event.getElement()));
        }
    };

    /* variables for delayed alert that details have been refreshed */
    private boolean delayedDetailsRefreshedCommandTriggered = false;
    private boolean delayedDetailsRefreshed = false;
    private ScheduledCommand delayedDetailsRefreshedCommand = () -> {
        getParent().detailsRefreshed(delayedDetailsRefreshed);
        delayedDetailsRefreshedCommandTriggered = false;
        delayedDetailsRefreshed = false;
    };

    /* variables for triggering a delayed re-positioning in Escalator */
    private boolean delayedRepositioningTriggered = false;
    private Integer delayedRepositioningStart = null;
    private Integer delayedRepositioningEnd = null;
    private ScheduledCommand delayedRepositioningCommand = () -> {
        // refresh the positions of all affected rows and those
        // below them, unless all affected rows are outside of the
        // visual range
        if (getWidget().getEscalator().getVisibleRowRange().intersects(Range
                .between(delayedRepositioningStart, delayedRepositioningEnd))) {
            getWidget().getEscalator().getBody().updateRowPositions(
                    delayedRepositioningStart,
                    getWidget().getEscalator().getBody().getRowCount()
                            - delayedRepositioningStart);
        }
        delayedRepositioningTriggered = false;
        delayedRepositioningStart = null;
        delayedRepositioningEnd = null;
    };

    /* calculated when the first details row is opened */
    private Double spacerCellBorderHeights = null;

    /**
     * DataChangeHandler for updating the visibility of detail widgets.
     */
    private final class DetailsChangeHandler implements DataChangeHandler {
        boolean removing = false;

        @Override
        public void resetDataAndSize(int estimatedNewDataSize) {
            // Full clean up
            indexToDetailConnectorId.clear();
        }

        @Override
        public void dataUpdated(int firstRowIndex, int numberOfRows) {
            if (!getState().hasDetailsGenerator) {
                triggerDelayedDetailsRefreshedCommand(false);
                return;
            }
            for (int i = 0; i < numberOfRows; ++i) {
                int index = firstRowIndex + i;
                detachIfNeeded(index, getDetailsComponentConnectorId(index));
            }
            if (numberOfRows == 1) {
                getParent().singleDetailsOpened(firstRowIndex);
            }
            // the update may have affected details row contents and size,
            // recalculation and triggering of any pending navigation
            // confirmations etc. is needed
            triggerDelayedRepositioning(firstRowIndex, numberOfRows);
            triggerDelayedDetailsRefreshedCommand(true);
        }

        @Override
        public void dataRemoved(int firstRowIndex, int numberOfRows) {
            if (!getState().hasDetailsGenerator) {
                triggerDelayedDetailsRefreshedCommand(false);
                return;
            }
            removing = true;
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
            removing = false;
            // Grid and Escalator take care of their own cleanup at removal, no
            // need to clear details from those. Because this removal happens
            // instantly any pending scroll to row or such should not need
            // another attempt and unless something else causes such need the
            // pending operations should be cleared out.
            triggerDelayedDetailsRefreshedCommand(false);
        }

        @Override
        public void dataAvailable(int firstRowIndex, int numberOfRows) {
            if (removing || !getState().hasDetailsGenerator) {
                triggerDelayedDetailsRefreshedCommand(false);
                return;
            }
            // NOTE: this relies on Escalator getting updated first
            Range visibleRowRange = getWidget().getEscalator()
                    .getVisibleRowRange();
            boolean newOrUpdatedDetails = false;
            for (int i = 0; i < numberOfRows; ++i) {
                int rowIndex = firstRowIndex + i;
                if (!visibleRowRange.contains(rowIndex)) {
                    continue;
                }
                String id = getDetailsComponentConnectorId(rowIndex);
                String oldId = indexToDetailConnectorId.get(rowIndex);
                if ((oldId == null && id == null)
                        || (oldId != null && oldId.equals(id))) {
                    // nothing to update, move along
                    continue;
                }
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
                        indexToDetailConnectorId.put(rowIndex, id);
                        newOrUpdatedDetails = true;
                    }
                } else {
                    // new Details content
                    indexToDetailConnectorId.put(rowIndex, id);
                    newOrUpdatedDetails = true;
                    getWidget().setDetailsVisible(rowIndex, true);
                }
            }
            triggerDelayedDetailsRefreshedCommand(newOrUpdatedDetails);
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

        // When details element is shown, remeasure it in the layout phase
        spacerVisibilityChangeRegistration = getWidget()
                .addSpacerVisibilityChangedHandler(event -> {
                    if (event.isSpacerVisible()) {
                        String id = indexToDetailConnectorId
                                .get(event.getRowIndex());
                        ComponentConnector connector = (ComponentConnector) ConnectorMap
                                .get(getConnection()).getConnector(id);
                        getLayoutManager()
                                .setNeedsMeasureRecursively(connector);
                    }
                });

        rowVisibilityChangeHandlerRegistration = getWidget()
                .addRowVisibilityChangeHandler(event -> {
                    refreshDetailsVisibilityWithRange(
                            event.getVisibleRowRange());
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

            Scheduler.get().scheduleFinally(delayedRepositioningCommand);
        }
    }

    /**
     * Trigger a delayed alert to the parent that details have been added or
     * updated, so that the parent can re-attempt any position-sensitive
     * operations. Since the delay needs to be introduced in order to allow the
     * details row(s) to get their final size, it's possible that more than one
     * operation that might affect the existence or size will be performed
     * before the check can actually be made. The check will be only performed
     * once regardless of how many times it gets triggered within the delay
     * period. Re-attempt will be performed if this method is called at least
     * once with value {@code true} before the check, even if the initial or any
     * subsequent call within the delay period were called with value
     * {@code false}.
     *
     * @param detailsShown
     *            {@code true} if the calling operation added or updated
     *            details, {@code false} otherwise
     */
    private void triggerDelayedDetailsRefreshedCommand(boolean detailsShown) {
        if (detailsShown) {
            delayedDetailsRefreshed = true;
        }
        if (!delayedDetailsRefreshedCommandTriggered) {
            Scheduler.get().scheduleFinally(delayedDetailsRefreshedCommand);
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

        spacerVisibilityChangeRegistration.removeHandler();
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
     * gives a delayed notice to the parent if any got added or updated.
     */
    private void refreshDetailsVisibilityWithRange(Range rangeToRefresh) {
        if (!getState().hasDetailsGenerator) {
            triggerDelayedDetailsRefreshedCommand(false);
            return;
        }
        boolean shownDetails = false;
        Range visibleRowRange = getWidget().getEscalator().getVisibleRowRange();
        Range[] partitions = visibleRowRange.partitionWith(rangeToRefresh);
        // only inspect the range where visible and refreshed rows overlap
        Range filteredRange = partitions[1];

        for (int i = filteredRange.getStart(); i < filteredRange
                .getEnd(); ++i) {
            String id = getDetailsComponentConnectorId(i);

            detachIfNeeded(i, id);

            if (id == null) {
                continue;
            }

            indexToDetailConnectorId.put(i, id);
            getWidget().setDetailsVisible(i, true);
            shownDetails = true;
        }

        triggerDelayedDetailsRefreshedCommand(shownDetails);
    }
}
