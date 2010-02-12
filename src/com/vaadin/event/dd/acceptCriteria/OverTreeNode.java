/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Tree.Location;
import com.vaadin.ui.Tree.TreeDropDetails;

public class OverTreeNode implements AcceptCriterion {

    public boolean isClientSideVerifiable() {
        return true;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", "overTreeNode");
        target.endTag("-ac");
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        try {
            TreeDropDetails eventDetails = (TreeDropDetails) dragEvent
                    .getDropTargetData();
            return eventDetails.getDropLocation() == Location.MIDDLE;
        } catch (Exception e) {
            return false;
        }
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }

}