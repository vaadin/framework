package com.itmill.toolkit.demo.sampler.features.table;

import java.util.HashSet;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Table.CellStyleGenerator;

public class TableMainFeaturesExample extends VerticalLayout {

    Table table = new Table("ISO-3166 Country Codes and flags");

    HashSet<Object> markedRows = new HashSet<Object>();

    static final Action ACTION_MARK = new Action("Mark");
    static final Action ACTION_UNMARK = new Action("Unmark");
    static final Action ACTION_LOG = new Action("Log");
    static final Action[] ACTIONS_UNMARKED = new Action[] { ACTION_MARK,
            ACTION_LOG };
    static final Action[] ACTIONS_MARKED = new Action[] { ACTION_UNMARK,
            ACTION_LOG };

    public TableMainFeaturesExample() {
        addComponent(table);

        // set a style name, so we can style rows and cells
        table.setStyleName("iso3166");

        // size
        table.setWidth("100%");
        table.setPageLength(7);

        // multiselect mode
        table.setMultiSelect(true);

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
                    addComponent(new Label(target
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

    }
}
