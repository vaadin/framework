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

public class KeyframesNode extends Node implements IVariableNode {
    private String keyframeName;
    private String animationName;

    public KeyframesNode(String keyframeName, String animationName) {
        this.keyframeName = keyframeName;
        this.animationName = animationName;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(keyframeName).append(" ").append(animationName)
                .append(" {\n");
        for (Node child : children) {
            string.append("\t\t").append(child.toString()).append("\n");
        }
        string.append("\t}");
        return string.toString();
    }

    @Override
    public void traverse() {
        replaceVariables(ScssStylesheet.getVariables());
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode node : variables) {
            String interpolation = "#{$" + node.getName() + "}";
            if (animationName != null && animationName.contains(interpolation)) {
                if (animationName.contains(interpolation)) {
                    animationName = animationName.replaceAll(Pattern
                            .quote(interpolation), node.getExpr()
                            .unquotedString());
                }
            }
        }
    }

}
