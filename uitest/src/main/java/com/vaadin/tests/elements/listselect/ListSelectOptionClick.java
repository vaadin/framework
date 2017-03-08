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
package com.vaadin.tests.elements.listselect;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;

public class ListSelectOptionClick extends AbstractTestUI {

    private Label counterLbl = new Label();
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect<String> multiSelect = new ListSelect<String>();
        counterLbl.setValue("0");
        List<String> options = new ArrayList<String>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        multiSelect.setItems(options);
        multiSelect.select("item1");
        multiSelect.addSelectionListener(event -> {
            counter++;
            counterLbl.setValue("" + counter + ": " + event.getValue());
        });
        addComponent(multiSelect);
        counterLbl.setId("multiCounterLbl");
        addComponent(counterLbl);
    }

    @Override
    protected String getTestDescription() {
        return "Test that user can pick option from ListSelectElement by calling the click() method";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
