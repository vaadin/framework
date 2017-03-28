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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.SectionState;
import com.vaadin.shared.ui.grid.SectionState.CellState;
import com.vaadin.shared.ui.grid.SectionState.RowState;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * Represents the header or footer section of a Grid.
 *
 * @author Vaadin Ltd.
 *
 * @param <ROW>
 *            the type of the rows in the section
 *
 * @since 8.0
 */
public abstract class StaticSection<ROW extends StaticSection.StaticRow<?>>
        implements Serializable {

    /**
     * Abstract base class for Grid header and footer rows.
     *
     * @param <CELL>
     *            the type of the cells in the row
     */
    public abstract static class StaticRow<CELL extends StaticCell>
            implements Serializable {

        private final RowState rowState = new RowState();
        private final StaticSection<?> section;
        private final Map<String, CELL> cells = new LinkedHashMap<>();

        /**
         * Creates a new row belonging to the given section.
         *
         * @param section
         *            the section of the row
         */
        protected StaticRow(StaticSection<?> section) {
            this.section = section;
        }

        /**
         * Creates and returns a new instance of the cell type.
         *
         * @return the created cell
         */
        protected abstract CELL createCell();

        /**
         * Returns the declarative tag name used for the cells in this row.
         *
         * @return the cell tag name
         */
        protected abstract String getCellTagName();

        /**
         * Adds a cell to this section, corresponding to the given user-defined
         * column id.
         *
         * @param columnId
         *            the id of the column for which to add a cell
         */
        protected void addCell(String columnId) {
            Column<?, ?> column = section.getGrid().getColumn(columnId);
            Objects.requireNonNull(column,
                    "No column matching given identifier");
            addCell(column);
        }

        /**
         * Adds a cell to this section for given column.
         *
         * @param column
         *            the column for which to add a cell
         */
        protected void addCell(Column<?, ?> column) {
            if (!section.getGrid().getColumns().contains(column)) {
                throw new IllegalArgumentException(
                        "Given column does not exist in this Grid");
            }
            internalAddCell(section.getInternalIdForColumn(column));
        }

        /**
         * Adds a cell to this section, corresponding to the given internal
         * column id.
         *
         * @param internalId
         *            the internal id of the column for which to add a cell
         */
        protected void internalAddCell(String internalId) {
            CELL cell = createCell();
            cell.setColumnId(internalId);
            cells.put(internalId, cell);
            rowState.cells.put(internalId, cell.getCellState());
        }

        /**
         * Removes the cell from this section that corresponds to the given
         * column id. If there is no such cell, does nothing.
         *
         * @param columnId
         *            the id of the column from which to remove the cell
         */
        protected void removeCell(String columnId) {
            CELL cell = cells.remove(columnId);
            if (cell != null) {
                rowState.cells.remove(columnId);
                for (Iterator<Set<String>> iterator = rowState.cellGroups
                        .values().iterator(); iterator.hasNext();) {
                    Set<String> group = iterator.next();
                    group.remove(columnId);
                    if (group.size() < 2) {
                        iterator.remove();
                    }
                }
                cell.detach();
            }
        }

        /**
         * Returns the shared state of this row.
         *
         * @return the row state
         */
        protected RowState getRowState() {
            return rowState;
        }

        /**
         * Returns the cell in this section that corresponds to the given column
         * id.
         *
         * @see Column#setId(String)
         *
         * @param columnId
         *            the id of the column
         * @return the cell for the given column
         *
         * @throws IllegalArgumentException
         *             if no cell was found for the column id
         */
        public CELL getCell(String columnId) {
            Column<?, ?> column = section.getGrid().getColumn(columnId);
            Objects.requireNonNull(column,
                    "No column matching given identifier");
            return getCell(column);
        }

        /**
         * Returns the cell in this section that corresponds to the given
         * column.
         *
         * @param column
         *            the column
         * @return the cell for the given column
         *
         * @throws IllegalArgumentException
         *             if no cell was found for the column
         */
        public CELL getCell(Column<?, ?> column) {
            return internalGetCell(section.getInternalIdForColumn(column));
        }

        /**
         * Returns the custom style name for this row.
         *
         * @return the style name or null if no style name has been set
         */
        public String getStyleName() {
            return getRowState().styleName;
        }

        /**
         * Sets a custom style name for this row.
         *
         * @param styleName
         *            the style name to set or null to not use any style name
         */
        public void setStyleName(String styleName) {
            getRowState().styleName = styleName;
        }

        /**
         * Returns the cell in this section that corresponds to the given
         * internal column id.
         *
         * @param internalId
         *            the internal id of the column
         * @return the cell for the given column
         *
         * @throws IllegalArgumentException
         *             if no cell was found for the column id
         */
        protected CELL internalGetCell(String internalId) {
            CELL cell = cells.get(internalId);
            if (cell == null) {
                throw new IllegalArgumentException(
                        "No cell found for column id " + internalId);
            }
            return cell;
        }

        /**
         * Reads the declarative design from the given table row element.
         *
         * @since 7.5.0
         * @param trElement
         *            Element to read design from
         * @param designContext
         *            the design context
         * @throws DesignException
         *             if the given table row contains unexpected children
         */
        protected void readDesign(Element trElement,
                DesignContext designContext) throws DesignException {
            Elements cellElements = trElement.children();
            for (int i = 0; i < cellElements.size(); i++) {
                Element element = cellElements.get(i);
                if (!element.tagName().equals(getCellTagName())) {
                    throw new DesignException(
                            "Unexpected element in tr while expecting "
                                    + getCellTagName() + ": "
                                    + element.tagName());
                }

                int colspan = DesignAttributeHandler.readAttribute("colspan",
                        element.attributes(), 1, int.class);

                String columnIdsString = DesignAttributeHandler.readAttribute(
                        "column-ids", element.attributes(), "", String.class);
                if (columnIdsString.trim().isEmpty()) {
                    throw new DesignException(
                            "Unexpected 'column-ids' attribute value '"
                                    + columnIdsString
                                    + "'. It cannot be empty and must "
                                    + "be comma separated column identifiers");
                }
                String[] columnIds = columnIdsString.split(",");
                if (columnIds.length != colspan) {
                    throw new DesignException(
                            "Unexpected 'colspan' attribute value '" + colspan
                                    + "' whereas there is " + columnIds.length
                                    + " column identifiers specified : '"
                                    + columnIdsString + "'");
                }

                Stream.of(columnIds).forEach(this::addCell);

                Stream<String> idsStream = Stream.of(columnIds);
                if (colspan > 1) {
                    CELL newCell = createCell();
                    addMergedCell(createCell(),
                            idsStream.collect(Collectors.toSet()));
                    newCell.readDesign(element, designContext);
                } else {
                    idsStream.map(this::getCell).forEach(
                            cell -> cell.readDesign(element, designContext));
                }
            }
        }

        /**
         * Writes the declarative design to the given table row element.
         *
         * @since 7.5.0
         * @param trElement
         *            Element to write design to
         * @param designContext
         *            the design context
         */
        protected void writeDesign(Element trElement,
                DesignContext designContext) {
            Set<String> visited = new HashSet<>();
            for (Entry<String, CELL> entry : cells.entrySet()) {
                if (visited.contains(entry.getKey())) {
                    continue;
                }
                visited.add(entry.getKey());

                Element cellElement = trElement.appendElement(getCellTagName());

                Optional<Entry<CellState, Set<String>>> groupCell = getRowState().cellGroups
                        .entrySet().stream().filter(groupEntry -> groupEntry
                                .getValue().contains(entry.getKey()))
                        .findFirst();
                Stream<String> columnIds = Stream.of(entry.getKey());

                if (groupCell.isPresent()) {
                    Set<String> orderedSet = new LinkedHashSet<>(
                            cells.keySet());
                    orderedSet.retainAll(groupCell.get().getValue());
                    columnIds = orderedSet.stream();
                    visited.addAll(orderedSet);
                    cellElement.attr("colspan", "" + orderedSet.size());
                    writeCellState(cellElement, designContext,
                            groupCell.get().getKey());
                } else {
                    writeCellState(cellElement, designContext,
                            entry.getValue().getCellState());
                }
                cellElement.attr("column-ids",
                        columnIds.map(section::getColumnByInternalId)
                                .map(Column::getId)
                                .collect(Collectors.joining(",")));
            }
        }

        /**
         *
         * Writes declarative design for the cell using its {@code state} to the
         * given table cell element.
         * <p>
         * The method is used instead of StaticCell::writeDesign because
         * sometimes there is no a reference to the cell which should be written
         * (merged cell) but only its state is available (the cell is virtual
         * and is not stored).
         *
         * @param cellElement
         *            Element to write design to
         * @param context
         *            the design context
         * @param state
         *            a cell state
         */
        protected void writeCellState(Element cellElement,
                DesignContext context, CellState state) {
            switch (state.type) {
            case TEXT:
                cellElement.attr("plain-text", true);
                cellElement
                        .appendText(Optional.ofNullable(state.text).orElse(""));
                break;
            case HTML:
                cellElement.append(Optional.ofNullable(state.html).orElse(""));
                break;
            case WIDGET:
                cellElement.appendChild(
                        context.createElement((Component) state.connector));
                break;
            }
        }

        void detach() {
            for (CELL cell : cells.values()) {
                cell.detach();
            }
            for (CellState cellState : rowState.cellGroups.keySet()) {
                if (cellState.type == GridStaticCellType.WIDGET
                        && cellState.connector != null) {
                    ((Component) cellState.connector).setParent(null);
                    cellState.connector = null;
                }
            }
        }

        void checkIfAlreadyMerged(String columnId) {
            for (Set<String> cellGroup : getRowState().cellGroups.values()) {
                if (cellGroup.contains(columnId)) {
                    throw new IllegalArgumentException(
                            "Cell " + columnId + " is already merged");
                }
            }
            if (!cells.containsKey(columnId)) {
                throw new IllegalArgumentException(
                        "Cell " + columnId + " does not exist on this row");
            }
        }

        void addMergedCell(CELL newCell, Set<String> columnGroup) {
            rowState.cellGroups.put(newCell.getCellState(), columnGroup);
        }

        public Collection<? extends Component> getComponents() {
            List<Component> components = new ArrayList<>();
            cells.forEach((id, cell) -> {
                if (cell.getCellType() == GridStaticCellType.WIDGET) {
                    components.add(cell.getComponent());
                }
            });
            rowState.cellGroups.forEach((cellState, columnIds) -> {
                if (cellState.connector != null) {
                    components.add((Component) cellState.connector);
                }
            });
            return components;
        }
    }

    /**
     * A header or footer cell. Has a simple textual caption.
     */
    abstract static class StaticCell implements Serializable {

        private final CellState cellState = new CellState();
        private final StaticRow<?> row;

        protected StaticCell(StaticRow<?> row) {
            this.row = row;
        }

        void setColumnId(String id) {
            cellState.columnId = id;
        }

        public String getColumnId() {
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

        /**
         * Returns the shared state of this cell.
         *
         * @return the cell state
         */
        protected CellState getCellState() {
            return cellState;
        }

        /**
         * Sets the textual caption of this cell.
         *
         * @param text
         *            a plain text caption, not null
         */
        public void setText(String text) {
            Objects.requireNonNull(text, "text cannot be null");
            removeComponentIfPresent();
            cellState.text = text;
            cellState.type = GridStaticCellType.TEXT;
            row.section.markAsDirty();
        }

        /**
         * Returns the textual caption of this cell.
         *
         * @return the plain text caption
         */
        public String getText() {
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
         *            the html to set, not null
         */
        public void setHtml(String html) {
            Objects.requireNonNull(html, "html cannot be null");
            removeComponentIfPresent();
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
         *            the component to set, not null
         */
        public void setComponent(Component component) {
            Objects.requireNonNull(component, "component cannot be null");
            removeComponentIfPresent();
            component.setParent(row.section.getGrid());
            cellState.connector = component;
            cellState.type = GridStaticCellType.WIDGET;
            row.section.markAsDirty();
        }

        /**
         * Returns the type of content stored in this cell.
         *
         * @return cell content type
         */
        public GridStaticCellType getCellType() {
            return cellState.type;
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
         *            the style name to set or null to not use any style name
         */
        public void setStyleName(String styleName) {
            cellState.styleName = styleName;
            row.section.markAsDirty();
        }

        /**
         * Reads the declarative design from the given table cell element.
         *
         * @since 7.5.0
         * @param cellElement
         *            Element to read design from
         * @param designContext
         *            the design context
         */
        protected void readDesign(Element cellElement,
                DesignContext designContext) {
            if (!cellElement.hasAttr("plain-text")) {
                if (cellElement.children().size() > 0
                        && cellElement.child(0).tagName().contains("-")) {
                    setComponent(
                            designContext.readDesign(cellElement.child(0)));
                } else {
                    setHtml(cellElement.html());
                }
            } else {
                // text – need to unescape HTML entities
                setText(DesignFormatter.decodeFromTextNode(cellElement.html()));
            }
        }

        private void removeComponentIfPresent() {
            Component component = (Component) cellState.connector;
            if (component != null) {
                component.setParent(null);
                cellState.connector = null;
            }
        }

        void detach() {
            removeComponentIfPresent();
        }
    }

    private final List<ROW> rows = new ArrayList<>();

    /**
     * Creates a new row instance.
     *
     * @return the new row
     */
    protected abstract ROW createRow();

    /**
     * Returns the shared state of this section.
     *
     * @param markAsDirty
     *            {@code true} to mark the state as modified, {@code false}
     *            otherwise
     * @return the section state
     */
    protected abstract SectionState getState(boolean markAsDirty);

    protected abstract Grid<?> getGrid();

    protected abstract Column<?, ?> getColumnByInternalId(String internalId);

    protected abstract String getInternalIdForColumn(Column<?, ?> column);

    /**
     * Marks the state of this section as modified.
     */
    protected void markAsDirty() {
        getState(true);
    }

    /**
     * Adds a new row at the given index.
     *
     * @param index
     *            the index of the new row
     * @return the added row
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index > getRowCount()}
     */
    public ROW addRowAt(int index) {
        ROW row = createRow();
        rows.add(index, row);
        getState(true).rows.add(index, row.getRowState());

        getGrid().getColumns().stream().forEach(row::addCell);

        return row;
    }

    /**
     * Removes the row at the given index.
     *
     * @param index
     *            the index of the row to remove
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index >= getRowCount()}
     */
    public void removeRow(int index) {
        ROW row = rows.remove(index);
        row.detach();
        getState(true).rows.remove(index);
    }

    /**
     * Removes the given row from this section.
     *
     * @param row
     *            the row to remove, not null
     * @throws IllegalArgumentException
     *             if this section does not contain the row
     */
    public void removeRow(Object row) {
        Objects.requireNonNull(row, "row cannot be null");
        int index = rows.indexOf(row);
        if (index < 0) {
            throw new IllegalArgumentException(
                    "Section does not contain the given row");
        }
        removeRow(index);
    }

    /**
     * Returns the row at the given index.
     *
     * @param index
     *            the index of the row
     * @return the row at the index
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index >= getRowCount()}
     */
    public ROW getRow(int index) {
        return rows.get(index);
    }

    /**
     * Returns the number of rows in this section.
     *
     * @return the number of rows
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Adds a cell corresponding to the given column id to this section.
     *
     * @param columnId
     *            the id of the column for which to add a cell
     */
    public void addColumn(String columnId) {
        for (ROW row : rows) {
            row.internalAddCell(columnId);
        }
    }

    /**
     * Removes the cell corresponding to the given column id.
     *
     * @param columnId
     *            the id of the column whose cell to remove
     */
    public void removeColumn(String columnId) {
        for (ROW row : rows) {
            row.removeCell(columnId);
        }
        markAsDirty();
    }

    /**
     * Writes the declarative design to the given table section element.
     *
     * @param tableSectionElement
     *            Element to write design to
     * @param designContext
     *            the design context
     */
    public void writeDesign(Element tableSectionElement,
            DesignContext designContext) {
        for (ROW row : getRows()) {
            Element tr = tableSectionElement.appendElement("tr");
            row.writeDesign(tr, designContext);
        }
    }

    /**
     * Reads the declarative design from the given table section element.
     *
     * @since 7.5.0
     * @param tableSectionElement
     *            Element to read design from
     * @param designContext
     *            the design context
     * @throws DesignException
     *             if the table section contains unexpected children
     */
    public void readDesign(Element tableSectionElement,
            DesignContext designContext) throws DesignException {
        while (getRowCount() > 0) {
            removeRow(0);
        }

        for (Element row : tableSectionElement.children()) {
            if (!row.tagName().equals("tr")) {
                throw new DesignException("Unexpected element in "
                        + tableSectionElement.tagName() + ": " + row.tagName());
            }
            addRowAt(getRowCount()).readDesign(row, designContext);
        }
    }

    /**
     * Returns an unmodifiable list of the rows in this section.
     *
     * @return the rows in this section
     */
    protected List<ROW> getRows() {
        return Collections.unmodifiableList(rows);
    }

}
