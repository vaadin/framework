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

import java.util.HashSet;
import java.util.Set;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class NotselectablePaintSelections extends TestBase {

    @Override
    protected String getDescription() {
        return "Table should paint selections even if it's not selectable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3500;
    }

    @Override
    protected void setup() {
        // Multiselect
        Table t = new Table("Multiselect");
        addComponent(t);
        t.setSelectable(false);
        t.setMultiSelect(true);
        t.setPageLength(5);
        t.addContainerProperty("Name", String.class, null);
        Set<Object> selected = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            Item item = t.addItem(i);
            item.getItemProperty("Name").setValue("Name " + i);
            if (i % 2 == 0) {
                selected.add(i);
            }
        }
        t.setValue(selected);

        // Singleselect
        t = new Table("Singleselect");
        addComponent(t);
        t.setSelectable(false);
        t.setMultiSelect(false);
        t.setPageLength(5);
        t.addContainerProperty("Name", String.class, null);
        for (int i = 0; i < 30; i++) {
            Item item = t.addItem(i);
            item.getItemProperty("Name").setValue("Name " + i);
        }
        t.setValue(3);

    }
}
