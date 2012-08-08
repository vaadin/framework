package com.vaadin.sass.tree;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.css.sac.LexicalUnit;

public class MixinNode extends Node {
    private static final long serialVersionUID = 4725008226813110658L;

    private String name;
    private ArrayList<LexicalUnit> arglist;

    public MixinNode(String name, Collection<LexicalUnit> args) {
        super();
        this.name = name;
        arglist = new ArrayList<LexicalUnit>();
        if (args != null && !args.isEmpty()) {
            arglist.addAll(args);
        }
    }

    @Override
    public String toString() {
        return "name: " + name + " args: " + arglist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LexicalUnit> getArglist() {
        return arglist;
    }

    public void setArglist(ArrayList<LexicalUnit> arglist) {
        this.arglist = arglist;
    }
}
