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

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.IVariableNode;
import com.vaadin.sass.internal.tree.MixinDefNode;
import com.vaadin.sass.internal.tree.MixinNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.util.DeepCopy;

public class MixinNodeHandler {

    public static void traverse(MixinNode node) throws Exception {
        replaceMixins(node);
    }

    private static void replaceMixins(MixinNode node) throws Exception {
        MixinDefNode mixinDef = ScssStylesheet.getMixinDefinition(node
                .getName());
        if (mixinDef == null) {
            throw new Exception("Mixin Definition: " + node.getName()
                    + " not found");
        }
        replaceMixinNode(node, mixinDef);
    }

    private static void replaceMixinNode(MixinNode mixinNode,
            MixinDefNode mixinDef) {
        MixinDefNode defClone = (MixinDefNode) DeepCopy.copy(mixinDef);
        defClone.traverse();

        defClone.replaceContentDirective(mixinNode);

        if (mixinDef.getArglist().isEmpty()) {
            mixinNode.getParentNode().appendChildrenAfter(
                    new ArrayList<Node>(defClone.getChildren()), mixinNode);
        } else {
            if (mixinNode.getArglist() != null
                    && !mixinNode.getArglist().isEmpty()) {
                replacePossibleArguments(mixinNode, defClone);
            }

            Node previous = mixinNode;
            for (final Node child : new ArrayList<Node>(defClone.getChildren())) {
                replaceChildVariables(defClone, child);
                mixinNode.getParentNode().appendChild(child, previous);
                previous = child;
            }

        }

        mixinNode.getParentNode().removeChild(mixinNode);
    }

    /**
     * We have to replace all the mixin parameters. This is done in two phases.
     * First phase replaces all the named parameters while the second replaces
     * in order of remaining unmodified parameters.
     * 
     * @param mixinNode
     * @param def
     */
    private static void replacePossibleArguments(MixinNode mixinNode,
            MixinDefNode def) {
        if (mixinNode.getArglist().size() > 0) {
            ArrayList<VariableNode> remainingNodes = new ArrayList<VariableNode>(
                    def.getArglist());
            ArrayList<LexicalUnitImpl> remainingUnits = new ArrayList<LexicalUnitImpl>(
                    mixinNode.getArglist());

            for (final LexicalUnitImpl unit : mixinNode.getArglist()) {
                if (unit.getLexicalUnitType() == LexicalUnitImpl.SCSS_VARIABLE
                        && unit.getNextLexicalUnit() != null) {
                    for (final VariableNode node : def.getArglist()) {
                        if (node.getName().equals(unit.getValue().toString())) {
                            node.setExpr((LexicalUnitImpl) DeepCopy.copy(unit
                                    .getNextLexicalUnit()));
                            remainingNodes.remove(node);
                            remainingUnits.remove(unit);
                            break;
                        }
                    }
                }
            }
            checkExtraParameters(mixinNode, remainingNodes.size(),
                    remainingUnits.size());
            for (int i = 0; i < remainingNodes.size()
                    && i < remainingUnits.size(); i++) {
                LexicalUnitImpl unit = remainingUnits.get(i);
                remainingNodes.get(i).setExpr(
                        (LexicalUnitImpl) DeepCopy.copy(unit));
            }
        }

    }

    protected static void checkExtraParameters(MixinNode mixinNode,
            int remainingNodesSize, int remainingUnitsSize) {
        if (remainingUnitsSize > remainingNodesSize) {
            String fileName = null;
            Node root = mixinNode.getParentNode();
            while (root != null && !(root instanceof ScssStylesheet)) {
                root = root.getParentNode();
            }
            if (root != null) {
                fileName = ((ScssStylesheet) root).getFileName();
            }
            StringBuilder builder = new StringBuilder();
            builder.append("More parameters than expected, in Mixin ").append(
                    mixinNode.getName());
            if (fileName != null) {
                builder.append(", in file ").append(fileName);
            }
            throw new RuntimeException(builder.toString());
        }
    }

    private static void replaceChildVariables(MixinDefNode mixinDef, Node node) {
        for (final Node child : node.getChildren()) {
            replaceChildVariables(mixinDef, child);
        }
        if (node instanceof IVariableNode) {
            ((IVariableNode) node).replaceVariables(mixinDef.getArglist());
        }
    }
}
