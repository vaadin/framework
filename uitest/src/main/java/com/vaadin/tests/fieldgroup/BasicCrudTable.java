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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Table;

public class BasicCrudTable extends AbstractBasicCrud {

    private Table table;

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);

        table = new Table();
        table.setSelectable(true);

        table.setContainerDataSource(container);

        table.setVisibleColumns((Object[]) columns);
        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                form.edit((BeanItem<ComplexPerson>) table.getItem(table
                        .getValue()));
            }
        });

        table.setSizeFull();

        addComponent(table);
        addComponent(form);
        getLayout().setExpandRatio(table, 1);
    }

    @Override
    protected void deselectAll() {
        table.setValue(null);

    }

}
