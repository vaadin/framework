/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DragSource;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.dd.DragEventType;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

import elemental.json.JsonObject;

/**
 *
 * @author Vaadin Ltd
 * @deprecated Since 8.1, no direct replacement, see {@link DragSourceExtension}
 *             and {@link DropTargetExtension}.
 */
@Deprecated
public class DragAndDropService implements VariableOwner, ClientConnector {

    private int lastVisitId;

    private boolean lastVisitAccepted = false;

    private DragAndDropEvent dragEvent;

    private final VaadinSession session;

    private AcceptCriterion acceptCriterion;

    private ErrorHandler errorHandler;

    public DragAndDropService(VaadinSession session) {
        this.session = session;
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        Object owner = variables.get("dhowner");

        final Component sourceComponent = (Component) variables
                .get("component");
        if (sourceComponent != null && !sourceComponent.isConnectorEnabled()) {
            // source component not supposed to be enabled
            getLogger().warning("Client dropped from " + sourceComponent
                    + " even though it's disabled");
            return;
        }

        // Validate drop handler owner
        if (!(owner instanceof DropTarget)) {
            getLogger().severe("DropHandler owner " + owner
                    + " must implement DropTarget");
            return;
        }
        // owner cannot be null here

        DropTarget dropTarget = (DropTarget) owner;

        if (!dropTarget.isConnectorEnabled()) {
            getLogger().warning("Client dropped on " + owner
                    + " even though it's disabled");
            return;
        }

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
        DropHandler dropHandler = dropTarget.getDropHandler();
        if (dropHandler == null) {
            // No dropHandler returned so no drop can be performed.
            getLogger().log(Level.FINE,
                    "DropTarget.getDropHandler() returned null for owner: {0}",
                    dropTarget);
            return;
        }

        /*
         * Construct the Transferable and the DragDropDetails for the drop
         * operation based on the info passed from the client widgets (drag
         * source for Transferable, drop target for DragDropDetails).
         */
        Transferable transferable = constructTransferable(dropTarget,
                variables);
        TargetDetails dropData = constructDragDropDetails(dropTarget,
                variables);
        DragAndDropEvent dropEvent = new DragAndDropEvent(transferable,
                dropData);
        if (dropHandler.getAcceptCriterion().accept(dropEvent)) {
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
        Transferable transferable = constructTransferable(dropTarget,
                variables);
        TargetDetails dragDropDetails = constructDragDropDetails(dropTarget,
                variables);

        dragEvent = new DragAndDropEvent(transferable, dragDropDetails);

        lastVisitAccepted = acceptCriterion.accept(dragEvent);
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
    private TargetDetails constructDragDropDetails(DropTarget dropTarget,
            Map<String, Object> variables) {
        Map<String, Object> rawDragDropDetails = (Map<String, Object>) variables
                .get("evt");

        TargetDetails dropData = dropTarget
                .translateDropTargetDetails(rawDragDropDetails);

        if (dropData == null) {
            // Create a default DragDropDetails with all the raw variables
            dropData = new TargetDetailsImpl(rawDragDropDetails, dropTarget);
        }

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
        final Component sourceComponent = (Component) variables
                .get("component");

        variables = (Map<String, Object>) variables.get("tra");

        Transferable transferable = null;
        if (sourceComponent instanceof DragSource) {
            transferable = ((DragSource) sourceComponent)
                    .getTransferable(variables);
        }
        if (transferable == null) {
            transferable = new TransferableImpl(sourceComponent, variables);
        }

        return transferable;
    }

    /**
     * <p>
     * Tests if the variable owner is enabled or not. The terminal should not
     * send any variable changes to disabled variable owners.
     * </p>
     * Implementation detail: this method is originally from the VariableOwner
     * class, which has been removed in Vaadin 8.
     *
     * @return <code>true</code> if the variable owner is enabled,
     *         <code>false</code> if not
     */
    @Override
    public boolean isEnabled() {
        return isConnectorEnabled();
    }

    public void printJSONResponse(Writer outWriter) throws IOException {
        if (isDirty()) {

            outWriter.write(", \"dd\":");

            JsonPaintTarget jsonPaintTarget = new JsonPaintTarget(
                    session.getCommunicationManager(), outWriter, false);
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

    @Override
    public String getConnectorId() {
        return ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID;
    }

    @Override
    public boolean isConnectorEnabled() {
        // Drag'n'drop can't be disabled
        return true;
    }

    @Override
    public List<ClientMethodInvocation> retrievePendingRpcCalls() {
        return null;
    }

    @Override
    public ServerRpcManager<?> getRpcManager(String interfaceName) {
        // TODO Use rpc for drag'n'drop
        return null;
    }

    @Override
    public Class<? extends SharedState> getStateType() {
        return SharedState.class;
    }

    @Override
    @Deprecated
    public void requestRepaint() {
        markAsDirty();
    }

    @Override
    public void markAsDirty() {
    }

    @Override
    public ClientConnector getParent() {
        return null;
    }

    @Override
    @Deprecated
    public void requestRepaintAll() {
        markAsDirtyRecursive();
    }

    @Override
    public void markAsDirtyRecursive() {
    }

    @Override
    public void attach() {
    }

    @Override
    public void detach() {
    }

    @Override
    public Collection<Extension> getExtensions() {
        return Collections.emptySet();
    }

    @Override
    public void removeExtension(Extension extension) {
    }

    private Logger getLogger() {
        return Logger.getLogger(DragAndDropService.class.getName());
    }

    @Override
    public UI getUI() {
        return null;
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        // Nothing to do
    }

    @Override
    public JsonObject encodeState() {
        return null;
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
            VaadinResponse response, String path) throws IOException {
        return false;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public Registration addAttachListener(AttachListener listener) {
        return () -> {
            /* NO-OP */
        };
    }

    @Override
    @Deprecated
    public void removeAttachListener(AttachListener listener) {
    }

    @Override
    public Registration addDetachListener(DetachListener listener) {
        return () -> {
            /* NO-OP */
        };
    }

    @Override
    @Deprecated
    public void removeDetachListener(DetachListener listener) {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.ClientConnector#isAttached()
     */
    @Override
    public boolean isAttached() {
        return true;
    }
}
