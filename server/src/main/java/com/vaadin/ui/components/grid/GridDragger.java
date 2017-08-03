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
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Allows dragging rows for reording within a Grid and between seperate Grids.
 * <p>
 * When dragging a selected row, all the visible selected rows are dragged. Note that ONLY visible rows are taken into account.
 *
 * @param <T> The Grid bean type.
 * @author Stephan Knitelius
 * @since 8.1
 */
public class GridDragger<T> implements Serializable {

    /**
     * Drop target grid.
     */
    private final GridDropTarget<T> gridDropTarget;

    /**
     * Source drag grid.
     */
    private final GridDragSource<T> gridDragSource;

    /**
     * Items that are currently dragged.
     */
    private Set<T> draggedItems;

    /**
     * Extends a Grid and makes it's row ordrable by dragging entries up or down.
     *
     * @param grid Grid to be extended.
     */
    public GridDragger(Grid<T> grid) {
        this(grid, grid, DropMode.ON_TOP_OR_BETWEEN);
    }

    /**
     * Extends a Grid and makes it's row ordrable by dragging entries up or down.
     *
     * @param grid Grid to be extended.
     * @param dropMode DropMode to be used.
     */
    public GridDragger(Grid<T> grid, DropMode dropMode) {
        this(grid, grid, dropMode);
    }

    /**
     * Extends a the source and target grid so that rows can be dragged from the source to the target grid.
     *
     * @param target Grid to be extended.
     */
    public GridDragger(Grid<T> source, Grid<T> target) {
        this(source, target, DropMode.ON_TOP_OR_BETWEEN);
    }

    /**
     * Extends a the source and target grid so that rows can be dragged from the source to the target grid.
     *
     * @param target Grid to be extended.
     * @param dropMode DropMode to be used.
     */
    public GridDragger(Grid<T> source, Grid<T> target, DropMode dropMode) {
        gridDragSource = new GridDragSource(source);

        gridDropTarget = new GridDropTarget(target, dropMode);
        gridDropTarget.setDropEffect(DropEffect.MOVE);

        gridDragSource.addGridDragStartListener(event -> {
            draggedItems = event.getDraggedItems();
        });

        gridDropTarget.addGridDropListener(event -> {
            ListDataProvider sourceDataProvider = (ListDataProvider) source.getDataProvider();
            List<T> sourceItems = new ArrayList(sourceDataProvider.getItems());
            sourceItems.removeAll(draggedItems);
            source.setItems(sourceItems);

            ListDataProvider targetDataProvider = (ListDataProvider) target.getDataProvider();
            List<T> items = new ArrayList(targetDataProvider.getItems());
            int index = items.size();
            if (event.getDropTargetRow().isPresent()) {
                index = items.indexOf(event.getDropTargetRow().get())
                        + (event.getDropLocation() == DropLocation.BELOW ? 1 : 0);
            }

            items.addAll(index, draggedItems);

            source.setItems(items);
        });
    }

    /**
     * Gets the GridDropTarget extension, for the target grid.
     */
    public GridDropTarget<T> getGridDropTarget() {
        return gridDropTarget;
    }

    /**
     * Gets the GridDragSource extension, for the source grid.
     */
    public GridDragSource<T> getGridDragSource() {
        return gridDragSource;
    }
    
}
