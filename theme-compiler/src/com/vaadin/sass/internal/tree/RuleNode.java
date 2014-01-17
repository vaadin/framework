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
import java.util.regex.Pattern;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.expression.ArithmeticExpressionEvaluator;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.util.StringUtil;

public class RuleNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 6653493127869037022L;

    String variable;
    LexicalUnitImpl value;
    String comment;
    private boolean important;

    public RuleNode(String variable, LexicalUnitImpl value, boolean important,
            String comment) {
        this.variable = variable;
        this.value = value;
        this.important = important;
        this.comment = comment;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public LexicalUnitImpl getValue() {
        return value;
    }

    public void setValue(LexicalUnitImpl value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String stringValue = value.toString()
                + (important ? " !important" : "");
        if (!"".equals(stringValue.trim())) {
            stringValue = variable + ": " + stringValue + ";";
        } else {
            stringValue = "";
        }

        if (comment != null) {
            stringValue += comment;
        }
        return stringValue;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {

            String interpolation = "#{$" + node.getName() + "}";

            if (variable != null && variable.contains(interpolation)) {
                variable = variable.replaceAll(Pattern.quote(interpolation),
                        node.getExpr().unquotedString());

            }

            if (value.getLexicalUnitType() == LexicalUnitImpl.SAC_FUNCTION) {

                if (value.getParameters() != null) {
                    if (StringUtil.containsVariable(value.getParameters()
                            .toString(), node.getName())) {
                        LexicalUnitImpl param = value.getParameters();
                        while (param != null) {
                            if (param.getLexicalUnitType() == LexicalUnitImpl.SCSS_VARIABLE
                                    && param.getValue().toString()
                                            .equals(node.getName())) {
                                param.replaceValue(node.getExpr());
                            }
                            param = param.getNextLexicalUnit();
                        }
                    }
                }
            } else if (value.getStringValue() != null
                    && value.getStringValue().contains(interpolation)) {
                LexicalUnitImpl current = value;
                while (current != null) {
                    if (current.getValue().toString().contains(interpolation)) {

                        current.setStringValue(current
                                .getValue()
                                .toString()
                                .replaceAll(Pattern.quote(interpolation),
                                        node.getExpr().unquotedString()));
                    }
                    current = current.getNextLexicalUnit();
                }
            } else {
                LexicalUnitImpl current = value;
                while (current != null) {
                    if (current.getLexicalUnitType() == LexicalUnitImpl.SCSS_VARIABLE
                            && current.getValue().toString()
                                    .equals(node.getName())) {

                        current.replaceValue(node.getExpr());
                    }
                    current = current.getNextLexicalUnit();
                }
            }
        }
    }

    @Override
    public void traverse() {
        /*
         * "replaceVariables(ScssStylesheet.getVariables());" seems duplicated
         * and can be extracted out of if, but it is not.
         * containsArithmeticalOperator must be called before replaceVariables.
         * Because for the "/" operator, it needs to see if its predecessor or
         * successor is a Variable or not, to determine it is an arithmetic
         * operator.
         */
        if (ArithmeticExpressionEvaluator.get().containsArithmeticalOperator(
                value)) {
            replaceVariables(ScssStylesheet.getVariables());
            value = ArithmeticExpressionEvaluator.get().evaluate(value);
        } else {
            replaceVariables(ScssStylesheet.getVariables());
        }
    }
}
