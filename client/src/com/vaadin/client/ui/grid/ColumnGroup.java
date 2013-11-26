/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Column groups are used to group columns together for adding common auxiliary
 * headers and footers. Columns groups are added to {@link ColumnGroupRow
 * ColumnGroupRows}.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class ColumnGroup<T> {

    /**
     * The text shown in the header
     */
    private String header;

    /**
     * The text shown in the footer
     */
    private String footer;

    /**
     * The columns included in the group when also accounting for subgroup
     * columns
     */
    private final List<GridColumn<?, T>> columns;

    /**
     * The grid associated with the column group
     */
    private final Grid grid;

    /**
     * Constructs a new column group
     */
    ColumnGroup(Grid grid, Collection<GridColumn<?, T>> columns) {
        if (columns == null) {
            throw new IllegalArgumentException(
                    "columns cannot be null. Pass an empty list instead.");
        }
        this.grid = grid;
        this.columns = Collections
                .unmodifiableList(new ArrayList<GridColumn<?, T>>(columns));
    }

    /**
     * Gets the header text.
     * 
     * @return the header text
     */
    public String getHeaderCaption() {
        return header;
    }

    /**
     * Sets the text shown in the header.
     * 
     * @param header
     *            the header to set
     */
    public void setHeaderCaption(String header) {
        this.header = header;
        grid.refreshHeader();
    }

    /**
     * Gets the text shown in the footer.
     * 
     * @return the text in the footer
     */
    public String getFooterCaption() {
        return footer;
    }

    /**
     * Sets the text displayed in the footer.
     * 
     * @param footer
     *            the footer to set
     */
    public void setFooterCaption(String footer) {
        this.footer = footer;
        grid.refreshFooter();
    }

    /**
     * Returns all column in this group. It includes the subgroups columns as
     * well.
     * 
     * @return unmodifiable list of columns
     */
    public List<GridColumn<?, T>> getColumns() {
        return columns;
    }
}
