/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * TODO implementation could now be simplified/optimized
 * 
 */
final public class VOr extends VAcceptCriterion {
    private boolean b1;
    private boolean b2;
    private VAcceptCriterion crit1;
    private VAcceptCriterion crit2;

    @Override
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        if (crit1 == null) {
            crit1 = getCriteria(drag, configuration, 0);
            crit2 = getCriteria(drag, configuration, 1);
            if (crit1 == null || crit2 == null) {
                ApplicationConnection.getConsole().log(
                        "Or criteria didn't found a chidl criteria");
                return;
            }
        }

        b1 = false;
        b2 = false;

        VAcceptCallback accept1cb = new VAcceptCallback() {
            public void accepted(VDragEvent event) {
                b1 = true;
            }
        };
        VAcceptCallback accept2cb = new VAcceptCallback() {
            public void accepted(VDragEvent event) {
                b2 = true;
            }
        };

        crit1.accept(drag, configuration.getChildUIDL(0), accept1cb);
        crit2.accept(drag, configuration.getChildUIDL(1), accept2cb);
        if (b1 || b2) {
            callback.accepted(drag);
        }
    }

    private VAcceptCriterion getCriteria(VDragEvent drag, UIDL configuration,
            int i) {
        UIDL childUIDL = configuration.getChildUIDL(i);
        return VAcceptCriteria.get(childUIDL.getStringAttribute("name"));
    }

    @Override
    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false; // TODO enforce on server side
    }

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        return false; // not used here
    }

}