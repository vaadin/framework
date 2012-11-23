package com.vaadin.sass.internal.tree;

import java.util.ArrayList;

public class ListAppendNode extends ListModifyNode {

    public ListAppendNode(String variable, String list, String append,
            String separator) {
        this.variable = variable;
        checkSeparator(separator, list);
        populateList(list, append);
    }

    @Override
    protected void modifyList(ArrayList<String> newList) {
        newList.addAll(modify);
    }

}
