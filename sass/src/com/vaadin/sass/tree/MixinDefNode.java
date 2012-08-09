/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

import java.util.ArrayList;
import java.util.Collection;

public class MixinDefNode extends Node {
    private static final long serialVersionUID = 5469294053247343948L;

    private String name;
    private ArrayList<VariableNode> arglist;
    private String args;
    private String body;

    public MixinDefNode(String name, Collection<VariableNode> args) {
        super();
        this.name = name;
        arglist = new ArrayList<VariableNode>();
        if (args != null && !args.isEmpty()) {
            arglist.addAll(args);
        }
    }

    public MixinDefNode(String name, String args, String body) {
        this.name = name;
        this.args = args;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Mixin Definition Node: {name: " + name + ", args: " + args
                + ", body: " + body + "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<VariableNode> getArglist() {
        return arglist;
    }

    public void setArglist(ArrayList<VariableNode> arglist) {
        this.arglist = arglist;
    }

}
