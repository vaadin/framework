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
import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.grid.EditorRowHandler.EditorRowRequest;
import com.vaadin.client.ui.grid.EditorRowHandler.EditorRowRequest.RequestCallback;
import com.vaadin.client.ui.grid.Escalator.AbstractRowContainer;
import com.vaadin.client.ui.grid.Grid.SelectionColumn;
import com.vaadin.client.ui.grid.ScrollbarBundle.Direction;
import com.vaadin.client.ui.grid.events.ScrollEvent;
import com.vaadin.client.ui.grid.events.ScrollHandler;
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

    private Grid<T> grid;

    private EditorRowHandler<T> handler;

    private DivElement editorOverlay = DivElement.as(DOM.createDiv());

    private List<Widget> editorWidgets = new ArrayList<Widget>();

    private boolean enabled = false;
    private State state = State.INACTIVE;
    private int rowIndex = -1;
    private String styleName = null;

    private HandlerRegistration scrollHandler;

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

        if (grid.getEscalator().getVisibleRowRange().contains(rowIndex)) {
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
        handler.cancel(new EditorRowRequest(rowIndex, null));
        state = State.INACTIVE;
    }

    /**
     * Returns the handler responsible for binding data and editor widgets to
     * this editor row.
     * 
     * @return the editor row handler or null if not set
     */
    public EditorRowHandler<T> getHandler() {
        return handler;
    }

    /**
     * Sets the handler responsible for binding data and editor widgets to this
     * editor row.
     * 
     * @param rowHandler
     *            the new editor row handler
     * 
     * @throws IllegalStateException
     *             if this editor row is currently in edit mode
     */
    public void setHandler(EditorRowHandler<T> rowHandler) {
        if (state != State.INACTIVE) {
            throw new IllegalStateException(
                    "Cannot set EditorRowHandler: EditorRow is currently in edit mode");
        }
        this.handler = rowHandler;
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
     * @throws IllegalStateException
     *             if the editor row handler is not set
     */
    public void setEnabled(boolean enabled) {
        if (enabled == false && state != State.INACTIVE) {
            throw new IllegalStateException(
                    "Cannot disable: EditorRow is in edit mode");
        } else if (enabled == true && getHandler() == null) {
            throw new IllegalStateException(
                    "Cannot enable: EditorRowHandler not set");
        }
        this.enabled = enabled;
    }

    protected void show() {
        if (state == State.ACTIVATING) {
            handler.bind(new EditorRowRequest(rowIndex, new RequestCallback() {
                @Override
                public void onResponse(EditorRowRequest request) {
                    if (state == State.ACTIVATING) {
                        state = State.ACTIVE;
                        showOverlay(grid.getEscalator().getBody()
                                .getRowElement(request.getRowIndex()));
                    }
                }
            }));
            grid.getEscalator().setScrollLocked(Direction.VERTICAL, true);
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

        updateHorizontalScrollPosition();

        scrollHandler = grid.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                updateHorizontalScrollPosition();
            }
        });

        tableWrapper.appendChild(editorOverlay);

        for (int i = 0; i < tr.getCells().getLength(); i++) {
            Element cell = createCell(tr.getCells().getItem(i));

            editorOverlay.appendChild(cell);

            GridColumn<?, T> column = grid.getColumnFromVisibleIndex(i);
            if (column instanceof SelectionColumn) {
                continue;
            }

            Widget editor = getHandler().getWidget(column);
            if (editor != null) {
                editorWidgets.add(editor);
                cell.appendChild(editor.getElement());
                Grid.setParent(editor, grid);
            }
        }
    }

    protected void hideOverlay() {
        for (Widget w : editorWidgets) {
            Grid.setParent(w, null);
        }
        editorWidgets.clear();

        editorOverlay.removeAllChildren();
        editorOverlay.removeFromParent();

        scrollHandler.removeHandler();
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

    private void updateHorizontalScrollPosition() {
        editorOverlay.getStyle().setLeft(-grid.getScrollLeft(), Unit.PX);
    }
}
