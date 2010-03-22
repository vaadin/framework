/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

public abstract class VAcceptCriterion {

    /**
     * Checks if current drag event has valid drop target and target accepts the
     * transferable. If drop target is valid, callback is used.
     * 
     * @param drag
     * @param configuration
     * @param callback
     */
    public void accept(final VDragEvent drag, UIDL configuration,
            final VAcceptCallback callback) {
        if (needsServerSideCheck(drag, configuration)) {
            VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {
                public void handleResponse(boolean accepted, UIDL response) {
                    if (accepted) {
                        callback.accepted(drag);
                    }
                }
            };
            VDragAndDropManager.get().visitServer(acceptCallback);
        } else {
            boolean validates = accept(drag, configuration);
            if (validates) {
                callback.accepted(drag);
            }
        }

    }

    protected abstract boolean accept(VDragEvent drag, UIDL configuration);

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }

}
