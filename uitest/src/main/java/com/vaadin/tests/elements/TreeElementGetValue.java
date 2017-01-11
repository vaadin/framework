/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.elements;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.Tree;

/**
 *
 * @since
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
