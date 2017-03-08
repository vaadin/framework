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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

@SuppressWarnings("serial")
public class ModifyContainerProperty extends TestBase {

    private Table table = new Table();
    private IndexedContainer ic = new IndexedContainer();

    @Override
    protected void setup() {
        addComponent(table);

        ic.addContainerProperty("one", String.class, "one");
        ic.addContainerProperty("two", String.class, "two");

        ic.addItem("foo");

        ic.getContainerProperty("foo", "one").setValue("bar");
        ic.getContainerProperty("foo", "two").setValue("baz");

        table.setContainerDataSource(ic);
        addComponent(new Button("Remove container property",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent arg0) {
                        ic.removeContainerProperty("one");
                    }
                }));
        addComponent(new Button("Add container property",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent arg0) {
                        boolean added = ic.addContainerProperty("three",
                                String.class, "three");
                        if (added) {
                            Object[] current = table.getVisibleColumns();
                            Object[] vis = new Object[current.length + 1];
                            for (int i = 0; i < current.length; i++) {
                                vis[i] = current[i];
                            }
                            vis[current.length] = "three";
                            table.setVisibleColumns(vis);
                        }
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Clicking on \"Add container property\" adds a property to the container and sets it visible. The table should then show a \"three\" column in addition to the others. Clicking on \"Remove container property\" should remove column \"two\" from the table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3165;
    }
}
