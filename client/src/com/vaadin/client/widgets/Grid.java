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
package com.vaadin.client.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.Focusable;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.ui.FocusUtil;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.dd.DragAndDropHandler;
import com.vaadin.client.ui.dd.DragAndDropHandler.DragAndDropCallback;
import com.vaadin.client.ui.dd.DragHandle;
import com.vaadin.client.ui.dd.DragHandle.DragHandleCallback;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widget.escalator.ColumnConfiguration;
import com.vaadin.client.widget.escalator.EscalatorUpdater;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widget.escalator.Row;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.escalator.RowVisibilityChangeEvent;
import com.vaadin.client.widget.escalator.RowVisibilityChangeHandler;
import com.vaadin.client.widget.escalator.ScrollbarBundle.Direction;
import com.vaadin.client.widget.escalator.Spacer;
import com.vaadin.client.widget.escalator.SpacerUpdater;
import com.vaadin.client.widget.grid.AutoScroller;
import com.vaadin.client.widget.grid.AutoScroller.AutoScrollerCallback;
import com.vaadin.client.widget.grid.AutoScroller.ScrollAxis;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.CellStyleGenerator;
import com.vaadin.client.widget.grid.DataAvailableEvent;
import com.vaadin.client.widget.grid.DataAvailableHandler;
import com.vaadin.client.widget.grid.DefaultEditorEventHandler;
import com.vaadin.client.widget.grid.DetailsGenerator;
import com.vaadin.client.widget.grid.EditorHandler;
import com.vaadin.client.widget.grid.EditorHandler.EditorRequest;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.HeightAwareDetailsGenerator;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.grid.RowReference;
import com.vaadin.client.widget.grid.RowStyleGenerator;
import com.vaadin.client.widget.grid.events.AbstractGridKeyEventHandler;
import com.vaadin.client.widget.grid.events.AbstractGridMouseEventHandler;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.grid.events.BodyDoubleClickHandler;
import com.vaadin.client.widget.grid.events.BodyKeyDownHandler;
import com.vaadin.client.widget.grid.events.BodyKeyPressHandler;
import com.vaadin.client.widget.grid.events.BodyKeyUpHandler;
import com.vaadin.client.widget.grid.events.ColumnReorderEvent;
import com.vaadin.client.widget.grid.events.ColumnReorderHandler;
import com.vaadin.client.widget.grid.events.ColumnResizeEvent;
import com.vaadin.client.widget.grid.events.ColumnResizeHandler;
import com.vaadin.client.widget.grid.events.ColumnVisibilityChangeEvent;
import com.vaadin.client.widget.grid.events.ColumnVisibilityChangeHandler;
import com.vaadin.client.widget.grid.events.FooterClickHandler;
import com.vaadin.client.widget.grid.events.FooterDoubleClickHandler;
import com.vaadin.client.widget.grid.events.FooterKeyDownHandler;
import com.vaadin.client.widget.grid.events.FooterKeyPressHandler;
import com.vaadin.client.widget.grid.events.FooterKeyUpHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widget.grid.events.GridDoubleClickEvent;
import com.vaadin.client.widget.grid.events.GridKeyDownEvent;
import com.vaadin.client.widget.grid.events.GridKeyPressEvent;
import com.vaadin.client.widget.grid.events.GridKeyUpEvent;
import com.vaadin.client.widget.grid.events.HeaderClickHandler;
import com.vaadin.client.widget.grid.events.HeaderDoubleClickHandler;
import com.vaadin.client.widget.grid.events.HeaderKeyDownHandler;
import com.vaadin.client.widget.grid.events.HeaderKeyPressHandler;
import com.vaadin.client.widget.grid.events.HeaderKeyUpHandler;
import com.vaadin.client.widget.grid.events.ScrollEvent;
import com.vaadin.client.widget.grid.events.ScrollHandler;
import com.vaadin.client.widget.grid.events.SelectAllEvent;
import com.vaadin.client.widget.grid.events.SelectAllHandler;
import com.vaadin.client.widget.grid.selection.HasSelectionHandlers;
import com.vaadin.client.widget.grid.selection.MultiSelectionRenderer;
import com.vaadin.client.widget.grid.selection.SelectionEvent;
import com.vaadin.client.widget.grid.selection.SelectionHandler;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SelectionModel.Multi;
import com.vaadin.client.widget.grid.selection.SelectionModel.Single;
import com.vaadin.client.widget.grid.selection.SelectionModelMulti;
import com.vaadin.client.widget.grid.selection.SelectionModelNone;
import com.vaadin.client.widget.grid.selection.SelectionModelSingle;
import com.vaadin.client.widget.grid.sort.Sort;
import com.vaadin.client.widget.grid.sort.SortEvent;
import com.vaadin.client.widget.grid.sort.SortHandler;
import com.vaadin.client.widget.grid.sort.SortOrder;
import com.vaadin.client.widgets.Escalator.AbstractRowContainer;
import com.vaadin.client.widgets.Escalator.SubPartArguments;
import com.vaadin.client.widgets.Grid.Editor.State;
import com.vaadin.client.widgets.Grid.StaticSection.StaticCell;
import com.vaadin.client.widgets.Grid.StaticSection.StaticRow;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.util.SharedUtil;

/**
 * A data grid view that supports columns and lazy loading of data rows from a
 * data source.
 * 
 * <h1>Columns</h1>
 * <p>
 * Each column in Grid is represented by a {@link Column}. Each
 * {@code GridColumn} has a custom implementation for
 * {@link Column#getValue(Object)} that gets the row object as an argument, and
 * returns the value for that particular column, extracted from the row object.
 * <p>
 * Each column also has a Renderer. Its function is to take the value that is
 * given by the {@code GridColumn} and display it to the user. A simple column
 * might have a {@link com.vaadin.client.renderers.TextRenderer TextRenderer}
 * that simply takes in a {@code String} and displays it as the cell's content.
 * A more complex renderer might be
 * {@link com.vaadin.client.renderers.ProgressBarRenderer ProgressBarRenderer}
 * that takes in a floating point number, and displays a progress bar instead,
 * based on the given number.
 * <p>
 * <em>See:</em> {@link #addColumn(Column)}, {@link #addColumn(Column, int)} and
 * {@link #addColumns(Column...)}. <em>Also</em>
 * {@link Column#setRenderer(Renderer)}.
 * 
 * <h1>Data Sources</h1>
 * <p>
 * Grid gets its data from a {@link DataSource}, providing row objects to Grid
 * from a user-defined endpoint. It can be either a local in-memory data source
 * (e.g. {@link com.vaadin.client.widget.grid.datasources.ListDataSource
 * ListDataSource}) or even a remote one, retrieving data from e.g. a REST API
 * (see {@link com.vaadin.client.data.AbstractRemoteDataSource
 * AbstractRemoteDataSource}).
 * 
 * 
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Grid<T> extends ResizeComposite implements
        HasSelectionHandlers<T>, SubPartAware, DeferredWorker, Focusable,
        com.google.gwt.user.client.ui.Focusable, HasWidgets, HasEnabled {

    private static final String STYLE_NAME = "v-grid";

    private static final String SELECT_ALL_CHECKBOX_CLASSNAME = "-select-all-checkbox";

    /**
     * Abstract base class for Grid header and footer sections.
     * 
     * @since 7.5.0
     * 
     * @param <ROWTYPE>
     *            the type of the rows in the section
     */
    public abstract static class StaticSection<ROWTYPE extends StaticSection.StaticRow<?>> {

        /**
         * A header or footer cell. Has a simple textual caption.
         * 
         */
        public static class StaticCell {

            private Object content = null;

            private int colspan = 1;

            private StaticSection<?> section;

            private GridStaticCellType type = GridStaticCellType.TEXT;

            private String styleName = null;

            /**
             * Sets the text displayed in this cell.
             * 
             * @param text
             *            a plain text caption
             */
            public void setText(String text) {
                this.content = text;
                this.type = GridStaticCellType.TEXT;
                section.requestSectionRefresh();
            }

            /**
             * Returns the text displayed in this cell.
             * 
             * @return the plain text caption
             */
            public String getText() {
                if (type != GridStaticCellType.TEXT) {
                    throw new IllegalStateException(
                            "Cannot fetch Text from a cell with type " + type);
                }
                return (String) content;
            }

            protected StaticSection<?> getSection() {
                assert section != null;
                return section;
            }

            protected void setSection(StaticSection<?> section) {
                this.section = section;
            }

            /**
             * Returns the amount of columns the cell spans. By default is 1.
             * 
             * @return The amount of columns the cell spans.
             */
            public int getColspan() {
                return colspan;
            }

            /**
             * Sets the amount of columns the cell spans. Must be more or equal
             * to 1. By default is 1.
             * 
             * @param colspan
             *            the colspan to set
             */
            public void setColspan(int colspan) {
                if (colspan < 1) {
                    throw new IllegalArgumentException(
                            "Colspan cannot be less than 1");
                }

                this.colspan = colspan;
                section.requestSectionRefresh();
            }

            /**
             * Returns the html inside the cell.
             * 
             * @throws IllegalStateException
             *             if trying to retrive HTML from a cell with a type
             *             other than {@link GridStaticCellType#HTML}.
             * @return the html content of the cell.
             */
            public String getHtml() {
                if (type != GridStaticCellType.HTML) {
                    throw new IllegalStateException(
                            "Cannot fetch HTML from a cell with type " + type);
                }
                return (String) content;
            }

            /**
             * Sets the content of the cell to the provided html. All previous
             * content is discarded and the cell type is set to
             * {@link GridStaticCellType#HTML}.
             * 
             * @param html
             *            The html content of the cell
             */
            public void setHtml(String html) {
                this.content = html;
                this.type = GridStaticCellType.HTML;
                section.requestSectionRefresh();
            }

            /**
             * Returns the widget in the cell.
             * 
             * @throws IllegalStateException
             *             if the cell is not {@link GridStaticCellType#WIDGET}
             * 
             * @return the widget in the cell
             */
            public Widget getWidget() {
                if (type != GridStaticCellType.WIDGET) {
                    throw new IllegalStateException(
                            "Cannot fetch Widget from a cell with type " + type);
                }
                return (Widget) content;
            }

            /**
             * Set widget as the content of the cell. The type of the cell
             * becomes {@link GridStaticCellType#WIDGET}. All previous content
             * is discarded.
             * 
             * @param widget
             *            The widget to add to the cell. Should not be
             *            previously attached anywhere (widget.getParent ==
             *            null).
             */
            public void setWidget(Widget widget) {
                if (this.content == widget) {
                    return;
                }

                if (this.content instanceof Widget) {
                    // Old widget in the cell, detach it first
                    section.getGrid().detachWidget((Widget) this.content);
                }
                this.content = widget;
                this.type = GridStaticCellType.WIDGET;
                section.requestSectionRefresh();
            }

            /**
             * Returns the type of the cell.
             * 
             * @return the type of content the cell contains.
             */
            public GridStaticCellType getType() {
                return type;
            }

            /**
             * Returns the custom style name for this cell.
             * 
             * @return the style name or null if no style name has been set
             */
            public String getStyleName() {
                return styleName;
            }

            /**
             * Sets a custom style name for this cell.
             * 
             * @param styleName
             *            the style name to set or null to not use any style
             *            name
             */
            public void setStyleName(String styleName) {
                this.styleName = styleName;
                section.requestSectionRefresh();

            }

            /**
             * Called when the cell is detached from the row
             * 
             * @since 7.6.3
             */
            void detach() {
                if (this.content instanceof Widget) {
                    // Widget in the cell, detach it
                    section.getGrid().detachWidget((Widget) this.content);
                }
            }
        }

        /**
         * Abstract base class for Grid header and footer rows.
         * 
         * @param <CELLTYPE>
         *            the type of the cells in the row
         */
        public abstract static class StaticRow<CELLTYPE extends StaticCell> {

            private Map<Column<?, ?>, CELLTYPE> cells = new HashMap<Column<?, ?>, CELLTYPE>();

            private StaticSection<?> section;

            /**
             * Map from set of spanned columns to cell meta data.
             */
            private Map<Set<Column<?, ?>>, CELLTYPE> cellGroups = new HashMap<Set<Column<?, ?>>, CELLTYPE>();

            /**
             * A custom style name for the row or null if none is set.
             */
            private String styleName = null;

            /**
             * Returns the cell on given GridColumn. If the column is merged
             * returned cell is the cell for the whole group.
             * 
             * @param column
             *            the column in grid
             * @return the cell on given column, merged cell for merged columns,
             *         null if not found
             */
            public CELLTYPE getCell(Column<?, ?> column) {
                Set<Column<?, ?>> cellGroup = getCellGroupForColumn(column);
                if (cellGroup != null) {
                    return cellGroups.get(cellGroup);
                }
                return cells.get(column);
            }

            /**
             * Returns <code>true</code> if this row contains spanned cells.
             * 
             * @since 7.5.0
             * @return does this row contain spanned cells
             */
            public boolean hasSpannedCells() {
                return !cellGroups.isEmpty();
            }

            /**
             * Merges columns cells in a row
             * 
             * @param columns
             *            the columns which header should be merged
             * @return the remaining visible cell after the merge, or the cell
             *         on first column if all are hidden
             */
            public CELLTYPE join(Column<?, ?>... columns) {
                if (columns.length <= 1) {
                    throw new IllegalArgumentException(
                            "You can't merge less than 2 columns together.");
                }

                HashSet<Column<?, ?>> columnGroup = new HashSet<Column<?, ?>>();
                // NOTE: this doesn't care about hidden columns, those are
                // filtered in calculateColspans()
                for (Column<?, ?> column : columns) {
                    if (!cells.containsKey(column)) {
                        throw new IllegalArgumentException(
                                "Given column does not exists on row " + column);
                    } else if (getCellGroupForColumn(column) != null) {
                        throw new IllegalStateException(
                                "Column is already in a group.");
                    }
                    columnGroup.add(column);
                }

                CELLTYPE joinedCell = createCell();
                cellGroups.put(columnGroup, joinedCell);
                joinedCell.setSection(getSection());

                calculateColspans();

                return joinedCell;
            }

            /**
             * Merges columns cells in a row
             * 
             * @param cells
             *            The cells to merge. Must be from the same row.
             * @return The remaining visible cell after the merge, or the first
             *         cell if all columns are hidden
             */
            public CELLTYPE join(CELLTYPE... cells) {
                if (cells.length <= 1) {
                    throw new IllegalArgumentException(
                            "You can't merge less than 2 cells together.");
                }

                Column<?, ?>[] columns = new Column<?, ?>[cells.length];

                int j = 0;
                for (Column<?, ?> column : this.cells.keySet()) {
                    CELLTYPE cell = this.cells.get(column);
                    if (!this.cells.containsValue(cells[j])) {
                        throw new IllegalArgumentException(
                                "Given cell does not exists on row");
                    } else if (cell.equals(cells[j])) {
                        columns[j++] = column;
                        if (j == cells.length) {
                            break;
                        }
                    }
                }

                return join(columns);
            }

            private Set<Column<?, ?>> getCellGroupForColumn(Column<?, ?> column) {
                for (Set<Column<?, ?>> group : cellGroups.keySet()) {
                    if (group.contains(column)) {
                        return group;
                    }
                }
                return null;
            }

            void calculateColspans() {
                // Reset all cells
                for (CELLTYPE cell : this.cells.values()) {
                    cell.setColspan(1);
                }
                // Set colspan for grouped cells
                for (Set<Column<?, ?>> group : cellGroups.keySet()) {
                    if (!checkMergedCellIsContinuous(group)) {
                        // on error simply break the merged cell
                        cellGroups.get(group).setColspan(1);
                    } else {
                        int colSpan = 0;
                        for (Column<?, ?> column : group) {
                            if (!column.isHidden()) {
                                colSpan++;
                            }
                        }
                        // colspan can't be 0
                        cellGroups.get(group).setColspan(Math.max(1, colSpan));
                    }
                }

            }

            private boolean checkMergedCellIsContinuous(
                    Set<Column<?, ?>> mergedCell) {
                // no matter if hidden or not, just check for continuous order
                final List<Column<?, ?>> columnOrder = new ArrayList<Column<?, ?>>(
                        section.grid.getColumns());

                if (!columnOrder.containsAll(mergedCell)) {
                    return false;
                }

                for (int i = 0; i < columnOrder.size(); ++i) {
                    if (!mergedCell.contains(columnOrder.get(i))) {
                        continue;
                    }

                    for (int j = 1; j < mergedCell.size(); ++j) {
                        if (!mergedCell.contains(columnOrder.get(i + j))) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }

            protected void addCell(Column<?, ?> column) {
                CELLTYPE cell = createCell();
                cell.setSection(getSection());
                cells.put(column, cell);
            }

            protected void removeCell(Column<?, ?> column) {
                cells.remove(column);
            }

            protected abstract CELLTYPE createCell();

            protected StaticSection<?> getSection() {
                return section;
            }

            protected void setSection(StaticSection<?> section) {
                this.section = section;
            }

            /**
             * Returns the custom style name for this row.
             * 
             * @return the style name or null if no style name has been set
             */
            public String getStyleName() {
                return styleName;
            }

            /**
             * Sets a custom style name for this row.
             * 
             * @param styleName
             *            the style name to set or null to not use any style
             *            name
             */
            public void setStyleName(String styleName) {
                this.styleName = styleName;
                section.requestSectionRefresh();
            }

            /**
             * Called when the row is detached from the grid
             * 
             * @since 7.6.3
             */
            void detach() {
                // Avoid calling detach twice for a merged cell
                HashSet<CELLTYPE> cells = new HashSet<CELLTYPE>();
                for (Column<?, ?> column : getSection().grid.getColumns()) {
                    cells.add(getCell(column));
                }
                for (CELLTYPE cell : cells) {
                    cell.detach();
                }
            }
        }

        private Grid<?> grid;

        private List<ROWTYPE> rows = new ArrayList<ROWTYPE>();

        private boolean visible = true;

        /**
         * Creates and returns a new instance of the row type.
         * 
         * @return the created row
         */
        protected abstract ROWTYPE createRow();

        /**
         * Informs the grid that this section should be re-rendered.
         * <p>
         * <b>Note</b> that re-render means calling update() on each cell,
         * preAttach()/postAttach()/preDetach()/postDetach() is not called as
         * the cells are not removed from the DOM.
         */
        protected abstract void requestSectionRefresh();

        /**
         * Sets the visibility of the whole section.
         * 
         * @param visible
         *            true to show this section, false to hide
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
            requestSectionRefresh();
        }

        /**
         * Returns the visibility of this section.
         * 
         * @return true if visible, false otherwise.
         */
        public boolean isVisible() {
            return visible;
        }

        /**
         * Inserts a new row at the given position. Shifts the row currently at
         * that position and any subsequent rows down (adds one to their
         * indices).
         * 
         * @param index
         *            the position at which to insert the row
         * @return the new row
         * 
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         * @see #appendRow()
         * @see #prependRow()
         * @see #removeRow(int)
         * @see #removeRow(StaticRow)
         */
        public ROWTYPE addRowAt(int index) {
            ROWTYPE row = createRow();
            row.setSection(this);
            for (int i = 0; i < getGrid().getColumnCount(); ++i) {
                row.addCell(grid.getColumn(i));
            }
            rows.add(index, row);

            requestSectionRefresh();
            return row;
        }

        /**
         * Adds a new row at the top of this section.
         * 
         * @return the new row
         * @see #appendRow()
         * @see #addRowAt(int)
         * @see #removeRow(int)
         * @see #removeRow(StaticRow)
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
         * @see #removeRow(int)
         * @see #removeRow(StaticRow)
         */
        public ROWTYPE appendRow() {
            return addRowAt(rows.size());
        }

        /**
         * Removes the row at the given position.
         * 
         * @param index
         *            the position of the row
         * 
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         * @see #addRowAt(int)
         * @see #appendRow()
         * @see #prependRow()
         * @see #removeRow(StaticRow)
         */
        public void removeRow(int index) {
            ROWTYPE row = rows.remove(index);
            row.detach();
            requestSectionRefresh();
        }

        /**
         * Removes the given row from the section.
         * 
         * @param row
         *            the row to be removed
         * 
         * @throws IllegalArgumentException
         *             if the row does not exist in this section
         * @see #addRowAt(int)
         * @see #appendRow()
         * @see #prependRow()
         * @see #removeRow(int)
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
         * Returns the row at the given position.
         * 
         * @param index
         *            the position of the row
         * @return the row with the given index
         * 
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         */
        public ROWTYPE getRow(int index) {
            try {
                return rows.get(index);
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Row with index " + index
                        + " does not exist");
            }
        }

        /**
         * Returns the number of rows in this section.
         * 
         * @return the number of rows
         */
        public int getRowCount() {
            return rows.size();
        }

        protected List<ROWTYPE> getRows() {
            return rows;
        }

        protected int getVisibleRowCount() {
            return isVisible() ? getRowCount() : 0;
        }

        protected void addColumn(Column<?, ?> column) {
            for (ROWTYPE row : rows) {
                row.addCell(column);
            }
        }

        protected void removeColumn(Column<?, ?> column) {
            for (ROWTYPE row : rows) {
                row.removeCell(column);
            }
        }

        protected void setGrid(Grid<?> grid) {
            this.grid = grid;
        }

        protected Grid<?> getGrid() {
            assert grid != null;
            return grid;
        }

        protected void updateColSpans() {
            for (ROWTYPE row : rows) {
                if (row.hasSpannedCells()) {
                    row.calculateColspans();
                }
            }
        }
    }

    /**
     * Represents the header section of a Grid. A header consists of a single
     * header row containing a header cell for each column. Each cell has a
     * simple textual caption.
     */
    protected static class Header extends StaticSection<HeaderRow> {
        private HeaderRow defaultRow;

        private boolean markAsDirty = false;

        @Override
        public void removeRow(int index) {
            HeaderRow removedRow = getRow(index);
            super.removeRow(index);
            if (removedRow == defaultRow) {
                setDefaultRow(null);
            }
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
            if (row != null && !getRows().contains(row)) {
                throw new IllegalArgumentException(
                        "Cannot set a default row that does not exist in the container");
            }
            if (defaultRow != null) {
                defaultRow.setDefault(false);
            }
            if (row != null) {
                row.setDefault(true);
            }

            defaultRow = row;
            requestSectionRefresh();
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
        protected HeaderRow createRow() {
            return new HeaderRow();
        }

        @Override
        protected void requestSectionRefresh() {
            markAsDirty = true;

            /*
             * Defer the refresh so if we multiple times call refreshSection()
             * (for example when updating cell values) we only get one actual
             * refresh in the end.
             */
            Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    if (markAsDirty) {
                        markAsDirty = false;
                        getGrid().refreshHeader();
                    }
                }
            });
        }

        /**
         * Returns the events consumed by the header
         * 
         * @return a collection of BrowserEvents
         */
        public Collection<String> getConsumedEvents() {
            return Arrays.asList(BrowserEvents.TOUCHSTART,
                    BrowserEvents.TOUCHMOVE, BrowserEvents.TOUCHEND,
                    BrowserEvents.TOUCHCANCEL, BrowserEvents.CLICK);
        }

        @Override
        protected void addColumn(Column<?, ?> column) {
            super.addColumn(column);

            // Add default content for new columns.
            if (defaultRow != null) {
                column.setDefaultHeaderContent(defaultRow.getCell(column));
            }
        }
    }

    /**
     * A single row in a grid header section.
     * 
     */
    public static class HeaderRow extends StaticSection.StaticRow<HeaderCell> {

        private boolean isDefault = false;

        protected void setDefault(boolean isDefault) {
            this.isDefault = isDefault;
            if (isDefault) {
                for (Column<?, ?> column : getSection().grid.getColumns()) {
                    column.setDefaultHeaderContent(getCell(column));
                }
            }
        }

        public boolean isDefault() {
            return isDefault;
        }

        @Override
        protected HeaderCell createCell() {
            return new HeaderCell();
        }
    }

    /**
     * A single cell in a grid header row. Has a caption and, if it's in a
     * default row, a drag handle.
     */
    public static class HeaderCell extends StaticSection.StaticCell {
    }

    /**
     * Represents the footer section of a Grid. The footer is always empty.
     */
    protected static class Footer extends StaticSection<FooterRow> {
        private boolean markAsDirty = false;

        @Override
        protected FooterRow createRow() {
            return new FooterRow();
        }

        @Override
        protected void requestSectionRefresh() {
            markAsDirty = true;

            /*
             * Defer the refresh so if we multiple times call refreshSection()
             * (for example when updating cell values) we only get one actual
             * refresh in the end.
             */
            Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    if (markAsDirty) {
                        markAsDirty = false;
                        getGrid().refreshFooter();
                    }
                }
            });
        }
    }

    /**
     * A single cell in a grid Footer row. Has a textual caption.
     * 
     */
    public static class FooterCell extends StaticSection.StaticCell {
    }

    /**
     * A single row in a grid Footer section.
     * 
     */
    public static class FooterRow extends StaticSection.StaticRow<FooterCell> {

        @Override
        protected FooterCell createCell() {
            return new FooterCell();
        }
    }

    private static class EditorRequestImpl<T> implements EditorRequest<T> {

        /**
         * A callback interface used to notify the invoker of the editor handler
         * of completed editor requests.
         * 
         * @param <T>
         *            the row data type
         */
        public static interface RequestCallback<T> {
            /**
             * The method that must be called when the request has been
             * processed correctly.
             * 
             * @param request
             *            the original request object
             */
            public void onSuccess(EditorRequest<T> request);

            /**
             * The method that must be called when processing the request has
             * produced an aborting error.
             * 
             * @param request
             *            the original request object
             */
            public void onError(EditorRequest<T> request);
        }

        private Grid<T> grid;
        private final int rowIndex;
        private final int columnIndex;
        private RequestCallback<T> callback;
        private boolean completed = false;

        public EditorRequestImpl(Grid<T> grid, int rowIndex, int columnIndex,
                RequestCallback<T> callback) {
            this.grid = grid;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.callback = callback;
        }

        @Override
        public int getRowIndex() {
            return rowIndex;
        }

        @Override
        public int getColumnIndex() {
            return columnIndex;
        }

        @Override
        public T getRow() {
            return grid.getDataSource().getRow(rowIndex);
        }

        @Override
        public Grid<T> getGrid() {
            return grid;
        }

        @Override
        public Widget getWidget(Grid.Column<?, T> column) {
            Widget w = grid.getEditorWidget(column);
            assert w != null;
            return w;
        }

        private void complete(String errorMessage,
                Collection<Column<?, T>> errorColumns) {
            if (completed) {
                throw new IllegalStateException(
                        "An EditorRequest must be completed exactly once");
            }
            completed = true;

            if (errorColumns == null) {
                errorColumns = Collections.emptySet();
            }
            grid.getEditor().setEditorError(errorMessage, errorColumns);
        }

        @Override
        public void success() {
            complete(null, null);
            if (callback != null) {
                callback.onSuccess(this);
            }
        }

        @Override
        public void failure(String errorMessage,
                Collection<Grid.Column<?, T>> errorColumns) {
            complete(errorMessage, errorColumns);
            if (callback != null) {
                callback.onError(this);
            }
        }

        @Override
        public boolean isCompleted() {
            return completed;
        }
    }

    /**
     * A wrapper for native DOM events originating from Grid. In addition to the
     * native event, contains a {@link CellReference} instance specifying which
     * cell the event originated from.
     * 
     * @since 7.6
     * @param <T>
     *            The row type of the grid
     */
    public static class GridEvent<T> {
        private Event event;
        private EventCellReference<T> cell;

        protected GridEvent(Event event, EventCellReference<T> cell) {
            this.event = event;
            this.cell = cell;
        }

        /**
         * Returns the wrapped DOM event.
         * 
         * @return the DOM event
         */
        public Event getDomEvent() {
            return event;
        }

        /**
         * Returns the Grid cell this event originated from.
         * 
         * @return the event cell
         */
        public EventCellReference<T> getCell() {
            return cell;
        }

        /**
         * Returns the Grid instance this event originated from.
         * 
         * @return the grid
         */
        public Grid<T> getGrid() {
            return cell.getGrid();
        }
    }

    /**
     * A wrapper for native DOM events related to the {@link Editor Grid editor}
     * .
     * 
     * @since 7.6
     * @param <T>
     *            the row type of the grid
     */
    public static class EditorDomEvent<T> extends GridEvent<T> {

        private final Widget editorWidget;

        protected EditorDomEvent(Event event, EventCellReference<T> cell,
                Widget editorWidget) {
            super(event, cell);
            this.editorWidget = editorWidget;
        }

        /**
         * Returns the editor of the Grid this event originated from.
         * 
         * @return the related editor instance
         */
        public Editor<T> getEditor() {
            return getGrid().getEditor();
        }

        /**
         * Returns the currently focused editor widget.
         * 
         * @return the focused editor widget or {@code null} if not editable
         */
        public Widget getEditorWidget() {
            return editorWidget;
        }

        /**
         * Returns the row index the editor is open at. If the editor is not
         * open, returns -1.
         * 
         * @return the index of the edited row or -1 if editor is not open
         */
        public int getRowIndex() {
            return getEditor().rowIndex;
        }

        /**
         * Returns the column index the editor was opened at. If the editor is
         * not open, returns -1.
         * 
         * @return the column index or -1 if editor is not open
         */
        public int getFocusedColumnIndex() {
            return getEditor().focusedColumnIndex;
        }
    }

    /**
     * An editor UI for Grid rows. A single Grid row at a time can be opened for
     * editing.
     * 
     * @since 7.6
     * @param <T>
     *            the row type of the grid
     */
    public static class Editor<T> implements DeferredWorker {

        public static final int KEYCODE_SHOW = KeyCodes.KEY_ENTER;
        public static final int KEYCODE_HIDE = KeyCodes.KEY_ESCAPE;

        private static final String ERROR_CLASS_NAME = "error";
        private static final String NOT_EDITABLE_CLASS_NAME = "not-editable";

        ScheduledCommand fieldFocusCommand = new ScheduledCommand() {
            private int count = 0;

            @Override
            public void execute() {
                Element focusedElement = WidgetUtil.getFocusedElement();
                if (focusedElement == grid.getElement()
                        || focusedElement == Document.get().getBody()
                        || count > 2) {
                    focusColumn(focusedColumnIndex);
                } else {
                    ++count;
                    Scheduler.get().scheduleDeferred(this);
                }
            }
        };

        /**
         * A handler for events related to the Grid editor. Responsible for
         * opening, moving or closing the editor based on the received event.
         * 
         * @since 7.6
         * @author Vaadin Ltd
         * @param <T>
         *            the row type of the grid
         */
        public interface EventHandler<T> {
            /**
             * Handles editor-related events in an appropriate way. Opens,
             * moves, or closes the editor based on the given event.
             * 
             * @param event
             *            the received event
             * @return true if the event was handled and nothing else should be
             *         done, false otherwise
             */
            boolean handleEvent(EditorDomEvent<T> event);
        }

        protected enum State {
            INACTIVE, ACTIVATING, BINDING, ACTIVE, SAVING
        }

        private Grid<T> grid;
        private EditorHandler<T> handler;
        private EventHandler<T> eventHandler = GWT
                .create(DefaultEditorEventHandler.class);

        private DivElement editorOverlay = DivElement.as(DOM.createDiv());
        private DivElement cellWrapper = DivElement.as(DOM.createDiv());
        private DivElement frozenCellWrapper = DivElement.as(DOM.createDiv());

        private DivElement messageAndButtonsWrapper = DivElement.as(DOM
                .createDiv());

        private DivElement messageWrapper = DivElement.as(DOM.createDiv());
        private DivElement buttonsWrapper = DivElement.as(DOM.createDiv());

        // Element which contains the error message for the editor
        // Should only be added to the DOM when there's a message to show
        private DivElement message = DivElement.as(DOM.createDiv());

        private Map<Column<?, T>, Widget> columnToWidget = new HashMap<Column<?, T>, Widget>();
        private List<HandlerRegistration> focusHandlers = new ArrayList<HandlerRegistration>();

        private boolean enabled = false;
        private State state = State.INACTIVE;
        private int rowIndex = -1;
        private int focusedColumnIndex = -1;
        private String styleName = null;

        private HandlerRegistration hScrollHandler;
        private HandlerRegistration vScrollHandler;

        private final Button saveButton;
        private final Button cancelButton;

        private static final int SAVE_TIMEOUT_MS = 5000;
        private final Timer saveTimeout = new Timer() {
            @Override
            public void run() {
                getLogger().warning(
                        "Editor save action is taking longer than expected ("
                                + SAVE_TIMEOUT_MS + "ms). Does your "
                                + EditorHandler.class.getSimpleName()
                                + " remember to call success() or fail()?");
            }
        };

        private final EditorRequestImpl.RequestCallback<T> saveRequestCallback = new EditorRequestImpl.RequestCallback<T>() {
            @Override
            public void onSuccess(EditorRequest<T> request) {
                if (state == State.SAVING) {
                    cleanup();
                    cancel();
                    grid.clearSortOrder();
                }
            }

            @Override
            public void onError(EditorRequest<T> request) {
                if (state == State.SAVING) {
                    cleanup();
                }
            }

            private void cleanup() {
                state = State.ACTIVE;
                setButtonsEnabled(true);
                saveTimeout.cancel();
            }
        };

        private static final int BIND_TIMEOUT_MS = 5000;
        private final Timer bindTimeout = new Timer() {
            @Override
            public void run() {
                getLogger().warning(
                        "Editor bind action is taking longer than expected ("
                                + BIND_TIMEOUT_MS + "ms). Does your "
                                + EditorHandler.class.getSimpleName()
                                + " remember to call success() or fail()?");
            }
        };

        private final EditorRequestImpl.RequestCallback<T> bindRequestCallback = new EditorRequestImpl.RequestCallback<T>() {
            @Override
            public void onSuccess(EditorRequest<T> request) {
                if (state == State.BINDING) {
                    state = State.ACTIVE;
                    bindTimeout.cancel();

                    rowIndex = request.getRowIndex();
                    focusedColumnIndex = request.getColumnIndex();
                    if (focusedColumnIndex >= 0) {
                        // Update internal focus of Grid
                        grid.focusCell(rowIndex, focusedColumnIndex);
                    }

                    showOverlay();
                }
            }

            @Override
            public void onError(EditorRequest<T> request) {
                if (state == State.BINDING) {
                    if (rowIndex == -1) {
                        doCancel();
                    } else {
                        state = State.ACTIVE;
                        // TODO: Maybe restore focus?
                    }
                    bindTimeout.cancel();
                }
            }
        };

        /** A set of all the columns that display an error flag. */
        private final Set<Column<?, T>> columnErrors = new HashSet<Grid.Column<?, T>>();
        private boolean buffered = true;

        /** Original position of editor */
        private double originalTop;
        /** Original scroll position of grid when editor was opened */
        private double originalScrollTop;
        private RowHandle<T> pinnedRowHandle;

        public Editor() {
            saveButton = new Button();
            saveButton.setText(GridConstants.DEFAULT_SAVE_CAPTION);
            saveButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    save();
                }
            });

            cancelButton = new Button();
            cancelButton.setText(GridConstants.DEFAULT_CANCEL_CAPTION);
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    cancel();
                }
            });
        }

        public void setEditorError(String errorMessage,
                Collection<Column<?, T>> errorColumns) {

            if (errorMessage == null) {
                message.removeFromParent();
            } else {
                message.setInnerText(errorMessage);
                if (message.getParentElement() == null) {
                    messageWrapper.appendChild(message);
                }
            }
            // In unbuffered mode only show message wrapper if there is an error
            if (!isBuffered()) {
                setMessageAndButtonsWrapperVisible(errorMessage != null);
            }

            if (state == State.ACTIVE || state == State.SAVING) {
                for (Column<?, T> c : grid.getColumns()) {
                    grid.getEditor().setEditorColumnError(c,
                            errorColumns.contains(c));
                }
            }
        }

        public int getRow() {
            return rowIndex;
        }

        /**
         * If a cell of this Grid had focus once this editRow call was
         * triggered, the editor component at the previously focused column
         * index will be focused.
         * 
         * If a Grid cell was not focused prior to calling this method, it will
         * be equivalent to {@code editRow(rowIndex, -1)}.
         * 
         * @see #editRow(int, int)
         */
        public void editRow(int rowIndex) {
            // Focus the last focused column in the editor iff grid or its child
            // was focused before the edit request
            Cell focusedCell = grid.cellFocusHandler.getFocusedCell();
            Element focusedElement = WidgetUtil.getFocusedElement();
            if (focusedCell != null && focusedElement != null
                    && grid.getElement().isOrHasChild(focusedElement)) {
                editRow(rowIndex, focusedCell.getColumn());
            } else {
                editRow(rowIndex, -1);
            }
        }

        /**
         * Opens the editor over the row with the given index and attempts to
         * focus the editor widget in the given column index. Does not move
         * focus if the widget is not focusable or if the column index is -1.
         * 
         * @param rowIndex
         *            the index of the row to be edited
         * @param columnIndex
         *            the column index of the editor widget that should be
         *            initially focused or -1 to not set focus
         * 
         * @throws IllegalStateException
         *             if this editor is not enabled
         * @throws IllegalStateException
         *             if this editor is already in edit mode and in buffered
         *             mode
         * 
         * @since 7.5
         */
        public void editRow(final int rowIndex, final int columnIndex) {
            if (!enabled) {
                throw new IllegalStateException(
                        "Cannot edit row: editor is not enabled");
            }

            if (isWorkPending()) {
                // Request pending a response, don't move try to start another
                // request.
                return;
            }

            if (state != State.INACTIVE && this.rowIndex != rowIndex) {
                if (isBuffered()) {
                    throw new IllegalStateException(
                            "Cannot edit row: editor already in edit mode");
                } else if (!columnErrors.isEmpty()) {
                    // Don't move row if errors are present

                    // FIXME: Should attempt bind if error field values have
                    // changed.

                    return;
                }
            }
            if (columnIndex >= grid.getVisibleColumns().size()) {
                throw new IllegalArgumentException("Edited column index "
                        + columnIndex
                        + " was bigger than visible column count.");
            }

            if (this.rowIndex == rowIndex && focusedColumnIndex == columnIndex) {
                // NO-OP
                return;
            }

            if (this.rowIndex == rowIndex) {
                if (focusedColumnIndex != columnIndex) {
                    if (columnIndex >= grid.getFrozenColumnCount()) {
                        // Scroll to new focused column.
                        grid.getEscalator().scrollToColumn(columnIndex,
                                ScrollDestination.ANY, 0);
                    }

                    focusedColumnIndex = columnIndex;
                }

                updateHorizontalScrollPosition();

                // Update Grid internal focus and focus widget if possible
                if (focusedColumnIndex >= 0) {
                    grid.focusCell(rowIndex, focusedColumnIndex);
                    focusColumn(focusedColumnIndex);
                }

                // No need to request anything from the editor handler.
                return;
            }
            state = State.ACTIVATING;

            final Escalator escalator = grid.getEscalator();
            if (escalator.getVisibleRowRange().contains(rowIndex)) {
                show(rowIndex, columnIndex);
            } else {
                vScrollHandler = grid.addScrollHandler(new ScrollHandler() {
                    @Override
                    public void onScroll(ScrollEvent event) {
                        if (escalator.getVisibleRowRange().contains(rowIndex)) {
                            show(rowIndex, columnIndex);
                            vScrollHandler.removeHandler();
                        }
                    }
                });
                grid.scrollToRow(rowIndex,
                        isBuffered() ? ScrollDestination.MIDDLE
                                : ScrollDestination.ANY);
            }
        }

        /**
         * Cancels the currently active edit and hides the editor. Any changes
         * that are not {@link #save() saved} are lost.
         * 
         * @throws IllegalStateException
         *             if this editor is not enabled
         * @throws IllegalStateException
         *             if this editor is not in edit mode
         */
        public void cancel() {
            if (!enabled) {
                throw new IllegalStateException(
                        "Cannot cancel edit: editor is not enabled");
            }
            if (state == State.INACTIVE) {
                throw new IllegalStateException(
                        "Cannot cancel edit: editor is not in edit mode");
            }
            handler.cancel(new EditorRequestImpl<T>(grid, rowIndex,
                    focusedColumnIndex, null));
            doCancel();
        }

        private void doCancel() {
            hideOverlay();
            state = State.INACTIVE;
            rowIndex = -1;
            focusedColumnIndex = -1;
            grid.getEscalator().setScrollLocked(Direction.VERTICAL, false);
            updateSelectionCheckboxesAsNeeded(true);
        }

        private void updateSelectionCheckboxesAsNeeded(boolean isEnabled) {
            // FIXME: This is too much guessing. Define a better way to do this.
            if (grid.selectionColumn != null
                    && grid.selectionColumn.getRenderer() instanceof MultiSelectionRenderer) {
                grid.refreshBody();
                CheckBox checkBox = (CheckBox) grid.getDefaultHeaderRow()
                        .getCell(grid.selectionColumn).getWidget();
                checkBox.setEnabled(isEnabled);
            }
        }

        /**
         * Saves any unsaved changes to the data source and hides the editor.
         * 
         * @throws IllegalStateException
         *             if this editor is not enabled
         * @throws IllegalStateException
         *             if this editor is not in edit mode
         */
        public void save() {
            if (!enabled) {
                throw new IllegalStateException(
                        "Cannot save: editor is not enabled");
            }
            if (state != State.ACTIVE) {
                throw new IllegalStateException(
                        "Cannot save: editor is not in edit mode");
            }

            state = State.SAVING;
            setButtonsEnabled(false);
            saveTimeout.schedule(SAVE_TIMEOUT_MS);
            EditorRequest<T> request = new EditorRequestImpl<T>(grid, rowIndex,
                    focusedColumnIndex, saveRequestCallback);
            handler.save(request);
            updateSelectionCheckboxesAsNeeded(true);
        }

        /**
         * Returns the handler responsible for binding data and editor widgets
         * to this editor.
         * 
         * @return the editor handler or null if not set
         */
        public EditorHandler<T> getHandler() {
            return handler;
        }

        /**
         * Sets the handler responsible for binding data and editor widgets to
         * this editor.
         * 
         * @param rowHandler
         *            the new editor handler
         * 
         * @throws IllegalStateException
         *             if this editor is currently in edit mode
         */
        public void setHandler(EditorHandler<T> rowHandler) {
            if (state != State.INACTIVE) {
                throw new IllegalStateException(
                        "Cannot set EditorHandler: editor is currently in edit mode");
            }
            handler = rowHandler;
        }

        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets the enabled state of this editor.
         * 
         * @param enabled
         *            true if enabled, false otherwise
         * 
         * @throws IllegalStateException
         *             if in edit mode and trying to disable
         * @throws IllegalStateException
         *             if the editor handler is not set
         */
        public void setEnabled(boolean enabled) {
            if (enabled == false && state != State.INACTIVE) {
                throw new IllegalStateException(
                        "Cannot disable: editor is in edit mode");
            } else if (enabled == true && getHandler() == null) {
                throw new IllegalStateException(
                        "Cannot enable: EditorHandler not set");
            }
            this.enabled = enabled;
        }

        protected void show(int rowIndex, int columnIndex) {
            if (state == State.ACTIVATING) {
                state = State.BINDING;
                bindTimeout.schedule(BIND_TIMEOUT_MS);
                EditorRequest<T> request = new EditorRequestImpl<T>(grid,
                        rowIndex, columnIndex, bindRequestCallback);
                handler.bind(request);
                grid.getEscalator().setScrollLocked(Direction.VERTICAL,
                        isBuffered());
                updateSelectionCheckboxesAsNeeded(false);
            }
        }

        protected void setGrid(final Grid<T> grid) {
            assert grid != null : "Grid cannot be null";
            assert this.grid == null : "Can only attach editor to Grid once";

            this.grid = grid;
        }

        protected State getState() {
            return state;
        }

        protected void setState(State state) {
            this.state = state;
        }

        /**
         * Returns the editor widget associated with the given column. If the
         * editor is not active or the column is not
         * {@link Grid.Column#isEditable() editable}, returns null.
         * 
         * @param column
         *            the column
         * @return the widget if the editor is open and the column is editable,
         *         null otherwise
         */
        protected Widget getWidget(Column<?, T> column) {
            return columnToWidget.get(column);
        }

        /**
         * Equivalent to {@code showOverlay()}. The argument is ignored.
         * 
         * @param unused
         *            ignored argument
         * 
         * @deprecated As of 7.5, use {@link #showOverlay()} instead.
         */
        @Deprecated
        protected void showOverlay(TableRowElement unused) {
            showOverlay();
        }

        /**
         * Opens the editor overlay over the table row indicated by
         * {@link #getRow()}.
         * 
         * @since 7.5
         */
        protected void showOverlay() {
            // Ensure overlay is hidden initially
            hideOverlay();
            DivElement gridElement = DivElement.as(grid.getElement());

            TableRowElement tr = grid.getEscalator().getBody()
                    .getRowElement(rowIndex);

            hScrollHandler = grid.addScrollHandler(new ScrollHandler() {
                @Override
                public void onScroll(ScrollEvent event) {
                    updateHorizontalScrollPosition();
                    updateVerticalScrollPosition();
                }
            });

            gridElement.appendChild(editorOverlay);
            editorOverlay.appendChild(frozenCellWrapper);
            editorOverlay.appendChild(cellWrapper);
            editorOverlay.appendChild(messageAndButtonsWrapper);

            updateBufferedStyleName();

            int frozenColumns = grid.getVisibleFrozenColumnCount();
            double frozenColumnsWidth = 0;
            double cellHeight = 0;

            for (int i = 0; i < tr.getCells().getLength(); i++) {
                Element cell = createCell(tr.getCells().getItem(i));
                cellHeight = Math.max(cellHeight, WidgetUtil
                        .getRequiredHeightBoundingClientRectDouble(tr
                                .getCells().getItem(i)));

                Column<?, T> column = grid.getVisibleColumn(i);

                if (i < frozenColumns) {
                    frozenCellWrapper.appendChild(cell);
                    frozenColumnsWidth += WidgetUtil
                            .getRequiredWidthBoundingClientRectDouble(tr
                                    .getCells().getItem(i));
                } else {
                    cellWrapper.appendChild(cell);
                }

                if (column.isEditable()) {
                    Widget editor = getHandler().getWidget(column);

                    if (editor != null) {
                        columnToWidget.put(column, editor);
                        grid.attachWidget(editor, cell);
                    }

                    if (i == focusedColumnIndex) {
                        if (BrowserInfo.get().isIE8()) {
                            Scheduler.get().scheduleDeferred(fieldFocusCommand);
                        } else {
                            focusColumn(focusedColumnIndex);
                        }
                    }
                } else {
                    cell.addClassName(NOT_EDITABLE_CLASS_NAME);
                    cell.addClassName(tr.getCells().getItem(i).getClassName());
                    // If the focused or frozen stylename is present it should
                    // not be inherited by the editor cell as it is not useful
                    // in the editor and would look broken without additional
                    // style rules. This is a bit of a hack.
                    cell.removeClassName(grid.cellFocusStyleName);
                    cell.removeClassName("frozen");

                    if (column == grid.selectionColumn) {
                        // Duplicate selection column CheckBox

                        pinnedRowHandle = grid.getDataSource().getHandle(
                                grid.getDataSource().getRow(rowIndex));
                        pinnedRowHandle.pin();

                        // We need to duplicate the selection CheckBox for the
                        // editor overlay since the original one is hidden by
                        // the overlay
                        final CheckBox checkBox = GWT.create(CheckBox.class);
                        checkBox.setValue(grid.isSelected(pinnedRowHandle
                                .getRow()));
                        checkBox.sinkEvents(Event.ONCLICK);

                        checkBox.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                T row = pinnedRowHandle.getRow();
                                if (grid.isSelected(row)) {
                                    grid.deselect(row);
                                } else {
                                    grid.select(row);
                                }
                            }
                        });
                        grid.attachWidget(checkBox, cell);
                        columnToWidget.put(column, checkBox);

                        // Only enable CheckBox in non-buffered mode
                        checkBox.setEnabled(!isBuffered());

                    } else if (!(column.getRenderer() instanceof WidgetRenderer)) {
                        // Copy non-widget content directly
                        cell.setInnerHTML(tr.getCells().getItem(i)
                                .getInnerHTML());
                    }
                }
            }

            setBounds(frozenCellWrapper, 0, 0, frozenColumnsWidth, 0);
            setBounds(cellWrapper, frozenColumnsWidth, 0, tr.getOffsetWidth()
                    - frozenColumnsWidth, cellHeight);

            // Only add these elements once
            if (!messageAndButtonsWrapper.isOrHasChild(messageWrapper)) {
                messageAndButtonsWrapper.appendChild(messageWrapper);
                messageAndButtonsWrapper.appendChild(buttonsWrapper);
            }

            if (isBuffered()) {
                grid.attachWidget(saveButton, buttonsWrapper);
                grid.attachWidget(cancelButton, buttonsWrapper);
            }

            setMessageAndButtonsWrapperVisible(isBuffered());

            updateHorizontalScrollPosition();

            AbstractRowContainer body = (AbstractRowContainer) grid
                    .getEscalator().getBody();
            double rowTop = body.getRowTop(tr);

            int bodyTop = body.getElement().getAbsoluteTop();
            int gridTop = gridElement.getAbsoluteTop();
            double overlayTop = rowTop + bodyTop - gridTop;

            originalScrollTop = grid.getScrollTop();
            if (!isBuffered() || buttonsShouldBeRenderedBelow(tr)) {
                // Default case, editor buttons are below the edited row
                editorOverlay.getStyle().setTop(overlayTop, Unit.PX);
                originalTop = overlayTop;
                editorOverlay.getStyle().clearBottom();
            } else {
                // Move message and buttons wrapper on top of cell wrapper if
                // there is not enough space visible space under and fix the
                // overlay from the bottom
                editorOverlay.insertFirst(messageAndButtonsWrapper);
                int gridHeight = grid.getElement().getOffsetHeight();
                editorOverlay.getStyle()
                        .setBottom(
                                gridHeight - overlayTop - tr.getOffsetHeight(),
                                Unit.PX);
                editorOverlay.getStyle().clearTop();
            }

            // Do not render over the vertical scrollbar
            editorOverlay.getStyle().setWidth(grid.escalator.getInnerWidth(),
                    Unit.PX);
        }

        private void focusColumn(int colIndex) {
            if (colIndex < 0 || colIndex >= grid.getVisibleColumns().size()) {
                // NO-OP
                return;
            }

            Widget editor = getWidget(grid.getVisibleColumn(colIndex));
            if (editor instanceof Focusable) {
                ((Focusable) editor).focus();
            } else if (editor instanceof com.google.gwt.user.client.ui.Focusable) {
                ((com.google.gwt.user.client.ui.Focusable) editor)
                        .setFocus(true);
            } else {
                grid.focus();
            }
        }

        private boolean buttonsShouldBeRenderedBelow(TableRowElement tr) {
            TableSectionElement tfoot = grid.escalator.getFooter().getElement();
            double tfootPageTop = WidgetUtil.getBoundingClientRect(tfoot)
                    .getTop();
            double trPageBottom = WidgetUtil.getBoundingClientRect(tr)
                    .getBottom();
            int messageAndButtonsHeight = messageAndButtonsWrapper
                    .getOffsetHeight();
            double bottomOfButtons = trPageBottom + messageAndButtonsHeight;

            return bottomOfButtons < tfootPageTop;
        }

        protected void hideOverlay() {
            if (editorOverlay.getParentElement() == null) {
                return;
            }

            if (pinnedRowHandle != null) {
                pinnedRowHandle.unpin();
                pinnedRowHandle = null;
            }

            for (HandlerRegistration r : focusHandlers) {
                r.removeHandler();
            }
            focusHandlers.clear();

            for (Widget w : columnToWidget.values()) {
                setParent(w, null);
            }
            columnToWidget.clear();

            if (isBuffered()) {
                grid.detachWidget(saveButton);
                grid.detachWidget(cancelButton);
            }

            editorOverlay.removeAllChildren();
            cellWrapper.removeAllChildren();
            frozenCellWrapper.removeAllChildren();
            editorOverlay.removeFromParent();

            hScrollHandler.removeHandler();

            clearEditorColumnErrors();
        }

        private void updateBufferedStyleName() {
            if (isBuffered()) {
                editorOverlay.removeClassName("unbuffered");
                editorOverlay.addClassName("buffered");
            } else {
                editorOverlay.removeClassName("buffered");
                editorOverlay.addClassName("unbuffered");
            }
        }

        protected void setStylePrimaryName(String primaryName) {
            if (styleName != null) {
                editorOverlay.removeClassName(styleName);

                cellWrapper.removeClassName(styleName + "-cells");
                frozenCellWrapper.removeClassName(styleName + "-cells");
                messageAndButtonsWrapper.removeClassName(styleName + "-footer");

                messageWrapper.removeClassName(styleName + "-message");
                buttonsWrapper.removeClassName(styleName + "-buttons");

                saveButton.removeStyleName(styleName + "-save");
                cancelButton.removeStyleName(styleName + "-cancel");
            }
            styleName = primaryName + "-editor";
            editorOverlay.setClassName(styleName);

            cellWrapper.setClassName(styleName + "-cells");
            frozenCellWrapper.setClassName(styleName + "-cells frozen");
            messageAndButtonsWrapper.setClassName(styleName + "-footer");

            messageWrapper.setClassName(styleName + "-message");
            buttonsWrapper.setClassName(styleName + "-buttons");

            saveButton.setStyleName(styleName + "-save");
            cancelButton.setStyleName(styleName + "-cancel");
        }

        /**
         * Creates an editor cell corresponding to the given table cell. The
         * returned element is empty and has the same dimensions and position as
         * the table cell.
         * 
         * @param td
         *            the table cell used as a reference
         * @return an editor cell corresponding to the given cell
         */
        protected Element createCell(TableCellElement td) {
            DivElement cell = DivElement.as(DOM.createDiv());
            double width = WidgetUtil
                    .getRequiredWidthBoundingClientRectDouble(td);
            double height = WidgetUtil
                    .getRequiredHeightBoundingClientRectDouble(td);
            setBounds(cell, td.getOffsetLeft(), td.getOffsetTop(), width,
                    height);
            return cell;
        }

        private static void setBounds(Element e, double left, double top,
                double width, double height) {
            Style style = e.getStyle();
            style.setLeft(left, Unit.PX);
            style.setTop(top, Unit.PX);
            style.setWidth(width, Unit.PX);
            style.setHeight(height, Unit.PX);
        }

        private void updateHorizontalScrollPosition() {
            double scrollLeft = grid.getScrollLeft();
            cellWrapper.getStyle().setLeft(
                    frozenCellWrapper.getOffsetWidth() - scrollLeft, Unit.PX);
        }

        /**
         * Moves the editor overlay on scroll so that it stays on top of the
         * edited row. This will also snap the editor to top or bottom of the
         * row container if the edited row is scrolled out of the visible area.
         */
        private void updateVerticalScrollPosition() {
            if (isBuffered()) {
                return;
            }

            double newScrollTop = grid.getScrollTop();

            int gridTop = grid.getElement().getAbsoluteTop();
            int editorHeight = editorOverlay.getOffsetHeight();

            Escalator escalator = grid.getEscalator();
            TableSectionElement header = escalator.getHeader().getElement();
            int footerTop = escalator.getFooter().getElement().getAbsoluteTop();
            int headerBottom = header.getAbsoluteBottom();

            double newTop = originalTop - (newScrollTop - originalScrollTop);

            if (newTop + gridTop < headerBottom) {
                // Snap editor to top of the row container
                newTop = header.getOffsetHeight();
            } else if (newTop + gridTop > footerTop - editorHeight) {
                // Snap editor to the bottom of the row container
                newTop = footerTop - editorHeight - gridTop;
            }

            editorOverlay.getStyle().setTop(newTop, Unit.PX);
        }

        protected void setGridEnabled(boolean enabled) {
            // TODO: This should be informed to handler as well so possible
            // fields can be disabled.
            setButtonsEnabled(enabled);
        }

        private void setButtonsEnabled(boolean enabled) {
            saveButton.setEnabled(enabled);
            cancelButton.setEnabled(enabled);
        }

        public void setSaveCaption(String saveCaption)
                throws IllegalArgumentException {
            if (saveCaption == null) {
                throw new IllegalArgumentException(
                        "Save caption cannot be null");
            }
            saveButton.setText(saveCaption);
        }

        public String getSaveCaption() {
            return saveButton.getText();
        }

        public void setCancelCaption(String cancelCaption)
                throws IllegalArgumentException {
            if (cancelCaption == null) {
                throw new IllegalArgumentException(
                        "Cancel caption cannot be null");
            }
            cancelButton.setText(cancelCaption);
        }

        public String getCancelCaption() {
            return cancelButton.getText();
        }

        public void setEditorColumnError(Column<?, T> column, boolean hasError) {
            if (state != State.ACTIVE && state != State.SAVING) {
                throw new IllegalStateException("Cannot set cell error "
                        + "status: editor is neither active nor saving.");
            }

            if (isEditorColumnError(column) == hasError) {
                return;
            }

            Element editorCell = getWidget(column).getElement()
                    .getParentElement();
            if (hasError) {
                editorCell.addClassName(ERROR_CLASS_NAME);
                columnErrors.add(column);
            } else {
                editorCell.removeClassName(ERROR_CLASS_NAME);
                columnErrors.remove(column);
            }
        }

        public void clearEditorColumnErrors() {

            /*
             * editorOverlay has no children if it's not active, effectively
             * making this loop a NOOP.
             */
            Element e = editorOverlay.getFirstChildElement();
            while (e != null) {
                e.removeClassName(ERROR_CLASS_NAME);
                e = e.getNextSiblingElement();
            }

            columnErrors.clear();
        }

        public boolean isEditorColumnError(Column<?, T> column) {
            return columnErrors.contains(column);
        }

        public void setBuffered(boolean buffered) {
            this.buffered = buffered;
            setMessageAndButtonsWrapperVisible(buffered);
        }

        public boolean isBuffered() {
            return buffered;
        }

        private void setMessageAndButtonsWrapperVisible(boolean visible) {
            if (visible) {
                messageAndButtonsWrapper.getStyle().clearDisplay();
            } else {
                messageAndButtonsWrapper.getStyle().setDisplay(Display.NONE);
            }
        }

        /**
         * Sets the event handler for this Editor.
         * 
         * @since 7.6
         * @param handler
         *            the new event handler
         */
        public void setEventHandler(EventHandler<T> handler) {
            eventHandler = handler;
        }

        /**
         * Returns the event handler of this Editor.
         * 
         * @since 7.6
         * @return the current event handler
         */
        public EventHandler<T> getEventHandler() {
            return eventHandler;
        }

        @Override
        public boolean isWorkPending() {
            return saveTimeout.isRunning() || bindTimeout.isRunning();
        }

        protected int getElementColumn(Element e) {
            int frozenCells = frozenCellWrapper.getChildCount();
            if (frozenCellWrapper.isOrHasChild(e)) {
                for (int i = 0; i < frozenCells; ++i) {
                    if (frozenCellWrapper.getChild(i).isOrHasChild(e)) {
                        return i;
                    }
                }
            }

            if (cellWrapper.isOrHasChild(e)) {
                for (int i = 0; i < cellWrapper.getChildCount(); ++i) {
                    if (cellWrapper.getChild(i).isOrHasChild(e)) {
                        return i + frozenCells;
                    }
                }
            }

            return -1;
        }
    }

    public static abstract class AbstractGridKeyEvent<HANDLER extends AbstractGridKeyEventHandler>
            extends KeyEvent<HANDLER> {

        private Grid<?> grid;
        private final Type<HANDLER> associatedType = new Type<HANDLER>(
                getBrowserEventType(), this);
        private final CellReference<?> targetCell;

        public AbstractGridKeyEvent(Grid<?> grid, CellReference<?> targetCell) {
            this.grid = grid;
            this.targetCell = targetCell;
        }

        protected abstract String getBrowserEventType();

        /**
         * Gets the Grid instance for this event.
         * 
         * @return grid
         */
        public Grid<?> getGrid() {
            return grid;
        }

        /**
         * Gets the focused cell for this event.
         * 
         * @return focused cell
         */
        public CellReference<?> getFocusedCell() {
            return targetCell;
        }

        @Override
        protected void dispatch(HANDLER handler) {
            EventTarget target = getNativeEvent().getEventTarget();
            if (Element.is(target)
                    && !grid.isElementInChildWidget(Element.as(target))) {

                Section section = Section.FOOTER;
                final RowContainer container = grid.cellFocusHandler.containerWithFocus;
                if (container == grid.escalator.getHeader()) {
                    section = Section.HEADER;
                } else if (container == grid.escalator.getBody()) {
                    section = Section.BODY;
                }

                doDispatch(handler, section);
            }
        }

        protected abstract void doDispatch(HANDLER handler, Section section);

        @Override
        public Type<HANDLER> getAssociatedType() {
            return associatedType;
        }
    }

    public static abstract class AbstractGridMouseEvent<HANDLER extends AbstractGridMouseEventHandler>
            extends MouseEvent<HANDLER> {

        private Grid<?> grid;
        private final CellReference<?> targetCell;
        private final Type<HANDLER> associatedType = new Type<HANDLER>(
                getBrowserEventType(), this);

        public AbstractGridMouseEvent(Grid<?> grid, CellReference<?> targetCell) {
            this.grid = grid;
            this.targetCell = targetCell;
        }

        protected abstract String getBrowserEventType();

        /**
         * Gets the Grid instance for this event.
         * 
         * @return grid
         */
        public Grid<?> getGrid() {
            return grid;
        }

        /**
         * Gets the reference of target cell for this event.
         * 
         * @return target cell
         */
        public CellReference<?> getTargetCell() {
            return targetCell;
        }

        @Override
        protected void dispatch(HANDLER handler) {
            EventTarget target = getNativeEvent().getEventTarget();
            if (!Element.is(target)) {
                // Target is not an element
                return;
            }

            Element targetElement = Element.as(target);
            if (grid.isElementInChildWidget(targetElement)) {
                // Target is some widget inside of Grid
                return;
            }

            final RowContainer container = grid.escalator
                    .findRowContainer(targetElement);
            if (container == null) {
                // No container for given element
                return;
            }

            Section section = Section.FOOTER;
            if (container == grid.escalator.getHeader()) {
                section = Section.HEADER;
            } else if (container == grid.escalator.getBody()) {
                section = Section.BODY;
            }

            doDispatch(handler, section);
        }

        protected abstract void doDispatch(HANDLER handler, Section section);

        @Override
        public Type<HANDLER> getAssociatedType() {
            return associatedType;
        }
    }

    private static final String CUSTOM_STYLE_PROPERTY_NAME = "customStyle";

    /**
     * An initial height that is given to new details rows before rendering the
     * appropriate widget that we then can be measure
     * 
     * @see GridSpacerUpdater
     */
    private static final double DETAILS_ROW_INITIAL_HEIGHT = 50;

    private EventCellReference<T> eventCell = new EventCellReference<T>(this);
    private GridKeyDownEvent keyDown = new GridKeyDownEvent(this, eventCell);
    private GridKeyUpEvent keyUp = new GridKeyUpEvent(this, eventCell);
    private GridKeyPressEvent keyPress = new GridKeyPressEvent(this, eventCell);
    private GridClickEvent clickEvent = new GridClickEvent(this, eventCell);
    private GridDoubleClickEvent doubleClickEvent = new GridDoubleClickEvent(
            this, eventCell);

    private class CellFocusHandler {

        private RowContainer containerWithFocus = escalator.getBody();
        private int rowWithFocus = 0;
        private Range cellFocusRange = Range.withLength(0, 1);
        private int lastFocusedBodyRow = 0;
        private int lastFocusedHeaderRow = 0;
        private int lastFocusedFooterRow = 0;
        private TableCellElement cellWithFocusStyle = null;
        private TableRowElement rowWithFocusStyle = null;

        public CellFocusHandler() {
            sinkEvents(getNavigationEvents());
        }

        private Cell getFocusedCell() {
            return new Cell(rowWithFocus, cellFocusRange.getStart(),
                    cellWithFocusStyle);
        }

        /**
         * Sets style names for given cell when needed.
         */
        public void updateFocusedCellStyle(FlyweightCell cell,
                RowContainer cellContainer) {
            int cellRow = cell.getRow();
            int cellColumn = cell.getColumn();
            int colSpan = cell.getColSpan();
            boolean columnHasFocus = Range.withLength(cellColumn, colSpan)
                    .intersects(cellFocusRange);

            if (cellContainer == containerWithFocus) {
                // Cell is in the current container
                if (cellRow == rowWithFocus && columnHasFocus) {
                    if (cellWithFocusStyle != cell.getElement()) {
                        // Cell is correct but it does not have focused style
                        if (cellWithFocusStyle != null) {
                            // Remove old focus style
                            setStyleName(cellWithFocusStyle,
                                    cellFocusStyleName, false);
                        }
                        cellWithFocusStyle = cell.getElement();

                        // Add focus style to correct cell.
                        setStyleName(cellWithFocusStyle, cellFocusStyleName,
                                true);
                    }
                } else if (cellWithFocusStyle == cell.getElement()) {
                    // Due to escalator reusing cells, a new cell has the same
                    // element but is not the focused cell.
                    setStyleName(cellWithFocusStyle, cellFocusStyleName, false);
                    cellWithFocusStyle = null;
                }
            }
        }

        /**
         * Sets focus style for the given row if needed.
         * 
         * @param row
         *            a row object
         */
        public void updateFocusedRowStyle(Row row) {
            if (rowWithFocus == row.getRow()
                    && containerWithFocus == escalator.getBody()) {
                if (row.getElement() != rowWithFocusStyle) {
                    // Row should have focus style but does not have it.
                    if (rowWithFocusStyle != null) {
                        setStyleName(rowWithFocusStyle, rowFocusStyleName,
                                false);
                    }
                    rowWithFocusStyle = row.getElement();
                    setStyleName(rowWithFocusStyle, rowFocusStyleName, true);
                }
            } else if (rowWithFocusStyle == row.getElement()
                    || (containerWithFocus != escalator.getBody() && rowWithFocusStyle != null)) {
                // Remove focus style.
                setStyleName(rowWithFocusStyle, rowFocusStyleName, false);
                rowWithFocusStyle = null;
            }
        }

        /**
         * Sets the currently focused.
         * <p>
         * <em>NOTE:</em> the column index is the index in DOM, not the logical
         * column index which includes hidden columns.
         * 
         * @param rowIndex
         *            the index of the row having focus
         * @param columnIndexDOM
         *            the index of the cell having focus
         * @param container
         *            the row container having focus
         */
        private void setCellFocus(int rowIndex, int columnIndexDOM,
                RowContainer container) {
            if (rowIndex == rowWithFocus
                    && cellFocusRange.contains(columnIndexDOM)
                    && container == this.containerWithFocus) {
                refreshRow(rowWithFocus);
                return;
            }

            int oldRow = rowWithFocus;
            rowWithFocus = rowIndex;
            Range oldRange = cellFocusRange;

            if (container == escalator.getBody()) {
                scrollToRow(rowWithFocus);
                cellFocusRange = Range.withLength(columnIndexDOM, 1);
            } else {
                int i = 0;
                Element cell = container.getRowElement(rowWithFocus)
                        .getFirstChildElement();
                do {
                    int colSpan = cell
                            .getPropertyInt(FlyweightCell.COLSPAN_ATTR);
                    Range cellRange = Range.withLength(i, colSpan);
                    if (cellRange.contains(columnIndexDOM)) {
                        cellFocusRange = cellRange;
                        break;
                    }
                    cell = cell.getNextSiblingElement();
                    ++i;
                } while (cell != null);
            }
            int columnIndex = getColumns().indexOf(
                    getVisibleColumn(columnIndexDOM));
            if (columnIndex >= escalator.getColumnConfiguration()
                    .getFrozenColumnCount()) {
                escalator.scrollToColumn(columnIndexDOM, ScrollDestination.ANY,
                        10);
            }

            if (this.containerWithFocus == container) {
                if (oldRange.equals(cellFocusRange) && oldRow != rowWithFocus) {
                    refreshRow(oldRow);
                } else {
                    refreshHeader();
                    refreshFooter();
                }
            } else {
                RowContainer oldContainer = this.containerWithFocus;
                this.containerWithFocus = container;

                if (oldContainer == escalator.getBody()) {
                    lastFocusedBodyRow = oldRow;
                } else if (oldContainer == escalator.getHeader()) {
                    lastFocusedHeaderRow = oldRow;
                } else {
                    lastFocusedFooterRow = oldRow;
                }

                if (!oldRange.equals(cellFocusRange)) {
                    refreshHeader();
                    refreshFooter();
                    if (oldContainer == escalator.getBody()) {
                        oldContainer.refreshRows(oldRow, 1);
                    }
                } else {
                    oldContainer.refreshRows(oldRow, 1);
                }
            }
            refreshRow(rowWithFocus);
        }

        /**
         * Sets focus on a cell.
         * 
         * <p>
         * <em>Note</em>: cell focus is not the same as JavaScript's
         * {@code document.activeElement}.
         * 
         * @param cell
         *            a cell object
         */
        public void setCellFocus(CellReference<T> cell) {
            setCellFocus(cell.getRowIndex(), cell.getColumnIndexDOM(),
                    escalator.findRowContainer(cell.getElement()));
        }

        /**
         * Gets list of events that can be used for cell focusing.
         * 
         * @return list of navigation related event types
         */
        public Collection<String> getNavigationEvents() {
            return Arrays.asList(BrowserEvents.KEYDOWN, BrowserEvents.CLICK);
        }

        /**
         * Handle events that can move the cell focus.
         */
        public void handleNavigationEvent(Event event, CellReference<T> cell) {
            if (event.getType().equals(BrowserEvents.CLICK)) {
                setCellFocus(cell);
                // Grid should have focus when clicked.
                getElement().focus();
            } else if (event.getType().equals(BrowserEvents.KEYDOWN)) {
                int newRow = rowWithFocus;
                RowContainer newContainer = containerWithFocus;
                int newColumn = cellFocusRange.getStart();

                switch (event.getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    ++newRow;
                    break;
                case KeyCodes.KEY_UP:
                    --newRow;
                    break;
                case KeyCodes.KEY_RIGHT:
                    if (cellFocusRange.getEnd() >= getVisibleColumns().size()) {
                        return;
                    }
                    newColumn = cellFocusRange.getEnd();
                    break;
                case KeyCodes.KEY_LEFT:
                    if (newColumn == 0) {
                        return;
                    }
                    --newColumn;
                    break;
                case KeyCodes.KEY_TAB:
                    if (event.getShiftKey()) {
                        newContainer = getPreviousContainer(containerWithFocus);
                    } else {
                        newContainer = getNextContainer(containerWithFocus);
                    }

                    if (newContainer == containerWithFocus) {
                        return;
                    }
                    break;
                case KeyCodes.KEY_HOME:
                    if (newContainer.getRowCount() > 0) {
                        newRow = 0;
                    }
                    break;
                case KeyCodes.KEY_END:
                    if (newContainer.getRowCount() > 0) {
                        newRow = newContainer.getRowCount() - 1;
                    }
                    break;
                case KeyCodes.KEY_PAGEDOWN:
                case KeyCodes.KEY_PAGEUP:
                    if (newContainer.getRowCount() > 0) {
                        boolean down = event.getKeyCode() == KeyCodes.KEY_PAGEDOWN;
                        // If there is a visible focused cell, scroll by one
                        // page from its position. Otherwise, use the first or
                        // the last visible row as the scroll start position.
                        // This avoids jumping when using both keyboard and the
                        // scroll bar for scrolling.
                        int firstVisible = getFirstVisibleRowIndex();
                        int lastVisible = getLastVisibleRowIndex();
                        if (newRow < firstVisible || newRow > lastVisible) {
                            newRow = down ? lastVisible : firstVisible;
                        }
                        // Scroll by a little less than the visible area to
                        // account for the possibility that the top and the
                        // bottom row are only partially visible.
                        int moveFocusBy = Math.max(1, lastVisible
                                - firstVisible - 1);
                        moveFocusBy *= down ? 1 : -1;
                        newRow += moveFocusBy;
                        newRow = Math.max(0, Math.min(
                                newContainer.getRowCount() - 1, newRow));
                    }
                    break;
                default:
                    return;
                }

                if (newContainer != containerWithFocus) {
                    if (newContainer == escalator.getBody()) {
                        newRow = lastFocusedBodyRow;
                    } else if (newContainer == escalator.getHeader()) {
                        newRow = lastFocusedHeaderRow;
                    } else {
                        newRow = lastFocusedFooterRow;
                    }
                } else if (newRow < 0) {
                    newContainer = getPreviousContainer(newContainer);

                    if (newContainer == containerWithFocus) {
                        newRow = 0;
                    } else if (newContainer == escalator.getBody()) {
                        newRow = getLastVisibleRowIndex();
                    } else {
                        newRow = newContainer.getRowCount() - 1;
                    }
                } else if (newRow >= containerWithFocus.getRowCount()) {
                    newContainer = getNextContainer(newContainer);

                    if (newContainer == containerWithFocus) {
                        newRow = containerWithFocus.getRowCount() - 1;
                    } else if (newContainer == escalator.getBody()) {
                        newRow = getFirstVisibleRowIndex();
                    } else {
                        newRow = 0;
                    }
                }

                if (newContainer.getRowCount() == 0) {
                    /*
                     * There are no rows in the container. Can't change the
                     * focused cell.
                     */
                    return;
                }

                event.preventDefault();
                event.stopPropagation();

                setCellFocus(newRow, newColumn, newContainer);
            }

        }

        private RowContainer getPreviousContainer(RowContainer current) {
            if (current == escalator.getFooter()) {
                current = escalator.getBody();
            } else if (current == escalator.getBody()) {
                current = escalator.getHeader();
            } else {
                return current;
            }

            if (current.getRowCount() == 0) {
                return getPreviousContainer(current);
            }
            return current;
        }

        private RowContainer getNextContainer(RowContainer current) {
            if (current == escalator.getHeader()) {
                current = escalator.getBody();
            } else if (current == escalator.getBody()) {
                current = escalator.getFooter();
            } else {
                return current;
            }

            if (current.getRowCount() == 0) {
                return getNextContainer(current);
            }
            return current;
        }

        private void refreshRow(int row) {
            containerWithFocus.refreshRows(row, 1);
        }

        /**
         * Offsets the focused cell's range.
         * 
         * @param offset
         *            offset for fixing focused cell's range
         */
        public void offsetRangeBy(int offset) {
            cellFocusRange = cellFocusRange.offsetBy(offset);
        }

        /**
         * Informs {@link CellFocusHandler} that certain range of rows has been
         * added to the Grid body. {@link CellFocusHandler} will fix indices
         * accordingly.
         * 
         * @param added
         *            a range of added rows
         */
        public void rowsAddedToBody(Range added) {
            boolean bodyHasFocus = (containerWithFocus == escalator.getBody());
            boolean insertionIsAboveFocusedCell = (added.getStart() <= rowWithFocus);
            if (bodyHasFocus && insertionIsAboveFocusedCell) {
                rowWithFocus += added.length();
                rowWithFocus = Math.min(rowWithFocus, escalator.getBody()
                        .getRowCount() - 1);
                refreshRow(rowWithFocus);
            }
        }

        /**
         * Informs {@link CellFocusHandler} that certain range of rows has been
         * removed from the Grid body. {@link CellFocusHandler} will fix indices
         * accordingly.
         * 
         * @param removed
         *            a range of removed rows
         */
        public void rowsRemovedFromBody(Range removed) {
            if (containerWithFocus != escalator.getBody()) {
                return;
            } else if (!removed.contains(rowWithFocus)) {
                if (removed.getStart() > rowWithFocus) {
                    return;
                }
                rowWithFocus = rowWithFocus - removed.length();
            } else {
                if (containerWithFocus.getRowCount() > removed.getEnd()) {
                    rowWithFocus = removed.getStart();
                } else if (removed.getStart() > 0) {
                    rowWithFocus = removed.getStart() - 1;
                } else {
                    if (escalator.getHeader().getRowCount() > 0) {
                        rowWithFocus = Math.min(lastFocusedHeaderRow, escalator
                                .getHeader().getRowCount() - 1);
                        containerWithFocus = escalator.getHeader();
                    } else if (escalator.getFooter().getRowCount() > 0) {
                        rowWithFocus = Math.min(lastFocusedFooterRow, escalator
                                .getFooter().getRowCount() - 1);
                        containerWithFocus = escalator.getFooter();
                    }
                }
            }
            refreshRow(rowWithFocus);
        }
    }

    public final class SelectionColumn extends Column<Boolean, T> {

        private boolean initDone = false;
        private boolean selected = false;
        private CheckBox selectAllCheckBox;

        SelectionColumn(final Renderer<Boolean> selectColumnRenderer) {
            super(selectColumnRenderer);
        }

        void initDone() {
            setWidth(-1);

            setEditable(false);
            setResizable(false);

            initDone = true;
        }

        @Override
        protected void setDefaultHeaderContent(HeaderCell selectionCell) {
            /*
             * TODO: Currently the select all check box is shown when multi
             * selection is in use. This might result in malfunctions if no
             * SelectAllHandlers are present.
             * 
             * Later on this could be fixed so that it check such handlers
             * exist.
             */
            final SelectionModel.Multi<T> model = (Multi<T>) getSelectionModel();

            if (selectAllCheckBox == null) {
                selectAllCheckBox = GWT.create(CheckBox.class);
                selectAllCheckBox.setStylePrimaryName(getStylePrimaryName()
                        + SELECT_ALL_CHECKBOX_CLASSNAME);
                selectAllCheckBox
                        .addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                            @Override
                            public void onValueChange(
                                    ValueChangeEvent<Boolean> event) {
                                if (event.getValue()) {
                                    fireEvent(new SelectAllEvent<T>(model));
                                    selected = true;
                                } else {
                                    model.deselectAll();
                                    selected = false;
                                }
                            }
                        });
                selectAllCheckBox.setValue(selected);

                addHeaderClickHandler(new HeaderClickHandler() {
                    @Override
                    public void onClick(GridClickEvent event) {
                        CellReference<?> targetCell = event.getTargetCell();
                        int defaultRowIndex = getHeader().getRows().indexOf(
                                getDefaultHeaderRow());

                        if (targetCell.getColumnIndex() == 0
                                && targetCell.getRowIndex() == defaultRowIndex) {
                            selectAllCheckBox.setValue(
                                    !selectAllCheckBox.getValue(), true);
                        }
                    }
                });

                // Select all with space when "select all" cell is active
                addHeaderKeyUpHandler(new HeaderKeyUpHandler() {
                    @Override
                    public void onKeyUp(GridKeyUpEvent event) {
                        if (event.getNativeKeyCode() != KeyCodes.KEY_SPACE) {
                            return;
                        }
                        HeaderRow targetHeaderRow = getHeader().getRow(
                                event.getFocusedCell().getRowIndex());
                        if (!targetHeaderRow.isDefault()) {
                            return;
                        }
                        if (event.getFocusedCell().getColumn() == SelectionColumn.this) {
                            // Send events to ensure state is updated
                            selectAllCheckBox.setValue(
                                    !selectAllCheckBox.getValue(), true);
                        }
                    }
                });
            } else {
                for (HeaderRow row : header.getRows()) {
                    if (row.getCell(this).getType() == GridStaticCellType.WIDGET) {
                        // Detach from old header.
                        row.getCell(this).setText("");
                    }
                }
            }

            selectionCell.setWidget(selectAllCheckBox);
        }

        @Override
        public Column<Boolean, T> setWidth(double pixels) {
            if (pixels != getWidth() && initDone) {
                throw new UnsupportedOperationException("The selection "
                        + "column cannot be modified after init");
            } else {
                super.setWidth(pixels);
            }

            return this;
        }

        @Override
        public Boolean getValue(T row) {
            return Boolean.valueOf(isSelected(row));
        }

        @Override
        public Column<Boolean, T> setExpandRatio(int ratio) {
            throw new UnsupportedOperationException(
                    "can't change the expand ratio of the selection column");
        }

        @Override
        public int getExpandRatio() {
            return 0;
        }

        @Override
        public Column<Boolean, T> setMaximumWidth(double pixels) {
            throw new UnsupportedOperationException(
                    "can't change the maximum width of the selection column");
        }

        @Override
        public double getMaximumWidth() {
            return -1;
        }

        @Override
        public Column<Boolean, T> setMinimumWidth(double pixels) {
            throw new UnsupportedOperationException(
                    "can't change the minimum width of the selection column");
        }

        @Override
        public double getMinimumWidth() {
            return -1;
        }

        @Override
        public Column<Boolean, T> setEditable(boolean editable) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "can't set the selection column editable");
            }
            super.setEditable(editable);
            return this;
        }
    }

    /**
     * Helper class for performing sorting through the user interface. Controls
     * the sort() method, reporting USER as the event originator. This is a
     * completely internal class, and is, as such, safe to re-name should a more
     * descriptive name come to mind.
     */
    private final class UserSorter {

        private final Timer timer;
        private boolean scheduledMultisort;
        private Column<?, T> column;

        private UserSorter() {
            timer = new Timer() {

                @Override
                public void run() {
                    UserSorter.this.sort(column, scheduledMultisort);
                }
            };
        }

        /**
         * Toggle sorting for a cell. If the multisort parameter is set to true,
         * the cell's sort order is modified as a natural part of a multi-sort
         * chain. If false, the sorting order is set to ASCENDING for that
         * cell's column. If that column was already the only sorted column in
         * the Grid, the sort direction is flipped.
         * 
         * @param cell
         *            a valid cell reference
         * @param multisort
         *            whether the sort command should act as a multi-sort stack
         *            or not
         */
        public void sort(Column<?, ?> column, boolean multisort) {

            if (!columns.contains(column)) {
                throw new IllegalArgumentException(
                        "Given column is not a column in this grid. "
                                + column.toString());
            }

            if (!column.isSortable()) {
                return;
            }

            final SortOrder so = getSortOrder(column);

            if (multisort) {

                // If the sort order exists, replace existing value with its
                // opposite
                if (so != null) {
                    final int idx = sortOrder.indexOf(so);
                    sortOrder.set(idx, so.getOpposite());
                } else {
                    // If it doesn't, just add a new sort order to the end of
                    // the list
                    sortOrder.add(new SortOrder(column));
                }

            } else {

                // Since we're doing single column sorting, first clear the
                // list. Then, if the sort order existed, add its opposite,
                // otherwise just add a new sort value

                int items = sortOrder.size();
                sortOrder.clear();
                if (so != null && items == 1) {
                    sortOrder.add(so.getOpposite());
                } else {
                    sortOrder.add(new SortOrder(column));
                }
            }

            // sortOrder has been changed; tell the Grid to re-sort itself by
            // user request.
            Grid.this.sort(true);
        }

        /**
         * Perform a sort after a delay.
         * 
         * @param delay
         *            delay, in milliseconds
         */
        public void sortAfterDelay(int delay, boolean multisort) {
            column = eventCell.getColumn();
            scheduledMultisort = multisort;
            timer.schedule(delay);
        }

        /**
         * Check if a delayed sort command has been issued but not yet carried
         * out.
         * 
         * @return a boolean value
         */
        public boolean isDelayedSortScheduled() {
            return timer.isRunning();
        }

        /**
         * Cancel a scheduled sort.
         */
        public void cancelDelayedSort() {
            timer.cancel();
        }

    }

    /**
     * @see Grid#autoColumnWidthsRecalculator
     */
    private class AutoColumnWidthsRecalculator {
        private double lastCalculatedInnerWidth = -1;

        private final ScheduledCommand calculateCommand = new ScheduledCommand() {

            @Override
            public void execute() {
                if (!isScheduled) {
                    // something cancelled running this.
                    return;
                }

                if (header.markAsDirty || footer.markAsDirty) {
                    if (rescheduleCount < 10) {
                        /*
                         * Headers and footers are rendered as finally, this way
                         * we re-schedule this loop as finally, at the end of
                         * the queue, so that the headers have a chance to
                         * render themselves.
                         */
                        Scheduler.get().scheduleFinally(this);
                        rescheduleCount++;
                    } else {
                        /*
                         * We've tried too many times reschedule finally. Seems
                         * like something is being deferred. Let the queue
                         * execute and retry again.
                         */
                        rescheduleCount = 0;
                        Scheduler.get().scheduleDeferred(this);
                    }
                } else if (dataIsBeingFetched) {
                    Scheduler.get().scheduleDeferred(this);
                } else {
                    calculate();
                }
            }
        };

        private int rescheduleCount = 0;
        private boolean isScheduled;

        /**
         * Calculates and applies column widths, taking into account fixed
         * widths and column expand rules
         * 
         * @param immediately
         *            <code>true</code> if the widths should be executed
         *            immediately (ignoring lazy loading completely), or
         *            <code>false</code> if the command should be run after a
         *            while (duplicate non-immediately invocations are ignored).
         * @see Column#setWidth(double)
         * @see Column#setExpandRatio(int)
         * @see Column#setMinimumWidth(double)
         * @see Column#setMaximumWidth(double)
         */
        public void schedule() {
            if (!isScheduled && isAttached()) {
                isScheduled = true;
                Scheduler.get().scheduleFinally(calculateCommand);
            }
        }

        private void calculate() {
            isScheduled = false;
            rescheduleCount = 0;

            assert !dataIsBeingFetched : "Trying to calculate column widths even though data is still being fetched.";

            if (columnsAreGuaranteedToBeWiderThanGrid()) {
                applyColumnWidths();
            } else {
                applyColumnWidthsWithExpansion();
            }

            // Update latest width to prevent recalculate on height change.
            lastCalculatedInnerWidth = escalator.getInnerWidth();
        }

        private boolean columnsAreGuaranteedToBeWiderThanGrid() {
            double freeSpace = escalator.getInnerWidth();
            for (Column<?, ?> column : getVisibleColumns()) {
                if (column.getWidth() >= 0) {
                    freeSpace -= column.getWidth();
                } else if (column.getMinimumWidth() >= 0) {
                    freeSpace -= column.getMinimumWidth();
                }
            }
            return freeSpace < 0;
        }

        @SuppressWarnings("boxing")
        private void applyColumnWidths() {

            /* Step 1: Apply all column widths as they are. */

            Map<Integer, Double> selfWidths = new LinkedHashMap<Integer, Double>();
            List<Column<?, T>> columns = getVisibleColumns();
            for (int index = 0; index < columns.size(); index++) {
                selfWidths.put(index, columns.get(index).getWidth());
            }
            Grid.this.escalator.getColumnConfiguration().setColumnWidths(
                    selfWidths);

            /*
             * Step 2: Make sure that each column ends up obeying their min/max
             * width constraints if defined as autowidth. If constraints are
             * violated, fix it.
             */

            Map<Integer, Double> constrainedWidths = new LinkedHashMap<Integer, Double>();
            for (int index = 0; index < columns.size(); index++) {
                Column<?, T> column = columns.get(index);

                boolean hasAutoWidth = column.getWidth() < 0;
                if (!hasAutoWidth) {
                    continue;
                }

                // TODO: bug: these don't honor the CSS max/min. :(
                double actualWidth = column.getWidthActual();
                if (actualWidth < getMinWidth(column)) {
                    constrainedWidths.put(index, column.getMinimumWidth());
                } else if (actualWidth > getMaxWidth(column)) {
                    constrainedWidths.put(index, column.getMaximumWidth());
                }
            }
            Grid.this.escalator.getColumnConfiguration().setColumnWidths(
                    constrainedWidths);
        }

        private void applyColumnWidthsWithExpansion() {
            boolean defaultExpandRatios = true;
            int totalRatios = 0;
            double reservedPixels = 0;
            final Set<Column<?, T>> columnsToExpand = new HashSet<Column<?, T>>();
            List<Column<?, T>> nonFixedColumns = new ArrayList<Column<?, T>>();
            Map<Integer, Double> columnSizes = new HashMap<Integer, Double>();
            final List<Column<?, T>> visibleColumns = getVisibleColumns();

            /*
             * Set all fixed widths and also calculate the size-to-fit widths
             * for the autocalculated columns.
             * 
             * This way we know with how many pixels we have left to expand the
             * rest.
             */
            for (Column<?, T> column : visibleColumns) {
                final double widthAsIs = column.getWidth();
                final boolean isFixedWidth = widthAsIs >= 0;
                // Check for max width just to be sure we don't break the limits
                final double widthFixed = Math.max(
                        Math.min(getMaxWidth(column), widthAsIs),
                        column.getMinimumWidth());
                defaultExpandRatios = defaultExpandRatios
                        && (column.getExpandRatio() == -1 || column == selectionColumn);

                if (isFixedWidth) {
                    columnSizes.put(visibleColumns.indexOf(column), widthFixed);
                    reservedPixels += widthFixed;
                } else {
                    nonFixedColumns.add(column);
                    columnSizes.put(visibleColumns.indexOf(column), -1.0d);
                }
            }

            setColumnSizes(columnSizes);

            for (Column<?, T> column : nonFixedColumns) {
                final int expandRatio = (defaultExpandRatios ? 1 : column
                        .getExpandRatio());
                final double maxWidth = getMaxWidth(column);
                final double newWidth = Math.min(maxWidth,
                        column.getWidthActual());
                boolean shouldExpand = newWidth < maxWidth && expandRatio > 0
                        && column != selectionColumn;
                if (shouldExpand) {
                    totalRatios += expandRatio;
                    columnsToExpand.add(column);
                }
                reservedPixels += newWidth;
                columnSizes.put(visibleColumns.indexOf(column), newWidth);
            }

            /*
             * Now that we know how many pixels we need at the very least, we
             * can distribute the remaining pixels to all columns according to
             * their expand ratios.
             */
            double pixelsToDistribute = escalator.getInnerWidth()
                    - reservedPixels;
            if (pixelsToDistribute <= 0 || totalRatios <= 0) {
                if (pixelsToDistribute <= 0) {
                    // Set column sizes for expanding columns
                    setColumnSizes(columnSizes);
                }

                return;
            }

            /*
             * Check for columns that hit their max width. Adjust
             * pixelsToDistribute and totalRatios accordingly. Recheck. Stop
             * when no new columns hit their max width
             */
            boolean aColumnHasMaxedOut;
            do {
                aColumnHasMaxedOut = false;
                final double widthPerRatio = pixelsToDistribute / totalRatios;
                final Iterator<Column<?, T>> i = columnsToExpand.iterator();
                while (i.hasNext()) {
                    final Column<?, T> column = i.next();
                    final int expandRatio = getExpandRatio(column,
                            defaultExpandRatios);
                    final int columnIndex = visibleColumns.indexOf(column);
                    final double autoWidth = columnSizes.get(columnIndex);
                    final double maxWidth = getMaxWidth(column);
                    double expandedWidth = autoWidth + widthPerRatio
                            * expandRatio;

                    if (maxWidth <= expandedWidth) {
                        i.remove();
                        totalRatios -= expandRatio;
                        aColumnHasMaxedOut = true;
                        pixelsToDistribute -= maxWidth - autoWidth;
                        columnSizes.put(columnIndex, maxWidth);
                    }
                }
            } while (aColumnHasMaxedOut);

            if (totalRatios <= 0 && columnsToExpand.isEmpty()) {
                setColumnSizes(columnSizes);
                return;
            }
            assert pixelsToDistribute > 0 : "We've run out of pixels to distribute ("
                    + pixelsToDistribute
                    + "px to "
                    + totalRatios
                    + " ratios between " + columnsToExpand.size() + " columns)";
            assert totalRatios > 0 && !columnsToExpand.isEmpty() : "Bookkeeping out of sync. Ratios: "
                    + totalRatios + " Columns: " + columnsToExpand.size();

            /*
             * If we still have anything left, distribute the remaining pixels
             * to the remaining columns.
             */
            final double widthPerRatio;
            int leftOver = 0;
            if (BrowserInfo.get().isIE8() || BrowserInfo.get().isIE9()
                    || BrowserInfo.getBrowserString().contains("PhantomJS")) {
                // These browsers report subpixels as integers. this usually
                // results into issues..
                widthPerRatio = (int) (pixelsToDistribute / totalRatios);
                leftOver = (int) (pixelsToDistribute - widthPerRatio
                        * totalRatios);
            } else {
                widthPerRatio = pixelsToDistribute / totalRatios;
            }
            for (Column<?, T> column : columnsToExpand) {
                final int expandRatio = getExpandRatio(column,
                        defaultExpandRatios);
                final int columnIndex = visibleColumns.indexOf(column);
                final double autoWidth = columnSizes.get(columnIndex);
                double totalWidth = autoWidth + widthPerRatio * expandRatio;
                if (leftOver > 0) {
                    totalWidth += 1;
                    leftOver--;
                }
                columnSizes.put(columnIndex, totalWidth);

                totalRatios -= expandRatio;
            }
            assert totalRatios == 0 : "Bookkeeping error: there were still some ratios left undistributed: "
                    + totalRatios;

            /*
             * Check the guarantees for minimum width and scoot back the columns
             * that don't care.
             */
            boolean minWidthsCausedReflows;
            do {
                minWidthsCausedReflows = false;

                /*
                 * First, let's check which columns were too cramped, and expand
                 * them. Also keep track on how many pixels we grew - we need to
                 * remove those pixels from other columns
                 */
                double pixelsToRemoveFromOtherColumns = 0;
                for (Column<?, T> column : visibleColumns) {
                    /*
                     * We can't iterate over columnsToExpand, even though that
                     * would be convenient. This is because some column without
                     * an expand ratio might still have a min width - those
                     * wouldn't show up in that set.
                     */

                    double minWidth = getMinWidth(column);
                    final int columnIndex = visibleColumns.indexOf(column);
                    double currentWidth = columnSizes.get(columnIndex);
                    boolean hasAutoWidth = column.getWidth() < 0;
                    if (hasAutoWidth && currentWidth < minWidth) {
                        columnSizes.put(columnIndex, minWidth);
                        pixelsToRemoveFromOtherColumns += (minWidth - currentWidth);
                        minWidthsCausedReflows = true;

                        /*
                         * Remove this column form the set if it exists. This
                         * way we make sure that it doesn't get shrunk in the
                         * next step.
                         */
                        columnsToExpand.remove(column);
                    }
                }

                /*
                 * Now we need to shrink the remaining columns according to
                 * their ratios. Recalculate the sum of remaining ratios.
                 */
                totalRatios = 0;
                for (Column<?, ?> column : columnsToExpand) {
                    totalRatios += getExpandRatio(column, defaultExpandRatios);
                }
                final double pixelsToRemovePerRatio = pixelsToRemoveFromOtherColumns
                        / totalRatios;
                for (Column<?, T> column : columnsToExpand) {
                    final double pixelsToRemove = pixelsToRemovePerRatio
                            * getExpandRatio(column, defaultExpandRatios);
                    int colIndex = visibleColumns.indexOf(column);
                    columnSizes.put(colIndex, columnSizes.get(colIndex)
                            - pixelsToRemove);
                }

            } while (minWidthsCausedReflows);

            // Finally set all the column sizes.
            setColumnSizes(columnSizes);
        }

        private void setColumnSizes(Map<Integer, Double> columnSizes) {
            // Set all widths at once
            escalator.getColumnConfiguration().setColumnWidths(columnSizes);
        }

        private int getExpandRatio(Column<?, ?> column,
                boolean defaultExpandRatios) {
            int expandRatio = column.getExpandRatio();
            if (expandRatio > 0) {
                return expandRatio;
            } else if (expandRatio < 0) {
                assert defaultExpandRatios : "No columns should've expanded";
                return 1;
            } else {
                assert false : "this method should've not been called at all if expandRatio is 0";
                return 0;
            }
        }

        /**
         * Returns the maximum width of the column, or {@link Double#MAX_VALUE}
         * if defined as negative.
         */
        private double getMaxWidth(Column<?, ?> column) {
            double maxWidth = column.getMaximumWidth();
            if (maxWidth >= 0) {
                return maxWidth;
            } else {
                return Double.MAX_VALUE;
            }
        }

        /**
         * Returns the minimum width of the column, or {@link Double#MIN_VALUE}
         * if defined as negative.
         */
        private double getMinWidth(Column<?, ?> column) {
            double minWidth = column.getMinimumWidth();
            if (minWidth >= 0) {
                return minWidth;
            } else {
                return Double.MIN_VALUE;
            }
        }

        /**
         * Check whether the auto width calculation is currently scheduled.
         * 
         * @return <code>true</code> if auto width calculation is currently
         *         scheduled
         */
        public boolean isScheduled() {
            return isScheduled;
        }
    }

    private class GridSpacerUpdater implements SpacerUpdater {

        private static final String STRIPE_CLASSNAME = "stripe";

        private final Map<Element, Widget> elementToWidgetMap = new HashMap<Element, Widget>();

        @Override
        public void init(Spacer spacer) {
            initTheming(spacer);

            int rowIndex = spacer.getRow();

            Widget detailsWidget = null;
            try {
                detailsWidget = detailsGenerator.getDetails(rowIndex);
            } catch (Throwable e) {
                getLogger().log(
                        Level.SEVERE,
                        "Exception while generating details for row "
                                + rowIndex, e);
            }

            final double spacerHeight;
            Element spacerElement = spacer.getElement();
            if (detailsWidget == null) {
                spacerElement.removeAllChildren();
                spacerHeight = DETAILS_ROW_INITIAL_HEIGHT;
            } else {
                Element element = detailsWidget.getElement();
                spacerElement.appendChild(element);
                setParent(detailsWidget, Grid.this);
                Widget previousWidget = elementToWidgetMap.put(element,
                        detailsWidget);

                assert previousWidget == null : "Overwrote a pre-existing widget on row "
                        + rowIndex + " without proper removal first.";

                /*
                 * Once we have the content properly inside the DOM, we should
                 * re-measure it to make sure that it's the correct height.
                 * 
                 * This is rather tricky, since the row (tr) will get the
                 * height, but the spacer cell (td) has the borders, which
                 * should go on top of the previous row and next row.
                 */
                double contentHeight;
                if (detailsGenerator instanceof HeightAwareDetailsGenerator) {
                    HeightAwareDetailsGenerator sadg = (HeightAwareDetailsGenerator) detailsGenerator;
                    contentHeight = sadg.getDetailsHeight(rowIndex);
                } else {
                    contentHeight = WidgetUtil
                            .getRequiredHeightBoundingClientRectDouble(element);
                }
                double borderTopAndBottomHeight = WidgetUtil
                        .getBorderTopAndBottomThickness(spacerElement);
                double measuredHeight = contentHeight
                        + borderTopAndBottomHeight;
                assert getElement().isOrHasChild(spacerElement) : "The spacer element wasn't in the DOM during measurement, but was assumed to be.";
                spacerHeight = measuredHeight;
            }

            escalator.getBody().setSpacer(rowIndex, spacerHeight);
        }

        @Override
        public void destroy(Spacer spacer) {
            Element spacerElement = spacer.getElement();

            assert getElement().isOrHasChild(spacerElement) : "Trying "
                    + "to destroy a spacer that is not connected to this "
                    + "Grid's DOM. (row: " + spacer.getRow() + ", element: "
                    + spacerElement + ")";

            Widget detailsWidget = elementToWidgetMap.remove(spacerElement
                    .getFirstChildElement());

            if (detailsWidget != null) {
                /*
                 * The widget may be null here if the previous generator
                 * returned a null widget.
                 */

                assert spacerElement.getFirstChild() != null : "The "
                        + "details row to destroy did not contain a widget - "
                        + "probably removed by something else without "
                        + "permission? (row: " + spacer.getRow()
                        + ", element: " + spacerElement + ")";

                setParent(detailsWidget, null);
                spacerElement.removeAllChildren();
            }
        }

        private void initTheming(Spacer spacer) {
            Element spacerRoot = spacer.getElement();

            if (spacer.getRow() % 2 == 1) {
                spacerRoot.getParentElement().addClassName(STRIPE_CLASSNAME);
            } else {
                spacerRoot.getParentElement().removeClassName(STRIPE_CLASSNAME);
            }
        }

    }

    /**
     * Sidebar displaying toggles for hidable columns and custom widgets
     * provided by the application.
     * <p>
     * The button for opening the sidebar is automatically visible inside the
     * grid, if it contains any column hiding options or custom widgets. The
     * column hiding toggles and custom widgets become visible once the sidebar
     * has been opened.
     * 
     * @since 7.5.0
     */
    private static class Sidebar extends Composite implements HasEnabled {

        private final ClickHandler openCloseButtonHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (!isOpen()) {
                    open();
                } else {
                    close();
                }
            }
        };

        private final FlowPanel rootContainer;

        private final FlowPanel content;

        private final MenuBar menuBar;

        private final Button openCloseButton;

        private final Grid<?> grid;

        private Overlay overlay;

        private Sidebar(Grid<?> grid) {
            this.grid = grid;

            rootContainer = new FlowPanel();
            initWidget(rootContainer);

            openCloseButton = new Button();

            openCloseButton.addClickHandler(openCloseButtonHandler);

            rootContainer.add(openCloseButton);

            content = new FlowPanel() {
                @Override
                public boolean remove(Widget w) {
                    // Check here to catch child.removeFromParent() calls
                    boolean removed = super.remove(w);
                    if (removed) {
                        updateVisibility();
                    }

                    return removed;
                }
            };

            createOverlay();

            menuBar = new MenuBar(true) {

                @Override
                public MenuItem insertItem(MenuItem item, int beforeIndex)
                        throws IndexOutOfBoundsException {
                    if (getParent() == null) {
                        content.insert(this, 0);
                        updateVisibility();
                    }
                    return super.insertItem(item, beforeIndex);
                }

                @Override
                public void removeItem(MenuItem item) {
                    super.removeItem(item);
                    if (getItems().isEmpty()) {
                        menuBar.removeFromParent();
                    }
                }

                @Override
                public void onBrowserEvent(Event event) {
                    // selecting a item with enter will lose the focus and
                    // selected item, which means that further keyboard
                    // selection won't work unless we do this:
                    if (event.getTypeInt() == Event.ONKEYDOWN
                            && event.getKeyCode() == KeyCodes.KEY_ENTER) {
                        final MenuItem item = getSelectedItem();
                        super.onBrowserEvent(event);
                        Scheduler.get().scheduleDeferred(
                                new ScheduledCommand() {

                                    @Override
                                    public void execute() {
                                        selectItem(item);
                                        focus();
                                    }
                                });

                    } else {
                        super.onBrowserEvent(event);
                    }
                }

            };
            KeyDownHandler keyDownHandler = new KeyDownHandler() {

                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                        close();
                    }
                }
            };
            openCloseButton.addDomHandler(keyDownHandler,
                    KeyDownEvent.getType());
            menuBar.addDomHandler(keyDownHandler, KeyDownEvent.getType());
        }

        /**
         * Creates and initializes the overlay.
         */
        private void createOverlay() {
            overlay = GWT.create(Overlay.class);
            overlay.setOwner(grid);
            overlay.setAutoHideEnabled(true);
            overlay.addStyleDependentName("popup");
            overlay.add(content);
            overlay.addAutoHidePartner(rootContainer.getElement());
            overlay.addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(CloseEvent<PopupPanel> event) {
                    removeStyleName("open");
                    addStyleName("closed");
                }
            });
        }

        /**
         * Opens the sidebar if not yet opened. Opening the sidebar has no
         * effect if it is empty.
         */
        public void open() {
            if (!isOpen() && isInDOM()) {
                addStyleName("open");
                removeStyleName("closed");
                overlay.showRelativeTo(rootContainer);
            }
        }

        /**
         * Closes the sidebar if not yet closed.
         */
        public void close() {
            overlay.hide();
        }

        /**
         * Returns whether the sidebar is open or not.
         * 
         * @return <code>true</code> if open, <code>false</code> if not
         */
        public boolean isOpen() {
            return overlay != null && overlay.isShowing();
        }

        @Override
        public void setStylePrimaryName(String styleName) {
            super.setStylePrimaryName(styleName);
            overlay.setStylePrimaryName(styleName);
            content.setStylePrimaryName(styleName + "-content");
            openCloseButton.setStylePrimaryName(styleName + "-button");
            if (isOpen()) {
                addStyleName("open");
                removeStyleName("closed");
            } else {
                removeStyleName("open");
                addStyleName("closed");
            }
        }

        @Override
        public void addStyleName(String style) {
            super.addStyleName(style);
            overlay.addStyleName(style);
        }

        @Override
        public void removeStyleName(String style) {
            super.removeStyleName(style);
            overlay.removeStyleName(style);
        }

        private void setHeightToHeaderCellHeight() {
            RowContainer header = grid.escalator.getHeader();
            if (header.getRowCount() == 0
                    || !header.getRowElement(0).hasChildNodes()) {
                getLogger()
                        .info("No header cell available when calculating sidebar button height");
                openCloseButton.setHeight(header.getDefaultRowHeight() + "px");

                return;
            }

            Element firstHeaderCell = header.getRowElement(0)
                    .getFirstChildElement();
            double height = WidgetUtil
                    .getRequiredHeightBoundingClientRectDouble(firstHeaderCell)
                    - (WidgetUtil.measureVerticalBorder(getElement()) / 2);
            openCloseButton.setHeight(height + "px");
        }

        private void updateVisibility() {
            final boolean hasWidgets = content.getWidgetCount() > 0;
            final boolean isVisible = isInDOM();
            if (isVisible && !hasWidgets) {
                Grid.setParent(this, null);
                getElement().removeFromParent();
            } else if (!isVisible && hasWidgets) {
                close();
                grid.getElement().appendChild(getElement());
                Grid.setParent(this, grid);
                // border calculation won't work until attached
                setHeightToHeaderCellHeight();
            }
        }

        private boolean isInDOM() {
            return getParent() != null;
        }

        @Override
        protected void onAttach() {
            super.onAttach();
            // make sure the button will get correct height if the button should
            // be visible when the grid is rendered the first time.
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    setHeightToHeaderCellHeight();
                }
            });
        }

        @Override
        public boolean isEnabled() {
            return openCloseButton.isEnabled();
        }

        @Override
        public void setEnabled(boolean enabled) {
            if (!enabled && isOpen()) {
                close();
            }

            openCloseButton.setEnabled(enabled);
        }
    }

    /**
     * UI and functionality related to hiding columns with toggles in the
     * sidebar.
     */
    private final class ColumnHider {

        /** Map from columns to their hiding toggles, component might change */
        private HashMap<Column<?, T>, MenuItem> columnToHidingToggleMap = new HashMap<Grid.Column<?, T>, MenuItem>();

        /**
         * When column is being hidden with a toggle, do not refresh toggles for
         * no reason. Also helps for keeping the keyboard navigation working.
         */
        private boolean hidingColumn;

        private void updateColumnHidable(final Column<?, T> column) {
            if (column.isHidable()) {
                MenuItem toggle = columnToHidingToggleMap.get(column);
                if (toggle == null) {
                    toggle = createToggle(column);
                }
                toggle.setStyleName("hidden", column.isHidden());
            } else if (columnToHidingToggleMap.containsKey(column)) {
                sidebar.menuBar.removeItem((columnToHidingToggleMap
                        .remove(column)));
            }
            updateTogglesOrder();
        }

        private MenuItem createToggle(final Column<?, T> column) {
            MenuItem toggle = new MenuItem(createHTML(column), true,
                    new ScheduledCommand() {

                        @Override
                        public void execute() {
                            hidingColumn = true;
                            column.setHidden(!column.isHidden(), true);
                            hidingColumn = false;
                        }
                    });
            toggle.addStyleName("column-hiding-toggle");
            columnToHidingToggleMap.put(column, toggle);
            return toggle;
        }

        private String createHTML(Column<?, T> column) {
            final StringBuffer buf = new StringBuffer();
            buf.append("<span class=\"");
            if (column.isHidden()) {
                buf.append("v-off");
            } else {
                buf.append("v-on");
            }
            buf.append("\"><div>");
            String caption = column.getHidingToggleCaption();
            if (caption == null) {
                caption = column.headerCaption;
            }
            buf.append(caption);
            buf.append("</div></span>");

            return buf.toString();
        }

        private void updateTogglesOrder() {
            if (!hidingColumn) {
                int lastIndex = 0;
                for (Column<?, T> column : getColumns()) {
                    if (column.isHidable()) {
                        final MenuItem menuItem = columnToHidingToggleMap
                                .get(column);
                        sidebar.menuBar.removeItem(menuItem);
                        sidebar.menuBar.insertItem(menuItem, lastIndex++);
                    }
                }
            }
        }

        private void updateHidingToggle(Column<?, T> column) {
            if (column.isHidable()) {
                MenuItem toggle = columnToHidingToggleMap.get(column);
                toggle.setHTML(createHTML(column));
                toggle.setStyleName("hidden", column.isHidden());
            } // else we can just ignore
        }

        private void removeColumnHidingToggle(Column<?, T> column) {
            sidebar.menuBar.removeItem(columnToHidingToggleMap.get(column));
        }

    }

    /**
     * Escalator used internally by grid to render the rows
     */
    private Escalator escalator = GWT.create(Escalator.class);

    private final Header header = GWT.create(Header.class);

    private final Footer footer = GWT.create(Footer.class);

    private final Sidebar sidebar = new Sidebar(this);

    /**
     * List of columns in the grid. Order defines the visible order.
     */
    private List<Column<?, T>> columns = new ArrayList<Column<?, T>>();

    /**
     * The datasource currently in use. <em>Note:</em> it is <code>null</code>
     * on initialization, but not after that.
     */
    private DataSource<T> dataSource;

    /**
     * Currently available row range in DataSource.
     */
    private Range currentDataAvailable = Range.withLength(0, 0);

    /**
     * The number of frozen columns, 0 freezes the selection column if
     * displayed, -1 also prevents selection col from freezing.
     */
    private int frozenColumnCount = 0;

    /**
     * Current sort order. The (private) sort() method reads this list to
     * determine the order in which to present rows.
     */
    private List<SortOrder> sortOrder = new ArrayList<SortOrder>();

    private Renderer<Boolean> selectColumnRenderer = null;

    private SelectionColumn selectionColumn;

    private String rowStripeStyleName;
    private String rowHasDataStyleName;
    private String rowSelectedStyleName;
    private String cellFocusStyleName;
    private String rowFocusStyleName;

    /**
     * Current selection model.
     */
    private SelectionModel<T> selectionModel;

    protected final CellFocusHandler cellFocusHandler;

    private final UserSorter sorter = new UserSorter();

    private final Editor<T> editor = GWT.create(Editor.class);

    private boolean dataIsBeingFetched = false;

    /**
     * The cell a click event originated from
     * <p>
     * This is a workaround to make Chrome work like Firefox. In Chrome,
     * normally if you start a drag on one cell and release on:
     * <ul>
     * <li>that same cell, the click event is that {@code <td>}.
     * <li>a cell on that same row, the click event is the parent {@code <tr>}.
     * <li>a cell on another row, the click event is the table section ancestor
     * ({@code <thead>}, {@code <tbody>} or {@code <tfoot>}).
     * </ul>
     * 
     * @see #onBrowserEvent(Event)
     */
    private Cell cellOnPrevMouseDown;

    /**
     * A scheduled command to re-evaluate the widths of <em>all columns</em>
     * that have calculated widths. Most probably called because
     * minwidth/maxwidth/expandratio has changed.
     */
    private final AutoColumnWidthsRecalculator autoColumnWidthsRecalculator = new AutoColumnWidthsRecalculator();

    private boolean enabled = true;

    private DetailsGenerator detailsGenerator = DetailsGenerator.NULL;
    private GridSpacerUpdater gridSpacerUpdater = new GridSpacerUpdater();
    /** A set keeping track of the indices of all currently open details */
    private Set<Integer> visibleDetails = new HashSet<Integer>();

    private boolean columnReorderingAllowed;

    private ColumnHider columnHider = new ColumnHider();

    private DragAndDropHandler dndHandler = new DragAndDropHandler();

    private AutoScroller autoScroller = new AutoScroller(this);

    private DragAndDropHandler.DragAndDropCallback headerCellDndCallback = new DragAndDropCallback() {

        private final AutoScrollerCallback autoScrollerCallback = new AutoScrollerCallback() {

            @Override
            public void onAutoScroll(int scrollDiff) {
                autoScrollX = scrollDiff;
                onDragUpdate(null);
            }

            @Override
            public void onAutoScrollReachedMin() {
                // make sure the drop marker is visible on the left
                autoScrollX = 0;
                updateDragDropMarker(clientX);
            }

            @Override
            public void onAutoScrollReachedMax() {
                // make sure the drop marker is visible on the right
                autoScrollX = 0;
                updateDragDropMarker(clientX);
            }
        };
        /**
         * Elements for displaying the dragged column(s) and drop marker
         * properly
         */
        private Element table;
        private Element tableHeader;
        /** Marks the column drop location */
        private Element dropMarker;
        /** A copy of the dragged column(s), moves with cursor. */
        private Element dragElement;
        /** Tracks index of the column whose left side the drop would occur */
        private int latestColumnDropIndex;
        /**
         * Map of possible drop positions for the column and the corresponding
         * column index.
         */
        private final TreeMap<Double, Integer> possibleDropPositions = new TreeMap<Double, Integer>();
        /**
         * Makes sure that drag cancel doesn't cause anything unwanted like sort
         */
        private HandlerRegistration columnSortPreventRegistration;

        private int clientX;

        /** How much the grid is being auto scrolled while dragging. */
        private int autoScrollX;

        /** Captures the value of the focused column before reordering */
        private int focusedColumnIndex;

        /** Offset caused by the drag and drop marker width */
        private double dropMarkerWidthOffset;

        private void initHeaderDragElementDOM() {
            if (table == null) {
                tableHeader = DOM.createTHead();
                dropMarker = DOM.createDiv();
                tableHeader.appendChild(dropMarker);
                table = DOM.createTable();
                table.appendChild(tableHeader);
                table.setClassName("header-drag-table");
            }
            // update the style names on each run in case primary name has been
            // modified
            tableHeader.setClassName(escalator.getHeader().getElement()
                    .getClassName());
            dropMarker.setClassName(getStylePrimaryName() + "-drop-marker");
            int topOffset = 0;
            for (int i = 0; i < eventCell.getRowIndex(); i++) {
                topOffset += escalator.getHeader().getRowElement(i)
                        .getFirstChildElement().getOffsetHeight();
            }
            tableHeader.getStyle().setTop(topOffset, Unit.PX);

            getElement().appendChild(table);

            dropMarkerWidthOffset = WidgetUtil
                    .getRequiredWidthBoundingClientRectDouble(dropMarker) / 2;
        }

        @Override
        public void onDragUpdate(Event e) {
            if (e != null) {
                clientX = WidgetUtil.getTouchOrMouseClientX(e);
                autoScrollX = 0;
            }
            resolveDragElementHorizontalPosition(clientX);
            updateDragDropMarker(clientX);
        }

        private void updateDragDropMarker(final int clientX) {
            final double scrollLeft = getScrollLeft();
            final double cursorXCoordinate = clientX
                    - escalator.getHeader().getElement().getAbsoluteLeft();
            final Entry<Double, Integer> cellEdgeOnRight = possibleDropPositions
                    .ceilingEntry(cursorXCoordinate);
            final Entry<Double, Integer> cellEdgeOnLeft = possibleDropPositions
                    .floorEntry(cursorXCoordinate);
            final double diffToRightEdge = cellEdgeOnRight == null ? Double.MAX_VALUE
                    : cellEdgeOnRight.getKey() - cursorXCoordinate;
            final double diffToLeftEdge = cellEdgeOnLeft == null ? Double.MAX_VALUE
                    : cursorXCoordinate - cellEdgeOnLeft.getKey();

            double dropMarkerLeft = 0 - scrollLeft;
            if (diffToRightEdge > diffToLeftEdge) {
                latestColumnDropIndex = cellEdgeOnLeft.getValue();
                dropMarkerLeft += cellEdgeOnLeft.getKey();
            } else {
                latestColumnDropIndex = cellEdgeOnRight.getValue();
                dropMarkerLeft += cellEdgeOnRight.getKey();
            }

            dropMarkerLeft += autoScrollX;

            final double frozenColumnsWidth = autoScroller
                    .getFrozenColumnsWidth();
            final double rightBoundaryForDrag = getSidebarBoundaryComparedTo(dropMarkerLeft);
            final int visibleColumns = getVisibleColumns().size();

            // First check if the drop marker should move left because of the
            // sidebar opening button. this only the case if the grid is
            // scrolled to the right
            if (latestColumnDropIndex == visibleColumns
                    && rightBoundaryForDrag < dropMarkerLeft
                    && dropMarkerLeft <= escalator.getInnerWidth()) {
                dropMarkerLeft = rightBoundaryForDrag - dropMarkerWidthOffset;
            }

            // Check if the drop marker shouldn't be shown at all
            else if (dropMarkerLeft < frozenColumnsWidth
                    || dropMarkerLeft > Math.min(rightBoundaryForDrag,
                            escalator.getInnerWidth()) || dropMarkerLeft < 0) {
                dropMarkerLeft = -10000000;
            }
            dropMarker.getStyle().setLeft(dropMarkerLeft, Unit.PX);
        }

        private void resolveDragElementHorizontalPosition(final int clientX) {
            double left = clientX - table.getAbsoluteLeft();

            // Do not show the drag element beyond a spanned header cell
            // limitation
            final Double leftBound = possibleDropPositions.firstKey();
            final Double rightBound = possibleDropPositions.lastKey();
            final double scrollLeft = getScrollLeft();
            if (left + scrollLeft < leftBound) {
                left = leftBound - scrollLeft + autoScrollX;
            } else if (left + scrollLeft > rightBound) {
                left = rightBound - scrollLeft + autoScrollX;
            }

            // Do not show the drag element beyond the grid
            final double sidebarBoundary = getSidebarBoundaryComparedTo(left);
            final double gridBoundary = escalator.getInnerWidth();
            final double rightBoundary = Math
                    .min(sidebarBoundary, gridBoundary);

            // Do not show on left of the frozen columns (even if scrolled)
            final int frozenColumnsWidth = (int) autoScroller
                    .getFrozenColumnsWidth();

            left = Math.max(frozenColumnsWidth, Math.min(left, rightBoundary));

            left -= dragElement.getClientWidth() / 2;
            dragElement.getStyle().setLeft(left, Unit.PX);
        }

        private boolean isSidebarOnDraggedRow() {
            return eventCell.getRowIndex() == 0 && sidebar.isInDOM()
                    && !sidebar.isOpen();
        }

        /**
         * Returns the sidebar left coordinate, in relation to the grid. Or
         * Double.MAX_VALUE if it doesn't cause a boundary.
         */
        private double getSidebarBoundaryComparedTo(double left) {
            if (isSidebarOnDraggedRow()) {
                double absoluteLeft = left + getElement().getAbsoluteLeft();
                double sidebarLeft = sidebar.getElement().getAbsoluteLeft();
                double diff = absoluteLeft - sidebarLeft;

                if (diff > 0) {
                    return left - diff;
                }
            }
            return Double.MAX_VALUE;
        }

        @Override
        public boolean onDragStart(Event e) {
            calculatePossibleDropPositions();

            if (possibleDropPositions.isEmpty()) {
                return false;
            }

            initHeaderDragElementDOM();
            // needs to clone focus and sorting indicators too (UX)
            dragElement = DOM.clone(eventCell.getElement(), true);
            dragElement.getStyle().clearWidth();
            dropMarker.getStyle().setProperty("height",
                    dragElement.getStyle().getHeight());
            tableHeader.appendChild(dragElement);
            // mark the column being dragged for styling
            eventCell.getElement().addClassName("dragged");
            // mark the floating cell, for styling & testing
            dragElement.addClassName("dragged-column-header");

            // start the auto scroll handler
            autoScroller.setScrollArea(60);
            autoScroller.start(e, ScrollAxis.HORIZONTAL, autoScrollerCallback);
            return true;
        }

        @Override
        public void onDragEnd() {
            table.removeFromParent();
            dragElement.removeFromParent();
            eventCell.getElement().removeClassName("dragged");
        }

        @Override
        public void onDrop() {
            final int draggedColumnIndex = eventCell.getColumnIndex();
            final int colspan = header.getRow(eventCell.getRowIndex())
                    .getCell(eventCell.getColumn()).getColspan();
            if (latestColumnDropIndex != draggedColumnIndex
                    && latestColumnDropIndex != (draggedColumnIndex + colspan)) {
                List<Column<?, T>> columns = getColumns();
                List<Column<?, T>> reordered = new ArrayList<Column<?, T>>();
                if (draggedColumnIndex < latestColumnDropIndex) {
                    reordered.addAll(columns.subList(0, draggedColumnIndex));
                    reordered.addAll(columns.subList(draggedColumnIndex
                            + colspan, latestColumnDropIndex));
                    reordered.addAll(columns.subList(draggedColumnIndex,
                            draggedColumnIndex + colspan));
                    reordered.addAll(columns.subList(latestColumnDropIndex,
                            columns.size()));
                } else {
                    reordered.addAll(columns.subList(0, latestColumnDropIndex));
                    reordered.addAll(columns.subList(draggedColumnIndex,
                            draggedColumnIndex + colspan));
                    reordered.addAll(columns.subList(latestColumnDropIndex,
                            draggedColumnIndex));
                    reordered.addAll(columns.subList(draggedColumnIndex
                            + colspan, columns.size()));
                }
                reordered.remove(selectionColumn); // since setColumnOrder will
                                                   // add it anyway!

                // capture focused cell column before reorder
                Cell focusedCell = cellFocusHandler.getFocusedCell();
                if (focusedCell != null) {
                    // take hidden columns into account
                    focusedColumnIndex = getColumns().indexOf(
                            getVisibleColumn(focusedCell.getColumn()));
                }

                Column<?, T>[] array = reordered.toArray(new Column[reordered
                        .size()]);
                setColumnOrder(array);
                transferCellFocusOnDrop();
            } // else no reordering
        }

        private void transferCellFocusOnDrop() {
            final Cell focusedCell = cellFocusHandler.getFocusedCell();
            if (focusedCell != null) {
                final int focusedColumnIndexDOM = focusedCell.getColumn();
                final int focusedRowIndex = focusedCell.getRow();
                final int draggedColumnIndex = eventCell.getColumnIndex();
                // transfer focus if it was effected by the new column order
                final RowContainer rowContainer = escalator
                        .findRowContainer(focusedCell.getElement());
                if (focusedColumnIndex == draggedColumnIndex) {
                    // move with the dragged column
                    int adjustedDropIndex = latestColumnDropIndex > draggedColumnIndex ? latestColumnDropIndex - 1
                            : latestColumnDropIndex;
                    // remove hidden columns from indexing
                    adjustedDropIndex = getVisibleColumns().indexOf(
                            getColumn(adjustedDropIndex));
                    cellFocusHandler.setCellFocus(focusedRowIndex,
                            adjustedDropIndex, rowContainer);
                } else if (latestColumnDropIndex <= focusedColumnIndex
                        && draggedColumnIndex > focusedColumnIndex) {
                    cellFocusHandler.setCellFocus(focusedRowIndex,
                            focusedColumnIndexDOM + 1, rowContainer);
                } else if (latestColumnDropIndex > focusedColumnIndex
                        && draggedColumnIndex < focusedColumnIndex) {
                    cellFocusHandler.setCellFocus(focusedRowIndex,
                            focusedColumnIndexDOM - 1, rowContainer);
                }
            }
        }

        @Override
        public void onDragCancel() {
            // cancel next click so that we may prevent column sorting if
            // mouse was released on top of the dragged cell
            if (columnSortPreventRegistration == null) {
                columnSortPreventRegistration = Event
                        .addNativePreviewHandler(new NativePreviewHandler() {

                            @Override
                            public void onPreviewNativeEvent(
                                    NativePreviewEvent event) {
                                if (event.getTypeInt() == Event.ONCLICK) {
                                    event.cancel();
                                    event.getNativeEvent().preventDefault();
                                    columnSortPreventRegistration
                                            .removeHandler();
                                    columnSortPreventRegistration = null;
                                }
                            }
                        });
            }
            autoScroller.stop();
        }

        /**
         * Returns the amount of frozen columns. The selection column is always
         * considered frozen, since it can't be moved.
         */
        private int getSelectionAndFrozenColumnCount() {
            // no matter if selection column is frozen or not, it is considered
            // frozen for column dnd reorder
            if (getSelectionModel().getSelectionColumnRenderer() != null) {
                return Math.max(0, getFrozenColumnCount()) + 1;
            } else {
                return Math.max(0, getFrozenColumnCount());
            }
        }

        @SuppressWarnings("boxing")
        private void calculatePossibleDropPositions() {
            possibleDropPositions.clear();

            final int draggedColumnIndex = eventCell.getColumnIndex();
            final StaticRow<?> draggedCellRow = header.getRow(eventCell
                    .getRowIndex());
            final int draggedColumnRightIndex = draggedColumnIndex
                    + draggedCellRow.getCell(eventCell.getColumn())
                            .getColspan();
            final int frozenColumns = getSelectionAndFrozenColumnCount();
            final Range draggedCellRange = Range.between(draggedColumnIndex,
                    draggedColumnRightIndex);
            /*
             * If the dragged cell intersects with a spanned cell in any other
             * header or footer row, then the drag is limited inside that
             * spanned cell. The same rules apply: the cell can't be dropped
             * inside another spanned cell. The left and right bounds keep track
             * of the edges of the most limiting spanned cell.
             */
            int leftBound = -1;
            int rightBound = getColumnCount() + 1;

            final HashSet<Integer> unavailableColumnDropIndices = new HashSet<Integer>();
            final List<StaticRow<?>> rows = new ArrayList<StaticRow<?>>();
            rows.addAll(header.getRows());
            rows.addAll(footer.getRows());
            for (StaticRow<?> row : rows) {
                if (!row.hasSpannedCells()) {
                    continue;
                }
                final boolean isDraggedCellRow = row.equals(draggedCellRow);
                for (int cellColumnIndex = frozenColumns; cellColumnIndex < getColumnCount(); cellColumnIndex++) {
                    StaticCell cell = row.getCell(getColumn(cellColumnIndex));
                    int colspan = cell.getColspan();
                    if (colspan <= 1) {
                        continue;
                    }
                    final int cellColumnRightIndex = cellColumnIndex + colspan;
                    final Range cellRange = Range.between(cellColumnIndex,
                            cellColumnRightIndex);
                    final boolean intersects = draggedCellRange
                            .intersects(cellRange);
                    if (intersects && !isDraggedCellRow) {
                        // if the currently iterated cell is inside or same as
                        // the dragged cell, then it doesn't restrict the drag
                        if (cellRange.isSubsetOf(draggedCellRange)) {
                            cellColumnIndex = cellColumnRightIndex - 1;
                            continue;
                        }
                        /*
                         * if the dragged cell is a spanned cell and it crosses
                         * with the currently iterated cell without sharing
                         * either start or end then not possible to drag the
                         * cell.
                         */
                        if (!draggedCellRange.isSubsetOf(cellRange)) {
                            return;
                        }
                        // the spanned cell overlaps the dragged cell (but is
                        // not the dragged cell)
                        if (cellColumnIndex <= draggedColumnIndex
                                && cellColumnIndex > leftBound) {
                            leftBound = cellColumnIndex;
                        }
                        if (cellColumnRightIndex < rightBound) {
                            rightBound = cellColumnRightIndex;
                        }
                        cellColumnIndex = cellColumnRightIndex - 1;
                    }

                    else { // can't drop inside a spanned cell, or this is the
                           // dragged cell
                        while (colspan > 1) {
                            cellColumnIndex++;
                            colspan--;
                            unavailableColumnDropIndices.add(cellColumnIndex);
                        }
                    }
                }
            }

            if (leftBound == (rightBound - 1)) {
                return;
            }

            double position = autoScroller.getFrozenColumnsWidth();
            // iterate column indices and add possible drop positions
            for (int i = frozenColumns; i < getColumnCount(); i++) {
                Column<?, T> column = getColumn(i);
                if (!unavailableColumnDropIndices.contains(i)
                        && !column.isHidden()) {
                    if (leftBound != -1) {
                        if (i >= leftBound && i <= rightBound) {
                            possibleDropPositions.put(position, i);
                        }
                    } else {
                        possibleDropPositions.put(position, i);
                    }
                }
                position += column.getWidthActual();
            }

            if (leftBound == -1) {
                // add the right side of the last column as columns.size()
                possibleDropPositions.put(position, getColumnCount());
            }
        }

    };

    /**
     * Enumeration for easy setting of selection mode.
     */
    public enum SelectionMode {

        /**
         * Shortcut for {@link SelectionModelSingle}.
         */
        SINGLE {

            @Override
            protected <T> SelectionModel<T> createModel() {
                return GWT.create(SelectionModelSingle.class);
            }
        },

        /**
         * Shortcut for {@link SelectionModelMulti}.
         */
        MULTI {

            @Override
            protected <T> SelectionModel<T> createModel() {
                return GWT.create(SelectionModelMulti.class);
            }
        },

        /**
         * Shortcut for {@link SelectionModelNone}.
         */
        NONE {

            @Override
            protected <T> SelectionModel<T> createModel() {
                return GWT.create(SelectionModelNone.class);
            }
        };

        protected abstract <T> SelectionModel<T> createModel();
    }

    /**
     * Base class for grid columns internally used by the Grid. The user should
     * use {@link Column} when creating new columns.
     * 
     * @param <C>
     *            the column type
     * 
     * @param <T>
     *            the row type
     */
    public static abstract class Column<C, T> {

        /**
         * Default renderer for GridColumns. Renders everything into text
         * through {@link Object#toString()}.
         */
        private final class DefaultTextRenderer implements Renderer<Object> {
            boolean warned = false;
            private final String DEFAULT_RENDERER_WARNING = "This column uses a dummy default TextRenderer. "
                    + "A more suitable renderer should be set using the setRenderer() method.";

            @Override
            public void render(RendererCellReference cell, Object data) {
                if (!warned && !(data instanceof String)) {
                    getLogger().warning(
                            Column.this.toString() + ": "
                                    + DEFAULT_RENDERER_WARNING);
                    warned = true;
                }

                final String text;
                if (data == null) {
                    text = "";
                } else {
                    text = data.toString();
                }

                cell.getElement().setInnerText(text);
            }
        }

        /**
         * the column is associated with
         */
        private Grid<T> grid;

        /**
         * Width of column in pixels as {@link #setWidth(double)} has been
         * called
         */
        private double widthUser = GridConstants.DEFAULT_COLUMN_WIDTH_PX;

        /**
         * Renderer for rendering a value into the cell
         */
        private Renderer<? super C> bodyRenderer;

        private boolean sortable = false;

        private boolean editable = true;

        private boolean resizable = true;

        private boolean hidden = false;

        private boolean hidable = false;

        private String headerCaption = "";

        private String hidingToggleCaption = null;

        private double minimumWidthPx = GridConstants.DEFAULT_MIN_WIDTH;
        private double maximumWidthPx = GridConstants.DEFAULT_MAX_WIDTH;
        private int expandRatio = GridConstants.DEFAULT_EXPAND_RATIO;

        /**
         * Constructs a new column with a simple TextRenderer.
         */
        public Column() {
            setRenderer(new DefaultTextRenderer());
        }

        /**
         * Constructs a new column with a simple TextRenderer.
         * 
         * @param caption
         *            The header caption for this column
         * 
         * @throws IllegalArgumentException
         *             if given header caption is null
         */
        public Column(String caption) throws IllegalArgumentException {
            this();
            setHeaderCaption(caption);
        }

        /**
         * Constructs a new column with a custom renderer.
         * 
         * @param renderer
         *            The renderer to use for rendering the cells
         * 
         * @throws IllegalArgumentException
         *             if given Renderer is null
         */
        public Column(Renderer<? super C> renderer)
                throws IllegalArgumentException {
            setRenderer(renderer);
        }

        /**
         * Constructs a new column with a custom renderer.
         * 
         * @param renderer
         *            The renderer to use for rendering the cells
         * @param caption
         *            The header caption for this column
         * 
         * @throws IllegalArgumentException
         *             if given Renderer or header caption is null
         */
        public Column(String caption, Renderer<? super C> renderer)
                throws IllegalArgumentException {
            this(renderer);
            setHeaderCaption(caption);
        }

        /**
         * Internally used by the grid to set itself
         * 
         * @param grid
         */
        private void setGrid(Grid<T> grid) {
            if (this.grid != null && grid != null) {
                // Trying to replace grid
                throw new IllegalStateException("Column already is attached "
                        + "to a grid. Remove the column first from the grid "
                        + "and then add it. (in: " + toString() + ")");
            }

            if (this.grid != null) {
                this.grid.recalculateColumnWidths();
            }
            this.grid = grid;
            if (this.grid != null) {
                this.grid.recalculateColumnWidths();
            }
        }

        /**
         * Sets a header caption for this column.
         * 
         * @param caption
         *            The header caption for this column
         * @return the column itself
         * 
         */
        public Column<C, T> setHeaderCaption(String caption) {
            if (caption == null) {
                caption = "";
            }

            if (!this.headerCaption.equals(caption)) {
                this.headerCaption = caption;
                if (grid != null) {
                    updateHeader();
                }
            }

            return this;
        }

        /**
         * Returns the current header caption for this column
         * 
         * @since 7.6
         * @return the header caption string
         */
        public String getHeaderCaption() {
            return headerCaption;
        }

        private void updateHeader() {
            HeaderRow row = grid.getHeader().getDefaultRow();
            if (row != null) {
                row.getCell(this).setText(headerCaption);
                if (isHidable()) {
                    grid.columnHider.updateHidingToggle(this);
                }
            }
        }

        /**
         * Returns the data that should be rendered into the cell. By default
         * returning Strings and Widgets are supported. If the return type is a
         * String then it will be treated as preformatted text.
         * <p>
         * To support other types you will need to pass a custom renderer to the
         * column via the column constructor.
         * 
         * @param row
         *            The row object that provides the cell content.
         * 
         * @return The cell content
         */
        public abstract C getValue(T row);

        /**
         * The renderer to render the cell with. By default renders the data as
         * a String or adds the widget into the cell if the column type is of
         * widget type.
         * 
         * @return The renderer to render the cell content with
         */
        public Renderer<? super C> getRenderer() {
            return bodyRenderer;
        }

        /**
         * Sets a custom {@link Renderer} for this column.
         * 
         * @param renderer
         *            The renderer to use for rendering the cells
         * @return the column itself
         * 
         * @throws IllegalArgumentException
         *             if given Renderer is null
         */
        public Column<C, T> setRenderer(Renderer<? super C> renderer)
                throws IllegalArgumentException {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer cannot be null.");
            }

            if (renderer != bodyRenderer) {
                // Variables used to restore removed column.
                boolean columnRemoved = false;
                double widthInConfiguration = 0.0d;
                ColumnConfiguration conf = null;
                int index = 0;

                if (grid != null
                        && (bodyRenderer instanceof WidgetRenderer || renderer instanceof WidgetRenderer)) {
                    // Column needs to be recreated.
                    index = grid.getColumns().indexOf(this);
                    conf = grid.escalator.getColumnConfiguration();
                    widthInConfiguration = conf.getColumnWidth(index);

                    conf.removeColumns(index, 1);
                    columnRemoved = true;
                }

                // Complex renderers need to be destroyed.
                if (bodyRenderer instanceof ComplexRenderer) {
                    ((ComplexRenderer) bodyRenderer).destroy();
                }

                bodyRenderer = renderer;

                if (columnRemoved) {
                    // Restore the column.
                    conf.insertColumns(index, 1);
                    conf.setColumnWidth(index, widthInConfiguration);
                }

                if (grid != null) {
                    grid.refreshBody();
                }
            }
            return this;
        }

        /**
         * Sets the pixel width of the column. Use a negative value for the grid
         * to autosize column based on content and available space.
         * <p>
         * This action is done "finally", once the current execution loop
         * returns. This is done to reduce overhead of unintentionally always
         * recalculate all columns, when modifying several columns at once.
         * <p>
         * If the column is currently {@link #isHidden() hidden}, then this set
         * width has effect only once the column has been made visible again.
         * 
         * @param pixels
         *            the width in pixels or negative for auto sizing
         */
        public Column<C, T> setWidth(double pixels) {
            if (!WidgetUtil.pixelValuesEqual(widthUser, pixels)) {
                widthUser = pixels;
                if (!isHidden()) {
                    scheduleColumnWidthRecalculator();
                }
            }
            return this;
        }

        void doSetWidth(double pixels) {
            assert !isHidden() : "applying width for a hidden column";
            if (grid != null) {
                int index = grid.getVisibleColumns().indexOf(this);
                ColumnConfiguration conf = grid.escalator
                        .getColumnConfiguration();
                conf.setColumnWidth(index, pixels);
            }
        }

        /**
         * Returns the pixel width of the column as given by the user.
         * <p>
         * <em>Note:</em> If a negative value was given to
         * {@link #setWidth(double)}, that same negative value is returned here.
         * <p>
         * <em>Note:</em> Returns the value, even if the column is currently
         * {@link #isHidden() hidden}.
         * 
         * @return pixel width of the column, or a negative number if the column
         *         width has been automatically calculated.
         * @see #setWidth(double)
         * @see #getWidthActual()
         */
        public double getWidth() {
            return widthUser;
        }

        /**
         * Returns the effective pixel width of the column.
         * <p>
         * This differs from {@link #getWidth()} only when the column has been
         * automatically resized, or when the column is currently
         * {@link #isHidden() hidden}, when the value is 0.
         * 
         * @return pixel width of the column.
         */
        public double getWidthActual() {
            if (isHidden()) {
                return 0;
            }
            return grid.escalator.getColumnConfiguration()
                    .getColumnWidthActual(
                            grid.getVisibleColumns().indexOf(this));
        }

        void reapplyWidth() {
            scheduleColumnWidthRecalculator();
        }

        /**
         * Sets whether the column should be sortable by the user. The grid can
         * be sorted by a sortable column by clicking or tapping the column's
         * default header. Programmatic sorting using the Grid#sort methods is
         * not affected by this setting.
         * 
         * @param sortable
         *            {@code true} if the user should be able to sort the
         *            column, {@code false} otherwise
         * @return the column itself
         */
        public Column<C, T> setSortable(boolean sortable) {
            if (this.sortable != sortable) {
                this.sortable = sortable;
                if (grid != null) {
                    grid.refreshHeader();
                }
            }
            return this;
        }

        /**
         * Returns whether the user can sort the grid by this column.
         * <p>
         * <em>Note:</em> it is possible to sort by this column programmatically
         * using the Grid#sort methods regardless of the returned value.
         * 
         * @return {@code true} if the column is sortable by the user,
         *         {@code false} otherwise
         */
        public boolean isSortable() {
            return sortable;
        }

        /**
         * Sets whether this column can be resized by the user.
         * 
         * @since 7.6
         * 
         * @param resizable
         *            {@code true} if this column should be resizable,
         *            {@code false} otherwise
         */
        public Column<C, T> setResizable(boolean resizable) {
            if (this.resizable != resizable) {
                this.resizable = resizable;
                if (grid != null) {
                    grid.refreshHeader();
                }
            }
            return this;
        }

        /**
         * Returns whether this column can be resized by the user. Default is
         * {@code true}.
         * <p>
         * <em>Note:</em> the column can be programmatically resized using
         * {@link #setWidth(double)} and {@link #setWidthUndefined()} regardless
         * of the returned value.
         * 
         * @since 7.6
         * 
         * @return {@code true} if this column is resizable, {@code false}
         *         otherwise
         */
        public boolean isResizable() {
            return resizable;
        }

        /**
         * Hides or shows the column. By default columns are visible before
         * explicitly hiding them.
         * 
         * @since 7.5.0
         * @param hidden
         *            <code>true</code> to hide the column, <code>false</code>
         *            to show
         */
        public Column<C, T> setHidden(boolean hidden) {
            setHidden(hidden, false);
            return this;
        }

        private void setHidden(boolean hidden, boolean userOriginated) {
            if (this.hidden != hidden) {
                if (hidden) {
                    grid.escalator.getColumnConfiguration().removeColumns(
                            grid.getVisibleColumns().indexOf(this), 1);
                    this.hidden = hidden;
                } else {
                    this.hidden = hidden;

                    final int columnIndex = grid.getVisibleColumns().indexOf(
                            this);
                    grid.escalator.getColumnConfiguration().insertColumns(
                            columnIndex, 1);

                    // make sure column is set to frozen if it needs to be,
                    // escalator doesn't handle situation where the added column
                    // would be the last frozen column
                    int gridFrozenColumns = grid.getFrozenColumnCount();
                    int escalatorFrozenColumns = grid.escalator
                            .getColumnConfiguration().getFrozenColumnCount();
                    if (gridFrozenColumns > escalatorFrozenColumns
                            && escalatorFrozenColumns == columnIndex) {
                        grid.escalator.getColumnConfiguration()
                                .setFrozenColumnCount(++escalatorFrozenColumns);
                    }
                }
                grid.columnHider.updateHidingToggle(this);
                grid.header.updateColSpans();
                grid.footer.updateColSpans();
                scheduleColumnWidthRecalculator();
                this.grid.fireEvent(new ColumnVisibilityChangeEvent<T>(this,
                        hidden, userOriginated));
            }
        }

        /**
         * Returns whether this column is hidden. Default is {@code false}.
         * 
         * @since 7.5.0
         * @return {@code true} if the column is currently hidden, {@code false}
         *         otherwise
         */
        public boolean isHidden() {
            return hidden;
        }

        /**
         * Set whether it is possible for the user to hide this column or not.
         * Default is {@code false}.
         * <p>
         * <em>Note:</em> it is still possible to hide the column
         * programmatically using {@link #setHidden(boolean)}.
         * 
         * @since 7.5.0
         * @param hidable
         *            {@code true} the user can hide this column, {@code false}
         *            otherwise
         */
        public Column<C, T> setHidable(boolean hidable) {
            if (this.hidable != hidable) {
                this.hidable = hidable;
                grid.columnHider.updateColumnHidable(this);
            }
            return this;
        }

        /**
         * Is it possible for the the user to hide this column. Default is
         * {@code false}.
         * <p>
         * <em>Note:</em> the column can be programmatically hidden using
         * {@link #setHidden(boolean)} regardless of the returned value.
         * 
         * @since 7.5.0
         * @return <code>true</code> if the user can hide the column,
         *         <code>false</code> if not
         */
        public boolean isHidable() {
            return hidable;
        }

        /**
         * Sets the hiding toggle's caption for this column. Shown in the toggle
         * for this column in the grid's sidebar when the column is
         * {@link #isHidable() hidable}.
         * <p>
         * The default value is <code>null</code>. In this case the header
         * caption is used, see {@link #setHeaderCaption(String)}.
         * 
         * @since 7.5.0
         * @param hidingToggleCaption
         *            the caption for the hiding toggle for this column
         */
        public Column<C, T> setHidingToggleCaption(String hidingToggleCaption) {
            this.hidingToggleCaption = hidingToggleCaption;
            if (isHidable()) {
                grid.columnHider.updateHidingToggle(this);
            }
            return this;
        }

        /**
         * Gets the hiding toggle caption for this column.
         * 
         * @since 7.5.0
         * @see #setHidingToggleCaption(String)
         * @return the hiding toggle's caption for this column
         */
        public String getHidingToggleCaption() {
            return hidingToggleCaption;
        }

        @Override
        public String toString() {
            String details = "";

            if (headerCaption != null && !headerCaption.isEmpty()) {
                details += "header:\"" + headerCaption + "\" ";
            } else {
                details += "header:empty ";
            }

            if (grid != null) {
                int index = grid.getColumns().indexOf(this);
                if (index != -1) {
                    details += "attached:#" + index + " ";
                } else {
                    details += "attached:unindexed ";
                }
            } else {
                details += "detached ";
            }

            details += "sortable:" + sortable + " ";

            return getClass().getSimpleName() + "[" + details.trim() + "]";
        }

        /**
         * Sets the minimum width for this column.
         * <p>
         * This defines the minimum guaranteed pixel width of the column
         * <em>when it is set to expand</em>.
         * <p>
         * This action is done "finally", once the current execution loop
         * returns. This is done to reduce overhead of unintentionally always
         * recalculate all columns, when modifying several columns at once.
         * 
         * @param pixels
         *            the minimum width
         * @return this column
         */
        public Column<C, T> setMinimumWidth(double pixels) {
            final double maxwidth = getMaximumWidth();
            if (pixels >= 0 && pixels > maxwidth && maxwidth >= 0) {
                throw new IllegalArgumentException("New minimum width ("
                        + pixels + ") was greater than maximum width ("
                        + maxwidth + ")");
            }

            if (minimumWidthPx != pixels) {
                minimumWidthPx = pixels;
                scheduleColumnWidthRecalculator();
            }
            return this;
        }

        /**
         * Sets the maximum width for this column.
         * <p>
         * This defines the maximum allowed pixel width of the column
         * <em>when it is set to expand</em>.
         * <p>
         * This action is done "finally", once the current execution loop
         * returns. This is done to reduce overhead of unintentionally always
         * recalculate all columns, when modifying several columns at once.
         * 
         * @param pixels
         *            the maximum width
         * @param immediately
         *            <code>true</code> if the widths should be executed
         *            immediately (ignoring lazy loading completely), or
         *            <code>false</code> if the command should be run after a
         *            while (duplicate non-immediately invocations are ignored).
         * @return this column
         */
        public Column<C, T> setMaximumWidth(double pixels) {
            final double minwidth = getMinimumWidth();
            if (pixels >= 0 && pixels < minwidth && minwidth >= 0) {
                throw new IllegalArgumentException("New maximum width ("
                        + pixels + ") was less than minimum width (" + minwidth
                        + ")");
            }

            if (maximumWidthPx != pixels) {
                maximumWidthPx = pixels;
                scheduleColumnWidthRecalculator();
            }
            return this;
        }

        /**
         * Sets the ratio with which the column expands.
         * <p>
         * By default, all columns expand equally (treated as if all of them had
         * an expand ratio of 1). Once at least one column gets a defined expand
         * ratio, the implicit expand ratio is removed, and only the defined
         * expand ratios are taken into account.
         * <p>
         * If a column has a defined width ({@link #setWidth(double)}), it
         * overrides this method's effects.
         * <p>
         * <em>Example:</em> A grid with three columns, with expand ratios 0, 1
         * and 2, respectively. The column with a <strong>ratio of 0 is exactly
         * as wide as its contents requires</strong>. The column with a ratio of
         * 1 is as wide as it needs, <strong>plus a third of any excess
         * space</strong>, bceause we have 3 parts total, and this column
         * reservs only one of those. The column with a ratio of 2, is as wide
         * as it needs to be, <strong>plus two thirds</strong> of the excess
         * width.
         * <p>
         * This action is done "finally", once the current execution loop
         * returns. This is done to reduce overhead of unintentionally always
         * recalculate all columns, when modifying several columns at once.
         * 
         * @param expandRatio
         *            the expand ratio of this column. {@code 0} to not have it
         *            expand at all. A negative number to clear the expand
         *            value.
         * @return this column
         */
        public Column<C, T> setExpandRatio(int ratio) {
            if (expandRatio != ratio) {
                expandRatio = ratio;
                scheduleColumnWidthRecalculator();
            }
            return this;
        }

        /**
         * Clears the column's expand ratio.
         * <p>
         * Same as calling {@link #setExpandRatio(int) setExpandRatio(-1)}
         * 
         * @return this column
         */
        public Column<C, T> clearExpandRatio() {
            return setExpandRatio(-1);
        }

        /**
         * Gets the minimum width for this column.
         * 
         * @return the minimum width for this column
         * @see #setMinimumWidth(double)
         */
        public double getMinimumWidth() {
            return minimumWidthPx;
        }

        /**
         * Gets the maximum width for this column.
         * 
         * @return the maximum width for this column
         * @see #setMaximumWidth(double)
         */
        public double getMaximumWidth() {
            return maximumWidthPx;
        }

        /**
         * Gets the expand ratio for this column.
         * 
         * @return the expand ratio for this column
         * @see #setExpandRatio(int)
         */
        public int getExpandRatio() {
            return expandRatio;
        }

        /**
         * Sets whether the values in this column should be editable by the user
         * when the row editor is active. By default columns are editable.
         * 
         * @param editable
         *            {@code true} to set this column editable, {@code false}
         *            otherwise
         * @return this column
         * 
         * @throws IllegalStateException
         *             if the editor is currently active
         * 
         * @see Grid#editRow(int)
         * @see Grid#isEditorActive()
         */
        public Column<C, T> setEditable(boolean editable) {
            if (editable != this.editable && grid.isEditorActive()) {
                throw new IllegalStateException(
                        "Cannot change column editable status while the editor is active");
            }
            this.editable = editable;
            return this;
        }

        /**
         * Returns whether the values in this column are editable by the user
         * when the row editor is active.
         * 
         * @return {@code true} if this column is editable, {@code false}
         *         otherwise
         * 
         * @see #setEditable(boolean)
         */
        public boolean isEditable() {
            return editable;
        }

        private void scheduleColumnWidthRecalculator() {
            if (grid != null) {
                grid.recalculateColumnWidths();
            } else {
                /*
                 * NOOP
                 * 
                 * Since setGrid() will call reapplyWidths as the colum is
                 * attached to a grid, it will call setWidth, which, in turn,
                 * will call this method again. Therefore, it's guaranteed that
                 * the recalculation is scheduled eventually, once the column is
                 * attached to a grid.
                 */
            }
        }

        /**
         * Resets the default header cell contents to column header captions.
         * 
         * @since 7.5.1
         * @param cell
         *            default header cell for this column
         */
        protected void setDefaultHeaderContent(HeaderCell cell) {
            cell.setText(headerCaption);
        }
    }

    protected class BodyUpdater implements EscalatorUpdater {

        @Override
        public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
            int rowIndex = row.getRow();
            rowReference.set(rowIndex, getDataSource().getRow(rowIndex),
                    row.getElement());
            for (FlyweightCell cell : cellsToAttach) {
                Renderer<?> renderer = findRenderer(cell);
                if (renderer instanceof ComplexRenderer) {
                    try {
                        Column<?, T> column = getVisibleColumn(cell.getColumn());
                        rendererCellReference.set(cell,
                                getColumns().indexOf(column), column);
                        ((ComplexRenderer<?>) renderer)
                                .init(rendererCellReference);
                    } catch (RuntimeException e) {
                        getLogger().log(
                                Level.SEVERE,
                                "Error initing cell in column "
                                        + cell.getColumn(), e);
                    }
                }
            }
        }

        @Override
        public void postAttach(Row row, Iterable<FlyweightCell> attachedCells) {
            for (FlyweightCell cell : attachedCells) {
                Renderer<?> renderer = findRenderer(cell);
                if (renderer instanceof WidgetRenderer) {
                    try {
                        WidgetRenderer<?, ?> widgetRenderer = (WidgetRenderer<?, ?>) renderer;

                        Widget widget = widgetRenderer.createWidget();
                        assert widget != null : "WidgetRenderer.createWidget() returned null. It should return a widget.";
                        assert widget.getParent() == null : "WidgetRenderer.createWidget() returned a widget which already is attached.";
                        assert cell.getElement().getChildCount() == 0 : "Cell content should be empty when adding Widget";

                        // Physical attach
                        cell.getElement().appendChild(widget.getElement());

                        // Logical attach
                        setParent(widget, Grid.this);
                    } catch (RuntimeException e) {
                        getLogger().log(
                                Level.SEVERE,
                                "Error attaching child widget in column "
                                        + cell.getColumn(), e);
                    }
                }
            }
        }

        @Override
        public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {
            int rowIndex = row.getRow();
            TableRowElement rowElement = row.getElement();
            T rowData = dataSource.getRow(rowIndex);

            boolean hasData = rowData != null;

            /*
             * TODO could be more efficient to build a list of all styles that
             * should be used and update the element only once instead of
             * attempting to update only the ones that have changed.
             */

            // Assign stylename for rows with data
            boolean usedToHaveData = rowElement
                    .hasClassName(rowHasDataStyleName);

            if (usedToHaveData != hasData) {
                setStyleName(rowElement, rowHasDataStyleName, hasData);
            }

            boolean isEvenIndex = (row.getRow() % 2 == 0);
            setStyleName(rowElement, rowStripeStyleName, !isEvenIndex);

            rowReference.set(rowIndex, rowData, rowElement);

            if (hasData) {
                setStyleName(rowElement, rowSelectedStyleName,
                        isSelected(rowData));

                if (rowStyleGenerator != null) {
                    try {
                        String rowStylename = rowStyleGenerator
                                .getStyle(rowReference);
                        setCustomStyleName(rowElement, rowStylename);
                    } catch (RuntimeException e) {
                        getLogger().log(
                                Level.SEVERE,
                                "Error generating styles for row "
                                        + row.getRow(), e);
                    }
                } else {
                    // Remove in case there was a generator previously
                    setCustomStyleName(rowElement, null);
                }
            } else if (usedToHaveData) {
                setStyleName(rowElement, rowSelectedStyleName, false);

                setCustomStyleName(rowElement, null);
            }

            cellFocusHandler.updateFocusedRowStyle(row);

            for (FlyweightCell cell : cellsToUpdate) {
                Column<?, T> column = getVisibleColumn(cell.getColumn());
                final int columnIndex = getColumns().indexOf(column);

                assert column != null : "Column was not found from cell ("
                        + cell.getColumn() + "," + cell.getRow() + ")";

                cellFocusHandler.updateFocusedCellStyle(cell,
                        escalator.getBody());

                if (hasData && cellStyleGenerator != null) {
                    try {
                        cellReference
                                .set(cell.getColumn(), columnIndex, column);
                        String generatedStyle = cellStyleGenerator
                                .getStyle(cellReference);
                        setCustomStyleName(cell.getElement(), generatedStyle);
                    } catch (RuntimeException e) {
                        getLogger().log(
                                Level.SEVERE,
                                "Error generating style for cell in column "
                                        + cell.getColumn(), e);
                    }
                } else if (hasData || usedToHaveData) {
                    setCustomStyleName(cell.getElement(), null);
                }

                Renderer renderer = column.getRenderer();

                try {
                    rendererCellReference.set(cell, columnIndex, column);
                    if (renderer instanceof ComplexRenderer) {
                        // Hide cell content if needed
                        ComplexRenderer clxRenderer = (ComplexRenderer) renderer;
                        if (hasData) {
                            if (!usedToHaveData) {
                                // Prepare cell for rendering
                                clxRenderer.setContentVisible(
                                        rendererCellReference, true);
                            }

                            Object value = column.getValue(rowData);
                            clxRenderer.render(rendererCellReference, value);

                        } else {
                            // Prepare cell for no data
                            clxRenderer.setContentVisible(
                                    rendererCellReference, false);
                        }

                    } else if (hasData) {
                        // Simple renderers just render
                        Object value = column.getValue(rowData);
                        renderer.render(rendererCellReference, value);

                    } else {
                        // Clear cell if there is no data
                        cell.getElement().removeAllChildren();
                    }
                } catch (RuntimeException e) {
                    getLogger().log(
                            Level.SEVERE,
                            "Error rendering cell in column "
                                    + cell.getColumn(), e);
                }
            }
        }

        @Override
        public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
            for (FlyweightCell cell : cellsToDetach) {
                Renderer<?> renderer = findRenderer(cell);
                if (renderer instanceof WidgetRenderer) {
                    try {
                        Widget w = WidgetUtil.findWidget(cell.getElement()
                                .getFirstChildElement(), null);
                        if (w != null) {

                            // Logical detach
                            setParent(w, null);

                            // Physical detach
                            cell.getElement().removeChild(w.getElement());
                        }
                    } catch (RuntimeException e) {
                        getLogger().log(
                                Level.SEVERE,
                                "Error detaching widget in column "
                                        + cell.getColumn(), e);
                    }
                }
            }
        }

        @Override
        public void postDetach(Row row, Iterable<FlyweightCell> detachedCells) {
            int rowIndex = row.getRow();
            // Passing null row data since it might not exist in the data source
            // any more
            rowReference.set(rowIndex, null, row.getElement());
            for (FlyweightCell cell : detachedCells) {
                Renderer<?> renderer = findRenderer(cell);
                if (renderer instanceof ComplexRenderer) {
                    try {
                        Column<?, T> column = getVisibleColumn(cell.getColumn());
                        rendererCellReference.set(cell,
                                getColumns().indexOf(column), column);
                        ((ComplexRenderer) renderer)
                                .destroy(rendererCellReference);
                    } catch (RuntimeException e) {
                        getLogger().log(
                                Level.SEVERE,
                                "Error destroying cell in column "
                                        + cell.getColumn(), e);
                    }
                }
            }
        }
    }

    protected class StaticSectionUpdater implements EscalatorUpdater {

        private StaticSection<?> section;
        private RowContainer container;

        public StaticSectionUpdater(StaticSection<?> section,
                RowContainer container) {
            super();
            this.section = section;
            this.container = container;
        }

        @Override
        public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {
            StaticSection.StaticRow<?> staticRow = section.getRow(row.getRow());
            final List<Column<?, T>> columns = getVisibleColumns();

            setCustomStyleName(row.getElement(), staticRow.getStyleName());

            for (FlyweightCell cell : cellsToUpdate) {
                final StaticSection.StaticCell metadata = staticRow
                        .getCell(columns.get(cell.getColumn()));

                // Decorate default row with sorting indicators
                if (staticRow instanceof HeaderRow) {
                    addSortingIndicatorsToHeaderRow((HeaderRow) staticRow, cell);
                }

                // Assign colspan to cell before rendering
                cell.setColSpan(metadata.getColspan());

                Element td = cell.getElement();
                td.removeAllChildren();
                setCustomStyleName(td, metadata.getStyleName());

                Element content;
                // Wrap text or html content in default header to isolate
                // the content from the possible column resize drag handle
                // next to it
                if (metadata.getType() != GridStaticCellType.WIDGET) {
                    content = DOM.createDiv();

                    if (staticRow instanceof HeaderRow) {
                        content.setClassName(getStylePrimaryName()
                                + "-column-header-content");
                        if (((HeaderRow) staticRow).isDefault()) {
                            content.setClassName(content.getClassName() + " "
                                    + getStylePrimaryName()
                                    + "-column-default-header-content");
                        }
                    } else if (staticRow instanceof FooterRow) {
                        content.setClassName(getStylePrimaryName()
                                + "-column-footer-content");
                    } else {
                        getLogger().severe(
                                "Unhandled static row type "
                                        + staticRow.getClass()
                                                .getCanonicalName());
                    }

                    td.appendChild(content);
                } else {
                    content = td;
                }

                switch (metadata.getType()) {
                case TEXT:
                    content.setInnerText(metadata.getText());
                    break;
                case HTML:
                    content.setInnerHTML(metadata.getHtml());
                    break;
                case WIDGET:
                    preDetach(row, Arrays.asList(cell));
                    content.setInnerHTML("");
                    postAttach(row, Arrays.asList(cell));
                    break;
                }

                // XXX: Should add only once in preAttach/postAttach or when
                // resizable status changes
                // Only add resize handles to default header row for now
                if (columns.get(cell.getColumn()).isResizable()
                        && staticRow instanceof HeaderRow
                        && ((HeaderRow) staticRow).isDefault()) {

                    final int column = cell.getColumn();
                    DragHandle dragger = new DragHandle(getStylePrimaryName()
                            + "-column-resize-handle",
                            new DragHandleCallback() {

                                private Column<?, T> col = getVisibleColumn(column);
                                private double initialWidth = 0;
                                private double minCellWidth;

                                @Override
                                public void onUpdate(double deltaX,
                                        double deltaY) {
                                    col.setWidth(Math.max(minCellWidth,
                                            initialWidth + deltaX));
                                }

                                @Override
                                public void onStart() {
                                    initialWidth = col.getWidthActual();

                                    minCellWidth = escalator
                                            .getMinCellWidth(getColumns()
                                                    .indexOf(col));
                                    for (Column<?, T> c : getColumns()) {
                                        if (selectionColumn == c) {
                                            // Don't modify selection column.
                                            continue;
                                        }

                                        if (c.getWidth() < 0) {
                                            c.setWidth(c.getWidthActual());
                                            fireEvent(new ColumnResizeEvent<T>(
                                                    c));
                                        }
                                    }

                                    WidgetUtil.setTextSelectionEnabled(
                                            getElement(), false);
                                }

                                @Override
                                public void onComplete() {
                                    fireEvent(new ColumnResizeEvent<T>(col));

                                    WidgetUtil.setTextSelectionEnabled(
                                            getElement(), true);
                                }

                                @Override
                                public void onCancel() {
                                    col.setWidth(initialWidth);

                                    WidgetUtil.setTextSelectionEnabled(
                                            getElement(), true);
                                }
                            });
                    dragger.addTo(td);
                }

                cellFocusHandler.updateFocusedCellStyle(cell, container);
            }
        }

        private void addSortingIndicatorsToHeaderRow(HeaderRow headerRow,
                FlyweightCell cell) {

            Element cellElement = cell.getElement();

            boolean sortedBefore = cellElement.hasClassName("sort-asc")
                    || cellElement.hasClassName("sort-desc");

            cleanup(cell);
            if (!headerRow.isDefault()) {
                // Nothing more to do if not in the default row
                return;
            }

            final Column<?, T> column = getVisibleColumn(cell.getColumn());
            SortOrder sortingOrder = getSortOrder(column);
            boolean sortable = column.isSortable();

            if (sortable) {
                cellElement.addClassName("sortable");
            }

            if (!sortable || sortingOrder == null) {
                // Only apply sorting indicators to sortable header columns
                return;
            }

            if (SortDirection.ASCENDING == sortingOrder.getDirection()) {
                cellElement.addClassName("sort-asc");
            } else {
                cellElement.addClassName("sort-desc");
            }

            int sortIndex = Grid.this.getSortOrder().indexOf(sortingOrder);
            if (sortIndex > -1 && Grid.this.getSortOrder().size() > 1) {
                // Show sort order indicator if column is
                // sorted and other sorted columns also exists.
                cellElement.setAttribute("sort-order",
                        String.valueOf(sortIndex + 1));
            }

            if (!sortedBefore) {
                verifyColumnWidth(column);
            }
        }

        /**
         * Sort indicator requires a bit more space from the cell than normally.
         * This method check that the now sorted column has enough width.
         * 
         * @param column
         *            sorted column
         */
        private void verifyColumnWidth(Column<?, T> column) {
            int colIndex = getColumns().indexOf(column);
            double minWidth = escalator.getMinCellWidth(colIndex);
            if (column.getWidthActual() < minWidth) {
                // Fix column size
                escalator.getColumnConfiguration().setColumnWidth(colIndex,
                        minWidth);

                fireEvent(new ColumnResizeEvent<T>(column));
            }
        }

        /**
         * Finds the sort order for this column
         */
        private SortOrder getSortOrder(Column<?, ?> column) {
            for (SortOrder order : Grid.this.getSortOrder()) {
                if (order.getColumn() == column) {
                    return order;
                }
            }
            return null;
        }

        private void cleanup(FlyweightCell cell) {
            Element cellElement = cell.getElement();
            cellElement.removeAttribute("sort-order");
            cellElement.removeClassName("sort-desc");
            cellElement.removeClassName("sort-asc");
            cellElement.removeClassName("sortable");
        }

        @Override
        public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
        }

        @Override
        public void postAttach(Row row, Iterable<FlyweightCell> attachedCells) {
            StaticSection.StaticRow<?> gridRow = section.getRow(row.getRow());
            List<Column<?, T>> columns = getVisibleColumns();

            for (FlyweightCell cell : attachedCells) {
                StaticSection.StaticCell metadata = gridRow.getCell(columns
                        .get(cell.getColumn()));
                /*
                 * If the cell contains widgets that are not currently attached
                 * then attach them now.
                 */
                if (GridStaticCellType.WIDGET.equals(metadata.getType())) {
                    final Widget widget = metadata.getWidget();
                    if (widget != null && !widget.isAttached()) {
                        getGrid().attachWidget(metadata.getWidget(),
                                cell.getElement());
                    }
                }
            }
        }

        @Override
        public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
            if (section.getRowCount() > row.getRow()) {
                StaticSection.StaticRow<?> gridRow = section.getRow(row
                        .getRow());
                List<Column<?, T>> columns = getVisibleColumns();
                for (FlyweightCell cell : cellsToDetach) {
                    StaticSection.StaticCell metadata = gridRow.getCell(columns
                            .get(cell.getColumn()));

                    if (GridStaticCellType.WIDGET.equals(metadata.getType())
                            && metadata.getWidget() != null
                            && metadata.getWidget().isAttached()) {

                        getGrid().detachWidget(metadata.getWidget());
                    }
                }
            }
        }

        protected Grid getGrid() {
            return section.grid;
        }

        @Override
        public void postDetach(Row row, Iterable<FlyweightCell> detachedCells) {
        }
    };

    /**
     * Creates a new instance.
     */
    public Grid() {
        initWidget(escalator);
        getElement().setTabIndex(0);
        cellFocusHandler = new CellFocusHandler();

        setStylePrimaryName(STYLE_NAME);

        escalator.getHeader().setEscalatorUpdater(createHeaderUpdater());
        escalator.getBody().setEscalatorUpdater(createBodyUpdater());
        escalator.getFooter().setEscalatorUpdater(createFooterUpdater());

        header.setGrid(this);
        HeaderRow defaultRow = header.appendRow();
        header.setDefaultRow(defaultRow);

        footer.setGrid(this);

        editor.setGrid(this);

        setSelectionMode(SelectionMode.SINGLE);

        escalator.getBody().setSpacerUpdater(gridSpacerUpdater);

        escalator.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                fireEvent(new ScrollEvent());
            }
        });

        escalator
                .addRowVisibilityChangeHandler(new RowVisibilityChangeHandler() {
                    @Override
                    public void onRowVisibilityChange(
                            RowVisibilityChangeEvent event) {
                        if (dataSource != null && dataSource.size() != 0) {
                            dataIsBeingFetched = true;
                            dataSource.ensureAvailability(
                                    event.getFirstVisibleRow(),
                                    event.getVisibleRowCount());
                        }
                    }
                });

        // Default action on SelectionEvents. Refresh the body so changed
        // become visible.
        addSelectionHandler(new SelectionHandler<T>() {

            @Override
            public void onSelect(SelectionEvent<T> event) {
                refreshBody();
            }
        });

        // Sink header events and key events
        sinkEvents(getHeader().getConsumedEvents());
        sinkEvents(Arrays.asList(BrowserEvents.KEYDOWN, BrowserEvents.KEYUP,
                BrowserEvents.KEYPRESS, BrowserEvents.DBLCLICK,
                BrowserEvents.MOUSEDOWN, BrowserEvents.CLICK));

        // Make ENTER and SHIFT+ENTER in the header perform sorting
        addHeaderKeyUpHandler(new HeaderKeyUpHandler() {
            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                if (event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
                    return;
                }
                if (getHeader().getRow(event.getFocusedCell().getRowIndex())
                        .isDefault()) {
                    // Only sort for enter on the default header
                    sorter.sort(event.getFocusedCell().getColumn(),
                            event.isShiftKeyDown());
                }
            }
        });

        addDataAvailableHandler(new DataAvailableHandler() {
            @Override
            public void onDataAvailable(DataAvailableEvent event) {
                dataIsBeingFetched = false;
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == this.enabled) {
            return;
        }

        this.enabled = enabled;
        getElement().setTabIndex(enabled ? 0 : -1);

        // Editor save and cancel buttons need to be disabled.
        boolean editorOpen = editor.getState() != State.INACTIVE;
        if (editorOpen) {
            editor.setGridEnabled(enabled);
        }

        sidebar.setEnabled(enabled);

        getEscalator().setScrollLocked(Direction.VERTICAL,
                !enabled || editorOpen);
        getEscalator().setScrollLocked(Direction.HORIZONTAL, !enabled);
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        escalator.setStylePrimaryName(style);
        editor.setStylePrimaryName(style);
        sidebar.setStylePrimaryName(style + "-sidebar");
        sidebar.addStyleName("v-contextmenu");

        String rowStyle = getStylePrimaryName() + "-row";
        rowHasDataStyleName = rowStyle + "-has-data";
        rowSelectedStyleName = rowStyle + "-selected";
        rowStripeStyleName = rowStyle + "-stripe";

        cellFocusStyleName = getStylePrimaryName() + "-cell-focused";
        rowFocusStyleName = getStylePrimaryName() + "-row-focused";

        if (isAttached()) {
            refreshHeader();
            refreshBody();
            refreshFooter();
        }
    }

    /**
     * Creates the escalator updater used to update the header rows in this
     * grid. The updater is invoked when header rows or columns are added or
     * removed, or the content of existing header cells is changed.
     * 
     * @return the new header updater instance
     * 
     * @see GridHeader
     * @see Grid#getHeader()
     */
    protected EscalatorUpdater createHeaderUpdater() {
        return new StaticSectionUpdater(header, escalator.getHeader());
    }

    /**
     * Creates the escalator updater used to update the body rows in this grid.
     * The updater is invoked when body rows or columns are added or removed,
     * the content of body cells is changed, or the body is scrolled to expose
     * previously hidden content.
     * 
     * @return the new body updater instance
     */
    protected EscalatorUpdater createBodyUpdater() {
        return new BodyUpdater();
    }

    /**
     * Creates the escalator updater used to update the footer rows in this
     * grid. The updater is invoked when header rows or columns are added or
     * removed, or the content of existing header cells is changed.
     * 
     * @return the new footer updater instance
     * 
     * @see GridFooter
     * @see #getFooter()
     */
    protected EscalatorUpdater createFooterUpdater() {
        return new StaticSectionUpdater(footer, escalator.getFooter());
    }

    /**
     * Refreshes header or footer rows on demand
     * 
     * @param rows
     *            The row container
     * @param firstRowIsVisible
     *            is the first row visible
     * @param isHeader
     *            <code>true</code> if we refreshing the header, else assumed
     *            the footer
     */
    private void refreshRowContainer(RowContainer rows, StaticSection<?> section) {

        // Add or Remove rows on demand
        int rowDiff = section.getVisibleRowCount() - rows.getRowCount();
        if (rowDiff > 0) {
            rows.insertRows(0, rowDiff);
        } else if (rowDiff < 0) {
            rows.removeRows(0, -rowDiff);
        }

        // Refresh all the rows
        if (rows.getRowCount() > 0) {
            rows.refreshRows(0, rows.getRowCount());
        }
    }

    /**
     * Focus a body cell by row and column index.
     * 
     * @param rowIndex
     *            index of row to focus
     * @param columnIndex
     *            index of cell to focus
     */
    void focusCell(int rowIndex, int columnIndex) {
        final Range rowRange = Range.between(0, dataSource.size());
        final Range columnRange = Range.between(0, getVisibleColumns().size());

        assert rowRange.contains(rowIndex) : "Illegal row index. Should be in range "
                + rowRange;
        assert columnRange.contains(columnIndex) : "Illegal column index. Should be in range "
                + columnRange;

        if (rowRange.contains(rowIndex) && columnRange.contains(columnIndex)) {
            cellFocusHandler.setCellFocus(rowIndex, columnIndex,
                    escalator.getBody());
            WidgetUtil.focus(getElement());
        }
    }

    /**
     * Refreshes all header rows
     */
    void refreshHeader() {
        refreshRowContainer(escalator.getHeader(), header);
    }

    /**
     * Refreshes all body rows
     */
    private void refreshBody() {
        escalator.getBody().refreshRows(0, escalator.getBody().getRowCount());
    }

    /**
     * Refreshes all footer rows
     */
    void refreshFooter() {
        refreshRowContainer(escalator.getFooter(), footer);
    }

    /**
     * Adds columns as the last columns in the grid.
     * 
     * @param columns
     *            the columns to add
     */
    public void addColumns(Column<?, T>... columns) {
        int count = getColumnCount();
        for (Column<?, T> column : columns) {
            addColumn(column, count++);
        }
    }

    /**
     * Adds a column as the last column in the grid.
     * 
     * @param column
     *            the column to add
     * @return given column
     */
    public <C extends Column<?, T>> C addColumn(C column) {
        addColumn(column, getColumnCount());
        return column;
    }

    /**
     * Inserts a column into a specific position in the grid.
     * 
     * @param index
     *            the index where the column should be inserted into
     * @param column
     *            the column to add
     * @return given column
     * 
     * @throws IllegalStateException
     *             if Grid's current selection model renders a selection column,
     *             and {@code index} is 0.
     */
    public <C extends Column<?, T>> C addColumn(C column, int index) {
        if (column == selectionColumn) {
            throw new IllegalArgumentException("The selection column many "
                    + "not be added manually");
        } else if (selectionColumn != null && index == 0) {
            throw new IllegalStateException("A column cannot be inserted "
                    + "before the selection column");
        }

        addColumnSkipSelectionColumnCheck(column, index);
        return column;
    }

    private void addColumnSkipSelectionColumnCheck(Column<?, T> column,
            int index) {
        // Register column with grid
        columns.add(index, column);

        header.addColumn(column);
        footer.addColumn(column);

        // Register this grid instance with the column
        ((Column<?, T>) column).setGrid(this);

        // Grid knows about hidden columns, Escalator only knows about what is
        // visible so column indexes do not match
        if (!column.isHidden()) {
            int escalatorIndex = index;
            for (int existingColumn = 0; existingColumn < index; existingColumn++) {
                if (getColumn(existingColumn).isHidden()) {
                    escalatorIndex--;
                }
            }
            escalator.getColumnConfiguration().insertColumns(escalatorIndex, 1);
        }

        // Reapply column width
        column.reapplyWidth();

        // Sink all renderer events
        Set<String> events = new HashSet<String>();
        events.addAll(getConsumedEventsForRenderer(column.getRenderer()));

        if (column.isHidable()) {
            columnHider.updateColumnHidable(column);
        }

        sinkEvents(events);
    }

    private void sinkEvents(Collection<String> events) {
        assert events != null;

        int eventsToSink = 0;
        for (String typeName : events) {
            int typeInt = Event.getTypeInt(typeName);
            if (typeInt < 0) {
                // Type not recognized by typeInt
                sinkBitlessEvent(typeName);
            } else {
                eventsToSink |= typeInt;
            }
        }

        if (eventsToSink > 0) {
            sinkEvents(eventsToSink);
        }
    }

    private Renderer<?> findRenderer(FlyweightCell cell) {
        Column<?, T> column = getVisibleColumn(cell.getColumn());
        assert column != null : "Could not find column at index:"
                + cell.getColumn();
        return column.getRenderer();
    }

    /**
     * Removes a column from the grid.
     * 
     * @param column
     *            the column to remove
     */
    public void removeColumn(Column<?, T> column) {
        if (column != null && column.equals(selectionColumn)) {
            throw new IllegalArgumentException(
                    "The selection column may not be removed manually.");
        }

        removeColumnSkipSelectionColumnCheck(column);
    }

    private void removeColumnSkipSelectionColumnCheck(Column<?, T> column) {
        int columnIndex = columns.indexOf(column);

        // Remove from column configuration
        escalator.getColumnConfiguration().removeColumns(
                getVisibleColumns().indexOf(column), 1);

        updateFrozenColumns();

        header.removeColumn(column);
        footer.removeColumn(column);

        // de-register column with grid
        ((Column<?, T>) column).setGrid(null);

        columns.remove(columnIndex);

        if (column.isHidable()) {
            columnHider.removeColumnHidingToggle(column);
        }
    }

    /**
     * Returns the amount of columns in the grid.
     * <p>
     * <em>NOTE:</em> this includes the hidden columns in the count.
     * 
     * @return The number of columns in the grid
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Returns a list columns in the grid, including hidden columns.
     * <p>
     * For currently visible columns, use {@link #getVisibleColumns()}.
     * 
     * @return A unmodifiable list of the columns in the grid
     */
    public List<Column<?, T>> getColumns() {
        return Collections
                .unmodifiableList(new ArrayList<Column<?, T>>(columns));
    }

    /**
     * Returns a list of the currently visible columns in the grid.
     * <p>
     * No {@link Column#isHidden() hidden} columns included.
     * 
     * @since 7.5.0
     * @return A unmodifiable list of the currently visible columns in the grid
     */
    public List<Column<?, T>> getVisibleColumns() {
        ArrayList<Column<?, T>> visible = new ArrayList<Column<?, T>>();
        for (Column<?, T> c : columns) {
            if (!c.isHidden()) {
                visible.add(c);
            }
        }
        return Collections.unmodifiableList(visible);
    }

    /**
     * Returns a column by its index in the grid.
     * <p>
     * <em>NOTE:</em> The indexing includes hidden columns.
     * 
     * @param index
     *            the index of the column
     * @return The column in the given index
     * @throws IllegalArgumentException
     *             if the column index does not exist in the grid
     */
    public Column<?, T> getColumn(int index) throws IllegalArgumentException {
        if (index < 0 || index >= columns.size()) {
            throw new IllegalStateException("Column not found.");
        }
        return columns.get(index);
    }

    private Column<?, T> getVisibleColumn(int index)
            throws IllegalArgumentException {
        List<Column<?, T>> visibleColumns = getVisibleColumns();
        if (index < 0 || index >= visibleColumns.size()) {
            throw new IllegalStateException("Column not found.");
        }
        return visibleColumns.get(index);
    }

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
     * @see #prependHeaderRow()
     * @see #addHeaderRowAt(int)
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow appendHeaderRow() {
        return header.appendRow();
    }

    /**
     * Returns the current default row of the header section. The default row is
     * a special header row providing a user interface for sorting columns.
     * Setting a header caption for column updates cells in the default header.
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
     * <p>
     * Note: Setting the default header row will reset all cell contents to
     * Column defaults.
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
     * Returns the footer section of this grid. The default footer is empty.
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
     * @see #prependFooterRow()
     * @see #addFooterRowAt(int)
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
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

    public Editor<T> getEditor() {
        return editor;
    }

    protected Escalator getEscalator() {
        return escalator;
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
    public void setHeight(String height) {
        escalator.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        escalator.setWidth(width);
    }

    /**
     * Sets the data source used by this grid.
     * 
     * @param dataSource
     *            the data source to use, not null
     * @throws IllegalArgumentException
     *             if <code>dataSource</code> is <code>null</code>
     */
    public void setDataSource(final DataSource<T> dataSource)
            throws IllegalArgumentException {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource can't be null.");
        }

        selectionModel.reset();

        if (this.dataSource != null) {
            this.dataSource.setDataChangeHandler(null);
        }

        this.dataSource = dataSource;
        dataSource.setDataChangeHandler(new DataChangeHandler() {
            @Override
            public void dataUpdated(int firstIndex, int numberOfItems) {
                escalator.getBody().refreshRows(firstIndex, numberOfItems);
            }

            @Override
            public void dataRemoved(int firstIndex, int numberOfItems) {
                escalator.getBody().removeRows(firstIndex, numberOfItems);
                Range removed = Range.withLength(firstIndex, numberOfItems);
                cellFocusHandler.rowsRemovedFromBody(removed);
            }

            @Override
            public void dataAdded(int firstIndex, int numberOfItems) {
                escalator.getBody().insertRows(firstIndex, numberOfItems);
                Range added = Range.withLength(firstIndex, numberOfItems);
                cellFocusHandler.rowsAddedToBody(added);
            }

            @Override
            public void dataAvailable(int firstIndex, int numberOfItems) {
                currentDataAvailable = Range.withLength(firstIndex,
                        numberOfItems);
                fireEvent(new DataAvailableEvent(currentDataAvailable));
            }

            @Override
            public void resetDataAndSize(int newSize) {
                RowContainer body = escalator.getBody();
                int oldSize = body.getRowCount();

                // Hide all details.
                Set<Integer> oldDetails = new HashSet<Integer>(visibleDetails);
                for (int i : oldDetails) {
                    setDetailsVisible(i, false);
                }

                if (newSize > oldSize) {
                    body.insertRows(oldSize, newSize - oldSize);
                    cellFocusHandler.rowsAddedToBody(Range.withLength(oldSize,
                            newSize - oldSize));
                } else if (newSize < oldSize) {
                    body.removeRows(newSize, oldSize - newSize);
                    cellFocusHandler.rowsRemovedFromBody(Range.withLength(
                            newSize, oldSize - newSize));
                }

                if (newSize > 0) {
                    dataIsBeingFetched = true;
                    Range visibleRowRange = escalator.getVisibleRowRange();
                    dataSource.ensureAvailability(visibleRowRange.getStart(),
                            visibleRowRange.length());
                } else {
                    // We won't expect any data more data updates, so just make
                    // the bookkeeping happy
                    dataAvailable(0, 0);
                }

                assert body.getRowCount() == newSize;
            }
        });

        int previousRowCount = escalator.getBody().getRowCount();
        if (previousRowCount != 0) {
            escalator.getBody().removeRows(0, previousRowCount);
        }

        setEscalatorSizeFromDataSource();
    }

    private void setEscalatorSizeFromDataSource() {
        assert escalator.getBody().getRowCount() == 0;

        int size = dataSource.size();
        if (size == -1 && isAttached()) {
            // Exact size is not yet known, start with some reasonable guess
            // just to get an initial backend request going
            size = getEscalator().getMaxVisibleRowCount();
        }
        if (size > 0) {
            escalator.getBody().insertRows(0, size);
        }
    }

    /**
     * Gets the {@Link DataSource} for this Grid.
     * 
     * @return the data source used by this grid
     */
    public DataSource<T> getDataSource() {
        return dataSource;
    }

    /**
     * Sets the number of frozen columns in this grid. Setting the count to 0
     * means that no data columns will be frozen, but the built-in selection
     * checkbox column will still be frozen if it's in use. Setting the count to
     * -1 will also disable the selection column.
     * <p>
     * The default value is 0.
     * 
     * @param numberOfColumns
     *            the number of columns that should be frozen
     * 
     * @throws IllegalArgumentException
     *             if the column count is < -1 or > the number of visible
     *             columns
     */
    public void setFrozenColumnCount(int numberOfColumns) {
        if (numberOfColumns < -1 || numberOfColumns > getColumnCount()) {
            throw new IllegalArgumentException(
                    "count must be between -1 and the current number of columns ("
                            + getColumnCount() + ")");
        }

        frozenColumnCount = numberOfColumns;
        updateFrozenColumns();
    }

    private void updateFrozenColumns() {
        escalator.getColumnConfiguration().setFrozenColumnCount(
                getVisibleFrozenColumnCount());
    }

    private int getVisibleFrozenColumnCount() {
        int numberOfColumns = getFrozenColumnCount();

        // for the escalator the hidden columns are not in the frozen column
        // count, but for grid they are. thus need to convert the index
        for (int i = 0; i < frozenColumnCount; i++) {
            if (getColumn(i).isHidden()) {
                numberOfColumns--;
            }
        }

        if (numberOfColumns == -1) {
            numberOfColumns = 0;
        } else if (selectionColumn != null) {
            numberOfColumns++;
        }
        return numberOfColumns;
    }

    /**
     * Gets the number of frozen columns in this grid. 0 means that no data
     * columns will be frozen, but the built-in selection checkbox column will
     * still be frozen if it's in use. -1 means that not even the selection
     * column is frozen.
     * <p>
     * <em>NOTE:</em> This includes {@link Column#isHidden() hidden columns} in
     * the count.
     * 
     * @return the number of frozen columns
     */
    public int getFrozenColumnCount() {
        return frozenColumnCount;
    }

    public HandlerRegistration addRowVisibilityChangeHandler(
            RowVisibilityChangeHandler handler) {
        /*
         * Reusing Escalator's RowVisibilityChangeHandler, since a scroll
         * concept is too abstract. e.g. the event needs to be re-sent when the
         * widget is resized.
         */
        return escalator.addRowVisibilityChangeHandler(handler);
    }

    /**
     * Scrolls to a certain row, using {@link ScrollDestination#ANY}.
     * <p>
     * If the details for that row are visible, those will be taken into account
     * as well.
     * 
     * @param rowIndex
     *            zero-based index of the row to scroll to.
     * @throws IllegalArgumentException
     *             if rowIndex is below zero, or above the maximum value
     *             supported by the data source.
     */
    public void scrollToRow(int rowIndex) throws IllegalArgumentException {
        scrollToRow(rowIndex, ScrollDestination.ANY,
                GridConstants.DEFAULT_PADDING);
    }

    /**
     * Scrolls to a certain row, using user-specified scroll destination.
     * <p>
     * If the details for that row are visible, those will be taken into account
     * as well.
     * 
     * @param rowIndex
     *            zero-based index of the row to scroll to.
     * @param destination
     *            desired destination placement of scrolled-to-row. See
     *            {@link ScrollDestination} for more information.
     * @throws IllegalArgumentException
     *             if rowIndex is below zero, or above the maximum value
     *             supported by the data source.
     */
    public void scrollToRow(int rowIndex, ScrollDestination destination)
            throws IllegalArgumentException {
        scrollToRow(rowIndex, destination,
                destination == ScrollDestination.MIDDLE ? 0
                        : GridConstants.DEFAULT_PADDING);
    }

    /**
     * Scrolls to a certain row using only user-specified parameters.
     * <p>
     * If the details for that row are visible, those will be taken into account
     * as well.
     * 
     * @param rowIndex
     *            zero-based index of the row to scroll to.
     * @param destination
     *            desired destination placement of scrolled-to-row. See
     *            {@link ScrollDestination} for more information.
     * @param paddingPx
     *            number of pixels to overscroll. Behavior depends on
     *            destination.
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE}
     *             and padding is nonzero, because having a padding on a
     *             centered row is undefined behavior, or if rowIndex is below
     *             zero or above the row count of the data source.
     */
    private void scrollToRow(int rowIndex, ScrollDestination destination,
            int paddingPx) throws IllegalArgumentException {
        int maxsize = escalator.getBody().getRowCount() - 1;

        if (rowIndex < 0) {
            throw new IllegalArgumentException("Row index (" + rowIndex
                    + ") is below zero!");
        }

        if (rowIndex > maxsize) {
            throw new IllegalArgumentException("Row index (" + rowIndex
                    + ") is above maximum (" + maxsize + ")!");
        }

        escalator.scrollToRowAndSpacer(rowIndex, destination, paddingPx);
    }

    /**
     * Scrolls to the beginning of the very first row.
     */
    public void scrollToStart() {
        scrollToRow(0, ScrollDestination.START);
    }

    /**
     * Scrolls to the end of the very last row.
     */
    public void scrollToEnd() {
        scrollToRow(escalator.getBody().getRowCount() - 1,
                ScrollDestination.END);
    }

    /**
     * Sets the vertical scroll offset.
     * 
     * @param px
     *            the number of pixels this grid should be scrolled down
     */
    public void setScrollTop(double px) {
        escalator.setScrollTop(px);
    }

    /**
     * Gets the vertical scroll offset
     * 
     * @return the number of pixels this grid is scrolled down
     */
    public double getScrollTop() {
        return escalator.getScrollTop();
    }

    /**
     * Sets the horizontal scroll offset
     * 
     * @since 7.5.0
     * @param px
     *            the number of pixels this grid should be scrolled right
     */
    public void setScrollLeft(double px) {
        escalator.setScrollLeft(px);
    }

    /**
     * Gets the horizontal scroll offset
     * 
     * @return the number of pixels this grid is scrolled to the right
     */
    public double getScrollLeft() {
        return escalator.getScrollLeft();
    }

    /**
     * Returns the height of the scrollable area in pixels.
     * 
     * @since 7.5.0
     * @return the height of the scrollable area in pixels
     */
    public double getScrollHeight() {
        return escalator.getScrollHeight();
    }

    /**
     * Returns the width of the scrollable area in pixels.
     * 
     * @since 7.5.0
     * @return the width of the scrollable area in pixels.
     */
    public double getScrollWidth() {
        return escalator.getScrollWidth();
    }

    private static final Logger getLogger() {
        return Logger.getLogger(Grid.class.getName());
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
     *            displayed instead.
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInifinite(double)
     *             infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     * 
     * @see #setHeightMode(HeightMode)
     */
    public void setHeightByRows(double rows) throws IllegalArgumentException {
        escalator.setHeightByRows(rows);
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * By default, it is {@value Escalator#DEFAULT_HEIGHT_BY_ROWS}.
     * 
     * @return the amount of rows that should be shown in Grid's body, while in
     *         {@link HeightMode#ROW}.
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return escalator.getHeightByRows();
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via {@link #setHeight(String)}, and behave as a traditional Widget.
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

        escalator.setHeightMode(heightMode);
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     * 
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return escalator.getHeightMode();
    }

    private Set<String> getConsumedEventsForRenderer(Renderer<?> renderer) {
        Set<String> events = new HashSet<String>();
        if (renderer instanceof ComplexRenderer) {
            Collection<String> consumedEvents = ((ComplexRenderer<?>) renderer)
                    .getConsumedEvents();
            if (consumedEvents != null) {
                events.addAll(consumedEvents);
            }
        }
        return events;
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (!isEnabled()) {
            return;
        }

        String eventType = event.getType();

        if (eventType.equals(BrowserEvents.FOCUS)
                || eventType.equals(BrowserEvents.BLUR)) {
            super.onBrowserEvent(event);
            return;
        }

        EventTarget target = event.getEventTarget();

        if (!Element.is(target) || isOrContainsInSpacer(Element.as(target))) {
            return;
        }

        Element e = Element.as(target);
        RowContainer container = escalator.findRowContainer(e);
        Cell cell;

        if (container == null) {
            if (eventType.equals(BrowserEvents.KEYDOWN)
                    || eventType.equals(BrowserEvents.KEYUP)
                    || eventType.equals(BrowserEvents.KEYPRESS)) {
                cell = cellFocusHandler.getFocusedCell();
                container = cellFocusHandler.containerWithFocus;
            } else {
                // Click might be in an editor cell, should still map.
                if (editor.editorOverlay != null
                        && editor.editorOverlay.isOrHasChild(e)) {
                    container = escalator.getBody();
                    int rowIndex = editor.getRow();
                    int colIndex = editor.getElementColumn(e);

                    if (colIndex < 0) {
                        // Click in editor, but not for any column.
                        return;
                    }

                    TableCellElement cellElement = container
                            .getRowElement(rowIndex).getCells()
                            .getItem(colIndex);

                    cell = new Cell(rowIndex, colIndex, cellElement);
                } else {
                    if (escalator.getElement().isOrHasChild(e)) {
                        eventCell.set(new Cell(-1, -1, null), Section.BODY);
                        // Fire native events.
                        super.onBrowserEvent(event);
                    }
                    return;
                }
            }
        } else {
            cell = container.getCell(e);
            if (eventType.equals(BrowserEvents.MOUSEDOWN)) {
                cellOnPrevMouseDown = cell;
            } else if (cell == null && eventType.equals(BrowserEvents.CLICK)) {
                /*
                 * Chrome has an interesting idea on click targets (see
                 * cellOnPrevMouseDown javadoc). Firefox, on the other hand, has
                 * the mousedown target as the click target.
                 */
                cell = cellOnPrevMouseDown;
            }
        }

        assert cell != null : "received " + eventType
                + "-event with a null cell target";
        eventCell.set(cell, getSectionFromContainer(container));

        // Editor can steal focus from Grid and is still handled
        if (isEditorEnabled() && handleEditorEvent(event, container)) {
            return;
        }

        // Fire GridKeyEvents and GridClickEvents. Pass the event to escalator.
        super.onBrowserEvent(event);

        if (!isElementInChildWidget(e)) {

            if (handleHeaderCellDragStartEvent(event, container)) {
                return;
            }

            // Sorting through header Click / KeyUp
            if (handleHeaderDefaultRowEvent(event, container)) {
                return;
            }

            if (handleRendererEvent(event, container)) {
                return;
            }

            if (handleCellFocusEvent(event, container)) {
                return;
            }
        }
    }

    private Section getSectionFromContainer(RowContainer container) {
        assert container != null : "RowContainer should not be null";

        if (container == escalator.getBody()) {
            return Section.BODY;
        } else if (container == escalator.getFooter()) {
            return Section.FOOTER;
        } else if (container == escalator.getHeader()) {
            return Section.HEADER;
        }
        assert false : "RowContainer was not header, footer or body.";
        return null;
    }

    private boolean isOrContainsInSpacer(Node node) {
        Node n = node;
        while (n != null && n != getElement()) {
            boolean isElement = Element.is(n);
            if (isElement) {
                String className = Element.as(n).getClassName();
                if (className.contains(getStylePrimaryName() + "-spacer")) {
                    return true;
                }
            }
            n = n.getParentNode();
        }
        return false;
    }

    private boolean isElementInChildWidget(Element e) {
        Widget w = WidgetUtil.findWidget(e, null);

        if (w == this) {
            return false;
        }

        /*
         * If e is directly inside this grid, but the grid is wrapped in a
         * Composite, findWidget is not going to find this, only the wrapper.
         * Thus we need to check its parents to see if we encounter this; if we
         * don't, the found widget is actually a parent of this, so we should
         * return false.
         */
        while (w != null && w != this) {
            w = w.getParent();
        }
        return w != null;
    }

    private boolean handleEditorEvent(Event event, RowContainer container) {
        Widget w;
        if (editor.focusedColumnIndex < 0) {
            w = null;
        } else {
            w = editor.getWidget(getColumn(editor.focusedColumnIndex));
        }

        EditorDomEvent<T> editorEvent = new EditorDomEvent<T>(event,
                getEventCell(), w);

        return getEditor().getEventHandler().handleEvent(editorEvent);
    }

    private boolean handleRendererEvent(Event event, RowContainer container) {

        if (container == escalator.getBody()) {
            Column<?, T> gridColumn = eventCell.getColumn();
            boolean enterKey = event.getType().equals(BrowserEvents.KEYDOWN)
                    && event.getKeyCode() == KeyCodes.KEY_ENTER;
            boolean doubleClick = event.getType()
                    .equals(BrowserEvents.DBLCLICK);

            if (gridColumn.getRenderer() instanceof ComplexRenderer) {
                ComplexRenderer<?> cplxRenderer = (ComplexRenderer<?>) gridColumn
                        .getRenderer();
                if (cplxRenderer.getConsumedEvents().contains(event.getType())) {
                    if (cplxRenderer.onBrowserEvent(eventCell, event)) {
                        return true;
                    }
                }

                // Calls onActivate if KeyDown and Enter or double click
                if ((enterKey || doubleClick)
                        && cplxRenderer.onActivate(eventCell)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleCellFocusEvent(Event event, RowContainer container) {
        Collection<String> navigation = cellFocusHandler.getNavigationEvents();
        if (navigation.contains(event.getType())) {
            cellFocusHandler.handleNavigationEvent(event, eventCell);
        }
        return false;
    }

    private boolean handleHeaderCellDragStartEvent(Event event,
            RowContainer container) {
        if (!isColumnReorderingAllowed()) {
            return false;
        }
        if (container != escalator.getHeader()) {
            return false;
        }
        if (eventCell.getColumnIndex() < escalator.getColumnConfiguration()
                .getFrozenColumnCount()) {
            return false;
        }

        if (event.getTypeInt() == Event.ONMOUSEDOWN
                && event.getButton() == NativeEvent.BUTTON_LEFT
                || event.getTypeInt() == Event.ONTOUCHSTART) {
            dndHandler.onDragStartOnDraggableElement(event,
                    headerCellDndCallback);
            event.preventDefault();
            event.stopPropagation();
            return true;
        }
        return false;
    }

    private Point rowEventTouchStartingPoint;
    private CellStyleGenerator<T> cellStyleGenerator;
    private RowStyleGenerator<T> rowStyleGenerator;
    private RowReference<T> rowReference = new RowReference<T>(this);
    private CellReference<T> cellReference = new CellReference<T>(rowReference);
    private RendererCellReference rendererCellReference = new RendererCellReference(
            (RowReference<Object>) rowReference);

    private boolean handleHeaderDefaultRowEvent(Event event,
            RowContainer container) {
        if (container != escalator.getHeader()) {
            return false;
        }
        if (!getHeader().getRow(eventCell.getRowIndex()).isDefault()) {
            return false;
        }
        if (!eventCell.getColumn().isSortable()) {
            // Only handle sorting events if the column is sortable
            return false;
        }

        if (BrowserEvents.MOUSEDOWN.equals(event.getType())
                && event.getShiftKey()) {
            // Don't select text when shift clicking on a header.
            event.preventDefault();
        }

        if (BrowserEvents.TOUCHSTART.equals(event.getType())) {
            if (event.getTouches().length() > 1) {
                return false;
            }

            event.preventDefault();

            Touch touch = event.getChangedTouches().get(0);
            rowEventTouchStartingPoint = new Point(touch.getClientX(),
                    touch.getClientY());

            sorter.sortAfterDelay(GridConstants.LONG_TAP_DELAY, true);

            return true;

        } else if (BrowserEvents.TOUCHMOVE.equals(event.getType())) {
            if (event.getTouches().length() > 1) {
                return false;
            }

            event.preventDefault();

            Touch touch = event.getChangedTouches().get(0);
            double diffX = Math.abs(touch.getClientX()
                    - rowEventTouchStartingPoint.getX());
            double diffY = Math.abs(touch.getClientY()
                    - rowEventTouchStartingPoint.getY());

            // Cancel long tap if finger strays too far from
            // starting point
            if (diffX > GridConstants.LONG_TAP_THRESHOLD
                    || diffY > GridConstants.LONG_TAP_THRESHOLD) {
                sorter.cancelDelayedSort();
            }

            return true;

        } else if (BrowserEvents.TOUCHEND.equals(event.getType())) {
            if (event.getTouches().length() > 1) {
                return false;
            }

            if (sorter.isDelayedSortScheduled()) {
                // Not a long tap yet, perform single sort
                sorter.cancelDelayedSort();
                sorter.sort(eventCell.getColumn(), false);
            }

            return true;

        } else if (BrowserEvents.TOUCHCANCEL.equals(event.getType())) {
            if (event.getTouches().length() > 1) {
                return false;
            }

            sorter.cancelDelayedSort();

            return true;

        } else if (BrowserEvents.CLICK.equals(event.getType())) {

            sorter.sort(eventCell.getColumn(), event.getShiftKey());

            // Click events should go onward to cell focus logic
            return false;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {

        /*
         * handles details[] (translated to spacer[] for Escalator), cell[],
         * header[] and footer[]
         */

        // "#header[0][0]/DRAGhANDLE"
        Element escalatorElement = escalator.getSubPartElement(subPart
                .replaceFirst("^details\\[", "spacer["));

        if (escalatorElement != null) {

            int detailIdx = subPart.indexOf("/");
            if (detailIdx > 0) {
                String detail = subPart.substring(detailIdx + 1);
                getLogger().severe(
                        "Looking up detail from index " + detailIdx
                                + " onward: \"" + detail + "\"");
                if (detail.equalsIgnoreCase("content")) {
                    // XXX: Fix this to look up by class name!
                    return DOM.asOld(Element.as(escalatorElement.getChild(0)));
                }
                if (detail.equalsIgnoreCase("draghandle")) {
                    // XXX: Fix this to look up by class name!
                    return DOM.asOld(Element.as(escalatorElement.getChild(1)));
                }
            }

            return DOM.asOld(escalatorElement);
        }

        SubPartArguments args = SubPartArguments.create(subPart);
        Element editor = getSubPartElementEditor(args);
        if (editor != null) {
            return DOM.asOld(editor);
        }

        return null;
    }

    private Element getSubPartElementEditor(SubPartArguments args) {

        if (!args.getType().equalsIgnoreCase("editor")
                || editor.getState() != State.ACTIVE) {
            return null;
        }

        if (args.getIndicesLength() == 0) {
            return editor.editorOverlay;
        } else if (args.getIndicesLength() == 1) {
            int index = args.getIndex(0);
            if (index >= columns.size()) {
                return null;
            }

            escalator.scrollToColumn(index, ScrollDestination.ANY, 0);
            Widget widget = editor.getWidget(columns.get(index));

            if (widget != null) {
                return widget.getElement();
            }

            // No widget for the column.
            return null;
        }

        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {

        String escalatorStructureName = escalator.getSubPartName(subElement);
        if (escalatorStructureName != null) {
            return escalatorStructureName.replaceFirst("^spacer", "details");
        }

        String editorName = getSubPartNameEditor(subElement);
        if (editorName != null) {
            return editorName;
        }

        return null;
    }

    private String getSubPartNameEditor(Element subElement) {

        if (editor.getState() != State.ACTIVE
                || !editor.editorOverlay.isOrHasChild(subElement)) {
            return null;
        }

        int i = 0;
        for (Column<?, T> column : columns) {
            if (editor.getWidget(column).getElement().isOrHasChild(subElement)) {
                return "editor[" + i + "]";
            }
            ++i;
        }

        return "editor";
    }

    private void setSelectColumnRenderer(
            final Renderer<Boolean> selectColumnRenderer) {
        if (this.selectColumnRenderer == selectColumnRenderer) {
            return;
        }

        if (this.selectColumnRenderer != null) {
            if (this.selectColumnRenderer instanceof ComplexRenderer) {
                // End of Life for the old selection column renderer.
                ((ComplexRenderer<?>) this.selectColumnRenderer).destroy();
            }

            // Clear field so frozen column logic in the remove method knows
            // what to do
            Column<?, T> colToRemove = selectionColumn;
            selectionColumn = null;
            removeColumnSkipSelectionColumnCheck(colToRemove);
            cellFocusHandler.offsetRangeBy(-1);
        }

        this.selectColumnRenderer = selectColumnRenderer;

        if (selectColumnRenderer != null) {
            cellFocusHandler.offsetRangeBy(1);
            selectionColumn = new SelectionColumn(selectColumnRenderer);

            addColumnSkipSelectionColumnCheck(selectionColumn, 0);
            selectionColumn.initDone();
        } else {
            selectionColumn = null;
            refreshBody();
        }

        updateFrozenColumns();
    }

    /**
     * Sets the current selection model.
     * <p>
     * This function will call {@link SelectionModel#setGrid(Grid)}.
     * 
     * @param selectionModel
     *            a selection model implementation.
     * @throws IllegalArgumentException
     *             if selection model argument is null
     */
    public void setSelectionModel(SelectionModel<T> selectionModel) {

        if (selectionModel == null) {
            throw new IllegalArgumentException("Selection model can't be null");
        }

        if (this.selectionModel != null) {
            // Detach selection model from Grid.
            this.selectionModel.setGrid(null);
        }

        this.selectionModel = selectionModel;
        selectionModel.setGrid(this);
        setSelectColumnRenderer(this.selectionModel
                .getSelectionColumnRenderer());

        // Refresh rendered rows to update selection, if it has changed
        refreshBody();
    }

    /**
     * Gets a reference to the current selection model.
     * 
     * @return the currently used SelectionModel instance.
     */
    public SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    /**
     * Sets current selection mode.
     * <p>
     * This is a shorthand method for {@link Grid#setSelectionModel}.
     * 
     * @param mode
     *            a selection mode value
     * @see {@link SelectionMode}.
     */
    public void setSelectionMode(SelectionMode mode) {
        SelectionModel<T> model = mode.createModel();
        setSelectionModel(model);
    }

    /**
     * Test if a row is selected.
     * 
     * @param row
     *            a row object
     * @return true, if the current selection model considers the provided row
     *         object selected.
     */
    public boolean isSelected(T row) {
        return selectionModel.isSelected(row);
    }

    /**
     * Select a row using the current selection model.
     * <p>
     * Only selection models implementing {@link SelectionModel.Single} and
     * {@link SelectionModel.Multi} are supported; for anything else, an
     * exception will be thrown.
     * 
     * @param row
     *            a row object
     * @return <code>true</code> iff the current selection changed
     * @throws IllegalStateException
     *             if the current selection model is not an instance of
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    public boolean select(T row) {
        if (selectionModel instanceof SelectionModel.Single<?>) {
            return ((SelectionModel.Single<T>) selectionModel).select(row);
        } else if (selectionModel instanceof SelectionModel.Multi<?>) {
            return ((SelectionModel.Multi<T>) selectionModel)
                    .select(Collections.singleton(row));
        } else {
            throw new IllegalStateException("Unsupported selection model");
        }
    }

    /**
     * Deselect a row using the current selection model.
     * <p>
     * Only selection models implementing {@link SelectionModel.Single} and
     * {@link SelectionModel.Multi} are supported; for anything else, an
     * exception will be thrown.
     * 
     * @param row
     *            a row object
     * @return <code>true</code> iff the current selection changed
     * @throws IllegalStateException
     *             if the current selection model is not an instance of
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    public boolean deselect(T row) {
        if (selectionModel instanceof SelectionModel.Single<?>) {
            return ((SelectionModel.Single<T>) selectionModel).deselect(row);
        } else if (selectionModel instanceof SelectionModel.Multi<?>) {
            return ((SelectionModel.Multi<T>) selectionModel)
                    .deselect(Collections.singleton(row));
        } else {
            throw new IllegalStateException("Unsupported selection model");
        }
    }

    /**
     * Deselect all rows using the current selection model.
     * 
     * @param row
     *            a row object
     * @return <code>true</code> iff the current selection changed
     * @throws IllegalStateException
     *             if the current selection model is not an instance of
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    public boolean deselectAll() {
        if (selectionModel instanceof SelectionModel.Single<?>) {
            Single<T> single = ((SelectionModel.Single<T>) selectionModel);
            if (single.getSelectedRow() != null) {
                return single.deselect(single.getSelectedRow());
            } else {
                return false;
            }
        } else if (selectionModel instanceof SelectionModel.Multi<?>) {
            return ((SelectionModel.Multi<T>) selectionModel).deselectAll();
        } else {
            throw new IllegalStateException("Unsupported selection model");
        }
    }

    /**
     * Gets last selected row from the current SelectionModel.
     * <p>
     * Only selection models implementing {@link SelectionModel.Single} are
     * valid for this method; for anything else, use the
     * {@link Grid#getSelectedRows()} method.
     * 
     * @return a selected row reference, or null, if no row is selected
     * @throws IllegalStateException
     *             if the current selection model is not an instance of
     *             {@link SelectionModel.Single}
     */
    public T getSelectedRow() {
        if (selectionModel instanceof SelectionModel.Single<?>) {
            return ((SelectionModel.Single<T>) selectionModel).getSelectedRow();
        } else {
            throw new IllegalStateException(
                    "Unsupported selection model; can not get single selected row");
        }
    }

    /**
     * Gets currently selected rows from the current selection model.
     * 
     * @return a non-null collection containing all currently selected rows.
     */
    public Collection<T> getSelectedRows() {
        return selectionModel.getSelectedRows();
    }

    @Override
    public HandlerRegistration addSelectionHandler(
            final SelectionHandler<T> handler) {
        return addHandler(handler, SelectionEvent.getType());
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
     * Sorts the Grid data in ascending order along one column.
     * 
     * @param column
     *            a grid column reference
     */
    public <C> void sort(Column<C, T> column) {
        sort(column, SortDirection.ASCENDING);
    }

    /**
     * Sorts the Grid data along one column.
     * 
     * @param column
     *            a grid column reference
     * @param direction
     *            a sort direction value
     */
    public <C> void sort(Column<C, T> column, SortDirection direction) {
        sort(Sort.by(column, direction));
    }

    /**
     * Sets the sort order to use. Setting this causes the Grid to re-sort
     * itself.
     * 
     * @param order
     *            a sort order list. If set to null, the sort order is cleared.
     */
    public void setSortOrder(List<SortOrder> order) {
        setSortOrder(order, false);
    }

    /**
     * Clears the sort order and indicators without re-sorting.
     */
    private void clearSortOrder() {
        sortOrder.clear();
        refreshHeader();
    }

    private void setSortOrder(List<SortOrder> order, boolean userOriginated) {
        if (order != sortOrder) {
            sortOrder.clear();
            if (order != null) {
                sortOrder.addAll(order);
            }
        }
        sort(userOriginated);
    }

    /**
     * Get a copy of the current sort order array.
     * 
     * @return a copy of the current sort order array
     */
    public List<SortOrder> getSortOrder() {
        return Collections.unmodifiableList(sortOrder);
    }

    /**
     * Finds the sorting order for this column
     */
    private SortOrder getSortOrder(Column<?, ?> column) {
        for (SortOrder order : getSortOrder()) {
            if (order.getColumn() == column) {
                return order;
            }
        }
        return null;
    }

    /**
     * Register a GWT event handler for a sorting event. This handler gets
     * called whenever this Grid needs its data source to provide data sorted in
     * a specific order.
     * 
     * @param handler
     *            a sort event handler
     * @return the registration for the event
     */
    public HandlerRegistration addSortHandler(SortHandler<T> handler) {
        return addHandler(handler, SortEvent.getType());
    }

    /**
     * Register a GWT event handler for a select all event. This handler gets
     * called whenever Grid needs all rows selected.
     * 
     * @param handler
     *            a select all event handler
     */
    public HandlerRegistration addSelectAllHandler(SelectAllHandler<T> handler) {
        return addHandler(handler, SelectAllEvent.getType());
    }

    /**
     * Register a GWT event handler for a data available event. This handler
     * gets called whenever the {@link DataSource} for this Grid has new data
     * available.
     * <p>
     * This handle will be fired with the current available data after
     * registration is done.
     * 
     * @param handler
     *            a data available event handler
     * @return the registartion for the event
     */
    public HandlerRegistration addDataAvailableHandler(
            final DataAvailableHandler handler) {
        // Deferred call to handler with current row range
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                if (!dataIsBeingFetched) {
                    handler.onDataAvailable(new DataAvailableEvent(
                            currentDataAvailable));
                }
            }
        });
        return addHandler(handler, DataAvailableEvent.TYPE);
    }

    /**
     * Register a BodyKeyDownHandler to this Grid. The event for this handler is
     * fired when a KeyDown event occurs while cell focus is in the Body of this
     * Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addBodyKeyDownHandler(BodyKeyDownHandler handler) {
        return addHandler(handler, keyDown.getAssociatedType());
    }

    /**
     * Register a BodyKeyUpHandler to this Grid. The event for this handler is
     * fired when a KeyUp event occurs while cell focus is in the Body of this
     * Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addBodyKeyUpHandler(BodyKeyUpHandler handler) {
        return addHandler(handler, keyUp.getAssociatedType());
    }

    /**
     * Register a BodyKeyPressHandler to this Grid. The event for this handler
     * is fired when a KeyPress event occurs while cell focus is in the Body of
     * this Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addBodyKeyPressHandler(
            BodyKeyPressHandler handler) {
        return addHandler(handler, keyPress.getAssociatedType());
    }

    /**
     * Register a HeaderKeyDownHandler to this Grid. The event for this handler
     * is fired when a KeyDown event occurs while cell focus is in the Header of
     * this Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addHeaderKeyDownHandler(
            HeaderKeyDownHandler handler) {
        return addHandler(handler, keyDown.getAssociatedType());
    }

    /**
     * Register a HeaderKeyUpHandler to this Grid. The event for this handler is
     * fired when a KeyUp event occurs while cell focus is in the Header of this
     * Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addHeaderKeyUpHandler(HeaderKeyUpHandler handler) {
        return addHandler(handler, keyUp.getAssociatedType());
    }

    /**
     * Register a HeaderKeyPressHandler to this Grid. The event for this handler
     * is fired when a KeyPress event occurs while cell focus is in the Header
     * of this Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addHeaderKeyPressHandler(
            HeaderKeyPressHandler handler) {
        return addHandler(handler, keyPress.getAssociatedType());
    }

    /**
     * Register a FooterKeyDownHandler to this Grid. The event for this handler
     * is fired when a KeyDown event occurs while cell focus is in the Footer of
     * this Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addFooterKeyDownHandler(
            FooterKeyDownHandler handler) {
        return addHandler(handler, keyDown.getAssociatedType());
    }

    /**
     * Register a FooterKeyUpHandler to this Grid. The event for this handler is
     * fired when a KeyUp event occurs while cell focus is in the Footer of this
     * Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addFooterKeyUpHandler(FooterKeyUpHandler handler) {
        return addHandler(handler, keyUp.getAssociatedType());
    }

    /**
     * Register a FooterKeyPressHandler to this Grid. The event for this handler
     * is fired when a KeyPress event occurs while cell focus is in the Footer
     * of this Grid.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addFooterKeyPressHandler(
            FooterKeyPressHandler handler) {
        return addHandler(handler, keyPress.getAssociatedType());
    }

    /**
     * Register a BodyClickHandler to this Grid. The event for this handler is
     * fired when a Click event occurs in the Body of this Grid.
     * 
     * @param handler
     *            the click handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addBodyClickHandler(BodyClickHandler handler) {
        return addHandler(handler, clickEvent.getAssociatedType());
    }

    /**
     * Register a HeaderClickHandler to this Grid. The event for this handler is
     * fired when a Click event occurs in the Header of this Grid.
     * 
     * @param handler
     *            the click handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addHeaderClickHandler(HeaderClickHandler handler) {
        return addHandler(handler, clickEvent.getAssociatedType());
    }

    /**
     * Register a FooterClickHandler to this Grid. The event for this handler is
     * fired when a Click event occurs in the Footer of this Grid.
     * 
     * @param handler
     *            the click handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addFooterClickHandler(FooterClickHandler handler) {
        return addHandler(handler, clickEvent.getAssociatedType());
    }

    /**
     * Register a BodyDoubleClickHandler to this Grid. The event for this
     * handler is fired when a double click event occurs in the Body of this
     * Grid.
     * 
     * @param handler
     *            the double click handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addBodyDoubleClickHandler(
            BodyDoubleClickHandler handler) {
        return addHandler(handler, doubleClickEvent.getAssociatedType());
    }

    /**
     * Register a HeaderDoubleClickHandler to this Grid. The event for this
     * handler is fired when a double click event occurs in the Header of this
     * Grid.
     * 
     * @param handler
     *            the double click handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addHeaderDoubleClickHandler(
            HeaderDoubleClickHandler handler) {
        return addHandler(handler, doubleClickEvent.getAssociatedType());
    }

    /**
     * Register a FooterDoubleClickHandler to this Grid. The event for this
     * handler is fired when a double click event occurs in the Footer of this
     * Grid.
     * 
     * @param handler
     *            the double click handler to register
     * @return the registration for the event
     */
    public HandlerRegistration addFooterDoubleClickHandler(
            FooterDoubleClickHandler handler) {
        return addHandler(handler, doubleClickEvent.getAssociatedType());
    }

    /**
     * Register a column reorder handler to this Grid. The event for this
     * handler is fired when the Grid's columns are reordered.
     * 
     * @since 7.5.0
     * @param handler
     *            the handler for the event
     * @return the registration for the event
     */
    public HandlerRegistration addColumnReorderHandler(
            ColumnReorderHandler<T> handler) {
        return addHandler(handler, ColumnReorderEvent.getType());
    }

    /**
     * Register a column visibility change handler to this Grid. The event for
     * this handler is fired when the Grid's columns change visibility.
     * 
     * @since 7.5.0
     * @param handler
     *            the handler for the event
     * @return the registration for the event
     */
    public HandlerRegistration addColumnVisibilityChangeHandler(
            ColumnVisibilityChangeHandler<T> handler) {
        return addHandler(handler, ColumnVisibilityChangeEvent.getType());
    }

    /**
     * Register a column resize handler to this Grid. The event for this handler
     * is fired when the Grid's columns are resized.
     * 
     * @since 7.6
     * @param handler
     *            the handler for the event
     * @return the registration for the event
     */
    public HandlerRegistration addColumnResizeHandler(
            ColumnResizeHandler<T> handler) {
        return addHandler(handler, ColumnResizeEvent.getType());
    }

    /**
     * Apply sorting to data source.
     */
    private void sort(boolean userOriginated) {
        refreshHeader();
        fireEvent(new SortEvent<T>(this,
                Collections.unmodifiableList(sortOrder), userOriginated));
    }

    private int getLastVisibleRowIndex() {
        int lastRowIndex = escalator.getVisibleRowRange().getEnd();
        int footerTop = escalator.getFooter().getElement().getAbsoluteTop();
        Element lastRow;

        do {
            lastRow = escalator.getBody().getRowElement(--lastRowIndex);
        } while (lastRow.getAbsoluteTop() > footerTop);

        return lastRowIndex;
    }

    private int getFirstVisibleRowIndex() {
        int firstRowIndex = escalator.getVisibleRowRange().getStart();
        int headerBottom = escalator.getHeader().getElement()
                .getAbsoluteBottom();
        Element firstRow = escalator.getBody().getRowElement(firstRowIndex);

        while (firstRow.getAbsoluteBottom() < headerBottom) {
            firstRow = escalator.getBody().getRowElement(++firstRowIndex);
        }

        return firstRowIndex;
    }

    /**
     * Adds a scroll handler to this grid
     * 
     * @param handler
     *            the scroll handler to add
     * @return a handler registration for the registered scroll handler
     */
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return addHandler(handler, ScrollEvent.TYPE);
    }

    @Override
    public boolean isWorkPending() {
        return escalator.isWorkPending() || dataIsBeingFetched
                || autoColumnWidthsRecalculator.isScheduled()
                || editor.isWorkPending();
    }

    /**
     * Returns whether columns can be reordered with drag and drop.
     * 
     * @since 7.5.0
     * @return <code>true</code> if columns can be reordered, false otherwise
     */
    public boolean isColumnReorderingAllowed() {
        return columnReorderingAllowed;
    }

    /**
     * Sets whether column reordering with drag and drop is allowed or not.
     * 
     * @since 7.5.0
     * @param columnReorderingAllowed
     *            specifies whether column reordering is allowed
     */
    public void setColumnReorderingAllowed(boolean columnReorderingAllowed) {
        this.columnReorderingAllowed = columnReorderingAllowed;
    }

    /**
     * Sets a new column order for the grid. All columns which are not ordered
     * here will remain in the order they were before as the last columns of
     * grid.
     * 
     * @param orderedColumns
     *            array of columns in wanted order
     */
    public void setColumnOrder(Column<?, T>... orderedColumns) {
        ColumnConfiguration conf = getEscalator().getColumnConfiguration();

        // Trigger ComplexRenderer.destroy for old content
        conf.removeColumns(0, conf.getColumnCount());

        List<Column<?, T>> newOrder = new ArrayList<Column<?, T>>();
        if (selectionColumn != null) {
            newOrder.add(selectionColumn);
        }

        int i = 0;
        for (Column<?, T> column : orderedColumns) {
            if (columns.contains(column)) {
                newOrder.add(column);
                ++i;
            } else {
                throw new IllegalArgumentException("Given column at index " + i
                        + " does not exist in Grid");
            }
        }

        if (columns.size() != newOrder.size()) {
            columns.removeAll(newOrder);
            newOrder.addAll(columns);
        }
        columns = newOrder;

        List<Column<?, T>> visibleColumns = getVisibleColumns();

        // Do ComplexRenderer.init and render new content
        conf.insertColumns(0, visibleColumns.size());

        // Number of frozen columns should be kept same #16901
        updateFrozenColumns();

        // Update column widths.
        for (Column<?, T> column : columns) {
            column.reapplyWidth();
        }

        // Recalculate all the colspans
        for (HeaderRow row : header.getRows()) {
            row.calculateColspans();
        }
        for (FooterRow row : footer.getRows()) {
            row.calculateColspans();
        }

        columnHider.updateTogglesOrder();

        fireEvent(new ColumnReorderEvent<T>());
    }

    /**
     * Sets the style generator that is used for generating styles for cells
     * 
     * @param cellStyleGenerator
     *            the cell style generator to set, or <code>null</code> to
     *            remove a previously set generator
     */
    public void setCellStyleGenerator(CellStyleGenerator<T> cellStyleGenerator) {
        this.cellStyleGenerator = cellStyleGenerator;
        refreshBody();
    }

    /**
     * Gets the style generator that is used for generating styles for cells
     * 
     * @return the cell style generator, or <code>null</code> if no generator is
     *         set
     */
    public CellStyleGenerator<T> getCellStyleGenerator() {
        return cellStyleGenerator;
    }

    /**
     * Sets the style generator that is used for generating styles for rows
     * 
     * @param rowStyleGenerator
     *            the row style generator to set, or <code>null</code> to remove
     *            a previously set generator
     */
    public void setRowStyleGenerator(RowStyleGenerator<T> rowStyleGenerator) {
        this.rowStyleGenerator = rowStyleGenerator;
        refreshBody();
    }

    /**
     * Gets the style generator that is used for generating styles for rows
     * 
     * @return the row style generator, or <code>null</code> if no generator is
     *         set
     */
    public RowStyleGenerator<T> getRowStyleGenerator() {
        return rowStyleGenerator;
    }

    private static void setCustomStyleName(Element element, String styleName) {
        assert element != null;

        String oldStyleName = element
                .getPropertyString(CUSTOM_STYLE_PROPERTY_NAME);

        if (!SharedUtil.equals(oldStyleName, styleName)) {
            if (oldStyleName != null && !oldStyleName.isEmpty()) {
                element.removeClassName(oldStyleName);
            }
            if (styleName != null && !styleName.isEmpty()) {
                element.addClassName(styleName);
            }
            element.setPropertyString(CUSTOM_STYLE_PROPERTY_NAME, styleName);
        }

    }

    /**
     * Opens the editor over the row with the given index.
     * 
     * @param rowIndex
     *            the index of the row to be edited
     * 
     * @throws IllegalStateException
     *             if the editor is not enabled
     * @throws IllegalStateException
     *             if the editor is already in edit mode
     */
    public void editRow(int rowIndex) {
        editor.editRow(rowIndex);
    }

    /**
     * Returns whether the editor is currently open on some row.
     * 
     * @return {@code true} if the editor is active, {@code false} otherwise.
     */
    public boolean isEditorActive() {
        return editor.getState() != State.INACTIVE;
    }

    /**
     * Saves any unsaved changes in the editor to the data source.
     * 
     * @throws IllegalStateException
     *             if the editor is not enabled
     * @throws IllegalStateException
     *             if the editor is not in edit mode
     */
    public void saveEditor() {
        editor.save();
    }

    /**
     * Cancels the currently active edit and hides the editor. Any changes that
     * are not {@link #saveEditor() saved} are lost.
     * 
     * @throws IllegalStateException
     *             if the editor is not enabled
     * @throws IllegalStateException
     *             if the editor is not in edit mode
     */
    public void cancelEditor() {
        editor.cancel();
    }

    /**
     * Returns the handler responsible for binding data and editor widgets to
     * the editor.
     * 
     * @return the editor handler or null if not set
     */
    public EditorHandler<T> getEditorHandler() {
        return editor.getHandler();
    }

    /**
     * Sets the handler responsible for binding data and editor widgets to the
     * editor.
     * 
     * @param rowHandler
     *            the new editor handler
     * 
     * @throws IllegalStateException
     *             if the editor is currently in edit mode
     */
    public void setEditorHandler(EditorHandler<T> handler) {
        editor.setHandler(handler);
    }

    /**
     * Returns the enabled state of the editor.
     * 
     * @return true if editing is enabled, false otherwise
     */
    public boolean isEditorEnabled() {
        return editor.isEnabled();
    }

    /**
     * Sets the enabled state of the editor.
     * 
     * @param enabled
     *            true to enable editing, false to disable
     * 
     * @throws IllegalStateException
     *             if in edit mode and trying to disable
     * @throws IllegalStateException
     *             if the editor handler is not set
     */
    public void setEditorEnabled(boolean enabled) {
        editor.setEnabled(enabled);
    }

    /**
     * Returns the editor widget associated with the given column. If the editor
     * is not active, returns null.
     * 
     * @param column
     *            the column
     * @return the widget if the editor is open, null otherwise
     */
    public Widget getEditorWidget(Column<?, T> column) {
        return editor.getWidget(column);
    }

    /**
     * Sets the caption on the save button in the Grid editor.
     * 
     * @param saveCaption
     *            the caption to set
     * @throws IllegalArgumentException
     *             if {@code saveCaption} is {@code null}
     */
    public void setEditorSaveCaption(String saveCaption)
            throws IllegalArgumentException {
        editor.setSaveCaption(saveCaption);
    }

    /**
     * Gets the current caption on the save button in the Grid editor.
     * 
     * @return the current caption on the save button
     */
    public String getEditorSaveCaption() {
        return editor.getSaveCaption();
    }

    /**
     * Sets the caption on the cancel button in the Grid editor.
     * 
     * @param cancelCaption
     *            the caption to set
     * @throws IllegalArgumentException
     *             if {@code cancelCaption} is {@code null}
     */
    public void setEditorCancelCaption(String cancelCaption)
            throws IllegalArgumentException {
        editor.setCancelCaption(cancelCaption);
    }

    /**
     * Gets the caption on the cancel button in the Grid editor.
     * 
     * @return the current caption on the cancel button
     */
    public String getEditorCancelCaption() {
        return editor.getCancelCaption();
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (getEscalator().getBody().getRowCount() == 0 && dataSource != null) {
            setEscalatorSizeFromDataSource();
        }

        // Grid was just attached to DOM. Column widths should be calculated.
        recalculateColumnWidths();
    }

    @Override
    protected void onDetach() {
        Set<Integer> details = new HashSet<Integer>(visibleDetails);
        for (int row : details) {
            setDetailsVisible(row, false);
        }

        super.onDetach();
    }

    @Override
    public void onResize() {
        super.onResize();

        /*
         * Delay calculation to be deferred so Escalator can do it's magic.
         */
        Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
                if (escalator.getInnerWidth() != autoColumnWidthsRecalculator.lastCalculatedInnerWidth) {
                    recalculateColumnWidths();
                }

                // Vertical resizing could make editor positioning invalid so it
                // needs to be recalculated on resize
                if (isEditorActive()) {
                    editor.updateVerticalScrollPosition();
                }
            }
        });
    }

    /**
     * Grid does not support adding Widgets this way.
     * <p>
     * This method is implemented only because removing widgets from Grid (added
     * via e.g. {@link Renderer}s) requires the {@link HasWidgets} interface.
     * 
     * @param w
     *            irrelevant
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    @Deprecated
    public void add(Widget w) {
        throw new UnsupportedOperationException(
                "Cannot add widgets to Grid with this method");
    }

    /**
     * Grid does not support clearing Widgets this way.
     * <p>
     * This method is implemented only because removing widgets from Grid (added
     * via e.g. {@link Renderer}s) requires the {@link HasWidgets} interface.
     * 
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException(
                "Cannot clear widgets from Grid this way");
    }

    /**
     * Grid does not support iterating through Widgets this way.
     * <p>
     * This method is implemented only because removing widgets from Grid (added
     * via e.g. {@link Renderer}s) requires the {@link HasWidgets} interface.
     * 
     * @return never
     * @throws UnsupportedOperationException
     *             always
     */
    @Override
    @Deprecated
    public Iterator<Widget> iterator() {
        throw new UnsupportedOperationException(
                "Cannot iterate through widgets in Grid this way");
    }

    /**
     * Grid does not support removing Widgets this way.
     * <p>
     * This method is implemented only because removing widgets from Grid (added
     * via e.g. {@link Renderer}s) requires the {@link HasWidgets} interface.
     * 
     * @return always <code>false</code>
     */
    @Override
    @Deprecated
    public boolean remove(Widget w) {
        /*
         * This is the method that is the sole reason to have Grid implement
         * HasWidget - when Vaadin removes a Component from the hierarchy, the
         * corresponding Widget will call removeFromParent() on itself. GWT will
         * check there that its parent (i.e. Grid) implements HasWidgets, and
         * will call this remove(Widget) method.
         * 
         * tl;dr: all this song and dance to make sure GWT's sanity checks
         * aren't triggered, even though they effectively do nothing interesting
         * from Grid's perspective.
         */
        return false;
    }

    /**
     * Accesses the package private method Widget#setParent()
     * 
     * @param widget
     *            The widget to access
     * @param parent
     *            The parent to set
     */
    private static native final void setParent(Widget widget, Grid<?> parent)
    /*-{
        widget.@com.google.gwt.user.client.ui.Widget::setParent(Lcom/google/gwt/user/client/ui/Widget;)(parent);
    }-*/;

    private static native final void onAttach(Widget widget)
    /*-{
        widget.@Widget::onAttach()();
    }-*/;

    private static native final void onDetach(Widget widget)
    /*-{
        widget.@Widget::onDetach()();
    }-*/;

    @Override
    protected void doAttachChildren() {
        if (sidebar.getParent() == this) {
            onAttach(sidebar);
        }
    }

    @Override
    protected void doDetachChildren() {
        if (sidebar.getParent() == this) {
            onDetach(sidebar);
        }
    }

    private void attachWidget(Widget w, Element parent) {
        assert w.getParent() == null;

        parent.appendChild(w.getElement());
        setParent(w, this);
    }

    private void detachWidget(Widget w) {
        assert w.getParent() == this;

        setParent(w, null);
        w.getElement().removeFromParent();
    }

    /**
     * Resets all cached pixel sizes and reads new values from the DOM. This
     * methods should be used e.g. when styles affecting the dimensions of
     * elements in this grid have been changed.
     */
    public void resetSizesFromDom() {
        getEscalator().resetSizesFromDom();
    }

    /**
     * Sets a new details generator for row details.
     * <p>
     * The currently opened row details will be re-rendered.
     * 
     * @since 7.5.0
     * @param detailsGenerator
     *            the details generator to set
     * @throws IllegalArgumentException
     *             if detailsGenerator is <code>null</code>;
     */
    public void setDetailsGenerator(DetailsGenerator detailsGenerator)
            throws IllegalArgumentException {

        if (detailsGenerator == null) {
            throw new IllegalArgumentException(
                    "Details generator may not be null");
        }

        for (Integer index : visibleDetails) {
            setDetailsVisible(index, false);
        }

        this.detailsGenerator = detailsGenerator;

        // this will refresh all visible spacers
        escalator.getBody().setSpacerUpdater(gridSpacerUpdater);
    }

    /**
     * Gets the current details generator for row details.
     * 
     * @since 7.5.0
     * @return the detailsGenerator the current details generator
     */
    public DetailsGenerator getDetailsGenerator() {
        return detailsGenerator;
    }

    /**
     * Shows or hides the details for a specific row.
     * <p>
     * This method does nothing if trying to set show already-visible details,
     * or hide already-hidden details.
     * 
     * @since 7.5.0
     * @param rowIndex
     *            the index of the affected row
     * @param visible
     *            <code>true</code> to show the details, or <code>false</code>
     *            to hide them
     * @see #isDetailsVisible(int)
     */
    public void setDetailsVisible(int rowIndex, boolean visible) {
        if (DetailsGenerator.NULL.equals(detailsGenerator)) {
            return;
        }

        Integer rowIndexInteger = Integer.valueOf(rowIndex);

        /*
         * We want to prevent opening a details row twice, so any subsequent
         * openings (or closings) of details is a NOOP.
         * 
         * When a details row is opened, it is given an arbitrary height
         * (because Escalator requires a height upon opening). Only when it's
         * opened, Escalator will ask the generator to generate a widget, which
         * we then can measure. When measured, we correct the initial height by
         * the original height.
         * 
         * Without this check, we would override the measured height, and revert
         * back to the initial, arbitrary, height which would most probably be
         * wrong.
         * 
         * see GridSpacerUpdater.init for implementation details.
         */

        boolean isVisible = isDetailsVisible(rowIndex);
        if (visible && !isVisible) {
            escalator.getBody().setSpacer(rowIndex, DETAILS_ROW_INITIAL_HEIGHT);
            visibleDetails.add(rowIndexInteger);
        }

        else if (!visible && isVisible) {
            escalator.getBody().setSpacer(rowIndex, -1);
            visibleDetails.remove(rowIndexInteger);
        }
    }

    /**
     * Check whether the details for a row is visible or not.
     * 
     * @since 7.5.0
     * @param rowIndex
     *            the index of the row for which to check details
     * @return <code>true</code> iff the details for the given row is visible
     * @see #setDetailsVisible(int, boolean)
     */
    public boolean isDetailsVisible(int rowIndex) {
        return visibleDetails.contains(Integer.valueOf(rowIndex));
    }

    /**
     * Requests that the column widths should be recalculated.
     * <p>
     * The actual recalculation is not necessarily done immediately so you
     * cannot rely on the columns being the correct width after the call
     * returns.
     * 
     * @since 7.4.1
     */
    public void recalculateColumnWidths() {
        autoColumnWidthsRecalculator.schedule();
    }

    /**
     * Gets the customizable menu bar that is by default used for toggling
     * column hidability. The application developer is allowed to add their
     * custom items to the end of the menu, but should try to avoid modifying
     * the items in the beginning of the menu that control the column hiding if
     * any columns are marked as hidable. A toggle for opening the menu will be
     * displayed whenever the menu contains at least one item.
     * 
     * @since 7.5.0
     * @return the menu bar
     */
    public MenuBar getSidebarMenu() {
        return sidebar.menuBar;
    }

    /**
     * Tests whether the sidebar menu is currently open.
     * 
     * @since 7.5.0
     * @see #getSidebarMenu()
     * @return <code>true</code> if the sidebar is open; <code>false</code> if
     *         it is closed
     */
    public boolean isSidebarOpen() {
        return sidebar.isOpen();
    }

    /**
     * Sets whether the sidebar menu is open.
     * 
     * 
     * @since 7.5.0
     * @see #getSidebarMenu()
     * @see #isSidebarOpen()
     * @param sidebarOpen
     *            <code>true</code> to open the sidebar; <code>false</code> to
     *            close it
     */
    public void setSidebarOpen(boolean sidebarOpen) {
        if (sidebarOpen) {
            sidebar.open();
        } else {
            sidebar.close();
        }
    }

    @Override
    public int getTabIndex() {
        return FocusUtil.getTabIndex(this);
    }

    @Override
    public void setAccessKey(char key) {
        FocusUtil.setAccessKey(this, key);
    }

    @Override
    public void setFocus(boolean focused) {
        FocusUtil.setFocus(this, focused);
    }

    @Override
    public void setTabIndex(int index) {
        FocusUtil.setTabIndex(this, index);
    }

    @Override
    public void focus() {
        setFocus(true);
    }

    /**
     * Sets the buffered editor mode.
     * 
     * @since 7.6
     * @param editorUnbuffered
     *            <code>true</code> to enable buffered editor,
     *            <code>false</code> to disable it
     */
    public void setEditorBuffered(boolean editorBuffered) {
        editor.setBuffered(editorBuffered);
    }

    /**
     * Gets the buffered editor mode.
     * 
     * @since 7.6
     * @return <code>true</code> if buffered editor is enabled,
     *         <code>false</code> otherwise
     */
    public boolean isEditorBuffered() {
        return editor.isBuffered();
    }

    /**
     * Returns the {@link EventCellReference} for the latest event fired from
     * this Grid.
     * <p>
     * Note: This cell reference will be updated when firing the next event.
     * 
     * @since 7.5
     * @return event cell reference
     */
    public EventCellReference<T> getEventCell() {
        return eventCell;
    }

    /**
     * Returns a CellReference for the cell to which the given element belongs
     * to.
     * 
     * @since 7.6
     * @param element
     *            Element to find from the cell's content.
     * @return CellReference or <code>null</code> if cell was not found.
     */
    public CellReference<T> getCellReference(Element element) {
        RowContainer container = getEscalator().findRowContainer(element);
        if (container != null) {
            Cell cell = container.getCell(element);
            if (cell != null) {
                EventCellReference<T> cellRef = new EventCellReference<T>(this);
                cellRef.set(cell, getSectionFromContainer(container));
                return cellRef;
            }
        }
        return null;
    }
}
