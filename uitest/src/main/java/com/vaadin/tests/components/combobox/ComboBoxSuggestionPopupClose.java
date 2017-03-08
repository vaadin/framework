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
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPopupClose extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> select = new ComboBox<>("ComboBox");
        select.setItems("one", "two", "three");
        addComponent(select);
    }

    @Override
    protected String getTestDescription() {
        return "Closing the suggestion popup using Enter key is "
                + "broken in combobox when opening popup using Enter "
                + "key and not changin the selection using arrows";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14379;
    }
}
