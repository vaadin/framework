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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private boolean removeFromSource = true;

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

        gridDropTarget.addGridDropListener(event -> {
            if (removeFromSource) {
                handleSourceGridDrop(draggedItems);
            }

            int index = calculateDropIndex(event);
            handleTargetGridDrop(index, draggedItems);
        });
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
    public SourceDataProviderUpdater<T> getSourceGridDropHandler() {
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
     * Returns whether dropped items are removed from the source grid or not.
     * <p>
     * Default value is {@code true} and the dropped items are removed from the
     * source grid.
     * <p>
     * <em>NOTE: when this is set to {@code false}, any custom handler with
     * {@link #setSourceDataProviderUpdater(SourceDataProviderUpdater)} is not
     * triggered on drop.</em>
     *
     * @return {@code true} to remove dropped items, {@code false} to not
     *         remove.
     */
    public boolean isRemoveItemsFromSource() {
        return removeFromSource;
    }

    private void handleSourceGridDrop(final Collection<T> droppedItems) {
        Grid<T> source = getGridDragSource().getGrid();
        if (getSourceGridDropHandler() == null) {
            if (!(source.getDataProvider() instanceof ListDataProvider)) {
                throwIllegalStateExceptionForUnsupportedDataProvider(true);
            }
            ListDataProvider<T> listDataProvider = (ListDataProvider<T>) source
                    .getDataProvider();
            List<T> sourceItems = new ArrayList<>(listDataProvider.getItems());
            sourceItems.removeAll(droppedItems);
            source.setItems(sourceItems);
        } else {
            getSourceGridDropHandler().removeItems(source.getDataProvider(),
                    droppedItems);
        }
    }

    private void handleTargetGridDrop(final int index,
            Collection<T> droppedItems) {
        Grid<T> target = getGridDropTarget().getGrid();
        if (targetDataProviderUpdater == null) {
            if (!(target.getDataProvider() instanceof ListDataProvider)) {
                throwIllegalStateExceptionForUnsupportedDataProvider(false);
            }
            ListDataProvider<T> listDataProvider = (ListDataProvider<T>) target
                    .getDataProvider();
            List<T> targetItems = new ArrayList<>(listDataProvider.getItems());

            // DropIndexCalculator can return this to make sure things are added
            // to end
            if (index == Integer.MAX_VALUE) {
                targetItems.addAll(droppedItems);
            } else {
                targetItems.addAll(index, droppedItems);
            }
            target.setItems(targetItems);
        } else {
            getTargetDataProviderUpdater().onDrop(target.getDataProvider(),
                    index, droppedItems);
        }
    }

    private int calculateDropIndex(GridDropEvent<T> event) {
        if (getDropIndexCalculator() == null) {
            ListDataProvider<T> targetDataProvider = (ListDataProvider<T>) getGridDropTarget()
                    .getGrid().getDataProvider();
            List<T> items = new ArrayList<>(targetDataProvider.getItems());
            int index = items.size();
            if (event.getDropTargetRow().isPresent()) {
                index = items.indexOf(event.getDropTargetRow().get())
                        + (event.getDropLocation() == DropLocation.BELOW ? 1
                                : 0);
            }
            return index;
        } else {
            return getDropIndexCalculator().calculateDropIndex(event);
        }
    }

    private static void throwIllegalStateExceptionForUnsupportedDataProvider(
            boolean sourceGrid) {
        throw new IllegalStateException(
                new StringBuilder().append(sourceGrid ? "Source " : "Target ")
                        .append("grid does not have a ListDataProvider, cannot automatically ")
                        .append(sourceGrid ? "remove " : "add ")
                        .append("items. Use GridDragger.set")
                        .append(sourceGrid ? "Source" : "Target")
                        .append("GridDropHandler(...) to customize how to handle updating the data provider.")
                        .toString());
    }

}
