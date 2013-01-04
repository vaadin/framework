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

package com.vaadin.sass.internal.tree.controldirective;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.IVariableNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.visitor.EachNodeHandler;

public class EachDefNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 7943948981204906221L;

    private String var;
    private ArrayList<String> list;

    private String listVariable;

    public EachDefNode(String var, ArrayList<String> list) {
        super();
        this.var = var;
        this.list = list;
    }

    public EachDefNode(String var, String listVariable) {
        this.var = var;
        this.listVariable = listVariable;
    }

    public List<String> getVariables() {
        return list;
    }

    public String getVariableName() {
        return var;
    }

    @Override
    public String toString() {
        if (hasListVariable()) {
            return "Each Definition Node: {variable : " + var + ", "
                    + "listVariable : " + listVariable + "}";
        } else {
            return "Each Definition Node: {variable : " + var + ", "
                    + "children : " + list.size() + "}";
        }
    }

    public boolean hasListVariable() {
        return listVariable != null;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        if (listVariable != null) {
            for (final VariableNode var : variables) {
                if (listVariable.equals(var.getName())) {

                    LexicalUnitImpl current = var.getExpr();
                    list = new ArrayList<String>();

                    while (current != null) {
                        if (current.getValue() != null
                                && current.getLexicalUnitType() != LexicalUnitImpl.SAC_OPERATOR_COMMA) {
                            list.add(current.getValue().toString());
                        }
                        current = current.getNextLexicalUnit();
                    }
                    listVariable = null;
                    break;
                }
            }

        }
    }

    public String getListVariable() {
        return listVariable;
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
        EachNodeHandler.traverse(this);
    }
}
