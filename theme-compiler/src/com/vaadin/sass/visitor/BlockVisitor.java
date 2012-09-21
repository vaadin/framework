/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.sass.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;

public class BlockVisitor implements Visitor {

    @Override
    public void traverse(Node node) {
        traverse(null, node);
    }

    private void traverse(Node parent, Node node) {
        Node after = node;
        Set<Node> toBeDeleted = new HashSet<Node>();
        for (int pos = 0; pos < node.getChildren().size(); pos++) {
            Node child = node.getChildren().get(pos);

            traverse(node, child);

            if (child instanceof BlockNode && node instanceof BlockNode
                    && parent != null) {
                combineParentSelectorListToChild(node, child);
                toBeDeleted.add(child);
                parent.appendChild(child, after);
                after = child;
            } else if (child instanceof BlockNode
                    && child.getChildren().size() == 0) {
                toBeDeleted.add(child);
            }
        }
        for (Node child : toBeDeleted) {
            node.removeChild(child);
        }
    }

    private void combineParentSelectorListToChild(Node parent, Node child) {
        if (parent instanceof BlockNode && child instanceof BlockNode) {
            ArrayList<String> newList = new ArrayList<String>();
            ArrayList<String> parentSelectors = ((BlockNode) parent)
                    .getSelectorList();
            ArrayList<String> childSelectors = ((BlockNode) child)
                    .getSelectorList();
            for (int i = 0; i < parentSelectors.size(); i++) {
                String parentSelector = parentSelectors.get(i);
                for (int j = 0; j < childSelectors.size(); j++) {
                    String childSelector = childSelectors.get(j);
                    newList.add(parentSelector + " " + childSelector);
                }

            }
            ((BlockNode) child).setSelectorList(newList);
        }
    }
}
