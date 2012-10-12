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

package com.vaadin.sass.visitor;

import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.sass.tree.IVariableNode;
import com.vaadin.sass.tree.ListModifyNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.VariableNode;

public class VariableVisitor implements Visitor {

    private final HashMap<String, VariableNode> variables = new HashMap<String, VariableNode>();

    @Override
    public void traverse(Node node) {

        replaceVariables(node, node.getChildren());

        removeVariableNodes(node, node);
    }

    private void removeVariableNodes(Node parent, Node node) {
        for (final Node child : new ArrayList<Node>(node.getChildren())) {
            removeVariableNodes(node, child);
        }
        if (node instanceof VariableNode) {
            for (final Node child : node.getChildren()) {
                parent.appendChild(child, node);
            }
            parent.removeChild(node);
        }
    }

    private void replaceVariables(Node n, ArrayList<Node> children) {

        ArrayList<VariableNode> variables = new ArrayList<VariableNode>(
                this.variables.values());

        for (Node node : children) {
            if (node instanceof VariableNode) {

                VariableNode variableNode = (VariableNode) node;
                if (this.variables.containsKey(variableNode.getName())
                        && variableNode.isGuarded()) {
                    continue;
                }
                this.variables.put(variableNode.getName(), variableNode);
            } else if (node instanceof ListModifyNode) {

                ((IVariableNode) node)
                        .replaceVariables(new ArrayList<VariableNode>(
                                this.variables.values()));

                ListModifyNode modify = (ListModifyNode) node;

                String variable = modify.getNewVariable().substring(1);

                try {
                    VariableNode modifiedList = modify.getModifiedList();

                    this.variables.put(variable, modifiedList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (node instanceof IVariableNode) {
                ((IVariableNode) node)
                        .replaceVariables(new ArrayList<VariableNode>(
                                this.variables.values()));
            }

            replaceVariables(node, node.getChildren());
        }

        for (final VariableNode v : variables) {
            this.variables.put(v.getName(), v);
        }
    }

}
