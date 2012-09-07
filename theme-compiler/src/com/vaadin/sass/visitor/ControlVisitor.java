package com.vaadin.sass.visitor;

import java.util.HashSet;
import java.util.regex.Pattern;

import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.SimpleNode;
import com.vaadin.sass.tree.controldirective.ControlDefNode;
import com.vaadin.sass.tree.controldirective.EachDefNode;

public class ControlVisitor implements Visitor {

    HashSet<Node> controlDefs = new HashSet<Node>();
    private Node rootNode;

    @Override
    public void traverse(Node node) throws Exception {
        this.rootNode = node;
        for (Node child : node.getChildren()) {
            if (child instanceof ControlDefNode) {
                controlDefs.add(child);
            }
        }

        replaceControlNodes();

    }

    private void replaceControlNodes() {
        for (final Node defNode : controlDefs) {
            if (defNode instanceof EachDefNode) {
                replaceEachDefNode((EachDefNode) defNode);
            }

        }
    }

    private void replaceEachDefNode(EachDefNode defNode) {
        for (final Node child : defNode.getChildren()) {
            if (child instanceof BlockNode) {
                for (final String variable : defNode.getVariables()) {

                    String output = child.toString();
                    output = output.replaceAll(
                            Pattern.quote("#{" + defNode.getVariableName()
                                    + "}"), variable);
                    SimpleNode simple = new SimpleNode(output);

                    rootNode.appendChild(simple, defNode);
                }
            }
        }
        rootNode.removeChild(defNode);
    }

}
