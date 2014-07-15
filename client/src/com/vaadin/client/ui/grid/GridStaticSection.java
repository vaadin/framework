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
import java.util.List;

import com.vaadin.client.ui.grid.GridStaticSection.StaticRow;
import com.vaadin.client.ui.grid.renderers.TextRenderer;

/**
 * Abstract base class for Grid header and footer sections.
 * 
 * @since
 * @author Vaadin Ltd
 * @param <ROWTYPE>
 *            the type of the rows in the section
 */
abstract class GridStaticSection<ROWTYPE extends StaticRow<?>> {

    /**
     * A header or footer cell. Has a simple textual caption.
     * 
     * TODO HTML content
     * 
     * TODO Widget content
     */
    static class StaticCell {

        private String text = "";

        /**
         * Sets the text displayed in this cell.
         *
         * @param text
         *            a plain text caption
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         * Returns the text displayed in this cell.
         * 
         * @return the plain text caption
         */
        public String getText() {
            return text;
        }
    }

    /**
     * Abstract base class for Grid header and footer rows.
     * 
     * @param <CELLTYPE>
     *            the type of the cells in the row
     */
    abstract static class StaticRow<CELLTYPE extends StaticCell> {

        private List<CELLTYPE> cells = new ArrayList<CELLTYPE>();

        private Renderer<String> renderer = new TextRenderer();

        /**
         * Returns the cell at the given position in this row.
         * 
         * @param index
         *            the position of the cell
         * @return the cell at the index
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         */
        public CELLTYPE getCell(int index) {
            return cells.get(index);
        }

        protected void addCell(int index) {
            cells.add(index, createCell());
        }

        protected void removeCell(int index) {
            cells.remove(index);
        }

        protected Renderer<String> getRenderer() {
            return renderer;
        }

        protected abstract CELLTYPE createCell();
    }

    private List<ROWTYPE> rows = createRowList();

    /**
     * Returns the row at the given position in this section.
     * 
     * @param index
     *            the position of the row
     * @return the row at the index
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds
     */
    public ROWTYPE getRow(int index) {
        return rows.get(index);
    }

    protected List<ROWTYPE> getRows() {
        return rows;
    }

    protected void addColumn(GridColumn<?, ?> column, int index) {
        for (ROWTYPE row : getRows()) {
            row.addCell(index);
        }
    }

    protected void removeColumn(int index) {
        for (ROWTYPE row : getRows()) {
            row.removeCell(index);
        }
    }

    protected List<ROWTYPE> createRowList() {
        return new ArrayList<ROWTYPE>();
    }

    protected abstract ROWTYPE createRow();
}
