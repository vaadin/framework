package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

/**
 * Table example.
 * 
 * @author IT Mill Ltd.
 */
public class TableExample extends CustomComponent implements
        Field.ValueChangeListener, Action.Handler {

    // Actions
    private static final Action ACTION_SAVE = new Action("Save");
    private static final Action ACTION_DELETE = new Action("Delete");
    private static final Action ACTION_HIRE = new Action("Hire");
    // Action sets
    private static final Action[] ACTIONS_NOHIRE = new Action[] { ACTION_SAVE,
            ACTION_DELETE };
    private static final Action[] ACTIONS_HIRE = new Action[] { ACTION_HIRE,
            ACTION_SAVE, ACTION_DELETE };
    // Properties
    private static final Object PROPERTY_SPECIES = "Species";
    private static final Object PROPERTY_TYPE = "Type";
    private static final Object PROPERTY_KIND = "Kind";
    private static final Object PROPERTY_HIRED = "Hired";

    Table source;
    Table saved;

    public TableExample() {

        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        source = new Table("The source");
        source.setPageLength(7);
        source.setWidth(100);
        source.setWidthUnits(Table.UNITS_PERCENTAGE);
        source.setColumnCollapsingAllowed(true);
        source.setColumnReorderingAllowed(true);
        source.setSelectable(true);
        source.setMultiSelect(true);
        source.setImmediate(true);
        source.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        fillTable(source);
        source.addActionHandler(this);
        main.addComponent(source);

        saved = new Table("Saved");
        saved.setPageLength(4);
        saved.setWidth(100);
        saved.setWidthUnits(Table.UNITS_PERCENTAGE);
        saved.setSelectable(false);
        saved.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        saved.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        initProperties(saved);
        saved.addActionHandler(this);
        main.addComponent(saved);
    }

    private void initProperties(Table table) {
        table.addContainerProperty(PROPERTY_SPECIES, String.class, "");
        table.addContainerProperty(PROPERTY_TYPE, String.class, "");
        table.addContainerProperty(PROPERTY_KIND, String.class, "");
        table
                .addContainerProperty(PROPERTY_HIRED, Boolean.class,
                        Boolean.FALSE);
    }

    private void fillTable(Table table) {
        initProperties(table);

        String[] sp = new String[] { "Fox", "Dog", "Cat", "Moose", "Penguin",
                "Cow" };
        String[] ty = new String[] { "Quick", "Lazy", "Sleepy", "Fidgety",
                "Crazy", "Kewl" };
        String[] ki = new String[] { "Jumping", "Walking", "Sleeping",
                "Skipping", "Dancing" };

        for (int i = 0; i < 100; i++) {
            String s = sp[(int) (Math.random() * sp.length)];
            String t = ty[(int) (Math.random() * ty.length)];
            String k = ki[(int) (Math.random() * ki.length)];
            table.addItem(new Object[] { s, t, k, Boolean.FALSE }, new Integer(
                    i));
        }

    }

    public Action[] getActions(Object target, Object sender) {
        if (sender == source) {
            Item item = source.getItem(target);
            if (item != null
                    && item.getItemProperty(PROPERTY_HIRED).getValue() == Boolean.FALSE) {
                return ACTIONS_HIRE;
            } else {
                return ACTIONS_NOHIRE;
            }
        } else {
            return new Action[] { ACTION_DELETE };
        }
    }

    public void handleAction(Action action, Object sender, Object target) {
        if (sender == source) {
            Item item = source.getItem(target);
            if (action == ACTION_HIRE) {
                item.getItemProperty(PROPERTY_HIRED).setValue(Boolean.TRUE);
                source.requestRepaint();
                if (saved.containsId(target)) {
                    item = saved.getItem(target);
                    item.getItemProperty(PROPERTY_HIRED).setValue(Boolean.TRUE);
                    saved.requestRepaint();
                }

                getWindow().showNotification("Hired", "" + item);
            } else if (action == ACTION_SAVE) {
                if (saved.containsId(target)) {
                    getWindow().showNotification("Already saved", "" + item);
                    return;
                }
                Item added = saved.addItem(target);
                Property p = added.getItemProperty(PROPERTY_SPECIES);
                p.setValue(item.getItemProperty(PROPERTY_SPECIES).getValue());
                p = added.getItemProperty(PROPERTY_TYPE);
                p.setValue(item.getItemProperty(PROPERTY_TYPE).getValue());
                p = added.getItemProperty(PROPERTY_KIND);
                p.setValue(item.getItemProperty(PROPERTY_KIND).getValue());
                p = added.getItemProperty(PROPERTY_HIRED);
                p.setValue(item.getItemProperty(PROPERTY_HIRED).getValue());
                getWindow().showNotification("Saved", "" + item);
            } else {
                // ACTION_DELETE
                getWindow().showNotification("Deleted ", "" + item);
                source.removeItem(target);
            }
        } else {
            // sender==saved
            if (action == ACTION_DELETE) {
                Item item = saved.getItem(target);
                getWindow().showNotification("Deleted", "" + item);
                saved.removeItem(target);
            }
        }
    }

    public void valueChange(ValueChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
