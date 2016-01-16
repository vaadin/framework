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

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.TextField;

@Theme("valo")
public class GridHeaderFooterComponents extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.setWidth("800px");
        grid.setContainerDataSource(createContainer());
        grid.setFooterVisible(true);
        final HeaderRow defaultRow = grid.getDefaultHeaderRow();
        final HeaderRow toggleVisibilityRow = grid.appendHeaderRow();
        final Grid.HeaderRow filterRow = grid.appendHeaderRow();

        final FooterRow footerRow = grid.addFooterRowAt(0);
        final FooterRow toggleVisibilityFooterRow = grid.addFooterRowAt(0);
        final FooterRow filterFooterRow = grid.addFooterRowAt(0);

        // Set up a filter for all columns
        for (final Object pid : grid.getContainerDataSource()
                .getContainerPropertyIds()) {
            final Grid.HeaderCell headerCell = filterRow.getCell(pid);
            final Grid.FooterCell footerCell = filterFooterRow.getCell(pid);

            headerCell.setComponent(createTextField(pid));
            footerCell.setComponent(createTextField(pid));

            toggleVisibilityRow.getCell(pid).setComponent(
                    new Button("Toggle field", new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            Component c = headerCell.getComponent();
                            c.setVisible(!c.isVisible());
                        }
                    }));
            toggleVisibilityFooterRow.getCell(pid).setComponent(
                    new Button("Toggle field", new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            Component c = footerCell.getComponent();
                            c.setVisible(!c.isVisible());
                        }
                    }));
        }
        addComponent(grid);

        addRemoveHeaderRow(grid, defaultRow);
        addRemoveHeaderRow(grid, filterRow);
        addRemoveHeaderRow(grid, toggleVisibilityRow);

        addRemoveFooterRow(grid, footerRow);
        addRemoveFooterRow(grid, filterFooterRow);
        addRemoveFooterRow(grid, toggleVisibilityFooterRow);

        // Hide first field initially
        filterRow.getCell("string").getComponent().setVisible(false);
        filterFooterRow.getCell("string").getComponent().setVisible(false);
    }

    private void addRemoveHeaderRow(final Grid grid, final HeaderRow row) {
        row.getCell("action").setComponent(
                new Button("Remove row", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.removeHeaderRow(row);
                    }
                }));

    }

    private void addRemoveFooterRow(final Grid grid, final FooterRow row) {
        row.getCell("action").setComponent(
                new Button("Remove row", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.removeFooterRow(row);
                    }
                }));

    }

    private IndexedContainer createContainer() {
        IndexedContainer ic = new IndexedContainer();
        ic.addContainerProperty("action", String.class, "");
        ic.addContainerProperty("string", String.class, "Hello world");
        ic.addContainerProperty("int", int.class, 13);
        ic.addContainerProperty("double", double.class, 5.2f);

        for (int i = 0; i < 5; i++) {
            ic.addItem();
        }
        return ic;
    }

    private TextField createTextField(final Object pid) {
        TextField filterField = new TextField();
        filterField.setColumns(8);
        filterField.setValue("Filter: " + pid);
        filterField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log("value change for field in " + pid + " to "
                        + event.getProperty().getValue());
            }
        });
        return filterField;
    }

}
