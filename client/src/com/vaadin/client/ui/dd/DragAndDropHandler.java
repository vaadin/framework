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
package com.vaadin.client.ui.dd;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.widgets.Grid;

/**
 * A simple event handler for elements that can be drag and dropped. Properly
 * handles drag start, cancel and end. For example, used in {@link Grid} column
 * header reordering.
 * <p>
 * The showing of the dragged element, drag hints and reacting to drop/cancel is
 * delegated to {@link DragAndDropCallback} implementation.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public class DragAndDropHandler {

    /**
     * Callback interface for drag and drop.
     */
    public interface DragAndDropCallback {
        /**
         * Called when the drag has started. The drag can be canceled by
         * returning {@code false}.
         * 
         * @param startEvent
         *            the original event that started the drag
         * @return {@code true} if the drag is OK to start, {@code false} to
         *         cancel
         */
        boolean onDragStart(NativeEvent startEvent);

        /**
         * Called on drag.
         * 
         * @param event
         *            the event related to the drag
         */
        void onDragUpdate(NativePreviewEvent event);

        /**
         * Called after the has ended on a drop or cancel.
         */
        void onDragEnd();

        /**
         * Called when the drag has ended on a drop.
         */
        void onDrop();

        /**
         * Called when the drag has been canceled.
         */
        void onDragCancel();
    }

    private HandlerRegistration dragStartNativePreviewHandlerRegistration;
    private HandlerRegistration dragHandlerRegistration;

    private boolean dragging;

    private DragAndDropCallback callback;

    private final NativePreviewHandler dragHandler = new NativePreviewHandler() {

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (dragging) {
                final int typeInt = event.getTypeInt();
                switch (typeInt) {
                case Event.ONKEYDOWN:
                    int keyCode = event.getNativeEvent().getKeyCode();
                    if (keyCode == KeyCodes.KEY_ESCAPE) {
                        // end drag if ESC is hit
                        cancelDrag(event);
                    }
                    break;
                case Event.ONMOUSEMOVE:
                case Event.ONTOUCHMOVE:
                    callback.onDragUpdate(event);
                    // prevent text selection on IE
                    event.getNativeEvent().preventDefault();
                    break;
                case Event.ONTOUCHCANCEL:
                    cancelDrag(event);
                    break;
                case Event.ONTOUCHEND:
                    /* Avoid simulated event on drag end */
                    event.getNativeEvent().preventDefault();
                    //$FALL-THROUGH$
                case Event.ONMOUSEUP:
                    callback.onDragUpdate(event);
                    callback.onDrop();
                    stopDrag();
                    event.cancel();
                    break;
                default:
                    break;
                }
            } else {
                stopDrag();
            }
        }

    };

    /**
     * This method can be called to trigger drag and drop on any grid element
     * that can be dragged and dropped.
     * 
     * @param dragStartingEvent
     *            the drag triggering event, usually a {@link Event#ONMOUSEDOWN}
     *            or {@link Event#ONTOUCHSTART} event on the draggable element
     * 
     * @param callback
     *            the callback that will handle actual drag and drop related
     *            operations
     */
    public void onDragStartOnDraggableElement(
            final NativeEvent dragStartingEvent,
            final DragAndDropCallback callback) {
        dragStartNativePreviewHandlerRegistration = Event
                .addNativePreviewHandler(new NativePreviewHandler() {

                    private int startX = WidgetUtil
                            .getTouchOrMouseClientX(dragStartingEvent);
                    private int startY = WidgetUtil
                            .getTouchOrMouseClientY(dragStartingEvent);

                    @Override
                    public void onPreviewNativeEvent(NativePreviewEvent event) {
                        final int typeInt = event.getTypeInt();
                        if (typeInt == -1
                                && event.getNativeEvent().getType()
                                        .toLowerCase().contains("pointer")) {
                            /*
                             * Ignore PointerEvents since IE10 and IE11 send
                             * also MouseEvents for backwards compatibility.
                             */
                            return;
                        }
                        switch (typeInt) {
                        case Event.ONMOUSEOVER:
                        case Event.ONMOUSEOUT:
                            // we don't care
                            break;
                        case Event.ONKEYDOWN:
                        case Event.ONKEYPRESS:
                        case Event.ONKEYUP:
                        case Event.ONBLUR:
                        case Event.ONFOCUS:
                            // don't cancel possible drag start
                            break;
                        case Event.ONMOUSEMOVE:
                        case Event.ONTOUCHMOVE:
                            int currentX = WidgetUtil
                                    .getTouchOrMouseClientX(event
                                            .getNativeEvent());
                            int currentY = WidgetUtil
                                    .getTouchOrMouseClientY(event
                                            .getNativeEvent());
                            if (Math.abs(startX - currentX) > 3
                                    || Math.abs(startY - currentY) > 3) {
                                removeNativePreviewHandlerRegistration();
                                startDrag(dragStartingEvent, event, callback);
                            }
                            break;
                        default:
                            // on any other events, clean up this preview
                            // listener
                            removeNativePreviewHandlerRegistration();
                            break;
                        }
                    }
                });
    }

    private void startDrag(NativeEvent startEvent,
            NativePreviewEvent triggerEvent, DragAndDropCallback callback) {
        if (callback.onDragStart(startEvent)) {
            dragging = true;
            // just capture something to prevent text selection in IE
            Event.setCapture(RootPanel.getBodyElement());
            this.callback = callback;
            dragHandlerRegistration = Event
                    .addNativePreviewHandler(dragHandler);
            callback.onDragUpdate(triggerEvent);
        }
    }

    private void stopDrag() {
        dragging = false;
        if (dragHandlerRegistration != null) {
            dragHandlerRegistration.removeHandler();
            dragHandlerRegistration = null;
        }
        Event.releaseCapture(RootPanel.getBodyElement());
        if (callback != null) {
            callback.onDragEnd();
            callback = null;
        }
    }

    private void cancelDrag(NativePreviewEvent event) {
        callback.onDragCancel();
        callback.onDragEnd();
        stopDrag();
        event.cancel();
        event.getNativeEvent().preventDefault();
    }

    private void removeNativePreviewHandlerRegistration() {
        if (dragStartNativePreviewHandlerRegistration != null) {
            dragStartNativePreviewHandlerRegistration.removeHandler();
            dragStartNativePreviewHandlerRegistration = null;
        }
    }
}
