package com.vaadin.v7.tests.components.tree;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.table.DndTableTargetDetails;
import com.vaadin.v7.ui.Tree;

/**
 * Test UI for tree as a drop target: AbstractSelectTargetDetails should provide
 * getMouseEvent() method.
 *
 * @author Vaadin Ltd
 */
public class DndTreeTargetDetails extends DndTableTargetDetails {

    @Override
    protected void setup(VaadinRequest request) {
        createSourceTable();

        Tree target = new Tree();
        target.addStyleName("target");
        target.setWidth(100, Unit.PERCENTAGE);
        target.addItem("treeItem");
        target.setDropHandler(new TestDropHandler());
        addComponent(target);
    }

    @Override
    protected String getTestDescription() {
        return "Mouse details should be available for AbstractSelectTargetDetails DnD when tree is a target";
    }

}
