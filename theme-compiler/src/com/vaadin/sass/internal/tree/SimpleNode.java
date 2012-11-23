package com.vaadin.sass.internal.tree;

import java.util.ArrayList;

import com.vaadin.sass.internal.ScssStylesheet;

/**
 * A simple BlockNode where input text equals output. <b>Note : </b> ignores any
 * possible children so only use it when you are sure no child nodes will be
 * applied.
 * 
 * @author Sebastian Nyholm @ Vaadin Ltd
 * 
 */
public class SimpleNode extends Node implements IVariableNode {

    private String text;

    public SimpleNode(String text) {
        this.text = text;

    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            if (text.contains(node.getName())) {
                text = text.replaceAll(node.getName(), node.getExpr()
                        .toString());
            }
        }
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }
}
