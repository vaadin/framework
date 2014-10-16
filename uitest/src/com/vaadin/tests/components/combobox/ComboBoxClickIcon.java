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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

/**
 * Test UI to check click on icon in the combobox.
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxClickIcon extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final List<String> items = new ArrayList<String>();
        items.add("A");
        items.add("B");
        items.add("C");
        final ComboBox combo = new ComboBox();
        combo.setImmediate(true);
        combo.setItemIcon(items.get(0), FontAwesome.ALIGN_CENTER);
        combo.setItemIcon(items.get(1), FontAwesome.ALIGN_CENTER);
        combo.setItemIcon(items.get(2), FontAwesome.ALIGN_CENTER);
        combo.addItems(items);
        combo.setTextInputAllowed(false);
        addComponent(combo);
    }

    @Override
    protected String getTestDescription() {
        return "Combobox icon should handle click events";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14624;
    }

}
