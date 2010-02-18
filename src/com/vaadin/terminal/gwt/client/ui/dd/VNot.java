/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VNot implements VAcceptCriterion {
    private boolean b1;
    private VAcceptCriterion crit1;

    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        if (crit1 == null) {
            crit1 = getCriteria(drag, configuration, 0);
            if (crit1 == null) {
                ApplicationConnection.getConsole().log(
                        "Not criteria didn't found a child criteria");
                return;
            }
        }

        b1 = false;

        VAcceptCallback accept1cb = new VAcceptCallback() {
            public void accepted(VDragEvent event) {
                b1 = true;
            }
        };

        crit1.accept(drag, configuration.getChildUIDL(0), accept1cb);
        if (!b1) {
            callback.accepted(drag);
        }
    }

    private VAcceptCriterion getCriteria(VDragEvent drag, UIDL configuration,
            int i) {
        UIDL childUIDL = configuration.getChildUIDL(i);
        return VAcceptCriteria.get(childUIDL.getStringAttribute("name"));
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false; // TODO enforce on server side
    }
}