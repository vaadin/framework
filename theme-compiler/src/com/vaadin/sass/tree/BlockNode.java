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

public class BlockNode extends Node implements IVariableNode, InterpolationNode {

    private static final long serialVersionUID = 5742962631468325048L;

    ArrayList<String> selectorList;

    public BlockNode(ArrayList<String> selectorList) {
        this.selectorList = selectorList;
    }

    public ArrayList<String> getSelectorList() {
        return selectorList;
    }

    public void setSelectorList(ArrayList<String> selectorList) {
        this.selectorList = selectorList;
    }

    public String toString(boolean indent) {
        StringBuilder string = new StringBuilder();
        int i = 0;
        for (final String s : selectorList) {
            string.append(s);
            if (i != selectorList.size() - 1) {
                string.append(", ");
            }
            i++;
        }
        string.append(" {\n");
        for (Node child : children) {
            if (indent) {
                string.append("\t");
            }
            string.append("\t" + child.toString() + "\n");
        }
        if (indent) {
            string.append("\t");
        }
        string.append("}");
        return string.toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {

        if (selectorList == null || selectorList.isEmpty()) {
            return;
        }

        for (final VariableNode var : variables) {
            for (final String selector : new ArrayList<String>(selectorList)) {
                String interpolation = "#{$" + var.getName() + "}";
                if (selector.contains(interpolation)) {
                    String replace = selector.replace(interpolation, var
                            .getExpr().toString());
                    selectorList.add(selectorList.indexOf(selector), replace);
                    selectorList.remove(selector);
                }
            }
        }
    }

    public String getSelectors() {
        StringBuilder b = new StringBuilder();
        for (final String s : selectorList) {
            b.append(s);
        }

        return b.toString();
    }

    @Override
    public void replaceInterpolation(String variableName, String variable) {
        if (selectorList == null || selectorList.isEmpty()) {
            return;
        }

        for (final String selector : new ArrayList<String>(selectorList)) {
            String interpolation = "#{" + variableName + "}";
            if (selector.contains(interpolation)) {
                String replace = selector.replace(interpolation, variable);
                selectorList.add(selectorList.indexOf(selector), replace);
                selectorList.remove(selector);
            }
        }
    }

    @Override
    public boolean containsInterpolationVariable(String variable) {
        return getSelectors().contains(variable);
    }

}
