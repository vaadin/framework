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

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * Test UI for delete rows operation in multiselect table.
 * 
 * @author Vaadin Ltd
 */
public class TableDeleteSelectedRow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.setSelectable(true);
        table.setImmediate(true);

        BeanItemContainer<TableBean> container = createContainer();

        table.setContainerDataSource(container);

        final Label selectedSize = new Label();
        selectedSize.addStyleName("selected-rows");

        Button changeMode = new Button("Set multiselect", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setMultiSelect(true);
                table.setMultiSelectMode(MultiSelectMode.SIMPLE);

                BeanItemContainer<TableBean> container = createContainer();

                table.setContainerDataSource(container);
            }
        });
        changeMode.addStyleName("multiselect");

        Button delete = new Button("Delete selected", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (table.getValue() instanceof Collection) {
                    Collection<?> rows = (Collection<?>) table.getValue();
                    for (Object row : rows) {
                        table.getContainerDataSource().removeItem(row);
                    }
                    rows = (Collection<?>) table.getValue();
                    selectedSize.setValue(String.valueOf(rows.size()));
                } else {
                    table.getContainerDataSource().removeItem(table.getValue());
                    selectedSize.setValue(table.getValue() == null ? "0" : "1");
                }
            }
        });
        delete.addStyleName("delete");

        addComponents(delete, changeMode, selectedSize, table);
    }

    @Override
    protected String getTestDescription() {
        return "Items deleted via container data source should not be available as selected in the table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13580;
    }

    private BeanItemContainer<TableBean> createContainer() {
        BeanItemContainer<TableBean> container = new BeanItemContainer<TableBean>(
                TableBean.class);
        container.addBean(new TableBean("first"));
        container.addBean(new TableBean("second"));
        container.addBean(new TableBean("third"));
        return container;
    }

    public static class TableBean {

        TableBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private String name;
    }
}
