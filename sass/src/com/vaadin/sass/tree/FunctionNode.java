package com.vaadin.sass.tree;

public class FunctionNode extends Node {
    private static final long serialVersionUID = -5383104165955523923L;

    private String name;
    private String args;
    private String body;

    public FunctionNode(String name) {
        super();
        this.name = name;
    }

    public FunctionNode(String name, String args, String body) {
        this.name = name;
        this.args = args;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Function Node: {name: " + name + ", args: " + args + ", body: "
                + body + "}";
    }
}
