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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.tree.BlockNode;
import com.vaadin.sass.internal.tree.ExtendNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.util.StringUtil;

public class ExtendNodeHandler {
    private static Map<String, List<ArrayList<String>>> extendsMap = new HashMap<String, List<ArrayList<String>>>();

    public static void traverse(ExtendNode node) throws Exception {
        buildExtendsMap(node);
        modifyTree(ScssStylesheet.get());
    }

    public static void clear() {
        if (extendsMap != null) {
            extendsMap.clear();
        }
    }

    private static void modifyTree(Node node) throws Exception {
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
                        if (StringUtil.containsSubString(selectorString,
                                entry.getKey())) {
                            for (ArrayList<String> sList : entry.getValue()) {
                                ArrayList<String> clone = (ArrayList<String>) sList
                                        .clone();
                                addAdditionalSelectorListToBlockNode(blockNode,
                                        clone, entry.getKey());
                            }
                        }
                    }
                }
            }
        }

    }

    private static void buildExtendsMap(ExtendNode node) {
        String extendedString = node.getListAsString();
        if (extendsMap.get(extendedString) == null) {
            extendsMap.put(extendedString, new ArrayList<ArrayList<String>>());
        }
        // prevent a selector extends itself, e.g. .test{ @extend .test}
        String parentSelectorString = ((BlockNode) node.getParentNode())
                .getSelectors();
        if (!parentSelectorString.equals(extendedString)) {
            extendsMap.get(extendedString).add(
                    ((BlockNode) node.getParentNode()).getSelectorList());
        }
    }

    private static void addAdditionalSelectorListToBlockNode(
            BlockNode blockNode, ArrayList<String> extendingSelectors,
            String extendedSelector) {
        if (extendingSelectors != null) {
            for (String extendingSelector : extendingSelectors) {
                if (extendedSelector == null) {
                    blockNode.getSelectorList().add(extendingSelector);
                } else {
                    ArrayList<String> newTags = new ArrayList<String>();
                    for (final String selectorString : blockNode
                            .getSelectorList()) {
                        if (StringUtil.containsSubString(selectorString,
                                extendedSelector)) {
                            String newTag = generateExtendingSelectors(
                                    selectorString, extendedSelector,
                                    extendingSelector);
                            // prevent adding duplicated selector list
                            if (!blockNode.getSelectorList().contains(newTag)
                                    && !newTags.contains(newTag)) {
                                newTags.add(newTag);
                            }
                        }
                    }
                    blockNode.getSelectorList().addAll(newTags);
                }
            }
        }
    }

    private static String generateExtendingSelectors(String selectorString,
            String extendedSelector, String extendingSelector) {
        String result = StringUtil.replaceSubString(selectorString,
                extendedSelector, extendingSelector);
        // remove duplicated class selectors.
        if (result.startsWith(".")) {
            result = StringUtil.removeDuplicatedSubString(result, ".");
        }
        return result;
    }
}
