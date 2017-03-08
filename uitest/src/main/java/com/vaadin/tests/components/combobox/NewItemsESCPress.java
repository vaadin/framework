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
package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.AbstractSelect.NewItemHandler;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

@SuppressWarnings("serial")
public class NewItemsESCPress extends TestBase {

    @Override
    protected void setup() {
        final TextArea addedItems = new TextArea("Last added items:");
        addedItems.setRows(10);
        addComponent(addedItems);

        final ComboBox box = new ComboBox("New items are allowed");
        box.setNewItemsAllowed(true);
        box.setNewItemHandler(new NewItemHandler() {
            @Override
            public void addNewItem(String newItemCaption) {
                String value = addedItems.getValue();
                addedItems.setValue(value + newItemCaption + "\n");
                box.addItem(newItemCaption);
            }
        });
        box.setImmediate(true);
        addComponent(box);
    }

    @Override
    protected String getDescription() {
        return "Firefox flashes the previously entered value when holding the ESC-key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5694;
    }

}
