/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.themes.valo;

import com.vaadin.data.Container;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class Tables extends VerticalLayout implements View {

    static final Container normalContainer = ValoThemeTest.generateContainer(
            200, false);
    static final Container hierarchicalContainer = ValoThemeTest
            .generateContainer(200, true);

    public Tables() {
        setMargin(true);
        setSpacing(true);

        Label h1 = new Label("Tables");
        h1.addStyleName("h1");
        addComponent(h1);

        Table table = getTable("Normal");
        addComponent(table);

        table = getTable("Footer");
        table.setFooterVisible(true);
        table.setColumnFooter(ValoThemeTest.CAPTION_PROPERTY, "caption");
        table.setColumnFooter(ValoThemeTest.DESCRIPTION_PROPERTY, "description");
        table.setColumnFooter(ValoThemeTest.ICON_PROPERTY, "icon");
        table.setColumnFooter(ValoThemeTest.INDEX_PROPERTY, "index");
        addComponent(table);

        table = getTable("Sized ");
        table.setWidth("300px");
        addComponent(table);

        table = getTable("Sized w/ expand ratios");
        table.setWidth("100%");
        table.setColumnExpandRatio(ValoThemeTest.CAPTION_PROPERTY, 1.0f);
        table.setColumnExpandRatio(ValoThemeTest.DESCRIPTION_PROPERTY, 1.0f);
        // table.setColumnExpandRatio(ValoThemeTest.ICON_PROPERTY, 1.0f);
        // table.setColumnExpandRatio(ValoThemeTest.INDEX_PROPERTY, 1.0f);
        addComponent(table);

        table = getTable("No stripes");
        table.addStyleName("no-stripes");
        addComponent(table);

        table = getTable("No vertical lines");
        table.addStyleName("no-vertical-lines");
        addComponent(table);

        table = getTable("No horizontal lines");
        table.addStyleName("no-horizontal-lines");
        addComponent(table);

        table = getTable("Borderless");
        table.addStyleName("borderless");
        addComponent(table);

        table = getTable("No headers");
        table.addStyleName("no-header");
        addComponent(table);

        table = getTable("Compact");
        table.addStyleName("compact");
        addComponent(table);

        table = getTable("Small");
        table.addStyleName("small");
        addComponent(table);

        h1 = new Label("Tree Tables");
        h1.addStyleName("h1");
        addComponent(h1);

        addComponent(new Label(
                "TreeTables have all the same features as Tables, but they support hierarchical containers as well."));

        table = getTreeTable(null);
        addComponent(table);
    }

    TreeTable getTreeTable(String caption) {
        TreeTable table = new TreeTable(caption);
        configure(table, true);
        return table;
    }

    Table getTable(String caption) {
        Table table = new Table(caption);
        configure(table, false);
        return table;
    }

    void configure(Table table, boolean hierarchical) {
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setSortEnabled(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setPageLength(6);
        table.addActionHandler(ValoThemeTest.getActionHandler());
        table.setDragMode(TableDragMode.MULTIROW);
        table.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Notification.show(event.getTransferable().toString());
            }
        });
        Container tableData = hierarchical ? hierarchicalContainer
                : normalContainer;
        table.setContainerDataSource(tableData);
        table.select(tableData.getItemIds().iterator().next());
        // table.setSortContainerPropertyId(ValoThemeTest.CAPTION_PROPERTY);
        // table.setItemIconPropertyId(ValoThemeTest.ICON_PROPERTY);
        table.setColumnAlignment(ValoThemeTest.DESCRIPTION_PROPERTY,
                Align.RIGHT);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
