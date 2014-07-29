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

import com.vaadin.shared.ui.grid.GridStaticSectionState;

/**
 * Represents the header section of a Grid.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridHeader extends GridStaticSection<GridHeader.HeaderRow> {

    public class HeaderRow extends GridStaticSection.StaticRow<HeaderCell> {

        protected HeaderRow(GridStaticSection<?> section) {
            super(section);
        }

        private void setDefaultRow(boolean value) {
            getRowState().defaultRow = value;
        }

        @Override
        protected HeaderCell createCell() {
            return new HeaderCell(this);
        }
    }

    public class HeaderCell extends GridStaticSection.StaticCell<HeaderRow> {

        protected HeaderCell(HeaderRow row) {
            super(row);
        }
    }

    private HeaderRow defaultRow = null;
    private final GridStaticSectionState headerState = new GridStaticSectionState();

    protected GridHeader(Grid grid) {
        this.grid = grid;
        grid.getState(true).header = headerState;
        HeaderRow row = createRow();
        rows.add(row);
        setDefaultRow(row);
        getState().rows.add(row.getRowState());
    }

    /**
     * Sets the default row of this header. The default row is a special header
     * row providing a user interface for sorting columns.
     * 
     * @param row
     *            the new default row, or null for no default row
     * 
     * @throws IllegalArgumentException
     *             this header does not contain the row
     */
    public void setDefaultRow(HeaderRow row) {
        if (row == defaultRow) {
            return;
        }

        if (row != null && !rows.contains(row)) {
            throw new IllegalArgumentException(
                    "Cannot set a default row that does not exist in the section");
        }

        if (defaultRow != null) {
            defaultRow.setDefaultRow(false);
        }

        if (row != null) {
            row.setDefaultRow(true);
        }

        defaultRow = row;
        markAsDirty();
    }

    /**
     * Returns the current default row of this header. The default row is a
     * special header row providing a user interface for sorting columns.
     * 
     * @return the default row or null if no default row set
     */
    public HeaderRow getDefaultRow() {
        return defaultRow;
    }

    @Override
    protected GridStaticSectionState getState() {
        return headerState;
    }

    @Override
    protected HeaderRow createRow() {
        return new HeaderRow(this);
    }

    @Override
    public HeaderRow removeRow(int rowIndex) {
        HeaderRow row = super.removeRow(rowIndex);
        if (row == defaultRow) {
            // Default Header Row was just removed.
            setDefaultRow(null);
        }
        return row;
    }
}
