/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VAcceptAll implements VAcceptCriteria {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        callback.accepted(drag);
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}