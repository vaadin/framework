/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Set;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VIsOverId implements VAcceptCriterion {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        try {
            Set<String> stringArrayVariableAsSet = configuration
                    .getStringArrayVariableAsSet("keys");
            if (stringArrayVariableAsSet.contains(drag.getDropDetails().get(
                    "itemIdOver"))) {
                callback.accepted(drag);
            }
        } catch (Exception e) {
        }
        return;
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}