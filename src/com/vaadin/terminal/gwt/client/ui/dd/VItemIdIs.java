/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ComponentConnector;

final public class VItemIdIs extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {
            String pid = configuration.getStringAttribute("s");
            ComponentConnector dragSource = drag.getTransferable()
                    .getDragSource();
            VDropHandler currentDropHandler = VDragAndDropManager.get()
                    .getCurrentDropHandler();
            String pid2 = ConnectorMap.get(
                    currentDropHandler.getApplicationConnection()).getConnectorId(
                    dragSource);
            if (pid2.equals(pid)) {
                Object searchedId = drag.getTransferable().getData("itemId");
                String[] stringArrayAttribute = configuration
                        .getStringArrayAttribute("keys");
                for (String string : stringArrayAttribute) {
                    if (string.equals(searchedId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}