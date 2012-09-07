package com.vaadin.sass.tree;

/**
 * A simple BlockNode where input text equals output. <b>Note : </b> ignores any
 * possible children so only use it when you are sure no child nodes will be
 * applied.
 * 
 * @author Sebastian Nyholm @ Vaadin Ltd
 * 
 */
public class SimpleNode extends Node {

    private final String text;

    public SimpleNode(String text) {
        this.text = text;

    }

    @Override
    public String toString() {
        return text;
    }
}
