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
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Table;

public class TableSingleSelect extends TestBase {
    Log log = new Log(3);

    @Override
    protected void setup() {
        log.setId("eventlog");

        Table t = new Table();

        t.setSelectable(true);
        t.setNullSelectionAllowed(true);
        t.setImmediate(true);
        t.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Selected value: " + event.getProperty().getValue());
            }
        });

        t.addContainerProperty("string", String.class, null);
        t.addContainerProperty("button", Component.class, null);

        for (int i = 0; i < 10; i++) {
            t.addItem(i);
            t.getContainerProperty(i, "string").setValue(String.valueOf(i));
            t.getContainerProperty(i, "button")
                    .setValue(new Button("Click me"));
        }

        addComponent(log);
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "Table in single-select mode with null selection allowed. Tests that single select does not select multiple items, selects and deselects properly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5431;
    }

}
