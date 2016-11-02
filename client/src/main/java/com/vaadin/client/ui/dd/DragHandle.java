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

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.dd.DragAndDropHandler.DragAndDropCallback;

/**
 * Drag handle implementation. Drag handles are used for moving or resizing
 * widgets. This is a minimal-case component, meant to be used specifically as a
 * drag handle attached to another widget or element. As such, it does
 * <b>not</b> provide access to the events it's listening to (from the point of
 * view of this component, there really is no use for that). For the more
 * general, event-providing interface that this component is based on, see
 * {@link DragAndDropHandler}.
 *
 * @since 7.6
 */
public class DragHandle {

    /**
     * Callback interface for the DragHandle event life cycle
     */
    public interface DragHandleCallback {

        /**
         * Called when dragging starts
         */
        void onStart();

        /**
         * Called when the drag handle has moved.
         *
         * @param deltaX
         *            change in X direction since start
         * @param deltaY
         *            change in Y direction since start
         */
        void onUpdate(double deltaX, double deltaY);

        /**
         * Called when the drag operation has been cancelled (usually by
         * pressing ESC)
         */
        void onCancel();

        /**
         * Called when the drag operation completes successfully
         */
        void onComplete();

    }

    private Element parent;
    private DivElement element;
    private String baseClassName;

    private DragAndDropHandler dndHandler;
    private DragAndDropCallback dndCallback;

    private DragHandleCallback userCallback;

    /**
     * Creates a new DragHandle.
     *
     * @param baseName
     *            CSS style name to use for this DragHandle element. This
     *            parameter is supplied to the constructor (rather than added
     *            later) both to provide the "-dragged" style and to make sure
     *            that the drag handle can be properly styled (it's otherwise
     *            invisible)
     */
    public DragHandle(String baseName) {
        this(baseName,null);
    }

    /**
     * Creates a new DragHandle.
     *
     * @param baseName
     *            CSS style name to use for this DragHandle element. This
     *            parameter is supplied to the constructor (rather than added
     *            later) both to provide the "-dragged" style and to make sure
     *            that the drag handle can be properly styled (it's otherwise
     *            invisible)
     * @param callback
     *            Callback object allows hooking up the drag handle to the rest
     *            of the program logic
     */
    public DragHandle(String baseName, DragHandleCallback callback) {
        parent = null;
        element = DivElement.as(DOM.createElement("div"));
        baseClassName = baseName;
        userCallback = callback;
        addStyleName(baseClassName);

        dndCallback = new DragAndDropCallback() {

            private double startX;
            private double startY;

            @Override
            public void onDrop() {
                removeDraggingStyle();
                if(userCallback != null) {
                    userCallback.onComplete();
                }
            }

            @Override
            public void onDragUpdate(Event e) {
                if(userCallback != null) {
                    double dx = WidgetUtil.getTouchOrMouseClientX(e) - startX;
                    double dy = WidgetUtil.getTouchOrMouseClientY(e) - startY;
                    userCallback.onUpdate(dx, dy);
                }
            }

            @Override
            public boolean onDragStart(Event e) {
                addDraggingStyle();
                if(userCallback != null) {
                    startX = WidgetUtil.getTouchOrMouseClientX(e);
                    startY = WidgetUtil.getTouchOrMouseClientY(e);
                    userCallback.onStart();
                }
                return true;
            }

            @Override
            public void onDragEnd() {
                // NOP, handled in onDrop and onDragCancel
            }

            @Override
            public void onDragCancel() {
                removeDraggingStyle();
                if(userCallback != null) {
                    userCallback.onCancel();
                }
            }

            private void addDraggingStyle() {
                addStyleName(baseClassName + "-dragged");
            }

            private void removeDraggingStyle() {
                removeStyleName(baseClassName + "-dragged");
            }
        };
        dndHandler = new DragAndDropHandler();

        DOM.sinkEvents(element, Event.ONMOUSEDOWN | Event.ONTOUCHSTART);
        DOM.setEventListener(element, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                dndHandler.onDragStartOnDraggableElement(event, dndCallback);
                event.stopPropagation();
            }
        });
    }

    /**
     * Sets the user-facing drag handle callback method. This allows
     * code using the DragHandle to react to the situations where a
     * drag handle first touched, when it's moved and when it's released.
     *
     * @param dragHandleCallback the callback object to use (can be null)
     */
    public void setCallback(DragHandleCallback dragHandleCallback) {
        userCallback = dragHandleCallback;
    }

    /**
     * Returns the current parent element for this drag handle. May be null.
     *
     * @return an Element or null
     */
    public Element getParent() {
        return parent;
    }

    /**
     * Gets the element used as actual drag handle.
     *
     * @return an Element
     */
    public Element getElement() {
        return element;
    }

    /**
     * Adds this drag handle to an HTML element.
     *
     * @param elem
     *            an element
     */
    public void addTo(Element elem) {
        removeFromParent();
        parent = elem;
        parent.appendChild(element);
    }

    /**
     * Removes this drag handle from whatever it was attached to.
     */
    public void removeFromParent() {
        if (parent != null) {
            parent.removeChild(element);
            parent = null;
        }
    }

    /**
     * Adds CSS style name to the drag handle element.
     *
     * @param styleName
     *            a CSS style name
     */
    public void addStyleName(String styleName) {
        element.addClassName(styleName);
    }

    /**
     * Removes existing style name from drag handle element.
     *
     * @param styleName
     *            a CSS style name
     */
    public void removeStyleName(String styleName) {
        element.removeClassName(styleName);
    }

}
