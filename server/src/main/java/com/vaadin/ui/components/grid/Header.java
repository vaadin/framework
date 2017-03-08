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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Represents the header section of a Grid.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
public abstract class Header extends StaticSection<Header.Row> {

    /**
     * A row in a Grid header.
     */
    public class Row extends StaticSection.StaticRow<Row.Cell>
            implements HeaderRow {

        /**
         * A cell in a Grid header row.
         */
        public class Cell extends StaticSection.StaticCell
                implements HeaderCell {
            /**
             * Creates a new header cell.
             */
            protected Cell() {
                super(Row.this);
            }

            @Override
            public void setText(String text) {
                super.setText(text);
                if (isDefault()) {
                    Column<?, ?> col = getColumnByInternalId(getColumnId());
                    if (col != null) {
                        col.setCaption(text);
                    }
                }
            }
        }

        /**
         * Creates a new header row.
         */
        protected Row() {
            super(Header.this);
        }

        @Override
        protected Cell createCell() {
            return new Cell();
        }

        @Override
        protected String getCellTagName() {
            return "th";
        }

        /**
         * Returns whether this row is the default header row.
         *
         * @return {@code true} if this row is the default row, {@code false}
         *         otherwise.
         */
        protected boolean isDefault() {
            return getRowState().defaultHeader;
        }

        /**
         * Sets whether this row is the default header row.
         *
         * @param defaultHeader
         *            {@code true} to set to default, {@code false} otherwise.
         */
        protected void setDefault(boolean defaultHeader) {
            getRowState().defaultHeader = defaultHeader;
        }

        /**
         * Merges column cells in the row. Original cells are hidden, and new
         * merged cell is shown instead. The cell has a width of all merged
         * cells together, inherits styles of the first merged cell but has
         * empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be
         *            merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(HeaderCell...)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        @Override
        public HeaderCell join(Set<HeaderCell> cellsToMerge) {
            for (HeaderCell cell : cellsToMerge) {
                checkIfAlreadyMerged(cell.getColumnId());
            }

            // Create new cell data for the group
            Cell newCell = createCell();

            Set<String> columnGroup = new HashSet<>();
            for (HeaderCell cell : cellsToMerge) {
                columnGroup.add(cell.getColumnId());
            }
            addMergedCell(newCell, columnGroup);
            markAsDirty();
            return newCell;
        }

        /**
         * Merges column cells in the row. Original cells are hidden, and new
         * merged cell is shown instead. The cell has a width of all merged
         * cells together, inherits styles of the first merged cell but has
         * empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be
         *            merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Set)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        @Override
        public HeaderCell join(HeaderCell... cellsToMerge) {
            return join(Stream.of(cellsToMerge));
        }

        private HeaderCell join(Stream<HeaderCell> cellStream) {
            return join(cellStream.collect(Collectors.toSet()));
        }

        @Override
        public HeaderCell join(Column<?, ?>... columnsToMerge) {
            return join(Stream.of(columnsToMerge).map(this::getCell));
        }

        @Override
        public HeaderCell join(String... columnIdsToMerge) {
            Grid<?> grid = getGrid();
            return join(Stream.of(columnIdsToMerge).map(columnId -> {
                Column<?, ?> column = grid.getColumn(columnId);
                if (column == null) {
                    throw new IllegalStateException(
                            "There is no column with the id " + columnId);
                }
                return getCell(column);
            }));
        }

        @Override
        protected void readDesign(Element trElement,
                DesignContext designContext) {
            super.readDesign(trElement, designContext);

            boolean defaultRow = DesignAttributeHandler.readAttribute("default",
                    trElement.attributes(), false, boolean.class);
            if (defaultRow) {
                setDefault(true);
            }
        }

        @Override
        protected void writeDesign(Element trElement,
                DesignContext designContext) {
            super.writeDesign(trElement, designContext);

            if (isDefault()) {
                DesignAttributeHandler.writeAttribute("default",
                        trElement.attributes(), true, null, boolean.class,
                        designContext);
            }
        }
    }

    @Override
    public Row createRow() {
        return new Row();
    }

    @Override
    public void removeRow(int index) {
        if (getRow(index).isDefault()) {
            setDefaultRow(null);
        }
        super.removeRow(index);
    }

    /**
     * Returns the default row of this header. The default row displays column
     * captions and sort indicators.
     *
     * @return the default row, or {@code null} if there is no default row
     */
    public Row getDefaultRow() {
        return getRows().stream().filter(Row::isDefault).findAny().orElse(null);
    }

    /**
     * Sets the default row of this header. The default row displays column
     * captions and sort indicators.
     *
     * @param defaultRow
     *            the new default row, or null for no default row
     *
     * @throws IllegalArgumentException
     *             if the header does not contain the row
     */
    public void setDefaultRow(Row defaultRow) {
        if (defaultRow != null) {
            if (!getRows().contains(defaultRow)) {
                throw new IllegalArgumentException(
                        "The section does not contain the row");
            }
            if (defaultRow.isDefault()) {
                return;
            }
        }
        getRows().forEach(row -> row.setDefault(row == defaultRow));

        markAsDirty();
    }

}
