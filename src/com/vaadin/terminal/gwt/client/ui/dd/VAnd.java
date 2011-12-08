/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VAnd extends VAcceptCriterion implements VAcceptCallback {
    private boolean b1;

    static VAcceptCriterion getCriteria(VDragEvent drag, UIDL configuration,
            int i) {
        UIDL childUIDL = configuration.getChildUIDL(i);
        return VAcceptCriteria.get(childUIDL.getStringAttribute("name"));
    }

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        int childCount = configuration.getChildCount();
        for (int i = 0; i < childCount; i++) {
            VAcceptCriterion crit = getCriteria(drag, configuration, i);
            b1 = false;
            crit.accept(drag, configuration.getChildUIDL(i), this);
            if (!b1) {
                return false;
            }
        }
        return true;
    }

    public void accepted(VDragEvent event) {
        b1 = true;
    }

}