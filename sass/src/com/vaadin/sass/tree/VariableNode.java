/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

import org.w3c.css.sac.LexicalUnit;

public class VariableNode extends Node {
    private static final long serialVersionUID = 7003372557547748734L;

    private String name;
    private LexicalUnit expr;
    private boolean guarded;

    public VariableNode(String name, LexicalUnit expr, boolean guarded) {
        super();
        this.name = name;
        this.expr = expr;
        this.guarded = guarded;
    }

    public VariableNode(String name, String raw) {
        super(raw);
        this.name = name;
    }

    public LexicalUnit getExpr() {
        return expr;
    }

    public void setExpr(LexicalUnit expr) {
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public boolean isGuarded() {
        return guarded;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("$");
        builder.append(name).append(": ").append(expr);
        return builder.toString();
    }

    public void setGuarded(boolean guarded) {
        this.guarded = guarded;
    }

}
