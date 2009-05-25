package com.vaadin.demo.sampler.features.table;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.demo.sampler.ExampleUtil;
import com.vaadin.event.Action;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.CellStyleGenerator;

@SuppressWarnings("serial")
public class TableMainFeaturesExample extends VerticalLayout {

    Table table = new Table("ISO-3166 Country Codes and flags");

    HashSet<Object> markedRows = new HashSet<Object>();

    static final Action ACTION_MARK = new Action("Mark");
    static final Action ACTION_UNMARK = new Action("Unmark");
    static final Action ACTION_LOG = new Action("Save");
    static final Action[] ACTIONS_UNMARKED = new Action[] { ACTION_MARK,
            ACTION_LOG };
    static final Action[] ACTIONS_MARKED = new Action[] { ACTION_UNMARK,
            ACTION_LOG };

    public TableMainFeaturesExample() {
        addComponent(table);

        // Label to indicate current selection
        final Label selected = new Label("No selection");
        addComponent(selected);

        // set a style name, so we can style rows and cells
        table.setStyleName("iso3166");

        // size
        table.setWidth("100%");
        table.setPageLength(7);

        // selectable
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true); // react at once when something is selected

        // connect data source
        table.setContainerDataSource(ExampleUtil.getISO3166Container());

        // turn on column reordering and collapsing
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        // set column headers
        table.setColumnHeaders(new String[] { "Country", "Code", "Icon file" });

        // Icons for column headers
        table.setColumnIcon(ExampleUtil.iso3166_PROPERTY_FLAG,
                new ThemeResource("icons/action_save.gif"));
        table.setColumnIcon(ExampleUtil.iso3166_PROPERTY_NAME,
                new ThemeResource("icons/icon_get_world.gif"));
        table.setColumnIcon(ExampleUtil.iso3166_PROPERTY_SHORT,
                new ThemeResource("icons/page_code.gif"));

        // Column alignment
        table.setColumnAlignment(ExampleUtil.iso3166_PROPERTY_SHORT,
                Table.ALIGN_CENTER);

        // Column width
        table.setColumnExpandRatio(ExampleUtil.iso3166_PROPERTY_NAME, 1);
        table.setColumnWidth(ExampleUtil.iso3166_PROPERTY_SHORT, 70);

        // Collapse one column - the user can make it visible again
        try {
            table.setColumnCollapsed(ExampleUtil.iso3166_PROPERTY_FLAG, true);
        } catch (IllegalAccessException e) {
            // Not critical, but strange
            System.err.println(e);
        }

        // show row header w/ icon
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        table.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);

        // Actions (a.k.a context menu)
        table.addActionHandler(new Action.Handler() {
            public Action[] getActions(Object target, Object sender) {
                if (markedRows.contains(target)) {
                    return ACTIONS_MARKED;
                } else {
                    return ACTIONS_UNMARKED;
                }
            }

            public void handleAction(Action action, Object sender, Object target) {
                if (ACTION_MARK.equals(action)) {
                    markedRows.add(target);
                    table.requestRepaint();
                } else if (ACTION_UNMARK.equals(action)) {
                    markedRows.remove(target);
                    table.requestRepaint();
                } else if (ACTION_LOG.equals(action)) {
                    Item item = table.getItem(target);
                    addComponent(new Label("Saved: "
                            + target
                            + ", "
                            + item.getItemProperty(
                                    ExampleUtil.iso3166_PROPERTY_NAME)
                                    .getValue()));
                }

            }

        });

        // style generator
        table.setCellStyleGenerator(new CellStyleGenerator() {
            public String getStyle(Object itemId, Object propertyId) {
                if (propertyId == null) {
                    // no propertyId, styling row
                    return (markedRows.contains(itemId) ? "marked" : null);
                } else if (ExampleUtil.iso3166_PROPERTY_NAME.equals(propertyId)) {
                    return "bold";
                } else {
                    // no style
                    return null;
                }

            }

        });

        // listen for valueChange, a.k.a 'select' and update the label
        table.addListener(new Table.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // in multiselect mode, a Set of itemIds is returned,
                // in singleselect mode the itemId is returned directly
                Set<?> value = (Set<?>) event.getProperty().getValue();
                if (null == value || value.size() == 0) {
                    selected.setValue("No selection");
                } else {
                    selected.setValue("Selected: " + table.getValue());
                }
            }
        });

    }
}
