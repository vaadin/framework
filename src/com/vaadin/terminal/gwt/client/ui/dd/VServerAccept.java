/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.terminal.gwt.client.UIDL;

@AcceptCriterion(ServerSideCriterion.class)
final public class VServerAccept extends VAcceptCriterion {
    @Override
    public void accept(final VDragEvent drag, UIDL configuration,
            final VAcceptCallback callback) {

        VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {
            @Override
            public void handleResponse(boolean accepted, UIDL response) {
                if (accepted) {
                    callback.accepted(drag);
                }
            }
        };
        VDragAndDropManager.get().visitServer(acceptCallback);
    }

    @Override
    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return true;
    }

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        return false; // not used
    }
}