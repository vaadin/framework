/**
 * 
 */
package com.vaadin.tests;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.data.util.ContainerOrderedWrapper;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Button.ClickEvent;

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

        OrderedLayout main = new OrderedLayout();
        setCompositionRoot(main);

        main
                .addComponent(new Label(
                        "The same IndexedContainer is wrapped in a ordered/hierarchical wrapper and is set as data source for all components . The buttons only affect the 'original' IndexedContainer."));

        OrderedLayout h = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        main.addComponent(h);

        OrderedLayout v = new OrderedLayout();
        h.addComponent(v);
        tbl = new Table();
        tbl.setHeight(200);
        tbl.setWidth(300);
        v.addComponent(tbl);
        tbl.setSelectable(true);
        tbl.setMultiSelect(false);
        tbl.setImmediate(true);
        tbl.setEditable(true);
        tbl.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        // Original container
        tbl.setContainerDataSource(hierarchical);

        Table tbl2 = new Table();
        tbl2.setHeight(200);
        tbl2.setWidth(300);
        v.addComponent(tbl2);
        tbl2.setSelectable(true);
        tbl2.setMultiSelect(false);
        tbl2.setImmediate(true);
        tbl2.addListener(new Table.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                System.err.println("Value now "
                        + event.getProperty().getValue());

            }
        });
        tbl2.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        // non-ordered container will get wrapped
        tbl2.setContainerDataSource(hierarchical);

        OrderedLayout buttons = new OrderedLayout();
        v.addComponent(buttons);

        Button b = new Button("table.commit()", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                tbl.commit();
            }

        });
        buttons.addComponent(b);

        b = new Button("indexedcontainer.addItem()",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        cont.addItem(new Integer(cnt++));
                    }

                });
        buttons.addComponent(b);
        b = new Button("indexedcontainer.addItem(null)",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        cont.addItem(null);
                    }

                });
        buttons.addComponent(b);
        b = new Button("indexedcontainer.removeItem(table.lastItemId()",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        cont.removeItem(tbl.lastItemId());
                    }

                });
        buttons.addComponent(b);

        b = new Button("indexedcontainer.addContainerProperty()",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        cont.addContainerProperty("prop" + cnt, String.class,
                                "#" + cnt++);
                    }

                });
        buttons.addComponent(b);

        b = new Button("indexedcontainer.clear()", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                cont.removeAllItems();
            }

        });
        buttons.addComponent(b);
        b = new Button("table.setContainerDataSource(indexedcontainer)",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        tbl.setContainerDataSource(cont);
                    }

                });
        buttons.addComponent(b);
        b = new Button("table.setContainerDataSource(orderedwrapper)",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        tbl.setContainerDataSource(ordered);
                    }

                });
        buttons.addComponent(b);
        b = new Button("table.setContainerDataSource(hierarchicalwrapper)",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        tbl.setContainerDataSource(hierarchical);
                    }

                });
        buttons.addComponent(b);

        Panel p = new Panel("Tree");
        p.setStyleName(Panel.STYLE_LIGHT);
        h.addComponent(p);
        Tree tree = new Tree("ITEM_CAPTION_MODE_PROPERTY");
        tree.setContainerDataSource(ordered);
        tree.setItemCaptionPropertyId("Asd");
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        p.addComponent(tree);
        tree = new Tree("ITEM_CAPTION_MODE_ITEM");
        // nonhierarchical container will get wrapped
        tree.setContainerDataSource(ordered);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_ITEM);
        p.addComponent(tree);

        p = new Panel("ComboBox");
        p.setStyleName(Panel.STYLE_LIGHT);
        h.addComponent(p);
        ComboBox c = new ComboBox("ITEM_CAPTION_MODE_PROPERTY");
        c.setImmediate(true);
        c.setContainerDataSource(cont);
        c.setItemCaptionPropertyId("Asd");
        c.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        p.addComponent(c);
        c = new ComboBox("ITEM_CAPTION_MODE_ITEM");
        c.setImmediate(true);
        c.setContainerDataSource(cont);
        c.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_ITEM);
        p.addComponent(c);

        p = new Panel("ListBox");
        p.setStyleName(Panel.STYLE_LIGHT);
        h.addComponent(p);
        ListSelect l = new ListSelect("ITEM_CAPTION_MODE_PROPERTY");
        l.setContainerDataSource(cont);
        l.setItemCaptionPropertyId("Asd");
        l.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        p.addComponent(l);
        l = new ListSelect("ITEM_CAPTION_MODE_ITEM");
        l.setContainerDataSource(cont);
        l.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_ITEM);
        p.addComponent(l);
    }
}
