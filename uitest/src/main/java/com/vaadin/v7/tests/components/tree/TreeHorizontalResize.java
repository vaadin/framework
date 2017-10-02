package com.vaadin.v7.tests.components.tree;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Tree;

public class TreeHorizontalResize extends TestBase {

    // copied from Sampler to eliminate dependency
    public static final Object hw_PROPERTY_NAME = "name";
    public static final Object hw_PROPERTY_ICON = "icon";
    private static final String[][] hardware = { //
            { "Desktops", "Dell OptiPlex GX240", "Dell OptiPlex GX260",
                    "Dell OptiPlex GX280" },
            { "Monitors", "Benq T190HD", "Benq T220HD", "Benq T240HD" },
            { "Laptops", "IBM ThinkPad T40", "IBM ThinkPad T43",
                    "IBM ThinkPad T60" } };

    @Override
    protected void setup() {
        VerticalLayout treeLayout = new VerticalLayout();
        treeLayout.setMargin(true);
        treeLayout.setSizeUndefined();
        Panel treePanel = new Panel(treeLayout);
        treePanel.setHeight("500px");
        treePanel.setWidth(null);
        addComponent(treePanel);

        Tree tree = new Tree();
        tree.setContainerDataSource(getHardwareContainer());
        tree.setItemCaptionPropertyId(hw_PROPERTY_NAME);
        for (Object id : tree.rootItemIds()) {
            tree.expandItemsRecursively(id);
        }
        treeLayout.addComponent(tree);
    }

    @Override
    protected String getDescription() {
        return "The Tree should be properly resized horizontally when collapsing/expanding nodes. The height is fixed to 500px.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6230;
    }

    public static HierarchicalContainer getHardwareContainer() {
        Item item = null;
        int itemId = 0; // Increasing numbering for itemId:s

        // Create new container
        HierarchicalContainer hwContainer = new HierarchicalContainer();
        // Create containerproperty for name
        hwContainer.addContainerProperty(hw_PROPERTY_NAME, String.class, null);
        // Create containerproperty for icon
        hwContainer.addContainerProperty(hw_PROPERTY_ICON, ThemeResource.class,
                new ThemeResource("../runo/icons/16/document.png"));
        for (String[] type : hardware) {
            // Add new item
            item = hwContainer.addItem(itemId);
            // Add name property for item
            item.getItemProperty(hw_PROPERTY_NAME).setValue(type[0]);
            // Allow children
            hwContainer.setChildrenAllowed(itemId, true);
            itemId++;
            for (int j = 1; j < type.length; j++) {
                if (j == 1) {
                    item.getItemProperty(hw_PROPERTY_ICON).setValue(
                            new ThemeResource("../runo/icons/16/folder.png"));
                }
                // Add child items
                item = hwContainer.addItem(itemId);
                item.getItemProperty(hw_PROPERTY_NAME).setValue(type[j]);
                hwContainer.setParent(itemId, itemId - j);
                hwContainer.setChildrenAllowed(itemId, false);

                itemId++;
            }
        }
        return hwContainer;
    }

}
