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
package com.vaadin.tests.elements.twincolselect;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectUI extends AbstractTestUI {

    Label multiCounterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        TwinColSelect<String> twinColSelect = new TwinColSelect<>();
        multiCounterLbl.setValue("0");
        List<String> options = new ArrayList<>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        twinColSelect.setItems(options);
        twinColSelect.select("item1");
        twinColSelect.addSelectionListener(new CounterListener(0));

        addComponent(twinColSelect);
        multiCounterLbl.setId("multiCounterLbl");
        addComponent(multiCounterLbl);

    }

    @Override
    protected String getTestDescription() {
        return "Test that user can pick option from ListSelectElement by call click() method";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private class CounterListener implements MultiSelectionListener<String> {
        int counter = 0;

        public CounterListener(int i) {
            counter = i;
        }

        @Override
        public void selectionChange(MultiSelectionEvent<String> event) {
            counter++;
            multiCounterLbl.setValue("" + counter + ": " + event.getValue());
        }
    }
}
