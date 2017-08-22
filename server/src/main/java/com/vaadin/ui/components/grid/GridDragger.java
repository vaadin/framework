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

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Allows dragging rows for reordering within a Grid and between separate Grids.
 * <p>
 * When dragging a selected row, all the visible selected rows are dragged. Note
 * that ONLY visible rows are taken into account.
 *
 * @param <T> The Grid bean type.
 * @author Stephan Knitelius
 * @since 8.1
 */
public class GridDragger<T> implements Serializable {

    private final GridDropTarget<T> gridDropTarget;
    private final GridDragSource<T> gridDragSource;

    private GridDropTargetIndex gridDropTargetIndex = null;
    private GridSourceWriter<T> gridSourceWriter = null;
    private GridTargetWriter<T> gridTargetWriter = null;

    /**
     * Set of items currently being dragged.
     */
    private Set<T> draggedItems;
    private boolean addToEnd = false;
    private boolean removeFromSource = true;

    /**
     * Extends a Grid and makes it's row orderable by dragging entries up or
     * down.
     *
     * @param grid Grid to be extended.
     */
    public GridDragger(Grid<T> grid) {
        this(grid, grid, DropMode.ON_TOP_OR_BETWEEN);
    }

    /**
     * Extends the Grid and makes it's row orderable by dragging entries up or
     * down.
     *
     * @param grid Grid to be extended.
     * @param dropMode DropMode to be used.
     */
    public GridDragger(Grid<T> grid, DropMode dropMode) {
        this(grid, grid, dropMode);
    }

    /**
     * Extends the source and target grid so that rows can be dragged from the
     * source to the target grid.
     *
     * @param source Grid dragged from.
     * @param target Grid dropped to.
     */
    public GridDragger(Grid<T> source, Grid<T> target) {
        this(source, target, DropMode.ON_TOP_OR_BETWEEN);
    }

    /**
     * Extends the grid so that items can be reordered, use the gridTargetWriter
     * to write to non-standard DataProvider.
     *
     * @param grid Grid to be reorderable.
     * @param gridTargetWriter callback for writing to custom DataProvider.
     */
    public GridDragger(Grid<T> grid, GridTargetWriter gridTargetWriter) {
        this(grid, grid, gridTargetWriter, null);
    }

    /**
     * Extends the source and target grid so that items can be reordered, use
     * the gridTargetWriter to write to non-standard DataProviders.
     *
     * @param source Grid dragged from.
     * @param target Grid dragged to.
     * @param gridTargetWriter callback for writing to custom target
     * DataProvider.
     */
    public GridDragger(Grid<T> source, Grid<T> target, GridTargetWriter gridTargetWriter) {
        this(source, target, gridTargetWriter, null);
    }

    /**
     * Extends the source and target grid so that items can be reordered, use
     * the gridTargetWriter to write to non-standard DataProviders and
     * gridSourceWriter to update source Grid.
     *
     * @param source Grid dragged from.
     * @param target Grid dragged to.
     * @param gridTargetWriter callback for writing to custom target
     * DataProvider.
     * @param gridSourceWriter callback for updating custom source DataProvider.
     */
    public GridDragger(Grid<T> source, Grid<T> target, GridTargetWriter gridTargetWriter, GridSourceWriter gridSourceWriter) {
        this(source, target, DropMode.ON_TOP_OR_BETWEEN);
        this.gridTargetWriter = gridTargetWriter;
        this.gridSourceWriter = gridSourceWriter;
    }

    /**
     * Extends a the source and target grid so that rows can be dragged from the
     * source to the target grid.
     *
     * @param target Grid to be extended.
     * @param dropMode DropMode to be used.
     */
    public GridDragger(Grid<T> source, Grid<T> target, DropMode dropMode) {
        checkAndInitalizeGridWriter(source, target);

        gridDragSource = new GridDragSource(source);

        gridDropTarget = new GridDropTarget(target, dropMode);

        gridDragSource.addGridDragStartListener(event -> {
            draggedItems = event.getDraggedItems();
        });

        gridDropTarget.addGridDropListener(event -> {
            if (removeFromSource) {
                gridSourceWriter.removeItems(draggedItems);
            }

            int index = gridDropTargetIndex.calculateDropIndex(event);
            gridTargetWriter.addItems(index, draggedItems);
        });
    }

    public void setGridTargetWriter(GridTargetWriter<T> gridTargetWriter) {
        this.gridTargetWriter = gridTargetWriter;
    }

    public void setGridSourceWriter(GridSourceWriter<T> gridSourceWriter) {
        this.gridSourceWriter = gridSourceWriter;
    }

    public void setGrid(GridDropTargetIndex<T> gridDropTargetIndex) {
        this.gridDropTargetIndex = gridDropTargetIndex;
    }

    /**
     * Exposes the GridDropTarget to perform customizations such as
     * DropEffect.MOVE.
     */
    public GridDropTarget<T> getGridDropTarget() {
        return gridDropTarget;
    }

    /**
     * Exposes the GridDragSource for customizations.
     */
    public GridDragSource<T> getGridDragSource() {
        return gridDragSource;
    }

    /**
     * By default items are dropped into the selected position. Set addToEnd
     * will add the items to the end of the grid instead.
     *
     * @param addToEnd add items to end of Grid.
     */
    public void setAddToEnd(boolean addToEnd) {
        this.addToEnd = addToEnd;
    }

    /**
     * By default the dragged Items are removed from the source Grid.
     *
     * @param removeFromSource set to false to keep items in source Grid.
     */
    public void removeFromSourceGrid(boolean removeFromSource) {
        this.removeFromSource = removeFromSource;
    }

    /**
     * Checks if custom implementations have been set otherwise the default
     * ListDataProvider implementation is used.
     */
    private void checkAndInitalizeGridWriter(final Grid<T> source, final Grid<T> target) {
        if (gridSourceWriter == null) {
            this.gridSourceWriter = (items) -> {
                ListDataProvider listDataProvider = (ListDataProvider) source.getDataProvider();
                List<T> sourceItems = new ArrayList(listDataProvider.getItems());
                sourceItems.removeAll(items);
                source.setItems(sourceItems);
            };
        }
        if (gridDropTargetIndex == null) {
            this.gridDropTargetIndex = event -> {
                if (!addToEnd) {
                    ListDataProvider targetDataProvider = (ListDataProvider) target.getDataProvider();
                    List<T> items = new ArrayList(targetDataProvider.getItems());
                    int index = items.size();
                    if (event.getDropTargetRow().isPresent()) {
                        index = items.indexOf(event.getDropTargetRow().get())
                                + (event.getDropLocation() == DropLocation.BELOW ? 1 : 0);
                    }
                    return index;
                }
                return Integer.MAX_VALUE;
            };
        }
        if (gridTargetWriter == null) {
            this.gridTargetWriter = (index, items) -> {
                ListDataProvider listDataProvider = (ListDataProvider) target.getDataProvider();
                List<T> targetItems = new ArrayList(listDataProvider.getItems());
                targetItems.addAll(index, items);
                target.setItems(targetItems);
            };
        }
    }
}
