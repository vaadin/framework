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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;

public class TableCacheBuildEfficiency extends TestBase {

    @Override
    protected String getDescription() {
        return "On each add, row property values should be queried only once (one log row for first addition).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4299;
    }

    @Override
    protected void setup() {

        final CssLayout log = new CssLayout();
        log.setWidth("100%");

        final Table table = new Table() {
            @Override
            public Property<?> getContainerProperty(Object itemId,
                    Object propertyId) {
                log("Fetched container property \"" + propertyId
                        + "\" for item \"" + itemId + "\"");
                return super.getContainerProperty(itemId, propertyId);
            }

            private void log(String string) {
                log.addComponent(new Label(string));

            }
        };

        table.addContainerProperty("foo", String.class, "bar");

        Button b = new Button("Click to add row", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.addItem();

            }
        });

        getLayout().addComponent(table);
        getLayout().addComponent(b);
        getLayout().addComponent(log);

    }
}
