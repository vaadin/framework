/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.sass.tree.MixinDefNode;
import com.vaadin.sass.tree.MixinNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.VariableNode;
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
            for (int i = 0; i < mixinDef.getArglist().size(); i++) {
                VariableNode arg = (VariableNode) DeepCopy.copy(mixinDef
                        .getArglist().get(i));
                if (i < mixinNode.getArglist().size()) {
                    arg.setExpr(mixinNode.getArglist().get(i));
                }
                current.appendChild(arg, pre);
                pre = arg;
            }
            for (Node child : mixinDef.getChildren()) {
                Node clonedChild = (Node) DeepCopy.copy(child);
                current.appendChild(clonedChild, pre);
                pre = clonedChild;
            }
        }
        current.removeChild(mixinNode);
    }
}
