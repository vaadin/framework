/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VServerAccept extends VAcceptCriterion {
    @Override
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

    @Override
    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return true;
    }

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        return false; // not used
    }
}