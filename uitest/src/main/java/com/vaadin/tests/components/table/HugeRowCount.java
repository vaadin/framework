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
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class HugeRowCount extends TestBase {

    private MockupContainer container;

    @Override
    protected void setup() {

        container = new MockupContainer();
        container.setSize(100000);

        final TextField tf = new TextField("Rows");
        tf.setValue(String.valueOf(100000));
        tf.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                container.setSize(Integer.parseInt(tf.getValue().toString()));
            }
        });
        addComponent(tf);
        addComponent(new Button("Update rowcount"));

        Table t = new Table();
        t.setWidth("400px");
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
        t.setContainerDataSource(container);

        addComponent(t);

    }

    class MockupContainer extends IndexedContainer {

        private Item item;
        private Object addItem;

        public MockupContainer() {
            addContainerProperty("foo", String.class, "bar");
            addItem = addItem();
            item = getItem(addItem);
        }

        private int size;

        public void setSize(int size) {
            this.size = size;
            fireItemSetChange();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Item getItem(Object itemId) {
            return item;
        }

        @Override
        public Object getIdByIndex(int index) {
            return addItem;
        }

    }

    @Override
    protected String getDescription() {
        return "Should work as well as possible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4220;
    }

}
