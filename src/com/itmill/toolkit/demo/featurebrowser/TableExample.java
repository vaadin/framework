package com.itmill.toolkit.demo.featurebrowser;

import java.util.Iterator;
import java.util.Set;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * Table example.
 * 
 * @author IT Mill Ltd.
 */
public class TableExample extends CustomComponent implements Action.Handler,
        Button.ClickListener {

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

    Button saveSelected;
    Button hireSelected;
    Button deleteSelected;

    public TableExample() {

        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        source = new Table("Also try the row context-menu");
        source.setPageLength(7);
        source.setWidth(550);
        source.setColumnCollapsingAllowed(true);
        source.setColumnReorderingAllowed(true);
        source.setSelectable(true);
        source.setMultiSelect(true);
        source.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        fillTable(source);
        source.addActionHandler(this);
        main.addComponent(source);

        OrderedLayout horiz = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        horiz.setMargin(false, false, true, false);
        main.addComponent(horiz);
        saveSelected = new Button("Save selected");
        saveSelected.setStyleName(Button.STYLE_LINK);
        saveSelected.addListener(this);
        horiz.addComponent(saveSelected);
        hireSelected = new Button("Hire selected");
        hireSelected.setStyleName(Button.STYLE_LINK);
        hireSelected.addListener(this);
        horiz.addComponent(hireSelected);
        deleteSelected = new Button("Delete selected");
        deleteSelected.setStyleName(Button.STYLE_LINK);
        deleteSelected.addListener(this);
        horiz.addComponent(deleteSelected);

        saved = new Table();
        saved.setPageLength(5);
        saved.setWidth(550);
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

    public void buttonClick(ClickEvent event) {
        Button b = event.getButton();
        if (b == saveSelected) {
            Set selected = (Set) source.getValue();
            if (selected != null) {
                int s = 0;
                for (Iterator it = selected.iterator(); it.hasNext();) {
                    Object id = it.next();
                    if (!saved.containsId(id)) {
                        Item item = source.getItem(id);
                        Item added = saved.addItem(id);
                        Property p = added.getItemProperty(PROPERTY_SPECIES);
                        p.setValue(item.getItemProperty(PROPERTY_SPECIES)
                                .getValue());
                        p = added.getItemProperty(PROPERTY_TYPE);
                        p.setValue(item.getItemProperty(PROPERTY_TYPE)
                                .getValue());
                        p = added.getItemProperty(PROPERTY_KIND);
                        p.setValue(item.getItemProperty(PROPERTY_KIND)
                                .getValue());
                        p = added.getItemProperty(PROPERTY_HIRED);
                        p.setValue(item.getItemProperty(PROPERTY_HIRED)
                                .getValue());
                        s++;
                    }
                }
                getWindow().showNotification("Saved " + s);
                saved.requestRepaint();
            }
        } else if (b == hireSelected) {
            int s = 0;
            Set selected = (Set) source.getValue();
            for (Iterator it = selected.iterator(); it.hasNext();) {
                Object id = it.next();
                Item item = source.getItem(id);
                Property p = item.getItemProperty(PROPERTY_HIRED);
                if (p.getValue() == Boolean.FALSE) {
                    p.setValue(Boolean.TRUE);
                    source.requestRepaint();
                    s++;
                }
                if (saved.containsId(id)) {
                    item = saved.getItem(id);
                    item.getItemProperty(PROPERTY_HIRED).setValue(Boolean.TRUE);
                    saved.requestRepaint();
                }
            }
            getWindow().showNotification("Hired " + s);

        } else {
            // delete selected
            int s = 0;
            Set selected = (Set) source.getValue();
            for (Iterator it = selected.iterator(); it.hasNext();) {
                Object id = it.next();
                if (source.containsId(id)) {
                    s++;
                    source.removeItem(id);
                    source.requestRepaint();
                }
            }
            getWindow().showNotification("Deleted " + s);
        }

    }

}
