package com.vaadin.tests.components.tree;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Tree;

public class TreeDefaultConstructor extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Tree<>());
    }
}
