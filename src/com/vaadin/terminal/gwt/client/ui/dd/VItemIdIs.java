/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Set;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VItemIdIs extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {
            Object data = drag.getTransferable().getData("itemId");
            Set<String> stringArrayVariableAsSet = configuration
                    .getStringArrayVariableAsSet("keys");
            return stringArrayVariableAsSet.contains(data);
        } catch (Exception e) {
        }
        return false;
    }
}