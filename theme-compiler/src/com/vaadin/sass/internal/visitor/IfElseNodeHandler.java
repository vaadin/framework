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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.w3c.flute.parser.ParseException;

import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.controldirective.ElseNode;
import com.vaadin.sass.internal.tree.controldirective.IfElseDefNode;
import com.vaadin.sass.internal.tree.controldirective.IfElseNode;
import com.vaadin.sass.internal.tree.controldirective.IfNode;

public class IfElseNodeHandler {

    private static final JexlEngine evaluator = new JexlEngine();
    private static final Pattern pattern = Pattern
            .compile("[a-zA-Z0-9]*[a-zA-Z]+[a-zA-Z0-9]*");

    public static void traverse(IfElseDefNode node) throws Exception {

        for (final Node child : node.getChildren()) {
            if (child instanceof IfNode) {
                try {
                    String expression = ((IfElseNode) child).getExpression();
                    // We need to add ' ' for strings in the expression for
                    // jexl to understand that is should do a string
                    // comparison
                    expression = replaceStrings(expression);
                    Expression e = evaluator.createExpression(expression);
                    try {
                        Object eval = e.evaluate(null);

                        Boolean result = false;
                        if (eval instanceof Boolean) {
                            result = (Boolean) eval;
                        } else if (eval instanceof String) {
                            result = Boolean.valueOf((String) eval);
                        }

                        if (result) {
                            replaceDefNodeWithCorrectChild(node,
                                    node.getParentNode(), child);
                            break;
                        }
                    } catch (ClassCastException ex) {
                        throw new ParseException(
                                "Invalid @if/@else in scss file, not a boolean expression : "
                                        + child.toString());
                    } catch (NullPointerException ex) {
                        throw new ParseException(
                                "Invalid @if/@else in scss file, not a boolean expression : "
                                        + child.toString());
                    }
                } catch (JexlException e) {
                    throw new ParseException(
                            "Invalid @if/@else in scss file for "
                                    + child.toString());
                }
            } else {
                if (!(child instanceof ElseNode)
                        && node.getChildren().indexOf(child) == node
                                .getChildren().size() - 1) {
                    throw new ParseException(
                            "Invalid @if/@else in scss file for " + node);
                } else {
                    replaceDefNodeWithCorrectChild(node, node.getParentNode(),
                            child);
                    break;
                }
            }
        }

        node.getParentNode().removeChild(node);
    }

    private static String replaceStrings(String expression) {
        expression = expression.replaceAll("\"", "");
        Matcher m = pattern.matcher(expression);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            m.appendReplacement(b, "'" + group + "'");
        }
        m.appendTail(b);
        if (b.length() != 0) {
            return b.toString();
        }
        return expression;
    }

    private static void replaceDefNodeWithCorrectChild(IfElseDefNode defNode,
            Node parent, final Node child) {
        for (final Node n : new ArrayList<Node>(child.getChildren())) {
            parent.appendChild(n, defNode);
        }
    }
}
