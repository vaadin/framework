/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * 
 */
package com.vaadin.client.ui.dd;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VTree;
import com.vaadin.client.ui.VTree.TreeNode;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.ui.Tree;

@AcceptCriterion(Tree.TargetInSubtree.class)
final public class VTargetInSubtree extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {

        VTree tree = (VTree) VDragAndDropManager.get().getCurrentDropHandler()
                .getConnector().getWidget();
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
                // panel -> next level node
                parent2 = parent2.getParent().getParent();
            }
        }

        return false;
    }
}
