package com.itmill.toolkit.demo.featurebrowser;

import java.util.HashMap;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Embedded;
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

    // Property IDs
    private static final Object PROPERTY_ID_CATEGORY = "Category";
    private static final Object PROPERTY_ID_NAME = "Name";
    private static final Object PROPERTY_ID_DESC = "Description";
    private static final Object PROPERTY_ID_CLASS = "Class";
    private static final Object PROPERTY_ID_VIEWED = "Viewed";

    // Global components
    private Tree tree;
    private Table table;
    private TabSheet ts;

    // Example "cache"
    private HashMap exampleInstances = new HashMap();

    // List of examples
    private static final Object[][] demos = new Object[][] {
    // Category, Name, Desc, Class, Viewed
            // Getting started: Labels
            { "Getting started", "Labels", "Some variations of Labels",
                    LabelExample.class },
            // Getting started: Buttons
            { "Getting started", "Buttons and links",
                    "Various Buttons and Links", ButtonExample.class },
            // Getting started: Fields
            { "Getting started", "Basic value input",
                    "TextFields, DateFields, and such", ValueInputExample.class },
            //
            { "Getting started", "RichText", "Rich text editing",
                    RichTextExample.class },
            // Getting started: Selects
            { "Getting started", "Choices, choices",
                    "Some variations of simple selects", SelectExample.class },
            // Layouts
            { "Getting started", "Layouts", "Laying out components",
                    LayoutExample.class },
            // Wrangling data: ComboBox
            { "Wrangling data", "ComboBox", "ComboBox - the swiss army select",
                    ComboBoxExample.class },
            // Wrangling data: Table
            { "Wrangling data", "Table",
                    "A dynamic Table with bells, whistles and actions",
                    TableExample.class },
            // Wrangling data: Tree
            { "Wrangling data", "Tree", "A hierarchy of things",
                    TreeExample.class },
            // Misc: Notifications
            { "Misc", "Notifications", "Notifications can improve usability",
                    NotificationExample.class },
            // Misc: Caching
            { "Misc", "Client caching", "Demonstrating of client-side caching",
                    ClientCachingExample.class },
            // Misc: Embedded
            { "Misc", "Embedding",
                    "Embedding resources - another site in this case",
                    EmbeddedBrowserExample.class },
            // Windowing
            { "Misc", "Windowing", "About windowing", WindowingExample.class },
    // END
    };

    public void init() {
        // Need to set a theme for ThemeResources to work
        setTheme("example");

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
        // Hide some columns
        table.setVisibleColumns(new Object[] { PROPERTY_ID_CATEGORY,
                PROPERTY_ID_NAME, PROPERTY_ID_DESC, PROPERTY_ID_VIEWED });
        table.addListener(this);
        table.setImmediate(true);
        split2.addComponent(table);

        ExpandLayout exp = new ExpandLayout();
        exp.setMargin(true);
        split2.addComponent(exp);

        OrderedLayout wbLayout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        Button b = new Button("Open in sub-window", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Component component = (Component) ts.getComponentIterator()
                        .next();
                String caption = ts.getTabCaption(component);
                try {
                    component = (Component) component.getClass().newInstance();
                } catch (Exception e) {
                    // Could not create
                    return;
                }
                Window w = new Window(caption);
                w.setWidth(640);
                if (Layout.class.isAssignableFrom(component.getClass())) {
                    w.setLayout((Layout) component);
                } else {
                    w.getLayout().setSizeFull();
                    w.addComponent(component);
                }
                getMainWindow().addWindow(w);
            }
        });
        b.setStyleName(Button.STYLE_LINK);
        wbLayout.addComponent(b);
        b = new Button("Open in native window", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Component component = (Component) ts.getComponentIterator()
                        .next();
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
                    if (Layout.class.isAssignableFrom(component.getClass())) {
                        w.setLayout((Layout) component);
                    } else {
                        w.getLayout().setSizeFull();
                        w.addComponent(component);
                    }
                    addWindow(w);
                }
                getMainWindow().open(new ExternalResource(w.getURL()), caption);
            }
        });
        b.setStyleName(Button.STYLE_LINK);
        wbLayout.addComponent(b);

        exp.addComponent(wbLayout);
        exp.setComponentAlignment(wbLayout, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_TOP);

        ts = new TabSheet();
        ts.setSizeFull();
        ts.addTab(new Label(""), "Choose example", null);
        exp.addComponent(ts);
        exp.expand(ts);

        Label status = new Label(
                "<a href=\"http://www.itmill.com/index_developers.htm\">Developer Area</a>"
                        + " | <a href=\"http://www.itmill.com/developers_documentation.htm\">Documentation</a>");
        status.setContentMode(Label.CONTENT_XHTML);
        exp.addComponent(status);
        exp.setComponentAlignment(status, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        // select initial section ("All")
        tree.setValue(rootId);

        getMainWindow()
                .showNotification(
                        "Welcome",
                        "Choose an example to begin.<br/><br/>And remember to experiment!",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
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
    }

    private HierarchicalContainer createContainer() {
        HierarchicalContainer c = new HierarchicalContainer();
        c.addContainerProperty(PROPERTY_ID_CATEGORY, String.class, null);
        c.addContainerProperty(PROPERTY_ID_NAME, String.class, "");
        c.addContainerProperty(PROPERTY_ID_DESC, String.class, "");
        c.addContainerProperty(PROPERTY_ID_CLASS, Class.class, null);
        c.addContainerProperty(PROPERTY_ID_VIEWED, Embedded.class, null);
        return c;
    }

    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty() == tree) {
            Object id = tree.getValue();
            Item item = tree.getItem(id);
            //
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
                // Example, not section
                // update table selection
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
                // update "viewed" state
                Property p = item.getItemProperty(PROPERTY_ID_VIEWED);
                if (p.getValue() == null) {
                    p.setValue(new Embedded("", new ThemeResource(
                            "icons/ok.png")));
                }
                table.requestRepaint();
            }
        }

    }

    private Component getComponent(Class componentClass) {
        if (!exampleInstances.containsKey(componentClass)) {
            try {
                Component c = (Component) componentClass.newInstance();
                exampleInstances.put(componentClass, c);
            } catch (Exception e) {
                return null;
            }
        }
        return (Component) exampleInstances.get(componentClass);
    }

}
