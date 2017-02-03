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
import java.util.Set;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

/**
 * A header row in a Grid.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public interface HeaderRow extends Serializable {

    /**
     * Returns the cell on this row corresponding to the given column id.
     *
     * @see Column#setId(String)
     *
     * @param columnId
     *            the id of the column whose header cell to get, not null
     * @return the header cell
     * @throws IllegalArgumentException
     *             if there is no such column in the grid
     */
    public HeaderCell getCell(String columnId);

    /**
     * Returns the cell on this row corresponding to the given column.
     *
     * @param column
     *            the column whose header cell to get, not null
     * @return the header cell
     * @throws IllegalArgumentException
     *             if there is no such column in the grid
     */
    public HeaderCell getCell(Grid.Column<?, ?> column);

    /**
     * Merges column cells in the row. Original cells are hidden, and new merged
     * cell is shown instead. The cell has a width of all merged cells together,
     * inherits styles of the first merged cell but has empty caption.
     *
     * @param cellsToMerge
     *            the cells which should be merged. The cells should not be
     *            merged to any other cell set.
     * @return the remaining visible cell after the merge
     *
     * @see #join(HeaderCell...)
     * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
     */
    HeaderCell join(Set<HeaderCell> cellsToMerge);

    /**
     * Merges column cells in the row. Original cells are hidden, and new merged
     * cell is shown instead. The cell has a width of all merged cells together,
     * inherits styles of the first merged cell but has empty caption.
     *
     * @param cellsToMerge
     *            the cells which should be merged. The cells should not be
     *            merged to any other cell set.
     * @return the remaining visible cell after the merge
     *
     * @see #join(Set)
     * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
     */
    HeaderCell join(HeaderCell... cellsToMerge);

    /**
     * Merges cells corresponding to the given columns in the row. Original
     * cells are hidden, and new merged cell is shown instead. The cell has a
     * width of all merged cells together, inherits styles of the first merged
     * cell but has empty caption.
     *
     * @param columnsToMerge
     *            the columns of the cells that should be merged. The cells
     *            should not be merged to any other cell set.
     * @return the remaining visible cell after the merge
     *
     * @see #join(Set)
     * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
     */
    HeaderCell join(Grid.Column<?, ?>... columnsToMerge);

    /**
     * Merges cells corresponding to the given column ids in the row. Original
     * cells are hidden, and new merged cell is shown instead. The cell has a
     * width of all merged cells together, inherits styles of the first merged
     * cell but has empty caption.
     *
     * @param columnIdsToMerge
     *            the ids of the columns of the cells that should be merged. The
     *            cells should not be merged to any other cell set.
     * @return the remaining visible cell after the merge
     *
     * @see #join(Set)
     * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
     * @see Column#setId(String)
     */
    HeaderCell join(String... columnIdsToMerge);
}
