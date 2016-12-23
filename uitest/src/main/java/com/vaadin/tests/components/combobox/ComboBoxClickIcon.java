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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

/**
 * Test UI to check click on icon in the combobox.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxClickIcon extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> combo = new ComboBox<>();
        combo.setItems("A", "B", "C");
        combo.setItemIconGenerator(item -> VaadinIcons.ALIGN_CENTER);
        combo.setTextInputAllowed(false);
        addComponent(combo);
    }

    @Override
    protected String getTestDescription() {
        return "Combobox icon should handle click events";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14624;
    }

}
