package com.vaadin.sass.visitor;

import java.util.ArrayList;

import com.vaadin.sass.tree.NestPropertiesNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.RuleNode;

public class NestPropertiesVisitor implements Visitor {

    @Override
    public void traverse(Node node) {
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            if (child instanceof NestPropertiesNode) {
                Node previous = child;
                for (RuleNode unNested : ((NestPropertiesNode) child)
                        .unNesting()) {
                    node.appendChild(unNested, previous);
                    previous = unNested;
                    node.removeChild(child);
                }
            } else {
                traverse(child);
            }
        }
    }
}
