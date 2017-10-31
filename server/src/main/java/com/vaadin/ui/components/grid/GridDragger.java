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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;

/**
 * Allows dragging rows for reordering within a Grid and between separate Grids.
 * <p>
 * When dragging a selected row, all the visible selected rows are dragged. Note
 * that ONLY visible rows are taken into account.
 * <p>
 * <em>NOTE: this helper works only with {@link ListDataProvider} on both grids.
 * If you have another data provider, you should customize data provider
 * updating on drop with
 * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} and
 * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)}.</em>
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd
 * @since
 */
public class GridDragger<T> implements Serializable {

    private final GridDropTarget<T> gridDropTarget;
    private final GridDragSource<T> gridDragSource;

    private DropIndexCalculator<T> dropTargetIndexCalculator = null;
    private SourceDataProviderUpdater<T> sourceDataProviderUpdater = null;
    private TargetDataProviderUpdater<T> targetDataProviderUpdater = null;

    /**
     * Set of items currently being dragged.
     */
    private Set<T> draggedItems;
    private int shiftedDropIndex;

    /**
     * Enables DnD reordering for the rows in the given grid.
     * <p>
     * {@link DropMode#BETWEEN} is used.
     *
     * @param grid
     *            Grid to be extended.
     */
    public GridDragger(Grid<T> grid) {
        this(grid, grid, DropMode.BETWEEN);
    }

    /**
     * Enables DnD reordering the rows in the given grid with the given drop
     * mode.
     * <p>
     * <em>NOTE: this only works when the grid has a
     * {@link ListDataProvider}.</em> Use the custom handlers
     * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} and
     * {@link #setTargetDataProviderUpdater(TargetDataProviderUpdater)} for
     * other data providers.
     *
     * @param grid
     *            the grid to enable row DnD reordering on
     * @param dropMode
     *            DropMode to be used.
     */
    public GridDragger(Grid<T> grid, DropMode dropMode) {
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
    public GridDragger(Grid<T> source, Grid<T> target) {
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
    public GridDragger(Grid<T> source, Grid<T> target,
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
    public GridDragger(Grid<T> source, Grid<T> target, DropMode dropMode) {
        gridDragSource = new GridDragSource<>(source);

        gridDropTarget = new GridDropTarget<>(target, dropMode);

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
     * @return the currenytly dragged items or {@code null}
     */
    public Set<T> getDraggedItems() {
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

        if (!(source.getDataProvider() instanceof ListDataProvider)) {
            throwIllegalStateExceptionForUnsupportedDataProvider(true);
        }
        ListDataProvider<T> listDataProvider = (ListDataProvider<T>) source
                .getDataProvider();

        // use the existing data source to keep filters and sort orders etc. in
        // place
        Collection<T> sourceItems = listDataProvider.getItems();

        // if reordering the same grid and dropping on top of one of the dragged
        // rows, need to calculate the new drop index before removing the items
        if (getGridDragSource().getGrid() == getGridDropTarget().getGrid()
                && sourceItems instanceof List
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
        listDataProvider.refreshAll();
    }

    private void handleTargetGridDrop(GridDropEvent event, final int index,
            Collection<T> droppedItems) {
        Grid<T> target = getGridDropTarget().getGrid();

        if (getTargetDataProviderUpdater() != null) {
            getTargetDataProviderUpdater().onDrop(event.getDropEffect(),
                    target.getDataProvider(), index, droppedItems);
            return;
        }

        if (!(target.getDataProvider() instanceof ListDataProvider)) {
            throwIllegalStateExceptionForUnsupportedDataProvider(false);
        }
        ListDataProvider<T> listDataProvider = (ListDataProvider<T>) target
                .getDataProvider();
        // update the existing to keep filters etc.
        Collection<T> targetItems = listDataProvider.getItems();

        // If the user has create the list data provider by hand, then it
        // can potentially have something else than a list
        if (index != Integer.MAX_VALUE && targetItems instanceof List) {
            ((List<T>) targetItems).addAll(index, droppedItems);
        } else {
            // for default we just add to the end
            targetItems.addAll(droppedItems);
        }
        // instead of using setItems or creating a new data provider,
        // refresh the existing one to keep filters etc. in place
        listDataProvider.refreshAll();
    }

    private int calculateDropIndex(GridDropEvent<T> event) {
        // use custom calculator if present
        if (getDropIndexCalculator() != null) {
            return getDropIndexCalculator().calculateDropIndex(event);
        }
        // always drop to end if no custom calculator and no list data provider
        // used
        if (!(getGridDropTarget().getGrid()
                .getDataProvider() instanceof ListDataProvider)) {
            return Integer.MAX_VALUE;
        }
        // if the source and target grids are the same, then the index has been
        // calculated before removing the items
        if (shiftedDropIndex != -1) {
            return shiftedDropIndex;
        }

        ListDataProvider<T> targetDataProvider = (ListDataProvider<T>) getGridDropTarget()
                .getGrid().getDataProvider();
        Collection<T> items = targetDataProvider.getItems();
        // drop to end by default if collection doesn't support indexOf
        int index = items.size();
        if (items instanceof List) {
            Optional<T> dropTargetRow = event.getDropTargetRow();
            if (dropTargetRow.isPresent()) {
                if (getGridDragSource().getGrid() == getGridDragSource()
                        .getGrid()
                        && getDraggedItems().contains(dropTargetRow.get())) {

                } else {
                    index = ((List<T>) items).indexOf(dropTargetRow.get())
                            + (event.getDropLocation() == DropLocation.BELOW ? 1
                                    : 0);
                }
            }
        }

        return index;
    }

    private static void throwIllegalStateExceptionForUnsupportedDataProvider(
            boolean sourceGrid) {
        throw new IllegalStateException(
                new StringBuilder().append(sourceGrid ? "Source " : "Target ")
                        .append("grid does not have a ListDataProvider, cannot automatically ")
                        .append(sourceGrid ? "remove " : "add ")
                        .append("items. Use GridDragger.set")
                        .append(sourceGrid ? "Source" : "Target")
                        .append("DataProviderUpdater(...) to customize how to handle updating the data provider.")
                        .toString());
    }

}
