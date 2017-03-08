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
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;

public class ComboFocusBlurEvents extends TestBase {

    private int counter = 0;

    @Override
    protected void setup() {

        ComboBox<String> cb = new ComboBox<>("Combobox");
        cb.setDataProvider(new ItemDataProvider(100));
        cb.setPlaceholder("Enter text");
        cb.setDescription("Some Combobox");
        addComponent(cb);

        final ObjectProperty<String> log = new ObjectProperty<>("");

        cb.addFocusListener(event -> {
            log.setValue(log.getValue().toString() + "<br>" + counter
                    + ": Focus event!");
            counter++;
        });

        cb.addBlurListener(event -> {
            log.setValue(log.getValue().toString() + "<br>" + counter
                    + ": Blur event!");
            counter++;
        });

        TextField field = new TextField("Some textfield");
        addComponent(field);

        Label output = new Label(log);
        output.setCaption("Events:");

        output.setContentMode(ContentMode.HTML);
        addComponent(output);

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 6536;
    }

}
