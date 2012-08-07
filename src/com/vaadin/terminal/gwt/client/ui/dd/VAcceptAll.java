/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.terminal.gwt.client.UIDL;

@AcceptCriterion(AcceptAll.class)
final public class VAcceptAll extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        return true;
    }
}