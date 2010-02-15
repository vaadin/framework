/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

@ServerCriterion("com.vaadin.event.dd.acceptCriteria.IsDatabound")
final class HasItemId implements VAcceptCriteria {
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