package com.itmill.toolkit.demo.sampler.features.trees;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TreeSingleSelectExample extends VerticalLayout implements
        Property.ValueChangeListener, Button.ClickListener, Action.Handler {

    private static final Action ACTION_ADD = new Action("Add child item");
    private static final Action ACTION_DELETE = new Action("Delete");
    private static final Action[] ACTIONS = new Action[] { ACTION_ADD,
            ACTION_DELETE };

    private Tree t;
    private TextField editor;
    private Button change;
    private int itemId;

    public TreeSingleSelectExample() {
        setSpacing(true);

        // Create new Tree object using a hierarchical container from
        // ExampleUtil class
        t = new Tree("Hardware Inventory", ExampleUtil.getHardwareContainer());

        // Add Valuechangelistener and Actionhandler
        t.addListener(this);
        t.addActionHandler(this);

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

        // Create the 'editor bar' (textfield and button in a horizontallayout)
        editor = new TextField();
        editor.setImmediate(true);
        change = new Button("Apply", this, "buttonClick");
        change.setEnabled(false);
        HorizontalLayout editBar = new HorizontalLayout();
        editBar.addComponent(editor);
        editBar.addComponent(change);

        addComponent(editBar);
        addComponent(t);

    }

    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty().getValue() != null) {
            // If something is selected from the tree, get it's 'name' and
            // insert it into the textfield
            editor.setValue(t.getItem(event.getProperty().getValue())
                    .getItemProperty(ExampleUtil.hw_PROPERTY_NAME));
            editor.requestRepaint();
            change.setEnabled(true);
        } else {
            editor.setValue("");
            change.setEnabled(false);
        }
    }

    public void buttonClick(ClickEvent event) {
        // If the edited value contains something, set it to be the item's new
        // 'name' property
        if (!editor.getValue().equals("")) {
            t.getItem(t.getValue()).getItemProperty(
                    ExampleUtil.hw_PROPERTY_NAME).setValue(editor.getValue());
        }
    }

    /*
     * Returns the set of available actions
     */
    public Action[] getActions(Object target, Object sender) {
        return ACTIONS;
    }

    /*
     * Handle actions
     */
    public void handleAction(Action action, Object sender, Object target) {
        if (action == ACTION_ADD) {
            // Allow children for the target item
            t.setChildrenAllowed(target, true);

            // Create new item, disallow children, add name, set parent
            Item i = t.addItem(itemId);
            t.setChildrenAllowed(itemId, false);
            String newItemName = "New Item # " + itemId;
            i.getItemProperty(ExampleUtil.hw_PROPERTY_NAME).setValue(
                    newItemName);
            t.setParent(itemId, target);
            t.expandItem(target);
            itemId++;
        } else if (action == ACTION_DELETE) {
            Object parent = t.getParent(target);
            t.removeItem(target);
            // If the deleted object's parent has no more children, set it's
            // childrenallowed property to false
            if (t.getChildren(parent).size() == 0) {
                t.setChildrenAllowed(parent, false);
            }
        }
    }
}
