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
package com.vaadin.tests.fieldgroup;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;

public class BasicCrudGrid extends AbstractBasicCrud {

    private Grid grid;

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        grid = new Grid();

        grid.setContainerDataSource(container);

        grid.setColumnOrder((Object[]) columns);
        grid.removeColumn("salary");
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                Item item = grid.getContainerDataSource().getItem(
                        grid.getSelectedRow());
                form.edit((BeanItem<ComplexPerson>) item);
            }
        });

        grid.setSizeFull();

        addComponent(grid);
        addComponent(form);
        getLayout().setExpandRatio(grid, 1);
    }

    @Override
    protected void deselectAll() {
        grid.select(null);
    }

}
