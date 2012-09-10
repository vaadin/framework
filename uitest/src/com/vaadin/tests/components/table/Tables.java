package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.Table.ColumnResizeListener;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.FooterClickListener;
import com.vaadin.ui.Table.GeneratedRow;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;
import com.vaadin.ui.Table.RowGenerator;
import com.vaadin.ui.Table.RowHeaderMode;

public class Tables<T extends Table> extends AbstractSelectTestCase<T>
        implements ItemClickListener, HeaderClickListener, FooterClickListener,
        ColumnResizeListener {

    protected static final String CATEGORY_ROWS = "Rows";
    private static final String CATEGORY_HEADER = "Header";
    private static final String CATEGORY_FOOTER = "Footer";
    private static final String CATEGORY_COLUMNS = "Columns";

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) Table.class;
    }

    /* COMMANDS */
    private Command<T, Align> columnAlignmentCommand = new Command<T, Align>() {

        @Override
        public void execute(T c, Align alignment, Object propertyId) {
            c.setColumnAlignment(propertyId, alignment);
        }

    };

    private Command<T, Boolean> columnVisibleCommand = new Command<T, Boolean>() {
        @Override
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

    private Command<T, Boolean> columnCollapsed = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean collapsed, Object propertyId) {
            c.setColumnCollapsed(propertyId, collapsed);

        }
    };

    private Command<T, Boolean> columnCollapsibleCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean collapsible, Object propertyId) {
            c.setColumnCollapsible(propertyId, collapsible);

        }
    };

    protected Command<T, Boolean> columnResizeListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(Table c, Boolean value, Object data) {
            if (value) {
                c.addListener((ColumnResizeListener) Tables.this);
            } else {
                c.removeListener((ColumnResizeListener) Tables.this);
            }
        }
    };

    protected Command<T, Boolean> headerClickListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((HeaderClickListener) Tables.this);
            } else {
                c.removeListener((HeaderClickListener) Tables.this);
            }
        }
    };

    protected Command<T, Boolean> footerClickListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(Table c, Boolean value, Object data) {
            if (value) {
                c.addListener((FooterClickListener) Tables.this);
            } else {
                c.removeListener((FooterClickListener) Tables.this);
            }
        }
    };

    protected Command<T, RowHeaderMode> rowHeaderModeCommand = new Command<T, RowHeaderMode>() {

        @Override
        public void execute(Table c, RowHeaderMode value, Object data) {
            if (value == RowHeaderMode.PROPERTY) {
                c.setItemCaptionPropertyId("Property 3");
            }
            c.setRowHeaderMode(value);
        }
    };

    protected Command<T, String> footerTextCommand = new Command<T, String>() {

        @Override
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

    protected Command<T, Object> alignColumnLeftCommand = new Command<T, Object>() {

        @Override
        public void execute(T c, Object propertyId, Object data) {
            c.setColumnAlignment(propertyId, (Align) data);
        }
    };

    private Command<T, ContextMenu> contextMenuCommand = new Command<T, ContextMenu>() {

        @Override
        public void execute(T c, final ContextMenu value, Object data) {
            c.removeAllActionHandlers();
            if (value != null) {
                c.addActionHandler(new Handler() {

                    @Override
                    public void handleAction(Action action, Object sender,
                            Object target) {
                        log("Action " + action.getCaption() + " performed on "
                                + target);
                    }

                    @Override
                    public Action[] getActions(Object target, Object sender) {
                        return value.getActions(target, sender);
                    }
                });
            }
        }
    };
    private Command<T, Integer> columnWidthCommand = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer width, Object propertyId) {
            c.setColumnWidth(propertyId, width);

        }
    };

    private Command<T, Resource> columnIconCommand = new Command<T, Resource>() {

        @Override
        public void execute(T c, Resource icon, Object propertyId) {
            c.setColumnIcon(propertyId, icon);

        }
    };
    private Command<T, ColumnHeaderMode> columnHeaderModeCommand = new Command<T, ColumnHeaderMode>() {

        @Override
        public void execute(T c, ColumnHeaderMode columnHeaderMode, Object data) {
            c.setColumnHeaderMode(columnHeaderMode);

        }
    };
    private Command<T, String> columnHeaderCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String header, Object propertyId) {
            c.setColumnHeader(propertyId, header);

        }
    };
    private Command<T, Float> columnExpandRatioCommand = new Command<T, Float>() {

        @Override
        public void execute(T c, Float expandRatio, Object propertyId) {
            c.setColumnExpandRatio(propertyId, expandRatio);
        }
    };

    private class GeneratedColumn {
        private Class<?> type;
        private String width;
        private boolean html;

        public GeneratedColumn(Class<?> type, String width, boolean html) {
            super();
            this.type = type;
            this.width = width;
            this.html = html;
        }
    }

    String generatedColumnId = "Generated ";
    int generatedColumnNextNr = 1;

    private Command<T, GeneratedColumn> addGeneratedColumnCommand = new Command<T, GeneratedColumn>() {

        @Override
        public void execute(T c, final GeneratedColumn col, Object data) {
            while (c.getColumnGenerator(generatedColumnId
                    + generatedColumnNextNr) != null) {
                generatedColumnNextNr++;
            }

            c.addGeneratedColumn(generatedColumnId + generatedColumnNextNr,
                    new ColumnGenerator() {

                        @Override
                        public Object generateCell(Table source, Object itemId,
                                Object columnId) {
                            String value = "";
                            if (col.html) {
                                value = "<i>" + itemId + "</i>" + "/" + "<b>"
                                        + columnId + "</b>";
                            } else {
                                value = itemId + "/" + columnId;
                            }
                            if (col.type == Button.class) {
                                Button b = new Button();
                                b.setCaption(value);
                                b.setWidth(col.width);
                                return b;
                            } else if (col.type == Label.class) {
                                Label l = new Label();
                                l.setWidth(col.width);
                                if (col.html) {
                                    l.setValue(value);
                                    l.setContentMode(ContentMode.HTML);
                                } else {
                                    l.setValue(value);
                                }
                                return l;
                            } else if (col.type == String.class) {
                                return value;
                            } else if (col.type == Object.class) {
                                return new Object();
                            }

                            return null;
                        }
                    });
            generatedColumnNextNr++;
            createColumnOptions(false);

        }
    };
    private Command<T, Object> removeGeneratedColumnsCommand = new Command<T, Object>() {

        @Override
        public void execute(T c, Object value, Object data) {
            for (int i = 0; i < generatedColumnNextNr; i++) {
                String columnId = generatedColumnId + i;
                if (c.getColumnGenerator(columnId) != null) {
                    c.removeGeneratedColumn(columnId);
                }

            }
            createColumnOptions(false);

        }
    };

    private class CellStyleInfo {
        private final String styleName;
        private final Object itemId;
        private final Object propertyId;

        public CellStyleInfo(String styleName, Object itemId, Object propertyId) {
            this.styleName = styleName;
            this.itemId = itemId;
            this.propertyId = propertyId;
        }

        public boolean appliesTo(Object itemId, Object propertyId) {
            return (this.itemId != null && this.itemId.equals(itemId))
                    && (this.propertyId == propertyId || (this.propertyId != null && this.propertyId
                            .equals(propertyId)));
        }
    }

    private Command<T, CellStyleInfo> cellStyleCommand = new Command<T, CellStyleInfo>() {

        @Override
        public void execute(T c, final CellStyleInfo cellStyleInfo, Object data) {
            if (cellStyleInfo == null) {
                c.setCellStyleGenerator(null);
            } else {
                c.setCellStyleGenerator(new CellStyleGenerator() {

                    @Override
                    public String getStyle(Table source, Object itemId,
                            Object propertyId) {
                        if (cellStyleInfo.appliesTo(itemId, propertyId)) {
                            return cellStyleInfo.styleName;
                        }
                        return null;
                    }
                });
            }
        }
    };

    private class GeneratedRowInfo {

        private final int nth;
        private final String[] text;
        private final boolean isHtml;

        public GeneratedRowInfo(int nth, boolean isHtml, String... text) {
            this.nth = nth;
            this.isHtml = isHtml;
            this.text = text;
        }

        public boolean appliesTo(Object itemId) {
            int ix = Integer.valueOf(itemId.toString().substring(5));
            return ix % nth == 0;
        }

        @Override
        public String toString() {
            return String.format("%d, %s, %s", nth, isHtml ? "true" : "false",
                    Arrays.toString(text));
        }
    }

    private Command<T, GeneratedRowInfo> rowGeneratorCommand = new Command<T, GeneratedRowInfo>() {

        @Override
        public void execute(T c, final GeneratedRowInfo generatedRowInfo,
                Object data) {
            if (generatedRowInfo == null) {
                c.setRowGenerator(null);
            } else {
                c.setRowGenerator(new RowGenerator() {

                    @Override
                    public GeneratedRow generateRow(Table table, Object itemId) {
                        if (generatedRowInfo.appliesTo(itemId)) {
                            GeneratedRow generatedRow = new GeneratedRow(
                                    generatedRowInfo.text);
                            generatedRow
                                    .setHtmlContentAllowed(generatedRowInfo.isHtml);
                            return generatedRow;
                        }
                        return null;
                    }
                });
            }
        }
    };

    private Command<T, Boolean> setSortEnabledCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setSortDisabled(!value);

        }
    };

    /* COMMANDS END */

    @Override
    protected void createActions() {
        super.createActions();

        createPageLengthSelect(CATEGORY_SIZE);

        createSelectionModeSelect(CATEGORY_SELECTION);
        createValueSelection(CATEGORY_SELECTION);

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

        createContextMenuAction(CATEGORY_FEATURES);

        createColumnHeaderMode(CATEGORY_FEATURES);
        createAddGeneratedColumnAction(CATEGORY_FEATURES);
        createCellStyleAction(CATEGORY_FEATURES);
        createGeneratedRowAction(CATEGORY_FEATURES);

        createBooleanAction("Sort enabled", CATEGORY_FEATURES, true,
                setSortEnabledCommand);
        createColumnOptions(true);
    }

    private void createAddGeneratedColumnAction(String categoryFeatures) {
        String category = "Generated columns";
        createCategory(category, categoryFeatures);
        createClickAction("Add Button", category, addGeneratedColumnCommand,
                new GeneratedColumn(Button.class, null, false));
        createClickAction("Add 200px wide Button", category,
                addGeneratedColumnCommand, new GeneratedColumn(Button.class,
                        "200px", false));
        createClickAction("Add 100% wide Button", category,
                addGeneratedColumnCommand, new GeneratedColumn(Button.class,
                        "100%", false));
        createClickAction("Add Label", category, addGeneratedColumnCommand,
                new GeneratedColumn(Label.class, null, false));
        createClickAction("Add 100px Label", category,
                addGeneratedColumnCommand, new GeneratedColumn(Label.class,
                        "100px", false));
        createClickAction("Add 100% wide Label", category,
                addGeneratedColumnCommand, new GeneratedColumn(Label.class,
                        "100%", false));

        createClickAction("Remove generated columns", category,
                removeGeneratedColumnsCommand, null);
        createClickAction("Add string as generated column", category,
                addGeneratedColumnCommand, new GeneratedColumn(String.class,
                        "", false));
        createClickAction("Add HTML string as generated column", category,
                addGeneratedColumnCommand, new GeneratedColumn(String.class,
                        "", true));
        createClickAction("Add 100px HTML Label", category,
                addGeneratedColumnCommand, new GeneratedColumn(Label.class,
                        "100px", true));
        createClickAction("Add Object as generated column", category,
                addGeneratedColumnCommand, new GeneratedColumn(Object.class,
                        "", false));
    }

    private void createCellStyleAction(String categoryFeatures) {
        LinkedHashMap<String, CellStyleInfo> options = new LinkedHashMap<String, CellStyleInfo>();
        options.put("None", null);
        options.put("Red row", new CellStyleInfo(
                "tables-test-cell-style-red-row", "Item 2", null));
        options.put("Red cell", new CellStyleInfo(
                "tables-test-cell-style-red-row", "Item 2", "Property 2"));
        createSelectAction("Cell style generator", categoryFeatures, options,
                "None", cellStyleCommand, true);
    }

    private void createGeneratedRowAction(String categoryFeatures) {
        LinkedHashMap<String, GeneratedRowInfo> options = new LinkedHashMap<String, GeneratedRowInfo>();
        options.put("None", null);
        options.put("Every fifth row, spanned", new GeneratedRowInfo(5, false,
                "foobarbaz this is a long one that should span."));
        int props = getComponent().getContainerPropertyIds().size();
        String[] text = new String[props];
        for (int ix = 0; ix < props; ix++) {
            text[ix] = "foo" + ix;
        }
        options.put("Every tenth row, no spanning", new GeneratedRowInfo(10,
                false, text));
        options.put(
                "Every eight row, spanned, html formatted",
                new GeneratedRowInfo(8, true,
                        "<b>foo</b> <i>bar</i> <span style='color:red;text-size:0.5em;'>baz</span>"));
        options.put("Every row, spanned", new GeneratedRowInfo(1, false,
                "spanned"));
        createSelectAction("Row generator", categoryFeatures, options, "None",
                rowGeneratorCommand, true);
    }

    private void createColumnHeaderMode(String category) {
        LinkedHashMap<String, ColumnHeaderMode> columnHeaderModeOptions = new LinkedHashMap<String, ColumnHeaderMode>();
        columnHeaderModeOptions.put("Hidden", ColumnHeaderMode.HIDDEN);
        columnHeaderModeOptions.put("Id", ColumnHeaderMode.ID);
        columnHeaderModeOptions.put("Explicit", ColumnHeaderMode.EXPLICIT);
        columnHeaderModeOptions.put("Explicit defaults id",
                ColumnHeaderMode.EXPLICIT_DEFAULTS_ID);

        createSelectAction("Column header mode", category,
                columnHeaderModeOptions, "Explicit defaults id",
                columnHeaderModeCommand);
    }

    private void createValueSelection(String categorySelection) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<String, Object>();
        options.put("null", null);
        for (int i = 1; i <= 10; i++) {
            options.put("Item " + i, "Item " + i);
        }
        createSelectAction("Value", categorySelection, options, null,
                setValueCommand);
    }

    private void createContextMenuAction(String category) {
        LinkedHashMap<String, ContextMenu> options = new LinkedHashMap<String, ContextMenu>();
        options.put("None", null);
        options.put("Item without icon", new ContextMenu("No icon", null));
        ContextMenu cm = new ContextMenu();
        cm.addItem("Caption only", null);
        cm.addItem("Has icon", ICON_16_USER_PNG_UNCACHEABLE);
        options.put("With and without icon", cm);
        options.put("Only one large icon", new ContextMenu("Icon",
                ICON_64_EMAIL_REPLY_PNG_UNCACHEABLE));
        options.put("Empty", new ContextMenu() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                return null;
            }
        });
        options.put("Edit/New", new ContextMenu() {
            @Override
            public Action[] getActions(Object itemId, Object component) {
                if (itemId == null) {
                    return new Action[] { new Action("New..."),
                            new Action("Common action") };
                } else {
                    return new Action[] { new Action("Edit " + itemId),
                            new Action("Common action") };
                }
            }
        });

        createSelectAction("Context menu", category, options, "None",
                contextMenuCommand, true);
    }

    private void createColumnReorderingAllowedCheckbox(String category) {
        createBooleanAction("Column reordering allowed", category, true,
                new Command<T, Boolean>() {
                    @Override
                    public void execute(Table c, Boolean value, Object data) {
                        c.setColumnReorderingAllowed(value);
                    }
                });
    }

    private void createColumnCollapsingAllowedCheckbox(String category) {
        createBooleanAction("Column collapsing allowed", category, true,
                new Command<T, Boolean>() {
                    @Override
                    public void execute(T c, Boolean value, Object data) {
                        c.setColumnCollapsingAllowed(value);
                    }
                });
    }

    private void createColumnOptions(boolean init) {
        if (!init && !hasCategory(CATEGORY_COLUMNS)) {
            return;
        }

        long start = System.currentTimeMillis();
        if (!init) {
            removeCategory(CATEGORY_COLUMNS);
        }

        for (Object id : getComponent().getContainerPropertyIds()) {
            String name = id.toString();
            createCategory(name, CATEGORY_COLUMNS);
            createColumnOption(name, id);
        }
        for (int i = 0; i < generatedColumnNextNr; i++) {
            String id = generatedColumnId + i;
            String name = id;
            if (getTestComponents().get(0).getColumnGenerator(id) != null) {
                createCategory(name, CATEGORY_COLUMNS);
                createColumnOption(name, id);
            }
        }

        long end = System.currentTimeMillis();
        System.err.println("Create options took " + (end - start) + "ms");
    }

    private class Timer {
        private long start, last;

        private Timer() {
            start = System.currentTimeMillis();
            last = System.currentTimeMillis();
        }

        public void log(String msg) {
            long now = System.currentTimeMillis();
            System.err.println("[This: " + (now - last) + "ms, total: "
                    + (now - start) + "ms]: " + msg);
            last = now;
        }
    }

    private void createColumnOption(String category, Object propertyId) {
        Timer t = new Timer();
        createBooleanAction("Visible", category, true, columnVisibleCommand,
                propertyId);
        t.log("Visible");
        createBooleanAction("Collapsed", category, false, columnCollapsed,
                propertyId);
        t.log("Collapsed");
        LinkedHashMap<String, Align> options = new LinkedHashMap<String, Align>();
        options.put("Left", Align.LEFT);
        options.put("Center", Align.CENTER);
        options.put("Right", Align.RIGHT);

        createSelectAction("Alignment", category, options, "Left",
                columnAlignmentCommand, propertyId);
        t.log("Alignment");
        LinkedHashMap<String, Integer> widthOptions = new LinkedHashMap<String, Integer>();
        widthOptions.put("- remove -", -1);
        for (int i : new int[] { 0, 1, 10, 100, 200, 400 }) {
            widthOptions.put(i + "px", i);
        }
        createSelectAction("Width", category, widthOptions, "- remove -",
                columnWidthCommand, propertyId);
        t.log("Width");

        LinkedHashMap<String, Resource> iconOptions = new LinkedHashMap<String, Resource>();
        iconOptions.put("- none -", null);
        iconOptions.put("ok 16x16", ICON_16_USER_PNG_CACHEABLE);
        iconOptions.put("help 16x16", ICON_16_HELP_PNG_CACHEABLE);
        iconOptions.put("folder 16x16", ICON_16_FOLDER_PNG_CACHEABLE);
        iconOptions.put("attention 32x32", ICON_32_ATTENTION_PNG_CACHEABLE);
        createSelectAction("Icon", category, iconOptions, "- none -",
                columnIconCommand, propertyId);

        t.log("Icon");
        LinkedHashMap<String, String> columnHeaderOptions = new LinkedHashMap<String, String>();
        columnHeaderOptions.put("- none -", null);
        columnHeaderOptions.put("A", "A");
        columnHeaderOptions.put("A nice column", "A nice column");

        createSelectAction("Column header", category, columnHeaderOptions,
                "- none -", columnHeaderCommand, propertyId);
        t.log("Header");
        LinkedHashMap<String, Float> expandOptions = new LinkedHashMap<String, Float>();
        expandOptions.put("- remove -", -1f);
        for (float i : new float[] { 0, 1, 2, 3, 4, 5 }) {
            expandOptions.put(i + "", i);
        }
        createSelectAction("Expand ratio", category, expandOptions,
                "- remove -", columnExpandRatioCommand, propertyId);
        t.log("Expand");
        createBooleanAction("Collapsible", category, true,
                columnCollapsibleCommand, propertyId);

        // Footer text (move)
        // Header text (move)

    }

    private void createRowHeaderModeSelect(String category) {
        LinkedHashMap<String, RowHeaderMode> options = new LinkedHashMap<String, RowHeaderMode>();
        options.put("Explicit", RowHeaderMode.EXPLICIT);
        options.put("Explicit defaults id", RowHeaderMode.EXPLICIT_DEFAULTS_ID);
        options.put("Hidden", RowHeaderMode.HIDDEN);
        options.put("Icon only", RowHeaderMode.ICON_ONLY);
        options.put("Id", RowHeaderMode.ID);
        options.put("Index", RowHeaderMode.INDEX);
        options.put("Item", RowHeaderMode.ITEM);
        options.put("'Property 3' property", RowHeaderMode.PROPERTY);

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
                new Command<T, String>() {
                    @Override
                    public void execute(T c, String value, Object data) {
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
    // setCurrentPageFirstItemIndex()
    // Editable
    // Cache rate
    // CurrentPageFirstItemId

    protected void createFooterVisibilityCheckbox(String category) {
        createBooleanAction("Footer visible", category, true,
                new Command<T, Boolean>() {

                    @Override
                    public void execute(T c, Boolean value, Object data) {
                        c.setFooterVisible(value);
                    }
                });
    }

    protected void createHeaderVisibilitySelect(String category) {
        LinkedHashMap<String, ColumnHeaderMode> options = new LinkedHashMap<String, ColumnHeaderMode>();
        options.put("Explicit", ColumnHeaderMode.EXPLICIT);
        options.put("Explicit defaults id",
                ColumnHeaderMode.EXPLICIT_DEFAULTS_ID);
        options.put("Id", ColumnHeaderMode.ID);
        options.put("Hidden", ColumnHeaderMode.HIDDEN);

        createSelectAction("Header mode", category, options,
                "Explicit defaults id", new Command<T, ColumnHeaderMode>() {

                    @Override
                    public void execute(T c, ColumnHeaderMode value, Object data) {
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
                new Command<T, Integer>() {

                    @Override
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
                "Multi - ctrl/shift", new Command<T, SelectMode>() {

                    @Override
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

    @Override
    public void columnResize(ColumnResizeEvent event) {
        log("ColumnResize on " + event.getPropertyId() + " from "
                + event.getPreviousWidth() + " to " + event.getCurrentWidth());
    }

    @Override
    public void footerClick(FooterClickEvent event) {
        log("FooterClick on " + event.getPropertyId() + " using "
                + event.getButtonName());
    }

    @Override
    public void headerClick(HeaderClickEvent event) {
        log("HeaderClick on " + event.getPropertyId() + " using "
                + event.getButtonName());
    }

    @Override
    protected void updateContainer() {
        super.updateContainer();

        // Recreate for the new properties
        createColumnOptions(false);

    }

}
