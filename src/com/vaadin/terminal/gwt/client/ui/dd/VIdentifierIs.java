/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Set;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VIdentifierIs extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
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