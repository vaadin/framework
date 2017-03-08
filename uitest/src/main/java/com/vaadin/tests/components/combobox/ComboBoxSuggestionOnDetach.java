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

import java.util.Arrays;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ComboBoxSuggestionOnDetach extends TestBase {

    @Override
    protected void setup() {
        final Window popup = new Window();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeUndefined();
        popup.setContent(layout);

        ComboBox<String> comboBox = new ComboBox<>("Combo box",
                Arrays.asList("Option 1", "Option 2", "Option 3"));
        comboBox.addFocusListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FocusEvent event) {
                popup.close();
            }
        });
        layout.addComponent(comboBox);

        popup.setSizeUndefined();
        popup.center();

        getMainWindow().addWindow(popup);
    }

    @Override
    protected String getDescription() {
        return "Click the arrow to open the combo box suggestion list. When the box is focused, the window is closed and the suggestion popup of the combo box should also be closed";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7536);
    }

}
