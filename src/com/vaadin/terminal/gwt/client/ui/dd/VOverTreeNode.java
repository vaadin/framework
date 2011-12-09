/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VOverTreeNode extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        Boolean containsKey = (Boolean) drag.getDropDetails().get(
                "itemIdOverIsNode");
        return containsKey != null && containsKey.booleanValue();
    }
}