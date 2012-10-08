package com.vaadin.sass.tree;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.sass.parser.LexicalUnitImpl;
import com.vaadin.sass.util.DeepCopy;

public class ListRemoveNode extends Node implements ListModifyNode,
        IVariableNode {

    private ArrayList<String> list;
    private ArrayList<String> remove;
    private String separator = " ";
    private String variable;

    public ListRemoveNode(String variable, String list, String remove,
            String separator) {
        this.variable = variable;
        checkSeparator(separator, list);

        populateList(list, remove);

    }

    private void checkSeparator(String separator, String list) {
        String lowerCase = "";
        if (separator == null
                || (lowerCase = separator.toLowerCase()).equals("auto")) {
            if (list.contains(",")) {
                this.separator = ",";
            }
        } else if (lowerCase.equals("comma")) {
            this.separator = ",";
        } else if (lowerCase.equals("space")) {
            this.separator = " ";
        }
    }

    private void populateList(String list, String remove) {
        this.list = new ArrayList<String>(Arrays.asList(list.split(separator)));
        this.remove = new ArrayList<String>(Arrays.asList(remove
                .split(separator)));
    }

    @Override
    public String getNewVariable() {
        return variable;
    }

    @Override
    public VariableNode getModifiedList(VariableNode variableNode) {

        if (variableNode != null) {
            VariableNode clone = (VariableNode) DeepCopy.copy(variableNode);

            LexicalUnitImpl first = null;
            LexicalUnitImpl current = (LexicalUnitImpl) clone.getExpr();
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
                current = (LexicalUnitImpl) current.getNextLexicalUnit();
            }

            clone.setExpr(first);

            return clone;
        } else {

            final ArrayList<String> newList = new ArrayList<String>(list);
            newList.removeAll(remove);

            LexicalUnitImpl unit = null;
            if (newList.size() > 0) {
                unit = LexicalUnitImpl.createString(newList.get(0));
                LexicalUnitImpl last = unit;
                for (int i = 1; i < newList.size(); i++) {
                    LexicalUnitImpl current = LexicalUnitImpl
                            .createString(newList.get(i));
                    last.setNextLexicalUnit(current);
                    last = current;
                }

            }
            VariableNode node = new VariableNode(variable, unit, false);
            return node;

        }
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

    @Override
    public String getModifyingList() {
        String firstListEntry = list.get(0);
        if (list.size() == 1 && firstListEntry.startsWith("$")) {
            return firstListEntry.substring(1, firstListEntry.length());
        }

        return null;
    }
}
