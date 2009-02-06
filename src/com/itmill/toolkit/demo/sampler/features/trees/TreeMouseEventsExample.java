package com.itmill.toolkit.demo.sampler.features.trees;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.event.ItemClickEvent;
import com.itmill.toolkit.event.ItemClickEvent.ItemClickListener;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;

public class TreeMouseEventsExample extends VerticalLayout implements
        ItemClickListener {

    private Tree t;
    private int itemId;

    public TreeMouseEventsExample() {
        setSpacing(true);

        // Create new Tree object using a hierarchical container from
        // ExampleUtil class
        t = new Tree("Hardware Inventory", ExampleUtil.getHardwareContainer());

        // Add ItemClickListener to the tree
        t.addListener(this);

        t.setImmediate(true);

        // Set tree to show the 'name' property as caption for items
        t.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
        t.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

        // Starting itemId # for new items
        itemId = t.getContainerDataSource().size();

        // Expand whole tree
        for (int i = 0; i < itemId; i++) {
            t.expandItemsRecursively(i);
        }

        // Disallow selecting items from the tree
        t.setSelectable(false);

        addComponent(t);
    }

    public void itemClick(ItemClickEvent event) {
        // Indicate which modifier keys are pressed
        String modifiers = "";
        if (event.isAltKey()) {
            modifiers += "Alt ";
        }
        if (event.isCtrlKey()) {
            modifiers += "Ctrl ";
        }
        if (event.isMetaKey()) {
            modifiers += "Meta ";
        }
        if (event.isShiftKey()) {
            modifiers += "Shift ";
        }
        if (modifiers.length() > 0) {
            modifiers = "Modifiers: " + modifiers;
        } else {
            modifiers = "Modifiers: none";
        }
        switch (event.getButton()) {
        case ItemClickEvent.BUTTON_LEFT:
            // Left button click updates the 'selected' Label
            getWindow().showNotification("Selected item: " + event.getItem(),
                    modifiers);
            break;
        case ItemClickEvent.BUTTON_MIDDLE:
            // Middle button click removes the item
            Object parent = t.getParent(event.getItemId());
            getWindow().showNotification("Removed item: " + event.getItem(),
                    modifiers);
            t.removeItem(event.getItemId());
            if (parent != null && t.getChildren(parent).size() == 0) {
                t.setChildrenAllowed(parent, false);
            }
            break;
        case ItemClickEvent.BUTTON_RIGHT:
            // Right button click creates a new child item
            getWindow().showNotification("Added item: New Item # " + itemId,
                    modifiers);
            t.setChildrenAllowed(event.getItemId(), true);
            Item i = t.addItem(itemId);
            t.setChildrenAllowed(itemId, false);
            String newItemName = "New Item # " + itemId;
            i.getItemProperty(ExampleUtil.hw_PROPERTY_NAME).setValue(
                    newItemName);
            t.setParent(itemId, event.getItemId());
            t.expandItem(event.getItemId());
            itemId++;
            break;
        }
    }
}
