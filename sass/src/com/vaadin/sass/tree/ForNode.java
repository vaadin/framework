/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

public class ForNode extends Node {
    private static final long serialVersionUID = -1159180539216623335L;

    String var;
    String from;
    String to;
    boolean exclusive;
    String body;

    public ForNode(String var, String from, String to, boolean exclusive,
            String body) {
        super();
        this.var = var;
        this.from = from;
        this.to = to;
        this.exclusive = exclusive;
        this.body = body;
    }

    @Override
    public String toString() {
        return "For Node: " + "{variable: " + var + ", from:" + from + ", to: "
                + to + ", exclusive: " + exclusive + ", body" + body;
    }

}
