/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.sass.internal.tree;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;

public abstract class ListModifyNode extends Node implements IVariableNode {

    protected ArrayList<String> list;
    protected ArrayList<String> modify;
    protected String separator = " ";
    protected String variable;

    public String getNewVariable() {
        return variable;
    }

    public VariableNode getModifiedList() {
        final ArrayList<String> newList = new ArrayList<String>(list);
        modifyList(newList);

        LexicalUnitImpl unit = null;
        if (newList.size() > 0) {
            unit = LexicalUnitImpl.createIdent(newList.get(0));
            LexicalUnitImpl last = unit;
            for (int i = 1; i < newList.size(); i++) {
                LexicalUnitImpl current = LexicalUnitImpl.createIdent(newList
                        .get(i));
                last.setNextLexicalUnit(current);
                last = current;
            }

        }
        VariableNode node = new VariableNode(variable.substring(1), unit, false);
        return node;
    }

    protected abstract void modifyList(ArrayList<String> newList);

    protected void checkSeparator(String separator, String list) {
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

    protected void populateList(String list, String modify) {
        this.list = new ArrayList<String>(Arrays.asList(list.split(separator)));
        this.modify = new ArrayList<String>(Arrays.asList(modify
                .split(separator)));
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final String listVar : new ArrayList<String>(list)) {
            replacePossibleVariable(variables, listVar, list);
        }

        for (final String listVar : new ArrayList<String>(modify)) {
            replacePossibleVariable(variables, listVar, modify);
        }

    }

    private void replacePossibleVariable(ArrayList<VariableNode> variables,
            final String listVar, ArrayList<String> list) {
        if (listVar.startsWith("$")) {

            for (final VariableNode var : variables) {

                if (var.getName().equals(listVar.substring(1))) {

                    String[] split = null;
                    if (var.getExpr().toString().contains(",")) {
                        split = var.getExpr().toString().split(",");
                    } else {
                        split = var.getExpr().toString().split(" ");
                    }
                    int i = list.indexOf(listVar);
                    for (final String s : split) {
                        list.add(i, s.trim());
                        i++;
                    }

                    list.remove(listVar);
                    break;

                }
            }

        }
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
        ScssStylesheet.addVariable(getModifiedList());
        getParentNode().removeChild(this);
    }

}
