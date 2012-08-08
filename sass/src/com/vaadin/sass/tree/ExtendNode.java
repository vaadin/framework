package com.vaadin.sass.tree;

import org.w3c.css.sac.SelectorList;

public class ExtendNode extends Node {
    private static final long serialVersionUID = 3301805078983796878L;

    SelectorList list;

    public ExtendNode(SelectorList list) {
        super();
        this.list = list;
    }

    public SelectorList getList() {
        return list;
    }

}
