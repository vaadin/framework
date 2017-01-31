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
 * A footer row in a Grid.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public interface FooterRow extends Serializable {

    /**
     * Returns the cell on this row corresponding to the given column id.
     *
     * @see Column#setId(String)
     *
     * @param columnId
     *            the id of the column whose footer cell to get, not null
     * @return the footer cell
     * @throws IllegalArgumentException
     *             if there is no such column in the grid
     */
    public FooterCell getCell(String columnId);

    /**
     * Returns the cell on this row corresponding to the given column.
     *
     * @param column
     *            the column whose footer cell to get, not null
     * @return the footer cell
     * @throws IllegalArgumentException
     *             if there is no such column in the grid
     */
    public FooterCell getCell(Grid.Column<?, ?> column);

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
     * @see #join(FooterCell...)
     * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
     */
    FooterCell join(Set<FooterCell> cellsToMerge);

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
    FooterCell join(FooterCell... cellsToMerge);
}
