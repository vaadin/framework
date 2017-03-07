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
package com.vaadin.tests.integration;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Table;

public class ServletIntegrationUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final Table table = new Table();
        table.addContainerProperty("icon", Resource.class, null);
        table.setItemIconPropertyId("icon");
        table.addContainerProperty("country", String.class, null);
        table.setRowHeaderMode(Table.RowHeaderMode.ICON_ONLY);
        table.setImmediate(true);
        table.setSelectable(true);
        table.setVisibleColumns(new Object[] { "country" });
        layout.addComponent(table);

        Item item = table.addItem("FI");
        item.getItemProperty("icon").setValue(new ClassResource("fi.gif"));
        item.getItemProperty("country").setValue("Finland");
        item = table.addItem("SE");
        item.getItemProperty("icon").setValue(new FlagSeResource());
        item.getItemProperty("country").setValue("Sweden");

        final Label selectedLabel = new LabelFromDesign();
        table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                selectedLabel.setValue(String.valueOf(table.getValue()));
            }
        });
        layout.addComponent(selectedLabel);
    }

    @DesignRoot
    public static class LabelFromDesign extends Label {
        public LabelFromDesign() {
            Design.read(this);
        }
    }
}
