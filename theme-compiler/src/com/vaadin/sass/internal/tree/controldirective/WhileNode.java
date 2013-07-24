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

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.tree.IVariableNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.util.StringUtil;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class WhileNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 7593896018196027279L;

    private String expression;

    public WhileNode(String expression) {
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
        return "@while" + expression;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            if (StringUtil.containsVariable(expression, node.getName())) {
                expression = StringUtil.replaceVariable(expression,
                        node.getName(), node.getExpr().toString());
            }
        }
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }

}
