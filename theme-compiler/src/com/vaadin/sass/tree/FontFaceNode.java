package com.vaadin.sass.tree;

public class FontFaceNode extends Node {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("@font-face {\n");

        for (final Node child : children) {
            builder.append("\t" + child.toString() + "\n");
        }

        builder.append("}");
        return builder.toString();
    }

}
