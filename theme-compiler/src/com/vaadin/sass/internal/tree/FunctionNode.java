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

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.util.StringUtil;

public class FunctionNode extends Node implements IVariableNode {
    private static final long serialVersionUID = -5383104165955523923L;

    private String name;
    private String args;
    private String body;

    public FunctionNode(String name) {
        super();
        this.name = name;
    }

    public FunctionNode(String name, String args, String body) {
        this.name = name;
        this.args = args;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Function Node: {name: " + name + ", args: " + args + ", body: "
                + body + "}";
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            if (StringUtil.containsVariable(args, node.getName())) {
                args = StringUtil.replaceVariable(args, node.getName(), node
                        .getExpr().toString());
            }
        }
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }
}
