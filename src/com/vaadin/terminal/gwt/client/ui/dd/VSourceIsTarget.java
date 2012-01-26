/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VSourceIsTarget extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        VPaintableWidget dragSource = drag.getTransferable().getDragSource();
        VPaintableWidget paintable = VDragAndDropManager.get().getCurrentDropHandler()
                .getPaintable();

        return paintable == dragSource;
    }
}