package com.vaadin.tests.components.table;

import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.Table.ColumnResizeListener;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.FooterClickListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;

public class Tables extends ComponentTestCase<Table> implements
        ItemClickListener, HeaderClickListener, FooterClickListener,
        ColumnResizeListener {

    @Override
    protected Class<Table> getTestClass() {
        return Table.class;
    }

    @Override
    protected void initializeComponents() {
        addTestComponent(createTable());
        enableLog();

    }

    private Table createTable() {
        Table t = new Table();
        return t;
    }

    private Container createContainer(int properties, int items) {
        IndexedContainer c = new IndexedContainer();
        for (int i = 1; i <= properties; i++) {
            c.addContainerProperty("Column " + i, String.class, "");
        }
        for (int i = 1; i <= items; i++) {
            Item item = c.addItem("Item " + i);
            for (int j = 1; j <= properties; j++) {
                item.getItemProperty("Column " + j).setValue(
                        "Item " + i + "," + j);
            }
        }

        return c;
    }

    @Override
    protected void createCustomActions(List<Component> actions) {
        actions.add(createNullSelectCheckbox());
        actions.add(createWidthSelect());
        actions.add(createHeightSelect());
        actions.add(createPageLengthSelect());
        actions.add(createItemsInContainerSelect());
        actions.add(createColumnsInContainerSelect());
        actions.add(createSelectionModeSelect());
        actions.add(createItemClickListenerCheckbox());
        actions.add(createColumnResizeListenerCheckbox());

        actions.add(createRowHeaderModeSelect());

        actions.add(createHeaderVisibilitySelect());
        actions.add(createHeaderClickListenerCheckbox());
        actions.add(createHeaderTextCheckbox());

        actions.add(createFooterVisibilityCheckbox());
        actions.add(createFooterClickListenerCheckbox());
        actions.add(createFooterTextCheckbox());

    }

    private Component createRowHeaderModeSelect() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Explicit", Table.ROW_HEADER_MODE_EXPLICIT);
        options.put("Explicit defaults id",
                Table.ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID);
        options.put("Hidden", Table.ROW_HEADER_MODE_HIDDEN);
        options.put("Icon only", Table.ROW_HEADER_MODE_ICON_ONLY);
        options.put("Id", Table.ROW_HEADER_MODE_ID);
        options.put("Index", Table.ROW_HEADER_MODE_INDEX);
        options.put("Item", Table.ROW_HEADER_MODE_ITEM);
        options.put("'Column 3' property", Table.ROW_HEADER_MODE_PROPERTY);

        return super.createSelectAction("Row header mode", options, "Hidden",
                new Command<Table, Integer>() {

                    public void execute(Table c, Integer value) {
                        if (value == Table.ROW_HEADER_MODE_PROPERTY) {
                            c.setItemCaptionPropertyId("Column 3");
                        }
                        c.setRowHeaderMode(value);

                    }
                });
    }

    private Component createFooterTextCheckbox() {
        return super.createCheckboxAction("Texts in footer", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        for (Object propertyId : c.getContainerPropertyIds()) {
                            if (value) {
                                c.setColumnFooter(propertyId, "Footer: "
                                        + propertyId);
                            } else {
                                c.setColumnFooter(propertyId, null);

                            }
                        }

                    }
                });
    }

    private Component createHeaderTextCheckbox() {
        return super.createCheckboxAction("Texts in header", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        for (Object propertyId : c.getContainerPropertyIds()) {
                            if (value) {
                                c.setColumnHeader(propertyId, "Header: "
                                        + propertyId);
                            } else {
                                c.setColumnHeader(propertyId, null);

                            }
                        }

                    }
                });
    }

    private Component createItemClickListenerCheckbox() {
        return super.createCheckboxAction("Item click listener", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        if (value) {
                            c.addListener((ItemClickListener) Tables.this);
                        } else {
                            c.removeListener((ItemClickListener) Tables.this);
                        }

                    }
                });
    }

    private Component createHeaderClickListenerCheckbox() {
        return super.createCheckboxAction("Header click listener", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        if (value) {
                            c.addListener((HeaderClickListener) Tables.this);
                        } else {
                            c.removeListener((HeaderClickListener) Tables.this);
                        }

                    }
                });
    }

    private Component createFooterClickListenerCheckbox() {
        return super.createCheckboxAction("Footer click listener", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        if (value) {
                            c.addListener((FooterClickListener) Tables.this);
                        } else {
                            c.removeListener((FooterClickListener) Tables.this);
                        }

                    }
                });
    }

    private Component createColumnResizeListenerCheckbox() {
        return super.createCheckboxAction("Column resize listener", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        if (value) {
                            c.addListener((ColumnResizeListener) Tables.this);
                        } else {
                            c.removeListener((ColumnResizeListener) Tables.this);
                        }

                    }
                });
    }

    // TODO:
    // Visible columns
    // Column headers
    // Column footers
    // Column icons
    // Column alignments
    // Column width
    // Column expand ratio
    // Column collapse
    // Column reordering allowed
    // Column collapsing allowed
    // setCurrentPageFirstItemIndex()
    // setColumnHeaderMode(int)
    // setRowHeaderMode(int)
    // Generated column
    // Cell style generator
    // Editable
    // Context menu

    // Cache rate
    // CurrentPageFirstItemId
    private Component createNullSelectCheckbox() {
        return super.createCheckboxAction("NullSelection", false,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        c.setNullSelectionAllowed(value);
                    }
                });
    }

    private Component createFooterVisibilityCheckbox() {
        return createCheckboxAction("Footer visible", true,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value) {
                        c.setFooterVisible(value);
                    }
                });
    }

    private Component createHeaderVisibilitySelect() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Explicit", Table.COLUMN_HEADER_MODE_EXPLICIT);
        options.put("Explicit defaults id",
                Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID);
        options.put("Id", Table.COLUMN_HEADER_MODE_ID);
        options.put("Hidden", Table.COLUMN_HEADER_MODE_HIDDEN);

        return createSelectAction("Header mode", options,
                "Explicit defaults id", new Command<Table, Integer>() {

                    public void execute(Table c, Integer value) {
                        c.setColumnHeaderMode(value);

                    }
                });
    }

    protected Component createWidthSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("Undefined", null);
        options.put("200px", "200px");
        options.put("500px", "500px");
        options.put("800px", "800px");

        return super.createSelectAction("Width", options, "Undefined",
                new Command<Table, String>() {

                    public void execute(Table t, String value) {
                        t.setWidth(value);
                    }
                });
    }

    protected Component createHeightSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("Undefined", null);
        options.put("200px", "200px");
        options.put("500px", "500px");
        options.put("800px", "800px");

        return super.createSelectAction("Height", options, "Undefined",
                new Command<Table, String>() {

                    public void execute(Table t, String value) {
                        t.setHeight(value);
                    }
                });
    }

    protected Component createPageLengthSelect() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("5", 5);
        options.put("10", 10);
        options.put("20", 20);
        options.put("50", 50);

        return super.createSelectAction("PageLength", options, "10",
                new Command<Table, Integer>() {

                    public void execute(Table t, Integer value) {
                        t.setPageLength(value);
                    }
                });
    }

    protected Component createItemsInContainerSelect() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("20", 20);
        options.put("100", 100);
        options.put("1000", 1000);
        options.put("10000", 10000);
        options.put("100000", 100000);

        return super.createSelectAction("Items in container", options, "20",
                new Command<Table, Integer>() {

                    public void execute(Table t, Integer value) {
                        t.setContainerDataSource(createContainer(t
                                .getContainerDataSource()
                                .getContainerPropertyIds().size(), value));
                    }
                });
    }

    protected Component createColumnsInContainerSelect() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("5", 5);
        options.put("10", 10);
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        return super.createSelectAction("Columns in container", options, "10",
                new Command<Table, Integer>() {

                    public void execute(Table t, Integer value) {
                        t.setContainerDataSource(createContainer(value, t
                                .getContainerDataSource().size()));
                    }
                });
    }

    private enum SelectMode {
        NONE, SINGLE, MULTI_SIMPLE, MULTI;
    }

    protected Component createSelectionModeSelect() {
        LinkedHashMap<String, SelectMode> options = new LinkedHashMap<String, SelectMode>();
        options.put("None", SelectMode.NONE);
        options.put("Single", SelectMode.SINGLE);
        options.put("Multi - simple", SelectMode.MULTI_SIMPLE);
        options.put("Multi - ctrl/shift", SelectMode.MULTI);

        return super.createSelectAction("Selection Mode", options,
                "Multi - ctrl/shift", new Command<Table, SelectMode>() {

                    public void execute(Table t, SelectMode value) {
                        switch (value) {
                        case NONE:
                            t.setSelectable(false);
                            break;
                        case SINGLE:
                            t.setMultiSelect(false);
                            t.setSelectable(true);
                            break;
                        case MULTI_SIMPLE:
                            t.setSelectable(true);
                            t.setMultiSelect(true);
                            t.setMultiSelectMode(MultiSelectMode.SIMPLE);
                            break;
                        case MULTI:
                            t.setSelectable(true);
                            t.setMultiSelect(true);
                            t.setMultiSelectMode(MultiSelectMode.DEFAULT);
                            break;
                        }
                    }
                });
    }

    public void columnResize(ColumnResizeEvent event) {
        log("ColumnResize on " + event.getPropertyId() + " from "
                + event.getPreviousWidth() + " to " + event.getCurrentWidth());
    }

    public void footerClick(FooterClickEvent event) {
        log("FooterClick on " + event.getPropertyId() + " using "
                + event.getButtonName());
    }

    public void headerClick(HeaderClickEvent event) {
        log("HeaderClick on " + event.getPropertyId() + " using "
                + event.getButtonName());
    }

    public void itemClick(ItemClickEvent event) {
        log("ItemClick on " + event.getPropertyId() + " using "
                + event.getButtonName());
    }
}
