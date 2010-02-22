package com.vaadin.tests.components.tree;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class TreeFiltering extends Application {

    @Override
    public void init() {
        final Window mainWindow = new Window("Vaadintest Application");

        final Tree ccTree = new Tree();
        mainWindow.addComponent(ccTree);

        final HierarchicalContainer cont = new HierarchicalContainer();
        cont.addContainerProperty("caption", String.class, "");

        Item item;
        for (int i = 0; i < 5; i++) {
            item = cont.addItem(i);
            item.getItemProperty("caption").setValue("Number " + i);
            cont.setParent(i, i - 1);
        }

        for (int i = 0; i < 5; i++) {
            Object id = cont.addItem();
            item = cont.getItem(id);
            item.getItemProperty("caption").setValue("0-" + i);
            cont.setParent(id, 0);
        }

        ccTree.setContainerDataSource(cont);
        ccTree.setItemCaptionPropertyId("caption");

        for (final Object o : ccTree.getItemIds()) {
            ccTree.expandItem(o);
        }

        final Button b = new Button("Add filter 'foo'", new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "foo", true, false);

            }
        });
        mainWindow.addComponent(b);
        final Button b2 = new Button("Add filter 'Num'", new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "Num", true, false);

            }
        });
        mainWindow.addComponent(b2);
        final Button num = new Button("Add filter '0'", new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "0", true, false);

            }
        });
        mainWindow.addComponent(num);
        final Button r = new Button("Remove filter", new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                cont.removeAllContainerFilters();

            }
        });
        mainWindow.addComponent(r);
        setMainWindow(mainWindow);
    }

}