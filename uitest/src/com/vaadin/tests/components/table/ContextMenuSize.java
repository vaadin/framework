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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

/**
 * Test UI for table context menu position and size.
 * 
 * @author Vaadin Ltd
 */
public class ContextMenuSize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.setPageLength(1);
        table.addActionHandler(new Handler() {

            @Override
            public void handleAction(Action action, Object sender, Object target) {
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new Action("action1"),
                        new Action("action2"), new Action("action3"),
                        new Action("action4") };
            }
        });
        BeanItemContainer<Bean> container = new BeanItemContainer<Bean>(
                Bean.class);
        container.addBean(new Bean());
        table.setContainerDataSource(container);
        addComponent(table);
    }

    @Override
    public String getDescription() {
        return "If context menu original position doesn't allow to show it then "
                + "its bottom should be aligned with the window bottom and height "
                + "should be reset after repositioning.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14863;
    }

    public static class Bean {

        public String getName() {
            return "name";
        }

        public void setName() {
        }
    }
}
