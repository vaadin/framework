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

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class SortLabelsInTable extends AbstractTestUI {

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        Table t = new Table("A table with a text column and a Label column");
        t.addContainerProperty("text", String.class, null);
        t.addContainerProperty("label", Label.class, null);

        for (int i = 0; i < 20; i++) {
            Item item = t.addItem("" + i);
            item.getItemProperty("text").setValue("Text " + i);
            item.getItemProperty("label").setValue(new Label("Label " + i));
        }
        addComponent(t);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that Labels are sorted in the same way than Strings are.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8845;
    }

}
