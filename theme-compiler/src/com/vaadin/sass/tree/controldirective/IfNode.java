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
package com.vaadin.sass.tree.controldirective;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.tree.IVariableNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.VariableNode;

public class IfNode extends Node implements IfElseNode, IVariableNode {
    private String expression;

    public IfNode(String expression) {
        this.expression = expression;
    }

    @Override
    public String getExpression() {
        if (expression != null) {
            return expression.trim();
        } else {
            return "false";
        }
    }

    @Override
    public String toString() {
        return "@if" + expression;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            String variable = "$" + node.getName();
            if (expression.contains(variable)) {
                expression = expression.replaceAll(Pattern.quote(variable),
                        node.getExpr().toString());
            }
        }
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }

}