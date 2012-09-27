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
import java.util.regex.Pattern;

import com.vaadin.sass.parser.LexicalUnitImpl;

public class RuleNode extends Node implements IVariableNode, InterpolationNode {
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
        StringBuilder builder = new StringBuilder();
        builder.append(variable).append(": ").append(value.toString());
        builder.append(important ? " !important;" : ";");
        if (comment != null) {
            builder.append(comment);
        }
        return builder.toString();
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
            if (value.getLexicalUnitType() == LexicalUnitImpl.SAC_FUNCTION) {
                if (value.getParameters().toString()
                        .contains("$" + node.getName())) {
                    if (value.getParameters() != null) {
                        if (value.getParameters().toString()
                                .contains(node.getName())) {

                            LexicalUnitImpl param = value.getParameters();
                            while (param != null) {
                                if (param.getValue().toString()
                                        .contains(node.getName())) {

                                    LexicalUnitImpl expr = node.getExpr();

                                    LexicalUnitImpl prev = param
                                            .getPreviousLexicalUnit();
                                    LexicalUnitImpl next = param
                                            .getNextLexicalUnit();

                                    if (param.getLexicalUnitType() == LexicalUnitImpl.SCSS_VARIABLE) {
                                        param.replaceValue(expr);
                                        param.setPrevLexicalUnit(prev);
                                        param.setNextLexicalUnit(next);
                                    }
                                }
                                param = param.getNextLexicalUnit();
                            }
                        }
                    }
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
    public void replaceInterpolation(String variableName, String variable) {
        if (this.variable.contains(variableName)) {
            this.variable = this.variable.replaceAll(variableName, variable);
        }

        if (value.toString().contains(variableName)) {

            LexicalUnitImpl current = value;
            while (current != null) {
                if (current.getValue().toString().contains(variableName)) {
                    current.setStringValue(current
                            .getValue()
                            .toString()
                            .replaceAll(
                                    Pattern.quote("#{" + variableName + "}"),
                                    variable));
                }

                current = value.getNextLexicalUnit();
            }
        }
    }

    @Override
    public boolean containsInterpolationVariable(String variable) {
        return value.toString().contains(variable)
                || this.variable.contains(variable);
    }
}
