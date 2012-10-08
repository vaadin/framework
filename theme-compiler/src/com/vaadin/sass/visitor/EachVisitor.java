package com.vaadin.sass.visitor;

import java.util.HashMap;
import java.util.Map.Entry;

import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.controldirective.EachDefNode;
import com.vaadin.sass.util.DeepCopy;

public class EachVisitor implements Visitor {

    HashMap<EachDefNode, Node> controlDefs = new HashMap<EachDefNode, Node>();
    private Node rootNode;

    @Override
    public void traverse(Node node) throws Exception {
        this.rootNode = node;

        findDefNodes(null, node);

        replaceControlNodes();

    }

    private void findDefNodes(Node parent, Node node) {
        for (Node child : node.getChildren()) {
            findDefNodes(node, child);
        }
        if (node instanceof EachDefNode) {
            controlDefs.put((EachDefNode) node, parent);
        }
    }

    private void replaceControlNodes() {
        for (final Entry<EachDefNode, Node> entry : controlDefs.entrySet()) {
            replaceEachDefNode(entry.getKey(), entry.getValue());
        }
    }

    private void replaceEachDefNode(EachDefNode defNode, Node parent) {
        Node last = defNode;
        for (final Node child : defNode.getChildren()) {
            if (child instanceof BlockNode) {
                BlockNode iNode = (BlockNode) child;
                String interpolation = "#{" + defNode.getVariableName() + "}";
                if (iNode.containsInterpolationVariable(interpolation)) {
                    for (final String variable : defNode.getVariables()) {
                        BlockNode copy = (BlockNode) DeepCopy.copy(child);
                        copy.replaceInterpolation(defNode.getVariableName(),
                                variable);

                        for (final Node blockChild : copy.getChildren()) {
                            if (blockChild instanceof RuleNode) {
                                ((RuleNode) blockChild).replaceInterpolation(
                                        defNode.getVariableName(), variable);
                            }
                        }

                        parent.appendChild(copy, last);
                        last = copy;
                    }
                }
            }

            last = child;
        }
        rootNode.removeChild(defNode);
    }

}
