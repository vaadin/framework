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
package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TableSizeInTabsheet extends AbstractTestUI {

    static final String TABLE = "table";
    static final String TABSHEET = "tabsheet";

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setId(TABSHEET);
        layout.addComponent(tabSheet);
        tabSheet.addTab(new TabComposite(), "Tab");
    }

    public class TabComposite extends CustomComponent {

        public TabComposite() {
            Layout mainLayout = new VerticalLayout();
            addComponent(mainLayout);
            setCompositionRoot(mainLayout);

            Component table = new Table();
            table.setWidth("100%");
            table.setId(TABLE);
            mainLayout.addComponent(table);
        }
    }

    @Override
    protected String getTestDescription() {
        return "The size calculations fails in IE8 when undefined size table is inside a tabsheet";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12687;
    }

}
