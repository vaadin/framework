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

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

/**
 * Extension to add drag source functionality to a widget for using HTML5 drag
 * and drop. Client side counterpart of {@link DragSourceExtension}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(DragSourceExtension.class)
public class DragSourceExtensionConnector extends AbstractExtensionConnector {

    /**
     * Style suffix for indicating that the element is a drag source.
     */
    protected static final String STYLE_SUFFIX_DRAGSOURCE = "-dragsource";

    private static final String STYLE_NAME_DRAGGABLE = "v-draggable";

    // Create event listeners
    private final EventListener dragStartListener = this::onDragStart;
    private final EventListener dragEndListener = this::onDragEnd;

    /**
     * Widget of the drag source component.
     */
    private Widget dragSourceWidget;

    @Override
    protected void extend(ServerConnector target) {
        dragSourceWidget = ((ComponentConnector) target).getWidget();

        // HTML5 DnD is by default not enabled for mobile devices
        if (BrowserInfo.get().isTouchDevice() && !getConnection()
                .getUIConnector().isMobileHTML5DndEnabled()) {
            return;
        }

        setDraggable(getDraggableElement());
        addDragListeners(getDraggableElement());
    }

    /**
     * Sets the given element draggable and adds class name.
     *
     * @param element
     *            Element to be set draggable.
     */
    protected void setDraggable(Element element) {
        element.setDraggable(Element.DRAGGABLE_TRUE);
        element.addClassName(
                getStylePrimaryName(element) + STYLE_SUFFIX_DRAGSOURCE);
        element.addClassName(STYLE_NAME_DRAGGABLE);

    }

    /**
     * Removes draggable and class name from the given element.
     *
     * @param element
     *            Element to remove draggable from.
     */
    protected void removeDraggable(Element element) {
        element.setDraggable(Element.DRAGGABLE_FALSE);
        element.removeClassName(
                getStylePrimaryName(element) + STYLE_SUFFIX_DRAGSOURCE);
        element.removeClassName(STYLE_NAME_DRAGGABLE);
    }

    /**
     * Adds dragstart and dragend event listeners to the given DOM element.
     *
     * @param element
     *            DOM element to attach event listeners to.
     */
    protected void addDragListeners(Element element) {
        EventTarget target = element.cast();

        target.addEventListener(Event.DRAGSTART, dragStartListener);
        target.addEventListener(Event.DRAGEND, dragEndListener);
    }

    /**
     * Removes dragstart and dragend event listeners from the given DOM element.
     *
     * @param element
     *            DOM element to remove event listeners from.
     */
    protected void removeDragListeners(Element element) {
        EventTarget target = element.cast();

        target.removeEventListener(Event.DRAGSTART, dragStartListener);
        target.removeEventListener(Event.DRAGEND, dragEndListener);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        Element dragSource = getDraggableElement();

        removeDraggable(dragSource);
        removeDragListeners(dragSource);
    }

    @OnStateChange("resources")
    private void prefetchDragImage() {
        String dragImageUrl = getResourceUrl(
                DragSourceState.RESOURCE_DRAG_IMAGE);
        if (dragImageUrl != null && !dragImageUrl.isEmpty()) {
            Image.prefetch(getConnection().translateVaadinUri(dragImageUrl));
        }
    }

    /**
     * Event handler for the {@code dragstart} event. Called when {@code
     * dragstart} event occurs.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragStart(Event event) {
        // Convert elemental event to have access to dataTransfer
        NativeEvent nativeEvent = (NativeEvent) event;

        // Do not allow drag starts from native Android Chrome, since it doesn't
        // work properly (never fires dragend)
        BrowserInfo browserInfo = BrowserInfo.get();
        if (browserInfo.isAndroid() && browserInfo.isChrome()
                && isAndroidChromeNativeDragStartEvent(nativeEvent)) {
            event.preventDefault();
            return;
        }

        // Set effectAllowed parameter
        if (getState().effectAllowed != null) {
            setEffectAllowed(nativeEvent.getDataTransfer(),
                    getState().effectAllowed.getValue());
        }

        // Set drag image
        setDragImage(event);

        // Set text data parameter
        String dataTransferText = createDataTransferText(event);
        // Always set something as the text data, or DnD won't work in FF !
        if (dataTransferText == null) {
            dataTransferText = "";
        }
        nativeEvent.getDataTransfer().setData(DragSourceState.DATA_TYPE_TEXT,
                dataTransferText);

        // Initiate firing server side dragstart event when there is a
        // DragStartListener attached on the server side
        if (hasEventListener(DragSourceState.EVENT_DRAGSTART)) {
            sendDragStartEventToServer(event);
        }

        // Stop event bubbling
        nativeEvent.stopPropagation();
    }

    /**
     * Creates data of type {@code "text"} for the {@code DataTransfer} object
     * of the given event.
     *
     * @param dragStartEvent
     *            Event to set the data for.
     * @return Textual data to be set for the event or {@literal null}.
     */
    protected String createDataTransferText(Event dragStartEvent) {
        return getState().dataTransferText;
    }

    /**
     * Initiates a server RPC for the drag start event.
     * <p>
     * This method is called only if there is a server side drag start event
     * handler attached.
     *
     * @param dragStartEvent
     *            Client side dragstart event.
     */
    protected void sendDragStartEventToServer(Event dragStartEvent) {
        getRpcProxy(DragSourceRpc.class).dragStart();
    }

    /**
     * Sets the drag image to be displayed.
     *
     * @param dragStartEvent
     *            The drag start event.
     */
    protected void setDragImage(Event dragStartEvent) {
        String imageUrl = getResourceUrl(DragSourceState.RESOURCE_DRAG_IMAGE);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image dragImage = new Image(
                    getConnection().translateVaadinUri(imageUrl));
            ((NativeEvent) dragStartEvent).getDataTransfer()
                    .setDragImage(dragImage.getElement(), 0, 0);
        }
    }

    /**
     * Event handler for the {@code dragend} event. Called when {@code dragend}
     * event occurs.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragEnd(Event event) {
        // Initiate server start dragend event when there is a DragEndListener
        // attached on the server side
        if (hasEventListener(DragSourceState.EVENT_DRAGEND)) {
            String dropEffect = getDropEffect(
                    ((NativeEvent) event).getDataTransfer());

            assert dropEffect != null : "Drop effect should never be null";

            sendDragEndEventToServer(event,
                    DropEffect.valueOf(dropEffect.toUpperCase()));
        }
    }

    /**
     * Initiates a server RPC for the drag end event.
     *
     * @param dragEndEvent
     *            Client side dragend event.
     * @param dropEffect
     *            Drop effect of the dragend event, extracted from {@code
     *         DataTransfer.dropEffect} parameter.
     */
    protected void sendDragEndEventToServer(Event dragEndEvent,
            DropEffect dropEffect) {
        getRpcProxy(DragSourceRpc.class).dragEnd(dropEffect);
    }

    /**
     * Finds the draggable element within the widget. By default, returns the
     * topmost element.
     *
     * @return the draggable element in the parent widget.
     */
    protected Element getDraggableElement() {
        return dragSourceWidget.getElement();
    }

    /**
     * Returns whether the given event is a native android drag start event, and
     * not produced by the drag-drop-polyfill.
     *
     * @param nativeEvent
     *            the event to test
     * @return {@code true} if native event, {@code false} if not (polyfill
     *         event)
     */
    protected boolean isAndroidChromeNativeDragStartEvent(
            NativeEvent nativeEvent) {
        return isTrusted(nativeEvent) || isComposed(nativeEvent);
    }

    private native boolean isTrusted(NativeEvent event)
    /*-{
        return event.isTrusted;
    }-*/;

    private native boolean isComposed(NativeEvent event)
    /*-{
        return event.isComposed;
    }-*/;

    private native void setEffectAllowed(DataTransfer dataTransfer,
            String effectAllowed)
    /*-{
        dataTransfer.effectAllowed = effectAllowed;
    }-*/;

    static native String getDropEffect(DataTransfer dataTransfer)
    /*-{
        return dataTransfer.dropEffect;
    }-*/;

    @Override
    public DragSourceState getState() {
        return (DragSourceState) super.getState();
    }

    private native boolean getStylePrimaryName(Element element)
    /*-{
        return @com.google.gwt.user.client.ui.UIObject::getStylePrimaryName(Lcom/google/gwt/dom/client/Element;)(element);
    }-*/;
}
