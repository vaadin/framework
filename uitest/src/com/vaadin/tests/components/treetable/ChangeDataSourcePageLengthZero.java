package com.vaadin.tests.components.treetable;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Link;
import com.vaadin.ui.TreeTable;

public class ChangeDataSourcePageLengthZero extends TestBase {
    @Override
    public void setup() {
        final TreeTable tt = new TreeTable();
        tt.setSizeUndefined();
        tt.setWidth("100%");
        tt.setImmediate(true);
        tt.setPageLength(0);
        setupContainer(tt, 20);
        addComponent(tt);
        Button page1 = new Button("Set new data source (20 items)");
        page1.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setupContainer(tt, 20);
            }
        });
        Button page2 = new Button("Set new data source (10 items)");
        page2.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setupContainer(tt, 10);
            }
        });
        Button addButton = new Button("Add item");
        addButton.addListener(new Button.ClickListener() {
            private int i = 1;

            @Override
            public void buttonClick(ClickEvent event) {
                HierarchicalContainer container = (HierarchicalContainer) tt
                        .getContainerDataSource();
                Object itemId = container.addItem();
                container.getContainerProperty(itemId, "i").setValue(i++);
                container.getContainerProperty(itemId, "link").setValue(
                        new Link(String.valueOf(i + 1), new ExternalResource(
                                "http://www.google.fi")));
                container.setChildrenAllowed(itemId, false);
                container.setParent(itemId, null);
            }
        });
        addComponent(page1);
        addComponent(page2);
        addComponent(addButton);
    }

    private static void setupContainer(TreeTable tt, int num) {
        HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("i", Integer.class, null);
        container.addContainerProperty("link", Link.class, null);
        fillContainer(container, num);
        tt.setContainerDataSource(container);
    }

    private static void fillContainer(Hierarchical container, int num) {
        Object previous = null;
        for (int i = 0; i < num; i++) {
            Object item = container.addItem();
            container.getContainerProperty(item, "i").setValue(i + 1);
            container.getContainerProperty(item, "link").setValue(
                    new Link(String.valueOf(i + 1), new ExternalResource(
                            "http://www.google.fi")));
            if (i > 0 && (i + 1) % 2 == 0) {
                container.setChildrenAllowed(item, false);
                container.setParent(item, previous);
            } else {
                container.setChildrenAllowed(item, true);
            }
            previous = item;
        }
    }

    @Override
    protected String getDescription() {
        return "Changing the data source should update the height of a TreeTable with pagelength zero";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7908;
    }
}
