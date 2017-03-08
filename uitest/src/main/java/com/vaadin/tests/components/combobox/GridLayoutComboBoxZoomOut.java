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

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;

@SuppressWarnings("serial")
public class GridLayoutComboBoxZoomOut extends AbstractTestCase {

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Gridlayoutbug Application");
        setMainWindow(mainWindow);

        Label description = new Label(
                "Open this application in Chrome, zoom out (cmd + \"-\") and "
                        + "open the ComboBox for weird behaviour.");
        mainWindow.addComponent(description);

        Layout formLayout = new GridLayout(2, 1);
        // formLayout.setWidth("100%");
        formLayout.setWidth("1000px");

        ComboBox<String> countryField = new ComboBox<>();
        countryField.setItems("Finland", "Sweden", "Canada", "USA");
        countryField.setCaption("Country");
        countryField.setWidth("100%");
        formLayout.addComponent(countryField);

        ComboBox<String> statusField = new ComboBox<>();
        statusField.setItems("Available", "On vacation", "Busy",
                "Left the building");
        statusField.setCaption("Status");
        statusField.setWidth("100%");
        formLayout.addComponent(statusField);

        mainWindow.addComponent(formLayout);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
