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
import com.vaadin.sass.internal.parser.LexicalUnitImpl;

import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.ReturnNode;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class ReturnNodeHandler {

    private static final JexlEngine evaluator = new JexlEngine();
    private static final Pattern pattern = Pattern
            .compile("[a-zA-Z0-9]*[a-zA-Z]+[a-zA-Z0-9]*");

    public static void traverse(ReturnNode node) throws Exception {

        String s = new String();
        for (final Node child : new ArrayList<Node>(node.getChildren())) {
            child.traverse();
            String temp = child.toString();
            if (temp != null) {
                s = s.concat(" ").concat(temp);
            }
        }

        node.setExpression(s);
    }

    public static LexicalUnitImpl evaluateExpression(String expression) {

        LexicalUnitImpl retVal = null;
        try {
            // We need to add ' ' for strings in the expression for
            // jexl to understand that it should do a string
            // comparison
            expression = replaceStrings(expression);
            Expression e = evaluator.createExpression(expression);
            try {
                //do we need to worry about numerical error?
                //changing to double requires a refactor of LexicalUnitImpl...
                Float eval = (Float)e.evaluate(null);
                retVal = new LexicalUnitImpl(0,0,null,eval);
            } catch (ClassCastException ex) {
                throw new ParseException(
                        "Invalid @return in scss file, not a boolean expression : "
                                + child.toString());
            } catch (NullPointerException ex) {
                throw new ParseException(
                        "Invalid @return in scss file, not a boolean expression : "
                                + child.toString());
            }
        } catch (JexlException e) {
            throw new ParseException(
                    "Invalid @return in scss file for "
                            + child.toString());
        }
        return retVal;
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
            parent.appendChild(n, defNode);//won't this append the children in reverse order? =defNode, lastChild, 2ndLastChild, ... , 1stChild
        }
    }
}
