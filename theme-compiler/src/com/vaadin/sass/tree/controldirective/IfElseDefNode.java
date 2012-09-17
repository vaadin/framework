package com.vaadin.sass.tree.controldirective;

import com.vaadin.sass.tree.Node;

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

}
