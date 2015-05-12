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
package com.vaadin.tests.components.tree;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Tree;

/**
 * Test UI for keyboard navigation for first and last tree item.
 * 
 * @author Vaadin Ltd
 */
public class TreeKeyboardNavigationToNone extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree();
        tree.addItem("a");
        tree.addItem("b");

        tree.select("a");
        addComponents(tree);
        tree.focus();

        Button button = new Button("Select last item",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        tree.select("b");
                        tree.focus();
                    }
                });
        addComponent(button);
    }

    @Override
    protected Integer getTicketNumber() {
        return 15343;
    }

    @Override
    protected String getTestDescription() {
        return "Keyboard navigation should not throw client side exception "
                + "when there are no items to navigate.";
    }

}
