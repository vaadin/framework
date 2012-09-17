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
import java.util.Map;

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.tree.IVariableNode;
import com.vaadin.sass.tree.MixinDefNode;
import com.vaadin.sass.tree.MixinNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.util.DeepCopy;

public class MixinVisitor implements Visitor {
    Map<String, MixinDefNode> mixinDefs = new HashMap<String, MixinDefNode>();

    @Override
    public void traverse(Node node) throws Exception {
        // create mixin map.
        for (Node child : node.getChildren()) {
            if (child instanceof MixinDefNode) {
                mixinDefs.put(((MixinDefNode) child).getName(),
                        (MixinDefNode) child);
            }
        }

        replaceMixins(node);

        // delete MixinDefNode
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            if (child instanceof MixinDefNode) {
                node.removeChild(child);
            }
        }

    }

    private void replaceMixins(Node node) throws Exception {
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            replaceMixins(child);
            if (child instanceof MixinNode) {
                MixinNode mixinNode = (MixinNode) child;
                MixinDefNode mixinDef = mixinDefs.get(mixinNode.getName());
                if (mixinDef == null) {
                    throw new Exception("Mixin Definition: "
                            + mixinNode.getName() + " not found");
                }
                replaceMixinNode(node, mixinNode, mixinDef);
            }
        }
    }

    private void replaceMixinNode(Node current, MixinNode mixinNode,
            MixinDefNode mixinDef) {
        Node pre = mixinNode;
        if (mixinDef.getArglist().isEmpty()) {
            for (Node child : mixinDef.getChildren()) {
                current.appendChild(child, pre);
                pre = child;
            }
        } else {
            int i = 0;
            for (final LexicalUnit unit : mixinNode.getArglist()) {
                mixinDef.getArglist().get(i)
                        .setExpr((LexicalUnit) DeepCopy.copy(unit));
                i++;
            }

            for (int j = mixinDef.getChildren().size() - 1; j >= 0; j--) {
                Node child = (Node) DeepCopy
                        .copy(mixinDef.getChildren().get(j));
                replaceChildVariables(mixinDef, child);
                current.appendChild(child, mixinNode);
            }
        }
        current.removeChild(mixinNode);
    }

    private void replaceChildVariables(MixinDefNode mixinDef, Node node) {
        for (final Node child : node.getChildren()) {
            replaceChildVariables(mixinDef, child);
        }
        if (node instanceof IVariableNode) {
            ((IVariableNode) node).replaceVariables(mixinDef.getArglist());
        }
    }
}
