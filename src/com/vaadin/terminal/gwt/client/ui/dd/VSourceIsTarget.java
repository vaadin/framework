/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VSourceIsTarget extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        ComponentConnector dragSource = drag.getTransferable().getDragSource();
        ComponentConnector paintable = VDragAndDropManager.get()
                .getCurrentDropHandler().getConnector();

        return paintable == dragSource;
    }
}