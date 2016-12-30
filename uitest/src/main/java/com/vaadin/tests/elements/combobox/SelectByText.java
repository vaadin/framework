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
package com.vaadin.tests.elements.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * UI used to validate ComboBox.selectByText(String s) works properly if input
 * String s contains parentheses
 */
@SuppressWarnings("serial")
public class SelectByText extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        addComponent(layout);

        ComboBox<String> combobox = new ComboBox<>();
        List<String> options = new ArrayList<String>();

        options.add("Value 1");
        options.add("(");
        options.add("(Value");
        options.add("Value 222");
        options.add("Value 22");
        options.add("Value 2");
        options.add("Value(");
        options.add("Value(i)");
        options.add("((Test ) selectByTest() method(with' parentheses)((");
        options.add("Value 3");

        combobox.setItems(options);

        layout.addComponent(combobox);
        combobox.addValueChangeListener(e -> {
            layout.addComponent(
                    new Label("Value is now '" + e.getValue() + "'"));
        });

    }

    @Override
    protected String getTestDescription() {
        return "ComboBox's selectByText(String text) method should work if text contains parentheses";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14048;
    }

}
