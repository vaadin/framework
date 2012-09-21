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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.ExtendNode;
import com.vaadin.sass.tree.Node;

public class ExtendVisitor implements Visitor {
    private Map<String, List<ArrayList<String>>> extendsMap = new HashMap<String, List<ArrayList<String>>>();

    @Override
    public void traverse(Node node) throws Exception {
        buildExtendsMap(node);
        modifyTree(node);
    }

    @SuppressWarnings("unchecked")
    private void modifyTree(Node node) throws Exception {
        for (Node child : node.getChildren()) {
            if (child instanceof BlockNode) {
                BlockNode blockNode = (BlockNode) child;
                String selectorString = blockNode.getSelectors();
                if (extendsMap.get(selectorString) != null) {
                    for (ArrayList<String> sList : extendsMap
                            .get(selectorString)) {
                        ArrayList<String> clone = (ArrayList<String>) sList
                                .clone();
                        addAdditionalSelectorListToBlockNode(blockNode, clone,
                                null);
                    }
                } else {
                    for (Entry<String, List<ArrayList<String>>> entry : extendsMap
                            .entrySet()) {
                        if (selectorString.contains(entry.getKey())) {
                            for (ArrayList<String> sList : entry.getValue()) {
                                ArrayList<String> clone = (ArrayList<String>) sList
                                        .clone();
                                addAdditionalSelectorListToBlockNode(blockNode,
                                        clone, entry.getKey());
                            }
                        }
                    }
                }
            } else {
                buildExtendsMap(child);
            }
        }

    }

    private void buildExtendsMap(Node node) {
        if (node instanceof BlockNode) {
            BlockNode blockNode = (BlockNode) node;
            for (Node child : new ArrayList<Node>(node.getChildren())) {
                if (child instanceof ExtendNode) {
                    ExtendNode extendNode = (ExtendNode) child;

                    String extendedString = extendNode.getListAsString();
                    if (extendsMap.get(extendedString) == null) {
                        extendsMap.put(extendedString,
                                new ArrayList<ArrayList<String>>());
                    }
                    extendsMap.get(extendedString).add(
                            blockNode.getSelectorList());
                    node.removeChild(child);
                } else {
                    buildExtendsMap(child);
                }
            }
        } else {
            for (Node child : node.getChildren()) {
                buildExtendsMap(child);
            }
        }

    }

    private void addAdditionalSelectorListToBlockNode(BlockNode blockNode,
            ArrayList<String> list, String selectorString) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (selectorString == null) {
                    blockNode.getSelectorList().add(list.get(i));
                } else {
                    ArrayList<String> newTags = new ArrayList<String>();
                    for (final String existing : blockNode.getSelectorList()) {
                        if (existing.contains(selectorString)) {
                            newTags.add(existing.replace(selectorString,
                                    list.get(i)));
                        }
                    }
                    blockNode.getSelectorList().addAll(newTags);
                }
            }
        }
    }
}
