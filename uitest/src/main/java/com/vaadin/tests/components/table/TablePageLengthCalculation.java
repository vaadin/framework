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
package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

public class TablePageLengthCalculation extends TestBase {

    @Override
    public void setup() {

        Window window = new Window();
        window.setCaption("usermanagement");
        window.center();
        window.setWidth(40, Window.UNITS_PERCENTAGE);
        window.setHeight(40, Window.UNITS_PERCENTAGE);
        window.setModal(true);
        getMainWindow().addWindow(window);

        TabSheet tab = new TabSheet();
        tab.setSizeFull();

        tab.addTab(new TableView(), "users", null);
        tab.addTab(new TableView(), "groups", null);

        window.setContent(tab);
    }

    public class TableView extends Table {
        private static final long serialVersionUID = 1L;

        public TableView() {
            setSizeFull();

            addContainerProperty("name", String.class, "name");
            addContainerProperty("right", Boolean.class, "right");
        }
    }

    @Override
    protected String getDescription() {
        return "Resize the window and change the selected tab. In Opera 10.50 the updated pagelength will be calculated as a float and not an integer, causing an \"Internal Error\"";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4374;
    }
}
