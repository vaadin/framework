package com.vaadin.sass.tree;

public class EachNode extends Node {
    private static final long serialVersionUID = 7943948981204906221L;

    private String var;
    private String list;
    private String body;

    public EachNode(String var, String list, String body) {
        super();
        this.var = var;
        this.list = list;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Each Node: {variable: " + var + ", list: " + list + ", body: "
                + body + "}";
    }
}
