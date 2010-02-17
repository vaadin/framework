/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VDataBound implements VAcceptCriteria {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        if (drag.getTransferable().getData("itemId") != null) {
            callback.accepted(drag);
        }
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}