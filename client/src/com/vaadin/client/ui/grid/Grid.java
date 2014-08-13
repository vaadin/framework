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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Util;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.grid.GridFooter.FooterRow;
import com.vaadin.client.ui.grid.GridHeader.HeaderRow;
import com.vaadin.client.ui.grid.GridStaticSection.StaticCell;
import com.vaadin.client.ui.grid.keyevents.AbstractGridKeyEventHandler;
import com.vaadin.client.ui.grid.keyevents.AbstractGridKeyEventHandler.GridKeyDownHandler;
import com.vaadin.client.ui.grid.keyevents.AbstractGridKeyEventHandler.GridKeyPressHandler;
import com.vaadin.client.ui.grid.keyevents.AbstractGridKeyEventHandler.GridKeyUpHandler;
import com.vaadin.client.ui.grid.keyevents.BodyKeyDownHandler;
import com.vaadin.client.ui.grid.keyevents.BodyKeyPressHandler;
import com.vaadin.client.ui.grid.keyevents.BodyKeyUpHandler;
import com.vaadin.client.ui.grid.keyevents.FooterKeyDownHandler;
import com.vaadin.client.ui.grid.keyevents.FooterKeyPressHandler;
import com.vaadin.client.ui.grid.keyevents.FooterKeyUpHandler;
import com.vaadin.client.ui.grid.keyevents.GridKeyDownEvent;
import com.vaadin.client.ui.grid.keyevents.GridKeyPressEvent;
import com.vaadin.client.ui.grid.keyevents.GridKeyUpEvent;
import com.vaadin.client.ui.grid.keyevents.HeaderKeyDownHandler;
import com.vaadin.client.ui.grid.keyevents.HeaderKeyPressHandler;
import com.vaadin.client.ui.grid.keyevents.HeaderKeyUpHandler;
import com.vaadin.client.ui.grid.renderers.ComplexRenderer;
import com.vaadin.client.ui.grid.renderers.WidgetRenderer;
import com.vaadin.client.ui.grid.selection.HasSelectionChangeHandlers;
import com.vaadin.client.ui.grid.selection.SelectionChangeEvent;
import com.vaadin.client.ui.grid.selection.SelectionChangeHandler;
import com.vaadin.client.ui.grid.selection.SelectionModel;
import com.vaadin.client.ui.grid.selection.SelectionModelMulti;
import com.vaadin.client.ui.grid.selection.SelectionModelNone;
import com.vaadin.client.ui.grid.selection.SelectionModelSingle;
import com.vaadin.client.ui.grid.sort.Sort;
import com.vaadin.client.ui.grid.sort.SortEvent;
import com.vaadin.client.ui.grid.sort.SortEventHandler;
import com.vaadin.client.ui.grid.sort.SortOrder;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.grid.SortDirection;
import com.vaadin.shared.ui.grid.SortEventOriginator;

/**
 * A data grid view that supports columns and lazy loading of data rows from a
 * data source.
 * 
 * <h3>Columns</h3>
 * <p>
 * The {@link GridColumn} class defines the renderer used to render a cell in
 * the grid. Implement {@link GridColumn#getValue(Object)} to retrieve the cell
 * value from the row object and return the cell renderer to render that cell.
 * </p>
 * <p>
 * {@link GridColumn}s contain other properties like the width of the column and
 * the visiblity of the column. If you want to change a column's properties
 * after it has been added to the grid you can get a column object for a
 * specific column index using {@link Grid#getColumn(int)}.
 * </p>
 * <p>
 * 
 * TODO Explain about headers/footers once the multiple header/footer api has
 * been implemented
 * 
 * <h3>Data sources</h3>
 * <p>
 * TODO Explain about what a data source is and how it should be implemented.
 * </p>
 * 
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since
 * @author Vaadin Ltd
 */
public class Grid<T> extends Composite implements
        HasSelectionChangeHandlers<T>, SubPartAware {

    public static abstract class AbstractGridKeyEvent<T, HANDLER extends AbstractGridKeyEventHandler>
            extends KeyCodeEvent<HANDLER> {

        /**
         * Enum describing different section of Grid.
         */
        public enum GridSection {
            HEADER, BODY, FOOTER
        }

        private Grid<T> grid;
        protected Cell activeCell;
        protected GridSection activeSection;
        private final Type<HANDLER> associatedType = new Type<HANDLER>(
                getBrowserEventType(), this);

        public AbstractGridKeyEvent(Grid<T> grid) {
            this.grid = grid;
        }

        protected abstract String getBrowserEventType();

        /**
         * Gets the Grid instance for this event.
         * 
         * @return grid
         */
        public Grid<T> getGrid() {
            return grid;
        }

        /**
         * Gets the active cell for this event.
         * 
         * @return active cell
         */
        public Cell getActiveCell() {
            return activeCell;
        }

        @Override
        protected void dispatch(HANDLER handler) {
            activeCell = grid.activeCellHandler.getActiveCell();
            activeSection = GridSection.FOOTER;
            final RowContainer container = grid.activeCellHandler.container;
            if (container == grid.escalator.getHeader()) {
                activeSection = GridSection.HEADER;
            } else if (container == grid.escalator.getBody()) {
                activeSection = GridSection.BODY;
            }
        }

        @Override
        public Type<HANDLER> getAssociatedType() {
            return associatedType;
        }
    }

    private GridKeyDownEvent<T> keyDown = new GridKeyDownEvent<T>(this);
    private GridKeyUpEvent<T> keyUp = new GridKeyUpEvent<T>(this);
    private GridKeyPressEvent<T> keyPress = new GridKeyPressEvent<T>(this);

    private class ActiveCellHandler {

        private RowContainer container = escalator.getBody();
        private int activeRow = 0;
        private int activeColumn = 0;
        private int lastActiveBodyRow = 0;
        private int lastActiveHeaderRow = 0;
        private int lastActiveFooterRow = 0;
        private TableCellElement cellWithActiveStyle = null;
        private TableRowElement rowWithActiveStyle = null;

        public ActiveCellHandler() {
            sinkEvents(getNavigationEvents());
        }

        private Cell getActiveCell() {
            return new Cell(activeRow, activeColumn, cellWithActiveStyle);
        }

        /**
         * Sets style names for given cell when needed.
         */
        public void updateActiveCellStyle(FlyweightCell cell,
                RowContainer cellContainer) {
            int cellRow = cell.getRow();
            int cellColumn = cell.getColumn();
            int colSpan = cell.getColSpan();
            boolean columnActive = Range.withLength(cellColumn, colSpan)
                    .contains(activeColumn);

            if (cellContainer == container) {
                // Cell is in the current container
                if (cellRow == activeRow && columnActive) {
                    if (cellWithActiveStyle != cell.getElement()) {
                        // Cell is correct but it does not have active style
                        if (cellWithActiveStyle != null) {
                            // Remove old active style
                            setStyleName(cellWithActiveStyle,
                                    cellActiveStyleName, false);
                        }
                        cellWithActiveStyle = cell.getElement();
                        // Add active style to correct cell.
                        setStyleName(cellWithActiveStyle, cellActiveStyleName,
                                true);
                    }
                } else if (cellWithActiveStyle == cell.getElement()) {
                    // Due to escalator reusing cells, a new cell has the same
                    // element but is not the active cell.
                    setStyleName(cellWithActiveStyle, cellActiveStyleName,
                            false);
                    cellWithActiveStyle = null;
                }
            }

            if (cellContainer == escalator.getHeader()
                    || cellContainer == escalator.getFooter()) {
                // Correct header and footer column also needs highlighting
                setStyleName(cell.getElement(), headerFooterActiveStyleName,
                        columnActive);
            }
        }

        /**
         * Sets active row style name for given row if needed.
         * 
         * @param row
         *            a row object
         */
        public void updateActiveRowStyle(Row row) {
            if (activeRow == row.getRow() && container == escalator.getBody()) {
                if (row.getElement() != rowWithActiveStyle) {
                    // Row should have active style but does not have it.
                    if (rowWithActiveStyle != null) {
                        setStyleName(rowWithActiveStyle, rowActiveStyleName,
                                false);
                    }
                    rowWithActiveStyle = row.getElement();
                    setStyleName(rowWithActiveStyle, rowActiveStyleName, true);
                }
            } else if (rowWithActiveStyle == row.getElement()
                    || (container != escalator.getBody() && rowWithActiveStyle != null)) {
                // Remove active style.
                setStyleName(rowWithActiveStyle, rowActiveStyleName, false);
                rowWithActiveStyle = null;
            }
        }

        /**
         * Sets currently active cell to a cell in given container with given
         * indices.
         * 
         * @param row
         *            new active row
         * @param column
         *            new active column
         * @param container
         *            new container
         */
        private void setActiveCell(int row, int column, RowContainer container) {
            if (row == activeRow && column == activeColumn
                    && container == this.container) {
                return;
            }

            int oldRow = activeRow;
            int oldColumn = activeColumn;
            activeRow = row;
            activeColumn = column;

            if (container == escalator.getBody()) {
                scrollToRow(activeRow);
            }
            escalator.scrollToColumn(activeColumn, ScrollDestination.ANY, 10);

            if (this.container == container) {
                if (container != escalator.getBody()) {
                    if (oldColumn == activeColumn && oldRow != activeRow) {
                        refreshRow(oldRow);
                    } else if (oldColumn != activeColumn) {
                        refreshHeader();
                        refreshFooter();
                    }
                } else {
                    if (oldRow != activeRow) {
                        refreshRow(oldRow);
                    }

                    if (oldColumn != activeColumn) {
                        refreshHeader();
                        refreshFooter();
                    }
                }
            } else {
                RowContainer oldContainer = this.container;
                this.container = container;

                if (oldContainer == escalator.getBody()) {
                    lastActiveBodyRow = oldRow;
                } else if (oldContainer == escalator.getHeader()) {
                    lastActiveHeaderRow = oldRow;
                } else {
                    lastActiveFooterRow = oldRow;
                }

                if (oldColumn != activeColumn) {
                    refreshHeader();
                    refreshFooter();
                    if (oldContainer == escalator.getBody()) {
                        oldContainer.refreshRows(oldRow, 1);
                    }
                } else {
                    oldContainer.refreshRows(oldRow, 1);
                }
            }
            refreshRow(activeRow);
        }

        /**
         * Sets currently active cell used for keyboard navigation. Note that
         * active cell is not JavaScript {@code document.activeElement}.
         * 
         * @param cell
         *            a cell object
         */
        public void setActiveCell(Cell cell) {
            setActiveCell(cell.getRow(), cell.getColumn(),
                    escalator.findRowContainer(cell.getElement()));
        }

        /**
         * Gets list of events that can be used for active cell navigation.
         * 
         * @return list of navigation related event types
         */
        public Collection<String> getNavigationEvents() {
            return Arrays.asList(BrowserEvents.KEYDOWN, BrowserEvents.CLICK);
        }

        /**
         * Handle events that can change the currently active cell.
         */
        public void handleNavigationEvent(Event event, Cell cell) {
            if (event.getType().equals(BrowserEvents.CLICK) && cell != null) {
                setActiveCell(cell);
                getElement().focus();
            } else if (event.getType().equals(BrowserEvents.KEYDOWN)) {
                int newRow = activeRow;
                int newColumn = activeColumn;
                RowContainer newContainer = container;

                switch (event.getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    newRow += 1;
                    break;
                case KeyCodes.KEY_UP:
                    newRow -= 1;
                    break;
                case KeyCodes.KEY_RIGHT:
                    newColumn += 1;
                    break;
                case KeyCodes.KEY_LEFT:
                    newColumn -= 1;
                    break;
                case KeyCodes.KEY_TAB:
                    if (event.getShiftKey()) {
                        newContainer = getPreviousContainer(container);
                    } else {
                        newContainer = getNextContainer(container);
                    }

                    if (newContainer == container) {
                        return;
                    }
                    break;
                default:
                    return;
                }

                if (newContainer != container) {
                    if (newContainer == escalator.getBody()) {
                        newRow = lastActiveBodyRow;
                    } else if (newContainer == escalator.getHeader()) {
                        newRow = lastActiveHeaderRow;
                    } else {
                        newRow = lastActiveFooterRow;
                    }
                } else if (newRow < 0) {
                    newContainer = getPreviousContainer(newContainer);

                    if (newContainer == container) {
                        newRow = 0;
                    } else if (newContainer == escalator.getBody()) {
                        newRow = getLastVisibleRowIndex();
                    } else {
                        newRow = newContainer.getRowCount() - 1;
                    }
                } else if (newRow >= container.getRowCount()) {
                    newContainer = getNextContainer(newContainer);

                    if (newContainer == container) {
                        newRow = container.getRowCount() - 1;
                    } else if (newContainer == escalator.getBody()) {
                        newRow = getFirstVisibleRowIndex();
                    } else {
                        newRow = 0;
                    }
                }

                if (newContainer.getRowCount() == 0) {
                    // There are no rows in the container. Can't change the
                    // active cell.
                    return;
                }

                if (newColumn < 0) {
                    newColumn = 0;
                } else if (newColumn >= getColumnCount()) {
                    newColumn = getColumnCount() - 1;
                }

                event.preventDefault();
                event.stopPropagation();

                setActiveCell(newRow, newColumn, newContainer);
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
            container.refreshRows(row, 1);
        }
    }

    private class SelectionColumn extends GridColumn<Boolean, T> {
        private boolean initDone = false;

        public SelectionColumn(final Renderer<Boolean> selectColumnRenderer) {
            super(selectColumnRenderer);
        }

        public void initDone() {
            initDone = true;
        }

        @Override
        public void setVisible(boolean visible) {
            if (!visible && initDone) {
                throw new UnsupportedOperationException("The selection "
                        + "column cannot be modified after init");
            } else {
                super.setVisible(visible);
            }
        }

        @Override
        public void setWidth(int pixels) {
            if (pixels != getWidth() && initDone) {
                throw new UnsupportedOperationException("The selection "
                        + "column cannot be modified after init");
            } else {
                super.setWidth(pixels);
            }
        }

        @Override
        public Boolean getValue(T row) {
            return Boolean.valueOf(isSelected(row));
        }
    }

    /**
     * Class for sorting at a later time
     */
    private class LazySorter extends Timer {

        private Cell cell;

        private boolean multisort;

        @Override
        public void run() {
            SortOrder sortingOrder = getSortOrder(getColumnFromVisibleIndex(cell
                    .getColumn()));
            if (sortingOrder == null) {
                /*
                 * No previous sorting, sort Ascending
                 */
                sort(cell, SortDirection.ASCENDING, multisort);

            } else {
                // Toggle sorting
                SortDirection direction = sortingOrder.getDirection();
                if (direction == SortDirection.ASCENDING) {
                    sort(cell, SortDirection.DESCENDING, multisort);
                } else {
                    sort(cell, SortDirection.ASCENDING, multisort);
                }
            }
        }

        /**
         * Set the cell reference to the primary cell that sorting should be
         * done for.
         * 
         * @param cell
         * 
         */
        public void setCellReference(Cell cell) {
            this.cell = cell;
        }

        /**
         * Is multiple column sorting is enabled/disabled
         * 
         * @param multisort
         *            true if multiple column sorting is enabled
         */
        public void setMultisort(boolean multisort) {
            this.multisort = multisort;
        }

        /**
         * Sorts the column in a direction
         */
        private void sort(Cell cell, SortDirection direction, boolean multisort) {
            TableCellElement th = TableCellElement.as(cell.getElement());

            // Apply primary sorting on clicked column
            GridColumn<?, ?> columnInstance = getColumnFromVisibleIndex(cell
                    .getColumn());
            Sort sorting = Sort.by(columnInstance, direction);

            // Re-apply old sorting to the sort order
            if (multisort) {
                for (SortOrder order : getSortOrder()) {
                    if (order.getColumn() != columnInstance) {
                        sorting = sorting.then(order.getColumn(),
                                order.getDirection());
                    }
                }
            }

            // Perform sorting; indicate originator as user
            Grid.this.setSortOrder(sorting.build(), SortEventOriginator.USER);
        }
    }

    /**
     * Escalator used internally by grid to render the rows
     */
    private Escalator escalator = GWT.create(Escalator.class);

    private final GridHeader header = GWT.create(GridHeader.class);

    private final GridFooter footer = GWT.create(GridFooter.class);

    /**
     * List of columns in the grid. Order defines the visible order.
     */
    private final List<GridColumn<?, T>> columns = new ArrayList<GridColumn<?, T>>();

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
     * The last column frozen counter from the left
     */
    private GridColumn<?, T> lastFrozenColumn;

    /**
     * Current sort order. The (private) sort() method reads this list to
     * determine the order in which to present rows.
     */
    private List<SortOrder> sortOrder = new ArrayList<SortOrder>();

    private Renderer<Boolean> selectColumnRenderer = null;

    private SelectionColumn selectionColumn;

    private String rowHasDataStyleName;
    private String rowSelectedStyleName;
    private String cellActiveStyleName;
    private String rowActiveStyleName;
    private String headerFooterActiveStyleName;

    /**
     * Current selection model.
     */
    private SelectionModel<T> selectionModel;

    protected final ActiveCellHandler activeCellHandler;

    private final LazySorter lazySorter = new LazySorter();

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
                return new SelectionModelSingle<T>();
            }
        },

        /**
         * Shortcut for {@link SelectionModelMulti}.
         */
        MULTI {

            @Override
            protected <T> SelectionModel<T> createModel() {
                return new SelectionModelMulti<T>();
            }
        },

        /**
         * Shortcut for {@link SelectionModelNone}.
         */
        NONE {

            @Override
            protected <T> SelectionModel<T> createModel() {
                return new SelectionModelNone<T>();
            }
        };

        protected abstract <T> SelectionModel<T> createModel();
    }

    /**
     * Base class for grid columns internally used by the Grid. The user should
     * use {@link GridColumn} when creating new columns.
     * 
     * @param <C>
     *            the column type
     * 
     * @param <T>
     *            the row type
     */
    static abstract class AbstractGridColumn<C, T> implements HasVisibility {

        /**
         * the column is associated with
         */
        private Grid<T> grid;

        /**
         * Should the column be visible in the grid
         */
        private boolean visible = true;

        /**
         * The text displayed in the header of the column
         */
        @Deprecated
        private String header;

        /**
         * Text displayed in the column footer
         */
        @Deprecated
        private String footer;

        /**
         * Width of column in pixels
         */
        private int width = 100;

        /**
         * Renderer for rendering a value into the cell
         */
        private Renderer<? super C> bodyRenderer;

        private boolean sortable = false;

        /**
         * Constructs a new column with a custom renderer.
         * 
         * @param renderer
         *            The renderer to use for rendering the cells
         */
        public AbstractGridColumn(Renderer<? super C> renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer cannot be null.");
            }
            bodyRenderer = renderer;
        }

        /**
         * Internally used by the grid to set itself
         * 
         * @param grid
         */
        private void setGrid(Grid<T> grid) {
            if (this.grid != null && grid != null) {
                // Trying to replace grid
                throw new IllegalStateException(
                        "Column already is attached to grid. Remove the column first from the grid and then add it.");
            }

            this.grid = grid;
        }

        /**
         * Is the column visible. By default all columns are visible.
         * 
         * @return <code>true</code> if the column is visible
         */
        @Override
        public boolean isVisible() {
            return visible;
        }

        /**
         * Sets a column as visible in the grid.
         * 
         * @param visible
         *            <code>true</code> if the column should be displayed in the
         *            grid
         */
        @Override
        public void setVisible(boolean visible) {
            if (this.visible == visible) {
                return;
            }

            this.visible = visible;

            if (grid != null) {
                int index = findIndexOfColumn();
                ColumnConfiguration conf = grid.escalator
                        .getColumnConfiguration();

                if (visible) {
                    conf.insertColumns(index, 1);
                } else {
                    conf.removeColumns(index, 1);
                }

                for (HeaderRow row : grid.getHeader().getRows()) {
                    row.calculateColspans();
                }

                for (FooterRow row : grid.getFooter().getRows()) {
                    row.calculateColspans();
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
         * The renderer to render the cell width. By default renders the data as
         * a String or adds the widget into the cell if the column type is of
         * widget type.
         * 
         * @return The renderer to render the cell content with
         */
        public Renderer<? super C> getRenderer() {
            return bodyRenderer;
        }

        /**
         * Finds the index of this column instance
         * 
         */
        private int findIndexOfColumn() {
            return grid.findVisibleColumnIndex((GridColumn<?, T>) this);
        }

        /**
         * Sets the pixel width of the column. Use a negative value for the grid
         * to autosize column based on content and available space
         * 
         * @param pixels
         *            the width in pixels or negative for auto sizing
         */
        public void setWidth(int pixels) {
            width = pixels;

            if (grid != null && isVisible()) {
                int index = findIndexOfColumn();
                ColumnConfiguration conf = grid.escalator
                        .getColumnConfiguration();
                conf.setColumnWidth(index, pixels);
            }
        }

        /**
         * Returns the pixel width of the column
         * 
         * @return pixel width of the column
         */
        public int getWidth() {
            return width;
        }

        /**
         * Enables sort indicators for the grid.
         * <p>
         * <b>Note:</b>The API can still sort the column even if this is set to
         * <code>false</code>.
         * 
         * @param sortable
         *            <code>true</code> when column sort indicators are visible.
         */
        public void setSortable(boolean sortable) {
            if (this.sortable != sortable) {
                this.sortable = sortable;
                grid.refreshHeader();
            }
        }

        /**
         * Are sort indicators shown for the column.
         * 
         * @return <code>true</code> if the column is sortable
         */
        public boolean isSortable() {
            return sortable;
        }
    }

    protected class BodyUpdater implements EscalatorUpdater {

        @Override
        public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
            for (FlyweightCell cell : cellsToAttach) {
                Renderer<?> renderer = findRenderer(cell);
                if (renderer instanceof ComplexRenderer) {
                    ((ComplexRenderer<?>) renderer).init(cell);
                }
            }
        }

        @Override
        public void postAttach(Row row, Iterable<FlyweightCell> attachedCells) {
            for (FlyweightCell cell : attachedCells) {
                Renderer<?> renderer = findRenderer(cell);
                if (renderer instanceof WidgetRenderer) {
                    WidgetRenderer<?, ?> widgetRenderer = (WidgetRenderer<?, ?>) renderer;

                    Widget widget = widgetRenderer.createWidget();
                    assert widget != null : "WidgetRenderer.createWidget() returned null. It should return a widget.";
                    assert widget.getParent() == null : "WidgetRenderer.createWidget() returned a widget which already is attached.";
                    assert cell.getElement().getChildCount() == 0 : "Cell content should be empty when adding Widget";

                    // Physical attach
                    cell.getElement().appendChild(widget.getElement());

                    // Logical attach
                    setParent(widget, Grid.this);
                }
            }
        }

        @Override
        public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {
            int rowIndex = row.getRow();
            Element rowElement = row.getElement();
            T rowData = dataSource.getRow(rowIndex);

            boolean hasData = rowData != null;

            // Assign stylename for rows with data
            boolean usedToHaveData = rowElement
                    .hasClassName(rowHasDataStyleName);

            if (usedToHaveData != hasData) {
                setStyleName(rowElement, rowHasDataStyleName, hasData);
            }

            // Assign stylename for selected rows
            if (hasData) {
                setStyleName(rowElement, rowSelectedStyleName,
                        isSelected(rowData));
            } else if (usedToHaveData) {
                setStyleName(rowElement, rowSelectedStyleName, false);
            }

            activeCellHandler.updateActiveRowStyle(row);

            for (FlyweightCell cell : cellsToUpdate) {
                GridColumn<?, T> column = getColumnFromVisibleIndex(cell
                        .getColumn());

                assert column != null : "Column was not found from cell ("
                        + cell.getColumn() + "," + cell.getRow() + ")";

                activeCellHandler.updateActiveCellStyle(cell,
                        escalator.getBody());

                Renderer renderer = column.getRenderer();

                // Hide cell content if needed
                if (renderer instanceof ComplexRenderer) {
                    ComplexRenderer clxRenderer = (ComplexRenderer) renderer;
                    if (hasData) {
                        if (!usedToHaveData) {
                            // Prepare cell for rendering
                            clxRenderer.setContentVisible(cell, true);
                        }

                        Object value = column.getValue(rowData);
                        clxRenderer.render(cell, value);

                    } else {
                        // Prepare cell for no data
                        clxRenderer.setContentVisible(cell, false);
                    }

                } else if (hasData) {
                    // Simple renderers just render
                    Object value = column.getValue(rowData);
                    renderer.render(cell, value);

                } else {
                    // Clear cell if there is no data
                    cell.getElement().removeAllChildren();
                }
            }
        }

        @Override
        public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
            for (FlyweightCell cell : cellsToDetach) {
                Renderer renderer = findRenderer(cell);
                if (renderer instanceof WidgetRenderer) {
                    Widget w = Util.findWidget(cell.getElement()
                            .getFirstChildElement(), Widget.class);
                    if (w != null) {

                        // Logical detach
                        setParent(w, null);

                        // Physical detach
                        cell.getElement().removeChild(w.getElement());
                    }
                }
            }
        }

        @Override
        public void postDetach(Row row, Iterable<FlyweightCell> detachedCells) {
            for (FlyweightCell cell : detachedCells) {
                Renderer renderer = findRenderer(cell);
                if (renderer instanceof ComplexRenderer) {
                    ((ComplexRenderer) renderer).destroy(cell);
                }
            }
        }
    }

    protected class StaticSectionUpdater implements EscalatorUpdater {

        private GridStaticSection<?> section;
        private RowContainer container;

        public StaticSectionUpdater(GridStaticSection<?> section,
                RowContainer container) {
            super();
            this.section = section;
            this.container = container;
        }

        @Override
        public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {
            GridStaticSection.StaticRow<?> staticRow = section.getRow(row
                    .getRow());

            final List<Integer> columnIndices = getVisibleColumnIndices();

            for (FlyweightCell cell : cellsToUpdate) {

                int index = columnIndices.get(cell.getColumn());
                final StaticCell metadata = staticRow.getCell(index);

                // Decorate default row with sorting indicators
                if (staticRow instanceof HeaderRow) {
                    addSortingIndicatorsToHeaderRow((HeaderRow) staticRow, cell);
                }

                // Assign colspan to cell before rendering
                cell.setColSpan(metadata.getColspan());

                switch (metadata.getType()) {
                case TEXT:
                    cell.getElement().setInnerText(metadata.getText());
                    break;
                case HTML:
                    cell.getElement().setInnerHTML(metadata.getHtml());
                    break;
                case WIDGET:
                    preDetach(row, Arrays.asList(cell));
                    cell.getElement().setInnerHTML("");
                    postAttach(row, Arrays.asList(cell));
                    break;
                }

                activeCellHandler.updateActiveCellStyle(cell, container);
            }
        }

        private void addSortingIndicatorsToHeaderRow(HeaderRow headerRow,
                FlyweightCell cell) {

            cleanup(cell);

            GridColumn<?, ?> column = getColumnFromVisibleIndex(cell
                    .getColumn());
            SortOrder sortingOrder = getSortOrder(column);
            if (!headerRow.isDefault() || !column.isSortable()
                    || sortingOrder == null) {
                // Only apply sorting indicators to sortable header columns in
                // the default header row
                return;
            }

            Element cellElement = cell.getElement();

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
        }

        /**
         * Finds the sort order for this column
         */
        private SortOrder getSortOrder(GridColumn<?, ?> column) {
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
        }

        @Override
        public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
        }

        @Override
        public void postAttach(Row row, Iterable<FlyweightCell> attachedCells) {
            GridStaticSection.StaticRow<?> gridRow = section.getRow(row
                    .getRow());
            List<Integer> columnIndices = getVisibleColumnIndices();

            for (FlyweightCell cell : attachedCells) {
                int index = columnIndices.get(cell.getColumn());
                StaticCell metadata = gridRow.getCell(index);
                /*
                 * If the cell contains widgets that are not currently attach
                 * then attach them now.
                 */
                if (GridStaticCellType.WIDGET.equals(metadata.getType())) {
                    final Widget widget = metadata.getWidget();
                    final Element cellElement = cell.getElement();

                    if (!widget.isAttached()) {

                        // Physical attach
                        cellElement.appendChild(widget.getElement());

                        // Logical attach
                        setParent(widget, Grid.this);

                        getLogger().info("Attached widget " + widget);
                    }
                }
            }
        }

        @Override
        public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
            if (section.getRowCount() > row.getRow()) {
                GridStaticSection.StaticRow<?> gridRow = section.getRow(row
                        .getRow());
                List<Integer> columnIndices = getVisibleColumnIndices();
                for (FlyweightCell cell : cellsToDetach) {
                    int index = columnIndices.get(cell.getColumn());
                    StaticCell metadata = gridRow.getCell(index);

                    if (GridStaticCellType.WIDGET.equals(metadata.getType())
                            && metadata.getWidget().isAttached()) {

                        Widget widget = metadata.getWidget();

                        // Logical detach
                        setParent(widget, null);

                        // Physical detach
                        widget.getElement().removeFromParent();

                        getLogger().info("Detached widget " + widget);
                    }
                }
            }
        }

        @Override
        public void postDetach(Row row, Iterable<FlyweightCell> detachedCells) {
        }

        private List<Integer> getVisibleColumnIndices() {
            List<Integer> indices = new ArrayList<Integer>(getColumnCount());
            for (int i = 0; i < getColumnCount(); i++) {
                if (getColumn(i).isVisible()) {
                    indices.add(i);
                }
            }
            return indices;
        }
    };

    /**
     * Creates a new instance.
     */
    public Grid() {
        initWidget(escalator);
        getElement().setTabIndex(0);
        activeCellHandler = new ActiveCellHandler();

        setStylePrimaryName("v-grid");

        escalator.getHeader().setEscalatorUpdater(createHeaderUpdater());
        escalator.getBody().setEscalatorUpdater(createBodyUpdater());
        escalator.getFooter().setEscalatorUpdater(createFooterUpdater());

        header.setGrid(this);
        HeaderRow defaultRow = header.appendRow();
        header.setDefaultRow(defaultRow);

        footer.setGrid(this);

        setSelectionMode(SelectionMode.SINGLE);

        escalator
                .addRowVisibilityChangeHandler(new RowVisibilityChangeHandler() {
                    @Override
                    public void onRowVisibilityChange(
                            RowVisibilityChangeEvent event) {
                        if (dataSource != null) {
                            dataSource.ensureAvailability(
                                    event.getFirstVisibleRow(),
                                    event.getVisibleRowCount());
                        }
                    }
                });

        // Default action on SelectionChangeEvents. Refresh the body so changed
        // become visible.
        addSelectionChangeHandler(new SelectionChangeHandler<T>() {

            @Override
            public void onSelectionChange(SelectionChangeEvent<T> event) {
                refreshBody();
            }
        });

        // Sink header events and key events
        sinkEvents(getHeader().getConsumedEvents());
        sinkEvents(Arrays.asList(BrowserEvents.KEYDOWN, BrowserEvents.KEYUP,
                BrowserEvents.KEYPRESS));
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        escalator.setStylePrimaryName(style);
        rowHasDataStyleName = getStylePrimaryName() + "-row-has-data";
        rowSelectedStyleName = getStylePrimaryName() + "-row-selected";
        cellActiveStyleName = getStylePrimaryName() + "-cell-active";
        headerFooterActiveStyleName = getStylePrimaryName() + "-header-active";
        rowActiveStyleName = getStylePrimaryName() + "-row-active";

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
    private void refreshRowContainer(RowContainer rows,
            GridStaticSection<?> section) {

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
     * Adds a column as the last column in the grid.
     * 
     * @param column
     *            the column to add
     */
    public void addColumn(GridColumn<?, T> column) {
        addColumn(column, getColumnCount());
    }

    /**
     * Inserts a column into a specific position in the grid.
     * 
     * @param index
     *            the index where the column should be inserted into
     * @param column
     *            the column to add
     * @throws IllegalStateException
     *             if Grid's current selection model renders a selection column,
     *             and {@code index} is 0.
     */
    public void addColumn(GridColumn<?, T> column, int index) {
        if (column == selectionColumn) {
            throw new IllegalArgumentException("The selection column many "
                    + "not be added manually");
        } else if (selectionColumn != null && index == 0) {
            throw new IllegalStateException("A column cannot be inserted "
                    + "before the selection column");
        }

        addColumnSkipSelectionColumnCheck(column, index);
    }

    private void addColumnSkipSelectionColumnCheck(GridColumn<?, T> column,
            int index) {
        // Register column with grid
        columns.add(index, column);

        header.addColumn(column, index);
        footer.addColumn(column, index);

        // Register this grid instance with the column
        ((AbstractGridColumn<?, T>) column).setGrid(this);

        // Insert column into escalator
        if (column.isVisible()) {
            int visibleIndex = findVisibleColumnIndex(column);
            ColumnConfiguration conf = escalator.getColumnConfiguration();

            // Insert column
            conf.insertColumns(visibleIndex, 1);

            // Transfer column width from column object to escalator
            conf.setColumnWidth(visibleIndex, column.getWidth());
        }

        if (lastFrozenColumn != null
                && ((AbstractGridColumn<?, T>) lastFrozenColumn)
                        .findIndexOfColumn() < index) {
            refreshFrozenColumns();
        }

        // Sink all renderer events
        Set<String> events = new HashSet<String>();
        events.addAll(getConsumedEventsForRenderer(column.getRenderer()));

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

    private int findVisibleColumnIndex(GridColumn<?, T> column) {
        int idx = 0;
        for (GridColumn<?, T> c : columns) {
            if (c == column) {
                return idx;
            } else if (c.isVisible()) {
                idx++;
            }
        }
        return -1;
    }

    private GridColumn<?, T> getColumnFromVisibleIndex(int index) {
        int idx = -1;
        for (GridColumn<?, T> c : columns) {
            if (c.isVisible()) {
                idx++;
            }
            if (index == idx) {
                return c;
            }
        }
        return null;
    }

    private Renderer<?> findRenderer(FlyweightCell cell) {
        GridColumn<?, T> column = getColumnFromVisibleIndex(cell.getColumn());
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
    public void removeColumn(GridColumn<?, T> column) {
        if (column != null && column.equals(selectionColumn)) {
            throw new IllegalArgumentException(
                    "The selection column may not be removed manually.");
        }

        removeColumnSkipSelectionColumnCheck(column);
    }

    private void removeColumnSkipSelectionColumnCheck(GridColumn<?, T> column) {
        int columnIndex = columns.indexOf(column);
        int visibleIndex = findVisibleColumnIndex(column);
        columns.remove(columnIndex);

        header.removeColumn(columnIndex);
        footer.removeColumn(columnIndex);

        // de-register column with grid
        ((AbstractGridColumn<?, T>) column).setGrid(null);

        if (column.isVisible()) {
            ColumnConfiguration conf = escalator.getColumnConfiguration();
            conf.removeColumns(visibleIndex, 1);
        }

        if (column.equals(lastFrozenColumn)) {
            setLastFrozenColumn(null);
        } else {
            refreshFrozenColumns();
        }
    }

    /**
     * Returns the amount of columns in the grid.
     * 
     * @return The number of columns in the grid
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Returns a list of columns in the grid.
     * 
     * @return A unmodifiable list of the columns in the grid
     */
    public List<GridColumn<?, T>> getColumns() {
        return Collections.unmodifiableList(new ArrayList<GridColumn<?, T>>(
                columns));
    }

    /**
     * Returns a column by its index in the grid.
     * 
     * @param index
     *            the index of the column
     * @return The column in the given index
     * @throws IllegalArgumentException
     *             if the column index does not exist in the grid
     */
    public GridColumn<?, T> getColumn(int index)
            throws IllegalArgumentException {
        if (index < 0 || index >= columns.size()) {
            throw new IllegalStateException("Column not found.");
        }
        return columns.get(index);
    }

    /**
     * Returns the header section of this grid. The default header contains a
     * single row displaying the column captions.
     * 
     * @return the header
     */
    public GridHeader getHeader() {
        return header;
    }

    /**
     * Returns the footer section of this grid. The default footer is empty.
     * 
     * @return the footer
     */
    public GridFooter getFooter() {
        return footer;
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
    public void setDataSource(DataSource<T> dataSource)
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
            }

            @Override
            public void dataAdded(int firstIndex, int numberOfItems) {
                escalator.getBody().insertRows(firstIndex, numberOfItems);
            }

            @Override
            public void dataAvailable(int firstIndex, int numberOfItems) {
                currentDataAvailable = Range.withLength(firstIndex,
                        numberOfItems);
                fireEvent(new DataAvailableEvent(currentDataAvailable));
            }
        });

        int previousRowCount = escalator.getBody().getRowCount();
        if (previousRowCount != 0) {
            escalator.getBody().removeRows(0, previousRowCount);
        }

        int estimatedSize = dataSource.getEstimatedSize();
        if (estimatedSize > 0) {
            escalator.getBody().insertRows(0, estimatedSize);
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
     * Sets the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the given column will be frozen in place
     * when the grid is scrolled sideways.
     * 
     * @param lastFrozenColumn
     *            the rightmost column to freeze, or <code>null</code> to not
     *            have any columns frozen
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    public void setLastFrozenColumn(GridColumn<?, T> lastFrozenColumn) {
        this.lastFrozenColumn = lastFrozenColumn;
        refreshFrozenColumns();
    }

    private void refreshFrozenColumns() {
        final int frozenCount;
        if (lastFrozenColumn != null) {
            frozenCount = columns.indexOf(lastFrozenColumn) + 1;
            if (frozenCount == 0) {
                throw new IllegalArgumentException(
                        "The given column isn't attached to this grid");
            }
        } else {
            frozenCount = 0;
        }

        escalator.getColumnConfiguration().setFrozenColumnCount(frozenCount);
    }

    /**
     * Gets the rightmost frozen column in the grid.
     * <p>
     * <em>Note:</em> Most usually, this method returns the very value set with
     * {@link #setLastFrozenColumn(GridColumn)}. This value, however, can be
     * reset to <code>null</code> if the column is removed from this grid.
     * 
     * @return the rightmost frozen column in the grid, or <code>null</code> if
     *         no columns are frozen.
     */
    public GridColumn<?, T> getLastFrozenColumn() {
        return lastFrozenColumn;
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

        escalator.scrollToRow(rowIndex, destination, paddingPx);
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
        super.onBrowserEvent(event);
        EventTarget target = event.getEventTarget();
        if (Element.is(target)) {
            Element e = Element.as(target);
            RowContainer container = escalator.findRowContainer(e);
            Cell cell = null;
            if (container != null) {
                cell = container.getCell(e);
                if (cell != null) {
                    // FIXME getFromVisibleIndex???
                    GridColumn<?, T> gridColumn = columns.get(cell.getColumn());

                    if (container == escalator.getHeader()) {
                        if (getHeader().getRow(cell.getRow()).isDefault()) {
                            handleDefaultRowEvent(cell, event);
                        }
                    } else if (container == escalator.getFooter()) {
                        // NOP
                    } else if (gridColumn.getRenderer() instanceof ComplexRenderer) {
                        ComplexRenderer<?> cplxRenderer = (ComplexRenderer<?>) gridColumn
                                .getRenderer();
                        if (cplxRenderer.getConsumedEvents().contains(
                                event.getType())) {
                            if (cplxRenderer.onBrowserEvent(cell, event)) {
                                return;
                            }
                        }
                    }
                }
            }

            Collection<String> navigation = activeCellHandler
                    .getNavigationEvents();
            if (navigation.contains(event.getType())
                    && (Util.getFocusedElement() == getElement() || cell != null)) {
                activeCellHandler.handleNavigationEvent(event, cell);
            }

            handleGridNavigation(event, cell);
        }
    }

    private void handleGridNavigation(Event event, Cell cell) {
        if (!event.getType().equals(BrowserEvents.KEYDOWN)) {
            // Only handle key downs
            return;
        }

        int newRow = -1;
        RowContainer container = escalator.getBody();
        switch (event.getKeyCode()) {
        case KeyCodes.KEY_HOME:
            if (container.getRowCount() > 0) {
                newRow = 0;
            }
            break;
        case KeyCodes.KEY_END:
            if (container.getRowCount() > 0) {
                newRow = container.getRowCount() - 1;
            }
            break;
        case KeyCodes.KEY_PAGEUP: {
            Range range = escalator.getVisibleRowRange();
            if (!range.isEmpty()) {
                int firstIndex = getFirstVisibleRowIndex();
                newRow = firstIndex - range.length();
                if (newRow < 0) {
                    newRow = 0;
                }
            }
            break;
        }
        case KeyCodes.KEY_PAGEDOWN: {
            Range range = escalator.getVisibleRowRange();
            if (!range.isEmpty()) {
                int lastIndex = getLastVisibleRowIndex();
                newRow = lastIndex + range.length();
                if (newRow >= container.getRowCount()) {
                    newRow = container.getRowCount() - 1;
                }
            }
            break;
        }
        default:
            return;
        }

        scrollToRow(newRow);
    }

    private Point rowEventTouchStartingPoint;

    private boolean handleDefaultRowEvent(final Cell cell, NativeEvent event) {
        if (!getColumn(cell.getColumn()).isSortable()) {
            // Only handle sorting events if the column is sortable
            return false;
        }

        if (BrowserEvents.TOUCHSTART.equals(event.getType())) {
            if (event.getTouches().length() > 1) {
                return false;
            }

            event.preventDefault();

            Touch touch = event.getChangedTouches().get(0);
            rowEventTouchStartingPoint = new Point(touch.getClientX(),
                    touch.getClientY());

            lazySorter.setCellReference(cell);
            lazySorter.setMultisort(true);
            lazySorter.schedule(GridConstants.LONG_TAP_DELAY);

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
                lazySorter.cancel();
            }

        } else if (BrowserEvents.TOUCHEND.equals(event.getType())) {
            if (event.getTouches().length() > 0) {
                return false;
            }

            if (lazySorter.isRunning()) {
                // Not a long tap yet, perform single sort
                lazySorter.cancel();
                lazySorter.setMultisort(false);
                lazySorter.run();
            }

        } else if (BrowserEvents.TOUCHCANCEL.equals(event.getType())) {
            if (event.getChangedTouches().length() > 1) {
                return false;
            }

            lazySorter.cancel();

        } else if (BrowserEvents.CLICK.equals(event.getType())) {
            lazySorter.setCellReference(cell);
            lazySorter.setMultisort(event.getShiftKey());
            lazySorter.run();

            // Active cell handling is also monitoring the click
            // event so we allow event to propagate for it
            return false;
        }

        return true;
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        // Parse SubPart string to type and indices
        String[] splitArgs = subPart.split("\\[");

        String type = splitArgs[0];
        int[] indices = new int[splitArgs.length - 1];
        for (int i = 0; i < indices.length; ++i) {
            String tmp = splitArgs[i + 1];
            indices[i] = Integer.parseInt(tmp.substring(0, tmp.length() - 1));
        }

        // Get correct RowContainer for type from Escalator
        RowContainer container = null;
        if (type.equalsIgnoreCase("header")) {
            container = escalator.getHeader();
        } else if (type.equalsIgnoreCase("cell")) {
            // If wanted row is not visible, we need to scroll there.
            Range visibleRowRange = escalator.getVisibleRowRange();
            if (indices.length > 0 && !visibleRowRange.contains(indices[0])) {
                try {
                    scrollToRow(indices[0]);
                } catch (IllegalArgumentException e) {
                    getLogger().log(Level.SEVERE, e.getMessage());
                }
                // Scrolling causes a lazy loading event. No element can
                // currently be retrieved.
                return null;
            }
            container = escalator.getBody();
        } else if (type.equalsIgnoreCase("footer")) {
            container = escalator.getFooter();
        }

        if (null != container) {
            if (indices.length == 0) {
                // No indexing. Just return the wanted container element
                return DOM.asOld(container.getElement());
            } else {
                try {
                    return DOM.asOld(getSubPart(container, indices));
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, e.getMessage());
                }
            }
        }
        return null;
    }

    private Element getSubPart(RowContainer container, int[] indices) {
        // Scroll wanted column to view if able
        if (indices.length > 1
                && escalator.getColumnConfiguration().getFrozenColumnCount() <= indices[1]) {
            escalator.scrollToColumn(indices[1], ScrollDestination.ANY, 0);
        }

        Element targetElement = container.getRowElement(indices[0]);
        for (int i = 1; i < indices.length && targetElement != null; ++i) {
            targetElement = (Element) targetElement.getChild(indices[i]);
        }
        return targetElement;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        // Containers and matching SubPart types
        List<RowContainer> containers = Arrays.asList(escalator.getHeader(),
                escalator.getBody(), escalator.getFooter());
        List<String> containerType = Arrays.asList("header", "cell", "footer");

        for (int i = 0; i < containers.size(); ++i) {
            RowContainer container = containers.get(i);
            boolean containerRow = (subElement.getTagName().equalsIgnoreCase(
                    "tr") && subElement.getParentElement() == container
                    .getElement());
            if (containerRow) {
                // Wanted SubPart is row that is a child of containers root
                // To get indices, we use a cell that is a child of this row
                subElement = DOM.asOld(subElement.getFirstChildElement());
            }

            Cell cell = container.getCell(subElement);
            if (cell != null) {
                // Skip the column index if subElement was a child of root
                return containerType.get(i) + "[" + cell.getRow()
                        + (containerRow ? "]" : "][" + cell.getColumn() + "]");
            }
        }
        return null;
    }

    private void setSelectColumnRenderer(
            final Renderer<Boolean> selectColumnRenderer) {
        if (this.selectColumnRenderer == selectColumnRenderer) {
            return;
        }

        if (this.selectColumnRenderer != null) {
            removeColumnSkipSelectionColumnCheck(selectionColumn);
            --activeCellHandler.activeColumn;
        }

        this.selectColumnRenderer = selectColumnRenderer;

        if (selectColumnRenderer != null) {
            ++activeCellHandler.activeColumn;
            selectionColumn = new SelectionColumn(selectColumnRenderer);

            // FIXME: this needs to be done elsewhere, requires design...
            selectionColumn.setWidth(25);
            addColumnSkipSelectionColumnCheck(selectionColumn, 0);
            selectionColumn.initDone();
        } else {
            selectionColumn = null;
            refreshBody();
        }
    }

    /**
     * Accesses the package private method Widget#setParent()
     * 
     * @param widget
     *            The widget to access
     * @param parent
     *            The parent to set
     */
    private static native final void setParent(Widget widget, Widget parent)
    /*-{
        widget.@com.google.gwt.user.client.ui.Widget::setParent(Lcom/google/gwt/user/client/ui/Widget;)(parent);
    }-*/;

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

        this.selectionModel = selectionModel;
        selectionModel.setGrid(this);
        setSelectColumnRenderer(this.selectionModel
                .getSelectionColumnRenderer());
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
    @SuppressWarnings("unchecked")
    public boolean select(T row) {
        if (selectionModel instanceof SelectionModel.Single<?>) {
            return ((SelectionModel.Single<T>) selectionModel).select(row);
        } else if (selectionModel instanceof SelectionModel.Multi<?>) {
            return ((SelectionModel.Multi<T>) selectionModel).select(row);
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
    @SuppressWarnings("unchecked")
    public boolean deselect(T row) {
        if (selectionModel instanceof SelectionModel.Single<?>) {
            return ((SelectionModel.Single<T>) selectionModel).deselect(row);
        } else if (selectionModel instanceof SelectionModel.Multi<?>) {
            return ((SelectionModel.Multi<T>) selectionModel).deselect(row);
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
    public HandlerRegistration addSelectionChangeHandler(
            final SelectionChangeHandler<T> handler) {
        return addHandler(handler, SelectionChangeEvent.getType());
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
    public <C> void sort(GridColumn<C, T> column) {
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
    public <C> void sort(GridColumn<C, T> column, SortDirection direction) {
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
        setSortOrder(order, SortEventOriginator.API);
    }

    private void setSortOrder(List<SortOrder> order,
            SortEventOriginator originator) {
        sortOrder.clear();
        if (order != null) {
            sortOrder.addAll(order);
        }
        sort(originator);
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
    private SortOrder getSortOrder(GridColumn<?, ?> column) {
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
    public HandlerRegistration addSortHandler(SortEventHandler<T> handler) {
        return addHandler(handler, SortEvent.getType());
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
                handler.onDataAvailable(new DataAvailableEvent(
                        currentDataAvailable));
            }
        });
        return addHandler(handler, DataAvailableEvent.TYPE);
    }

    /**
     * Register a KeyDown handler to this Grid. If the handler is a
     * HeaderKeyDownHandler, it will be fired only when a header cell is active.
     * The same goes for body and footer with their respective handlers.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public <HANDLER extends GridKeyDownHandler<T>> HandlerRegistration addKeyDownHandler(
            HANDLER handler) {
        if (handler instanceof BodyKeyDownHandler
                || handler instanceof HeaderKeyDownHandler
                || handler instanceof FooterKeyDownHandler) {
            return addHandler(handler, keyDown.getAssociatedType());
        }
        throw new IllegalArgumentException(
                "Handler not a valid extension of GridKeyDownHandler");
    }

    /**
     * Register a KeyUp handler to this Grid. If the handler is a
     * HeaderKeyUpHandler, it will be fired only when a header cell is active.
     * The same goes for body and footer with their respective handlers.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public <HANDLER extends GridKeyUpHandler<T>> HandlerRegistration addKeyUpHandler(
            HANDLER handler) {
        if (handler instanceof BodyKeyUpHandler
                || handler instanceof HeaderKeyUpHandler
                || handler instanceof FooterKeyUpHandler) {
            return addHandler(handler, keyUp.getAssociatedType());
        }
        throw new IllegalArgumentException(
                "Handler not a valid extension of GridKeyUpHandler");
    }

    /**
     * Register a KeyPress handler to this Grid. If the handler is a
     * HeaderKeyPressHandler, it will be fired only when a header cell is
     * active. The same goes for body and footer with their respective handlers.
     * 
     * @param handler
     *            the key handler to register
     * @return the registration for the event
     */
    public <HANDLER extends GridKeyPressHandler<T>> HandlerRegistration addKeyPressHandler(
            HANDLER handler) {
        if (handler instanceof BodyKeyPressHandler
                || handler instanceof HeaderKeyPressHandler
                || handler instanceof FooterKeyPressHandler) {
            return addHandler(handler, keyPress.getAssociatedType());
        }
        throw new IllegalArgumentException(
                "Handler not a valid extension of GridKeyPressHandler");
    }

    /**
     * Apply sorting to data source.
     */
    private void sort(SortEventOriginator originator) {
        refreshHeader();
        fireEvent(new SortEvent<T>(this,
                Collections.unmodifiableList(sortOrder), originator));
    }

    private int getLastVisibleRowIndex() {
        int lastRowIndex = escalator.getVisibleRowRange().getEnd();
        int footerTop = escalator.getFooter().getElement().getAbsoluteTop();
        Element lastRow;

        do {
            lastRow = escalator.getBody().getRowElement(--lastRowIndex);
        } while (lastRow.getAbsoluteBottom() > footerTop);

        return lastRowIndex;
    }

    private int getFirstVisibleRowIndex() {
        int firstRowIndex = escalator.getVisibleRowRange().getStart();
        int headerBottom = escalator.getHeader().getElement()
                .getAbsoluteBottom();
        Element firstRow = escalator.getBody().getRowElement(firstRowIndex);

        while (firstRow.getAbsoluteTop() < headerBottom) {
            firstRow = escalator.getBody().getRowElement(++firstRowIndex);
        }

        return firstRowIndex;
    }
}
