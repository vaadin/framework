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
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

@SuppressWarnings("serial")
public class MultiSelectWithNotIdentityEqualIds extends TestBase {

    @Override
    protected void setup() {
        final Table t = new Table();
        t.setContainerDataSource(getDS());
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        t.setMultiSelect(true);
        t.setSelectable(true);
        t.setImmediate(true);
        t.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Notification.show("Selected: " + event.getProperty());

            }
        });
        getLayout().addComponent(t);
    }

    private Container getDS() {

        IndexedContainer idx = new IndexedContainer() {

            @Override
            public Object nextItemId(Object itemId) {
                Integer id = (Integer) super.nextItemId(itemId);
                return id == null ? null : new Integer(id);
            }

        };
        for (int i = 0; i < 10; i++) {
            idx.addItem();
        }

        idx.addContainerProperty("Property", String.class, "foo");

        return idx;
    }

    @Override
    protected String getDescription() {
        return "Multiselection should work with container that uses ids that are equal, but not necessary identical. With bug an infinit loop is caused.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5342;
    }

}
