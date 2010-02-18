/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VComponentFilter implements VAcceptCriterion {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        try {
            Paintable component = drag.getTransferable().getDragSource();
            int c = configuration.getIntAttribute("c");
            for (int i = 0; i < c; i++) {
                String requiredPid = configuration
                        .getStringAttribute("component" + i);
                Paintable paintable = VDragAndDropManager.get()
                        .getCurrentDropHandler().getApplicationConnection()
                        .getPaintable(requiredPid);
                if (paintable == component) {
                    callback.accepted(drag);
                }
            }
        } catch (Exception e) {
        }
        return;
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}