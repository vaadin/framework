package com.vaadin.sass.tree;

public interface ListModifyNode {

    public String getNewVariable();

    public String getModifyingList();

    public VariableNode getModifiedList(VariableNode variableNode);

}
