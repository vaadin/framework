package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;

/**
 * 
 */
public class TreeExample extends CustomComponent implements Action.Handler,
        Tree.ValueChangeListener {

    private static final Action ADD = new Action("Add item");
    private static final Action DELETE = new Action("Delete item");
    private static final Action[] actions = new Action[] { ADD, DELETE };

    private static final Object CAPTION_PROPERTY = "caption";

    private static final String desc = "Select an item in the tree to edit it's"
            + " caption. Right-click to add or remove items.";

    Tree tree;
    TextField tf;

    public TreeExample() {
        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);
        // Description
        main.addComponent(new Label(desc));
        // Caption editor
        tf = new TextField("Edit item caption");
        tf.setImmediate(true);
        tf.setEnabled(false);
        tf.setColumns(15);
        main.addComponent(tf);

        // Add tree with a few items
        tree = new Tree();
        tree.setImmediate(true);
        tree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(CAPTION_PROPERTY);
        for (int i = 1; i <= 3; i++) {
            Object id = addCaptionedItem("Section " + i, null);
            tree.expandItem(id);
            addCaptionedItem("Team A", id);
            addCaptionedItem("Team B", id);
        }
        tree.addListener(this);
        tree.addActionHandler(this);
        main.addComponent(tree);

    }

    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        if (action == DELETE) {
            tree.removeItem(target);
        } else {
            // Add
            Object id = addCaptionedItem("New Item", target);
            tree.expandItem(target);
            tree.setValue(id);
            tf.focus();
        }
    }

    public void valueChange(ValueChangeEvent event) {
        Object id = tree.getValue();
        if (event.getProperty() == tree) {
            if (id == null) {
                tf.removeListener(this);
                tf.setValue("");
                tf.setEnabled(false);

            } else {
                tf.setEnabled(true);
                Item item = tree.getItem(id);
                tf.setValue(item.getItemProperty(CAPTION_PROPERTY).getValue());
                tf.addListener(this);
            }
        } else {
            // TextField
            if (id != null) {
                Item item = tree.getItem(id);
                Property p = item.getItemProperty(CAPTION_PROPERTY);
                p.setValue(tf.getValue());
                tree.requestRepaint();
            }

        }
    }

    private Object addCaptionedItem(String caption, Object parent) {
        Object id = tree.addItem();
        Item item = tree.getItem(id);
        Property p = item.getItemProperty("caption");
        p.setValue(caption);
        if (parent != null) {
            tree.setParent(id, parent);
        }
        return id;
    }

}
