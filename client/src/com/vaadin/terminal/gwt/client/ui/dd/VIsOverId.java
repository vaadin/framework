/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.ui.AbstractSelect;

@AcceptCriterion(AbstractSelect.TargetItemIs.class)
final public class VIsOverId extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {

            String pid = configuration.getStringAttribute("s");
            VDropHandler currentDropHandler = VDragAndDropManager.get()
                    .getCurrentDropHandler();
            ComponentConnector dropHandlerConnector = currentDropHandler
                    .getConnector();
            ConnectorMap paintableMap = ConnectorMap.get(currentDropHandler
                    .getApplicationConnection());

            String pid2 = dropHandlerConnector.getConnectorId();
            if (pid2.equals(pid)) {
                Object searchedId = drag.getDropDetails().get("itemIdOver");
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