/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.sass.tree;

import java.util.ArrayList;

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.parser.LexicalUnitImpl;
import com.vaadin.sass.util.DeepCopy;

public class VariableNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 7003372557547748734L;

    private String name;
    private LexicalUnitImpl expr;
    private boolean guarded;

    public VariableNode(String name, LexicalUnitImpl expr, boolean guarded) {
        super();
        this.name = name;
        this.expr = expr;
        this.guarded = guarded;
    }

    public LexicalUnitImpl getExpr() {
        return expr;
    }

    public void setExpr(LexicalUnitImpl expr) {
        this.expr = expr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isGuarded() {
        return guarded;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("$");
        builder.append(name).append(": ").append(expr);
        return builder.toString();
    }

    public void setGuarded(boolean guarded) {
        this.guarded = guarded;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            if (!equals(node)) {

                if (name.equals(node.getName())) {
                    expr = (LexicalUnitImpl) DeepCopy.copy(node.getExpr());
                    guarded = node.isGuarded();
                    continue;
                }

                LexicalUnit current = expr;
                while (current != null) {
                    if (current.toString().contains(node.getName())) {
                        ((LexicalUnitImpl) current)
                                .replaceValue(node.getExpr());
                    }

                    current = current.getNextLexicalUnit();
                }

            }
        }
    }
}
