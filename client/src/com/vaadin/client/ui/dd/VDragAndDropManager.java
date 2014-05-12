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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.Profiler;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ValueMap;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.dd.DragEventType;

/**
 * Helper class to manage the state of drag and drop event on Vaadin client
 * side. Can be used to implement most of the drag and drop operation
 * automatically via cross-browser event preview method or just as a helper when
 * implementing own low level drag and drop operation (like with HTML5 api).
 * <p>
 * Singleton. Only one drag and drop operation can be active anyways. Use
 * {@link #get()} to get instance.
 * 
 * TODO cancel drag and drop if more than one touches !?
 */
public class VDragAndDropManager {

    public static final String ACTIVE_DRAG_SOURCE_STYLENAME = "v-active-drag-source";

    private final class DefaultDragAndDropEventHandler implements
            NativePreviewHandler {

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            NativeEvent nativeEvent = event.getNativeEvent();

            int typeInt = event.getTypeInt();
            if (typeInt == Event.ONKEYDOWN) {
                int keyCode = event.getNativeEvent().getKeyCode();
                if (keyCode == KeyCodes.KEY_ESCAPE) {
                    // end drag if ESC is hit
                    interruptDrag();
                    event.cancel();
                    event.getNativeEvent().preventDefault();
                }
                // no use for handling for any key down event
                return;
            }

            currentDrag.setCurrentGwtEvent(nativeEvent);
            updateDragImagePosition();

            Node targetNode = Node.as(nativeEvent.getEventTarget());
            Element targetElement;
            if (Element.is(targetNode)) {
                targetElement = Element.as(targetNode);
            } else {
                targetElement = targetNode.getParentElement();
            }

            if (Util.isTouchEvent(nativeEvent) || dragElement != null) {
                // to detect the "real" target, hide dragelement temporary and
                // use elementFromPoint
                String display = dragElement.getStyle().getDisplay();
                dragElement.getStyle().setDisplay(Display.NONE);
                try {
                    int x = Util.getTouchOrMouseClientX(nativeEvent);
                    int y = Util.getTouchOrMouseClientY(nativeEvent);
                    // Util.browserDebugger();
                    targetElement = Util.getElementFromPoint(x, y);
                    if (targetElement == null) {
                        // ApplicationConnection.getConsole().log(
                        // "Event on dragImage, ignored");
                        event.cancel();
                        nativeEvent.stopPropagation();
                        return;

                    } else {
                        // ApplicationConnection.getConsole().log(
                        // "Event on dragImage, target changed");
                        // special handling for events over dragImage
                        // pretty much all events are mousemove althout below
                        // kind of happens mouseover
                        switch (typeInt) {
                        case Event.ONMOUSEOVER:
                        case Event.ONMOUSEOUT:
                            // ApplicationConnection
                            // .getConsole()
                            // .log(
                            // "IGNORING proxy image event, fired because of hack or not significant");
                            return;
                        case Event.ONMOUSEMOVE:
                        case Event.ONTOUCHMOVE:
                            VDropHandler findDragTarget = findDragTarget(targetElement);
                            if (findDragTarget != currentDropHandler) {
                                // dragleave on old
                                if (currentDropHandler != null) {
                                    currentDropHandler.dragLeave(currentDrag);
                                    currentDrag.getDropDetails().clear();
                                    serverCallback = null;
                                }
                                // dragenter on new
                                currentDropHandler = findDragTarget;
                                if (findDragTarget != null) {
                                    // ApplicationConnection.getConsole().log(
                                    // "DropHandler now"
                                    // + currentDropHandler
                                    // .getPaintable());
                                }

                                if (currentDropHandler != null) {
                                    currentDrag.setElementOver(targetElement);
                                    currentDropHandler.dragEnter(currentDrag);
                                }
                            } else if (findDragTarget != null) {
                                currentDrag.setElementOver(targetElement);
                                currentDropHandler.dragOver(currentDrag);
                            }
                            // prevent text selection on IE
                            nativeEvent.preventDefault();
                            return;
                        default:
                            // just update element over and let the actual
                            // handling code do the thing
                            // ApplicationConnection.getConsole().log(
                            // "Target just modified on "
                            // + event.getType());
                            currentDrag.setElementOver(targetElement);
                            break;
                        }

                    }
                } catch (RuntimeException e) {
                    // ApplicationConnection.getConsole().log(
                    // "ERROR during elementFromPoint hack.");
                    throw e;
                } finally {
                    dragElement.getStyle().setProperty("display", display);
                }
            }

            switch (typeInt) {
            case Event.ONMOUSEOVER:
                VDropHandler target = findDragTarget(targetElement);

                if (target != null && target != currentDropHandler) {
                    if (currentDropHandler != null) {
                        currentDropHandler.dragLeave(currentDrag);
                        currentDrag.getDropDetails().clear();
                    }

                    currentDropHandler = target;
                    // ApplicationConnection.getConsole().log(
                    // "DropHandler now"
                    // + currentDropHandler.getPaintable());
                    currentDrag.setElementOver(targetElement);
                    target.dragEnter(currentDrag);
                } else if (target == null && currentDropHandler != null) {
                    // ApplicationConnection.getConsole().log("Invalid state!?");
                    currentDropHandler.dragLeave(currentDrag);
                    currentDrag.getDropDetails().clear();
                    currentDropHandler = null;
                }
                break;
            case Event.ONMOUSEOUT:
                Element relatedTarget = Element.as(nativeEvent
                        .getRelatedEventTarget());
                VDropHandler newDragHanler = findDragTarget(relatedTarget);
                if (dragElement != null
                        && dragElement.isOrHasChild(relatedTarget)) {
                    // ApplicationConnection.getConsole().log(
                    // "Mouse out of dragImage, ignored");
                    return;
                }

                if (currentDropHandler != null
                        && currentDropHandler != newDragHanler) {
                    currentDropHandler.dragLeave(currentDrag);
                    currentDrag.getDropDetails().clear();
                    currentDropHandler = null;
                    serverCallback = null;
                }
                break;
            case Event.ONMOUSEMOVE:
            case Event.ONTOUCHMOVE:
                if (currentDropHandler != null) {
                    currentDrag.setElementOver(targetElement);
                    currentDropHandler.dragOver(currentDrag);
                }
                nativeEvent.preventDefault();

                break;

            case Event.ONTOUCHEND:
                /* Avoid simulated event on drag end */
                event.getNativeEvent().preventDefault();
            case Event.ONMOUSEUP:
                endDrag();
                break;

            default:
                break;
            }

        }

    }

    private static VDragAndDropManager instance;
    private HandlerRegistration handlerRegistration;
    private VDragEvent currentDrag;

    /**
     * If dragging is currently on a drophandler, this field has reference to it
     */
    private VDropHandler currentDropHandler;

    public VDropHandler getCurrentDropHandler() {
        return currentDropHandler;
    }

    /**
     * If drag and drop operation is not handled by {@link VDragAndDropManager}s
     * internal handler, this can be used to update current {@link VDropHandler}
     * .
     * 
     * @param currentDropHandler
     */
    public void setCurrentDropHandler(VDropHandler currentDropHandler) {
        this.currentDropHandler = currentDropHandler;
    }

    private VDragEventServerCallback serverCallback;

    private HandlerRegistration deferredStartRegistration;

    public static VDragAndDropManager get() {
        if (instance == null) {
            instance = GWT.create(VDragAndDropManager.class);
        }
        return instance;
    }

    /* Singleton */
    protected VDragAndDropManager() {
    }

    private final NativePreviewHandler defaultDragAndDropEventHandler = new DefaultDragAndDropEventHandler();

    /**
     * Flag to indicate if drag operation has really started or not. Null check
     * of currentDrag field is not enough as a lazy start may be pending.
     */
    private boolean isStarted;

    /**
     * This method is used to start Vaadin client side drag and drop operation.
     * Operation may be started by virtually any Widget.
     * <p>
     * Cancels possible existing drag. TODO figure out if this is always a bug
     * if one is active. Maybe a good and cheap lifesaver thought.
     * <p>
     * If possible, method automatically detects current {@link VDropHandler}
     * and fires {@link VDropHandler#dragEnter(VDragEvent)} event on it.
     * <p>
     * May also be used to control the drag and drop operation. If this option
     * is used, {@link VDropHandler} is searched on mouse events and appropriate
     * methods on it called automatically.
     * 
     * @param transferable
     * @param nativeEvent
     * @param handleDragEvents
     *            if true, {@link VDragAndDropManager} handles the drag and drop
     *            operation GWT event preview.
     * @return
     */
    public VDragEvent startDrag(VTransferable transferable,
            final NativeEvent startEvent, final boolean handleDragEvents) {
        interruptDrag();
        isStarted = false;

        currentDrag = new VDragEvent(transferable, startEvent);
        currentDrag.setCurrentGwtEvent(startEvent);

        final Command startDrag = new Command() {

            @Override
            public void execute() {
                isStarted = true;
                addActiveDragSourceStyleName();
                VDropHandler dh = null;
                if (startEvent != null) {
                    dh = findDragTarget(Element.as(currentDrag
                            .getCurrentGwtEvent().getEventTarget()));
                }
                if (dh != null) {
                    // drag has started on a DropHandler, kind of drag over
                    // happens
                    currentDropHandler = dh;
                    dh.dragEnter(currentDrag);
                }

                if (handleDragEvents) {
                    handlerRegistration = Event
                            .addNativePreviewHandler(defaultDragAndDropEventHandler);
                    if (dragElement != null
                            && dragElement.getParentElement() == null) {
                        attachDragElement();
                    }
                }
                // just capture something to prevent text selection in IE
                Event.setCapture(RootPanel.getBodyElement());
            }

            private void addActiveDragSourceStyleName() {
                ComponentConnector dragSource = currentDrag.getTransferable()
                        .getDragSource();
                dragSource.getWidget().addStyleName(
                        ACTIVE_DRAG_SOURCE_STYLENAME);
            }
        };

        final int eventType = Event.as(startEvent).getTypeInt();
        if (handleDragEvents
                && (eventType == Event.ONMOUSEDOWN || eventType == Event.ONTOUCHSTART)) {
            // only really start drag event on mousemove
            deferredStartRegistration = Event
                    .addNativePreviewHandler(new NativePreviewHandler() {

                        private int startX = Util
                                .getTouchOrMouseClientX(currentDrag
                                        .getCurrentGwtEvent());
                        private int startY = Util
                                .getTouchOrMouseClientY(currentDrag
                                        .getCurrentGwtEvent());

                        @Override
                        public void onPreviewNativeEvent(
                                NativePreviewEvent event) {
                            int typeInt = event.getTypeInt();
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
                                if (dragElement == null) {
                                    break;
                                }
                                EventTarget currentEventTarget = event
                                        .getNativeEvent()
                                        .getCurrentEventTarget();
                                if (Node.is(currentEventTarget)
                                        && !dragElement.isOrHasChild(Node
                                                .as(currentEventTarget))) {
                                    // drag image appeared below, ignore
                                    break;
                                }
                            case Event.ONKEYDOWN:
                            case Event.ONKEYPRESS:
                            case Event.ONKEYUP:
                            case Event.ONBLUR:
                            case Event.ONFOCUS:
                                // don't cancel possible drag start
                                break;
                            case Event.ONMOUSEOUT:

                                if (dragElement == null) {
                                    break;
                                }
                                EventTarget relatedEventTarget = event
                                        .getNativeEvent()
                                        .getRelatedEventTarget();
                                if (Node.is(relatedEventTarget)
                                        && !dragElement.isOrHasChild(Node
                                                .as(relatedEventTarget))) {
                                    // drag image appeared below, ignore
                                    break;
                                }
                            case Event.ONMOUSEMOVE:
                            case Event.ONTOUCHMOVE:
                                int currentX = Util
                                        .getTouchOrMouseClientX(event
                                                .getNativeEvent());
                                int currentY = Util
                                        .getTouchOrMouseClientY(event
                                                .getNativeEvent());
                                if (Math.abs(startX - currentX) > 3
                                        || Math.abs(startY - currentY) > 3) {
                                    if (deferredStartRegistration != null) {
                                        deferredStartRegistration
                                                .removeHandler();
                                        deferredStartRegistration = null;
                                    }
                                    currentDrag.setCurrentGwtEvent(event
                                            .getNativeEvent());
                                    startDrag.execute();
                                }
                                break;
                            default:
                                // on any other events, clean up the
                                // deferred drag start
                                if (deferredStartRegistration != null) {
                                    deferredStartRegistration.removeHandler();
                                    deferredStartRegistration = null;
                                }
                                currentDrag = null;
                                clearDragElement();
                                break;
                            }
                        }

                    });

        } else {
            startDrag.execute();
        }

        return currentDrag;
    }

    private void updateDragImagePosition() {
        if (currentDrag.getCurrentGwtEvent() != null && dragElement != null) {
            Style style = dragElement.getStyle();
            int clientY = Util.getTouchOrMouseClientY(currentDrag
                    .getCurrentGwtEvent());
            int clientX = Util.getTouchOrMouseClientX(currentDrag
                    .getCurrentGwtEvent());
            style.setTop(clientY, Unit.PX);
            style.setLeft(clientX, Unit.PX);
        }
    }

    /**
     * First seeks the widget from this element, then iterates widgets until one
     * implement HasDropHandler. Returns DropHandler from that.
     * 
     * @param element
     * @return
     */
    private VDropHandler findDragTarget(Element element) {
        try {
            Widget w = Util.findWidget(element, null);
            if (w == null) {
                return null;
            }
            while (!(w instanceof VHasDropHandler)
                    || !isDropEnabled((VHasDropHandler) w)) {
                w = w.getParent();
                if (w == null) {
                    break;
                }
            }
            if (w == null) {
                return null;
            } else {
                VDropHandler dh = ((VHasDropHandler) w).getDropHandler();
                return dh;
            }

        } catch (Exception e) {
            // ApplicationConnection.getConsole().log(
            // "FIXME: Exception when detecting drop handler");
            // e.printStackTrace();
            return null;
        }

    }

    /**
     * Checks if the given {@link VHasDropHandler} really is able to accept
     * drops.
     */
    private static boolean isDropEnabled(VHasDropHandler target) {
        VDropHandler dh = target.getDropHandler();
        return dh != null && dh.getConnector().isEnabled();
    }

    /**
     * Drag is ended (drop happened) on current drop handler. Calls drop method
     * on current drop handler and does appropriate cleanup.
     */
    public void endDrag() {
        endDrag(true);
    }

    /**
     * The drag and drop operation is ended, but drop did not happen. If
     * operation is currently on a drop handler, its dragLeave method is called
     * and appropriate cleanup happens.
     */
    public void interruptDrag() {
        endDrag(false);
    }

    private void endDrag(boolean doDrop) {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
        boolean sendTransferableToServer = false;
        if (currentDropHandler != null) {
            if (doDrop) {
                // we have dropped on a drop target
                sendTransferableToServer = currentDropHandler.drop(currentDrag);
                if (sendTransferableToServer) {
                    doRequest(DragEventType.DROP);
                    /*
                     * Clean active source class name deferred until response is
                     * handled. E.g. hidden on start, removed in drophandler ->
                     * would flicker in case removed eagerly.
                     */
                    final ComponentConnector dragSource = currentDrag
                            .getTransferable().getDragSource();
                    final ApplicationConnection client = currentDropHandler
                            .getApplicationConnection();
                    Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
                        @Override
                        public boolean execute() {
                            if (!client.hasActiveRequest()) {
                                removeActiveDragSourceStyleName(dragSource);
                                return false;
                            }
                            return true;
                        }

                    }, 30);

                }
            } else {
                currentDrag.setCurrentGwtEvent(null);
                currentDropHandler.dragLeave(currentDrag);
            }
            currentDropHandler = null;
            serverCallback = null;
            visitId = 0; // reset to ignore ongoing server check
        }

        /*
         * Remove class name indicating drag source when server visit is done
         * iff server visit was not initiated. Otherwise it will be removed once
         * the server visit is done.
         */
        if (!sendTransferableToServer && currentDrag != null) {
            removeActiveDragSourceStyleName(currentDrag.getTransferable()
                    .getDragSource());
        }

        currentDrag = null;

        clearDragElement();

        // release the capture (set to prevent text selection in IE)
        Event.releaseCapture(RootPanel.getBodyElement());

    }

    private void removeActiveDragSourceStyleName(ComponentConnector dragSource) {
        dragSource.getWidget().removeStyleName(ACTIVE_DRAG_SOURCE_STYLENAME);
    }

    private void clearDragElement() {
        if (dragElement != null) {
            if (dragElement.getParentElement() != null) {
                dragElement.removeFromParent();
            }
            dragElement = null;
        }
    }

    private int visitId = 0;
    private Element dragElement;

    /**
     * Visits server during drag and drop procedure. Transferable and event type
     * is given to server side counterpart of DropHandler.
     * 
     * If another server visit is started before the current is received, the
     * current is just dropped. TODO consider if callback should have
     * interrupted() method for cleanup.
     * 
     * @param acceptCallback
     */
    public void visitServer(VDragEventServerCallback acceptCallback) {
        doRequest(DragEventType.ENTER);
        serverCallback = acceptCallback;
    }

    private void doRequest(DragEventType drop) {
        if (currentDropHandler == null) {
            return;
        }
        ComponentConnector paintable = currentDropHandler.getConnector();
        ApplicationConnection client = currentDropHandler
                .getApplicationConnection();
        /*
         * For drag events we are using special id that are routed to
         * "drag service" which then again finds the corresponding DropHandler
         * on server side.
         * 
         * TODO add rest of the data in Transferable
         * 
         * TODO implement partial updates to Transferable (currently the whole
         * Transferable is sent on each request)
         */
        visitId++;
        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "visitId", visitId, false);
        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "eventId", currentDrag.getEventId(), false);
        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "dhowner", paintable, false);

        VTransferable transferable = currentDrag.getTransferable();

        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "component", transferable.getDragSource(), false);

        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "type", drop.ordinal(), false);

        if (currentDrag.getCurrentGwtEvent() != null) {
            try {
                MouseEventDetails mouseEventDetails = MouseEventDetailsBuilder
                        .buildMouseEventDetails(currentDrag
                                .getCurrentGwtEvent());
                currentDrag.getDropDetails().put("mouseEvent",
                        mouseEventDetails.serialize());
            } catch (Exception e) {
                // NOP, (at least oophm on Safari) can't serialize html dd event
                // to mouseevent
            }
        } else {
            currentDrag.getDropDetails().put("mouseEvent", null);
        }
        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "evt", currentDrag.getDropDetails(), false);

        client.updateVariable(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID,
                "tra", transferable.getVariableMap(), true);

    }

    public void handleServerResponse(ValueMap valueMap) {
        if (serverCallback == null) {
            return;
        }
        Profiler.enter("VDragAndDropManager.handleServerResponse");

        UIDL uidl = (UIDL) valueMap.cast();
        int visitId = uidl.getIntAttribute("visitId");

        if (this.visitId == visitId) {
            serverCallback.handleResponse(uidl.getBooleanAttribute("accepted"),
                    uidl);
            serverCallback = null;
        }
        runDeferredCommands();

        Profiler.leave("VDragAndDropManager.handleServerResponse");
    }

    private void runDeferredCommands() {
        if (deferredCommand != null) {
            Command command = deferredCommand;
            deferredCommand = null;
            command.execute();
            if (!isBusy()) {
                runDeferredCommands();
            }
        }
    }

    void setDragElement(Element node) {
        if (currentDrag != null) {
            if (dragElement != null && dragElement != node) {
                clearDragElement();
            } else if (node == dragElement) {
                return;
            }

            dragElement = node;
            dragElement.addClassName("v-drag-element");
            updateDragImagePosition();

            if (isStarted) {
                attachDragElement();
            }
        }
    }

    Element getDragElement() {
        return dragElement;
    }

    private void attachDragElement() {
        if (dragElement != null && dragElement.getParentElement() == null) {
            ApplicationConnection connection = getCurrentDragApplicationConnection();
            Element dragImageParent;
            if (connection == null) {
                VConsole.error("Could not determine ApplicationConnection for current drag operation. The drag image will likely look broken");
                dragImageParent = RootPanel.getBodyElement();
            } else {
                dragImageParent = VOverlay.getOverlayContainer(connection);
            }
            dragImageParent.appendChild(dragElement);
        }

    }

    private Command deferredCommand;

    private boolean isBusy() {
        return serverCallback != null;
    }

    protected ApplicationConnection getCurrentDragApplicationConnection() {
        if (currentDrag == null) {
            return null;
        }

        final ComponentConnector dragSource = currentDrag.getTransferable()
                .getDragSource();
        if (dragSource == null) {
            return null;
        }
        return dragSource.getConnection();
    }

    /**
     * Method to que tasks until all dd related server visits are done
     * 
     * @param command
     */
    private void defer(Command command) {
        deferredCommand = command;
    }

    /**
     * Method to execute commands when all existing dd related tasks are
     * completed (some may require server visit).
     * <p>
     * Using this method may be handy if criterion that uses lazy initialization
     * are used. Check
     * <p>
     * TODO Optimization: consider if we actually only need to keep the last
     * command in queue here.
     * 
     * @param command
     */
    public void executeWhenReady(Command command) {
        if (isBusy()) {
            defer(command);
        } else {
            command.execute();
        }
    }

}
