package com.vaadin.sass.internal.tree.controldirective;

import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.visitor.IfElseNodeHandler;

public class IfElseDefNode extends Node {

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (final Node child : getChildren()) {
            b.append(child.toString());
            b.append("\n");
        }
        return b.toString();
    }

    @Override
    public void traverse() {
        try {

            for (final Node child : children) {
                child.traverse();
            }

            IfElseNodeHandler.traverse(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
