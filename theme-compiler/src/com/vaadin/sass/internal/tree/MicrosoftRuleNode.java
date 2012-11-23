package com.vaadin.sass.internal.tree;

import java.util.ArrayList;

import com.vaadin.sass.internal.ScssStylesheet;

public class MicrosoftRuleNode extends Node implements IVariableNode {

    private final String name;
    private String value;

    public MicrosoftRuleNode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode var : variables) {
            if (value.contains("$" + var.getName())) {
                value = value.replaceAll("$" + var.getName(), var.getExpr()
                        .toString());
            }
        }
    }

    @Override
    public String toString() {
        return name + ": " + value + ";";
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }
}
