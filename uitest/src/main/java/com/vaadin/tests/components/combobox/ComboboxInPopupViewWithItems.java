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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.v7.ui.TextArea;

public class ComboboxInPopupViewWithItems extends TestBase {

    @Override
    protected void setup() {
        addComponent(new TextArea("Some component"));
        addComponent(new PopupView(new PopupContent()));

    }

    @Override
    protected String getDescription() {
        return "Combobox popup should be in the correct place even when it is located inside a PopupView";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9768;
    }

    class PopupContent implements PopupView.Content {

        private final ComboBox<String> cb = new ComboBox<>(null,
                Arrays.asList("Item 1", "Item 2", "Item 3"));

        @Override
        public String getMinimizedValueAsHTML() {
            return "click here";
        }

        @Override
        public Component getPopupComponent() {
            return cb;
        }
    }
}
