package com.vaadin.sass.visitor;

import com.vaadin.sass.tree.Node;

public interface Visitor {

    public void traverse(Node node) throws Exception;
}
