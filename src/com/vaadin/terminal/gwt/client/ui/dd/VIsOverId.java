/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Set;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VIsOverId extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {
            Set<String> stringArrayVariableAsSet = configuration
                    .getStringArrayVariableAsSet("keys");
            return stringArrayVariableAsSet.contains(drag.getDropDetails().get(
                    "itemIdOver"));
        } catch (Exception e) {
        }
        return false;
    }
}