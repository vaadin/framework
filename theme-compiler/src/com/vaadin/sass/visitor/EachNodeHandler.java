package com.vaadin.sass.visitor;

import java.util.ArrayList;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.parser.LexicalUnitImpl;
import com.vaadin.sass.tree.IVariableNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.VariableNode;
import com.vaadin.sass.tree.controldirective.EachDefNode;
import com.vaadin.sass.util.DeepCopy;

public class EachNodeHandler {

    public static void traverse(EachDefNode node) {
        replaceEachDefNode(node);
    }

    private static void replaceEachDefNode(EachDefNode defNode) {
        Node last = defNode;

        for (final String var : defNode.getVariables()) {
            VariableNode varNode = new VariableNode(defNode.getVariableName()
                    .substring(1), LexicalUnitImpl.createIdent(var), false);
            ArrayList<VariableNode> variables = new ArrayList<VariableNode>(
                    ScssStylesheet.getVariables());
            variables.add(varNode);

            for (final Node child : defNode.getChildren()) {

                Node copy = (Node) DeepCopy.copy(child);

                replaceInterpolation(copy, variables);

                defNode.getParentNode().appendChild(copy, last);
                last = copy;
            }

        }
        defNode.setChildren(new ArrayList<Node>());
        defNode.getParentNode().removeChild(defNode);
    }

    private static void replaceInterpolation(Node copy,
            ArrayList<VariableNode> variables) {
        if (copy instanceof IVariableNode) {
            IVariableNode n = (IVariableNode) copy;
            n.replaceVariables(variables);
        }

        for (Node c : copy.getChildren()) {
            replaceInterpolation(c, variables);
        }

    }

}
