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

package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableChangingDatasource extends CustomComponent implements
        ClickListener {
    Table t;
    Table[] ta = new Table[4];
    private int mode = 0;

    public TableChangingDatasource() {
        final VerticalLayout main = new VerticalLayout();

        main.addComponent(new Label(
                "Table should look sane after data source changes"));

        t = new Table();

        t.setWidth("500px");
        t.setHeight("300px");

        ta[0] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 0);
        ta[1] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 7);
        ta[2] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 5);
        ta[3] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 1);

        main.addComponent(t);
        main.addComponent(new Button("switch DS", this));

        setCompositionRoot(main);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        int i = mode % 4;
        t.setContainerDataSource(ta[i].getContainerDataSource());
        mode++;
    }
}
