/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Set;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VIsOverId extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
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