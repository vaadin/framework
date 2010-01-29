package com.vaadin.terminal.gwt.server;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.ComponentTransferrable;
import com.vaadin.event.DragRequest;
import com.vaadin.event.DropHandler;
import com.vaadin.event.HasDropHandler;
import com.vaadin.event.Transferable;
import com.vaadin.terminal.TransferTranslator;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.gwt.client.ui.dd.DragAndDropManager.DragEventType;
import com.vaadin.ui.Component;

public class DragAndDropService implements VariableOwner {

    private static final long serialVersionUID = -4745268869323400203L;

    private int lastVisitId;

    private DragRequest currentRequest;

    private int currentEventId;

    private Transferable transferable;

    public void changeVariables(Object source, Map<String, Object> variables) {
        HasDropHandler dropHandlerOwner = (HasDropHandler) variables
                .get("dhowner");
        if (dropHandlerOwner == null) {
            return;
        }
        lastVisitId = (Integer) variables.get("visitId");

        currentRequest = constructDragRequest(variables, dropHandlerOwner);

        DropHandler dropHandler = (dropHandlerOwner).getDropHandler();
        dropHandler.handleDragRequest(currentRequest);
        if (currentRequest.getType() == DragEventType.DROP) {
            // TODO transferable should also be cleaned on each non-dnd
            // variable change (if visited server, but drop did not happen ->
            // should do cleanup to release memory)
            transferable = null;
        }

    }

    private DragRequest constructDragRequest(Map<String, Object> variables,
            HasDropHandler dropHandlerOwner) {
        Transferable transferable = constructTransferrable(variables,
                dropHandlerOwner);

        int type = (Integer) variables.get("type");
        DragRequest dragRequest = new DragRequest(DragEventType.values()[type],
                transferable);
        return dragRequest;
    }

    @SuppressWarnings("unchecked")
    private Transferable constructTransferrable(Map<String, Object> variables,
            HasDropHandler dropHandlerOwner) {
        int eventId = (Integer) variables.get("eventId");
        if (currentEventId != eventId) {
            transferable = null;
        }
        currentEventId = eventId;

        final Component sourceComponent = (Component) variables
                .get("component");
        if (sourceComponent != null
                && sourceComponent instanceof TransferTranslator) {
            transferable = ((TransferTranslator) sourceComponent)
                    .getTransferrable(transferable, variables, false);
        } else {
            if (transferable == null) {
                if (sourceComponent != null) {
                    transferable = new ComponentTransferrable() {

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
         * Also let dropHandler translate variables if it implements
         * TransferTranslator
         */
        if (dropHandlerOwner instanceof TransferTranslator) {
            transferable = ((TransferTranslator) dropHandlerOwner)
                    .getTransferrable(transferable, variables, true);
        }

        /*
         * Add remaining (non-handled) variables to transferable as is
         */
        variables = (Map<String, Object>) variables.get("payload");
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
