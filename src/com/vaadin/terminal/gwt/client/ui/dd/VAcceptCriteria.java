package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

public interface VAcceptCriteria {

    /**
     * Checks if current drag event has valid drop target and target accepts the
     * transferable. If drop target is valid, callback is used.
     * 
     * @param drag
     * @param configuration
     * @param callback
     */
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback);

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL);

}
