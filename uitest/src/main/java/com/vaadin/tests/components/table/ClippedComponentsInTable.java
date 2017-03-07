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
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class ClippedComponentsInTable extends TestBase {

    @Override
    protected String getDescription() {
        return "The table below should display 3 rows. Each with a textfield containing the row number.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        Table t = new Table();
        addComponent(t);

        t.addContainerProperty("Name", TextField.class, null);
        t.addContainerProperty("Button", Button.class, null);

        for (int i = 0; i < 3; i++) {
            Item item = t.addItem(i);
            TextField tf = new TextField("", String.valueOf(i + 1));
            tf.setColumns(10);
            item.getItemProperty("Name").setValue(tf);

            Button b = new Button("OK");
            item.getItemProperty("Button").setValue(b);
        }

    }
}
