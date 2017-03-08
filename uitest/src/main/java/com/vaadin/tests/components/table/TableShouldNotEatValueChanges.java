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
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.TableDragMode;
import com.vaadin.v7.ui.TextField;

public class TableShouldNotEatValueChanges extends TestBase {

    @Override
    protected void setup() {
        Table t = new Table(
                "Table with multiselection and item click listener");
        t.focus();
        t.setPageLength(3);
        t.addContainerProperty("foo", String.class, "bar");
        t.addItem();
        t.setSelectable(true);
        t.setMultiSelect(true);
        t.setTabIndex(4);
        // t.setSelectable(true);

        final TextField tf = new TextField();
        tf.setTabIndex(1);
        ItemClickListener l = new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                Notification.show("TF Value on the server:" + tf.getValue(),
                        Notification.TYPE_WARNING_MESSAGE);
            }
        };
        t.addListener(l);
        addComponent(tf);
        addComponent(t);
        t = new Table("Table with drag and drop and item click listener");
        t.setDragMode(TableDragMode.ROW);
        t.setPageLength(3);
        t.addContainerProperty("foo", String.class, "bar");
        t.addItem();
        t.setSelectable(true);
        t.setMultiSelect(true);

        t.addListener(l);
        addComponent(t);

    }

    @Override
    protected String getDescription() {
        return "When selecting something from table or clicking on item, table should never eat value change from other components.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5429;
    }

}
