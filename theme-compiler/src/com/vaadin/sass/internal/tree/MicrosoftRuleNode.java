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

public class MicrosoftRuleNode extends Node implements IVariableNode {

    private final String name;
    private String value;

    public MicrosoftRuleNode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode var : variables) {
            if (StringUtil.containsVariable(value, var.getName())) {
                value = StringUtil.replaceVariable(value, var.getName(), var
                        .getExpr().toString());
            }
        }
    }

    @Override
    public String toString() {
        return name + ": " + value + ";";
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }
}
