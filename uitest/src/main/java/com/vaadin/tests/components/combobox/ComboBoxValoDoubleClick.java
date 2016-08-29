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
package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

@Theme("valo")
public class ComboBoxValoDoubleClick extends AbstractTestUI {

    // Quite impossible to autotest reliably as there must be a click to open
    // the popup and another click during the opening animation to reproduce the
    // bug. Manually a double click is just about the right timing.
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox cb = new ComboBox("Double-click Me");
        for (int i = 0; i < 100; i++) {
            cb.addItem("Item-" + i);
        }
        addComponent(cb);
    }

    @Override
    public String getTestDescription() {
        return "ComboBox should remain usable even after double-clicking (affects only Valo theme with $v-overlay-animate-in).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17903;
    }

}
