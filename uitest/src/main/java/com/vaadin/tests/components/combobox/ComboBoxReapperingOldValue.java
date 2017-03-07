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

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

@SuppressWarnings("serial")
public class ComboBoxReapperingOldValue extends LegacyApplication
        implements ValueChangeListener {

    ComboBox cbox1 = new ComboBox();
    ComboBox cbox2 = new ComboBox();

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("ComboBoxCacheTest");
        setMainWindow(mainWindow);

        VerticalLayout layout = new VerticalLayout();

        Label lbl = new Label(
                "try selecting value 1 from the first combo box, so that the second combo box will be populated. select a value in second combo box."
                        + "then select a new value from combo box one, after that click on the second combo box. The old selected value appears.");
        layout.addComponent(lbl);

        cbox1.setCaption("Com Box 1");
        cbox1.setFilteringMode(FilteringMode.CONTAINS);
        cbox1.setContainerDataSource(getContainer());
        cbox1.setImmediate(true);
        cbox1.setNullSelectionAllowed(false);
        cbox1.addListener(this);

        layout.addComponent(cbox1);
        layout.addComponent(cbox2);

        cbox2.setCaption("Com Box 2");
        cbox2.setEnabled(false);
        cbox2.setNullSelectionAllowed(false);

        mainWindow.setContent(layout);

    }

    private Container getContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("na", String.class, null);

        for (int i = 0; i < 10; i++) {
            container.addItem(i);
        }
        return container;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        cbox2.removeAllItems();
        if ("1".equals(event.getProperty().getValue().toString())) {
            cbox2.setEnabled(true);
            cbox2.setContainerDataSource(getContainer());
        }
    }

}
