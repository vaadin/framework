package com.vaadin.sass.visitor;

import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.controldirective.EachDefNode;
import com.vaadin.sass.util.DeepCopy;

public class EachNodeHandler {

    public static void traverse(EachDefNode node) {
        replaceEachDefNode(node);
    }

    private static void replaceEachDefNode(EachDefNode defNode) {
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

                        defNode.getParentNode().appendChild(copy, last);
                        last = copy;
                    }
                }
            }

            last = child;
        }
        defNode.getParentNode().removeChild(defNode);
    }

}
