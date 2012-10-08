package com.vaadin.sass.tree;

import java.util.ArrayList;

import com.vaadin.sass.parser.LexicalUnitImpl;

public class ListRemoveNode extends ListModifyNode {

    public ListRemoveNode(String variable, String list, String remove,
            String separator) {
        this.variable = variable;
        checkSeparator(separator, list);
        populateList(list, remove);

    }

    private boolean shouldInclude(LexicalUnitImpl current,
            LexicalUnitImpl lastAccepted) {

        if (lastAccepted != null
                && lastAccepted.getLexicalUnitType() == LexicalUnitImpl.SAC_OPERATOR_COMMA
                && current.getLexicalUnitType() == LexicalUnitImpl.SAC_OPERATOR_COMMA) {
            return false;
        }

        String string = current.getValue().toString();
        for (final String s : modify) {
            if (s.equals(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void modifyList(ArrayList<String> newList) {
        newList.removeAll(modify);
    }
}
