/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.event.dd.acceptcriteria.ContainsDataFlavor;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.terminal.gwt.client.UIDL;

@AcceptCriterion(ContainsDataFlavor.class)
final public class VContainsDataFlavor extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        String name = configuration.getStringAttribute("p");
        return drag.getTransferable().getDataFlavors().contains(name);
    }
}