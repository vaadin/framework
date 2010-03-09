/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VSourceIsSameAsTarget extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        Paintable dragSource = drag.getTransferable().getDragSource();
        Paintable paintable = VDragAndDropManager.get().getCurrentDropHandler()
                .getPaintable();

        return paintable == dragSource;
    }
}