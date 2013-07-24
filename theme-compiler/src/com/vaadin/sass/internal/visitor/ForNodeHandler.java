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
import com.vaadin.sass.internal.tree.controldirective.ForNode;
import com.vaadin.sass.internal.util.DeepCopy;

import org.w3c.flute.parser.ParseException;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class ForNodeHandler {

    public static void traverse(ForNode node) {
        replaceForNode(node);
    }

    private static void replaceForNode(ForNode forNode) {
        Node last = forNode;

        //Sass-lang.com Implementation:
        // * cannot count down
        // * can only iterate by 1
        LexicalUnitImpl from = forNode.getFrom();
        LexicalUnitImpl to = forNode.getTo();

        if (from.getLexicalUnitType() != 
            LexicalUnit.SAC_INTEGER) {

            throw new ParseException("Invalid @for in scss file, " + 
                "'from' is not an integer expression : " + from );
        }

        if (to.getLexicalUnitType() != 
            LexicalUnit.SAC_INTEGER) {

            throw new ParseException("Invalid @for in scss file, " + 
                "'to' is not an integer expression : " + to );
        } else if (from.getIntegerValue() > to.getIntegerValue()) {

            throw new ParseException("Invalid @for in scss file, " + 
                "'from' (" + from.getIntegerValue() + 
                ") cannot be larger than 'to' (" + to.geIntegerValue() +
                ")" );
        }

        int i = from.getIntegerValue();
        int j = (forNode.inclusive) ? 1 : 0;
        j += to.getIntegerValue();

        for (int var = i; var < j; var++) {

            VariableNode varNode = new VariableNode(
                defNode.getVariableName().substring(1), 
                new LexicalUnitImpl(0,0,null,var), 
                false);

            ArrayList<VariableNode> variables = new ArrayList<VariableNode>(
                    ScssStylesheet.getVariables());
            variables.add(varNode);

            for (final Node child : forNode.getChildren()) {

                Node copy = (Node) DeepCopy.copy(child);

                replaceInterpolation(copy, variables);

                forNode.getParentNode().appendChild(copy, last);
                last = copy;
            }

        }
        forNode.setChildren(new ArrayList<Node>());
        forNode.getParentNode().removeChild(forNode);
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
