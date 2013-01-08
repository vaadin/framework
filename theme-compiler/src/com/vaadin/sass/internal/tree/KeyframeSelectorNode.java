package com.vaadin.sass.internal.tree;

public class KeyframeSelectorNode extends Node {
    private String selector;

    public KeyframeSelectorNode(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(selector).append(" {\n");
        for (Node child : children) {
            string.append("\t\t").append(child.toString()).append("\n");
        }
        string.append("\t}");
        return string.toString();
    }

    @Override
    public void traverse() {

    }

}
