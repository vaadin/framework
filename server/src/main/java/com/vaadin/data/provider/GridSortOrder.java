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
package com.vaadin.data.provider;

import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid.Column;

/**
 * Sorting information for {@link Grid}.
 *
 * @param <T>
 *            the grid type
 * @since 8.0
 */
public class GridSortOrder<T> extends SortOrder<Column<T, ?>> {

    /**
     * Construct sorting information for usage in a {@link Grid}.
     *
     * @param column
     *            the column to be sorted
     * @param direction
     *            sorting direction
     */
    public GridSortOrder(Column<T, ?> column, SortDirection direction) {
        super(column, direction);
    }

    /**
     * Gets the column this sorting information is attached to.
     *
     * @return the column being sorted
     */
    @Override
    public Column<T, ?> getSorted() {
        return super.getSorted();
    }

    /**
     * Creates a new grid sort builder with given sorting using ascending sort
     * direction.
     *
     * @param by
     *            the column to sort by
     * @param <T>
     *            the grid type
     *
     * @return the grid sort builder
     */
    public static <T> GridSortOrderBuilder<T> asc(Column<T, ?> by) {
        return new GridSortOrderBuilder<T>().thenAsc(by);
    }

    /**
     * Creates a new grid sort builder with given sorting using descending sort
     * direction.
     *
     * @param by
     *            the column to sort by
     * @param <T>
     *            the grid type
     *
     * @return the grid sort builder
     */
    public static <T> GridSortOrderBuilder<T> desc(Column<T, ?> by) {
        return new GridSortOrderBuilder<T>().thenDesc(by);
    }
}
