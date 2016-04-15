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
package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

/**
 * A test UI for context menus on different parts of a VSCrollTable.
 *
 * This UI has no attached unit test due to the poor support of touch events on
 * Selenium.
 *
 * @since
 * @author Vaadin Ltd
 */
public class TabletContextMenu extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        setSizeFull();

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        addComponent(layout);

        Table table1 = createTable("no scrolling, has context menu");
        addActionHandler(table1);
        table1.addItem();
        layout.addComponent(table1);

        Table table2 = createTable("should scroll, has context menu");
        for (int i = 0; i < 100; ++i) {
            table2.addItem();
        }
        addActionHandler(table2);
        layout.addComponent(table2);

        Table table3 = createTable("no scrolling, no context menu");
        table3.addItem();
        layout.addComponent(table3);

        Table table4 = createTable("should scroll, no context menu");
        for (int i = 0; i < 100; ++i) {
            table4.addItem();
        }
        layout.addComponent(table4);
    }

    private Table createTable(String caption) {
        Table table = new Table(caption);
        table.setImmediate(true);

        table.addContainerProperty("column1", String.class, "test");
        table.setSizeFull();
        table.setHeight("500px");
        table.setSelectable(true);

        return table;
    }

    private void addActionHandler(Table table) {
        table.addActionHandler(new Handler() {

            Action tabNext = new ShortcutAction("Shift",
                    ShortcutAction.KeyCode.TAB, null);
            Action tabPrev = new ShortcutAction("Shift+Tab",
                    ShortcutAction.KeyCode.TAB,
                    new int[] { ShortcutAction.ModifierKey.SHIFT });
            Action curDown = new ShortcutAction("Down",
                    ShortcutAction.KeyCode.ARROW_DOWN, null);
            Action curUp = new ShortcutAction("Up",
                    ShortcutAction.KeyCode.ARROW_UP, null);
            Action enter = new ShortcutAction("Enter",
                    ShortcutAction.KeyCode.ENTER, null);
            Action add = new ShortcutAction("Add Below",
                    ShortcutAction.KeyCode.A, null);
            Action delete = new ShortcutAction("Delete",
                    ShortcutAction.KeyCode.DELETE, null);

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                System.out.println(action.getCaption());
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { tabNext, tabPrev, curDown, curUp, enter,
                        add, delete };
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Make sure empty table parts have context menu on touch screen devices";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13694;
    }

}
