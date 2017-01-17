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
package com.vaadin.tests.components.abstractlisting;

import java.util.LinkedHashMap;

import com.vaadin.ui.AbstractSingleSelect;

public abstract class AbstractSingleSelectTestUI<T extends AbstractSingleSelect<Object>>
        extends AbstractListingTestUI<T> {

    @Override
    protected void createActions() {
        super.createActions();

        createSelectionMenu();
        createListenerMenu();
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners", c -> c
                .addSelectionListener(e -> log("Selected: " + e.getValue())));
    }

    protected void createSelectionMenu() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("None", null);
        options.put("Item 0", "Item 0");
        options.put("Item 1", "Item 1");
        options.put("Item 2", "Item 2");
        options.put("Item 10", "Item 10");
        options.put("Item 100", "Item 100");

        createSelectAction("Select", "Selection", options, "None",
                (component, selected, data) -> {
                    component.setValue(selected);
                });
    }

}
