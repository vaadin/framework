package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TreeTable;

public class RemoveAllItemsRefresh extends TestBase {
    protected static final String NAME_PROPERTY = "Name";
    protected static final String TITLE_PROPERTY = "Title";

    private VerticalLayout treeLayout = new VerticalLayout();
    private Table treetable;

    private HierarchicalContainer treeContainer;

    @Override
    protected void setup() {
        treetable = new TreeTable();
        treeContainer = new HierarchicalContainer();
        // Create the treetable
        treetable.setSelectable(true);
        treetable.setSizeFull();

        treeContainer.addContainerProperty(NAME_PROPERTY, String.class, "");
        treeContainer.addContainerProperty(TITLE_PROPERTY, String.class, "");
        treetable.setContainerDataSource(treeContainer);

        treeLayout.addComponent(treetable);
        addComponent(treeLayout);

        Button cleanUp = new Button("clear");
        cleanUp.addClickListener(event -> treeContainer.removeAllItems());
        addComponent(cleanUp);

        Button refresh = new Button("fill");
        refresh.addClickListener(event -> fill());
        addComponent(refresh);

        fill();
    }

    private void fill() {
        Item containerItem;

        treeLayout.removeAllComponents();
        treeLayout.addComponent(treetable);

        treeContainer.removeAllItems();
        containerItem = treeContainer.addItem("first");
        containerItem.getItemProperty(NAME_PROPERTY)
                .setValue("1 NAME_PROPERTY");
        containerItem.getItemProperty(TITLE_PROPERTY)
                .setValue("1 TITLE_PROPERTY");

        containerItem = treeContainer.addItem("second");
        containerItem.getItemProperty(NAME_PROPERTY)
                .setValue("2 NAME_PROPERTY");
        containerItem.getItemProperty(TITLE_PROPERTY)
                .setValue("2 TITLE_PROPERTY");
        treetable.setContainerDataSource(treeContainer);
    }

    @Override
    protected String getDescription() {
        return "Removing all items from a treetable should refresh the component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7720;
    }
}
