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

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;

/**
 * 
 * @author Vaadin Ltd
 */
public class SelectAllConstantViewport extends AbstractTestUIWithLog {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.addContainerProperty("", Integer.class, null);
        table.setSizeFull();
        table.setMultiSelect(true);
        table.setNullSelectionAllowed(true);
        table.setSelectable(true);

        CheckBox selectAllCheckbox = new CheckBox("Select All");
        selectAllCheckbox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                Object checked = event.getProperty().getValue();
                if (checked instanceof Boolean) {
                    if (((Boolean) checked).booleanValue()) {
                        table.setValue(table.getItemIds());
                    } else {
                        table.setValue(null);
                    }
                }
            }
        });

        for (int i = 0; i < 200; i++) {
            table.addItem(new Object[] { new Integer(i) }, new Integer(i));
        }

        table.setCurrentPageFirstItemIndex(185);

        final CssLayout layout = new CssLayout();
        layout.addComponent(selectAllCheckbox);
        layout.addComponent(table);
        layout.setSizeFull();
        addComponent(layout);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {

        return "The scroll position of a table with many items should remain constant if all items are selected.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13341;
    }

}
