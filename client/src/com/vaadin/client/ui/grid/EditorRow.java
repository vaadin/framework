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

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ui.grid.Escalator.AbstractRowContainer;
import com.vaadin.client.ui.grid.ScrollbarBundle.Direction;
import com.vaadin.shared.ui.grid.ScrollDestination;

/**
 * An editor UI for Grid rows. A single Grid row at a time can be opened for
 * editing.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class EditorRow<T> {

    public static final int KEYCODE_SHOW = KeyCodes.KEY_ENTER;
    public static final int KEYCODE_HIDE = KeyCodes.KEY_ESCAPE;

    public enum State {
        INACTIVE, ACTIVATING, ACTIVE, COMMITTING
    }

    private DivElement editorOverlay = DivElement.as(DOM.createDiv());

    private Grid<T> grid;

    private boolean enabled = false;
    private State state = State.INACTIVE;
    private int rowIndex = -1;
    private String styleName = null;

    public int getRow() {
        return rowIndex;
    }

    /**
     * Opens the editor over the row with the given index.
     * 
     * @param rowIndex
     *            the index of the row to be edited
     * 
     * @throws IllegalStateException
     *             if this editor row is not enabled or if it is already in edit
     *             mode
     */
    public void editRow(int rowIndex) {
        if (!enabled) {
            throw new IllegalStateException(
                    "Cannot edit row: EditorRow is not enabled");
        }
        if (state != State.INACTIVE) {
            throw new IllegalStateException(
                    "Cannot edit row: EditorRow already in edit mode");
        }

        this.rowIndex = rowIndex;

        state = State.ACTIVATING;

        boolean rowVisible = grid.getEscalator().getVisibleRowRange()
                .contains(rowIndex);

        if (rowVisible) {
            show();
        } else {
            grid.scrollToRow(rowIndex, ScrollDestination.MIDDLE);
        }
    }

    /**
     * Cancels the currently active edit and hides the editor.
     * 
     * @throws IllegalStateException
     *             if this editor row is not in edit mode
     */
    public void cancel() {
        if (!enabled) {
            throw new IllegalStateException(
                    "Cannot cancel edit: EditorRow is not enabled");
        }
        if (state == State.INACTIVE) {
            throw new IllegalStateException(
                    "Cannot cancel edit: EditorRow is not in edit mode");
        }
        hideOverlay();
        grid.getEscalator().setScrollLocked(Direction.VERTICAL, false);
        state = State.INACTIVE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled state of this editor row.
     * 
     * @param enabled
     *            true if enabled, false otherwise
     * 
     * @throws IllegalStateException
     *             if in edit mode and trying to disable
     */
    public void setEnabled(boolean enabled) {
        if (enabled == false && state != State.INACTIVE) {
            throw new IllegalStateException(
                    "Cannot disable: EditorRow is in edit mode");
        }
        this.enabled = enabled;
    }

    protected void show() {
        if (state == State.ACTIVATING) {
            state = State.ACTIVE;
            grid.getEscalator().setScrollLocked(Direction.VERTICAL, true);
            showOverlay(grid.getEscalator().getBody().getRowElement(rowIndex));
        }
    }

    protected void setGrid(final Grid<T> grid) {
        assert grid != null : "Grid cannot be null";
        assert this.grid == null : "Can only attach EditorRow to Grid once";

        this.grid = grid;

        grid.addDataAvailableHandler(new DataAvailableHandler() {
            @Override
            public void onDataAvailable(DataAvailableEvent event) {
                if (event.getAvailableRows().contains(rowIndex)) {
                    show();
                }
            }
        });
    }

    protected State getState() {
        return state;
    }

    protected void setState(State state) {
        this.state = state;
    }

    /**
     * Opens the editor overlay over the given table row.
     * 
     * @param tr
     *            the row to be edited
     */
    protected void showOverlay(TableRowElement tr) {

        DivElement tableWrapper = DivElement.as(tr.getParentElement()
                .getParentElement().getParentElement());

        AbstractRowContainer body = (AbstractRowContainer) grid.getEscalator()
                .getBody();

        int rowTop = body.getRowTop(tr);
        int bodyTop = body.getElement().getAbsoluteTop();
        int wrapperTop = tableWrapper.getAbsoluteTop();

        setBounds(editorOverlay, tr.getOffsetLeft(), rowTop + bodyTop
                - wrapperTop, tr.getOffsetWidth(), tr.getOffsetHeight());

        for (int i = 0; i < tr.getCells().getLength(); i++) {
            Element cell = createCell(tr.getCells().getItem(i));
            editorOverlay.appendChild(cell);
        }

        tableWrapper.appendChild(editorOverlay);
    }

    protected void hideOverlay() {
        editorOverlay.removeFromParent();
    }

    protected void setStylePrimaryName(String primaryName) {
        if (styleName != null) {
            editorOverlay.removeClassName(styleName);
        }
        styleName = primaryName + "-editor-row";
        editorOverlay.addClassName(styleName);
    }

    /**
     * Creates an editor row cell corresponding to the given table cell. The
     * returned element is empty and has the same dimensions and position as the
     * table cell.
     * 
     * @param td
     *            the table cell used as a reference
     * @return an editor row cell corresponding to the given cell
     */
    protected Element createCell(TableCellElement td) {
        DivElement cell = DivElement.as(DOM.createDiv());
        setBounds(cell, td.getOffsetLeft(), td.getOffsetTop(),
                td.getOffsetWidth(), td.getOffsetHeight());
        return cell;
    }

    private static void setBounds(Element e, int left, int top, int width,
            int height) {
        Style style = e.getStyle();
        style.setLeft(left, Unit.PX);
        style.setTop(top, Unit.PX);
        style.setWidth(width, Unit.PX);
        style.setHeight(height, Unit.PX);
    }
}
