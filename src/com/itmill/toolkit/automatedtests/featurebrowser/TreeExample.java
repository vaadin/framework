/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.automatedtests.featurebrowser;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;

/**
 * Demonstrates basic Tree -functionality. Actions are used for add/remove item
 * functionality, and a ValueChangeListener reacts to both the Tree and the
 * TextField.
 */
public class TreeExample extends CustomComponent implements Action.Handler,
        Tree.ValueChangeListener {

    private static final Action ADD = new Action("Add item");
    private static final Action DELETE = new Action("Delete item");
    private static final Action[] actions = new Action[] { ADD, DELETE };

    // Id for the caption property
    private static final Object CAPTION_PROPERTY = "caption";

    private static final String desc = "Try both right- and left-click!";

    Tree tree;
    TextField editor;

    public TreeExample() {
        final OrderedLayout main = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.setDebugId("mainLayout");
        main.setMargin(true);
        setCompositionRoot(main);

        // Panel w/ Tree
        Panel p = new Panel("Select item");
        p.setStyleName(Panel.STYLE_LIGHT);
        p.setWidth(250);
        // Description
        p.addComponent(new Label(desc));
        // Tree with a few items
        tree = new Tree();
        tree.setDebugId("tree");
        tree.setImmediate(true);
        // we'll use a property for caption instead of the item id ("value"),
        // so that multiple items can have the same caption
        tree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(CAPTION_PROPERTY);
        for (int i = 1; i <= 3; i++) {
            final Object id = addCaptionedItem("Section " + i, null);
            tree.expandItem(id);
            addCaptionedItem("Team A", id);
            addCaptionedItem("Team B", id);
        }
        // listen for selections
        tree.addListener(this);
        // "context menu"
        tree.addActionHandler(this);
        p.addComponent(tree);
        main.addComponent(p);

        // Panel w/ TextField ("editor")
        p = new Panel("Edit item caption");
        p.setStyleName(Panel.STYLE_LIGHT);
        editor = new TextField();
        // make immediate, instead of adding an "apply" button
        editor.setImmediate(true);
        editor.setEnabled(false);
        editor.setColumns(15);
        p.addComponent(editor);
        main.addComponent(p);
    }

    public Action[] getActions(Object target, Object sender) {
        // We can provide different actions for each target (item), but we'll
        // use the same actions all the time.
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        if (action == DELETE) {
            tree.removeItem(target);
        } else {
            // Add
            final Object id = addCaptionedItem("New Item", target);
            tree.expandItem(target);
            tree.setValue(id);
            editor.focus();
        }
    }

    public void valueChange(ValueChangeEvent event) {
        final Object id = tree.getValue(); // selected item id
        if (event.getProperty() == tree) {
            // a Tree item was (un) selected
            if (id == null) {
                // no selecteion, disable TextField
                editor.removeListener(this);
                editor.setValue("");
                editor.setEnabled(false);
            } else {
                // item selected
                // first remove previous listener
                editor.removeListener(this);
                // enable TextField and update value
                editor.setEnabled(true);
                final Item item = tree.getItem(id);
                editor.setValue(item.getItemProperty(CAPTION_PROPERTY)
                        .getValue());
                // listen for TextField changes
                editor.addListener(this);
                editor.focus();
            }
        } else {
            // TextField
            if (id != null) {
                final Item item = tree.getItem(id);
                final Property p = item.getItemProperty(CAPTION_PROPERTY);
                p.setValue(editor.getValue());
                tree.requestRepaint();
            }

        }
    }

    /**
     * Helper to add an item with specified caption and (optional) parent.
     * 
     * @param caption
     *                The item caption
     * @param parent
     *                The (optional) parent item id
     * @return the created item's id
     */
    private Object addCaptionedItem(String caption, Object parent) {
        // add item, let tree decide id
        final Object id = tree.addItem();
        // get the created item
        final Item item = tree.getItem(id);
        // set our "caption" property
        final Property p = item.getItemProperty(CAPTION_PROPERTY);
        p.setValue(caption);
        if (parent != null) {
            tree.setParent(id, parent);
        }
        return id;
    }

}
