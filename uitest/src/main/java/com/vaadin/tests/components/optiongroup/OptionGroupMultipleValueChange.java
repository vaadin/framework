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
package com.vaadin.tests.components.optiongroup;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroupMultipleValueChange extends TestBase {

    @Override
    protected String getDescription() {
        return "Clicking on the description of an option should behave exactly like clicking on the radio button. No extra 'null' valuechange event should be sent";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3066;
    }

    @Override
    protected void setup() {
        final OptionGroup og = new OptionGroup();
        og.addItem(
                "Clicking on the text might cause an extra valuechange event");
        og.addItem("Second option, same thing");
        og.setImmediate(true);
        addComponent(og);

        final Label events = new Label("", ContentMode.PREFORMATTED);
        events.setWidth(null);
        addComponent(events);

        og.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                String s = "ValueChange: " + event.getProperty().getValue();
                events.setValue(events.getValue() + "\n" + s);
            }
        });
    }
}
