/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VOverTreeNode;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.Location;
import com.vaadin.ui.Tree.TreeDropDetails;

/**
 * Accepts transferable only on tree Node (middle of the node + can has child)
 * 
 * TODO relocate close to {@link Tree} as this is tree specifif
 * 
 */
@ClientCriterion(VOverTreeNode.class)
public class OverTreeNode extends ClientSideCriterion {

    private static final long serialVersionUID = 1L;

    public boolean accepts(DragAndDropEvent dragEvent) {
        try {
            // must be over tree node and in the middle of it (not top or bottom
            // part)
            TreeDropDetails eventDetails = (TreeDropDetails) dragEvent
                    .getDropTargetData();

            Object itemIdOver = eventDetails.getItemIdOver();
            if (!eventDetails.getTarget().areChildrenAllowed(itemIdOver)) {
                return false;
            }

            return eventDetails.getDropLocation() == Location.MIDDLE;
        } catch (Exception e) {
            return false;
        }
    }

}