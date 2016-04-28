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

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * A test case for typing in combo box input field fast plus then press TAB.
 * When type fast and then press tab didn't add new item. Uses SlowComboBox,
 * which has a delay in setVariables method
 */
public class ComboBoxTabWhenFilter extends AbstractTestUI {
    public static final String DESCRIPTION = "Adding new item by typing fast plus then press TAB, very quickly, should add new item and change focus.";

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);
        SlowComboBox comboBox = new SlowComboBox();
        comboBox.setNullSelectionAllowed(false);
        comboBox.setImmediate(true);
        Container container = createContainer();
        comboBox.setContainerDataSource(container);
        comboBox.setNewItemsAllowed(true);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        layout.addComponent(comboBox);
        layout.addComponent(new TextField());
    }

    private IndexedContainer createContainer() {
        IndexedContainer container = new IndexedContainer();
        for (int i = 0; i < 100000; ++i) {
            container.addItem("Item " + i);
        }
        return container;
    }

    @Override
    protected String getTestDescription() {
        return DESCRIPTION;
    }

    @Override
    protected Integer getTicketNumber() {
        return 12325;
    }

}
