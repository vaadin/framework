package com.vaadin.sass.internal.tree;

public class CharsetNode extends Node {
    private String value;

    public CharsetNode(String value) {
        this.value = value;
    }

    @Override
    public void traverse() {
    }

    @Override
    public String toString() {
        return "@charset " + value + ";";
    }
}
