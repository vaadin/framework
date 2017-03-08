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
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPopupWidth extends AbstractReindeerTestUI {

    private static List<String> items = Arrays.asList("abc", "cde", "efg",
            "ghi", "ijk", "more items 1", "more items 2", "more items 3",
            "Ridicilously long item caption so we can see how the ComboBox displays ridicilously long captions in the suggestion pop-up",
            "more items 4", "more items 5", "more items 6", "more items 7");

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> cb = new ComboBox<>(
                "200px wide ComboBox with 100% wide suggestion popup", items);
        cb.setPopupWidth("100%");
        cb.setWidth("200px");
        cb.addStyleName("width-as-percentage");
        addComponent(cb);

    }

    @Override
    protected String getTestDescription() {
        return "Suggestion pop-up's width should be the same width as the ComboBox itself";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19685;
    }

}
