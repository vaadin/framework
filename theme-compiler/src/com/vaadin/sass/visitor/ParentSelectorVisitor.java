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
import com.vaadin.sass.tree.Node;

public class ParentSelectorVisitor implements Visitor {

    @Override
    public void traverse(Node node) throws Exception {
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            if (child instanceof BlockNode) {
                traverse(node, (BlockNode) child);
            }
        }
    }

    private void traverse(Node parent, BlockNode block) throws Exception {
        Node pre = block;
        for (Node child : new ArrayList<Node>(block.getChildren())) {
            if (child instanceof BlockNode) {
                BlockNode blockChild = (BlockNode) child;
                traverse(block, blockChild);
                for (final String s : blockChild.getSelectorList()) {

                    if (s.contains("&")) {
                        parent.appendChild(child, pre);
                        pre = child;
                        block.removeChild(child);

                        ArrayList<String> newList = new ArrayList<String>(block
                                .getSelectorList().size());
                        for (final String parentSelector : block
                                .getSelectorList()) {
                            newList.add(s.replace("&", parentSelector));
                        }
                        blockChild.setSelectorList(newList);
                    }
                }
            }
        }
    }
}
