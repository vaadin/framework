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
package com.vaadin.client.ui.grid.selection;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.Util;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.renderers.ComplexRenderer;

/* This class will probably not survive the final merge of all selection functionality. */
public class MultiSelectionRenderer extends ComplexRenderer<Boolean> {

    private class TouchEventHandler implements NativePreviewHandler {
        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            switch (event.getTypeInt()) {
            case Event.ONTOUCHSTART:
            case Event.ONTOUCHMOVE:
            case Event.ONTOUCHEND:
            case Event.ONTOUCHCANCEL:
                final Element targetElement = Element.as(event.getNativeEvent()
                        .getEventTarget());
                if (isInFirstColumn(targetElement)) {
                    dispatchTouchEvent(event);
                    event.cancel();
                }
                break;
            }
        }

        private void dispatchTouchEvent(final NativePreviewEvent event) {
            final NativeEvent nativeEvent = event.getNativeEvent();

            final int currentRow;
            if (event.getTypeInt() == Event.ONTOUCHSTART
                    || event.getTypeInt() == Event.ONTOUCHMOVE) {
                final Element touchCurrentElement = findTouchCurrentElement(nativeEvent);
                currentRow = getLogicalRowIndex(touchCurrentElement);
            } else {
                currentRow = -1;
            }

            switch (event.getTypeInt()) {
            case Event.ONTOUCHSTART:
                selectionHandler.onSelectionStart(currentRow);
                break;
            case Event.ONTOUCHMOVE:
                selectionHandler.onSelectionMove(currentRow);
                break;
            case Event.ONTOUCHEND:
                selectionHandler.onSelectionEnd();
                removeNativeHandler();
                break;
            case Event.ONTOUCHCANCEL:
                selectionHandler.onSelectionEnd();
                removeNativeHandler();
                break;
            default:
                throw new UnsupportedOperationException(
                        "Internal error: unexpected event type slipped through into our logic: "
                                + event);
            }
            event.cancel();
        }

        private Element findTouchCurrentElement(final NativeEvent nativeEvent) {
            final Touch touch = nativeEvent.getTouches().get(0);
            return Util.getElementFromPoint(touch.getClientX(),
                    touch.getClientY());
        }

        private boolean isInFirstColumn(final Element element) {
            if (element == null) {
                return false;
            }
            final Element tbody = getTbodyElement();

            if (tbody == null || !tbody.isOrHasChild(element)) {
                return false;
            }

            /*
             * The null-parent in the while clause is in the case where element
             * is an immediate tr child in the tbody. Should never happen in
             * internal code, but hey...
             */
            Element cursor = element;
            while (cursor.getParentElement() != null
                    && cursor.getParentElement().getParentElement() != tbody) {
                cursor = cursor.getParentElement();
            }

            final Element tr = cursor.getParentElement();
            return tr.getFirstChildElement().equals(cursor);
        }
    }

    private class SelectionHandler {
        /** The row index that is currently/last being manipulated. */
        private int currentRow = -1;

        /**
         * The selection mode that we are currently painting.
         * <ul>
         * <li><code>true</code> == selection painting
         * <li><code>false</code> == deselection painting
         * </ul>
         * This value's meaning is undefined while {@link #selectionInProgress}
         * is <code>false</code>.
         */
        private boolean selectionPaint = false;

        /** Whether we are painting currently or not. */
        private boolean paintingInProgress = false;

        public void onSelectionStart(final int logicalRowIndex) {
            paintingInProgress = true;
            currentRow = logicalRowIndex;
            selectionPaint = !isSelected(currentRow);
            setSelected(currentRow, selectionPaint);
        }

        public void onSelectionMove(final int logicalRowIndex) {
            if (!paintingInProgress || logicalRowIndex == currentRow) {
                return;
            }

            assert currentRow != -1 : "currentRow was uninitialized.";

            currentRow = logicalRowIndex;
            setSelected(currentRow, selectionPaint);
        }

        public void onSelectionEnd() {
            currentRow = -1;
            paintingInProgress = false;
        }
    }

    private static final String LOGICAL_ROW_PROPERTY_INT = "vEscalatorLogicalRow";

    private final Grid<?> grid;
    private HandlerRegistration nativePreviewHandlerRegistration;

    private final SelectionHandler selectionHandler = new SelectionHandler();

    public MultiSelectionRenderer(final Grid<?> grid) {
        this.grid = grid;
    }

    @Override
    public void render(final FlyweightCell cell, final Boolean data) {
        /*
         * FIXME: Once https://dev.vaadin.com/review/#/c/3670/ is merged
         * (init/destroy), split this method. Also, remove all event preview
         * handlers on detach, to avoid hanging events.
         */

        final InputElement checkbox = InputElement.as(DOM.createInputCheck());
        checkbox.setChecked(data.booleanValue());
        checkbox.setPropertyInt(LOGICAL_ROW_PROPERTY_INT, cell.getRow());
        cell.getElement().removeAllChildren();
        cell.getElement().appendChild(checkbox);
    }

    @Override
    public Collection<String> getConsumedEvents() {
        final HashSet<String> events = new HashSet<String>();
        events.add(BrowserEvents.MOUSEDOWN);
        events.add(BrowserEvents.MOUSEUP);
        events.add(BrowserEvents.MOUSEMOVE);
        events.add(BrowserEvents.TOUCHSTART);
        return events;
    }

    @Override
    public void onBrowserEvent(final Cell cell, final NativeEvent event) {
        event.preventDefault();
        event.stopPropagation();

        if (BrowserEvents.TOUCHSTART.equals(event.getType())) {
            injectNativeHandler();
            selectionHandler.onSelectionStart(cell.getRow());
            return;
        }

        final Element target = Element.as(event.getEventTarget());
        final int logicalIndex = getLogicalRowIndex(target);

        if (BrowserEvents.MOUSEDOWN.equals(event.getType())) {
            selectionHandler.onSelectionStart(logicalIndex);
        } else if (BrowserEvents.MOUSEMOVE.equals(event.getType())) {
            selectionHandler.onSelectionMove(logicalIndex);
        } else if (BrowserEvents.MOUSEUP.equals(event.getType())) {
            selectionHandler.onSelectionEnd();
        } else {
            throw new IllegalStateException("received unexpected event: "
                    + event.getType());
        }
    }

    private void injectNativeHandler() {
        removeNativeHandler();
        nativePreviewHandlerRegistration = Event
                .addNativePreviewHandler(new TouchEventHandler());
    }

    private void removeNativeHandler() {
        if (nativePreviewHandlerRegistration != null) {
            nativePreviewHandlerRegistration.removeHandler();
            nativePreviewHandlerRegistration = null;
        }
    }

    private int getLogicalRowIndex(final Element target) {
        /*
         * We can't simply go backwards until we find a <tr> first element,
         * because of the table-in-table scenario. We need to, unfortunately, go
         * up from our known root.
         */
        final Element tbody = getTbodyElement();
        Element tr = tbody.getFirstChildElement();
        while (tr != null) {
            if (tr.isOrHasChild(target)) {
                final Element td = tr.getFirstChildElement();
                assert td != null : "Cell has disappeared";

                final Element checkbox = td.getFirstChildElement();
                assert checkbox != null : "Checkbox has disappeared";

                return checkbox.getPropertyInt(LOGICAL_ROW_PROPERTY_INT);
            }
            tr = tr.getNextSiblingElement();
        }
        return -1;
    }

    private Element getTbodyElement() {
        final Element root = grid.getElement();
        final Element tablewrapper = Element.as(root.getChild(2));
        if (tablewrapper != null) {
            final TableElement table = TableElement.as(tablewrapper
                    .getFirstChildElement());
            return table.getTBodies().getItem(0);
        } else {
            return null;
        }
    }

    private boolean isSelected(final int logicalRow) {
        // TODO
        // return grid.getSelectionModel().isSelected(logicalRow);
        return false;
    }

    private void setSelected(final int logicalRow, final boolean select) {
        if (select) {
            // TODO
            // grid.getSelectionModel().select(logicalRow);
            Logger.getLogger(getClass().getName()).warning(
                    "Selecting " + logicalRow);
        } else {
            // TODO
            // grid.getSelectionModel().deselect(logicalRow);
            Logger.getLogger(getClass().getName()).warning(
                    "Deselecting " + logicalRow);
        }
    }
}
