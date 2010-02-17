/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VSourceIsSameAsTarget;
import com.vaadin.ui.Component;

@ClientCriterion(VSourceIsSameAsTarget.class)
public class SourceIsSameAsTarget extends ClientSideCriterion {

    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof ComponentTransferable) {
            Component sourceComponent = ((ComponentTransferable) dragEvent
                    .getTransferable()).getSourceComponent();
            DropTarget target = dragEvent.getDropTargetData().getTarget();
            return sourceComponent == target;
        }

        return false;
    }

}