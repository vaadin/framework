package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
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
public class DragAndDropManager {

    public enum DragEventType {
        ENTER, LEAVE, OVER, DROP
    }

    private static final String DD_SERVICE = "DD";

    private static DragAndDropManager instance;
    private HandlerRegistration handlerRegistration;
    private DragEvent currentDrag;

    /**
     * If dragging is currently on a drophandler, this field has reference to it
     */
    private DropHandler currentDropHandler;

    public DropHandler getCurrentDropHandler() {
        return currentDropHandler;
    }

    /**
     * If drag and drop operation is not handled by {@link DragAndDropManager}s
     * internal handler, this can be used to update current {@link DropHandler}.
     * 
     * @param currentDropHandler
     */
    public void setCurrentDropHandler(DropHandler currentDropHandler) {
        this.currentDropHandler = currentDropHandler;
    }

    private AcceptCallback acceptCallback;

    public static DragAndDropManager get() {
        if (instance == null) {
            instance = new DragAndDropManager();
        }
        return instance;
    }

    /* Singleton */
    private DragAndDropManager() {
    }

    /**
     * This method is used to start Vaadin client side drag and drop operation.
     * Operation may be started by virtually any Widget.
     * <p>
     * Cancels possible existing drag. TODO figure out if this is always a bug
     * if one is active. Maybe a good and cheap lifesaver thought.
     * <p>
     * If possible, method automatically detects current {@link DropHandler} and
     * fires {@link DropHandler#dragEnter(DragEvent)} event on it.
     * <p>
     * May also be used to control the drag and drop operation. If this option
     * is used, {@link DropHandler} is searched on mouse events and appropriate
     * methods on it called automatically.
     * 
     * @param transferable
     * @param nativeEvent
     * @param handleDragEvents
     *            if true, {@link DragAndDropManager} handles the drag and drop
     *            operation GWT event preview.
     * @return
     */
    public DragEvent startDrag(Transferable transferable,
            NativeEvent startEvent, boolean handleDragEvents) {
        interruptDrag();

        currentDrag = new DragEvent(transferable, startEvent);
        DropHandler dh = null;
        if (startEvent != null) {
            dh = findDragTarget((Element) startEvent.getEventTarget().cast());
        }
        if (dh != null) {
            // drag has started on a DropHandler, kind of drag over happens
            currentDropHandler = dh;
            updateCurrentEvent(startEvent);
            dh.dragEnter(currentDrag);
        }

        if (handleDragEvents) {

            handlerRegistration = Event
                    .addNativePreviewHandler(new NativePreviewHandler() {

                        public void onPreviewNativeEvent(
                                NativePreviewEvent event) {
                            updateCurrentEvent(event.getNativeEvent());
                            updateDragImagePosition();

                            NativeEvent nativeEvent = event.getNativeEvent();
                            Element targetElement = (Element) nativeEvent
                                    .getEventTarget().cast();
                            if (dragElement != null
                                    && targetElement.isOrHasChild(dragElement)) {
                                ApplicationConnection.getConsole().log(
                                        "Event on dragImage, ignored");
                                event.cancel();
                                nativeEvent.stopPropagation();
                                return;
                            }

                            int typeInt = event.getTypeInt();
                            switch (typeInt) {
                            case Event.ONMOUSEOVER:
                                ApplicationConnection.getConsole().log(
                                        event.getNativeEvent().getType());
                                DropHandler target = findDragTarget(targetElement);
                                if (target != null && target != currentDrag) {
                                    currentDropHandler = target;
                                    target.dragEnter(currentDrag);
                                } else if (target == null
                                        && currentDropHandler != null) {
                                    ApplicationConnection.getConsole().log(
                                            "Invalid state!?");
                                    currentDropHandler = null;
                                }
                                break;
                            case Event.ONMOUSEOUT:
                                ApplicationConnection.getConsole().log(
                                        event.getNativeEvent().getType());

                                Element relatedTarget = (Element) nativeEvent
                                        .getRelatedEventTarget().cast();
                                DropHandler newDragHanler = findDragTarget(relatedTarget);
                                if (dragElement != null
                                        && dragElement
                                                .isOrHasChild(relatedTarget)) {
                                    ApplicationConnection.getConsole().log(
                                            "Mouse out of dragImage, ignored");
                                    return;
                                }

                                if (currentDropHandler != null
                                        && currentDropHandler != newDragHanler) {
                                    currentDropHandler.dragLeave(currentDrag);
                                    currentDropHandler = null;
                                    acceptCallback = null;
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

                    });
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
            int clientY = currentDrag.currentGwtEvent.getClientY() + 6;
            int clientX = currentDrag.currentGwtEvent.getClientX() + 6;
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
    private DropHandler findDragTarget(Element element) {

        EventListener eventListener = Event.getEventListener(element);
        while (eventListener == null) {
            element = element.getParentElement();
            if (element == null) {
                break;
            }
            eventListener = Event.getEventListener(element);
        }
        if (eventListener == null) {
            ApplicationConnection.getConsole().log(
                    "No suitable DropHandler found");
            return null;
        } else {
            Widget w = (Widget) eventListener;
            while (!(w instanceof HasDropHandler)) {
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
                DropHandler dh = ((HasDropHandler) w).getDropHandler();
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
            boolean sendTransferrableToServer = currentDropHandler
                    .drop(currentDrag);
            if (sendTransferrableToServer) {
                doRequest(DragEventType.DROP);
            }
            currentDropHandler = null;
            acceptCallback = null;

        }

        currentDrag = null;

        if (dragElement != null) {
            RootPanel.getBodyElement().removeChild(dragElement);
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
    public void visitServer(DragEventType type, AcceptCallback acceptCallback) {
        doRequest(type);
        this.acceptCallback = acceptCallback;
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

        Transferable transferable = currentDrag.getTransferrable();

        if (transferable.getItemId() != null) {
            client.updateVariable(DD_SERVICE, "itemId", transferable
                    .getItemId(), false);
        }
        if (transferable.getPropertyId() != null) {
            client.updateVariable(DD_SERVICE, "propertyId", transferable
                    .getPropertyId(), false);
        }

        client.updateVariable(DD_SERVICE, "component", transferable
                .getComponent(), false);

        client.updateVariable(DD_SERVICE, "type", drop.ordinal(), false);

        if (currentDrag.currentGwtEvent != null) {
            try {
                MouseEventDetails mouseEventDetails = new MouseEventDetails(
                        currentDrag.currentGwtEvent);
                currentDrag.getEventDetails().put("mouseEvent",
                        mouseEventDetails.serialize());
            } catch (Exception e) {
                // NOP, (at least oophm on Safari) can't serialize html dd event
                // to
                // mouseevent
            }
        } else {
            currentDrag.getEventDetails().put("mouseEvent", null);
        }
        client.updateVariable(DD_SERVICE, "evt", currentDrag.getEventDetails(),
                false);

        client.updateVariable(DD_SERVICE, "tra", transferable.getVariableMap(),
                true);

    }

    public void handleServerResponse(ValueMap valueMap) {
        if (acceptCallback == null) {
            return;
        }
        int visitId = valueMap.getInt("visitId");
        if (this.visitId == visitId) {
            acceptCallback.handleResponse(valueMap);
            acceptCallback = null;
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
            Style style = node.getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setZIndex(600000);
            RootPanel.getBodyElement().appendChild(node);
        }
    }

}
