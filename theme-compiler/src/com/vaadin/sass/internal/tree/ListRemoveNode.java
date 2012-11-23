package com.vaadin.sass.internal.tree;

import java.util.ArrayList;

public class ListRemoveNode extends ListModifyNode {

    public ListRemoveNode(String variable, String list, String remove,
            String separator) {
        this.variable = variable;
        checkSeparator(separator, list);
        populateList(list, remove);

    }

    @Override
    protected void modifyList(ArrayList<String> newList) {
        newList.removeAll(modify);
    }

}
