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
/**
 *
 */
package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.util.ContainerHierarchicalWrapper;
import com.vaadin.v7.data.util.ContainerOrderedWrapper;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.themes.Reindeer;

/**
 * @author marc
 *
 */
public class TestContainerChanges extends CustomComponent {
    Container cont = new IndexedContainer();
    Container hierarchical = new ContainerHierarchicalWrapper(cont);
    Container ordered = new ContainerOrderedWrapper(cont);

    int cnt = 0;
    Table tbl;

    public TestContainerChanges() {

        cont.addContainerProperty("Asd", String.class, "qwe");
        cont.addContainerProperty("Bar", String.class, "foo");

        VerticalLayout main = new VerticalLayout();
        setCompositionRoot(main);

        main.addComponent(new Label(
                "The same IndexedContainer is wrapped in a ordered/hierarchical wrapper and is set as data source for all components . The buttons only affect the 'original' IndexedContainer."));

        HorizontalLayout h = new HorizontalLayout();

        main.addComponent(h);

        VerticalLayout v = new VerticalLayout();
        h.addComponent(v);
        tbl = new Table();
        tbl.setHeight("200px");
        tbl.setWidth("300px");
        v.addComponent(tbl);
        tbl.setSelectable(true);
        tbl.setMultiSelect(false);
        tbl.setImmediate(true);
        tbl.setEditable(true);
        tbl.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        // Original container
        tbl.setContainerDataSource(hierarchical);

        Table tbl2 = new Table();
        tbl2.setHeight("200px");
        tbl2.setWidth("300px");
        v.addComponent(tbl2);
        tbl2.setSelectable(true);
        tbl2.setMultiSelect(false);
        tbl2.setImmediate(true);
        tbl2.addListener(new Table.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                System.err
                        .println("Value now " + event.getProperty().getValue());

            }
        });
        tbl2.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        // non-ordered container will get wrapped
        tbl2.setContainerDataSource(hierarchical);

        VerticalLayout buttons = new VerticalLayout();
        v.addComponent(buttons);

        Button b = new Button("table.commit()", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tbl.commit();
            }

        });
        buttons.addComponent(b);

        b = new Button("indexedcontainer.addItem()",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        cont.addItem(new Integer(cnt++));
                    }

                });
        buttons.addComponent(b);
        b = new Button("indexedcontainer.addItem(null)",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        cont.addItem(null);
                    }

                });
        buttons.addComponent(b);
        b = new Button("indexedcontainer.removeItem(table.lastItemId()",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        cont.removeItem(tbl.lastItemId());
                    }

                });
        buttons.addComponent(b);

        b = new Button("indexedcontainer.addContainerProperty()",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        cont.addContainerProperty("prop" + cnt, String.class,
                                "#" + cnt++);
                    }

                });
        buttons.addComponent(b);

        b = new Button("indexedcontainer.clear()", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                cont.removeAllItems();
            }

        });
        buttons.addComponent(b);
        b = new Button("table.setContainerDataSource(indexedcontainer)",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        tbl.setContainerDataSource(cont);
                    }

                });
        buttons.addComponent(b);
        b = new Button("table.setContainerDataSource(orderedwrapper)",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        tbl.setContainerDataSource(ordered);
                    }

                });
        buttons.addComponent(b);
        b = new Button("table.setContainerDataSource(hierarchicalwrapper)",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        tbl.setContainerDataSource(hierarchical);
                    }

                });
        buttons.addComponent(b);

        VerticalLayout pl = createPanelLayout();
        Panel p = new Panel("Tree", pl);
        p.setStyleName(Reindeer.PANEL_LIGHT);
        h.addComponent(p);
        Tree tree = new Tree("ITEM_CAPTION_MODE_PROPERTY");
        tree.setContainerDataSource(ordered);
        tree.setItemCaptionPropertyId("Asd");
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        pl.addComponent(tree);
        tree = new Tree("ITEM_CAPTION_MODE_ITEM");
        // nonhierarchical container will get wrapped
        tree.setContainerDataSource(ordered);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_ITEM);
        pl.addComponent(tree);

        pl = createPanelLayout();
        p = new Panel("ComboBox", pl);
        p.setStyleName(Reindeer.PANEL_LIGHT);
        h.addComponent(p);
        ComboBox c = new ComboBox("ITEM_CAPTION_MODE_PROPERTY");
        c.setImmediate(true);
        c.setContainerDataSource(cont);
        c.setItemCaptionPropertyId("Asd");
        c.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        pl.addComponent(c);
        c = new ComboBox("ITEM_CAPTION_MODE_ITEM");
        c.setImmediate(true);
        c.setContainerDataSource(cont);
        c.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_ITEM);
        pl.addComponent(c);

        pl = createPanelLayout();
        p = new Panel("ListBox", pl);
        p.setStyleName(Reindeer.PANEL_LIGHT);
        h.addComponent(p);
        ListSelect l = new ListSelect("ITEM_CAPTION_MODE_PROPERTY");
        l.setContainerDataSource(cont);
        l.setItemCaptionPropertyId("Asd");
        l.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        pl.addComponent(l);
        l = new ListSelect("ITEM_CAPTION_MODE_ITEM");
        l.setContainerDataSource(cont);
        l.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_ITEM);
        pl.addComponent(l);
    }

    private VerticalLayout createPanelLayout() {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        return pl;
    }
}
