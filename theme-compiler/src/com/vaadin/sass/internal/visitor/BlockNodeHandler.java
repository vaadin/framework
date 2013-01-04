/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.sass.internal.visitor;

import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.tree.BlockNode;
import com.vaadin.sass.internal.tree.Node;

/**
 * Handle nesting of blocks by moving child blocks to their parent, updating
 * their selector lists while doing so. Also parent selectors (&amp;) are
 * handled here.
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

        if (node.getChildren().size() == 0) {
            // empty blocks are removed later
            return;
        }

        Node parent = node.getParentNode();

        if (parent instanceof BlockNode) {
            combineParentSelectorListToChild(node);

        } else if (node.getSelectors().contains("&")) {
            ScssStylesheet.warning("Base-level rule contains"
                    + " the parent-selector-referencing character '&';"
                    + " the character will be removed:\n" + node);
            removeParentReference(node);
        }
    }

    /**
     * Goes through the selector list of the given BlockNode and removes the '&'
     * character from the selectors.
     * 
     * @param node
     */
    private static void removeParentReference(BlockNode node) {
        ArrayList<String> newList = new ArrayList<String>();
        for (String childSelector : node.getSelectorList()) {
            // remove parent selector
            if (childSelector.contains("&")) {
                newList.add(childSelector.replace("&", ""));
            } else {
                newList.add(childSelector);
            }
        }
        node.setSelectorList(newList);
    }

    private static void combineParentSelectorListToChild(BlockNode node) {
        ArrayList<String> newList = new ArrayList<String>();
        BlockNode parentBlock = (BlockNode) node.getParentNode();
        for (String parentSelector : parentBlock.getSelectorList()) {
            for (String childSelector : node.getSelectorList()) {
                // handle parent selector
                if (childSelector.contains("&")) {
                    newList.add(childSelector.replace("&", parentSelector));
                } else {
                    newList.add(parentSelector + " " + childSelector);
                }
            }
        }
        node.setSelectorList(newList);
        Node oldParent = node.getParentNode();

        HashMap<Node, Node> lastNodeAdded = ScssStylesheet.getLastNodeAdded();
        Node lastAdded = lastNodeAdded.get(oldParent.getParentNode());
        if (lastAdded == null) {
            lastAdded = oldParent;
        }

        oldParent.getParentNode().appendChild(node, lastAdded);

        lastNodeAdded.put(oldParent.getParentNode(), node);
    }
}
