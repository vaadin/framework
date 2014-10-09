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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
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

    private Map<GridColumn<?, T>, Widget> columnToWidget = new HashMap<GridColumn<?, T>, Widget>();

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
     *             if this editor row is not enabled
     * @throws IllegalStateException
     *             if this editor row is already in edit mode
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
     * Cancels the currently active edit and hides the editor. Any changes that
     * are not {@link #commit() committed} are lost.
     * 
     * @throws IllegalStateException
     *             if this editor row is not enabled
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
        handler.cancel(new EditorRowRequest<T>(grid, rowIndex, null));
        state = State.INACTIVE;
    }

    /**
     * Commits any unsaved changes to the data source.
     * 
     * @throws IllegalStateException
     *             if this editor row is not enabled
     * @throws IllegalStateException
     *             if this editor row is not in edit mode
     */
    public void commit() {
        if (!enabled) {
            throw new IllegalStateException(
                    "Cannot commit: EditorRow is not enabled");
        }
        if (state != State.ACTIVE) {
            throw new IllegalStateException(
                    "Cannot commit: EditorRow is not in edit mode");
        }

        state = State.COMMITTING;

        handler.commit(new EditorRowRequest<T>(grid, rowIndex,
                new RequestCallback<T>() {
                    @Override
                    public void onResponse(EditorRowRequest<T> request) {
                        if (state == State.COMMITTING) {
                            state = State.ACTIVE;
                        }
                    }
                }));
    }

    /**
     * Reloads row values from the data source, discarding any unsaved changes.
     * 
     * @throws IllegalStateException
     *             if this editor row is not enabled
     * @throws IllegalStateException
     *             if this editor row is not in edit mode
     */
    public void discard() {
        if (!enabled) {
            throw new IllegalStateException(
                    "Cannot discard: EditorRow is not enabled");
        }
        if (state != State.ACTIVE) {
            throw new IllegalStateException(
                    "Cannot discard: EditorRow is not in edit mode");
        }
        handler.discard(new EditorRowRequest<T>(grid, rowIndex, null));
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
            handler.bind(new EditorRowRequest<T>(grid, rowIndex,
                    new RequestCallback<T>() {
                        @Override
                        public void onResponse(EditorRowRequest<T> request) {
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
     * Returns the editor widget associated with the given column. If the editor
     * row is not active, returns null.
     *
     * @param column
     *            the column
     * @return the widget if the editor row is open, null otherwise
     */
    protected Widget getWidget(GridColumn<?, T> column) {
        return columnToWidget.get(column);
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
                columnToWidget.put(column, editor);
                attachWidget(editor, cell);
            }
        }

        Button save = new Button();
        save.setText("Save");
        save.setStyleName("v-editor-row-save");
        save.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO should have a mechanism for handling failed commits
                commit();
                cancel();
            }
        });
        setBounds(save.getElement(), 0, tr.getOffsetHeight() + 5, 50, 25);
        attachWidget(save, editorOverlay);

        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setStyleName("v-editor-row-cancel");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancel();
            }
        });
        setBounds(cancel.getElement(), 55, tr.getOffsetHeight() + 5, 50, 25);
        attachWidget(cancel, editorOverlay);
    }

    protected void hideOverlay() {
        for (Widget w : columnToWidget.values()) {
            GridUtil.setParent(w, null);
        }
        columnToWidget.clear();

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

    private void attachWidget(Widget w, Element parent) {
        parent.appendChild(w.getElement());
        GridUtil.setParent(w, grid);
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
