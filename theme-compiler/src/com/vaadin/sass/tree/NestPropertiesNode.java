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
import java.util.Collection;
import java.util.List;

import com.vaadin.sass.visitor.NestedNodeHandler;

public class NestPropertiesNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 3671253315690598308L;

    public NestPropertiesNode(String name) {
        super();
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<RuleNode> unNesting() {
        List<RuleNode> result = new ArrayList<RuleNode>();
        for (Node child : children) {
            RuleNode createNewRuleNodeFromChild = createNewRuleNodeFromChild((RuleNode) child);
            result.add(createNewRuleNodeFromChild);
        }
        return result;
    }

    public RuleNode createNewRuleNodeFromChild(RuleNode child) {
        StringBuilder builder = new StringBuilder(name);
        builder.append("-").append(child.getVariable());
        RuleNode newRuleNode = new RuleNode(builder.toString(),
                child.getValue(), child.isImportant(), null);
        return newRuleNode;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            if (name.contains(node.getName())) {
                name = name.replaceAll(node.getName(), node.getExpr()
                        .toString());
            }
        }
    }

    @Override
    public void traverse() {
        NestedNodeHandler.traverse(this);
    }

}
