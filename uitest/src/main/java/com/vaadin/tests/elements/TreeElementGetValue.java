package com.vaadin.tests.elements;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.Tree;

/**
 *
 * @author Vaadin Ltd
 */
public class TreeElementGetValue extends AbstractTestUI {

    public static final String TEST_VALUE = "testValue";
    public static final String TEST_VALUE_LVL2 = "testValueLvl2";
    public static final String TEST_VALUE_LVL3 = "testValueLvl3";

    @Override
    protected void setup(VaadinRequest request) {
        Tree tree = new Tree();
        tree.addItem("item1");
        tree.addItem("item1_1");
        tree.addItem("item1_2");
        tree.setParent("item1_1", "item1");
        tree.setParent("item1_2", "item1");
        tree.addItem(TEST_VALUE);
        tree.addItem(TEST_VALUE_LVL2);
        tree.addItem(TEST_VALUE_LVL3);
        tree.setParent(TEST_VALUE_LVL2, TEST_VALUE);
        tree.setParent(TEST_VALUE_LVL3, TEST_VALUE_LVL2);
        tree.addItem("item3");
        tree.setValue(TEST_VALUE_LVL2);
        tree.expandItem(TEST_VALUE);
        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Method getValue() should return selected item of the tree";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13455;
    }

}
