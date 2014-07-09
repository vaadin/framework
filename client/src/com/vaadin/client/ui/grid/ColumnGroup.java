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

package com.vaadin.client.ui.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.client.ui.grid.renderers.TextRenderer;

/**
 * A column group is a horizontal grouping of columns in a header or footer.
 * Columns groups are added to {@link ColumnGroupRow ColumnGroupRows}.
 * 
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since
 * @author Vaadin Ltd
 * @see ColumnGroupRow#addGroup(ColumnGroup...)
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
     * Renders the header cells for the column group
     */
    private Renderer<String> headerRenderer = new TextRenderer();

    /**
     * Renders the footer cells for the column group
     */
    private Renderer<String> footerRenderer = new TextRenderer();

    /**
     * The columns included in the group when also accounting for subgroup
     * columns
     */
    private final List<GridColumn<?, T>> columns;

    /**
     * The grid associated with the column group
     */
    private final Grid<T> grid;

    /**
     * Constructs a new column group
     */
    ColumnGroup(Grid<T> grid, Collection<GridColumn<?, T>> columns) {
        if (columns == null) {
            throw new IllegalArgumentException(
                    "columns cannot be null. Pass an empty list instead.");
        }
        this.grid = grid;
        this.columns = Collections
                .unmodifiableList(new ArrayList<GridColumn<?, T>>(columns));
    }

    /**
     * Gets the text shown in the header.
     * 
     * @return the header text
     */
    public String getHeaderCaption() {
        return header;
    }

    /**
     * Sets the text shown in the header.
     * 
     * @param caption
     *            the caption to set
     */
    public void setHeaderCaption(String caption) {
        this.header = caption;
        grid.refreshHeader();
    }

    /**
     * Gets the text shown in the footer.
     * 
     * @return the footer text
     */
    public String getFooterCaption() {
        return footer;
    }

    /**
     * Sets the text shown in the footer.
     * 
     * @param caption
     *            the caption to set
     */
    public void setFooterCaption(String caption) {
        this.footer = caption;
        grid.refreshFooter();
    }

    /**
     * Returns all columns in this group. This includes columns in all the
     * sub-groups as well.
     * 
     * @return an unmodifiable list of columns
     */
    public List<GridColumn<?, T>> getColumns() {
        return columns;
    }

    /**
     * Returns the renderer for the header cells.
     * 
     * @return the renderer for the header cells
     */
    public Renderer<String> getHeaderRenderer() {
        return headerRenderer;
    }

    /**
     * Sets the renderer for the header cells.
     * 
     * @param renderer
     *            a non-{@code null} renderer to use for the header cells.
     * @throws IllegalArgumentException
     *             if {@code renderer} is {@code null}
     */
    public void setHeaderRenderer(Renderer<String> renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer cannot be null.");
        }
        this.headerRenderer = renderer;
        grid.refreshHeader();
    }

    /**
     * Returns the renderer for the footer cells.
     * 
     * @return the renderer for the footer cells
     */
    public Renderer<String> getFooterRenderer() {
        return footerRenderer;
    }

    /**
     * Sets the renderer that renders footer cells.
     * 
     * @param renderer
     *            a non-{@code null} renderer for footer cells.
     * @throws IllegalArgumentException
     *             if {@code renderer} is {@code null}
     */
    public void setFooterRenderer(Renderer<String> renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer cannot be null.");
        }
        this.footerRenderer = renderer;
        grid.refreshFooter();
    }
}
