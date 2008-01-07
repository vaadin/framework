/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
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

    // "global" components
    Table source;
    Table saved;
    Button saveSelected;
    Button hireSelected;
    Button deleteSelected;
    Button deselect;

    public TableExample() {
        // main layout
        final OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        // "source" table with bells & whistlesenabled
        source = new Table("All creatures");
        source.setDebugId("PID_AllCreatures");
        source.setPageLength(7);
        source.getSize().setWidth(550);
        source.setColumnCollapsingAllowed(true);
        source.setColumnReorderingAllowed(true);
        source.setSelectable(true);
        source.setMultiSelect(true);
        source.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        fillTable(source);
        source.addActionHandler(this);
        main.addComponent(source);

        // x-selected button row
        final OrderedLayout horiz = new OrderedLayout(
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
        deselect = new Button("Deselect all");
        deselect.setStyleName(Button.STYLE_LINK);
        deselect.addListener(this);
        horiz.addComponent(deselect);
        final CheckBox editmode = new CheckBox("Editmode ");
        editmode.addListener(new CheckBox.ClickListener() {
            public void buttonClick(ClickEvent event) {
                source.setEditable(((Boolean) event.getButton().getValue())
                        .booleanValue());
            }
        });
        editmode.setImmediate(true);
        horiz.addComponent(editmode);

        // "saved" table, minimalistic
        saved = new Table("Saved creatures");
        saved.setPageLength(5);
        saved.getSize().setWidth(550);
        saved.setSelectable(false);
        saved.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        saved.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        initProperties(saved);
        saved.addActionHandler(this);
        main.addComponent(saved);

        final CheckBox b = new CheckBox("Modify saved creatures");
        b.addListener(new CheckBox.ClickListener() {
            public void buttonClick(ClickEvent event) {
                saved.setEditable(((Boolean) event.getButton().getValue())
                        .booleanValue());
            }
        });
        b.setImmediate(true);
        main.addComponent(b);

    }

    // set up the properties (columns)
    private void initProperties(Table table) {
        table.addContainerProperty(PROPERTY_SPECIES, String.class, "");
        table.addContainerProperty(PROPERTY_TYPE, String.class, "");
        table.addContainerProperty(PROPERTY_KIND, String.class, "");
        table
                .addContainerProperty(PROPERTY_HIRED, Boolean.class,
                        Boolean.FALSE);
    }

    // fill the table with some random data
    private void fillTable(Table table) {
        initProperties(table);

        final String[] sp = new String[] { "Fox", "Dog", "Cat", "Moose",
                "Penguin", "Cow" };
        final String[] ty = new String[] { "Quick", "Lazy", "Sleepy",
                "Fidgety", "Crazy", "Kewl" };
        final String[] ki = new String[] { "Jumping", "Walking", "Sleeping",
                "Skipping", "Dancing" };

        Random r = new Random(5);

        for (int i = 0; i < 100; i++) {
            final String s = sp[(int) (r.nextDouble() * sp.length)];
            final String t = ty[(int) (r.nextDouble() * ty.length)];
            final String k = ki[(int) (r.nextDouble() * ki.length)];
            table.addItem(new Object[] { s, t, k, Boolean.FALSE }, new Integer(
                    i));
        }

    }

    // Called for each item (row), returns valid actions for that item
    public Action[] getActions(Object target, Object sender) {
        if (sender == source) {
            final Item item = source.getItem(target);
            // save, delete, and hire if not already hired
            if (item != null
                    && item.getItemProperty(PROPERTY_HIRED).getValue() == Boolean.FALSE) {
                return ACTIONS_HIRE;
            } else {
                return ACTIONS_NOHIRE;
            }
        } else {
            // "saved" table only has one action
            return new Action[] { ACTION_DELETE };
        }
    }

    // called when an action is invoked on an item (row)
    public void handleAction(Action action, Object sender, Object target) {
        if (sender == source) {
            Item item = source.getItem(target);
            if (action == ACTION_HIRE) {
                // set HIRED property to true
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
                    // let's not save twice
                    getWindow().showNotification("Already saved", "" + item);
                    return;
                }
                // "manual" copy of the item properties we want
                final Item added = saved.addItem(target);
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
                final Item item = saved.getItem(target);
                getWindow().showNotification("Deleted", "" + item);
                saved.removeItem(target);
            }
        }
    }

    public void buttonClick(ClickEvent event) {
        final Button b = event.getButton();
        if (b == deselect) {
            source.setValue(null);
        } else if (b == saveSelected) {
            // loop each selected and copy to "saved" table
            final Set selected = (Set) source.getValue();
            int s = 0;
            for (final Iterator it = selected.iterator(); it.hasNext();) {
                final Object id = it.next();
                if (!saved.containsId(id)) {
                    final Item item = source.getItem(id);
                    final Item added = saved.addItem(id);
                    // "manual" copy of the properties we want
                    Property p = added.getItemProperty(PROPERTY_SPECIES);
                    p.setValue(item.getItemProperty(PROPERTY_SPECIES)
                            .getValue());
                    p = added.getItemProperty(PROPERTY_TYPE);
                    p.setValue(item.getItemProperty(PROPERTY_TYPE).getValue());
                    p = added.getItemProperty(PROPERTY_KIND);
                    p.setValue(item.getItemProperty(PROPERTY_KIND).getValue());
                    p = added.getItemProperty(PROPERTY_HIRED);
                    p.setValue(item.getItemProperty(PROPERTY_HIRED).getValue());
                    s++;
                }
            }
            getWindow().showNotification("Saved " + s);
            saved.requestRepaint();

        } else if (b == hireSelected) {
            // loop each selected and set property HIRED to true
            int s = 0;
            final Set selected = (Set) source.getValue();
            for (final Iterator it = selected.iterator(); it.hasNext();) {
                final Object id = it.next();
                Item item = source.getItem(id);
                final Property p = item.getItemProperty(PROPERTY_HIRED);
                if (p.getValue() == Boolean.FALSE) {
                    p.setValue(Boolean.TRUE);
                    source.requestRepaint();
                    s++;
                }
                if (saved.containsId(id)) {
                    // also update "saved" table
                    item = saved.getItem(id);
                    item.getItemProperty(PROPERTY_HIRED).setValue(Boolean.TRUE);
                    saved.requestRepaint();
                }
            }
            getWindow().showNotification("Hired " + s);

        } else {
            // loop trough selected and delete
            int s = 0;
            final Set selected = (Set) source.getValue();
            for (final Iterator it = selected.iterator(); it.hasNext();) {
                final Object id = it.next();
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
