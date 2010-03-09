/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VContainsDataFlavor extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        String name = configuration.getStringAttribute("p");
        return drag.getTransferable().getDataFlavors().contains(name);
    }
}