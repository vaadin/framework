package com.vaadin.sass.tree;

import java.util.ArrayList;

import com.vaadin.sass.parser.LexicalUnitImpl;

public class ListContainsNode extends ListModifyNode {

    public ListContainsNode(String variable, String list, String contains,
            String separator) {
        this.variable = variable;
        checkSeparator(separator, list);
        populateList(list, contains);
    }

    @Override
    protected void modifyList(ArrayList<String> newList) {
        // Does not actually modify the list
    }

    @Override
    public VariableNode getModifiedList() {

        String contains = "" + list.containsAll(modify);
        VariableNode node = new VariableNode(variable.substring(1),
                LexicalUnitImpl.createString(contains), false);
        return node;

    }

}
