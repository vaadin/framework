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
package com.vaadin.client.widget.grid;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.FocusUtil;
import com.vaadin.client.widgets.Grid.Editor;
import com.vaadin.client.widgets.Grid.EditorDomEvent;

/**
 * The default handler for Grid editor events. Offers several overridable
 * protected methods for easier customization.
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public class DefaultEditorEventHandler<T> implements Editor.EventHandler<T> {

    public static final int KEYCODE_OPEN = KeyCodes.KEY_ENTER;
    public static final int KEYCODE_MOVE_VERTICAL = KeyCodes.KEY_ENTER;
    public static final int KEYCODE_CLOSE = KeyCodes.KEY_ESCAPE;
    public static final int KEYCODE_MOVE_HORIZONTAL = KeyCodes.KEY_TAB;
    public static final int KEYCODE_BUFFERED_SAVE = KeyCodes.KEY_ENTER;

    private double lastTouchEventTime = 0;
    private int lastTouchEventX = -1;
    private int lastTouchEventY = -1;
    private int lastTouchEventRow = -1;

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
        }

        else if (e.getTypeInt() == Event.ONKEYDOWN) {

            int rowDelta = 0;
            int colDelta = 0;

            if (e.getKeyCode() == KEYCODE_MOVE_VERTICAL) {
                rowDelta = (e.getShiftKey() ? -1 : +1);
            } else if (e.getKeyCode() == KEYCODE_MOVE_HORIZONTAL) {
                colDelta = (e.getShiftKey() ? -1 : +1);
                // Prevent tab out of Grid Editor
                event.getDomEvent().preventDefault();
            }

            final boolean changed = rowDelta != 0 || colDelta != 0;

            if (changed) {

                int columnCount = event.getGrid().getVisibleColumns().size();

                int colIndex = event.getFocusedColumnIndex() + colDelta;
                int rowIndex = event.getRowIndex();

                // Handle row change with horizontal move when column goes out
                // of range.
                if (rowDelta == 0) {
                    if (colIndex >= columnCount
                            && rowIndex < event.getGrid().getDataSource().size()
                                    - 1) {
                        rowDelta = 1;
                        colIndex = 0;
                    } else if (colIndex < 0 && rowIndex > 0) {
                        rowDelta = -1;
                        colIndex = columnCount - 1;
                    }
                }

                editRow(event, rowIndex + rowDelta, colIndex);
            }

            return changed;
        }

        return false;
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

            editRow(event, event.getRowIndex(), event.getFocusedColumnIndex()
                    + (e.getShiftKey() ? -1 : +1));

            return true;
        } else if (e.getType().equals(BrowserEvents.KEYDOWN)
                && e.getKeyCode() == KEYCODE_BUFFERED_SAVE) {
            triggerValueChangeEvent(event);

            // Save and close.
            event.getGrid().getEditor().save();
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

    protected void editRow(EditorDomEvent<T> event, int rowIndex,
            int colIndex) {
        int rowCount = event.getGrid().getDataSource().size();
        // Limit rowIndex between 0 and rowCount - 1
        rowIndex = Math.max(0, Math.min(rowCount - 1, rowIndex));

        int colCount = event.getGrid().getVisibleColumns().size();
        // Limit colIndex between 0 and colCount - 1
        colIndex = Math.max(0, Math.min(colCount - 1, colIndex));

        if (rowIndex != event.getRowIndex()) {
            triggerValueChangeEvent(event);
        }

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
}