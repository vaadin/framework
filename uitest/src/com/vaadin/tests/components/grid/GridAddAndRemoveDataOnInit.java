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
package com.vaadin.tests.components.grid;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridAddAndRemoveDataOnInit extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid gridAdd = new Grid();
        gridAdd.setHeight("240px");
        gridAdd.setWidth("140px");
        addComponent(gridAdd);
        Indexed dataSource = gridAdd.getContainerDataSource();
        dataSource.addContainerProperty("foo", Integer.class, 0);
        for (int i = 0; i < 10; ++i) {
            Object id = dataSource.addItem();
            dataSource.getItem(id).getItemProperty("foo").setValue(i);
        }
        dataSource = new IndexedContainer();
        dataSource.addContainerProperty("bar", Integer.class, 0);
        for (int i = 0; i < 10; ++i) {
            Object id = dataSource.addItem();
            dataSource.getItem(id).getItemProperty("bar").setValue(i);
        }
        Grid gridRemove = new Grid(dataSource);
        gridRemove.setHeight("150px");
        gridRemove.setWidth("140px");
        addComponent(gridRemove);
        for (int i = 0; i < 5; ++i) {
            dataSource.removeItem(dataSource.getIdByIndex(i));
        }
    }

    @Override
    protected String getTestDescription() {
        return "Foo";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13334;
    }
}
