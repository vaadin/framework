/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ComponentConnector;

/**
 * TODO Javadoc!
 * 
 * @since 6.3
 */
final public class VDragSourceIs extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {
            ComponentConnector component = drag.getTransferable().getDragSource();
            int c = configuration.getIntAttribute("c");
            for (int i = 0; i < c; i++) {
                String requiredPid = configuration
                        .getStringAttribute("component" + i);
                VDropHandler currentDropHandler = VDragAndDropManager.get()
                        .getCurrentDropHandler();
                ComponentConnector paintable = (ComponentConnector) ConnectorMap
                        .get(currentDropHandler.getApplicationConnection())
                        .getConnector(requiredPid);
                if (paintable == component) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}