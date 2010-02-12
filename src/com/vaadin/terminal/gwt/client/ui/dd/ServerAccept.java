/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

@ServerCriterion("com.vaadin.event.dd.acceptCriteria.ServerSideCriterion")
final class ServerAccept implements VAcceptCriteria {
    public void accept(final VDragEvent drag, UIDL configuration,
            final VAcceptCallback callback) {

        VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {
            public void handleResponse(boolean accepted, UIDL response) {
                if (accepted) {
                    callback.accepted(drag);
                }
            }
        };
        VDragAndDropManager.get().visitServer(acceptCallback);
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return true;
    }
}