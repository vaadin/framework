/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.menubar;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Tree;

public class MenuBarInSplitPanel extends AbstractTestCase {

    @Override
    protected String getDescription() {
        return "Move the splitter left so that some menu items are collapsed, then back right. The menu bar should always fill the available space.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6590;
    }

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("MenuBar in SplitPanel",
                new HorizontalSplitPanel());
        VerticalLayout left = new VerticalLayout();
        mainWindow.addComponent(left);
        left.setSizeFull();
        MenuBar menu = new MenuBar();
        menu.setWidth("100%");
        menu.addItem("File", null);
        menu.addItem("Edit", null);
        menu.addItem("Help", null);
        left.addComponent(menu);
        Tree tree = new Tree();
        for (int i = 0; i < 10; i++) {
            tree.addItem("Node " + i);
        }
        left.addComponent(tree);
        left.setExpandRatio(tree, 1.0f);
        Label label = new Label("Right");
        mainWindow.addComponent(label);
        setMainWindow(mainWindow);
    }

}
