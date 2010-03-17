/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

/**
 * 
 */
final public class VOr extends VAcceptCriterion implements VAcceptCallback {
    private boolean accepted;

    @Override
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        int childCount = configuration.getChildCount();
        accepted = false;
        for (int i = 0; i < childCount; i++) {
            VAcceptCriterion crit = getCriteria(drag, configuration, i);
            crit.accept(drag, configuration.getChildUIDL(i), this);
            if (accepted == true) {
                callback.accepted(drag);
                return;
            }
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

    public void accepted(VDragEvent event) {
        accepted = true;
    }

}