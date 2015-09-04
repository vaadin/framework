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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.FocusUtil;
import com.vaadin.client.widget.grid.events.EditorMoveEvent;
import com.vaadin.client.widget.grid.events.EditorOpenEvent;
import com.vaadin.client.widgets.Grid.Editor;
import com.vaadin.client.widgets.Grid.EditorDomEvent;

/**
 * The default handler for Grid editor events. Offers several overridable
 * protected methods for easier customization.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class DefaultEditorEventHandler<T> implements Editor.EventHandler<T> {

    public static final int KEYCODE_OPEN = KeyCodes.KEY_ENTER;
    public static final int KEYCODE_MOVE = KeyCodes.KEY_ENTER;
    public static final int KEYCODE_CLOSE = KeyCodes.KEY_ESCAPE;

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
                || (e.getTypeInt() == Event.ONKEYDOWN && e.getKeyCode() == KEYCODE_OPEN)
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

            // FIXME should be in editRow
            event.getGrid().fireEvent(new EditorOpenEvent(cell));

            event.getDomEvent().preventDefault();

            return true;
        }
        return false;
    }

    /**
     * Moves the editor to another row if the received event is a move event.
     * The default implementation moves the editor to the clicked row if the
     * event is a click; otherwise, if the event is a keydown and the keycode is
     * {@link #KEYCODE_MOVE}, moves the editor one row up or down if the shift
     * key is pressed or not, respectively.
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

            // FIXME should be in editRow
            event.getGrid().fireEvent(new EditorMoveEvent(cell));

            return true;
        }

        else if (e.getTypeInt() == Event.ONKEYDOWN
                && e.getKeyCode() == KEYCODE_MOVE) {

            editRow(event, event.getRowIndex() + (e.getShiftKey() ? -1 : +1),
                    event.getFocusedColumnIndex());

            // FIXME should be in editRow
            event.getGrid().fireEvent(new EditorMoveEvent(cell));

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

    protected void editRow(EditorDomEvent<T> event, int rowIndex, int colIndex) {
        int rowCount = event.getGrid().getDataSource().size();
        // Limit rowIndex between 0 and rowCount - 1
        rowIndex = Math.max(0, Math.min(rowCount - 1, rowIndex));

        int colCount = event.getGrid().getVisibleColumns().size();
        // Limit colIndex between 0 and colCount - 1
        colIndex = Math.max(0, Math.min(colCount - 1, colIndex));

        event.getEditor().editRow(rowIndex, colIndex);
    }

    @Override
    public boolean handleEvent(EditorDomEvent<T> event) {
        final Editor<T> editor = event.getEditor();
        final boolean isBody = event.getCell().isBody();

        if (event.getGrid().isEditorActive()) {
            return (!editor.isBuffered() && isBody && handleMoveEvent(event))
                    || handleCloseEvent(event)
                    // Swallow events if editor is open and buffered (modal)
                    || editor.isBuffered();
        } else {
            return event.getGrid().isEnabled() && isBody
                    && handleOpenEvent(event);
        }
    }
}