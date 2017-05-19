/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.dnd.DropTargetState;
import com.vaadin.ui.dnd.DropTargetExtension;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

/**
 * Extension to add drop target functionality to a widget for using HTML5 drag
 * and drop. Client side counterpart of {@link DropTargetExtension}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(DropTargetExtension.class)
public class DropTargetExtensionConnector extends AbstractExtensionConnector {

    /**
     * Style name suffix for dragging data over the center of the drop target.
     */
    protected static final String STYLE_SUFFIX_DRAG_CENTER = "-drag-center";

    /**
     * Style name suffix for dragging data over the top part of the drop target.
     */
    protected static final String STYLE_SUFFIX_DRAG_TOP = "-drag-top";

    /**
     * Style name suffix for dragging data over the bottom part of the drop
     * target.
     */
    protected static final String STYLE_SUFFIX_DRAG_BOTTOM = "-drag-bottom";

    /**
     * Style name suffix for indicating that the element is drop target.
     */
    protected static final String STYLE_SUFFIX_DROPTARGET = "-droptarget";

    // Create event listeners
    private final EventListener dragEnterListener = this::onDragEnter;
    private final EventListener dragOverListener = this::onDragOver;
    private final EventListener dragLeaveListener = this::onDragLeave;
    private final EventListener dropListener = this::onDrop;

    private Widget dropTargetWidget;

    /**
     * Class name to apply when an element is dragged over the center of the
     * target.
     */
    private String styleDragCenter;

    @Override
    protected void extend(ServerConnector target) {
        dropTargetWidget = ((AbstractComponentConnector) target).getWidget();

        // HTML5 DnD is by default not enabled for mobile devices
        if (BrowserInfo.get().isTouchDevice() && !getConnection()
                .getUIConnector().isMobileHTML5DndEnabled()) {
            return;
        }

        addDropListeners(getDropTargetElement());

        ((AbstractComponentConnector) target).onDropTargetAttached();

        // Add drop target indicator to the drop target element
        addDropTargetStyle();
    }

    /**
     * Adds dragenter, dragover, dragleave and drop event listeners to the given
     * DOM element.
     *
     * @param element
     *            DOM element to attach event listeners to.
     */
    private void addDropListeners(Element element) {
        EventTarget target = element.cast();

        target.addEventListener(Event.DRAGENTER, dragEnterListener);
        target.addEventListener(Event.DRAGOVER, dragOverListener);
        target.addEventListener(Event.DRAGLEAVE, dragLeaveListener);
        target.addEventListener(Event.DROP, dropListener);
    }

    /**
     * Removes dragenter, dragover, dragleave and drop event listeners from the
     * given DOM element.
     *
     * @param element
     *            DOM element to remove event listeners from.
     */
    private void removeDropListeners(Element element) {
        EventTarget target = element.cast();

        target.removeEventListener(Event.DRAGENTER, dragEnterListener);
        target.removeEventListener(Event.DRAGOVER, dragOverListener);
        target.removeEventListener(Event.DRAGLEAVE, dragLeaveListener);
        target.removeEventListener(Event.DROP, dropListener);
    }

    @Override
    public void onUnregister() {
        AbstractComponentConnector parent = (AbstractComponentConnector) getParent();
        // parent is null when the component has been removed,
        // clean up only if only the extension was removed
        if (parent != null) {
            parent.onDropTargetDetached();

            removeDropListeners(getDropTargetElement());

            // Remove drop target indicator
            removeDropTargetStyle();

            dropTargetWidget = null;
        }

        super.onUnregister();
    }

    /**
     * Finds the drop target element within the widget. By default, returns the
     * topmost element.
     *
     * @return the drop target element in the parent widget.
     */
    protected Element getDropTargetElement() {
        return dropTargetWidget.getElement();
    }

    /**
     * Event handler for the {@code dragenter} event.
     * <p>
     * Override this method in case custom handling for the dragstart event is
     * required. If the drop is allowed, the event should prevent default.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragEnter(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        // Generate style name for drop target
        styleDragCenter = dropTargetWidget.getStylePrimaryName()
                + STYLE_SUFFIX_DRAG_CENTER;

        if (isDropAllowed(nativeEvent)) {
            addDragOverStyle(nativeEvent);

            setDropEffect(nativeEvent);

            // According to spec, need to call this for allowing dropping, the
            // default action would be to reject as target
            event.preventDefault();
        } else {
            // Remove drop effect
            nativeEvent.getDataTransfer()
                    .setDropEffect(DataTransfer.DropEffect.NONE);
        }
    }

    /**
     * Set the drop effect for the dragenter / dragover event, if one has been
     * set from server side.
     * <p>
     * From Moz Foundation: "You can modify the dropEffect property during the
     * dragenter or dragover events, if for example, a particular drop target
     * only supports certain operations. You can modify the dropEffect property
     * to override the user effect, and enforce a specific drop operation to
     * occur. Note that this effect must be one listed within the effectAllowed
     * property. Otherwise, it will be set to an alternate value that is
     * allowed."
     *
     * @param event
     *            the dragenter or dragover event.
     */
    private void setDropEffect(NativeEvent event) {
        if (getState().dropEffect != null) {

            DataTransfer.DropEffect dropEffect = DataTransfer.DropEffect
                    // the valueOf() needs to have equal string and name()
                    // doesn't return in all upper case
                    .valueOf(getState().dropEffect.name().toUpperCase());
            event.getDataTransfer().setDropEffect(dropEffect);
        }
    }

    /**
     * Event handler for the {@code dragover} event.
     * <p>
     * Override this method in case custom handling for the dragover event is
     * required. If the drop is allowed, the event should prevent default.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragOver(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (isDropAllowed(nativeEvent)) {
            setDropEffect(nativeEvent);

            // Add drag over indicator in case the element doesn't have one
            addDragOverStyle(nativeEvent);

            // Prevent default to allow drop
            nativeEvent.preventDefault();
            nativeEvent.stopPropagation();
        } else {
            // Remove drop effect
            nativeEvent.getDataTransfer()
                    .setDropEffect(DataTransfer.DropEffect.NONE);

            // Remove drag over indicator
            removeDragOverStyle(nativeEvent);
        }
    }

    /**
     * Event handler for the {@code dragleave} event.
     * <p>
     * Override this method in case custom handling for the dragleave event is
     * required.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragLeave(Event event) {
        removeDragOverStyle((NativeEvent) event);
    }

    /**
     * Event handler for the {@code drop} event.
     * <p>
     * Override this method in case custom handling for the drop event is
     * required. If the drop is allowed, the event should prevent default.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDrop(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (isDropAllowed(nativeEvent)) {

            JsArrayString typesJsArray = getTypes(
                    nativeEvent.getDataTransfer());

            /* Handle event if transfer doesn't contain files.
             *
             * Spec: "Dragging files can currently only happen from outside a
             * browsing context, for example from a file system manager
             * application."
             * Thus there cannot be at the same time both files and other data
             * dragged
             */
            if (!containsFiles(typesJsArray)) {
                nativeEvent.preventDefault();
                nativeEvent.stopPropagation();

                List<String> types = new ArrayList<>();
                Map<String, String> data = new HashMap<>();
                for (int i = 0; i < typesJsArray.length(); i++) {
                    String type = typesJsArray.get(i);
                    types.add(type);
                    data.put(type, nativeEvent.getDataTransfer().getData(type));
                }

                sendDropEventToServer(types, data, DragSourceExtensionConnector
                        .getDropEffect(nativeEvent.getDataTransfer()), nativeEvent);
            }

        }

        removeDragOverStyle(nativeEvent);
    }

    private boolean isDropAllowed(NativeEvent event) {
        // there never should be a drop when effect has been set to none
        if (getState().dropEffect != null
                && getState().dropEffect == DropEffect.NONE) {
            return false;
        }
        // TODO #9246: Should add verification for checking effectAllowed and
        // dropEffect from event and comparing that to target's dropEffect.
        // Currently Safari, Edge and IE don't follow the spec by allowing drop
        // if those don't match

        if (getState().dropCriteria != null) {
            return executeScript(event, getState().dropCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    /**
     * Tells if the given array of types contains files.
     * <p>
     * According to HTML specification, if any files are being dragged, {@code
     * dataTransfer.types} will contain the string "Files". See
     * https://html.spec.whatwg.org/multipage/interaction.html#the-datatransfer-interface:dom-datatransfer-types-2
     *
     * @param types
     *         Array of data types.
     * @return {@code} true if given array contains {@code "Files"}, {@code
     * false} otherwise.
     */
    private boolean containsFiles(JsArrayString types) {
        for (int i = 0; i < types.length(); i++) {
            if ("Files".equals(types.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initiates a server RPC for the drop event.
     *
     * @param types
     *            List of data types from {@code DataTransfer.types} object.
     * @param data
     *            Map containing all types and corresponding data from the
     *            {@code
     *         DataTransfer} object.
     * @param dropEffect
     *            The desired drop effect.
     */
    protected void sendDropEventToServer(List<String> types,
            Map<String, String> data, String dropEffect,
            NativeEvent dropEvent) {
        getRpcProxy(DropTargetRpc.class).drop(types, data, dropEffect);
    }

    /**
     * Add class name for the drop target element indicating that data can be
     * dropped onto it. The class name has the following format:
     *
     * <pre>
     *     [primaryStyleName]-droptarget
     * </pre>
     *
     * The added class name is update automatically by the framework when the
     * primary style name changes.
     */
    protected void addDropTargetStyle() {
        getDropTargetElement()
                .addClassName(getStylePrimaryName(getDropTargetElement())
                        + STYLE_SUFFIX_DROPTARGET);
    }

    /**
     * Remove class name from the drop target element indication that data can
     * be dropped onto it.
     */
    protected void removeDropTargetStyle() {
        getDropTargetElement()
                .removeClassName(getStylePrimaryName(getDropTargetElement())
                        + STYLE_SUFFIX_DROPTARGET);
    }

    /**
     * Add class that indicates that the component is a target while data is
     * being dragged over it.
     * <p>
     * This is triggered on {@link #onDragEnter(Event) dragenter} and
     * {@link #onDragOver(Event) dragover} events pending if the drop is
     * possible. The drop is possible if the drop effect for the target and
     * source do match and the drop criteria script evaluates to true or is not
     * set.
     *
     * @param event
     *            the dragenter or dragover event that triggered the indication.
     */
    protected void addDragOverStyle(NativeEvent event) {
        getDropTargetElement().addClassName(styleDragCenter);
    }

    /**
     * Remove the drag over indicator class name from the target element.
     * <p>
     * This is triggered on {@link #onDrop(Event) drop},
     * {@link #onDragLeave(Event) dragleave} and {@link #onDragOver(Event)
     * dragover} events pending on whether the drop has happened or if it is not
     * possible. The drop is not possible if the drop effect for the source and
     * target don't match or if there is a drop criteria script that evaluates
     * to false.
     *
     * @param event
     *            the event that triggered the removal of the indicator
     */
    protected void removeDragOverStyle(NativeEvent event) {
        getDropTargetElement().removeClassName(styleDragCenter);
    }

    private native JsArrayString getTypes(DataTransfer dataTransfer)
    /*-{
        return dataTransfer.types;
    }-*/;

    private native boolean executeScript(NativeEvent event, String script)
    /*-{
        return new Function('event', script)(event);
    }-*/;

    private native boolean getStylePrimaryName(Element element)
    /*-{
        return @com.google.gwt.user.client.ui.UIObject::getStylePrimaryName(Lcom/google/gwt/dom/client/Element;)(element);
    }-*/;

    @Override
    public DropTargetState getState() {
        return (DropTargetState) super.getState();
    }
}
