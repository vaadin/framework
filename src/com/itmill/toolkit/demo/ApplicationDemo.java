package com.itmill.toolkit.demo;

import java.util.HashMap;
import java.util.Iterator;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;

/**
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.ui.Window
 */
public class ApplicationDemo extends com.itmill.toolkit.Application implements
        Select.ValueChangeListener {

    private static final Object PROPERTY_ID_CATEGORY = "Category";
    private static final Object PROPERTY_ID_NAME = "Name";
    private static final Object PROPERTY_ID_DESC = "Description";
    private static final Object PROPERTY_ID_CLASS = "Class";
    private static final Object PROPERTY_ID_VIEWED = "Viewed";

    private Tree tree;
    private Table table;

    // category, name, desc, class, viewed
    private static final Object[][] demos = new Object[][] {
    // START
            // Intro
            { "Intro", "About", "About this demo", HelloWorld.class,
                    Boolean.FALSE },
            // Windowing
            { "Intro", "Windowing", "About windowing", HelloWorld.class,
                    Boolean.FALSE },
            // Basic: Labels
            { "Basic", "Labels", "Some variations of Labels", HelloWorld.class,
                    Boolean.FALSE },
            // Basic: Buttons
            { "Basic", "Buttons and links",
                    "Some variations of Buttons and Links", HelloWorld.class,
                    Boolean.FALSE },
            // Basic: Fields
            { "Basic", "User input", "TextFields, DateFields, and such",
                    HelloWorld.class, Boolean.FALSE },
            // Basic: Selects
            { "Basic", "Choices, choices", "Some variations of simple selects",
                    HelloWorld.class, Boolean.FALSE },
            // Organizing: ComboBox
            { "Organizing", "ComboBox", "ComboBox - the swiss army select",
                    HelloWorld.class, Boolean.FALSE },
            // Organizing: Table
            { "Organizing", "Table", "A dynamic Table with bells and whistles",
                    HelloWorld.class, Boolean.FALSE },
            // Organizing: Tree
            { "Organizing", "Tree", "Some variations of Buttons and Links",
                    HelloWorld.class, Boolean.FALSE },
            // Misc: Notifications
            { "Misc", "Notifications", "Notifications can improve usability",
                    HelloWorld.class, Boolean.FALSE },
            // Misc: Caching
            { "Misc", "Client caching", "A simple demo of client-side caching",
                    HelloWorld.class, Boolean.FALSE },
            // Misc: Embedded
            { "Misc", "Embedding",
                    "You can embed resources - another site in this case",
                    HelloWorld.class, Boolean.FALSE },
    // END
    };

    public void init() {

        // Create new window for the application and give the window a visible.
        Window main = new Window("IT Mill Toolkit 5");
        // set as main window
        setMainWindow(main);

        SplitPanel split = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        split.setSplitPosition(200, Sizeable.UNITS_PIXELS);
        main.setLayout(split);

        HashMap sectionIds = new HashMap();
        HierarchicalContainer container = createContainer();
        for (int i = 0; i < demos.length; i++) {
            Object[] demo = demos[i];
            String section = (String) demo[0];
            Object sectionId;
            if (sectionIds.containsKey(section)) {
                sectionId = sectionIds.get(section);
            } else {
                sectionId = container.addItem();
                sectionIds.put(section, sectionId);
                Item item = container.getItem(sectionId);
                Property p = item.getItemProperty(PROPERTY_ID_NAME);
                p.setValue(section);
            }
            Object id = container.addItem();
            container.setParent(id, sectionId);
            initItem(container.getItem(id), demo);

        }

        tree = new Tree();
        tree.setSelectable(true);
        tree.setMultiSelect(false);
        tree.setNullSelectionAllowed(false);
        tree.setContainerDataSource(container);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(PROPERTY_ID_NAME);
        tree.addListener(this);
        tree.setImmediate(true);
        for (Iterator it = sectionIds.values().iterator(); it.hasNext();) {
            // expand all sections
            tree.expandItemsRecursively(it.next());
        }
        split.addComponent(tree);

        SplitPanel split2 = new SplitPanel();
        split2.setSplitPosition(200, Sizeable.UNITS_PIXELS);
        split.addComponent(split2);

        table = new Table();
        table.setSizeFull();
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setNullSelectionAllowed(false);
        try {
            table.setContainerDataSource((IndexedContainer) container.clone());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        table.addListener(this);
        table.setImmediate(true);
        split2.addComponent(table);

        ExpandLayout exp = new ExpandLayout();
        exp.setMargin(true);
        split2.addComponent(exp);
        exp.addComponent(new Label("Short desc + open in window (+native)"));

        TabSheet ts = new TabSheet();
        ts.setSizeFull();
        ts.addTab(new Label("asd"), "Demo", null);
        ts.addTab(new Label("asd"), "Source", null);
        exp.addComponent(ts);
        exp.expand(ts);

        Label status = new Label("Copyright IT Mill 2007");
        exp.addComponent(status);
        exp.setComponentAlignment(status, exp.ALIGNMENT_RIGHT,
                exp.ALIGNMENT_VERTICAL_CENTER);

    }

    private void initItem(Item item, Object[] data) {
        int p = 0;
        Property prop = item.getItemProperty(PROPERTY_ID_CATEGORY);
        prop.setValue(data[p++]);
        prop = item.getItemProperty(PROPERTY_ID_NAME);
        prop.setValue(data[p++]);
        prop = item.getItemProperty(PROPERTY_ID_DESC);
        prop.setValue(data[p++]);
        prop = item.getItemProperty(PROPERTY_ID_CLASS);
        prop.setValue(data[p++]);
        prop = item.getItemProperty(PROPERTY_ID_VIEWED);
        prop.setValue(data[p++]);
    }

    private HierarchicalContainer createContainer() {
        HierarchicalContainer c = new HierarchicalContainer();
        c.addContainerProperty(PROPERTY_ID_CATEGORY, String.class, null);
        c.addContainerProperty(PROPERTY_ID_NAME, String.class, "");
        c.addContainerProperty(PROPERTY_ID_DESC, String.class, "");
        c
                .addContainerProperty(PROPERTY_ID_CLASS, Class.class,
                        HelloWorld.class);
        c
                .addContainerProperty(PROPERTY_ID_VIEWED, Boolean.class,
                        Boolean.FALSE);
        return c;
    }

    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty() == tree) {
            Object id = tree.getValue();
            Item item = tree.getItem(id);
            String section;
            if (tree.hasChildren(id)) {
                section = (String) item.getItemProperty(PROPERTY_ID_NAME)
                        .getValue();
            } else {
                section = (String) item.getItemProperty(PROPERTY_ID_CATEGORY)
                        .getValue();
            }

            table.setValue(null);
            IndexedContainer c = (IndexedContainer) table
                    .getContainerDataSource();
            c.removeAllContainerFilters();
            c.addContainerFilter(PROPERTY_ID_CATEGORY, section, false, true);
            if (!tree.hasChildren(id)) {
                table.setValue(id);
            }

        } else if (event.getProperty() == table) {
            if (table.getValue() != null) {
                table.removeListener(this);
                tree.setValue(table.getValue());
                table.addListener(this);
            }
        }

    }

}
