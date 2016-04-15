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
package com.vaadin.tests.components.grid;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class JavaScriptRenderers extends AbstractTestUI {

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
        container.addContainerProperty("id", Integer.class, Integer.valueOf(0));
        container.addContainerProperty("bean", MyBean.class, null);

        for (int i = 0; i < 1000; i++) {
            Integer itemId = Integer.valueOf(i);
            Item item = container.addItem(itemId);
            item.getItemProperty("id").setValue(itemId);
            item.getItemProperty("bean").setValue(
                    new MyBean(i + 1, Integer.toString(i - 1)));
        }

        Grid grid = new Grid(container);

        grid.getColumn("bean").setRenderer(new MyBeanJSRenderer());
        grid.getColumn("bean").setWidth(250);

        addComponent(grid);
    }

}
