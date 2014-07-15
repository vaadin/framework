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
import java.util.Arrays;
import java.util.List;

import com.vaadin.client.ui.grid.renderers.TextRenderer;

/**
 * Represents the header section of a Grid. A header consists of a single header
 * row containing a header cell for each column. Each cell has a simple textual
 * caption.
 * 
 * TODO Arbitrary number of header rows (zero included)
 * 
 * TODO Merging header cells
 * 
 * TODO "Default" row with sorting
 * 
 * TODO Widgets in cells
 * 
 * TODO HTML in cells
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridHeader {

    /**
     * A single row in a grid header section.
     * 
     * @since
     * @author Vaadin Ltd
     */
    public static class HeaderRow {

        private List<HeaderCell> cells = new ArrayList<HeaderCell>();

        private Renderer<String> renderer = new TextRenderer();

        public HeaderCell getCell(int index) {
            return cells.get(index);
        }

        protected void addCell(int index) {
            cells.add(index, new HeaderCell());
        }

        protected void removeCell(int index) {
            cells.remove(index);
        }

        protected Renderer<String> getRenderer() {
            return renderer;
        }
    }

    /**
     * A single cell in a grid header row. Has a textual caption.
     * 
     * @since
     * @author Vaadin Ltd
     */
    public static class HeaderCell {

        private String text = "";

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private List<HeaderRow> rows = Arrays.asList(new HeaderRow());

    public HeaderRow getRow(int index) {
        return rows.get(index);
    }

    protected void addColumn(GridColumn<?, ?> column, int index) {
        getRow(0).addCell(index);
    }

    protected void removeColumn(int index) {
        getRow(0).removeCell(index);
    }
}
