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
                            .quote(interpolation), node.getExpr().toString());
                }
            }
        }
    }

}
