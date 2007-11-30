package com.itmill.toolkit.demo.featurebrowser;

import java.util.HashMap;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.ui.Window
 */
public class FeatureBrowser extends com.itmill.toolkit.Application implements
        Select.ValueChangeListener {

    private static final Object PROPERTY_ID_CATEGORY = "Category";
    private static final Object PROPERTY_ID_NAME = "Name";
    private static final Object PROPERTY_ID_DESC = "Description";
    private static final Object PROPERTY_ID_CLASS = "Class";
    private static final Object PROPERTY_ID_VIEWED = "Viewed";

    private Tree tree;
    private Table table;
    private TabSheet ts;

    private HashMap components = new HashMap();

    private static final Object[][] demos = new Object[][] {
    // Category, Name, Desc, Class, Viewed
            // Basic: Labels
            { "Basic", "Labels", "Some variations of Labels", Button.class,
                    Boolean.FALSE },
            // Basic: Buttons
            { "Basic", "Buttons and links",
                    "Some variations of Buttons and Links", Button.class,
                    Boolean.FALSE },
            // Basic: Fields
            { "Basic", "User input", "TextFields, DateFields, and such",
                    Button.class, Boolean.FALSE },
            //
            { "Basic", "RichText", "Rich text editing", RichTextExample.class,
                    Boolean.FALSE },
            // Basic: Selects
            { "Basic", "Choices, choices", "Some variations of simple selects",
                    Button.class, Boolean.FALSE },
            // Organizing: ComboBox
            { "Organizing", "ComboBox", "ComboBox - the swiss army select",
                    ComboBoxExample.class, Boolean.FALSE },
            // Organizing: Table
            { "Organizing", "Table", "A dynamic Table with bells and whistles",
                    Button.class, Boolean.FALSE },
            // Organizing: Tree
            { "Organizing", "Tree", "Some variations of Buttons and Links",
                    TreeExample.class, Boolean.FALSE },
            // Misc: Notifications
            { "Misc", "Notifications", "Notifications can improve usability",
                    NotificationExample.class, Boolean.FALSE },
            // Misc: Caching
            { "Misc", "Client caching", "A simple demo of client-side caching",
                    ClientCachingExample.class, Boolean.FALSE },
            // Misc: Embedded
            { "Misc", "Embedding",
                    "You can embed resources - another site in this case",
                    EmbeddedBrowserExample.class, Boolean.FALSE },
            // Windowing
            { "Misc", "Windowing", "About windowing", WindowingExample.class,
                    Boolean.FALSE },
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
        Object rootId = container.addItem();
        Item item = container.getItem(rootId);
        Property p = item.getItemProperty(PROPERTY_ID_NAME);
        p.setValue("All examples");
        for (int i = 0; i < demos.length; i++) {
            Object[] demo = demos[i];
            String section = (String) demo[0];
            Object sectionId;
            if (sectionIds.containsKey(section)) {
                sectionId = sectionIds.get(section);
            } else {
                sectionId = container.addItem();
                sectionIds.put(section, sectionId);
                container.setParent(sectionId, rootId);
                item = container.getItem(sectionId);
                p = item.getItemProperty(PROPERTY_ID_NAME);
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
        tree.expandItemsRecursively(rootId);

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
        OrderedLayout wbLayout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        wbLayout.addComponent(new Button("Open in popup window",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Component component = (Component) ts
                                .getComponentIterator().next();
                        String caption = ts.getTabCaption(component);
                        try {
                            component = (Component) component.getClass()
                                    .newInstance();
                        } catch (Exception e) {
                            // Could not create
                            return;
                        }
                        Window w = new Window(caption);
                        if (Layout.class.isAssignableFrom(component.getClass())) {
                            w.setLayout((Layout) component);
                        } else {
                            w.getLayout().setSizeFull();
                            w.addComponent(component);
                        }
                        getMainWindow().addWindow(w);
                    }
                }));
        wbLayout.addComponent(new Button("Open in native window",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Component component = (Component) ts
                                .getComponentIterator().next();
                        String caption = ts.getTabCaption(component);
                        Window w = getWindow(caption);
                        if (w == null) {
                            try {
                                component = (Component) component.getClass()
                                        .newInstance();
                            } catch (Exception e) {
                                // Could not create
                                return;
                            }
                            w = new Window(caption);
                            w.setName(caption);
                            if (Layout.class.isAssignableFrom(component
                                    .getClass())) {
                                w.setLayout((Layout) component);
                            } else {
                                w.getLayout().setSizeFull();
                                w.addComponent(component);
                            }
                            addWindow(w);
                        }
                        getMainWindow().open(new ExternalResource(w.getURL()),
                                caption);
                    }
                }));

        exp.addComponent(wbLayout);
        exp.setComponentAlignment(wbLayout, exp.ALIGNMENT_RIGHT,
                exp.ALIGNMENT_TOP);

        ts = new TabSheet();
        ts.setSizeFull();
        ts.addTab(new Label(
                "Choose demo (Only Notification and CliensSideCaching yet"),
                "Demo", null);
        exp.addComponent(ts);
        exp.expand(ts);

        Label status = new Label("Copyright IT Mill 2007");
        exp.addComponent(status);
        exp.setComponentAlignment(status, exp.ALIGNMENT_RIGHT,
                exp.ALIGNMENT_VERTICAL_CENTER);

        // select initial section ("All")
        tree.setValue(rootId);
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
        c.addContainerProperty(PROPERTY_ID_CLASS, Class.class, Button.class);
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
            if (tree.isRoot(id)) {
                section = ""; // show all sections
            } else if (tree.hasChildren(id)) {
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
            if (section != null) {
                c
                        .addContainerFilter(PROPERTY_ID_CATEGORY, section,
                                false, true);
            }
            if (!tree.hasChildren(id)) {
                table.setValue(id);
            }

        } else if (event.getProperty() == table) {
            if (table.getValue() != null) {
                table.removeListener(this);
                tree.setValue(table.getValue());
                table.addListener(this);
                Item item = table.getItem(table.getValue());
                Class c = (Class) item.getItemProperty(PROPERTY_ID_CLASS)
                        .getValue();
                Component component = getComponent(c);
                if (component != null) {
                    String caption = (String) item.getItemProperty(
                            PROPERTY_ID_NAME).getValue();
                    ts.removeAllComponents();
                    ts.addTab(component, caption, null);
                }
            }
        }

    }

    private Component getComponent(Class componentClass) {
        if (!components.containsKey(componentClass)) {
            try {
                Component c = (Component) componentClass.newInstance();
                components.put(componentClass, c);
            } catch (Exception e) {
                return null;
            }
        }
        return (Component) components.get(componentClass);
    }

    public class Dummy extends Label {
        public Dummy() {
            super("Dummy component");
        }
    }
}
