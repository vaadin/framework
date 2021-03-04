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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

/**
 * Allows dragging rows for reordering within a Grid and between two separate
 * Grids when the item type is the same.
 * <p>
 * When dragging a selected row, all the visible selected rows are dragged. Note
 * that ONLY currently visible rows are taken into account. The drop mode for
 * the target grid is by default {@link DropMode#BETWEEN}.
 * <p>
 * To customize the settings for either the source or the target grid, use
 * {@link #getGridDragSource()} and {@link #getGridDropTarget()}.The drop target
 * grid has been set to not allow drops for a target row when the grid has been
 * sorted, since the visual drop target location would not match where the item
 * would actually be dropped into. Additionally, a grid MUST NOT be the target
 * of more than one GridRowDragger.
 * <p>
 * <em>NOTE: this helper works only with {@link ListDataProvider} on both grids.
 * If you have another data provider, you should customize data provider
 * updating on drop with
 * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} &
 * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} and add a
 * custom drop index calculator with
 * {@link #setDropIndexCalculator(DropIndexCalculator)}.</em>
 * <p>
 * In case you are not using a {@link ListDataProvider} and don't have custom
 * handlers, {@link UnsupportedOperationException} is thrown on drop event.
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd
 * @since 8.2
 */
public class GridRowDragger<T> implements Serializable {

    private final GridDropTarget<T> gridDropTarget;
    private final GridDragSource<T> gridDragSource;

    private DropIndexCalculator<T> dropTargetIndexCalculator = null;
    private SourceDataProviderUpdater<T> sourceDataProviderUpdater = null;
    private TargetDataProviderUpdater<T> targetDataProviderUpdater = null;

    /**
     * Set of items currently being dragged.
     */
    private List<T> draggedItems;
    private int shiftedDropIndex;

    /**
     * Enables DnD reordering for the rows in the given grid.
     * <p>
     * {@link DropMode#BETWEEN} is used.
     * <p>
     * <em>NOTE:</em> this only works when the grid has a
     * {@link ListDataProvider}. Use the custom handlers
     * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} and
     * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} for
     * other data providers.
     * <p>
     * <em>NOTE:</em> When allowing the user to DnD reorder a grid's rows, you
     * should not allow the user to sort the grid since when the grid is sorted,
     * as the reordering doens't make any sense since the drop target cannot be
     * shown for the correct place due to the sorting. Sorting columns is
     * enabled by default for in-memory data provider grids. Sorting can be
     * disabled for columns with {@link Grid#getColumns()} and
     * {@link Column#setSortable(boolean)}.
     *
     * @param grid
     *            Grid to be extended.
     */
    public GridRowDragger(Grid<T> grid) {
        this(grid, DropMode.BETWEEN);
    }

    /**
     * Enables DnD reordering the rows in the given grid with the given drop
     * mode.
     * <p>
     * <em>NOTE:</em> this only works when the grid has a
     * {@link ListDataProvider}. Use the custom handlers
     * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} and
     * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} for
     * other data providers.
     * <p>
     * <em>NOTE:</em> When allowing the user to DnD reorder a grid's rows, you
     * should not allow the user to sort the grid since when the grid is sorted,
     * as the reordering doens't make any sense since the drop target cannot be
     * shown for the correct place due to the sorting. Sorting columns is
     * enabled by default for in-memory data provider grids. Sorting can be
     * disabled for columns with {@link Grid#getColumns()} and
     * {@link Column#setSortable(boolean)}.
     *
     * @param grid
     *            the grid to enable row DnD reordering on
     * @param dropMode
     *            DropMode to be used.
     */
    public GridRowDragger(Grid<T> grid, DropMode dropMode) {
        this(grid, grid, dropMode);
    }

    /**
     * Enables DnD moving of rows from the source grid to the target grid.
     * <p>
     * {@link DropMode#BETWEEN} is used.
     * <p>
     * <em>NOTE: this only works when the grids have a
     * {@link ListDataProvider}.</em> Use the custom handlers
     * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} and
     * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} for
     * other data providers.
     *
     * @param source
     *            the source grid dragged from.
     * @param target
     *            the target grid dropped to.
     */
    public GridRowDragger(Grid<T> source, Grid<T> target) {
        this(source, target, DropMode.BETWEEN);
    }

    /**
     * Enables DnD moving of rows from the source grid to the target grid with
     * the custom data provider updaters.
     * <p>
     * {@link DropMode#BETWEEN} is used.
     *
     * @param source
     *            grid dragged from
     * @param target
     *            grid dragged to
     * @param targetDataProviderUpdater
     *            handler for updating target grid data provider
     * @param sourceDataProviderUpdater
     *            handler for updating source grid data provider
     */
    public GridRowDragger(Grid<T> source, Grid<T> target,
            TargetDataProviderUpdater<T> targetDataProviderUpdater,
            SourceDataProviderUpdater<T> sourceDataProviderUpdater) {
        this(source, target, DropMode.BETWEEN);
        this.targetDataProviderUpdater = targetDataProviderUpdater;
        this.sourceDataProviderUpdater = sourceDataProviderUpdater;
    }

    /**
     * Enables DnD moving of rows from the source grid to the target grid with
     * the given drop mode.
     * <p>
     * <em>NOTE: this only works when the grids have a
     * {@link ListDataProvider}.</em> Use the other constructors or custom
     * handlers {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)}
     * and {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} for
     * other data providers.
     *
     * @param source
     *            the drag source grid
     * @param target
     *            the drop target grid
     * @param dropMode
     *            the drop mode to use
     */
    public GridRowDragger(Grid<T> source, Grid<T> target, DropMode dropMode) {
        gridDragSource = new GridDragSource<>(source);

        gridDropTarget = new GridDropTarget<>(target, dropMode);
        gridDropTarget.setDropAllowedOnRowsWhenSorted(false);

        gridDragSource.addGridDragStartListener(event -> {
            draggedItems = event.getDraggedItems();
        });

        gridDropTarget.addGridDropListener(this::handleDrop);
    }

    /**
     * Sets the target data provider updater, which handles adding the dropped
     * items to the target grid.
     * <p>
     * By default, items are added to the index where they were dropped on for
     * any {@link ListDataProvider}. If another type of data provider is used,
     * this updater should be set to handle updating instead.
     *
     * @param targetDataProviderUpdater
     *            the target drop handler to set, or {@code null} to remove
     */
    public void setTargetDataProviderUpdater(
            TargetDataProviderUpdater<T> targetDataProviderUpdater) {
        this.targetDataProviderUpdater = targetDataProviderUpdater;
    }

    /**
     * Returns the target grid data provider updater.
     *
     * @return target grid drop handler
     */
    public TargetDataProviderUpdater<T> getTargetDataProviderUpdater() {
        return targetDataProviderUpdater;
    }

    /**
     * Sets the source data provider updater, which handles removing items from
     * the drag source grid.
     * <p>
     * By default the items are removed from any {@link ListDataProvider}. If
     * another type of data provider is used, this updater should be set to
     * handle updating instead.
     * <p>
     * If you want to skip removing items from the source, you can use
     * {@link SourceDataProviderUpdater#NOOP}.
     *
     * @param sourceDataProviderUpdater
     *            the drag source data provider updater to set, or {@code null}
     *            to remove
     */
    public void setSourceDataProviderUpdater(
            SourceDataProviderUpdater<T> sourceDataProviderUpdater) {
        this.sourceDataProviderUpdater = sourceDataProviderUpdater;
    }

    /**
     * Returns the source grid data provider updater.
     * <p>
     * Default is {@code null} and the items are just removed from the source
     * grid, which only works for {@link ListDataProvider}.
     *
     * @return the source grid drop handler
     */
    public SourceDataProviderUpdater<T> getSourceDataProviderUpdater() {
        return sourceDataProviderUpdater;
    }

    /**
     * Sets the drop index calculator for the target grid. With this callback
     * you can have a custom drop location instead of the actual one.
     * <p>
     * By default, items are placed on the index they are dropped into in the
     * target grid.
     * <p>
     * If you want to always drop items to the end of the target grid, you can
     * use {@link DropIndexCalculator#ALWAYS_DROP_TO_END}.
     *
     * @param dropIndexCalculator
     *            the drop index calculator
     */
    public void setDropIndexCalculator(
            DropIndexCalculator<T> dropIndexCalculator) {
        this.dropTargetIndexCalculator = dropIndexCalculator;
    }

    /**
     * Gets the drop index calculator.
     * <p>
     * Default is {@code null} and the dropped items are placed on the drop
     * location.
     *
     * @return the drop index calculator
     */
    public DropIndexCalculator<T> getDropIndexCalculator() {
        return dropTargetIndexCalculator;
    }

    /**
     * Returns the drop target grid to allow performing customizations such as
     * altering {@link DropEffect}.
     *
     * @return the drop target grid
     */
    public GridDropTarget<T> getGridDropTarget() {
        return gridDropTarget;
    }

    /**
     * Returns the drag source grid, exposing it for customizations.
     *
     * @return the drag source grid
     */
    public GridDragSource<T> getGridDragSource() {
        return gridDragSource;
    }

    /**
     * Returns the currently dragged items captured from the source grid no drag
     * start event, or {@code null} if no drag active.
     *
     * @return the currently dragged items or {@code null}
     */
    protected List<T> getDraggedItems() {
        return draggedItems;
    }

    /**
     * This method is triggered when there has been a drop on the target grid.
     * <p>
     * <em>This method is protected only for testing reasons, you should not
     * override this</em> but instead use
     * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)},
     * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} and
     * {@link #setDropIndexCalculator(DropIndexCalculator)} to customize how to
     * handle the drops.
     *
     * @param event
     *            the drop event on the target grid
     */
    protected void handleDrop(GridDropEvent<T> event) {
        // there is a case that the drop happened from some other grid than the
        // source one
        if (getDraggedItems() == null) {
            return;
        }

        // don't do anything if not supported data providers used without custom
        // handlers
        verifySupportedDataProviders();

        shiftedDropIndex = -1;
        handleSourceGridDrop(event, getDraggedItems());

        int index = calculateDropIndex(event);

        handleTargetGridDrop(event, index, getDraggedItems());

        draggedItems = null;
    }

    private void handleSourceGridDrop(GridDropEvent<T> event,
            final Collection<T> droppedItems) {
        Grid<T> source = getGridDragSource().getGrid();

        if (getSourceDataProviderUpdater() != null) {
            getSourceDataProviderUpdater().removeItems(event.getDropEffect(),
                    source.getDataProvider(), droppedItems);
            return;
        }

        ListDataProvider<T> listDataProvider = (ListDataProvider<T>) source
                .getDataProvider();

        // use the existing data source to keep filters and sort orders etc. in
        // place.
        Collection<T> sourceItems = listDataProvider.getItems();

        // if reordering the same grid and dropping on top of one of the dragged
        // rows, need to calculate the new drop index before removing the items
        if (getGridDragSource().getGrid() == getGridDropTarget().getGrid()
                && event.getDropTargetRow().isPresent()
                && getDraggedItems().contains(event.getDropTargetRow().get())) {
            List<T> sourceItemsList = (List<T>) sourceItems;
            shiftedDropIndex = sourceItemsList
                    .indexOf(event.getDropTargetRow().get());
            shiftedDropIndex -= getDraggedItems().stream().filter(
                    item -> sourceItemsList.indexOf(item) < shiftedDropIndex)
                    .count();
        }

        sourceItems.removeAll(droppedItems);

        // if reordering the same grid, DataProvider's refresh will be done later
        if (getGridDragSource().getGrid() != getGridDropTarget().getGrid()) {
            listDataProvider.refreshAll();
        }
    }

    private void handleTargetGridDrop(GridDropEvent<T> event, final int index,
            Collection<T> droppedItems) {
        Grid<T> target = getGridDropTarget().getGrid();

        if (getTargetDataProviderUpdater() != null) {
            getTargetDataProviderUpdater().onDrop(event.getDropEffect(),
                    target.getDataProvider(), index, droppedItems);
            return;
        }

        ListDataProvider<T> listDataProvider = (ListDataProvider<T>) target
                .getDataProvider();
        // update the existing to keep filters etc.
        List<T> targetItems = (List<T>) listDataProvider.getItems();

        if (index != Integer.MAX_VALUE) {
            targetItems.addAll(index, droppedItems);
        } else {
            targetItems.addAll(droppedItems);
        }
        // instead of using setItems or creating a new data provider,
        // refresh the existing one to keep filters etc. in place
        listDataProvider.refreshAll();

        // if dropped to the end of the grid, the grid should scroll there so
        // that the dropped row is visible, but that is just recommended in
        // documentation and left for the users to take into use
    }

    private int calculateDropIndex(GridDropEvent<T> event) {
        // use custom calculator if present
        if (getDropIndexCalculator() != null) {
            return getDropIndexCalculator().calculateDropIndex(event);
        }

        // if the source and target grids are the same, then the index has been
        // calculated before removing the items. In this case the drop location
        // is always above, since the items will be starting from that point on
        if (shiftedDropIndex != -1) {
            return shiftedDropIndex;
        }

        ListDataProvider<T> targetDataProvider = (ListDataProvider<T>) getGridDropTarget()
                .getGrid().getDataProvider();
        List<T> items = (List<T>) targetDataProvider.getItems();
        int index = items.size();

        Optional<T> dropTargetRow = event.getDropTargetRow();
        if (dropTargetRow.isPresent()) {
            index = items.indexOf(dropTargetRow.get())
                    + (event.getDropLocation() == DropLocation.BELOW ? 1 : 0);
        }

        return index;
    }

    private void verifySupportedDataProviders() {
        verifySourceDataProvider();
        verifyTargetDataProvider();
    }

    @SuppressWarnings("unchecked")
    private void verifySourceDataProvider() {
        if (getSourceDataProviderUpdater() != null) {
            return; // custom updater is always fine
        }

        if (!(getSourceDataProvider() instanceof ListDataProvider)) {
            throwUnsupportedOperationExceptionForUnsupportedDataProvider(true);
        }

        if (!(((ListDataProvider<T>) getSourceDataProvider())
                .getItems() instanceof List)) {
            throwUnsupportedOperationExceptionForUnsupportedCollectionInListDataProvider(
                    true);
        }
    }

    @SuppressWarnings("unchecked")
    private void verifyTargetDataProvider() {
        if (getTargetDataProviderUpdater() != null
                && getDropIndexCalculator() != null) {
            return; // custom updater and calculator is always fine
        }

        if (!(getTargetDataProvider() instanceof ListDataProvider)) {
            throwUnsupportedOperationExceptionForUnsupportedDataProvider(false);
        }

        if (!(((ListDataProvider<T>) getTargetDataProvider())
                .getItems() instanceof List)) {
            throwUnsupportedOperationExceptionForUnsupportedCollectionInListDataProvider(
                    false);
        }
    }

    private DataProvider<T, ?> getSourceDataProvider() {
        return getGridDragSource().getGrid().getDataProvider();
    }

    private DataProvider<T, ?> getTargetDataProvider() {
        return getGridDropTarget().getGrid().getDataProvider();
    }

    private static void throwUnsupportedOperationExceptionForUnsupportedDataProvider(
            boolean sourceGrid) {
        throw new UnsupportedOperationException(new StringBuilder()
                .append(sourceGrid ? "Source " : "Target ")
                .append("grid does not have a ListDataProvider, cannot automatically ")
                .append(sourceGrid ? "remove " : "add ")
                .append("items. Use GridRowDragger.set")
                .append(sourceGrid ? "Source" : "Target")
                .append("DataProviderUpdater(...) ")
                .append(sourceGrid ? ""
                        : "and setDropIndexCalculator(...) "
                                + "to customize how to handle updating the data provider.")
                .toString());
    }

    private static void throwUnsupportedOperationExceptionForUnsupportedCollectionInListDataProvider(
            boolean sourceGrid) {
        throw new UnsupportedOperationException(new StringBuilder()
                .append(sourceGrid ? "Source " : "Target ")
                .append("grid's ListDataProvider is not backed by a List-collection, cannot ")
                .append(sourceGrid ? "remove " : "add ")
                .append("items. Use a ListDataProvider backed by a List, or use GridRowDragger.set")
                .append(sourceGrid ? "Source" : "Target")
                .append("DataProviderUpdater(...) ")
                .append(sourceGrid ? "" : "and setDropIndexCalculator(...) ")
                .append(" to customize how to handle updating the data provider to customize how to handle updating the data provider.")
                .toString());
    }

}
