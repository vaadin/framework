/**
 * 
 */
package com.itmill.toolkit.tests;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.ContainerHierarchicalWrapper;
import com.itmill.toolkit.data.util.ContainerOrderedWrapper;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ListSelect;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * @author marc
 * 
 */
public class TestContainerChanges extends CustomComponent {
    Container cont = new IndexedContainer();
    Container notordered = new ContainerHierarchicalWrapper(cont);
    Container nothierarchical = new ContainerOrderedWrapper(cont);

    int cnt = 0;
    Table tbl;

    public TestContainerChanges() {

        cont.addContainerProperty("Asd", String.class, "qwe");
        cont.addContainerProperty("Bar", String.class, "foo");

        OrderedLayout main = new OrderedLayout();
        setCompositionRoot(main);

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
        tbl.setContainerDataSource(notordered);

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
        tbl2.setContainerDataSource(notordered);

        OrderedLayout buttons = new OrderedLayout();
        v.addComponent(buttons);

        Button b = new Button("Commit", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                tbl.commit();
            }

        });
        buttons.addComponent(b);

        b = new Button("Add item", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                cont.addItem(new Integer(cnt++));
            }

        });
        buttons.addComponent(b);
        b = new Button("Add NULL item", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                cont.addItem(null);
            }

        });
        buttons.addComponent(b);
        b = new Button("Remove last", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                cont.removeItem(tbl.lastItemId());
            }

        });
        buttons.addComponent(b);

        b = new Button("Add property", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                cont.addContainerProperty("prop" + cnt, String.class, "#"
                        + cnt++);
            }

        });
        buttons.addComponent(b);

        b = new Button("clear", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                cont.removeAllItems();
            }

        });
        buttons.addComponent(b);
        b = new Button("idx", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                tbl.setContainerDataSource(cont);
            }

        });
        buttons.addComponent(b);
        b = new Button("nothierarchical", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                tbl.setContainerDataSource(nothierarchical);
            }

        });
        buttons.addComponent(b);
        b = new Button("notordered", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                tbl.setContainerDataSource(notordered);
            }

        });
        buttons.addComponent(b);

        Panel p = new Panel("Tree");
        p.setStyleName(Panel.STYLE_LIGHT);
        h.addComponent(p);
        Tree tree = new Tree("ITEM_CAPTION_MODE_PROPERTY");
        tree.setContainerDataSource(nothierarchical);
        tree.setItemCaptionPropertyId("Asd");
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        p.addComponent(tree);
        tree = new Tree("ITEM_CAPTION_MODE_ITEM");
        // nonhierarchical container will get wrapped
        tree.setContainerDataSource(nothierarchical);
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
