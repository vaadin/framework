/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VSourceIsSameAsTarget implements VAcceptCriterion {
    public void accept(VDragEvent drag, UIDL configuration,
            VAcceptCallback callback) {
        Paintable dragSource = drag.getTransferable().getDragSource();
        Paintable paintable = VDragAndDropManager.get().getCurrentDropHandler()
                .getPaintable();

        if (paintable == dragSource) {
            callback.accepted(drag);
        }
    }

    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return false;
    }
}