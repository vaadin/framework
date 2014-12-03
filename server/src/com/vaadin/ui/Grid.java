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

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.google.gwt.thirdparty.guava.common.collect.Sets.SetView;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.RpcDataProviderExtension.DataProviderKeyMapper;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.BindException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterUtil;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.grid.EditorRowClientRpc;
import com.vaadin.shared.ui.grid.EditorRowServerRpc;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.GridState.SharedSelectionMode;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.GridStaticSectionState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.CellState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.RowState;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.grid.SortDirection;
import com.vaadin.shared.ui.grid.SortEventOriginator;
import com.vaadin.ui.Grid.StaticSection.StaticRow;
import com.vaadin.ui.components.grid.Renderer;
import com.vaadin.ui.components.grid.SortOrderChangeEvent;
import com.vaadin.ui.components.grid.SortOrderChangeListener;
import com.vaadin.ui.components.grid.renderers.TextRenderer;
import com.vaadin.ui.components.grid.selection.MultiSelectionModel;
import com.vaadin.ui.components.grid.selection.NoSelectionModel;
import com.vaadin.ui.components.grid.selection.SelectionChangeEvent;
import com.vaadin.ui.components.grid.selection.SelectionChangeListener;
import com.vaadin.ui.components.grid.selection.SelectionChangeNotifier;
import com.vaadin.ui.components.grid.selection.SelectionModel;
import com.vaadin.ui.components.grid.selection.SingleSelectionModel;
import com.vaadin.ui.components.grid.sort.Sort;
import com.vaadin.ui.components.grid.sort.SortOrder;
import com.vaadin.util.ReflectTools;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonValue;

/**
 * A grid component for displaying tabular data.
 * <p>
 * Grid is always bound to a {@link Container.Indexed}, but is not a
 * {@code Container} of any kind in of itself. The contents of the given
 * Container is displayed with the help of {@link Renderer Renderers}.
 * 
 * <h3 id="grid-headers-and-footers">Headers and Footers</h3>
 * <p>
 * 
 * 
 * <h3 id="grid-converters-and-renderers">Converters and Renderers</h3>
 * <p>
 * Each column has its own {@link Renderer} that displays data into something
 * that can be displayed in the browser. That data is first converted with a
 * {@link com.vaadin.data.util.converter.Converter Converter} into something
 * that the Renderer can process. This can also be an implicit step - if a
 * column has a simple data type, like a String, no explicit assignment is
 * needed.
 * <p>
 * Usually a renderer takes some kind of object, and converts it into a
 * HTML-formatted string.
 * <p>
 * <code><pre>
 * Grid grid = new Grid(myContainer);
 * Column column = grid.getColumn(STRING_DATE_PROPERTY);
 * column.setConverter(new StringToDateConverter());
 * column.setRenderer(new MyColorfulDateRenderer());
 * </pre></code>
 * 
 * <h3 id="grid-lazyloading">Lazy Loading</h3>
 * <p>
 * The data is accessed as it is needed by Grid and not any sooner. In other
 * words, if the given Container is huge, but only the first few rows are
 * displayed to the user, only those (and a few more, for caching purposes) are
 * accessed.
 * 
 * <h3 id="grid-selection-modes-and-models">Selection Modes and Models</h3>
 * <p>
 * Grid supports three selection <em>{@link SelectionMode modes}</em> (single,
 * multi, none), and comes bundled with one
 * <em>{@link SelectionModel model}</em> for each of the modes. The distinction
 * between a selection mode and selection model is as follows: a <em>mode</em>
 * essentially says whether you can have one, many or no rows selected. The
 * model, however, has the behavioral details of each. A single selection model
 * may require that the user deselects one row before selecting another one. A
 * variant of a multiselect might have a configurable maximum of rows that may
 * be selected. And so on.
 * <p>
 * <code><pre>
 * Grid grid = new Grid(myContainer);
 * 
 * // uses the bundled SingleSelectionModel class
 * grid.setSelectionMode(SelectionMode.SINGLE);
 * 
 * // changes the behavior to a custom selection model
 * grid.setSelectionModel(new MyTwoSelectionModel());
 * </pre></code>
 * 
 * @since
 * @author Vaadin Ltd
 */
public class Grid extends AbstractComponent implements SelectionChangeNotifier,
        SelectiveRenderer {

    /**
     * Selection modes representing built-in {@link SelectionModel
     * SelectionModels} that come bundled with {@link Grid}.
     * <p>
     * Passing one of these enums into
     * {@link Grid#setSelectionMode(SelectionMode)} is equivalent to calling
     * {@link Grid#setSelectionModel(SelectionModel)} with one of the built-in
     * implementations of {@link SelectionModel}.
     * 
     * @see Grid#setSelectionMode(SelectionMode)
     * @see Grid#setSelectionModel(SelectionModel)
     */
    public enum SelectionMode {
        /** A SelectionMode that maps to {@link SingleSelectionModel} */
        SINGLE {
            @Override
            protected SelectionModel createModel() {
                return new SingleSelectionModel();
            }

        },

        /** A SelectionMode that maps to {@link MultiSelectionModel} */
        MULTI {
            @Override
            protected SelectionModel createModel() {
                return new MultiSelectionModel();
            }
        },

        /** A SelectionMode that maps to {@link NoSelectionModel} */
        NONE {
            @Override
            protected SelectionModel createModel() {
                return new NoSelectionModel();
            }
        };

        protected abstract SelectionModel createModel();
    }

    /**
     * Abstract base class for Grid header and footer sections.
     * 
     * @param <ROWTYPE>
     *            the type of the rows in the section
     */
    protected static abstract class StaticSection<ROWTYPE extends StaticSection.StaticRow<?>>
            implements Serializable {

        /**
         * Abstract base class for Grid header and footer rows.
         * 
         * @param <CELLTYPE>
         *            the type of the cells in the row
         */
        abstract static class StaticRow<CELLTYPE extends StaticCell> implements
                Serializable {

            private RowState rowState = new RowState();
            protected StaticSection<?> section;
            private Map<Object, CELLTYPE> cells = new LinkedHashMap<Object, CELLTYPE>();
            private Map<Set<CELLTYPE>, CELLTYPE> cellGroups = new HashMap<Set<CELLTYPE>, CELLTYPE>();

            protected StaticRow(StaticSection<?> section) {
                this.section = section;
            }

            protected void addCell(Object propertyId) {
                CELLTYPE cell = createCell();
                cell.setColumnId(section.grid.getColumn(propertyId).getState().id);
                cells.put(propertyId, cell);
                rowState.cells.add(cell.getCellState());
            }

            protected void removeCell(Object propertyId) {
                CELLTYPE cell = cells.remove(propertyId);
                if (cell != null) {
                    Set<CELLTYPE> cellGroupForCell = getCellGroupForCell(cell);
                    if (cellGroupForCell != null) {
                        removeCellFromGroup(cell, cellGroupForCell);
                    }
                    rowState.cells.remove(cell.getCellState());
                }
            }

            private void removeCellFromGroup(CELLTYPE cell,
                    Set<CELLTYPE> cellGroup) {
                String columnId = cell.getColumnId();
                for (Set<String> group : rowState.cellGroups.keySet()) {
                    if (group.contains(columnId)) {
                        if (group.size() > 2) {
                            // Update map key correctly
                            CELLTYPE mergedCell = cellGroups.remove(cellGroup);
                            cellGroup.remove(cell);
                            cellGroups.put(cellGroup, mergedCell);

                            group.remove(columnId);
                        } else {
                            rowState.cellGroups.remove(group);
                            cellGroups.remove(cellGroup);
                        }
                        return;
                    }
                }
            }

            /**
             * Creates and returns a new instance of the cell type.
             * 
             * @return the created cell
             */
            protected abstract CELLTYPE createCell();

            protected RowState getRowState() {
                return rowState;
            }

            /**
             * Returns the cell for the given property id on this row. If the
             * column is merged returned cell is the cell for the whole group.
             * 
             * @param propertyId
             *            the property id of the column
             * @return the cell for the given property, merged cell for merged
             *         properties, null if not found
             */
            public CELLTYPE getCell(Object propertyId) {
                CELLTYPE cell = cells.get(propertyId);
                Set<CELLTYPE> cellGroup = getCellGroupForCell(cell);
                if (cellGroup != null) {
                    cell = cellGroups.get(cellGroup);
                }
                return cell;
            }

            /**
             * Merges columns cells in a row
             * 
             * @param propertyIds
             *            The property ids of columns to merge
             * @return The remaining visible cell after the merge
             */
            public CELLTYPE join(Object... propertyIds) {
                assert propertyIds.length > 1 : "You need to merge at least 2 properties";

                Set<CELLTYPE> cells = new HashSet<CELLTYPE>();
                for (int i = 0; i < propertyIds.length; ++i) {
                    cells.add(getCell(propertyIds[i]));
                }

                return join(cells);
            }

            /**
             * Merges columns cells in a row
             * 
             * @param cells
             *            The cells to merge. Must be from the same row.
             * @return The remaining visible cell after the merge
             */
            public CELLTYPE join(CELLTYPE... cells) {
                assert cells.length > 1 : "You need to merge at least 2 cells";

                return join(new HashSet<CELLTYPE>(Arrays.asList(cells)));
            }

            protected CELLTYPE join(Set<CELLTYPE> cells) {
                for (CELLTYPE cell : cells) {
                    if (getCellGroupForCell(cell) != null) {
                        throw new IllegalArgumentException(
                                "Cell already merged");
                    } else if (!this.cells.containsValue(cell)) {
                        throw new IllegalArgumentException(
                                "Cell does not exist on this row");
                    }
                }

                // Create new cell data for the group
                CELLTYPE newCell = createCell();

                Set<String> columnGroup = new HashSet<String>();
                for (CELLTYPE cell : cells) {
                    columnGroup.add(cell.getColumnId());
                }
                rowState.cellGroups.put(columnGroup, newCell.getCellState());
                cellGroups.put(cells, newCell);
                return newCell;
            }

            private Set<CELLTYPE> getCellGroupForCell(CELLTYPE cell) {
                for (Set<CELLTYPE> group : cellGroups.keySet()) {
                    if (group.contains(cell)) {
                        return group;
                    }
                }
                return null;
            }
        }

        /**
         * A header or footer cell. Has a simple textual caption.
         */
        abstract static class StaticCell implements Serializable {

            private CellState cellState = new CellState();
            private StaticRow<?> row;

            protected StaticCell(StaticRow<?> row) {
                this.row = row;
            }

            private void setColumnId(String id) {
                cellState.columnId = id;
            }

            private String getColumnId() {
                return cellState.columnId;
            }

            /**
             * Gets the row where this cell is.
             * 
             * @return row for this cell
             */
            public StaticRow<?> getRow() {
                return row;
            }

            protected CellState getCellState() {
                return cellState;
            }

            /**
             * Sets the text displayed in this cell.
             * 
             * @param text
             *            a plain text caption
             */
            public void setText(String text) {
                cellState.text = text;
                cellState.type = GridStaticCellType.TEXT;
                row.section.markAsDirty();
            }

            /**
             * Returns the text displayed in this cell.
             * 
             * @return the plain text caption
             */
            public String getText() {
                if (cellState.type != GridStaticCellType.TEXT) {
                    throw new IllegalStateException(
                            "Cannot fetch Text from a cell with type "
                                    + cellState.type);
                }
                return cellState.text;
            }

            /**
             * Returns the HTML content displayed in this cell.
             * 
             * @return the html
             * 
             */
            public String getHtml() {
                if (cellState.type != GridStaticCellType.HTML) {
                    throw new IllegalStateException(
                            "Cannot fetch HTML from a cell with type "
                                    + cellState.type);
                }
                return cellState.html;
            }

            /**
             * Sets the HTML content displayed in this cell.
             * 
             * @param html
             *            the html to set
             */
            public void setHtml(String html) {
                cellState.html = html;
                cellState.type = GridStaticCellType.HTML;
                row.section.markAsDirty();
            }

            /**
             * Returns the component displayed in this cell.
             * 
             * @return the component
             */
            public Component getComponent() {
                if (cellState.type != GridStaticCellType.WIDGET) {
                    throw new IllegalStateException(
                            "Cannot fetch Component from a cell with type "
                                    + cellState.type);
                }
                return (Component) cellState.connector;
            }

            /**
             * Sets the component displayed in this cell.
             * 
             * @param component
             *            the component to set
             */
            public void setComponent(Component component) {
                component.setParent(row.section.grid);
                cellState.connector = component;
                cellState.type = GridStaticCellType.WIDGET;
                row.section.markAsDirty();
            }

            /**
             * Returns the custom style name for this cell.
             * 
             * @return the style name or null if no style name has been set
             */
            public String getStyleName() {
                return cellState.styleName;
            }

            /**
             * Sets a custom style name for this cell.
             * 
             * @param styleName
             *            the style name to set
             */
            public void setStyleName(String styleName) {
                cellState.styleName = styleName;
                row.section.markAsDirty();
            }

        }

        protected Grid grid;
        protected List<ROWTYPE> rows = new ArrayList<ROWTYPE>();

        /**
         * Sets the visibility of the whole section.
         * 
         * @param visible
         *            true to show this section, false to hide
         */
        public void setVisible(boolean visible) {
            if (getSectionState().visible != visible) {
                getSectionState().visible = visible;
                markAsDirty();
            }
        }

        /**
         * Returns the visibility of this section.
         * 
         * @return true if visible, false otherwise.
         */
        public boolean isVisible() {
            return getSectionState().visible;
        }

        /**
         * Removes the row at the given position.
         * 
         * @param index
         *            the position of the row
         * 
         * @throws IllegalArgumentException
         *             if no row exists at given index
         * @see #removeRow(StaticRow)
         * @see #addRowAt(int)
         * @see #appendRow()
         * @see #prependRow()
         */
        public ROWTYPE removeRow(int rowIndex) {
            if (rowIndex >= rows.size() || rowIndex < 0) {
                throw new IllegalArgumentException("No row at given index "
                        + rowIndex);
            }
            ROWTYPE row = rows.remove(rowIndex);
            getSectionState().rows.remove(rowIndex);

            markAsDirty();
            return row;
        }

        /**
         * Removes the given row from the section.
         * 
         * @param row
         *            the row to be removed
         * 
         * @throws IllegalArgumentException
         *             if the row does not exist in this section
         * @see #removeRow(int)
         * @see #addRowAt(int)
         * @see #appendRow()
         * @see #prependRow()
         */
        public void removeRow(ROWTYPE row) {
            try {
                removeRow(rows.indexOf(row));
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Section does not contain the given row");
            }
        }

        /**
         * Gets row at given index.
         * 
         * @param rowIndex
         *            0 based index for row. Counted from top to bottom
         * @return row at given index
         */
        public ROWTYPE getRow(int rowIndex) {
            if (rowIndex >= rows.size() || rowIndex < 0) {
                throw new IllegalArgumentException("No row at given index "
                        + rowIndex);
            }
            return rows.get(rowIndex);
        }

        /**
         * Adds a new row at the top of this section.
         * 
         * @return the new row
         * @see #appendRow()
         * @see #addRowAt(int)
         * @see #removeRow(StaticRow)
         * @see #removeRow(int)
         */
        public ROWTYPE prependRow() {
            return addRowAt(0);
        }

        /**
         * Adds a new row at the bottom of this section.
         * 
         * @return the new row
         * @see #prependRow()
         * @see #addRowAt(int)
         * @see #removeRow(StaticRow)
         * @see #removeRow(int)
         */
        public ROWTYPE appendRow() {
            return addRowAt(rows.size());
        }

        /**
         * Inserts a new row at the given position.
         * 
         * @param index
         *            the position at which to insert the row
         * @return the new row
         * 
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         * @see #appendRow()
         * @see #prependRow()
         * @see #removeRow(StaticRow)
         * @see #removeRow(int)
         */
        public ROWTYPE addRowAt(int index) {
            if (index > rows.size() || index < 0) {
                throw new IllegalArgumentException(
                        "Unable to add row at index " + index);
            }
            ROWTYPE row = createRow();
            rows.add(index, row);
            getSectionState().rows.add(index, row.getRowState());

            Indexed dataSource = grid.getContainerDatasource();
            for (Object id : dataSource.getContainerPropertyIds()) {
                row.addCell(id);
            }

            markAsDirty();
            return row;
        }

        /**
         * Gets the amount of rows in this section.
         * 
         * @return row count
         */
        public int getRowCount() {
            return rows.size();
        }

        protected abstract GridStaticSectionState getSectionState();

        protected abstract ROWTYPE createRow();

        /**
         * Informs the grid that state has changed and it should be redrawn.
         */
        protected void markAsDirty() {
            grid.markAsDirty();
        }

        /**
         * Removes a column for given property id from the section.
         * 
         * @param propertyId
         *            property to be removed
         */
        protected void removeColumn(Object propertyId) {
            for (ROWTYPE row : rows) {
                row.removeCell(propertyId);
            }
        }

        /**
         * Adds a column for given property id to the section.
         * 
         * @param propertyId
         *            property to be added
         */
        protected void addColumn(Object propertyId) {
            for (ROWTYPE row : rows) {
                row.addCell(propertyId);
            }
        }

        /**
         * Performs a sanity check that section is in correct state.
         * 
         * @throws IllegalStateException
         *             if merged cells are not i n continuous range
         */
        protected void sanityCheck() throws IllegalStateException {
            List<String> columnOrder = grid.getState().columnOrder;
            for (ROWTYPE row : rows) {
                for (Set<String> cellGroup : row.getRowState().cellGroups
                        .keySet()) {
                    if (!checkCellGroupAndOrder(columnOrder, cellGroup)) {
                        throw new IllegalStateException(
                                "Not all merged cells were in a continuous range.");
                    }
                }
            }
        }

        private boolean checkCellGroupAndOrder(List<String> columnOrder,
                Set<String> cellGroup) {
            if (!columnOrder.containsAll(cellGroup)) {
                return false;
            }

            for (int i = 0; i < columnOrder.size(); ++i) {
                if (!cellGroup.contains(columnOrder.get(i))) {
                    continue;
                }

                for (int j = 1; j < cellGroup.size(); ++j) {
                    if (!cellGroup.contains(columnOrder.get(i + j))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Represents the header section of a Grid.
     */
    protected static class Header extends StaticSection<HeaderRow> {

        private HeaderRow defaultRow = null;
        private final GridStaticSectionState headerState = new GridStaticSectionState();

        protected Header(Grid grid) {
            this.grid = grid;
            grid.getState(true).header = headerState;
            HeaderRow row = createRow();
            rows.add(row);
            setDefaultRow(row);
            getSectionState().rows.add(row.getRowState());
        }

        /**
         * Sets the default row of this header. The default row is a special
         * header row providing a user interface for sorting columns.
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
        protected GridStaticSectionState getSectionState() {
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

        @Override
        protected void sanityCheck() throws IllegalStateException {
            super.sanityCheck();

            boolean hasDefaultRow = false;
            for (HeaderRow row : rows) {
                if (row.getRowState().defaultRow) {
                    if (!hasDefaultRow) {
                        hasDefaultRow = true;
                    } else {
                        throw new IllegalStateException(
                                "Multiple default rows in header");
                    }
                }
            }
        }
    }

    /**
     * Represents a header row in Grid.
     */
    public static class HeaderRow extends StaticSection.StaticRow<HeaderCell> {

        protected HeaderRow(StaticSection<?> section) {
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

    /**
     * Represents a header cell in Grid. Can be a merged cell for multiple
     * columns.
     */
    public static class HeaderCell extends StaticSection.StaticCell {

        protected HeaderCell(HeaderRow row) {
            super(row);
        }
    }

    /**
     * Represents the footer section of a Grid. By default Footer is not
     * visible.
     */
    protected static class Footer extends StaticSection<FooterRow> {

        private final GridStaticSectionState footerState = new GridStaticSectionState();

        protected Footer(Grid grid) {
            this.grid = grid;
            grid.getState(true).footer = footerState;
        }

        @Override
        protected GridStaticSectionState getSectionState() {
            return footerState;
        }

        @Override
        protected FooterRow createRow() {
            return new FooterRow(this);
        }

        @Override
        protected void sanityCheck() throws IllegalStateException {
            super.sanityCheck();
        }
    }

    /**
     * Represents a footer row in Grid.
     */
    public static class FooterRow extends StaticSection.StaticRow<FooterCell> {

        protected FooterRow(StaticSection<?> section) {
            super(section);
        }

        @Override
        protected FooterCell createCell() {
            return new FooterCell(this);
        }

    }

    /**
     * Represents a footer cell in Grid.
     */
    public static class FooterCell extends StaticSection.StaticCell {

        protected FooterCell(FooterRow row) {
            super(row);
        }
    }

    /**
     * A column in the grid. Can be obtained by calling
     * {@link Grid#getColumn(Object propertyId)}.
     */
    public static class Column implements Serializable {

        /**
         * The state of the column shared to the client
         */
        private final GridColumnState state;

        /**
         * The grid this column is associated with
         */
        private final Grid grid;

        private Converter<?, Object> converter;

        /**
         * A check for allowing the {@link #Column(Grid, GridColumnState)
         * constructor} to call {@link #setConverter(Converter)} with a
         * <code>null</code>, even if model and renderer aren't compatible.
         */
        private boolean isFirstConverterAssignment = true;

        /**
         * Internally used constructor.
         * 
         * @param grid
         *            The grid this column belongs to. Should not be null.
         * @param state
         *            the shared state of this column
         */
        Column(Grid grid, GridColumnState state) {
            this.grid = grid;
            this.state = state;
            internalSetRenderer(new TextRenderer());
        }

        /**
         * Returns the serializable state of this column that is sent to the
         * client side connector.
         * 
         * @return the internal state of the column
         */
        GridColumnState getState() {
            return state;
        }

        /**
         * Returns the caption of the header. By default the header caption is
         * the property id of the column.
         * 
         * @return the text in the default row of header, null if no default row
         * 
         * @throws IllegalStateException
         *             if the column no longer is attached to the grid
         */
        public String getHeaderCaption() throws IllegalStateException {
            checkColumnIsAttached();
            HeaderRow row = grid.getHeader().getDefaultRow();
            if (row != null) {
                return row.getCell(grid.getPropertyIdByColumnId(state.id))
                        .getText();
            }
            return null;
        }

        /**
         * Sets the caption of the header.
         * 
         * @param caption
         *            the text to show in the caption
         * @return the column itself
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column setHeaderCaption(String caption)
                throws IllegalStateException {
            checkColumnIsAttached();
            HeaderRow row = grid.getHeader().getDefaultRow();
            if (row != null) {
                row.getCell(grid.getPropertyIdByColumnId(state.id)).setText(
                        caption);
            }
            return this;
        }

        /**
         * Returns the width (in pixels). By default a column is 100px wide.
         * 
         * @return the width in pixels of the column
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public int getWidth() throws IllegalStateException {
            checkColumnIsAttached();
            return state.width;
        }

        /**
         * Sets the width (in pixels).
         * 
         * @param pixelWidth
         *            the new pixel width of the column
         * @return the column itself
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @throws IllegalArgumentException
         *             thrown if pixel width is less than zero
         */
        public Column setWidth(int pixelWidth) throws IllegalStateException,
                IllegalArgumentException {
            checkColumnIsAttached();
            if (pixelWidth < 0) {
                throw new IllegalArgumentException(
                        "Pixel width should be greated than 0 (in "
                                + toString() + ")");
            }
            state.width = pixelWidth;
            grid.markAsDirty();
            return this;
        }

        /**
         * Marks the column width as undefined meaning that the grid is free to
         * resize the column based on the cell contents and available space in
         * the grid.
         * 
         * @return the column itself
         */
        public Column setWidthUndefined() {
            checkColumnIsAttached();
            state.width = -1;
            grid.markAsDirty();
            return this;
        }

        /**
         * Is this column visible in the grid. By default all columns are
         * visible.
         * 
         * @return <code>true</code> if the column is visible
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public boolean isVisible() throws IllegalStateException {
            checkColumnIsAttached();
            return state.visible;
        }

        /**
         * Set the visibility of this column
         * 
         * @param visible
         *            is the column visible
         * @return the column itself
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column setVisible(boolean visible) throws IllegalStateException {
            checkColumnIsAttached();
            state.visible = visible;
            grid.markAsDirty();
            return this;
        }

        /**
         * Checks if column is attached and throws an
         * {@link IllegalStateException} if it is not
         * 
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        protected void checkColumnIsAttached() throws IllegalStateException {
            if (grid.getColumnByColumnId(state.id) == null) {
                throw new IllegalStateException("Column no longer exists.");
            }
        }

        /**
         * Sets this column as the last frozen column in its grid.
         * 
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if the column is no longer attached to any grid
         * @see Grid#setLastFrozenColumn(Column)
         */
        public Column setLastFrozenColumn() {
            checkColumnIsAttached();
            grid.setLastFrozenColumn(this);
            return this;
        }

        /**
         * Sets the renderer for this column.
         * <p>
         * If a suitable converter isn't defined explicitly, the session
         * converter factory is used to find a compatible converter.
         * 
         * @param renderer
         *            the renderer to use
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if no compatible converter could be found
         * 
         * @see VaadinSession#getConverterFactory()
         * @see ConverterUtil#getConverter(Class, Class, VaadinSession)
         * @see #setConverter(Converter)
         */
        public Column setRenderer(Renderer<?> renderer) {
            if (!internalSetRenderer(renderer)) {
                throw new IllegalArgumentException(
                        "Could not find a converter for converting from the model type "
                                + getModelType()
                                + " to the renderer presentation type "
                                + renderer.getPresentationType() + " (in "
                                + toString() + ")");
            }
            return this;
        }

        /**
         * Sets the renderer for this column and the converter used to convert
         * from the property value type to the renderer presentation type.
         * 
         * @param renderer
         *            the renderer to use, cannot be null
         * @param converter
         *            the converter to use
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if the renderer is already associated with a grid column
         */
        public <T> Column setRenderer(Renderer<T> renderer,
                Converter<? extends T, ?> converter) {
            if (renderer.getParent() != null) {
                throw new IllegalArgumentException(
                        "Cannot set a renderer that is already connected to a grid column (in "
                                + toString() + ")");
            }

            if (getRenderer() != null) {
                grid.removeExtension(getRenderer());
            }

            grid.addRenderer(renderer);
            state.rendererConnector = renderer;
            setConverter(converter);
            return this;
        }

        /**
         * Sets the converter used to convert from the property value type to
         * the renderer presentation type.
         * 
         * @param converter
         *            the converter to use, or {@code null} to not use any
         *            converters
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if the types are not compatible
         */
        public Column setConverter(Converter<?, ?> converter)
                throws IllegalArgumentException {
            Class<?> modelType = getModelType();
            if (converter != null) {
                if (!converter.getModelType().isAssignableFrom(modelType)) {
                    throw new IllegalArgumentException(
                            "The converter model type "
                                    + converter.getModelType()
                                    + " is not compatible with the property type "
                                    + modelType + " (in " + toString() + ")");

                } else if (!getRenderer().getPresentationType()
                        .isAssignableFrom(converter.getPresentationType())) {
                    throw new IllegalArgumentException(
                            "The converter presentation type "
                                    + converter.getPresentationType()
                                    + " is not compatible with the renderer presentation type "
                                    + getRenderer().getPresentationType()
                                    + " (in " + toString() + ")");
                }
            }

            else {
                /*
                 * Since the converter is null (i.e. will be removed), we need
                 * to know that the renderer and model are compatible. If not,
                 * we can't allow for this to happen.
                 * 
                 * The constructor is allowed to call this method with null
                 * without any compatibility checks, therefore we have a special
                 * case for it.
                 */

                Class<?> rendererPresentationType = getRenderer()
                        .getPresentationType();
                if (!isFirstConverterAssignment
                        && !rendererPresentationType
                                .isAssignableFrom(modelType)) {
                    throw new IllegalArgumentException(
                            "Cannot remove converter, "
                                    + "as renderer's presentation type "
                                    + rendererPresentationType.getName()
                                    + " and column's "
                                    + "model "
                                    + modelType.getName()
                                    + " type aren't "
                                    + "directly compatible with each other (in "
                                    + toString() + ")");
                }
            }

            isFirstConverterAssignment = false;

            @SuppressWarnings("unchecked")
            Converter<?, Object> castConverter = (Converter<?, Object>) converter;
            this.converter = castConverter;

            return this;
        }

        /**
         * Returns the renderer instance used by this column.
         * 
         * @return the renderer
         */
        public Renderer<?> getRenderer() {
            return (Renderer<?>) getState().rendererConnector;
        }

        /**
         * Returns the converter instance used by this column.
         * 
         * @return the converter
         */
        public Converter<?, ?> getConverter() {
            return converter;
        }

        private <T> boolean internalSetRenderer(Renderer<T> renderer) {

            Converter<? extends T, ?> converter;
            if (isCompatibleWithProperty(renderer, getConverter())) {
                // Use the existing converter (possibly none) if types
                // compatible
                converter = (Converter<? extends T, ?>) getConverter();
            } else {
                converter = ConverterUtil.getConverter(
                        renderer.getPresentationType(), getModelType(),
                        getSession());
            }
            setRenderer(renderer, converter);
            return isCompatibleWithProperty(renderer, converter);
        }

        private VaadinSession getSession() {
            UI ui = grid.getUI();
            return ui != null ? ui.getSession() : null;
        }

        private boolean isCompatibleWithProperty(Renderer<?> renderer,
                Converter<?, ?> converter) {
            Class<?> type;
            if (converter == null) {
                type = getModelType();
            } else {
                type = converter.getPresentationType();
            }
            return renderer.getPresentationType().isAssignableFrom(type);
        }

        private Class<?> getModelType() {
            return grid.getContainerDatasource().getType(
                    grid.getPropertyIdByColumnId(state.id));
        }

        /**
         * Should sorting controls be available for the column
         * 
         * @param sortable
         *            <code>true</code> if the sorting controls should be
         *            visible.
         * @return the column itself
         */
        public Column setSortable(boolean sortable) {
            checkColumnIsAttached();
            state.sortable = sortable;
            grid.markAsDirty();
            return this;
        }

        /**
         * Are the sorting controls visible in the column header
         */
        public boolean isSortable() {
            return state.sortable;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[propertyId:"
                    + grid.getPropertyIdByColumnId(state.id) + "]";
        }
    }

    /**
     * A class for configuring the editor row in a grid.
     */
    public static class EditorRow implements Serializable {
        private Grid grid;

        private FieldGroup fieldGroup = new FieldGroup();

        private ErrorHandler errorHandler;

        private Object editedItemId = null;

        private HashSet<Object> uneditableProperties = new HashSet<Object>();

        /**
         * Constructs a new editor row for the given grid component.
         * 
         * @param grid
         *            the grid this editor row is attached to
         */
        EditorRow(Grid grid) {
            this.grid = grid;
        }

        /**
         * Checks whether the editor row feature is enabled for the grid or not.
         * 
         * @return <code>true</code> iff the editor row feature is enabled for
         *         the grid
         * @see #getEditedItemId()
         */
        public boolean isEnabled() {
            checkDetached();
            return grid.getState(false).editorRowEnabled;
        }

        /**
         * Sets whether or not the editor row feature is enabled for the grid.
         * 
         * @param isEnabled
         *            <code>true</code> to enable the feature,
         *            <code>false</code> otherwise
         * @throws IllegalStateException
         *             if an item is currently being edited
         * @see #getEditedItemId()
         */
        public void setEnabled(boolean isEnabled) throws IllegalStateException {
            checkDetached();
            if (isEditing()) {
                throw new IllegalStateException(
                        "Cannot disable the editor row " + "while an item ("
                                + getEditedItemId() + ") is being edited.");
            }
            if (isEnabled() != isEnabled) {
                grid.getState().editorRowEnabled = isEnabled;
            }
        }

        /**
         * Gets the field group that is backing this editor row.
         * 
         * @return the backing field group
         */
        public FieldGroup getFieldGroup() {
            checkDetached();
            return fieldGroup;
        }

        /**
         * Sets the field group that is backing this editor row.
         * 
         * @param fieldGroup
         *            the backing field group
         */
        public void setFieldGroup(FieldGroup fieldGroup) {
            checkDetached();
            this.fieldGroup = fieldGroup;
            if (isEditing()) {
                this.fieldGroup.setItemDataSource(getContainer().getItem(
                        editedItemId));
            }
        }

        /**
         * Returns the error handler of this editor row.
         * 
         * @return the error handler or null if there is no dedicated error
         *         handler
         * 
         * @see #setErrorHandler(ErrorHandler)
         * @see ClientConnector#getErrorHandler()
         */
        public ErrorHandler getErrorHandler() {
            return errorHandler;
        }

        /**
         * Sets the error handler for this editor row. The error handler is
         * invoked for exceptions thrown while processing client requests;
         * specifically when {@link #commit()} triggered by the client throws a
         * CommitException. If the error handler is not set, one is looked up
         * via Grid.
         * 
         * @param errorHandler
         *            the error handler to use
         * 
         * @see ClientConnector#setErrorHandler(ErrorHandler)
         * @see ErrorEvent#findErrorHandler(ClientConnector)
         */
        public void setErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
        }

        /**
         * Builds a field using the given caption and binds it to the given
         * property id using the field binder. Ensures the new field is of the
         * given type.
         * <p>
         * <em>Note:</em> This is a pass-through call to the backing field
         * group.
         * 
         * @param propertyId
         *            The property id to bind to. Must be present in the field
         *            finder
         * @param fieldType
         *            The type of field that we want to create
         * @throws BindException
         *             If the field could not be created
         * @return The created and bound field. Can be any type of {@link Field}
         *         .
         */
        public <T extends Field<?>> T buildAndBind(Object propertyId,
                Class<T> fieldComponent) throws BindException {
            checkDetached();
            return fieldGroup.buildAndBind(null, propertyId, fieldComponent);
        }

        /**
         * Binds the field with the given propertyId from the current item. If
         * an item has not been set then the binding is postponed until the item
         * is set using {@link #editItem(Object)}.
         * <p>
         * This method also adds validators when applicable.
         * <p>
         * <em>Note:</em> This is a pass-through call to the backing field
         * group.
         * 
         * @param field
         *            The field to bind
         * @param propertyId
         *            The propertyId to bind to the field
         * @throws BindException
         *             If the property id is already bound to another field by
         *             this field binder
         */
        public void bind(Object propertyId, Field<?> field)
                throws BindException {
            checkDetached();
            fieldGroup.bind(field, propertyId);
        }

        /**
         * Sets the field factory for the {@link FieldGroup}. The field factory
         * is only used when {@link FieldGroup} creates a new field.
         * <p>
         * <em>Note:</em> This is a pass-through call to the backing field
         * group.
         * 
         * @param fieldFactory
         *            The field factory to use
         */
        public void setFieldFactory(FieldGroupFieldFactory factory) {
            checkDetached();
            fieldGroup.setFieldFactory(factory);
        }

        /**
         * Gets the field component that represents a property. If the property
         * is not yet bound to a field, null is returned.
         * <p>
         * When {@link #editItem(Object) editItem} is called, fields are
         * automatically created and bound to any unbound properties.
         * 
         * @param propertyId
         *            the property id of the property for which to find the
         *            field
         * @return the bound field or null if not bound
         * 
         * @see #setPropertyUneditable(Object)
         */
        public Field<?> getField(Object propertyId) {
            checkDetached();
            return fieldGroup.getField(propertyId);
        }

        /**
         * Sets a property editable or not.
         * <p>
         * In order for a user to edit a particular value with a Field, it needs
         * to be both non-readonly and editable.
         * <p>
         * The difference between read-only and uneditable is that the read-only
         * state is propagated back into the property, while the editable
         * property is internal metadata for the editor row.
         * 
         * @param propertyId
         *            the id of the property to set as editable state
         * @param editable
         *            whether or not {@code propertyId} chould be editable
         */
        public void setPropertyEditable(Object propertyId, boolean editable) {
            checkDetached();
            checkPropertyExists(propertyId);
            if (getField(propertyId) != null) {
                getField(propertyId).setReadOnly(!editable);
            }
            if (editable) {
                uneditableProperties.remove(propertyId);
            } else {
                uneditableProperties.add(propertyId);
            }
        }

        /**
         * Checks whether a property is uneditable or not.
         * <p>
         * This only checks whether the property is configured as uneditable in
         * this editor row. The property's or field's readonly status will
         * ultimately decide whether the value can be edited or not.
         * 
         * @param propertyId
         *            the id of the property to check for editable status
         * @return <code>true</code> iff the property is editable according to
         *         this editor row
         */
        public boolean isPropertyEditable(Object propertyId) {
            checkDetached();
            checkPropertyExists(propertyId);
            return !uneditableProperties.contains(propertyId);
        }

        /**
         * Commits all changes done to the bound fields.
         * <p>
         * <em>Note:</em> This is a pass-through call to the backing field
         * group.
         * 
         * @throws CommitException
         *             If the commit was aborted
         */
        public void commit() throws CommitException {
            checkDetached();
            fieldGroup.commit();
        }

        /**
         * Discards all changes done to the bound fields.
         * <p>
         * <em>Note:</em> This is a pass-through call to the backing field
         * group.
         */
        public void discard() {
            checkDetached();
            fieldGroup.discard();
        }

        /**
         * Internal method to inform the editor row that it is no longer
         * attached to a Grid.
         */
        void detach() {
            checkDetached();
            if (isEditing()) {
                /*
                 * Simply force cancel the editing; throwing here would just
                 * make Grid.setContainerDataSource semantics more complicated.
                 */
                cancel();
            }
            for (Field<?> editor : getFields()) {
                editor.setParent(null);
            }
            grid = null;
        }

        /**
         * Sets an item as editable.
         * 
         * @param itemId
         *            the id of the item to edit
         * @throws IllegalStateException
         *             if the editor row is not enabled
         * @throws IllegalArgumentException
         *             if the {@code itemId} is not in the backing container
         * @see #setEnabled(boolean)
         */
        public void editItem(Object itemId) throws IllegalStateException,
                IllegalArgumentException {
            checkDetached();

            internalEditItem(itemId);

            grid.getEditorRowRpc().bind(
                    grid.getContainerDatasource().indexOfId(itemId));
        }

        protected void internalEditItem(Object itemId) {
            if (!isEnabled()) {
                throw new IllegalStateException("This "
                        + getClass().getSimpleName() + " is not enabled");
            }

            Item item = getContainer().getItem(itemId);
            if (item == null) {
                throw new IllegalArgumentException("Item with id " + itemId
                        + " not found in current container");
            }

            fieldGroup.setItemDataSource(item);
            editedItemId = itemId;

            for (Object propertyId : item.getItemPropertyIds()) {

                final Field<?> editor;
                if (fieldGroup.getUnboundPropertyIds().contains(propertyId)) {
                    editor = fieldGroup.buildAndBind(propertyId);
                } else {
                    editor = fieldGroup.getField(propertyId);
                }

                grid.getColumn(propertyId).getState().editorConnector = editor;

                if (editor != null) {
                    editor.setReadOnly(!isPropertyEditable(propertyId));

                    if (editor.getParent() != grid) {
                        assert editor.getParent() == null;
                        editor.setParent(grid);
                    }
                }
            }
        }

        /**
         * Cancels the currently active edit if any.
         */
        public void cancel() {
            checkDetached();
            if (isEditing()) {
                grid.getEditorRowRpc().cancel(
                        grid.getContainerDatasource().indexOfId(editedItemId));
                internalCancel();
            }
        }

        protected void internalCancel() {
            editedItemId = null;
        }

        /**
         * Returns whether this editor row is currently editing an item.
         * 
         * @return true iff this editor row is editing an item
         */
        public boolean isEditing() {
            return editedItemId != null;
        }

        /**
         * Gets the id of the item that is currently being edited.
         * 
         * @return the id of the item that is currently being edited, or
         *         <code>null</code> if no item is being edited at the moment
         */
        public Object getEditedItemId() {
            checkDetached();
            return editedItemId;
        }

        /**
         * Gets a collection of all fields bound to this editor row.
         * <p>
         * All non-editable fields (either readonly or uneditable) are in
         * read-only mode.
         * <p>
         * When {@link #editItem(Object) editItem} is called, fields are
         * automatically created and bound to any unbound properties.
         * 
         * @return a collection of all the fields bound to this editor row
         */
        Collection<Field<?>> getFields() {
            checkDetached();
            return fieldGroup.getFields();
        }

        private Container getContainer() {
            return grid.getContainerDatasource();
        }

        private void checkDetached() throws IllegalStateException {
            if (grid == null) {
                throw new IllegalStateException("The method cannot be "
                        + "processed as this " + getClass().getSimpleName()
                        + " has become detached.");
            }
        }

        private void checkPropertyExists(Object propertyId) {
            if (!getContainer().getContainerPropertyIds().contains(propertyId)) {
                throw new IllegalArgumentException("Property with id "
                        + propertyId + " is not in the current Container");
            }
        }
    }

    /**
     * An abstract base class for server-side Grid renderers.
     * {@link com.vaadin.client.ui.grid.Renderer Grid renderers}. This class
     * currently extends the AbstractExtension superclass, but this fact should
     * be regarded as an implementation detail and subject to change in a future
     * major or minor Vaadin revision.
     * 
     * @param <T>
     *            the type this renderer knows how to present
     */
    public static abstract class AbstractRenderer<T> extends AbstractExtension
            implements Renderer<T> {

        private final Class<T> presentationType;

        protected AbstractRenderer(Class<T> presentationType) {
            this.presentationType = presentationType;
        }

        /**
         * This method is inherited from AbstractExtension but should never be
         * called directly with an AbstractRenderer.
         */
        @Deprecated
        @Override
        protected Class<Grid> getSupportedParentType() {
            return Grid.class;
        }

        /**
         * This method is inherited from AbstractExtension but should never be
         * called directly with an AbstractRenderer.
         */
        @Deprecated
        @Override
        protected void extend(AbstractClientConnector target) {
            super.extend(target);
        }

        @Override
        public Class<T> getPresentationType() {
            return presentationType;
        }

        @Override
        public JsonValue encode(T value) {
            return encode(value, getPresentationType());
        }

        /**
         * Encodes the given value to JSON.
         * <p>
         * This is a helper method that can be invoked by an
         * {@link #encode(Object) encode(T)} override if serializing a value of
         * type other than {@link #getPresentationType() the presentation type}
         * is desired. For instance, a {@code Renderer<Date>} could first turn a
         * date value into a formatted string and return
         * {@code encode(dateString, String.class)}.
         * 
         * @param value
         *            the value to be encoded
         * @param type
         *            the type of the value
         * @return a JSON representation of the given value
         */
        protected <U> JsonValue encode(U value, Class<U> type) {
            return JsonCodec.encode(value, null, type,
                    getUI().getConnectorTracker()).getEncodedValue();
        }

        /**
         * Gets the item id for a row key.
         * <p>
         * A key is used to identify a particular row on both a server and a
         * client. This method can be used to get the item id for the row key
         * that the client has sent.
         * 
         * @param rowKey
         *            the row key for which to retrieve an item id
         * @return the item id corresponding to {@code key}
         */
        protected Object getItemId(String rowKey) {
            if (getParent() instanceof Grid) {
                Grid grid = (Grid) getParent();
                return grid.getKeyMapper().getItemId(rowKey);
            } else {
                throw new IllegalStateException(
                        "Renderers can be used only with Grid");
            }
        }
    }

    /**
     * The data source attached to the grid
     */
    private Container.Indexed datasource;

    /**
     * Property id to column instance mapping
     */
    private final Map<Object, Column> columns = new HashMap<Object, Column>();

    /**
     * Key generator for column server-to-client communication
     */
    private final KeyMapper<Object> columnKeys = new KeyMapper<Object>();

    /**
     * The current sort order
     */
    private final List<SortOrder> sortOrder = new ArrayList<SortOrder>();

    /**
     * Property listener for listening to changes in data source properties.
     */
    private final PropertySetChangeListener propertyListener = new PropertySetChangeListener() {

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            Collection<?> properties = new HashSet<Object>(event.getContainer()
                    .getContainerPropertyIds());

            // Cleanup columns that are no longer in grid
            List<Object> removedColumns = new LinkedList<Object>();
            for (Object columnId : columns.keySet()) {
                if (!properties.contains(columnId)) {
                    removedColumns.add(columnId);
                }
            }
            for (Object columnId : removedColumns) {
                removeColumn(columnId);
            }
            datasourceExtension.propertiesRemoved(removedColumns);

            // Add new columns
            HashSet<Object> addedPropertyIds = new HashSet<Object>();
            for (Object propertyId : properties) {
                if (!columns.containsKey(propertyId)) {
                    appendColumn(propertyId);
                    addedPropertyIds.add(propertyId);
                }
            }
            datasourceExtension.propertiesAdded(addedPropertyIds);

            Object frozenPropertyId = columnKeys
                    .get(getState(false).lastFrozenColumnId);
            if (!columns.containsKey(frozenPropertyId)) {
                setLastFrozenPropertyId(null);
            }

            // Update sortable columns
            if (event.getContainer() instanceof Sortable) {
                Collection<?> sortableProperties = ((Sortable) event
                        .getContainer()).getSortableContainerPropertyIds();
                for (Entry<Object, Column> columnEntry : columns.entrySet()) {
                    columnEntry.getValue().setSortable(
                            sortableProperties.contains(columnEntry.getKey()));
                }
            }
        }
    };

    private RpcDataProviderExtension datasourceExtension;

    /**
     * The selection model that is currently in use. Never <code>null</code>
     * after the constructor has been run.
     */
    private SelectionModel selectionModel;

    /**
     * The number of times to ignore selection state sync to the client.
     * <p>
     * This usually means that the client side has modified the selection. We
     * still want to inform the listeners that the selection has changed, but we
     * don't want to send those changes "back to the client".
     */
    private int ignoreSelectionClientSync = 0;

    private final Header header = new Header(this);
    private final Footer footer = new Footer(this);

    private EditorRow editorRow;

    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(SelectionChangeListener.class, "selectionChange",
                    SelectionChangeEvent.class);

    private static final Method SORT_ORDER_CHANGE_METHOD = ReflectTools
            .findMethod(SortOrderChangeListener.class, "sortOrderChange",
                    SortOrderChangeEvent.class);

    /**
     * Creates a new Grid with a new {@link IndexedContainer} as the datasource.
     */
    public Grid() {
        this(new IndexedContainer());
    }

    /**
     * Creates a new Grid using the given datasource.
     * 
     * @param datasource
     *            the data source for the grid
     */
    public Grid(final Container.Indexed datasource) {
        setContainerDataSource(datasource);

        setSelectionMode(SelectionMode.MULTI);
        addSelectionChangeListener(new SelectionChangeListener() {
            @Override
            public void selectionChange(SelectionChangeEvent event) {
                /*
                 * This listener nor anything else in the server side should
                 * never unpin anything from KeyMapper. Pinning is mostly a
                 * client feature and is only used when selecting something from
                 * the server side. This is to ensure that client has the
                 * correct key from server when the selected row is first
                 * loaded.
                 * 
                 * Once client has gotten info that it is supposed to select a
                 * row, it will pin the data from the client side as well and it
                 * will be unpinned once it gets deselected.
                 */

                for (Object addedItemId : event.getAdded()) {
                    if (!getKeyMapper().isPinned(addedItemId)) {
                        getKeyMapper().pin(addedItemId);
                    }
                }

                List<String> keys = getKeyMapper().getKeys(getSelectedRows());

                boolean markAsDirty = true;

                /*
                 * If this clause is true, it means that the selection event
                 * originated from the client. This means that we don't want to
                 * send the changes back to the client (markAsDirty => false).
                 */
                if (ignoreSelectionClientSync > 0) {
                    ignoreSelectionClientSync--;
                    markAsDirty = false;

                    /*
                     * Make sure that the diffstate is aware of the "undirty"
                     * modification, so that the diffs are calculated correctly
                     * the next time we actually want to send the selection
                     * state to the client.
                     */
                    JsonArray jsonKeys = Json.createArray();
                    for (int i = 0; i < keys.size(); ++i) {
                        jsonKeys.set(i, keys.get(i));
                    }
                    getUI().getConnectorTracker().getDiffState(Grid.this)
                            .put("selectedKeys", jsonKeys);
                }

                getState(markAsDirty).selectedKeys = keys;
            }
        });

        registerRpc(new GridServerRpc() {

            @Override
            public void selectionChange(List<String> selection) {
                final HashSet<Object> newSelection = new HashSet<Object>(
                        getKeyMapper().getItemIds(selection));
                final HashSet<Object> oldSelection = new HashSet<Object>(
                        getSelectedRows());

                SetView<Object> addedItemIds = Sets.difference(newSelection,
                        oldSelection);
                SetView<Object> removedItemIds = Sets.difference(oldSelection,
                        newSelection);

                if (!removedItemIds.isEmpty()) {
                    /*
                     * Since these changes come from the client, we want to
                     * modify the selection model and get that event fired to
                     * all the listeners. One of the listeners is our internal
                     * selection listener, and this tells it not to send the
                     * selection event back to the client.
                     */
                    ignoreSelectionClientSync++;

                    if (removedItemIds.size() == 1) {
                        deselect(removedItemIds.iterator().next());
                    } else {
                        assert getSelectionModel() instanceof SelectionModel.Multi : "Got multiple deselections, but the selection model is not a SelectionModel.Multi";
                        ((SelectionModel.Multi) getSelectionModel())
                                .deselect(removedItemIds);
                    }
                }

                if (!addedItemIds.isEmpty()) {
                    /*
                     * Since these changes come from the client, we want to
                     * modify the selection model and get that event fired to
                     * all the listeners. One of the listeners is our internal
                     * selection listener, and this tells it not to send the
                     * selection event back to the client.
                     */
                    ignoreSelectionClientSync++;

                    if (addedItemIds.size() == 1) {
                        select(addedItemIds.iterator().next());
                    } else {
                        assert getSelectionModel() instanceof SelectionModel.Multi : "Got multiple selections, but the selection model is not a SelectionModel.Multi";
                        ((SelectionModel.Multi) getSelectionModel())
                                .select(addedItemIds);
                    }
                }
            }

            @Override
            public void sort(String[] columnIds, SortDirection[] directions,
                    SortEventOriginator originator) {
                assert columnIds.length == directions.length;

                List<SortOrder> order = new ArrayList<SortOrder>(
                        columnIds.length);
                for (int i = 0; i < columnIds.length; i++) {
                    Object propertyId = getPropertyIdByColumnId(columnIds[i]);
                    order.add(new SortOrder(propertyId, directions[i]));
                }

                setSortOrder(order, originator);
            }
        });

        registerRpc(new EditorRowServerRpc() {

            @Override
            public void bind(int rowIndex) {
                try {
                    Object id = getContainerDatasource().getIdByIndex(rowIndex);
                    getEditorRow().internalEditItem(id);
                    getEditorRowRpc().confirmBind();
                } catch (Exception e) {
                    handleError(e);
                }
            }

            @Override
            public void cancel(int rowIndex) {
                try {
                    // For future proofing even though cannot currently fail
                    getEditorRow().internalCancel();
                } catch (Exception e) {
                    handleError(e);
                }
            }

            @Override
            public void commit(int rowIndex) {
                try {
                    getEditorRow().commit();
                    getEditorRowRpc().confirmCommit();
                } catch (Exception e) {
                    handleError(e);
                }
            }

            @Override
            public void discard(int rowIndex) {
                try {
                    getEditorRow().discard();
                } catch (Exception e) {
                    handleError(e);
                }
            }

            private void handleError(Exception e) {
                ErrorHandler handler = getEditorRow().getErrorHandler();
                if (handler == null) {
                    handler = com.vaadin.server.ErrorEvent
                            .findErrorHandler(Grid.this);
                }
                handler.error(new ConnectorErrorEvent(Grid.this, e));
            }
        });
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        try {
            header.sanityCheck();
            footer.sanityCheck();
        } catch (Exception e) {
            e.printStackTrace();
            setComponentError(new ErrorMessage() {

                @Override
                public ErrorLevel getErrorLevel() {
                    return ErrorLevel.CRITICAL;
                }

                @Override
                public String getFormattedHtmlMessage() {
                    return "Incorrectly merged cells";
                }

            });
        }

        super.beforeClientResponse(initial);
    }

    /**
     * Sets the grid data source.
     * 
     * @param container
     *            The container data source. Cannot be null.
     * @throws IllegalArgumentException
     *             if the data source is null
     */
    public void setContainerDataSource(Container.Indexed container) {

        if (container == null) {
            throw new IllegalArgumentException(
                    "Cannot set the datasource to null");
        }
        if (datasource == container) {
            return;
        }

        // Remove old listeners
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .removePropertySetChangeListener(propertyListener);
        }

        if (datasourceExtension != null) {
            removeExtension(datasourceExtension);
        }

        datasource = container;

        /*
         * This is null when this method is called the first time in the
         * constructor
         */
        if (editorRow != null) {
            editorRow.detach();
        }
        editorRow = new EditorRow(this);

        //
        // Adjust sort order
        //

        if (container instanceof Container.Sortable) {

            // If the container is sortable, go through the current sort order
            // and match each item to the sortable properties of the new
            // container. If the new container does not support an item in the
            // current sort order, that item is removed from the current sort
            // order list.
            Collection<?> sortableProps = ((Container.Sortable) getContainerDatasource())
                    .getSortableContainerPropertyIds();

            Iterator<SortOrder> i = sortOrder.iterator();
            while (i.hasNext()) {
                if (!sortableProps.contains(i.next().getPropertyId())) {
                    i.remove();
                }
            }

            sort(SortEventOriginator.API);
        } else {

            // If the new container is not sortable, we'll just re-set the sort
            // order altogether.
            clearSortOrder();
        }

        datasourceExtension = new RpcDataProviderExtension(container);
        datasourceExtension.extend(this, columnKeys);

        /*
         * selectionModel == null when the invocation comes from the
         * constructor.
         */
        if (selectionModel != null) {
            selectionModel.reset();
        }

        // Listen to changes in properties and remove columns if needed
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .addPropertySetChangeListener(propertyListener);
        }
        /*
         * activeRowHandler will be updated by the client-side request that
         * occurs on container change - no need to actively re-insert any
         * ValueChangeListeners at this point.
         */

        setLastFrozenPropertyId(null);

        if (columns.isEmpty()) {
            // Add columns
            for (Object propertyId : datasource.getContainerPropertyIds()) {
                Column column = appendColumn(propertyId);

                // Initial sorting is defined by container
                if (datasource instanceof Sortable) {
                    column.setSortable(((Sortable) datasource)
                            .getSortableContainerPropertyIds().contains(
                                    propertyId));
                }
            }
        }
    }

    /**
     * Returns the grid data source.
     * 
     * @return the container data source of the grid
     */
    public Container.Indexed getContainerDatasource() {
        return datasource;
    }

    /**
     * Returns a column based on the property id
     * 
     * @param propertyId
     *            the property id of the column
     * @return the column or <code>null</code> if not found
     */
    public Column getColumn(Object propertyId) {
        return columns.get(propertyId);
    }

    /**
     * Used internally by the {@link Grid} to get a {@link Column} by
     * referencing its generated state id. Also used by {@link Column} to verify
     * if it has been detached from the {@link Grid}. Adds a new Column to Grid.
     * Also adds the property to container with data type String, if property
     * for column does not exist in it. Default value for the new property is an
     * empty String.
     * 
     * @param propertyId
     *            the property id of the new column
     * @return the new column
     */
    public Column addColumn(Object propertyId) {
        addColumnProperty(propertyId, String.class, "");
        return getColumn(propertyId);
    }

    /**
     * Adds a new Column to Grid. Also adds the property to container with given
     * Number data type, if property for column does not exist already in it.
     * Default value for the new property is 0.
     * 
     * @param propertyId
     *            the property id of the new column
     * @return the new column
     */
    public <T extends Number> Column addColumn(Object propertyId, Class<T> type) {
        addColumnProperty(propertyId, type, 0);
        return getColumn(propertyId);
    }

    protected void addColumnProperty(Object propertyId, Class<?> type,
            Object defaultValue) {
        if (!columns.containsKey(propertyId)) {
            if (!datasource.getContainerPropertyIds().contains(propertyId)) {
                datasource.addContainerProperty(propertyId, type, defaultValue);
            } else {
                Property<?> containerProperty = datasource
                        .getContainerProperty(datasource.firstItemId(),
                                propertyId);
                if (containerProperty.getType() == type) {
                    appendColumn(propertyId);
                } else {
                    throw new IllegalStateException(
                            "DataSource already has the given property "
                                    + propertyId + " with a different type");
                }
            }
        } else {
            throw new IllegalStateException(
                    "Grid already has a column for property " + propertyId);
        }
    }

    /**
     * Removes all columns from this Grid.
     */
    public void removeAllColumns() {
        Set<Object> properties = new HashSet<Object>(columns.keySet());
        for (Object propertyId : properties) {
            removeColumn(propertyId);
        }
    }

    /**
     * Used internally by the {@link Grid} to get a {@link Column} by
     * referencing its generated state id. Also used by {@link Column} to verify
     * if it has been detached from the {@link Grid}.
     * 
     * @param columnId
     *            the client id generated for the column when the column is
     *            added to the grid
     * @return the column with the id or <code>null</code> if not found
     */
    Column getColumnByColumnId(String columnId) {
        Object propertyId = getPropertyIdByColumnId(columnId);
        return getColumn(propertyId);
    }

    /**
     * Used internally by the {@link Grid} to get a property id by referencing
     * the columns generated state id.
     * 
     * @param columnId
     *            The state id of the column
     * @return The column instance or null if not found
     */
    Object getPropertyIdByColumnId(String columnId) {
        return columnKeys.get(columnId);
    }

    @Override
    protected GridState getState() {
        return (GridState) super.getState();
    }

    @Override
    protected GridState getState(boolean markAsDirty) {
        return (GridState) super.getState(markAsDirty);
    }

    /**
     * Creates a new column based on a property id and appends it as the last
     * column.
     * 
     * @param datasourcePropertyId
     *            The property id of a property in the datasource
     */
    private Column appendColumn(Object datasourcePropertyId) {
        if (datasourcePropertyId == null) {
            throw new IllegalArgumentException("Property id cannot be null");
        }
        assert datasource.getContainerPropertyIds().contains(
                datasourcePropertyId) : "Datasource should contain the property id";

        GridColumnState columnState = new GridColumnState();
        columnState.id = columnKeys.key(datasourcePropertyId);

        Column column = new Column(this, columnState);
        columns.put(datasourcePropertyId, column);

        getState().columns.add(columnState);
        getState().columnOrder.add(columnState.id);
        header.addColumn(datasourcePropertyId);
        footer.addColumn(datasourcePropertyId);

        column.setHeaderCaption(String.valueOf(datasourcePropertyId));

        return column;
    }

    /**
     * Removes a column from Grid based on a property id.
     * 
     * @param propertyId
     *            The property id of column to be removed
     */
    public void removeColumn(Object propertyId) {
        header.removeColumn(propertyId);
        footer.removeColumn(propertyId);
        Column column = columns.remove(propertyId);
        getState().columnOrder.remove(columnKeys.key(propertyId));
        getState().columns.remove(column.getState());
        columnKeys.remove(propertyId);
        removeExtension(column.getRenderer());
    }

    /**
     * Sets a new column order for the grid. All columns which are not ordered
     * here will remain in the order they were before as the last columns of
     * grid.
     * 
     * @param propertyIds
     *            properties in the order columns should be
     */
    public void setColumnOrder(Object... propertyIds) {
        List<String> columnOrder = new ArrayList<String>();
        for (Object propertyId : propertyIds) {
            if (columns.containsKey(propertyId)) {
                columnOrder.add(columnKeys.key(propertyId));
            } else {
                throw new IllegalArgumentException(
                        "Grid does not contain column for property "
                                + String.valueOf(propertyId));
            }
        }

        List<String> stateColumnOrder = getState().columnOrder;
        if (stateColumnOrder.size() != columnOrder.size()) {
            stateColumnOrder.removeAll(columnOrder);
            columnOrder.addAll(stateColumnOrder);
        }
        getState().columnOrder = columnOrder;
    }

    /**
     * Sets (or unsets) the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the given column will be frozen in place
     * when the grid is scrolled sideways.
     * <p>
     * Reordering columns in the grid while there is a frozen column will make
     * all columns frozen that are before the frozen column. ie. If you move the
     * frozen column to be last, all columns will be frozen.
     * 
     * @param lastFrozenColumn
     *            the rightmost column to freeze, or <code>null</code> to not
     *            have any columns frozen
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    void setLastFrozenColumn(Column lastFrozenColumn) {
        /*
         * TODO: If and when Grid supports column reordering or insertion of
         * columns before other columns, make sure to mention that adding
         * columns before lastFrozenColumn will change the frozen column count
         */

        if (lastFrozenColumn == null) {
            getState().lastFrozenColumnId = null;
        } else if (columns.containsValue(lastFrozenColumn)) {
            getState().lastFrozenColumnId = lastFrozenColumn.getState().id;
        } else {
            throw new IllegalArgumentException(
                    "The given column isn't attached to this grid");
        }
    }

    /**
     * Sets (or unsets) the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the indicated property will be frozen in
     * place when the grid is scrolled sideways.
     * <p>
     * <em>Note:</em> If the container used by this grid supports a propertyId
     * <code>null</code>, it can never be defined as the last frozen column, as
     * a <code>null</code> parameter will always reset the frozen columns in
     * Grid.
     * 
     * @param propertyId
     *            the property id corresponding to the column that should be the
     *            last frozen column, or <code>null</code> to not have any
     *            columns frozen.
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    public void setLastFrozenPropertyId(Object propertyId) {
        final Column column;
        if (propertyId == null) {
            column = null;
        } else {
            column = getColumn(propertyId);
            if (column == null) {
                throw new IllegalArgumentException(
                        "property id does not exist.");
            }
        }
        setLastFrozenColumn(column);
    }

    /**
     * Gets the rightmost frozen column in the grid.
     * <p>
     * <em>Note:</em> Most often, this method returns the very value set with
     * {@link #setLastFrozenPropertyId(Object)}. This value, however, can be
     * reset to <code>null</code> if the column is detached from this grid.
     * 
     * @return the rightmost frozen column in the grid, or <code>null</code> if
     *         no columns are frozen.
     */
    public Object getLastFrozenPropertyId() {
        return columnKeys.get(getState().lastFrozenColumnId);
    }

    /**
     * Scrolls to a certain item, using {@link ScrollDestination#ANY}.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollTo(Object itemId) throws IllegalArgumentException {
        scrollTo(itemId, ScrollDestination.ANY);
    }

    /**
     * Scrolls to a certain item, using user-specified scroll destination.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @param destination
     *            value specifying desired position of scrolled-to row.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollTo(Object itemId, ScrollDestination destination)
            throws IllegalArgumentException {

        int row = datasource.indexOfId(itemId);

        if (row == -1) {
            throw new IllegalArgumentException(
                    "Item with specified ID does not exist in data source");
        }

        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToRow(row, destination);
    }

    /**
     * Scrolls to the beginning of the first data row.
     */
    public void scrollToStart() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToStart();
    }

    /**
     * Scrolls to the end of the last data row.
     */
    public void scrollToEnd() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToEnd();
    }

    /**
     * Sets the number of rows that should be visible in Grid's body, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * If Grid is currently not in {@link HeightMode#ROW}, the given value is
     * remembered, and applied once the mode is applied.
     * 
     * @param rows
     *            The height in terms of number of rows displayed in Grid's
     *            body. If Grid doesn't contain enough rows, white space is
     *            displayed instead. If <code>null</code> is given, then Grid's
     *            height is undefined
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInifinite(double)
     *             infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     */
    public void setHeightByRows(double rows) {
        if (rows <= 0.0d) {
            throw new IllegalArgumentException(
                    "More than zero rows must be shown.");
        } else if (Double.isInfinite(rows)) {
            throw new IllegalArgumentException(
                    "Grid doesn't support infinite heights");
        } else if (Double.isNaN(rows)) {
            throw new IllegalArgumentException("NaN is not a valid row count");
        }

        getState().heightByRows = rows;
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * 
     * @return the amount of rows that are being shown in Grid's body
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return getState(false).heightByRows;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em>Note:</em> This method will change the widget's size in the browser
     * only if {@link #getHeightMode()} returns {@link HeightMode#CSS}.
     * 
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit);
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via a {@code setHeight}-method, and behave as a traditional Component.
     * <p>
     * If {@link HeightMode#ROW} is given, Grid will make sure that the body
     * will display as many rows as {@link #getHeightByRows()} defines.
     * <em>Note:</em> If headers/footers are inserted or removed, the widget
     * will resize itself to still display the required amount of rows in its
     * body. It also takes the horizontal scrollbar into account.
     * 
     * @param heightMode
     *            the mode in to which Grid should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight an setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         * 
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        getState().heightMode = heightMode;
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     * 
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return getState(false).heightMode;
    }

    /* Selection related methods: */

    /**
     * Takes a new {@link SelectionModel} into use.
     * <p>
     * The SelectionModel that is previously in use will have all its items
     * deselected.
     * <p>
     * If the given SelectionModel is already in use, this method does nothing.
     * 
     * @param selectionModel
     *            the new SelectionModel to use
     * @throws IllegalArgumentException
     *             if {@code selectionModel} is <code>null</code>
     */
    public void setSelectionModel(SelectionModel selectionModel)
            throws IllegalArgumentException {
        if (selectionModel == null) {
            throw new IllegalArgumentException(
                    "Selection model may not be null");
        }

        if (this.selectionModel != selectionModel) {
            // this.selectionModel is null on init
            if (this.selectionModel != null) {
                this.selectionModel.reset();
                this.selectionModel.setGrid(null);
            }

            this.selectionModel = selectionModel;
            this.selectionModel.setGrid(this);
            this.selectionModel.reset();

            if (selectionModel.getClass().equals(SingleSelectionModel.class)) {
                getState().selectionMode = SharedSelectionMode.SINGLE;
            } else if (selectionModel.getClass().equals(
                    MultiSelectionModel.class)) {
                getState().selectionMode = SharedSelectionMode.MULTI;
            } else if (selectionModel.getClass().equals(NoSelectionModel.class)) {
                getState().selectionMode = SharedSelectionMode.NONE;
            } else {
                throw new UnsupportedOperationException("Grid currently "
                        + "supports only its own bundled selection models");
            }
        }
    }

    /**
     * Returns the currently used {@link SelectionModel}.
     * 
     * @return the currently used SelectionModel
     */
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Changes the Grid's selection mode.
     * <p>
     * Grid supports three selection modes: multiselect, single select and no
     * selection, and this is a conveniency method for choosing between one of
     * them.
     * <P>
     * Technically, this method is a shortcut that can be used instead of
     * calling {@code setSelectionModel} with a specific SelectionModel
     * instance. Grid comes with three built-in SelectionModel classes, and the
     * {@link SelectionMode} enum represents each of them.
     * <p>
     * Essentially, the two following method calls are equivalent:
     * <p>
     * <code><pre>
     * grid.setSelectionMode(SelectionMode.MULTI);
     * grid.setSelectionModel(new MultiSelectionMode());
     * </pre></code>
     * 
     * 
     * @param selectionMode
     *            the selection mode to switch to
     * @return The {@link SelectionModel} instance that was taken into use
     * @throws IllegalArgumentException
     *             if {@code selectionMode} is <code>null</code>
     * @see SelectionModel
     */
    public SelectionModel setSelectionMode(final SelectionMode selectionMode)
            throws IllegalArgumentException {
        if (selectionMode == null) {
            throw new IllegalArgumentException("selection mode may not be null");
        }
        final SelectionModel newSelectionModel = selectionMode.createModel();
        setSelectionModel(newSelectionModel);
        return newSelectionModel;
    }

    /**
     * Checks whether an item is selected or not.
     * 
     * @param itemId
     *            the item id to check for
     * @return <code>true</code> iff the item is selected
     */
    // keep this javadoc in sync with SelectionModel.isSelected
    public boolean isSelected(Object itemId) {
        return selectionModel.isSelected(itemId);
    }

    /**
     * Returns a collection of all the currently selected itemIds.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}.
     * 
     * @return a collection of all the currently selected itemIds
     */
    // keep this javadoc in sync with SelectionModel.getSelectedRows
    public Collection<Object> getSelectedRows() {
        return getSelectionModel().getSelectedRows();
    }

    /**
     * Gets the item id of the currently selected item.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}. Only
     * {@link SelectionModel.Single} is supported.
     * 
     * @return the item id of the currently selected item, or <code>null</code>
     *         if nothing is selected
     * @throws IllegalStateException
     *             if the object that is returned by
     *             {@link #getSelectionModel()} is not an instance of
     *             {@link SelectionModel.Single}
     */
    // keep this javadoc in sync with SelectionModel.Single.getSelectedRow
    public Object getSelectedRow() throws IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).getSelectedRow();
        } else {
            throw new IllegalStateException(Grid.class.getSimpleName()
                    + " does not support the 'getSelectedRow' shortcut method "
                    + "unless the selection model implements "
                    + SelectionModel.Single.class.getName()
                    + ". The current one does not ("
                    + selectionModel.getClass().getName() + ")");
        }
    }

    /**
     * Marks an item as selected.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}. Only
     * {@link SelectionModel.Single} or {@link SelectionModel.Multi} are
     * supported.
     * 
     * 
     * @param itemIds
     *            the itemId to mark as selected
     * @return <code>true</code> if the selection state changed.
     *         <code>false</code> if the itemId already was selected
     * @throws IllegalArgumentException
     *             if the {@code itemId} doesn't exist in the currently active
     *             Container
     * @throws IllegalStateException
     *             if the selection was illegal. One such reason might be that
     *             the implementation already had an item selected, and that
     *             needs to be explicitly deselected before re-selecting
     *             something
     * @throws IllegalStateException
     *             if the object that is returned by
     *             {@link #getSelectionModel()} does not implement
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    // keep this javadoc in sync with SelectionModel.Single.select
    public boolean select(Object itemId) throws IllegalArgumentException,
            IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).select(itemId);
        } else if (selectionModel instanceof SelectionModel.Multi) {
            return ((SelectionModel.Multi) selectionModel).select(itemId);
        } else {
            throw new IllegalStateException(Grid.class.getSimpleName()
                    + " does not support the 'select' shortcut method "
                    + "unless the selection model implements "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + ". The current one does not ("
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Marks an item as deselected.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}. Only
     * {@link SelectionModel.Single} and {@link SelectionModel.Multi} are
     * supported.
     * 
     * @param itemId
     *            the itemId to remove from being selected
     * @return <code>true</code> if the selection state changed.
     *         <code>false</code> if the itemId already was selected
     * @throws IllegalArgumentException
     *             if the {@code itemId} doesn't exist in the currently active
     *             Container
     * @throws IllegalStateException
     *             if the deselection was illegal. One such reason might be that
     *             the implementation already had an item selected, and that
     *             needs to be explicitly deselected before re-selecting
     *             something
     * @throws IllegalStateException
     *             if the object that is returned by
     *             {@link #getSelectionModel()} does not implement
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    // keep this javadoc in sync with SelectionModel.Single.deselect
    public boolean deselect(Object itemId) throws IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).deselect(itemId);
        } else if (selectionModel instanceof SelectionModel.Multi) {
            return ((SelectionModel.Multi) selectionModel).deselect(itemId);
        } else {
            throw new IllegalStateException(Grid.class.getSimpleName()
                    + " does not support the 'deselect' shortcut method "
                    + "unless the selection model implements "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + ". The current one does not ("
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Fires a selection change event.
     * <p>
     * <strong>Note:</strong> This is not a method that should be called by
     * application logic. This method is publicly accessible only so that
     * {@link SelectionModel SelectionModels} would be able to inform Grid of
     * these events.
     * 
     * @param addedSelections
     *            the selections that were added by this event
     * @param removedSelections
     *            the selections that were removed by this event
     */
    public void fireSelectionChangeEvent(Collection<Object> oldSelection,
            Collection<Object> newSelection) {
        fireEvent(new SelectionChangeEvent(this, oldSelection, newSelection));
    }

    @Override
    public void addSelectionChangeListener(SelectionChangeListener listener) {
        addListener(SelectionChangeEvent.class, listener,
                SELECTION_CHANGE_METHOD);
    }

    @Override
    public void removeSelectionChangeListener(SelectionChangeListener listener) {
        removeListener(SelectionChangeEvent.class, listener,
                SELECTION_CHANGE_METHOD);
    }

    /**
     * Gets the
     * {@link com.vaadin.data.RpcDataProviderExtension.DataProviderKeyMapper
     * DataProviderKeyMapper} being used by the data source.
     * 
     * @return the key mapper being used by the data source
     */
    DataProviderKeyMapper getKeyMapper() {
        return datasourceExtension.getKeyMapper();
    }

    /**
     * Adds a renderer to this grid's connector hierarchy.
     * 
     * @param renderer
     *            the renderer to add
     */
    void addRenderer(Renderer<?> renderer) {
        addExtension(renderer);
    }

    /**
     * Sets the current sort order using the fluid Sort API. Read the
     * documentation for {@link Sort} for more information.
     * 
     * @param s
     *            a sort instance
     */
    public void sort(Sort s) {
        setSortOrder(s.build());
    }

    /**
     * Sort this Grid in ascending order by a specified property.
     * 
     * @param propertyId
     *            a property ID
     */
    public void sort(Object propertyId) {
        sort(propertyId, SortDirection.ASCENDING);
    }

    /**
     * Sort this Grid in user-specified {@link SortOrder} by a property.
     * 
     * @param propertyId
     *            a property ID
     * @param direction
     *            a sort order value (ascending/descending)
     */
    public void sort(Object propertyId, SortDirection direction) {
        sort(Sort.by(propertyId, direction));
    }

    /**
     * Clear the current sort order, and re-sort the grid.
     */
    public void clearSortOrder() {
        sortOrder.clear();
        sort(false);
    }

    /**
     * Sets the sort order to use. This method throws
     * {@link IllegalStateException} if the attached container is not a
     * {@link Container.Sortable}, and {@link IllegalArgumentException} if a
     * property in the list is not recognized by the container, or if the
     * 'order' parameter is null.
     * 
     * @param order
     *            a sort order list.
     */
    public void setSortOrder(List<SortOrder> order) {
        setSortOrder(order, SortEventOriginator.API);
    }

    private void setSortOrder(List<SortOrder> order,
            SortEventOriginator originator) {
        if (!(getContainerDatasource() instanceof Container.Sortable)) {
            throw new IllegalStateException(
                    "Attached container is not sortable (does not implement Container.Sortable)");
        }

        if (order == null) {
            throw new IllegalArgumentException("Order list may not be null!");
        }

        sortOrder.clear();

        Collection<?> sortableProps = ((Container.Sortable) getContainerDatasource())
                .getSortableContainerPropertyIds();

        for (SortOrder o : order) {
            if (!sortableProps.contains(o.getPropertyId())) {
                throw new IllegalArgumentException(
                        "Property "
                                + o.getPropertyId()
                                + " does not exist or is not sortable in the current container");
            }
        }

        sortOrder.addAll(order);
        sort(originator);
    }

    /**
     * Get the current sort order list.
     * 
     * @return a sort order list
     */
    public List<SortOrder> getSortOrder() {
        return Collections.unmodifiableList(sortOrder);
    }

    /**
     * Apply sorting to data source.
     */
    private void sort(SortEventOriginator originator) {

        Container c = getContainerDatasource();
        if (c instanceof Container.Sortable) {
            Container.Sortable cs = (Container.Sortable) c;

            final int items = sortOrder.size();
            Object[] propertyIds = new Object[items];
            boolean[] directions = new boolean[items];

            String[] columnKeys = new String[items];
            SortDirection[] stateDirs = new SortDirection[items];

            for (int i = 0; i < items; ++i) {
                SortOrder order = sortOrder.get(i);

                columnKeys[i] = this.columnKeys.key(order.getPropertyId());
                stateDirs[i] = order.getDirection();

                propertyIds[i] = order.getPropertyId();
                switch (order.getDirection()) {
                case ASCENDING:
                    directions[i] = true;
                    break;
                case DESCENDING:
                    directions[i] = false;
                    break;
                default:
                    throw new IllegalArgumentException("getDirection() of "
                            + order + " returned an unexpected value");
                }
            }

            cs.sort(propertyIds, directions);

            fireEvent(new SortOrderChangeEvent(this, new ArrayList<SortOrder>(
                    sortOrder), originator));

            getState().sortColumns = columnKeys;
            getState(false).sortDirs = stateDirs;
        } else {
            throw new IllegalStateException(
                    "Container is not sortable (does not implement Container.Sortable)");
        }
    }

    /**
     * Adds a sort order change listener that gets notified when the sort order
     * changes.
     * 
     * @param listener
     *            the sort order change listener to add
     */
    public void addSortOrderChangeListener(SortOrderChangeListener listener) {
        addListener(SortOrderChangeEvent.class, listener,
                SORT_ORDER_CHANGE_METHOD);
    }

    /**
     * Removes a sort order change listener previously added using
     * {@link #addSortOrderChangeListener(SortOrderChangeListener)}.
     * 
     * @param listener
     *            the sort order change listener to remove
     */
    public void removeSortOrderChangeListener(SortOrderChangeListener listener) {
        removeListener(SortOrderChangeEvent.class, listener,
                SORT_ORDER_CHANGE_METHOD);
    }

    /* Grid Headers */

    /**
     * Returns the header section of this grid. The default header contains a
     * single row displaying the column captions.
     * 
     * @return the header
     */
    protected Header getHeader() {
        return header;
    }

    /**
     * Gets the header row at given index.
     * 
     * @param rowIndex
     *            0 based index for row. Counted from top to bottom
     * @return header row at given index
     * @throws IllegalArgumentException
     *             if no row exists at given index
     */
    public HeaderRow getHeaderRow(int rowIndex) {
        return header.getRow(rowIndex);
    }

    /**
     * Inserts a new row at the given position to the header section. Shifts the
     * row currently at that position and any subsequent rows down (adds one to
     * their indices).
     * 
     * @param index
     *            the position at which to insert the row
     * @return the new row
     * 
     * @throws IllegalArgumentException
     *             if the index is less than 0 or greater than row count
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow addHeaderRowAt(int index) {
        return header.addRowAt(index);
    }

    /**
     * Adds a new row at the bottom of the header section.
     * 
     * @return the new row
     * @see #prependRow()
     * @see #addRowAt(int)
     * @see #removeRow(StaticRow)
     * @see #removeRow(int)
     */
    public HeaderRow appendHeaderRow() {
        return header.appendRow();
    }

    /**
     * Returns the current default row of the header section. The default row is
     * a special header row providing a user interface for sorting columns.
     * Setting a header text for column updates cells in the default header.
     * 
     * @return the default row or null if no default row set
     */
    public HeaderRow getDefaultHeaderRow() {
        return header.getDefaultRow();
    }

    /**
     * Gets the row count for the header section.
     * 
     * @return row count
     */
    public int getHeaderRowCount() {
        return header.getRowCount();
    }

    /**
     * Adds a new row at the top of the header section.
     * 
     * @return the new row
     * @see #appendHeaderRow()
     * @see #addHeaderRowAt(int)
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow prependHeaderRow() {
        return header.prependRow();
    }

    /**
     * Removes the given row from the header section.
     * 
     * @param row
     *            the row to be removed
     * 
     * @throws IllegalArgumentException
     *             if the row does not exist in this section
     * @see #removeHeaderRow(int)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(HeaderRow row) {
        header.removeRow(row);
    }

    /**
     * Removes the row at the given position from the header section.
     * 
     * @param index
     *            the position of the row
     * 
     * @throws IllegalArgumentException
     *             if no row exists at given index
     * @see #removeHeaderRow(HeaderRow)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(int rowIndex) {
        header.removeRow(rowIndex);
    }

    /**
     * Sets the default row of the header. The default row is a special header
     * row providing a user interface for sorting columns.
     * 
     * @param row
     *            the new default row, or null for no default row
     * 
     * @throws IllegalArgumentException
     *             header does not contain the row
     */
    public void setDefaultHeaderRow(HeaderRow row) {
        header.setDefaultRow(row);
    }

    /**
     * Sets the visibility of the header section.
     * 
     * @param visible
     *            true to show header section, false to hide
     */
    public void setHeaderVisible(boolean visible) {
        header.setVisible(visible);
    }

    /**
     * Returns the visibility of the header section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isHeaderVisible() {
        return header.isVisible();
    }

    /* Grid Footers */

    /**
     * Returns the footer section of this grid. The default header contains a
     * single row displaying the column captions.
     * 
     * @return the footer
     */
    protected Footer getFooter() {
        return footer;
    }

    /**
     * Gets the footer row at given index.
     * 
     * @param rowIndex
     *            0 based index for row. Counted from top to bottom
     * @return footer row at given index
     * @throws IllegalArgumentException
     *             if no row exists at given index
     */
    public FooterRow getFooterRow(int rowIndex) {
        return footer.getRow(rowIndex);
    }

    /**
     * Inserts a new row at the given position to the footer section. Shifts the
     * row currently at that position and any subsequent rows down (adds one to
     * their indices).
     * 
     * @param index
     *            the position at which to insert the row
     * @return the new row
     * 
     * @throws IllegalArgumentException
     *             if the index is less than 0 or greater than row count
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow addFooterRowAt(int index) {
        return footer.addRowAt(index);
    }

    /**
     * Adds a new row at the bottom of the footer section.
     * 
     * @return the new row
     * @see #prependRow()
     * @see #addRowAt(int)
     * @see #removeRow(StaticRow)
     * @see #removeRow(int)
     */
    public FooterRow appendFooterRow() {
        return footer.appendRow();
    }

    /**
     * Gets the row count for the footer.
     * 
     * @return row count
     */
    public int getFooterRowCount() {
        return footer.getRowCount();
    }

    /**
     * Adds a new row at the top of the footer section.
     * 
     * @return the new row
     * @see #appendFooterRow()
     * @see #addFooterRowAt(int)
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow prependFooterRow() {
        return footer.prependRow();
    }

    /**
     * Removes the given row from the footer section.
     * 
     * @param row
     *            the row to be removed
     * 
     * @throws IllegalArgumentException
     *             if the row does not exist in this section
     * @see #removeFooterRow(int)
     * @see #addFooterRowAt(int)
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     */
    public void removeFooterRow(FooterRow row) {
        footer.removeRow(row);
    }

    /**
     * Removes the row at the given position from the footer section.
     * 
     * @param index
     *            the position of the row
     * 
     * @throws IllegalArgumentException
     *             if no row exists at given index
     * @see #removeFooterRow(FooterRow)
     * @see #addFooterRowAt(int)
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     */
    public void removeFooterRow(int rowIndex) {
        footer.removeRow(rowIndex);
    }

    /**
     * Sets the visibility of the footer section.
     * 
     * @param visible
     *            true to show footer section, false to hide
     */
    public void setFooterVisible(boolean visible) {
        footer.setVisible(visible);
    }

    /**
     * Returns the visibility of the footer section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isFooterVisible() {
        return footer.isVisible();
    }

    @Override
    public Iterator<Component> iterator() {
        List<Component> componentList = new ArrayList<Component>();

        Header header = getHeader();
        for (int i = 0; i < header.getRowCount(); ++i) {
            HeaderRow row = header.getRow(i);
            for (Object propId : datasource.getContainerPropertyIds()) {
                HeaderCell cell = row.getCell(propId);
                if (cell.getCellState().type == GridStaticCellType.WIDGET) {
                    componentList.add(cell.getComponent());
                }
            }
        }

        Footer footer = getFooter();
        for (int i = 0; i < footer.getRowCount(); ++i) {
            FooterRow row = footer.getRow(i);
            for (Object propId : datasource.getContainerPropertyIds()) {
                FooterCell cell = row.getCell(propId);
                if (cell.getCellState().type == GridStaticCellType.WIDGET) {
                    componentList.add(cell.getComponent());
                }
            }
        }

        componentList.addAll(getEditorRow().getFields());
        return componentList.iterator();
    }

    @Override
    public boolean isRendered(Component childComponent) {
        if (getEditorRow().getFields().contains(childComponent)) {
            // Only render editor row fields if the editor is open
            return getEditorRow().isEditing();
        } else {
            // TODO Header and footer components should also only be rendered if
            // the header/footer is visible
            return true;
        }
    }

    /**
     * Gets the editor row configuration object.
     * 
     * @return the editor row configuration object
     */
    public EditorRow getEditorRow() {
        return editorRow;
    }

    EditorRowClientRpc getEditorRowRpc() {
        return getRpcProxy(EditorRowClientRpc.class);
    }
}
