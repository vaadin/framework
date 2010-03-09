/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VSourceIsSameAsTarget;
import com.vaadin.ui.Component;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VSourceIsSameAsTarget.class)
public class IsSameSourceAndTarget extends ClientSideCriterion {

    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof TransferableImpl) {
            Component sourceComponent = ((TransferableImpl) dragEvent
                    .getTransferable()).getSourceComponent();
            DropTarget target = dragEvent.getDropTargetDetails().getTarget();
            return sourceComponent == target;
        }

        return false;
    }

}