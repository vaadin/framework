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
package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;

public class ComboBoxSelectingWithNewItemsAllowed extends ComboBoxSelecting {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        comboBox.setNewItemsAllowed(true);

        final Label label = new Label(String.valueOf(comboBox.getItemIds()
                .size()));
        label.setCaption("Item count:");
        label.setId("count");
        comboBox.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                label.setValue(String.valueOf(comboBox.getItemIds().size()));
            }
        });
        addComponent(label);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should select value on TAB also when new items are allowed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9369;
    }
}
