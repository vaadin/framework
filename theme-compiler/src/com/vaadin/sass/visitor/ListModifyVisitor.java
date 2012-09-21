package com.vaadin.sass.visitor;

import java.util.ArrayList;

import com.vaadin.sass.tree.ListModifyNode;
import com.vaadin.sass.tree.Node;

public class ListModifyVisitor implements Visitor {

    @Override
    public void traverse(Node node) throws Exception {
        for (final Node child : new ArrayList<Node>(node.getChildren())) {
            removeNodes(child, node);
        }
    }

    private void removeNodes(Node child, Node parent) {
        for (final Node c : new ArrayList<Node>(child.getChildren())) {
            removeNodes(c, child);
        }

        if (child instanceof ListModifyNode) {
            parent.removeChild(child);
        }

    }
}
