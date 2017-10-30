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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;

public class ComboBoxAutoresetValue extends AbstractTestUIWithLog {

    public static final String RESET = "Reset";
    public static final String CHANGE = "Change to something else";
    public static final String SOMETHING = "Something else";

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(RESET, CHANGE, SOMETHING);
        comboBox.addValueChangeListener(e -> {
            String value = e.getValue();
            log("Value changed to " + value);

            if (e.isUserOriginated()) {
                if (RESET.equals(value)) {
                    e.getSource().setValue(null);
                } else if (CHANGE.equals(value)) {
                    e.getSource().setValue(SOMETHING);
                }
            }
        });
        addComponent(comboBox);
    }

    @Override
    public String getDescription() {
        return "Changing the ComboBox value in its own value change listener should work";
    }

}
