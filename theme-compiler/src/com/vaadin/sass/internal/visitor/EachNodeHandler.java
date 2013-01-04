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
package com.vaadin.sass.internal.visitor;

import java.util.ArrayList;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.IVariableNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.tree.controldirective.EachDefNode;
import com.vaadin.sass.internal.util.DeepCopy;

public class EachNodeHandler {

    public static void traverse(EachDefNode node) {
        replaceEachDefNode(node);
    }

    private static void replaceEachDefNode(EachDefNode defNode) {
        Node last = defNode;

        for (final String var : defNode.getVariables()) {
            VariableNode varNode = new VariableNode(defNode.getVariableName()
                    .substring(1), LexicalUnitImpl.createIdent(var), false);
            ArrayList<VariableNode> variables = new ArrayList<VariableNode>(
                    ScssStylesheet.getVariables());
            variables.add(varNode);

            for (final Node child : defNode.getChildren()) {

                Node copy = (Node) DeepCopy.copy(child);

                replaceInterpolation(copy, variables);

                defNode.getParentNode().appendChild(copy, last);
                last = copy;
            }

        }
        defNode.setChildren(new ArrayList<Node>());
        defNode.getParentNode().removeChild(defNode);
    }

    private static void replaceInterpolation(Node copy,
            ArrayList<VariableNode> variables) {
        if (copy instanceof IVariableNode) {
            IVariableNode n = (IVariableNode) copy;
            n.replaceVariables(variables);
        }

        for (Node c : copy.getChildren()) {
            replaceInterpolation(c, variables);
        }

    }

}
