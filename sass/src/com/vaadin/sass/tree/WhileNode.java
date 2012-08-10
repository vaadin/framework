/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

public class WhileNode extends Node {
    private static final long serialVersionUID = 7593896018196027279L;

    private String condition;
    private String body;

    public WhileNode(String condition, String body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "While Node: { condition: " + condition + ", body:" + body + "}";
    }

}
