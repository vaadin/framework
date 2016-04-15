package com.vaadin.tests.components.tree;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Tree;

public class TreeIconUpdate extends TestBase {

    private static final Resource ICON1 = new ThemeResource(
            "../runo/icons/16/folder.png");
    private static final Resource ICON2 = new ThemeResource(
            "../runo/icons/16/ok.png");

    @Override
    protected void setup() {
        HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("icon", Resource.class, null);
        final Tree tree = new Tree();
        tree.setContainerDataSource(container);
        tree.setItemIconPropertyId("icon");
        tree.setItemCaptionPropertyId("name");

        for (int i = 0; i < 20; i++) {
            Item bar = container.addItem("bar" + i);
            bar.getItemProperty("name").setValue("Bar" + i);
            bar.getItemProperty("icon").setValue(ICON1);

            if (i > 3) {
                container.setParent("bar" + i, "bar" + (i - 1));
            }
        }

        addComponent(tree);

        Button button = new Button("Change icon", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tree.getItem("bar0").getItemProperty("icon").setValue(ICON2);
            }
        });

        addComponent(button);
        button = new Button("Change caption", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tree.getItem("bar0").getItemProperty("name").setValue("foo");
            }
        });

        addComponent(button);

    }

    @Override
    protected String getDescription() {
        return "Click the button to change the icon. The tree should be updated";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9663;
    }

}
