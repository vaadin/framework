/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.components.grid.sort.SortOrder;

/**
 * Event fired by {@link Grid} when the sort order has changed.
 * 
 * @see SortOrderChangeListener
 * 
 * @since
 * @author Vaadin Ltd
 */
public class SortOrderChangeEvent extends Component.Event {

    private final List<SortOrder> sortOrder;

    /**
     * Creates a new sort order change event for a grid and a sort order list.
     * 
     * @param grid
     *            the grid from which the event originates
     * @param sortOrder
     *            the new sort order list
     */
    public SortOrderChangeEvent(Grid grid, List<SortOrder> sortOrder) {
        super(grid);
        this.sortOrder = sortOrder;
    }

    /**
     * Gets the sort order list.
     * 
     * @return the sort order list
     */
    public List<SortOrder> getSortOrder() {
        return sortOrder;
    }

}
