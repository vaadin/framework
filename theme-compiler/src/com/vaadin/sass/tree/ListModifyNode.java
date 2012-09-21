package com.vaadin.sass.tree;

public interface ListModifyNode {

    public boolean isModifyingVariable();

    public String getVariable();

    public VariableNode getModifiedList(VariableNode variableNode);

}
