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

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPageLength extends AbstractTestUI {

    private static List<String> items = Arrays.asList("abc", "cde", "efg",
            "ghi", "ijk");

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox cb = new ComboBox("Page length 0", items);
        cb.setPageLength(0);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        addComponent(cb);

        cb = new ComboBox("Page length 2", items);
        cb.setPageLength(2);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Filtering should also work when page length is set to zero.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14509;
    }

}
