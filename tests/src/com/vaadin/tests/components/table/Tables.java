package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.Table.ColumnResizeListener;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.FooterClickListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;

public class Tables extends AbstractSelectTestCase<Table> implements
        ItemClickListener, HeaderClickListener, FooterClickListener,
        ColumnResizeListener {

    protected static final String CATEGORY_ROWS = "Rows";
    private static final String CATEGORY_HEADER = "Header";
    private static final String CATEGORY_FOOTER = "Footer";
    private static final String CATEGORY_VISIBLE_COLUMNS = "Visible columns";

    @Override
    protected Class<Table> getTestClass() {
        return Table.class;
    }

    /* COMMANDS */
    private Command<Table, Boolean> visibleColumnCommand = new Command<Table, Boolean>() {
        public void execute(Table c, Boolean visible, Object propertyId) {
            List<Object> visibleColumns = new ArrayList<Object>(Arrays.asList(c
                    .getVisibleColumns()));
            if (visible) {
                // Table should really check this... Completely fails without
                // the check (#
                if (!visibleColumns.contains(propertyId)) {
                    visibleColumns.add(propertyId);
                }
            } else {
                visibleColumns.remove(propertyId);
            }
            c.setVisibleColumns(visibleColumns.toArray());
        }
    };

    protected Command<Table, Boolean> columnResizeListenerCommand = new Command<Table, Boolean>() {

        public void execute(Table c, Boolean value, Object data) {
            if (value) {
                c.addListener((ColumnResizeListener) Tables.this);
            } else {
                c.removeListener((ColumnResizeListener) Tables.this);
            }
        }
    };

    protected Command<Table, Boolean> headerClickListenerCommand = new Command<Table, Boolean>() {

        public void execute(Table c, Boolean value, Object data) {
            if (value) {
                c.addListener((HeaderClickListener) Tables.this);
            } else {
                c.removeListener((HeaderClickListener) Tables.this);
            }
        }
    };

    protected Command<Table, Boolean> footerClickListenerCommand = new Command<Table, Boolean>() {

        public void execute(Table c, Boolean value, Object data) {
            if (value) {
                c.addListener((FooterClickListener) Tables.this);
            } else {
                c.removeListener((FooterClickListener) Tables.this);
            }
        }
    };

    protected Command<Table, Integer> rowHeaderModeCommand = new Command<Table, Integer>() {

        public void execute(Table c, Integer value, Object data) {
            if (value == Table.ROW_HEADER_MODE_PROPERTY) {
                c.setItemCaptionPropertyId("Column 3");
            }
            c.setRowHeaderMode(value);
        }
    };

    protected Command<Table, String> footerTextCommand = new Command<Table, String>() {

        public void execute(Table c, String value, Object data) {
            for (Object propertyId : c.getContainerPropertyIds()) {
                if (value != null) {
                    c.setColumnFooter(propertyId,
                            value.replace("{id}", propertyId.toString()));
                } else {
                    c.setColumnFooter(propertyId, null);
                }
            }
        }
    };

    public class Alignments {

    }

    protected Command<Table, Alignments> columnAlignmentCommand = new Command<Table, Alignments>() {

        public void execute(Table c, Alignments value, Object data) {
            // TODO
            // for (Object propertyId : c.getContainerPropertyIds()) {
            // }
        }
    };

    /* COMMANDS END */

    @Override
    protected void createActions() {
        super.createActions();

        createPageLengthSelect(CATEGORY_SIZE);

        createSelectionModeSelect(CATEGORY_SELECTION);

        createItemClickListener(CATEGORY_LISTENERS);
        createColumnResizeListenerCheckbox(CATEGORY_LISTENERS);
        createHeaderClickListenerCheckbox(CATEGORY_LISTENERS);
        createFooterClickListenerCheckbox(CATEGORY_LISTENERS);

        createRowHeaderModeSelect(CATEGORY_DATA_SOURCE);

        createHeaderVisibilitySelect(CATEGORY_HEADER);
        createHeaderTextCheckbox(CATEGORY_HEADER);

        createFooterVisibilityCheckbox(CATEGORY_FOOTER);
        createFooterTextSelect(CATEGORY_FOOTER);

        createColumnReorderingAllowedCheckbox(CATEGORY_FEATURES);
        createColumnCollapsingAllowedCheckbox(CATEGORY_FEATURES);

        createVisibleColumnsMultiToggle(CATEGORY_VISIBLE_COLUMNS);

    }

    private void createColumnReorderingAllowedCheckbox(String category) {
        createBooleanAction("Column reordering allowed", category, true,
                new Command<Table, Boolean>() {
                    public void execute(Table c, Boolean value, Object data) {
                        c.setColumnReorderingAllowed(value);
                    }
                });
    }

    private void createColumnCollapsingAllowedCheckbox(String category) {
        createBooleanAction("Column collapsing allowed", category, true,
                new Command<Table, Boolean>() {
                    public void execute(Table c, Boolean value, Object data) {
                        c.setColumnCollapsingAllowed(value);
                    }
                });
    }

    private void createVisibleColumnsMultiToggle(String category) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<String, Object>();
        for (Object id : getComponent().getContainerPropertyIds()) {
            options.put(id.toString(), id);
        }

        createMultiToggleAction("Visible columns", category, options,
                visibleColumnCommand, true);
    }

    private void createRowHeaderModeSelect(String category) {
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

        createSelectAction("Row header mode", category, options, "Hidden",
                rowHeaderModeCommand);
    }

    private void createFooterTextSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("None", null);
        options.put("Footer X", "Footer {id}");
        options.put("X", "{id}");

        createSelectAction("Texts in footer", category, options, "None",
                footerTextCommand);
    }

    private void createHeaderTextCheckbox(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("None", null);
        options.put("Col: {id}", "Col: {id}");
        options.put("Header {id} - every second", "Header {id}");

        createSelectAction("Texts in header", category, options, "None",
                new Command<Table, String>() {
                    public void execute(Table c, String value, Object data) {
                        int nr = 0;
                        for (Object propertyId : c.getContainerPropertyIds()) {
                            nr++;
                            if (value != null && value.equals("Header {id}")
                                    && nr % 2 == 0) {
                                c.setColumnHeader(propertyId, null);
                            } else if (value != null) {
                                c.setColumnHeader(
                                        propertyId,
                                        value.replace("{id}",
                                                propertyId.toString()));
                            } else {
                                c.setColumnHeader(propertyId, null);
                            }
                        }

                    }
                });
    }

    private void createHeaderClickListenerCheckbox(String category) {

        createBooleanAction("Header click listener", category, false,
                headerClickListenerCommand);
    }

    private void createFooterClickListenerCheckbox(String category) {

        createBooleanAction("Footer click listener", category, false,
                footerClickListenerCommand);
    }

    private void createColumnResizeListenerCheckbox(String category) {

        createBooleanAction("Column resize listener", category, false,
                columnResizeListenerCommand);
    }

    // TODO:
    // Visible columns
    // Column icons
    // Column alignments
    // Column width
    // Column expand ratio
    // Column collapse
    // setCurrentPageFirstItemIndex()
    // setColumnHeaderMode(int)
    // setRowHeaderMode(int)
    // Generated column
    // Cell style generator
    // Editable
    // Context menu

    // Cache rate
    // CurrentPageFirstItemId

    protected void createFooterVisibilityCheckbox(String category) {
        createBooleanAction("Footer visible", category, true,
                new Command<Table, Boolean>() {

                    public void execute(Table c, Boolean value, Object data) {
                        c.setFooterVisible(value);
                    }
                });
    }

    protected void createHeaderVisibilitySelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Explicit", Table.COLUMN_HEADER_MODE_EXPLICIT);
        options.put("Explicit defaults id",
                Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID);
        options.put("Id", Table.COLUMN_HEADER_MODE_ID);
        options.put("Hidden", Table.COLUMN_HEADER_MODE_HIDDEN);

        createSelectAction("Header mode", category, options,
                "Explicit defaults id", new Command<Table, Integer>() {

                    public void execute(Table c, Integer value, Object data) {
                        c.setColumnHeaderMode(value);

                    }
                });
    }

    protected void createPageLengthSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("5", 5);
        options.put("10", 10);
        options.put("20", 20);
        options.put("50", 50);

        createSelectAction("PageLength", category, options, "10",
                new Command<Table, Integer>() {

                    public void execute(Table t, Integer value, Object data) {
                        t.setPageLength(value);
                    }
                });
    }

    private enum SelectMode {
        NONE, SINGLE, MULTI_SIMPLE, MULTI;
    }

    protected void createSelectionModeSelect(String category) {
        LinkedHashMap<String, SelectMode> options = new LinkedHashMap<String, SelectMode>();
        options.put("None", SelectMode.NONE);
        options.put("Single", SelectMode.SINGLE);
        options.put("Multi - simple", SelectMode.MULTI_SIMPLE);
        options.put("Multi - ctrl/shift", SelectMode.MULTI);

        createSelectAction("Selection Mode", category, options,
                "Multi - ctrl/shift", new Command<Table, SelectMode>() {

                    public void execute(Table t, SelectMode value, Object data) {
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

}
