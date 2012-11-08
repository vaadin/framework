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

import com.vaadin.sass.tree.BlockNode;

public class BlockNodeHandler {

    public static void traverse(BlockNode node) {

        if (node.getChildren().size() == 0) {
            node.getParentNode().removeChild(node);
            return;
        }

        if (node.getParentNode() instanceof BlockNode) {
            combineParentSelectorListToChild(node);
        }
    }

    private static void combineParentSelectorListToChild(BlockNode node) {
        ArrayList<String> newList = new ArrayList<String>();
        ArrayList<String> parentSelectors = ((BlockNode) node.getParentNode())
                .getSelectorList();
        ArrayList<String> childSelectors = ((BlockNode) node).getSelectorList();
        for (int i = 0; i < parentSelectors.size(); i++) {
            String parentSelector = parentSelectors.get(i);
            for (int j = 0; j < childSelectors.size(); j++) {
                String childSelector = childSelectors.get(j);
                newList.add(parentSelector + " " + childSelector);
            }

        }
        node.setSelectorList(newList);
        node.getParentNode().getParentNode()
                .appendChild(node, node.getParentNode());
    }
}
