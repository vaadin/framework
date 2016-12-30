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

/**
 *
 */
@SuppressWarnings("serial")
public class ComboBoxUI extends AbstractTestUI {

    public static final List<String> currencies = new ArrayList<String>();
    static {
        currencies.add("GBP");
        currencies.add("EUR");
        currencies.add("USD");
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox comboBox = new ComboBox("NullAllowedComboBox", currencies);
        addComponent(comboBox);

        comboBox = new ComboBox("NullForbiddenComboBox", currencies);
        comboBox.setEmptySelectionAllowed(false);
        addComponent(comboBox);
    }

    @Override
    protected String getTestDescription() {
        return "When calling ComboBoxElement.selectByText(String) several times, the input text should be cleared every time, instead of being appended";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14404;
    }

}
