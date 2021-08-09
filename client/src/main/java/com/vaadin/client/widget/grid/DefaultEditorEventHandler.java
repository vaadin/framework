/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.widget.grid;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.FocusUtil;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Editor;
import com.vaadin.client.widgets.Grid.EditorDomEvent;

/**
 * The default handler for Grid editor events. Offers several overridable
 * protected methods for easier customization.
 *
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class DefaultEditorEventHandler<T> implements Editor.EventHandler<T> {

    /** Default key code for showing the editor. */
    public static final int KEYCODE_OPEN = KeyCodes.KEY_ENTER;
    /** Default key code for moving the editor up or down. */
    public static final int KEYCODE_MOVE_VERTICAL = KeyCodes.KEY_ENTER;
    /** Default key code for hiding the editor. */
    public static final int KEYCODE_CLOSE = KeyCodes.KEY_ESCAPE;
    /** Default key code for moving cursor horizontally within the editor. */
    public static final int KEYCODE_MOVE_HORIZONTAL = KeyCodes.KEY_TAB;
    /** Default key code for triggering save in buffered mode. */
    public static final int KEYCODE_BUFFERED_SAVE = KeyCodes.KEY_ENTER;

    private double lastTouchEventTime = 0;
    private int lastTouchEventX = -1;
    private int lastTouchEventY = -1;
    private int lastTouchEventRow = -1;
    private PendingEdit pendingEdit;

    /**
     * Returns whether the given event is a touch event that should open the
     * editor.
     *
     * @param event
     *            the received event
     * @return whether the event is a touch open event
     */
    protected boolean isTouchOpenEvent(EditorDomEvent<T> event) {
        final Event e = event.getDomEvent();
        final int type = e.getTypeInt();

        final double now = Duration.currentTimeMillis();
        final int currentX = WidgetUtil.getTouchOrMouseClientX(e);
        final int currentY = WidgetUtil.getTouchOrMouseClientY(e);

        final boolean validTouchOpenEvent = type == Event.ONTOUCHEND
                && now - lastTouchEventTime < 500
                && lastTouchEventRow == event.getCell().getRowIndex()
                && Math.abs(lastTouchEventX - currentX) < 20
                && Math.abs(lastTouchEventY - currentY) < 20;

        if (type == Event.ONTOUCHSTART) {
            lastTouchEventX = currentX;
            lastTouchEventY = currentY;
        }

        if (type == Event.ONTOUCHEND) {
            lastTouchEventTime = now;
            lastTouchEventRow = event.getCell().getRowIndex();
        }

        return validTouchOpenEvent;
    }

    /**
     * Returns whether the given event should open the editor. The default
     * implementation returns true if and only if the event is a doubleclick or
     * if it is a keydown event and the keycode is {@link #KEYCODE_OPEN}.
     *
     * @param event
     *            the received event
     * @return true if the event is an open event, false otherwise
     */
    protected boolean isOpenEvent(EditorDomEvent<T> event) {
        final Event e = event.getDomEvent();
        return e.getTypeInt() == Event.ONDBLCLICK
                || (e.getTypeInt() == Event.ONKEYDOWN
                        && e.getKeyCode() == KEYCODE_OPEN)
                || isTouchOpenEvent(event);
    }

    /**
     * Opens the editor on the appropriate row if the received event is an open
     * event. The default implementation uses
     * {@link #isOpenEvent(EditorDomEvent) isOpenEvent}.
     *
     * @param event
     *            the received event
     * @return true if this method handled the event and nothing else should be
     *         done, false otherwise
     */
    protected boolean handleOpenEvent(EditorDomEvent<T> event) {
        if (isOpenEvent(event)) {
            final EventCellReference<T> cell = event.getCell();

            editRow(event, cell.getRowIndex(), cell.getColumnIndexDOM());

            event.getDomEvent().preventDefault();

            return true;
        }
        return false;
    }

    /**
     * Specifies the direction at which the focus should move.
     */
    public enum CursorMoveDelta {
        /** Move focus one step to the direction indicated by name. */
        UP(-1, 0), RIGHT(0, 1), DOWN(1, 0), LEFT(0, -1);

        /** Vertical change. */
        public final int rowDelta;
        /** Horizontal change. */
        public final int colDelta;

        CursorMoveDelta(int rowDelta, int colDelta) {
            this.rowDelta = rowDelta;
            this.colDelta = colDelta;
        }

        /**
         * Returns whether the cursor move has either horizontal or vertical
         * changes.
         *
         * @return {@code true} if there are changes, {@code false} otherwise
         */
        public boolean isChanged() {
            return rowDelta != 0 || colDelta != 0;
        }
    }

    /**
     * Returns the direction to which the cursor should move.
     *
     * @param event
     *            the mouse event, not null.
     * @return the direction. May return null if the cursor should not move.
     */
    protected CursorMoveDelta getDeltaFromKeyDownEvent(
            EditorDomEvent<T> event) {
        Event e = event.getDomEvent();
        if (e.getKeyCode() == KEYCODE_MOVE_VERTICAL) {
            return e.getShiftKey() ? CursorMoveDelta.UP : CursorMoveDelta.DOWN;
        } else if (e.getKeyCode() == KEYCODE_MOVE_HORIZONTAL) {
            // Prevent tab out of Grid Editor
            event.getDomEvent().preventDefault();
            return e.getShiftKey() ? CursorMoveDelta.LEFT
                    : CursorMoveDelta.RIGHT;
        }
        return null;
    }

    /**
     * Moves the editor to another row or another column if the received event
     * is a move event. The default implementation moves the editor to the
     * clicked row if the event is a click; otherwise, if the event is a keydown
     * and the keycode is {@link #KEYCODE_MOVE_VERTICAL}, moves the editor one
     * row up or down if the shift key is pressed or not, respectively. Keydown
     * event with keycode {@link #KEYCODE_MOVE_HORIZONTAL} moves the editor left
     * or right if shift key is pressed or not, respectively.
     *
     * @param event
     *            the received event
     * @return true if this method handled the event and nothing else should be
     *         done, false otherwise
     */
    protected boolean handleMoveEvent(EditorDomEvent<T> event) {
        Event e = event.getDomEvent();
        final EventCellReference<T> cell = event.getCell();

        // TODO: Move on touch events
        if (e.getTypeInt() == Event.ONCLICK) {

            editRow(event, cell.getRowIndex(), cell.getColumnIndexDOM());

            return true;
        } else if (e.getTypeInt() == Event.ONKEYDOWN) {

            CursorMoveDelta delta = getDeltaFromKeyDownEvent(event);
            final boolean changed = delta != null;

            if (changed) {

                int columnCount = event.getGrid().getVisibleColumns().size();

                int colIndex = delta.colDelta > 0
                        ? findNextEditableColumnIndex(event.getGrid(),
                                event.getFocusedColumnIndex() + delta.colDelta)
                        : findPrevEditableColumnIndex(event.getGrid(),
                                event.getFocusedColumnIndex() + delta.colDelta);
                int rowIndex = event.getRowIndex();

                // Handle row change with horizontal move when column goes out
                // of range.
                if (delta.rowDelta == 0 && colIndex < 0) {
                    if (delta.colDelta > 0
                            && rowIndex < event.getGrid().getDataSource().size()
                                    - 1) {
                        delta = CursorMoveDelta.DOWN;
                        colIndex = findNextEditableColumnIndex(event.getGrid(),
                                0);
                    } else if (delta.colDelta < 0 && rowIndex > 0) {
                        delta = CursorMoveDelta.UP;
                        colIndex = findPrevEditableColumnIndex(event.getGrid(),
                                columnCount - 1);
                    }
                }

                int newRowIndex = rowIndex + delta.rowDelta;
                if (newRowIndex != event.getRowIndex()) {
                    triggerValueChangeEvent(event);
                    // disable until validity check is done
                    setWidgetEnabled(event.getEditorWidget(), false);
                    event.getEditor().getHandler().checkValidity();
                    pendingEdit = new PendingEdit(event, newRowIndex, colIndex);
                } else {
                    editRow(event, newRowIndex, colIndex);
                }
            }

            return changed;
        }

        return false;
    }

    /**
     * Finds index of the first editable column, starting at the specified
     * index.
     *
     * @param grid
     *            the current grid, not null.
     * @param startingWith
     *            start with this column. Index into the
     *            {@link Grid#getVisibleColumns()}.
     * @return the index of the nearest visible column; may return the
     *         <code>startingWith</code> itself. Returns -1 if there is no such
     *         column.
     */
    protected int findNextEditableColumnIndex(Grid<T> grid, int startingWith) {
        final List<Grid.Column<?, T>> columns = grid.getVisibleColumns();
        for (int i = startingWith; i < columns.size(); i++) {
            if (isEditable(grid, columns.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks whether the field within the given editor column is editable.
     *
     * @param grid
     *            the grid that is being edited
     * @param column
     *            the column to investigate
     * @return {@code true} if the field is editable, {@code false} otherwise
     */
    protected boolean isEditable(Grid<T> grid, Grid.Column<?, T> column) {
        if (!column.isEditable()) {
            return false;
        }

        // figure out whether the widget nested in the editor cell is editable.
        // if it is disabled or read-only then it is not editable.

        final Widget editorCell = grid.getEditorWidget(column);
        final ComponentConnector connector = Util.findConnectorFor(editorCell);
        if (connector == null) {
            // not a Vaadin Connector, perhaps something generated by the
            // renderer? Assume it's enabled.
            return true;
        }

        if (!connector.isEnabled()) {
            return false;
        }
        if (connector instanceof AbstractFieldConnector) {
            final AbstractFieldConnector field = (AbstractFieldConnector) connector;
            if (field.isReadOnly()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds index of the last editable column, searching backwards starting at
     * the specified index.
     *
     * @param grid
     *            the current grid, not null.
     * @param startingWith
     *            start with this column. Index into the
     *            {@link Grid#getVisibleColumns()}.
     * @return the index of the nearest visible column; may return the
     *         <code>startingWith</code> itself. Returns -1 if there is no such
     *         column.
     */
    protected int findPrevEditableColumnIndex(Grid<T> grid, int startingWith) {
        final List<Grid.Column<?, T>> columns = grid.getVisibleColumns();
        for (int i = startingWith; i >= 0; i--) {
            if (isEditable(grid, columns.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Moves the editor to another column if the received event is a move event.
     * By default the editor is moved on a keydown event with keycode
     * {@link #KEYCODE_MOVE_HORIZONTAL}. This moves the editor left or right if
     * shift key is pressed or not, respectively.
     *
     * @param event
     *            the received event
     * @return true if this method handled the event and nothing else should be
     *         done, false otherwise
     */
    protected boolean handleBufferedMoveEvent(EditorDomEvent<T> event) {
        Event e = event.getDomEvent();

        if (e.getType().equals(BrowserEvents.CLICK)
                && event.getRowIndex() == event.getCell().getRowIndex()) {

            editRow(event, event.getRowIndex(),
                    event.getCell().getColumnIndexDOM());

            return true;

        } else if (e.getType().equals(BrowserEvents.KEYDOWN)
                && e.getKeyCode() == KEYCODE_MOVE_HORIZONTAL) {

            // Prevent tab out of Grid Editor
            event.getDomEvent().preventDefault();

            final int newColIndex = e.getShiftKey()
                    ? findPrevEditableColumnIndex(event.getGrid(),
                            event.getFocusedColumnIndex() - 1)
                    : findNextEditableColumnIndex(event.getGrid(),
                            event.getFocusedColumnIndex() + 1);

            if (newColIndex >= 0) {
                editRow(event, event.getRowIndex(), newColIndex);
            }

            return true;
        } else if (e.getType().equals(BrowserEvents.KEYDOWN)
                && e.getKeyCode() == KEYCODE_BUFFERED_SAVE) {
            triggerValueChangeEvent(event);

            // Save and close.
            event.getGrid().getEditor().save();
            FocusUtil.setFocus(event.getGrid(), true);
            return true;
        }

        return false;
    }

    /**
     * Returns whether the given event should close the editor. The default
     * implementation returns true if and only if the event is a keydown event
     * and the keycode is {@link #KEYCODE_CLOSE}.
     *
     * @param event
     *            the received event
     * @return true if the event is a close event, false otherwise
     */
    protected boolean isCloseEvent(EditorDomEvent<T> event) {
        final Event e = event.getDomEvent();
        return e.getTypeInt() == Event.ONKEYDOWN
                && e.getKeyCode() == KEYCODE_CLOSE;
    }

    /**
     * Closes the editor if the received event is a close event. The default
     * implementation uses {@link #isCloseEvent(EditorDomEvent) isCloseEvent}.
     *
     * @param event
     *            the received event
     * @return true if this method handled the event and nothing else should be
     *         done, false otherwise
     */
    protected boolean handleCloseEvent(EditorDomEvent<T> event) {
        if (isCloseEvent(event)) {
            event.getEditor().cancel();
            FocusUtil.setFocus(event.getGrid(), true);
            return true;
        }
        return false;
    }

    /**
     * Opens the editor over the row with the given index and attempts to focus
     * the editor widget in the given column index. If the given indices are
     * outside of the existing range, the closest value within the range is
     * used.
     *
     * @param event
     *            the wrapped DOM event
     * @param rowIndex
     *            index of the row to edit
     * @param colIndex
     *            index of the editor column to focus
     */
    protected void editRow(EditorDomEvent<T> event, int rowIndex,
            int colIndex) {
        int rowCount = event.getGrid().getDataSource().size();
        // Limit rowIndex between 0 and rowCount - 1
        rowIndex = Math.max(0, Math.min(rowCount - 1, rowIndex));

        int colCount = event.getGrid().getVisibleColumns().size();
        // Limit colIndex between 0 and colCount - 1
        colIndex = Math.max(0, Math.min(colCount - 1, colIndex));

        event.getEditor().editRow(rowIndex, colIndex);
    }

    /**
     * Triggers a value change event from the editor field if it has focus. This
     * is based on the assumption that editor field will fire the value change
     * when a blur event occurs.
     *
     * @param event
     *            the editor DOM event
     */
    private void triggerValueChangeEvent(EditorDomEvent<T> event) {
        // Force a blur to cause a value change event
        Widget editorWidget = event.getEditorWidget();
        if (editorWidget != null) {
            Element focusedElement = WidgetUtil.getFocusedElement();
            if (editorWidget.getElement().isOrHasChild(focusedElement)) {
                focusedElement.blur();
                focusedElement.focus();
            }
        }
    }

    @Override
    public boolean handleEvent(EditorDomEvent<T> event) {
        final Editor<T> editor = event.getEditor();
        final boolean isBody = event.getCell().isBody();

        final boolean handled;
        if (event.getGrid().isEditorActive()) {
            handled = handleCloseEvent(event)
                    || (!editor.isBuffered() && isBody
                            && handleMoveEvent(event))
                    || (editor.isBuffered() && isBody
                            && handleBufferedMoveEvent(event));
        } else {
            handled = event.getGrid().isEnabled() && isBody
                    && handleOpenEvent(event);
        }

        // Buffered mode should swallow all events, if not already handled.
        boolean swallowEvent = event.getGrid().isEditorActive()
                && editor.isBuffered();

        return handled || swallowEvent;
    }

    @Override
    public void confirmValidity(boolean isValid) {
        if (pendingEdit == null) {
            getLogger().log(Level.SEVERE,
                    "An editor's validation confirmation was received, but"
                            + " no pending edit object was found ");
            return;
        }
        setWidgetEnabled(pendingEdit.pendingEvent.getEditorWidget(), true);
        if (isValid) {
            editRow(pendingEdit.pendingEvent, pendingEdit.pendingRowIndex,
                    pendingEdit.pendingColIndex);
        } else {
            pendingEdit.pendingEvent.getEditorWidget().getElement().focus();
        }

        pendingEdit = null;
    }

    private void setWidgetEnabled(Widget widget, boolean widgetEnabled) {
        final ComponentConnector connector = Util.findConnectorFor(widget);
        // only enable widget if it hasn't been disabled programmatically
        if (connector.getState().enabled) {
            connector.setWidgetEnabled(widgetEnabled);
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(DefaultEditorEventHandler.class.getName());
    }

    private final class PendingEdit {
        private EditorDomEvent<T> pendingEvent;
        private int pendingRowIndex;
        private int pendingColIndex;

        private PendingEdit(EditorDomEvent<T> pendingEvent, int pendingRowIndex,
                int pendingColIndex) {
            if (pendingEvent == null) {
                throw new IllegalArgumentException(
                        "The pending event cannot be null");
            }
            this.pendingEvent = pendingEvent;
            this.pendingRowIndex = pendingRowIndex;
            this.pendingColIndex = pendingColIndex;
        }

    }
}
