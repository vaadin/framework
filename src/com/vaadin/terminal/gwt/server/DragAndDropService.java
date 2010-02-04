package com.vaadin.terminal.gwt.server;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.DragDropDataTranslator;
import com.vaadin.event.DragDropDetails;
import com.vaadin.event.DragDropDetailsImpl;
import com.vaadin.event.DragDropHandler;
import com.vaadin.event.DragRequest;
import com.vaadin.event.DropHandler;
import com.vaadin.event.DropTarget;
import com.vaadin.event.Transferable;
import com.vaadin.terminal.DragSource;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager.DragEventType;
import com.vaadin.ui.Component;

public class DragAndDropService implements VariableOwner {

    private static final long serialVersionUID = -4745268869323400203L;

    public static final String DROPTARGET_KEY = "target";

    private int lastVisitId;

    private DragRequest currentRequest;

    private int currentEventId;

    private Transferable transferable;

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

        // Is this a drop request or a drag/move request?
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
        DragDropDetails dropData = constructDragDropDetails(dropTarget,
                variables);

        dropHandler.drop(transferable, dropData);
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

        DropHandler dropHandler = (dropTarget).getDropHandler();
        if (!(dropHandler instanceof DragDropHandler)) {
            System.err
                    .println("DragRequest could not be send to handler as DropHandle does not implement DragDropHandler");
            return;
        }

        DragDropHandler dragDropHandler = (DragDropHandler) dropHandler;
        /*
         * Construct the Transferable and the DragDropDetails for the drag
         * operation based on the info passed from the client widgets (drag
         * source for Transferable, current target for DragDropDetails).
         */
        Transferable transferable = constructTransferable(dropTarget, variables);
        DragDropDetails dragDropDetails = constructDragDropDetails(dropTarget,
                variables);

        currentRequest = constructDragRequest(variables, transferable);
        dragDropHandler.handleDragRequest(currentRequest, transferable,
                dragDropDetails);
    }

    private static DragRequest constructDragRequest(
            Map<String, Object> variables, Transferable transferable) {

        int type = (Integer) variables.get("type");
        DragRequest dragRequest = new DragRequest(DragEventType.values()[type]);
        return dragRequest;
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
    private DragDropDetails constructDragDropDetails(DropTarget dropTarget,
            Map<String, Object> variables) {
        Map<String, Object> rawDragDropDetails = (Map<String, Object>) variables
                .get("evt");

        DragDropDetails dropData = null;
        if (dropTarget instanceof DragDropDataTranslator) {
            dropData = ((DragDropDataTranslator) dropTarget)
                    .translateDragDropDetails(rawDragDropDetails);
        }

        if (dropData == null) {
            // Create a default DragDropDetails with all the raw variables
            dropData = new DragDropDetailsImpl(rawDragDropDetails);
        }

        dropData.put(DROPTARGET_KEY, dropTarget);

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
        if (currentEventId != eventId) {
            transferable = null;
        }
        currentEventId = eventId;

        final Component sourceComponent = (Component) variables
                .get("component");

        variables = (Map<String, Object>) variables.get("tra");

        if (sourceComponent != null && sourceComponent instanceof DragSource) {
            transferable = ((DragSource) sourceComponent).getTransferable(
                    transferable, variables);
        } else {
            if (transferable == null) {
                if (sourceComponent != null) {
                    transferable = new ComponentTransferable() {

                        private Map<String, Object> td = new HashMap<String, Object>();

                        public Component getSourceComponent() {
                            return sourceComponent;
                        }

                        public Object getData(String dataFlawor) {
                            return td.get(dataFlawor);
                        }

                        public void setData(String dataFlawor, Object value) {
                            td.put(dataFlawor, value);
                        }

                        public Collection<String> getDataFlawors() {
                            return td.keySet();
                        }

                    };
                } else {
                    transferable = new Transferable() {
                        private Map<String, Object> td = new HashMap<String, Object>();

                        public Object getData(String dataFlawor) {
                            return td.get(dataFlawor);
                        }

                        public void setData(String dataFlawor, Object value) {
                            td.put(dataFlawor, value);
                        }

                        public Collection<String> getDataFlawors() {
                            return td.keySet();
                        }

                    };
                }
            }
        }

        /*
         * Add remaining (non-handled) variables to transferable as is
         */
        for (String key : variables.keySet()) {
            transferable.setData(key, variables.get(key));
        }

        return transferable;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isImmediate() {
        return true;
    }

    void printJSONResponse(PrintWriter outWriter) {
        if (isDirty()) {
            // TODO paint responsedata
            outWriter.print(", dd : {");
            outWriter.print("visitId:");
            outWriter.print(lastVisitId);
            Map<String, Object> responseData = currentRequest.getResponseData();
            if (responseData != null) {
                for (String key : responseData.keySet()) {
                    Object object = responseData.get(key);
                    outWriter.print(",\"");
                    // TODO JSON escaping for key and object
                    outWriter.print(key);
                    outWriter.print("\":");
                    outWriter.print(object);
                }
            }
            outWriter.print("}");
            currentRequest = null;
        }
    }

    private boolean isDirty() {
        if (currentRequest != null) {
            return true;
        }
        return false;
    }
}
