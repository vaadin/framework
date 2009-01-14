package com.itmill.toolkit.demo.sampler.features.trees;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.event.ItemClickEvent;
import com.itmill.toolkit.event.ItemClickEvent.ItemClickListener;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;

public class TreeMouseEventsExample extends VerticalLayout implements
        ItemClickListener {

    private Tree t;
    private int itemId;
    private Label l;

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

        l = new Label();
        addComponent(t);
        addComponent(l);
    }

    public void itemClick(ItemClickEvent event) {
        switch (event.getButton()) {
        case ItemClickEvent.BUTTON_LEFT:
            // Left button click updates the 'selected' Label
            l.setValue("Selected item: " + event.getItem());
            break;
        case ItemClickEvent.BUTTON_MIDDLE:
            // Middle button click removes the item
            Object parent = t.getParent(event.getItemId());
            l.setValue("Removed item: " + event.getItem());
            t.removeItem(event.getItemId());
            if (parent != null && t.getChildren(parent).size() == 0) {
                t.setChildrenAllowed(parent, false);
            }
            break;
        case ItemClickEvent.BUTTON_RIGHT:
            // Right button click creates a new child item
            l.setValue("Added item: New Item # " + itemId);
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
