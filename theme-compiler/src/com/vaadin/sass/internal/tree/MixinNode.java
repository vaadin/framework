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
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.visitor.MixinNodeHandler;

public class MixinNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 4725008226813110658L;

    private String name;
    private ArrayList<LexicalUnitImpl> arglist;

    public MixinNode(String name) {
        this.name = name;
        arglist = new ArrayList<LexicalUnitImpl>();
    }

    public MixinNode(String name, Collection<LexicalUnitImpl> args) {
        this(name);
        if (args != null && !args.isEmpty()) {
            arglist.addAll(args);
        }
    }

    @Override
    public String toString() {
        return "name: " + name + " args: " + arglist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LexicalUnitImpl> getArglist() {
        return arglist;
    }

    public void setArglist(ArrayList<LexicalUnitImpl> arglist) {
        this.arglist = arglist;
    }

    /**
     * Replace variable references with their values in the mixin argument list.
     */
    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode var : variables) {
            for (final LexicalUnitImpl arg : new ArrayList<LexicalUnitImpl>(
                    arglist)) {
                LexicalUnitImpl unit = arg;
                // only perform replace in the value if separate argument
                // name
                // and value
                if (unit.getNextLexicalUnit() != null) {
                    unit = unit.getNextLexicalUnit();
                }
                if (unit.getLexicalUnitType() == LexicalUnitImpl.SCSS_VARIABLE
                        && unit.getStringValue().equals(var.getName())) {
                    unit.replaceValue(var.getExpr());
                }
            }

            if (name.startsWith("$")) {
                if (name.equals("$" + var.getName())) {
                    name = var.getExpr().toString();
                }
            } else if (name.startsWith("#{") && name.endsWith("}")) {
                if (name.equals("#{$" + var.getName() + "}")) {
                    name = var.getExpr().toString();
                }
            }
        }
    }

    protected void replaceVariablesForChildren() {
        for (Node child : getChildren()) {
            if (child instanceof IVariableNode) {
                ((IVariableNode) child).replaceVariables(ScssStylesheet
                        .getVariables());
            }
        }
    }

    @Override
    public void traverse() {
        try {
            // limit variable scope to the mixin
            Map<String, VariableNode> variableScope = ScssStylesheet
                    .openVariableScope();

            replaceVariables(ScssStylesheet.getVariables());
            replaceVariablesForChildren();
            MixinNodeHandler.traverse(this);

            ScssStylesheet.closeVariableScope(variableScope);

        } catch (Exception e) {
            Logger.getLogger(MixinNode.class.getName()).log(Level.SEVERE, null,
                    e);
        }
    }

}
