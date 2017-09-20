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
package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

public class JavaScriptRenderers extends AbstractReindeerTestUI {

    public static class MyBean {
        private int integer;
        private String string;

        public MyBean(int integer, String string) {
            super();
            this.integer = integer;
            this.string = string;
        }

        public int getInteger() {
            return integer;
        }

        public void setInteger(int integer) {
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("id", Integer.class, 0);
        container.addContainerProperty("bean", MyBean.class, null);
        container.addContainerProperty("string", String.class, "");

        for (int i = 0; i < 1000; i++) {
            Item item = container.addItem(i);
            item.getItemProperty("id").setValue(i);
            item.getItemProperty("bean")
                    .setValue(new MyBean(i + 1, Integer.toString(i - 1)));
            item.getItemProperty("string").setValue("string" + i);
        }

        Grid grid = new Grid(container);

        grid.getColumn("bean").setRenderer(new MyBeanJSRenderer());
        grid.getColumn("bean").setWidth(250);

        grid.getColumn("string").setRenderer(new JavaScriptStringRenderer());

        addComponent(grid);
    }

}
