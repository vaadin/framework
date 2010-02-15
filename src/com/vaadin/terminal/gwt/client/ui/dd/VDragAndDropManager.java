package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ValueMap;

/**
 * Helper class to manage the state of drag and drop event on Vaadin client
 * side. Can be used to implement most of the drag and drop operation
 * automatically via cross-browser event preview method or just as a helper when
 * implementing own low level drag and drop operation (like with HTML5 api).
 * <p>
 * Singleton. Only one drag and drop operation can be active anyways. Use
 * {@link #get()} to get instance.
 * 
 */
public class VDragAndDropManager {

    private final class DefaultDragAndDropEventHandler implements
            NativePreviewHandler {

        public void onPreviewNativeEvent(NativePreviewEvent event) {
            NativeEvent nativeEvent = event.getNativeEvent();
            updateCurrentEvent(nativeEvent);
            updateDragImagePosition();

            int typeInt = event.getTypeInt();
            Element targetElement = (Element) nativeEvent.getEventTarget()
                    .cast();
            if (dragElement != null && dragElement.isOrHasChild(targetElement)) {

                // to detect the "real" target, hide dragelement temporary and
                // use elementFromPoint
                String display = dragElement.getStyle().getDisplay();
                dragElement.getStyle().setDisplay(Display.NONE);
                try {
                    int x = nativeEvent.getClientX();
                    int y = nativeEvent.getClientY();
                    // Util.browserDebugger();
                    targetElement = Util.getElementFromPoint(x, y);
                    if (targetElement == null) {
                        ApplicationConnection.getConsole().log(
                                "Event on dragImage, ignored");
                        event.cancel();
                        nativeEvent.stopPropagation();
                        return;

                    } else {
                        ApplicationConnection.getConsole().log(
                                "Event on dragImage, target changed");
                        // special handling for events over dragImage
                        // pretty much all events are mousemove althout below
                        // kind of happens mouseover
                        switch (typeInt) {
                        case Event.ONMOUSEOVER:
                        case Event.ONMOUSEOUT:
                            ApplicationConnection
                                    .getConsole()
                                    .log(
                                            "IGNORING proxy image event, fired because of hack or not significant");
                            return;
                        case Event.ONMOUSEMOVE:
                            VDropHandler findDragTarget = findDragTarget(targetElement);
                            if (findDragTarget != currentDropHandler) {
                                // dragleave on old
                                if (currentDropHandler != null) {
                                    currentDropHandler.dragLeave(currentDrag);
                                    serverCallback = null;
                                }
                                // dragenter on new
                                currentDropHandler = findDragTarget;
                                if (currentDropHandler != null) {
                                    currentDrag
                                            .setElementOver((com.google.gwt.user.client.Element) targetElement);
                                    currentDropHandler.dragEnter(currentDrag);
                                }
                            } else if (findDragTarget != null) {
                                currentDrag
                                        .setElementOver((com.google.gwt.user.client.Element) targetElement);
                                currentDropHandler.dragOver(currentDrag);
                            }
                            // prevent text selection on IE
                            nativeEvent.preventDefault();
                            return;
                        default:
                            // just update element over and let the actual
                            // handling code do the thing
                            ApplicationConnection.getConsole().log(
                                    "Target just modified on "
                                            + event.getType());
                            currentDrag
                                    .setElementOver((com.google.gwt.user.client.Element) targetElement);
                            break;
                        }

                    }
                } catch (Exception e) {
                    ApplicationConnection.getConsole().log(
                            "FIXME : ERROR in elementFromPoint hack.");
                    throw new RuntimeException(e);
                } finally {
                    dragElement.getStyle().setProperty("display", display);
                }
            }

            switch (typeInt) {
            case Event.ONMOUSEOVER:
                ApplicationConnection.getConsole().log(
                        event.getNativeEvent().getType());
                VDropHandler target = findDragTarget(targetElement);

                if (target != null && target != currentDropHandler) {
                    if (currentDropHandler != null) {
                        currentDropHandler.dragLeave(currentDrag);
                    }
                    currentDropHandler = target;
                    target.dragEnter(currentDrag);
                } else if (target == null && currentDropHandler != null) {
                    ApplicationConnection.getConsole().log("Invalid state!?");
                    currentDropHandler.dragLeave(currentDrag);
                    currentDropHandler = null;
                }
                break;
            case Event.ONMOUSEOUT:
                ApplicationConnection.getConsole().log(
                        event.getNativeEvent().getType());

                Element relatedTarget = (Element) nativeEvent
                        .getRelatedEventTarget().cast();
                VDropHandler newDragHanler = findDragTarget(relatedTarget);
                if (dragElement != null
                        && dragElement.isOrHasChild(relatedTarget)) {
                    ApplicationConnection.getConsole().log(
                            "Mouse out of dragImage, ignored");
                    return;
                }

                if (currentDropHandler != null
                        && currentDropHandler != newDragHanler) {
                    currentDropHandler.dragLeave(currentDrag);
                    currentDropHandler = null;
                    serverCallback = null;
                }
                break;
            case Event.ONMOUSEMOVE:
                if (currentDropHandler != null) {
                    currentDropHandler.dragOver(currentDrag);
                }
                nativeEvent.preventDefault();

                break;

            case Event.ONMOUSEUP:
                endDrag();
                break;

            default:
                break;
            }

        }

    }

    public enum DragEventType {
        ENTER, LEAVE, OVER, DROP
    }

    private static final String DD_SERVICE = "DD";

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
            instance = new VDragAndDropManager();
        }
        return instance;
    }

    /* Singleton */
    private VDragAndDropManager() {
    }

    private NativePreviewHandler defaultDragAndDropEventHandler = new DefaultDragAndDropEventHandler();

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
        updateCurrentEvent(startEvent);

        final Command startDrag = new Command() {

            public void execute() {
                isStarted = true;
                VDropHandler dh = null;
                if (startEvent != null) {
                    dh = findDragTarget((Element) currentDrag.currentGwtEvent
                            .getEventTarget().cast());
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
                        // deferred attaching drag image is on going, we can
                        // hurry with it now
                        lazyAttachDragElement.cancel();
                        lazyAttachDragElement.run();
                    }
                }
            }
        };

        if (handleDragEvents
                && Event.as(startEvent).getTypeInt() == Event.ONMOUSEDOWN) {
            // only really start drag event on mousemove
            deferredStartRegistration = Event
                    .addNativePreviewHandler(new NativePreviewHandler() {

                        public void onPreviewNativeEvent(
                                NativePreviewEvent event) {
                            int typeInt = event.getTypeInt();
                            switch (typeInt) {
                            case Event.ONMOUSEOVER:
                                if (dragElement == null
                                        || !dragElement
                                                .isOrHasChild((Node) event
                                                        .getNativeEvent()
                                                        .getCurrentEventTarget()
                                                        .cast())) {
                                    // drag image appeared below, ignore
                                    ApplicationConnection.getConsole().log(
                                            "Drag image appeared");
                                    break;
                                }
                            case Event.ONKEYDOWN:
                            case Event.ONKEYPRESS:
                            case Event.ONKEYUP:
                            case Event.ONBLUR:
                                // don't cancel possible drag start
                                break;
                            case Event.ONMOUSEOUT:

                                if (dragElement == null
                                        || !dragElement
                                                .isOrHasChild((Node) event
                                                        .getNativeEvent()
                                                        .getRelatedEventTarget()
                                                        .cast())) {
                                    // drag image appeared below, ignore
                                    ApplicationConnection.getConsole().log(
                                            "Drag image appeared");
                                    break;
                                }
                            case Event.ONMOUSEMOVE:
                                deferredStartRegistration.removeHandler();
                                deferredStartRegistration = null;
                                updateCurrentEvent(event.getNativeEvent());
                                startDrag.execute();
                                break;
                            default:
                                // on any other events, clean up the
                                // deferred drag start
                                ApplicationConnection.getConsole().log(
                                        "Drag did not start due event"
                                                + event.getNativeEvent()
                                                        .getType());

                                deferredStartRegistration.removeHandler();
                                deferredStartRegistration = null;
                                currentDrag = null;
                                if (dragElement != null) {
                                    RootPanel.getBodyElement().removeChild(
                                            dragElement);
                                    dragElement = null;
                                }
                                break;
                            }

                        }

                    });

        } else {
            startDrag.execute();
        }

        return currentDrag;
    }

    private void interruptDrag() {
        if (currentDrag != null) {
            ApplicationConnection.getConsole()
                    .log("Drag operation interrupted");
            if (currentDropHandler != null) {
                currentDrag.currentGwtEvent = null;
                currentDropHandler.dragLeave(currentDrag);
                currentDropHandler = null;
            }
            currentDrag = null;
        }
    }

    private void updateDragImagePosition() {
        if (currentDrag.currentGwtEvent != null && dragElement != null) {
            Style style = dragElement.getStyle();
            int clientY = currentDrag.currentGwtEvent.getClientY();
            int clientX = currentDrag.currentGwtEvent.getClientX();
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
        EventListener eventListener = Event.getEventListener(element);
        while (eventListener == null) {
            element = element.getParentElement();
            if (element == null) {
                break;
            }
            try {
                eventListener = Event.getEventListener(element);
            } catch (Exception e) {
                // CCE Should not happen but it does to me // MT 1.2.2010
                e.printStackTrace();
            }
        }
        if (eventListener == null) {
            ApplicationConnection.getConsole().log(
                    "No suitable DropHandler found");
            return null;
        } else {
            Widget w = (Widget) eventListener;
            while (!(w instanceof VHasDropHandler)) {
                w = w.getParent();
                if (w == null) {
                    break;
                }
            }
            if (w == null) {
                ApplicationConnection.getConsole().log(
                        "No suitable DropHandler found2");
                return null;
            } else {
                VDropHandler dh = ((VHasDropHandler) w).getDropHandler();
                if (dh == null) {
                    ApplicationConnection.getConsole().log(
                            "No suitable DropHandler found3");
                }
                return dh;
            }
        }
    }

    private void updateCurrentEvent(NativeEvent event) {
        currentDrag.currentGwtEvent = event;
    }

    public void endDrag() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
        if (currentDropHandler != null) {
            // we have dropped on a drop target
            boolean sendTransferableToServer = currentDropHandler
                    .drop(currentDrag);
            if (sendTransferableToServer) {
                doRequest(DragEventType.DROP);
            }
            currentDropHandler = null;
            serverCallback = null;

        }

        currentDrag = null;

        if (dragElement != null) {
            if (dragElement.getParentElement() != null) {
                RootPanel.getBodyElement().removeChild(dragElement);
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
        Paintable paintable = currentDropHandler.getPaintable();
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
        client.updateVariable(DD_SERVICE, "visitId", visitId, false);
        client.updateVariable(DD_SERVICE, "eventId", currentDrag.getEventId(),
                false);
        client.updateVariable(DD_SERVICE, "dhowner", paintable, false);

        VTransferable transferable = currentDrag.getTransferable();

        client.updateVariable(DD_SERVICE, "component", transferable
                .getComponent(), false);

        client.updateVariable(DD_SERVICE, "type", drop.ordinal(), false);

        if (currentDrag.currentGwtEvent != null) {
            try {
                MouseEventDetails mouseEventDetails = new MouseEventDetails(
                        currentDrag.currentGwtEvent);
                currentDrag.getDropDetails().put("mouseEvent",
                        mouseEventDetails.serialize());
            } catch (Exception e) {
                // NOP, (at least oophm on Safari) can't serialize html dd event
                // to
                // mouseevent
            }
        } else {
            currentDrag.getDropDetails().put("mouseEvent", null);
        }
        client.updateVariable(DD_SERVICE, "evt", currentDrag.getDropDetails(),
                false);

        client.updateVariable(DD_SERVICE, "tra", transferable.getVariableMap(),
                true);

    }

    public void handleServerResponse(ValueMap valueMap) {
        if (serverCallback == null) {
            return;
        }
        UIDL uidl = (UIDL) valueMap.cast();
        int visitId = uidl.getIntAttribute("visitId");

        if (this.visitId == visitId) {
            serverCallback.handleResponse(uidl.getBooleanAttribute("accepted"),
                    uidl);
            serverCallback = null;
        }
        runDeferredCommands();
    }

    private void runDeferredCommands() {
        if (deferredCommands != null && !deferredCommands.isEmpty()) {
            Command command = deferredCommands.poll();
            command.execute();
            if (!isBusy()) {
                runDeferredCommands();
            }
        }
    }

    void setDragElement(Element node) {
        if (currentDrag != null) {
            if (dragElement != null && dragElement != node) {
                RootPanel.getBodyElement().removeChild(dragElement);
            } else if (node == dragElement) {
                return;
            }

            dragElement = node;
            dragElement.addClassName("v-drag-element");
            updateDragImagePosition();

            if (isStarted) {
                lazyAttachDragElement.run();
            } else {
                /*
                 * To make our default dnd handler as compatible as possible, we
                 * need to defer the appearance of dragElement. Otherwise events
                 * that are derived from sequences of other events might not
                 * fire as domchanged will fire between them or mouse up might
                 * happen on dragElement.
                 */
                lazyAttachDragElement.schedule(300);
            }
        }
    }

    private final Timer lazyAttachDragElement = new Timer() {

        @Override
        public void run() {
            if (dragElement != null && dragElement.getParentElement() == null) {
                RootPanel.getBodyElement().appendChild(dragElement);
            }

        }
    };

    private LinkedList<Command> deferredCommands;

    private boolean isBusy() {
        return serverCallback != null;
    }

    /**
     * Method to que tasks until all dd related server visits are done
     * 
     * @param command
     */
    private void defer(Command command) {
        if (deferredCommands == null) {
            deferredCommands = new LinkedList<Command>();
        }
        deferredCommands.add(command);
    }

    /**
     * Method to execute commands when all existing dd related tasks are
     * completed (some may require server visit).
     * <p>
     * Using this method may be handy if criterion that uses lazy initialization
     * are used. Check
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
