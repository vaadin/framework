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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.dnd.DragSourceExtension;

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

    /**
     * Style suffix for indicating that the element is being dragged.
     */
    protected static final String STYLE_SUFFIX_DRAGGED = "-dragged";

    private static final String STYLE_NAME_DRAGGABLE = "v-draggable";

    // Create event listeners
    private final EventListener dragStartListener = this::onDragStart;
    private final EventListener dragEndListener = this::onDragEnd;

    private Widget dragSourceWidget;

    @Override
    protected void extend(ServerConnector target) {
        dragSourceWidget = ((AbstractComponentConnector) target).getWidget();
        // HTML5 DnD is by default not enabled for mobile devices
        if (BrowserInfo.get().isTouchDevice() && !getConnection()
                .getUIConnector().isMobileHTML5DndEnabled()) {
            return;
        }

        addDraggable(getDraggableElement());
        addDragListeners(getDraggableElement());

        ((AbstractComponentConnector) target).onDragSourceAttached();
    }

    /**
     * Makes the given element draggable and adds class name.
     *
     * @param element
     *            Element to be set draggable.
     */
    protected void addDraggable(Element element) {
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
        AbstractComponentConnector parent = (AbstractComponentConnector) getParent();
        // if parent is null, the whole component has been removed,
        // no need to do clean up then
        if (parent != null) {
            parent.onDragSourceDetached();

            Element dragSource = getDraggableElement();

            removeDraggable(dragSource);
            removeDragListeners(dragSource);

            dragSourceWidget = null;
        }

        super.onUnregister();
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
        // work properly (doesn't fire dragend reliably)
        if (isAndoidChrome() && isNativeDragEvent(nativeEvent)) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        // Set effectAllowed parameter
        if (getState().effectAllowed != null) {
            setEffectAllowed(nativeEvent.getDataTransfer(),
                    getState().effectAllowed.getValue());
        }

        // Set drag image
        setDragImage(nativeEvent);

        // Create drag data
        Map<String, String> dataMap = createDataTransferData(nativeEvent);

        if (dataMap != null) {
            // Always set something as the text data, or DnD won't work in FF !
            dataMap.putIfAbsent(DragSourceState.DATA_TYPE_TEXT, "");

            if (!BrowserInfo.get().isIE11()) {
                // Set data to the event's data transfer
                dataMap.forEach((type, data) -> nativeEvent.getDataTransfer()
                        .setData(type, data));
            } else {
                // IE11 accepts only data with type "text"
                nativeEvent.getDataTransfer().setData(
                        DragSourceState.DATA_TYPE_TEXT,
                        dataMap.get(DragSourceState.DATA_TYPE_TEXT));
            }

            // Set style to indicate the element being dragged
            addDraggedStyle(nativeEvent);

            // Initiate firing server side dragstart event when there is a
            // DragStartListener attached on the server side
            if (hasEventListener(DragSourceState.EVENT_DRAGSTART)) {
                sendDragStartEventToServer(nativeEvent);
            }
        } else {
            // If returned data map is null, cancel drag event
            nativeEvent.preventDefault();
        }

        // Stop event bubbling
        nativeEvent.stopPropagation();
    }

    /**
     * Fixes missing or offset drag image caused by using css transform:
     * translate (or such) by using a cloned drag image element, for which the
     * property has been cleared.
     * <p>
     * This bug only occurs on Desktop with Safari (gets offset and clips the
     * element for the parts that are not inside the element start & end
     * coordinates) and Firefox (gets offset), and calling this method is NOOP
     * for any other browser.
     * <p>
     * This fix is not needed if custom drag image has been used.
     *
     * @param dragStartEvent
     *            the drag start event
     * @param draggedElement
     *            the element being dragged
     */
    protected void fixDragImageOffsetsForDesktop(NativeEvent dragStartEvent,
            Element draggedElement) {
        BrowserInfo browserInfo = BrowserInfo.get();
        final boolean isSafari = browserInfo.isSafari();
        if (browserInfo.isTouchDevice()
                || !(isSafari || browserInfo.isFirefox())) {
            return;
        }

        Element clonedElement = (Element) draggedElement.cloneNode(true);
        Style clonedStyle = clonedElement.getStyle();
        clonedStyle.clearProperty("transform");
        // only relative, absolute and fixed positions work for safari or no
        // drag image is set
        clonedStyle.setPosition(Position.RELATIVE);

        int transformXOffset = 0;
        if (isSafari) {
            transformXOffset = fixDragImageTransformForSafari(draggedElement,
                    clonedStyle);
        }

        // need to use z-index -1 or otherwise the cloned node will flash
        clonedStyle.setZIndex(-1);
        draggedElement.getParentElement().appendChild(clonedElement);

        dragStartEvent.getDataTransfer().setDragImage(clonedElement,
                WidgetUtil.getRelativeX(draggedElement, dragStartEvent)
                        - transformXOffset,
                WidgetUtil.getRelativeY(draggedElement, dragStartEvent));
        AnimationScheduler.get().requestAnimationFrame(timestamp -> {
            clonedElement.removeFromParent();
        }, clonedElement);
    }

    /**
     * Fixes missing drag image on Safari when there is
     * {@code transform: translate(x,y)} CSS used on the parent DOM for the
     * dragged element. Safari apparently doesn't take those into account, and
     * creates the drag image of the element's location without all the
     * transforms.
     * <p>
     * This is required for e.g. Grid where transforms are used to position the
     * rows and scroll the body.
     *
     * @param draggedElement
     *            the dragged element
     * @param clonedStyle
     *            the style for the cloned element
     * @return the amount of X offset that was applied to the dragged element
     *         due to transform X, needed for calculation the relative position
     *         of the drag image according to mouse position
     */
    private int fixDragImageTransformForSafari(Element draggedElement,
            Style clonedStyle) {
        int xTransformOffsetForSafari = 0;
        int yTransformOffsetForSafari = 0;
        Element parent = draggedElement.getParentElement();
        /*
         * Unfortunately, the following solution does not work when there are
         * many nested layers of transforms. It seems that the outer transforms
         * do not effect the cloned element the same way. #9408
         */
        while (parent != null) {
            ComputedStyle computedStyle = new ComputedStyle(parent);
            String transform = computedStyle.getProperty("transform");
            computedStyle = new ComputedStyle(parent);
            transform = computedStyle.getProperty("transform");
            if (transform == null || transform.isEmpty()) {
                transform = computedStyle.getProperty("-webkitTransform");
            }
            if (transform != null && !transform.isEmpty()
                    && !transform.equalsIgnoreCase("none")) {
                // matrix format is "matrix(a,b,c,d,x,y)"
                xTransformOffsetForSafari -= getMatrixValue(transform, 4);
                yTransformOffsetForSafari -= getMatrixValue(transform, 5);
            }
            parent = parent.getParentElement();
        }
        if (xTransformOffsetForSafari != 0 || yTransformOffsetForSafari != 0) {
            StringBuilder sb = new StringBuilder("translate(")
                    .append(xTransformOffsetForSafari).append("px,")
                    .append(yTransformOffsetForSafari).append("px)");
            clonedStyle.setProperty("transform", sb.toString());
        }
        // the x-offset should be taken into account when the drag image is
        // adjusted according to the mouse position. The Y-offset doesn't matter
        // for some reason (TM), at least for grid DnD, and is probably related
        // to #9408
        return xTransformOffsetForSafari;
    }

    /**
     * Parses 1-dimensional matrix (six values) values.
     *
     * @param matrix
     *            the matrix string of format {@code matrix(a,b,c,d,x,y)}
     * @param n
     *            the Nth value to parse
     * @return the value, which is in pixels, or 0 if not able to determine
     *         value from given matrix string
     */
    private static int getMatrixValue(String matrix, int n) {
        if (matrix == null || matrix.isEmpty()
                || matrix.equalsIgnoreCase("none")
                || !matrix.startsWith("matrix(")) {
            return 0;
        }
        try {
            // the matrix is e.g. "matrix(x?, y?, 0, 0, tx, ty)" (note no unit
            // postfix, e.g. 10 instead of 10px)
            String x = matrix.substring(7, matrix.length() - 1).split(",")[n]
                    .trim();
            return Integer.parseInt(x);
        } catch (NumberFormatException nfe) {
            Logger.getLogger(DragSourceExtensionConnector.class.getName())
                    .info("Unable to parse \"transform: translate(...)\" matrix "
                            + n + ". value from computed style, matrix \""
                            + matrix + "\", drag image might not be visible");
        }
        return 0;
    }

    /**
     * Fix drag image offset for touch devices when the dragged image has been
     * offset with css transform: translate/translate3d.
     * <p>
     * This necessary for e.g grid rows.
     * <p>
     * This method is NOOP for non-touch browsers.
     *
     * @param draggedElement
     *            the element that forms the drag image
     */
    protected void fixDragImageTransformForMobile(Element draggedElement) {
        if (!BrowserInfo.get().isTouchDevice()) {
            return;
        }

        Style style = draggedElement.getStyle();
        String transition = style.getProperty("transform");
        if (transition == null || transition.isEmpty()
                || !transition.startsWith("translate")) {
            return;
        }
        style.clearProperty("transform");
        AnimationScheduler.get().requestAnimationFrame(timestamp -> {
            draggedElement.getStyle().setProperty("transform", transition);
        }, draggedElement);
    }

    /**
     * Creates the data map to be set as the {@code DataTransfer} object's data.
     *
     * @param dragStartEvent
     *            The drag start event
     * @return The map from type to data, or {@code null} for not setting any
     *         data. Returning {@code null} will cancel the drag start.
     */
    protected Map<String, String> createDataTransferData(
            NativeEvent dragStartEvent) {
        Map<String, String> orderedData = new LinkedHashMap<>();
        for (String type : getState().types) {
            orderedData.put(type, getState().data.get(type));
        }
        return orderedData;
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
    protected void sendDragStartEventToServer(NativeEvent dragStartEvent) {
        getRpcProxy(DragSourceRpc.class).dragStart();
    }

    /**
     * Sets the drag image to be displayed.
     * <p>
     * Override this method in case you need custom drag image setting. Called
     * from {@link #onDragStart(Event)}.
     *
     * @param dragStartEvent
     *            The drag start event.
     */
    protected void setDragImage(NativeEvent dragStartEvent) {
        String imageUrl = getResourceUrl(DragSourceState.RESOURCE_DRAG_IMAGE);
        Element draggedElement = (Element) dragStartEvent
                .getCurrentEventTarget().cast();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image dragImage = new Image(
                    getConnection().translateVaadinUri(imageUrl));
            dragStartEvent.getDataTransfer().setDragImage(
                    dragImage.getElement(),
                    WidgetUtil.getRelativeX(draggedElement, dragStartEvent),
                    WidgetUtil.getRelativeY(draggedElement, dragStartEvent));
        } else {
            fixDragImageOffsetsForDesktop(dragStartEvent, draggedElement);
            fixDragImageTransformForMobile(draggedElement);
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
        NativeEvent nativeEvent = (NativeEvent) event;

        // for android chrome we use the polyfill, in case browser fires a
        // native dragend event after the polyfill dragend, we need to ignore
        // that one
        if (isAndoidChrome() && isNativeDragEvent((nativeEvent))) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        // Remove dragged element indicator style
        removeDraggedStyle(nativeEvent);

        // Initiate server start dragend event when there is a DragEndListener
        // attached on the server side
        if (hasEventListener(DragSourceState.EVENT_DRAGEND)) {
            String dropEffect = getDropEffect(nativeEvent.getDataTransfer());

            assert dropEffect != null : "Drop effect should never be null";

            sendDragEndEventToServer(nativeEvent,
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
    protected void sendDragEndEventToServer(NativeEvent dragEndEvent,
            DropEffect dropEffect) {
        getRpcProxy(DragSourceRpc.class).dragEnd(dropEffect);
    }

    /**
     * Add class name to indicate that the drag source element is being dragged.
     * This method is called during the dragstart event.
     *
     * @param event
     *            The drag start event.
     */
    protected void addDraggedStyle(NativeEvent event) {
        Element dragSource = getDraggableElement();
        dragSource.addClassName(
                getStylePrimaryName(dragSource) + STYLE_SUFFIX_DRAGGED);
    }

    /**
     * Remove class name that indicated that the drag source element was being
     * dragged. This method is called during the dragend event.
     *
     * @param event
     *            The drag end element.
     */
    protected void removeDraggedStyle(NativeEvent event) {
        Element dragSource = getDraggableElement();
        dragSource.removeClassName(
                getStylePrimaryName(dragSource) + STYLE_SUFFIX_DRAGGED);
    }

    /**
     * Finds the draggable element within the widget. By default, returns the
     * topmost element.
     * <p>
     * Override this method to make some other than the root element draggable
     * instead.
     * <p>
     * In case you need to make more than whan element draggable, override
     * {@link #extend(ServerConnector)} instead.
     *
     * @return the draggable element in the parent widget.
     */
    protected Element getDraggableElement() {
        return dragSourceWidget.getElement();
    }

    /**
     * Returns whether the given event is a native (android) drag start/end
     * event, and not produced by the drag-drop-polyfill.
     *
     * @param nativeEvent
     *            the event to test
     * @return {@code true} if native event, {@code false} if not (polyfill
     *         event)
     */
    protected boolean isNativeDragEvent(NativeEvent nativeEvent) {
        return isTrusted(nativeEvent) || isComposed(nativeEvent);
    }

    /**
     * Returns whether the current browser is Android Chrome.
     *
     * @return {@code true} if Android Chrome, {@code false} if not
     *
     */
    protected boolean isAndoidChrome() {
        BrowserInfo browserInfo = BrowserInfo.get();
        return browserInfo.isAndroid() && browserInfo.isChrome();
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

    /**
     * Returns the dropEffect for the given data transfer.
     *
     * @param dataTransfer
     *            the data transfer with drop effect
     * @return the currently set drop effect
     */
    protected static native String getDropEffect(DataTransfer dataTransfer)
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
