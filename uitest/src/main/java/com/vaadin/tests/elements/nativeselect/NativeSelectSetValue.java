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
package com.vaadin.tests.elements.nativeselect;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class NativeSelectSetValue extends AbstractTestUI {

    private int counter = 0;
    Label lblCounter = new Label("0");

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect select = new NativeSelect();
        List<String> options = new ArrayList<>();
        options.add("item 1");
        options.add("item 2");
        options.add("item 3");
        select.setDataProvider(new ListDataProvider<>(options));
        select.setValue("item 1");
        lblCounter.setId("counter");

        select.addSelectionListener(new EventCounter());
        addComponent(select);
        addComponent(lblCounter);
    }

    private class EventCounter implements SingleSelectionListener<String> {
        private int counter = 0;

        @Override
        public void selectionChange(SingleSelectionEvent<String> event) {
            counter++;
            lblCounter.setValue("" + counter);
        }

    }

    @Override
    protected String getTestDescription() {
        return "Native select element setValue method should change value and triggers change event";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13365;
    }

}
