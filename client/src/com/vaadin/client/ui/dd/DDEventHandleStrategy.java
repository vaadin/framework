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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.dd.VDragAndDropManager.DDManagerMediator;

/**
 * Strategy to handle native preview events for VDragAndDropManager.
 * 
 * The strategy could be overridden via GWT Deferred Binding mechanism.
 * 
 * @author Vaadin Ltd
 * @since 7.4.4
 */
public class DDEventHandleStrategy {

    /**
     * Returns {@code true} if {@code event} interrupts Drag and Drop.
     * 
     * @param event
     *            GWT event to handle
     * @param mediator
     *            VDragAndDropManager data accessor
     * @return whether {@code true} interrupts DnD
     */
    public boolean isDragInterrupted(NativePreviewEvent event,
            DDManagerMediator mediator) {
        int typeInt = event.getTypeInt();
        if (typeInt == Event.ONKEYDOWN) {
            int keyCode = event.getNativeEvent().getKeyCode();
            if (keyCode == KeyCodes.KEY_ESCAPE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles key down {@code event}.
     * 
     * Default implementation doesn't do anything.
     * 
     * @param event
     *            key down GWT event
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    public void handleKeyDownEvent(NativePreviewEvent event,
            DDManagerMediator mediator) {
        // no use for handling for any key down event
    }

    /**
     * Get target element for {@code event}.
     * 
     * @param event
     *            GWT event to find target
     * @param mediator
     *            VDragAndDropManager data accessor
     * @return target element for {@code event}
     */
    public Element getTargetElement(NativePreviewEvent event,
            DDManagerMediator mediator) {
        NativeEvent gwtEvent = event.getNativeEvent();
        Element targetElement;
        if (WidgetUtil.isTouchEvent(gwtEvent)
                || mediator.getManager().getDragElement() != null) {
            int x = WidgetUtil.getTouchOrMouseClientX(gwtEvent);
            int y = WidgetUtil.getTouchOrMouseClientY(gwtEvent);
            // Util.browserDebugger();
            targetElement = WidgetUtil.getElementFromPoint(x, y);
        } else {
            Node targetNode = Node.as(gwtEvent.getEventTarget());

            if (Element.is(targetNode)) {
                targetElement = Element.as(targetNode);
            } else {
                targetElement = targetNode.getParentElement();
            }
        }
        return targetElement;
    }

    /**
     * Updates drag image DOM element. This method updates drag image position
     * and adds additional styles. Default implementation hides drag element to
     * be able to get target element by the point (see
     * {@link #getTargetElement(NativePreviewEvent, DDManagerMediator)}. Method
     * {@link #restoreDragImage(String, DDManagerMediator, NativePreviewEvent)}
     * is used later on to restore the drag element in its state before
     * temporary update. Returns "display" CSS style property of the original
     * drag image. This value will be passed to the
     * {@link #restoreDragImage(String, DDManagerMediator, NativePreviewEvent)}
     * method.
     * 
     * @param event
     *            GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     * @return "display" CSS style property of drag image element to restore it
     *         later on
     */
    public String updateDragImage(NativePreviewEvent event,
            DDManagerMediator mediator) {
        VDragAndDropManager manager = mediator.getManager();
        manager.updateDragImagePosition(event.getNativeEvent(),
                manager.getDragElement());
        String display = null;
        if (manager.getDragElement() != null) {
            // to detect the "real" target, hide dragelement temporary and
            // use elementFromPoint
            display = manager.getDragElement().getStyle().getDisplay();
            manager.getDragElement().getStyle().setDisplay(Display.NONE);
        }
        return display;
    }

    /**
     * Restores drag image after temporary update by
     * {@link #updateDragImage(NativePreviewEvent, DDManagerMediator)}.
     * 
     * @param originalImageDisplay
     *            original "display" CSS style property of drag image element
     * @param mediator
     *            VDragAndDropManager data accessor
     * @param event
     *            GWT event for active DnD operation
     */
    public void restoreDragImage(String originalImageDisplay,
            DDManagerMediator mediator, NativePreviewEvent event) {
        VDragAndDropManager manager = mediator.getManager();
        if (manager.getDragElement() != null) {
            manager.getDragElement().getStyle()
                    .setProperty("display", originalImageDisplay);
        }
    }

    /**
     * Handles event when drag image element (
     * {@link VDragAndDropManager#getDragElement()} return value) is not null or
     * {@code event} is touch event.
     * 
     * If method returns {@code true} then event processing will be stoped.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param event
     *            GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     * @return {@code true} is strategy handled the event and no further steps
     *         to handle required.
     */
    public boolean handleDragImageEvent(Element target,
            NativePreviewEvent event, DDManagerMediator mediator) {
        VDragAndDropManager manager = mediator.getManager();

        // ApplicationConnection.getConsole().log(
        // "Event on dragImage, target changed");
        // special handling for events over dragImage
        // pretty much all events are mousemove althout below
        // kind of happens mouseover
        switch (event.getTypeInt()) {
        case Event.ONMOUSEOVER:
        case Event.ONMOUSEOUT:
            // ApplicationConnection
            // .getConsole()
            // .log(
            // "IGNORING proxy image event, fired because of hack or not significant");
            return true;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            VDropHandler findDragTarget = findDragTarget(target, mediator);
            if (findDragTarget != manager.getCurrentDropHandler()) {
                // dragleave on old
                handleDragLeave(mediator, true);
                // dragenter on new
                manager.setCurrentDropHandler(findDragTarget);

                handleDragEnter(target, mediator);
            } else if (findDragTarget != null) {
                handleDragOver(target, mediator);
            }
            // prevent text selection on IE
            event.getNativeEvent().preventDefault();
            return true;
        }
        return false;
    }

    /**
     * Handles drag enter on new element.
     * 
     * @param mediator
     *            VDragAndDropManager data accessor
     * @param target
     *            target element over which DnD event has happened
     */
    protected void handleDragEnter(Element target, DDManagerMediator mediator) {
        VDragAndDropManager manager = mediator.getManager();
        if (manager.getCurrentDropHandler() != null) {
            mediator.getDragEvent().setElementOver(target);
            manager.getCurrentDropHandler().dragEnter(mediator.getDragEvent());
        }
    }

    /**
     * Handles drag over on element.
     * 
     * @param mediator
     *            VDragAndDropManager data accessor
     * @param target
     *            target element over which DnD event has happened
     */
    protected void handleDragOver(Element target, DDManagerMediator mediator) {
        mediator.getDragEvent().setElementOver(target);
        mediator.getManager().getCurrentDropHandler()
                .dragOver(mediator.getDragEvent());
    }

    /**
     * Final phase of event handling.
     * 
     * @param targetElement
     *            target element over which DnD event has happened
     * @param event
     *            GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    public void handleEvent(Element targetElement, NativePreviewEvent event,
            DDManagerMediator mediator) {
        switch (event.getTypeInt()) {
        case Event.ONMOUSEOVER:
            handleMouseOver(targetElement, event, mediator);
            break;
        case Event.ONMOUSEOUT:
            handleMouseOut(targetElement, event, mediator);
            break;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            handleMouseMove(targetElement, event, mediator);
            break;
        case Event.ONTOUCHEND:
            handleTouchEnd(targetElement, event, mediator);
            break;
        case Event.ONMOUSEUP:
            handleMouseUp(targetElement, event, mediator);
            break;
        }
    }

    /**
     * Called to handle {@link Event#ONMOUSEMOVE} event.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param event
     *            ONMOUSEMOVE GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    protected void handleMouseMove(Element target, NativePreviewEvent event,
            DDManagerMediator mediator) {
        VDragAndDropManager manager = mediator.getManager();
        if (manager.getCurrentDropHandler() != null) {
            handleDragOver(target, mediator);
        }
        event.getNativeEvent().preventDefault();
    }

    /**
     * Called to handle {@link Event#ONTOUCHEND} event.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param event
     *            ONTOUCHEND GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    protected void handleTouchEnd(Element target, NativePreviewEvent event,
            DDManagerMediator mediator) {
        /* Avoid simulated event on drag end */
        event.getNativeEvent().preventDefault();
        handleMouseUp(target, event, mediator);
    }

    /**
     * Called to handle {@link Event#ONMOUSEUP} event.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param event
     *            ONMOUSEUP GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    protected void handleMouseUp(Element target, NativePreviewEvent event,
            DDManagerMediator mediator) {
        mediator.getManager().endDrag();
    }

    /**
     * Called to handle {@link Event#ONMOUSEOUT} event.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param event
     *            ONMOUSEOUT GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    protected void handleMouseOut(Element target, NativePreviewEvent event,
            DDManagerMediator mediator) {
        VDragAndDropManager manager = mediator.getManager();
        Element relatedTarget = Element.as(event.getNativeEvent()
                .getRelatedEventTarget());
        VDropHandler newDragHanler = findDragTarget(relatedTarget, mediator);
        if (manager.getDragElement() != null
                && manager.getDragElement().isOrHasChild(relatedTarget)) {
            // ApplicationConnection.getConsole().log(
            // "Mouse out of dragImage, ignored");
            return;
        }

        if (manager.getCurrentDropHandler() != newDragHanler) {
            handleDragLeave(mediator, true);
            manager.setCurrentDropHandler(null);
        }
    }

    /**
     * Handles drag leave on old element.
     * 
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    protected void handleDragLeave(DDManagerMediator mediator,
            boolean clearServerCallback) {
        VDragAndDropManager manager = mediator.getManager();
        if (manager.getCurrentDropHandler() != null) {
            manager.getCurrentDropHandler().dragLeave(mediator.getDragEvent());
            mediator.getDragEvent().getDropDetails().clear();
            if (clearServerCallback) {
                mediator.clearServerCallback();
            }
        }
    }

    /**
     * Called to handle {@link Event#ONMOUSEOVER} event.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param event
     *            ONMOUSEOVER GWT event for active DnD operation
     * @param mediator
     *            VDragAndDropManager data accessor
     */
    protected void handleMouseOver(Element target, NativePreviewEvent event,
            DDManagerMediator mediator) {
        VDragAndDropManager manager = mediator.getManager();
        VDropHandler dragHandler = findDragTarget(target, mediator);

        if (dragHandler != null
                && dragHandler != manager.getCurrentDropHandler()) {
            handleDragLeave(mediator, false);

            manager.setCurrentDropHandler(dragHandler);
            // ApplicationConnection.getConsole().log(
            // "DropHandler now"
            // + currentDropHandler.getPaintable());
            handleDragEnter(target, mediator);
        } else if (dragHandler == null
                && manager.getCurrentDropHandler() != null) {
            // ApplicationConnection.getConsole().log("Invalid state!?");
            handleDragLeave(mediator, false);
            manager.setCurrentDropHandler(null);
        }
    }

    /**
     * Find drag handler for the {@code target} element.
     * 
     * @param target
     *            target element over which DnD event has happened
     * @param mediator
     *            VDragAndDropManager data accessor
     * @return drop handler of target element
     */
    protected VDropHandler findDragTarget(Element target,
            DDManagerMediator mediator) {
        return mediator.getManager().findDragTarget(target);
    }

}
