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
import java.util.HashMap;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;

/**
 * Handle nesting of blocks by moving child blocks to their parent, updating
 * their selector lists while doing so. Nodes already handled by
 * {@link ParentSelectorHandler} are not handled here.
 * 
 * Sample SASS code (from www.sass-lang.com):
 * 
 * <pre>
 * table.hl {
 *   margin: 2em 0;
 *   td.ln {
 *     text-align: right;
 *   }
 * }
 * </pre>
 * 
 * Note that nested properties are handled by {@link NestedNodeHandler}, not
 * here.
 */
public class BlockNodeHandler {

    public static void traverse(BlockNode node) {

        Node parent = node.getParentNode();
        if (node.getChildren().size() == 0) {
            parent.removeChild(node);
            while (parent != null && parent instanceof BlockNode
                    && parent.getChildren().size() == 0) {
                Node temp = parent;
                parent = parent.getParentNode();
                parent.removeChild(temp);
            }

            return;
        }

        if (parent instanceof BlockNode) {
            combineParentSelectorListToChild(node);
        }
    }

    private static void combineParentSelectorListToChild(BlockNode node) {
        ArrayList<String> newList = new ArrayList<String>();
        ArrayList<String> parentSelectors = ((BlockNode) node.getParentNode())
                .getSelectorList();
        ArrayList<String> childSelectors = node.getSelectorList();
        for (int i = 0; i < parentSelectors.size(); i++) {
            String parentSelector = parentSelectors.get(i);
            for (int j = 0; j < childSelectors.size(); j++) {
                String childSelector = childSelectors.get(j);
                newList.add(parentSelector + " " + childSelector);
            }

        }
        node.setSelectorList(newList);
        Node oldParent = node.getParentNode();
        HashMap<Node, Node> lastNodeAdded = ScssStylesheet.getLastNodeAdded();
        if (lastNodeAdded.get(oldParent) != null) {
            node.getParentNode().getParentNode()
                    .appendChild(node, lastNodeAdded.get(oldParent));
        } else {
            node.getParentNode().getParentNode()
                    .appendChild(node, node.getParentNode());
        }

        lastNodeAdded.put(oldParent, node);
    }
}
