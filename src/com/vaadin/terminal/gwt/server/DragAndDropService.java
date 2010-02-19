package com.vaadin.terminal.gwt.server;

import java.io.PrintWriter;
import java.util.Map;

import com.vaadin.event.TransferableImpl;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.DropTargetDetails;
import com.vaadin.event.dd.DropTargetDetailsImpl;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager.DragEventType;
import com.vaadin.ui.Component;

public class DragAndDropService implements VariableOwner {

    private static final long serialVersionUID = -4745268869323400203L;

    public static final String DROPTARGET_KEY = "target";

    private int lastVisitId;

    private int currentEventId;

    private boolean lastVisitAccepted = false;

    private DragAndDropEvent dragEvent;

    private final AbstractCommunicationManager manager;

    private AcceptCriterion acceptCriterion;

    public DragAndDropService(AbstractCommunicationManager manager) {
        this.manager = manager;
    }

    public void changeVariables(Object source, Map<String, Object> variables) {
        Object owner = variables.get("dhowner");

        // Validate drop handler owner
        if (!(owner instanceof DropTarget)) {
            System.err.println("DropHandler owner " + owner
                    + " must implement DropTarget");
            return;
        }
        if (owner == null) {
            System.err.println("DropHandler owner is null");
            return;
        }

        DropTarget dropTarget = (DropTarget) owner;
        lastVisitId = (Integer) variables.get("visitId");

        // request may be dropRequest or request during drag operation (commonly
        // dragover or dragenter)
        boolean dropRequest = isDropRequest(variables);
        if (dropRequest) {
            handleDropRequest(dropTarget, variables);
        } else {
            handleDragRequest(dropTarget, variables);
        }

    }

    /**
     * Handles a drop request from the VDragAndDropManager.
     * 
     * @param dropTarget
     * @param variables
     */
    private void handleDropRequest(DropTarget dropTarget,
            Map<String, Object> variables) {
        DropHandler dropHandler = (dropTarget).getDropHandler();
        if (dropHandler == null) {
            // No dropHandler returned so no drop can be performed.
            System.err
                    .println("DropTarget.getDropHandler() returned null for owner: "
                            + dropTarget);
            return;
        }

        /*
         * Construct the Transferable and the DragDropDetails for the drop
         * operation based on the info passed from the client widgets (drag
         * source for Transferable, drop target for DragDropDetails).
         */
        Transferable transferable = constructTransferable(dropTarget, variables);
        DropTargetDetails dropData = constructDragDropDetails(dropTarget,
                variables);
        DragAndDropEvent dropEvent = new DragAndDropEvent(transferable,
                dropData);
        if (dropHandler.getAcceptCriterion().accepts(dropEvent)) {
            dropHandler.drop(dropEvent);
        }
    }

    /**
     * Handles a drag/move request from the VDragAndDropManager.
     * 
     * @param dropTarget
     * @param variables
     */
    private void handleDragRequest(DropTarget dropTarget,
            Map<String, Object> variables) {
        lastVisitId = (Integer) variables.get("visitId");

        acceptCriterion = dropTarget.getDropHandler().getAcceptCriterion();

        /*
         * Construct the Transferable and the DragDropDetails for the drag
         * operation based on the info passed from the client widgets (drag
         * source for Transferable, current target for DragDropDetails).
         */
        Transferable transferable = constructTransferable(dropTarget, variables);
        DropTargetDetails dragDropDetails = constructDragDropDetails(
                dropTarget, variables);

        dragEvent = new DragAndDropEvent(transferable, dragDropDetails);

        lastVisitAccepted = acceptCriterion.accepts(dragEvent);
    }

    /**
     * Construct DragDropDetails based on variables from client drop target.
     * Uses DragDropDetailsTranslator if available, otherwise a default
     * DragDropDetails implementation is used.
     * 
     * @param dropTarget
     * @param variables
     * @return
     */
    @SuppressWarnings("unchecked")
    private DropTargetDetails constructDragDropDetails(DropTarget dropTarget,
            Map<String, Object> variables) {
        Map<String, Object> rawDragDropDetails = (Map<String, Object>) variables
                .get("evt");

        DropTargetDetails dropData = dropTarget
                .translateDragDropDetails(rawDragDropDetails);

        if (dropData == null) {
            // Create a default DragDropDetails with all the raw variables
            dropData = new DropTargetDetailsImpl(rawDragDropDetails);
        }

        dropData.setData(DROPTARGET_KEY, dropTarget);

        return dropData;
    }

    private boolean isDropRequest(Map<String, Object> variables) {
        return getRequestType(variables) == DragEventType.DROP;
    }

    private DragEventType getRequestType(Map<String, Object> variables) {
        int type = (Integer) variables.get("type");
        return DragEventType.values()[type];
    }

    @SuppressWarnings("unchecked")
    private Transferable constructTransferable(DropTarget dropHandlerOwner,
            Map<String, Object> variables) {
        int eventId = (Integer) variables.get("eventId");
        currentEventId = eventId;

        final Component sourceComponent = (Component) variables
                .get("component");

        variables = (Map<String, Object>) variables.get("tra");

        Transferable transferable = null;
        if (sourceComponent != null && sourceComponent instanceof DragSource) {
            transferable = ((DragSource) sourceComponent)
                    .getTransferable(variables);
        }
        if (transferable == null) {
            transferable = new TransferableImpl(sourceComponent, variables);
        }

        return transferable;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isImmediate() {
        return true;
    }

    void printJSONResponse(PrintWriter outWriter) throws PaintException {
        if (isDirty()) {

            outWriter.print(", \"dd\":");

            JsonPaintTarget jsonPaintTarget = new JsonPaintTarget(manager,
                    outWriter, false);
            jsonPaintTarget.startTag("dd");
            jsonPaintTarget.addAttribute("visitId", lastVisitId);
            if (acceptCriterion != null) {
                jsonPaintTarget.addAttribute("accepted", lastVisitAccepted);
                acceptCriterion.paintResponse(jsonPaintTarget);
            }
            jsonPaintTarget.endTag("dd");
            jsonPaintTarget.close();
            lastVisitId = -1;
            lastVisitAccepted = false;
            acceptCriterion = null;
            dragEvent = null;
        }
    }

    private boolean isDirty() {
        if (lastVisitId > 0) {
            return true;
        }
        return false;
    }
}
