/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

/**
 * TODO Javadoc!
 * 
 * @since 6.3
 */
final public class VDragSourceIs extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {
            VPaintableWidget component = drag.getTransferable().getDragSource();
            int c = configuration.getIntAttribute("c");
            for (int i = 0; i < c; i++) {
                String requiredPid = configuration
                        .getStringAttribute("component" + i);
                VDropHandler currentDropHandler = VDragAndDropManager.get()
                        .getCurrentDropHandler();
                VPaintableWidget paintable = (VPaintableWidget) VPaintableMap
                        .get(currentDropHandler.getApplicationConnection())
                        .getPaintable(requiredPid);
                if (paintable == component) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}