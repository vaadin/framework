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
package com.vaadin.tests.components.textfield;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TextFieldInLayoutInTable extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWindow = new LegacyWindow(
                this.getClass().getName());
        setMainWindow(mainWindow);

        final Table table = new Table();
        table.addContainerProperty("column1", Component.class, null);
        VerticalLayout vl = new VerticalLayout();
        final TextField textField = new TextField();
        vl.addComponent(textField);

        table.addItem(new Object[] { vl }, 1);

        table.setSizeFull();
        mainWindow.addComponent(table);
    }

}
