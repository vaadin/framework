/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VDropDetailEquals implements VAcceptCriterion {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        String name = configuration.getStringAttribute("p");
        String value = configuration.getStringAttribute("v");
        Object object = drag.getDropDetails().get(name);
        if (value.equals(object)) {
            callback.accepted(drag);
        }
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}