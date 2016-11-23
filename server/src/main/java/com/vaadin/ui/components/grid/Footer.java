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

import com.vaadin.ui.Grid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the footer section of a Grid.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
public abstract class Footer extends StaticSection<Footer.Row> {

    /**
     * A row in a Grid Footer.
     */
    public class Row extends StaticSection.StaticRow<Row.Cell>
            implements Grid.FooterRow {

        /**
         * A cell in a Grid footer row.
         */
        public class Cell extends StaticSection.StaticCell
                implements Grid.FooterCell {
            /**
             * Creates a new footer cell.
             */
            protected Cell() {
                super(Row.this);
            }
        }

        /**
         * Creates a new footer row.
         */
        protected Row() {
            super(Footer.this);
        }

        @Override
        protected Cell createCell() {
            return new Cell();
        }

        @Override
        protected String getCellTagName() {
            return "td";
        }

        /**
         * Merges column cells in the row. Original cells are hidden, and new merged cell is shown instead.
         * The cell has a width of all merged cells together, inherits styles of the first merged cell
         * but has empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Grid.FooterCell...)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        @Override
        public Grid.FooterCell join(Set<Grid.FooterCell> cellsToMerge) {
            for (Grid.FooterCell cell : cellsToMerge) {
                checkIfAlreadyMerged(cell.getColumnId());
            }

            // Create new cell data for the group
            Cell newCell = createCell();

            Set<String> columnGroup = new HashSet<>();
            for (Grid.FooterCell cell : cellsToMerge) {
                columnGroup.add(cell.getColumnId());
            }
            addMergedCell(newCell, columnGroup);
            markAsDirty();
            return newCell;
        }

        /**
         * Merges column cells in the row. Original cells are hidden, and new merged cell is shown instead.
         * The cell has a width of all merged cells together, inherits styles of the first merged cell
         * but has empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Set)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        @Override
        public Grid.FooterCell join(Grid.FooterCell... cellsToMerge) {
            Set<Grid.FooterCell> footerCells = new HashSet<>(Arrays.asList(cellsToMerge));
            return join(footerCells);
        }


    }

    @Override
    public Row createRow() {
        return new Row();
    }
}
