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
package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Table;

public class TableContextMenu extends TestBase {

    private static final Action ACTION_MYACTION = new Action("Action!!");

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.addActionHandler(new Action.Handler() {
            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                Notification.show("Done that :-)");
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { ACTION_MYACTION };
            }
        });

        // TODO should work with all combinations
        table.setImmediate(true);
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.addContainerProperty("Foo", String.class, "BAR1");
        table.addContainerProperty("Bar", String.class, "FOO2");

        // FIXME works with lots of rows (more than pagelength), don't work with
        // none
        for (int i = 0; i < 3; i++) {
            table.addItem();
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Right clicking on an item without a context menu should bring"
                + "up the Tables context menu. With touch devices context menu must popup with long touch.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8639;
    }

}
