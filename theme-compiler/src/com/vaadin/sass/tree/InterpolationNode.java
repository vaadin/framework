package com.vaadin.sass.tree;

public interface InterpolationNode {
    public void replaceInterpolation(String variableName, String variable);

    public boolean containsInterpolationVariable(String variable);
}
