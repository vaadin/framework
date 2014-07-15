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

/**
 * Represents the header section of a Grid. A header consists of a single header
 * row containing a header cell for each column. Each cell has a simple textual
 * caption.
 * 
 * TODO Arbitrary number of header rows (zero included, one by default)
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
public class GridHeader extends GridStaticSection<GridHeader.HeaderRow> {

    /**
     * A single row in a grid header section.
     * 
     */
    public class HeaderRow extends GridStaticSection.StaticRow<HeaderCell> {

        @Override
        protected HeaderCell createCell() {
            return new HeaderCell();
        }
    }

    /**
     * A single cell in a grid header row. Has a textual caption.
     * 
     */
    public class HeaderCell extends GridStaticSection.StaticCell {
    }

    @Override
    protected HeaderRow createRow() {
        return new HeaderRow();
    }

    @Override
    protected void refreshGrid() {
        getGrid().refreshHeader();
    }
}
