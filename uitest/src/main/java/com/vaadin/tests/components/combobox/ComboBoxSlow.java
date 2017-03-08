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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;

public class ComboBoxSlow extends AbstractReindeerTestUI {

    private Log log = new Log(5);

    @Override
    protected Integer getTicketNumber() {
        return 7949;
    }

    @Override
    protected String getTestDescription() {
        return "The ComboBox artificially introduces a server delay to more easily spot problems";
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        final SlowComboBox cb = new SlowComboBox();
        cb.setImmediate(true);
        for (int i = 0; i <= 1000; i++) {
            cb.addItem("Item " + i);
        }
        cb.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value changed to " + cb.getValue());
            }
        });
        addComponent(cb);
    }
}
