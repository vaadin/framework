package com.vaadin.sass.tree;

import java.util.ArrayList;

import com.vaadin.sass.parser.LexicalUnitImpl;
import com.vaadin.sass.util.DeepCopy;

public class ListRemoveNode extends Node implements ListModifyNode,
        IVariableNode {

    private ArrayList<String> list;
    private ArrayList<String> remove;
    private String separator;

    public ListRemoveNode(ArrayList<String> list, ArrayList<String> remove,
            String separator) {
        this.list = list;
        this.remove = remove;
        this.separator = separator;
    }

    @Override
    public boolean isModifyingVariable() {
        if (list != null) {
            return list.size() == 1 && list.get(0).startsWith("$");
        }
        return false;
    }

    @Override
    public String getVariable() {
        if (list != null && list.size() == 1) {
            String string = list.get(0);
            return string.substring(1, string.length());
        }
        return null;
    }

    @Override
    public VariableNode getModifiedList(VariableNode variableNode) {

        VariableNode clone = (VariableNode) DeepCopy.copy(variableNode);

        LexicalUnitImpl first = null;
        LexicalUnitImpl current = clone.getExpr();
        LexicalUnitImpl lastAccepted = null;
        while (current != null) {

            if (shouldInclude(current, lastAccepted)) {
                LexicalUnitImpl temp = current.clone();
                temp.setNextLexicalUnit(null);

                if (lastAccepted != null) {
                    lastAccepted.setNextLexicalUnit(temp);
                }

                lastAccepted = temp;

                if (first == null) {
                    first = lastAccepted;
                }
            }
            current = current.getNextLexicalUnit();
        }

        clone.setExpr(first);

        return clone;
    }

    private boolean shouldInclude(LexicalUnitImpl current,
            LexicalUnitImpl lastAccepted) {

        if (lastAccepted != null
                && lastAccepted.getLexicalUnitType() == LexicalUnitImpl.SAC_OPERATOR_COMMA
                && current.getLexicalUnitType() == LexicalUnitImpl.SAC_OPERATOR_COMMA) {
            return false;
        }

        String string = current.getValue().toString();
        for (final String s : remove) {
            if (s.equals(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        ArrayList<String> newList = new ArrayList<String>();

        for (final String removeVar : remove) {
            if (!removeVar.startsWith("$")) {
                continue;
            }

            for (final VariableNode var : variables) {
                if (removeVar.equals("$" + var.getName())) {
                    LexicalUnitImpl expr = var.getExpr();
                    while (expr != null) {
                        newList.add(expr.getValue().toString());
                        expr = expr.getNextLexicalUnit();
                    }

                }
            }
        }
        if (newList.size() > 0) {
            remove = newList;
        }

    }
}
