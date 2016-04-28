package com.vaadin.tests.components.tree;

import java.util.Date;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;

public class SimpleTree extends TestBase implements Action.Handler {
    private static final String[][] hardware = { //
            { "Desktops", "Dell OptiPlex GX240", "Dell OptiPlex GX260",
                    "Dell OptiPlex GX280" },
            { "Monitors", "Benq T190HD", "Benq T220HD", "Benq T240HD" },
            { "Laptops", "IBM ThinkPad T40", "IBM ThinkPad T43",
                    "IBM ThinkPad T60" } };

    ThemeResource notCachedFolderIconLargeOther = new ThemeResource(
            "../runo/icons/16/ok.png?" + new Date().getTime());
    ThemeResource notCachedFolderIconLarge = new ThemeResource(
            "../runo/icons/16/folder.png?" + new Date().getTime());

    // Actions for the context menu
    private static final Action ACTION_ADD = new Action("Add child item");
    private static final Action ACTION_DELETE = new Action("Delete");
    private static final Action[] ACTIONS = new Action[] { ACTION_ADD,
            ACTION_DELETE };

    private Tree tree;

    @Override
    public void setup() {
        // Create the Tree,a dd to layout
        tree = new Tree("Hardware Inventory");
        addComponent(tree);

        // Contents from a (prefilled example) hierarchical container:
        tree.setContainerDataSource(getHardwareContainer());

        // Add actions (context menu)
        tree.addActionHandler(this);

        // Cause valueChange immediately when the user selects
        tree.setImmediate(true);

        // Set tree to show the 'name' property as caption for items
        tree.setItemCaptionPropertyId("name");
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

        tree.setItemIcon(9, notCachedFolderIconLargeOther, "First Choice");
        tree.setItemIcon(11, notCachedFolderIconLarge);

        tree.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
            @Override
            public String generateDescription(Component source, Object itemId,
                    Object propertyId) {
                if ((Integer) itemId == 3) {
                    return "tree item tooltip";
                }
                return "";
            }
        });

        // Expand whole tree
        for (Object id : tree.rootItemIds()) {
            tree.expandItemsRecursively(id);
        }
    }

    public static HierarchicalContainer getHardwareContainer() {
        Item item = null;
        int itemId = 0; // Increasing numbering for itemId:s

        // Create new container
        HierarchicalContainer hwContainer = new HierarchicalContainer();
        // Create containerproperty for name
        hwContainer.addContainerProperty("name", String.class, null);
        // Create containerproperty for icon
        hwContainer.addContainerProperty("icon", ThemeResource.class,
                new ThemeResource("../runo/icons/16/document.png"));
        for (int i = 0; i < hardware.length; i++) {
            // Add new item
            item = hwContainer.addItem(itemId);
            // Add name property for item
            item.getItemProperty("name").setValue(hardware[i][0]);
            // Allow children
            hwContainer.setChildrenAllowed(itemId, true);
            itemId++;
            for (int j = 1; j < hardware[i].length; j++) {
                if (j == 1) {
                    item.getItemProperty("icon").setValue(
                            new ThemeResource("../runo/icons/16/folder.png"));
                }

                // Add child items
                item = hwContainer.addItem(itemId);
                item.getItemProperty("name").setValue(hardware[i][j]);
                hwContainer.setParent(itemId, itemId - j);

                hwContainer.setChildrenAllowed(itemId, false);
                if (j == 2) {
                    hwContainer.setChildrenAllowed(itemId, true);
                }

                itemId++;
            }
        }
        return hwContainer;
    }

    @Override
    protected String getDescription() {
        return "Sample Tree for testing WAI-ARIA functionality";
    }

    @Override
    protected Integer getTicketNumber() {
        return 0;
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        return ACTIONS;
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        System.out.println("Action: " + action.getCaption());
    }
}
