/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VTree;
import com.vaadin.terminal.gwt.client.ui.VTree.TreeNode;

final public class VTargetInSubtree extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {

        VTree tree = (VTree) VDragAndDropManager.get().getCurrentDropHandler()
                .getPaintable();
        TreeNode treeNode = tree.getNodeByKey((String) drag.getDropDetails()
                .get("itemIdOver"));
        if (treeNode != null) {
            Widget parent2 = treeNode;
            int depth = configuration.getIntAttribute("depth");
            if (depth < 0) {
                depth = Integer.MAX_VALUE;
            }
            final String searchedKey = configuration.getStringAttribute("key");
            for (int i = 0; i <= depth && parent2 instanceof TreeNode; i++) {
                if (searchedKey.equals(((TreeNode) parent2).key)) {
                    return true;
                }
                parent2 = parent2.getParent().getParent(); // panel -> next level node
            }
        }

        return false;
    }
}