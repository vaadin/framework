package com.vaadin.tests.integration;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.LiferayTheme;

@SuppressWarnings("serial")
public class LiferayThemeDemo extends LegacyApplication {

    @SuppressWarnings("deprecation")
    private static final Date DATE = new Date(2009 - 1900, 6 - 1, 2);

    private static final Resource ICON_GLOBE = new ThemeResource(
            "../runo/icons/16/globe.png");
    private static final Resource ICON_OK = new ThemeResource(
            "../runo/icons/16/ok.png");

    private LegacyWindow main;
    private VerticalLayout mainLayout;
    private TabSheet tabs;

    private Action.Handler handler = new Action.Handler() {

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            // NOP
        }

        @Override
        public Action[] getActions(Object target, Object sender) {
            return new Action[] {
                    new Action("Open"),
                    new Action("Delete", new ThemeResource(
                            "../runo/icons/16/trash.png")) };
        }
    };

    @Override
    public void init() {
        main = new LegacyWindow("Vaadin Liferay Theme");
        mainLayout = (VerticalLayout) main.getContent();
        mainLayout.setMargin(false);
        setMainWindow(main);

        // setTheme("liferay");

        buildMainView();
    }

    void buildMainView() {
        mainLayout.setWidth("100%");
        mainLayout.setHeight("400px");
        mainLayout.addComponent(getTopMenu());

        CssLayout margin = new CssLayout();
        margin.setSizeFull();
        tabs = new TabSheet();
        tabs.setSizeFull();
        margin.addComponent(tabs);
        mainLayout.addComponent(margin);
        mainLayout.setExpandRatio(margin, 1);

        tabs.addComponent(buildLabels());
        tabs.addComponent(buildButtons());
        tabs.addComponent(buildTextFields());
        tabs.addComponent(buildSelects());
        tabs.addComponent(buildDateFields());
        tabs.addComponent(buildSliders());
        tabs.addComponent(buildTabSheets());
        tabs.addComponent(buildAccordions());
        tabs.addComponent(buildPanels());
        tabs.addComponent(buildTables());
        tabs.addComponent(buildTrees());
        tabs.addComponent(buildWindows());
        tabs.addComponent(buildSplitPanels());
        tabs.addComponent(buildNotifications());
        tabs.addComponent(buildPopupViews());
    }

    Layout buildLabels() {
        final GridLayout l = new GridLayout(2, 1);
        l.setWidth("560px");
        l.setSpacing(true);
        l.setMargin(true);
        l.setCaption("Labels");

        l.addComponent(new Label("Normal Label", ContentMode.HTML));
        l.addComponent(new Label(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
        return l;
    }

    Layout buildButtons() {
        GridLayout l = new GridLayout(3, 1);
        l.setCaption("Buttons");
        l.setMargin(true);
        l.setSpacing(true);

        AbstractComponent b = new Button("Normal Button");
        b.setDescription("This is a tooltip!");
        l.addComponent(b);

        b = new NativeButton("Native Button");
        b.setDescription("<h2><img src=\"/html/VAADIN/themes/runo/icons/16/globe.png\"/>A richtext tooltip</h2>"
                + "<ul>"
                + "<li>HTML formatting</li><li>Images<br/>"
                + "</li><li>etc...</li></ul>");
        l.addComponent(b);

        b = new CheckBox("Checkbox");
        l.addComponent(b);

        b = new Button("Disabled");
        b.setEnabled(false);
        l.addComponent(b);

        b = new NativeButton("Disabled");
        b.setEnabled(false);
        l.addComponent(b);

        b = new CheckBox("Disabled");
        b.setEnabled(false);
        l.addComponent(b);

        b = new Button("OK");
        b.setIcon(ICON_OK);
        l.addComponent(b);

        b = new NativeButton("OK");
        b.setIcon(ICON_OK);
        l.addComponent(b);

        b = new CheckBox("OK");
        b.setIcon(ICON_OK);
        l.addComponent(b);

        b = new Button("Link Button");
        b.setStyleName(LiferayTheme.BUTTON_LINK);
        l.addComponent(b);

        b = new NativeButton("Link Button");
        b.setStyleName(LiferayTheme.BUTTON_LINK);
        l.addComponent(b);

        l.newLine();

        b = new Button("Link Button");
        b.setIcon(ICON_OK);
        b.setStyleName(LiferayTheme.BUTTON_LINK);
        l.addComponent(b);

        b = new NativeButton("Link Button");
        b.setIcon(ICON_OK);
        b.setStyleName(LiferayTheme.BUTTON_LINK);
        l.addComponent(b);

        return l;
    }

    Layout buildTextFields() {
        GridLayout l = new GridLayout(2, 1);
        l.setCaption("Text fields");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("400px");
        l.setColumnExpandRatio(0, 1);

        l.addComponent(new Label("Normal TextField", ContentMode.HTML));
        TextField tf = new TextField();
        tf.setInputPrompt("Enter text");
        l.addComponent(tf);

        l.addComponent(new Label("Normal TextArea", ContentMode.HTML));

        TextArea ta = new TextArea();
        ta.setHeight("5em");
        ta.setInputPrompt("Enter text");
        l.addComponent(ta);

        return l;
    }

    Layout buildSelects() {
        VerticalLayout l = new VerticalLayout();
        l.setCaption("Selects");
        l.setMargin(true);
        l.setSpacing(true);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(new MarginInfo(true, false, false, false));
        l.addComponent(hl);

        AbstractSelect cb = new ComboBox();
        AbstractSelect nat = new NativeSelect();
        AbstractSelect list = new ListSelect();
        AbstractSelect twincol = new TwinColSelect();

        for (int i = 0; i < 50; i++) {
            cb.addItem("Item " + i);
            nat.addItem("Item " + i);
            list.addItem("Item " + i);
            twincol.addItem("Item " + i);
        }

        hl.addComponent(cb);
        hl.addComponent(nat);
        hl.addComponent(list);
        hl.addComponent(twincol);

        return l;
    }

    Layout buildDateFields() {
        VerticalLayout l = new VerticalLayout();
        l.setCaption("Date fields");
        l.setMargin(true);
        l.setSpacing(true);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setMargin(new MarginInfo(true, false, false, false));
        l.addComponent(hl);

        DateField df = new DateField();
        df.setValue(DATE);
        df.setResolution(DateField.RESOLUTION_MIN);
        hl.addComponent(df);

        df = new InlineDateField();
        df.setLocale(new Locale("fi", "FI"));
        df.setShowISOWeekNumbers(true);
        df.setValue(DATE);
        df.setResolution(DateField.RESOLUTION_DAY);
        hl.addComponent(df);

        df = new InlineDateField();
        df.setValue(DATE);
        df.setResolution(DateField.RESOLUTION_YEAR);
        hl.addComponent(df);

        return l;
    }

    Layout buildTabSheets() {
        VerticalLayout l = new VerticalLayout();
        l.setCaption("Tabs");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("400px");

        CheckBox closable = new CheckBox("Closable tabs");
        closable.setImmediate(true);
        l.addComponent(closable);

        final TabSheet ts = new TabSheet();
        ts.setHeight("100px");
        l.addComponent(ts);

        for (int i = 1; i < 10; i++) {
            Tab t = ts.addTab(new Label(), "Tab " + i);
            if (i % 2 == 0) {
                t.setIcon(ICON_GLOBE);
            }
            if (i == 2) {
                t.setEnabled(false);
            }
        }

        closable.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Iterator<Component> it = ts.getComponentIterator();
                for (; it.hasNext();) {
                    Component c = it.next();
                    ts.getTab(c).setClosable(
                            (Boolean) event.getProperty().getValue());
                }
            }
        });

        return l;
    }

    Layout buildPanels() {
        GridLayout l = new GridLayout(2, 1);
        l.setCaption("Panels");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("700px");
        l.setColumnExpandRatio(0, 2);
        l.setColumnExpandRatio(1, 5);

        l.addComponent(new Label("Normal Panel", ContentMode.HTML));

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel p = new Panel("Normal Panel", pl);
        p.setHeight("100px");
        pl.addComponent(new Label("Panel content"));
        l.addComponent(p);

        l.addComponent(new Label(
                "Light Style (<code>LiferayTheme.PANEL_LIGHT</code>)",
                ContentMode.HTML));

        VerticalLayout p2l = new VerticalLayout();
        p2l.setMargin(true);
        Panel p2 = new Panel("Light Style Panel", p2l);
        p2.setStyleName(LiferayTheme.PANEL_LIGHT);
        p2l.addComponent(new Label("Panel content"));
        l.addComponent(p2);

        return l;
    }

    Layout buildTables() {
        GridLayout l = new GridLayout(1, 1);
        l.setCaption("Tables");
        l.setMargin(true);
        l.setSpacing(true);

        Table t = new Table();
        t.setWidth("700px");
        t.setPageLength(4);
        t.setSelectable(true);
        t.setColumnCollapsingAllowed(true);
        t.setColumnReorderingAllowed(true);
        t.addActionHandler(handler);

        t.addContainerProperty("First", String.class, null, "First",
                ICON_GLOBE, Table.ALIGN_RIGHT);
        t.addContainerProperty("Second", String.class, null);
        t.addContainerProperty("Third", String.class, null);
        t.addContainerProperty("Fourth", TextField.class, null);
        t.setColumnCollapsed("Fourth", true);

        int sum = 0;
        for (int j = 0; j < 100; j++) {
            t.addItem(new Object[] { "" + j, "Bar value " + j,
                    "Last column value " + j, new TextField() }, j);
            sum += j;
        }

        t.setFooterVisible(true);
        t.setColumnFooter("First", "" + sum);

        l.addComponent(t);

        return l;
    }

    Layout buildWindows() {
        final CssLayout l = new CssLayout();
        l.setCaption("Windows");

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        final Window w = new Window("Normal window", layout);
        w.setWidth("280px");
        w.setHeight("180px");
        w.setPositionX(40);
        w.setPositionY(160);

        VerticalLayout layout2 = new VerticalLayout();
        layout2.setMargin(true);
        final Window w2 = new Window("Window, no resize", layout2);
        w2.setResizable(false);
        w2.setWidth("280px");
        w2.setHeight("180px");
        w2.setPositionX(350);
        w2.setPositionY(160);
        layout2.addComponent(new Label(
                "<code>Window.setResizable(false)</code>", ContentMode.HTML));

        tabs.addListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (event.getTabSheet().getSelectedTab() == l) {
                    getMainWindow().addWindow(w);
                    getMainWindow().addWindow(w2);
                } else {
                    getMainWindow().removeWindow(w);
                    getMainWindow().removeWindow(w2);
                }
            }
        });

        return l;
    }

    Layout buildSplitPanels() {
        final GridLayout l = new GridLayout(2, 1);
        l.setCaption("Split panels");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("700px");
        l.setHeight("100%");
        l.setColumnExpandRatio(1, 1);

        CheckBox lockCheckBox = new CheckBox("Lock SplitPanels");
        lockCheckBox.setImmediate(true);
        l.addComponent(lockCheckBox, 1, 0);
        l.newLine();

        Label label = new Label("Normal SplitPanel", ContentMode.HTML);
        label.setWidth(null);
        l.addComponent(label);
        final HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setWidth("100%");
        sp.setHeight("100px");
        final VerticalSplitPanel sp2 = new VerticalSplitPanel();
        sp2.setSizeFull();
        sp.setSecondComponent(sp2);
        l.addComponent(sp);

        label = new Label(
                "Small Style<br />(<code>LiferayTheme.SPLITPANEL_SMALL</code>)",
                ContentMode.HTML);
        label.setWidth(null);
        l.addComponent(label);

        final HorizontalSplitPanel sp3 = new HorizontalSplitPanel();
        sp3.setStyleName(LiferayTheme.SPLITPANEL_SMALL);
        sp3.setWidth("100%");
        sp3.setHeight("100px");
        final VerticalSplitPanel sp4 = new VerticalSplitPanel();
        sp4.setStyleName(LiferayTheme.SPLITPANEL_SMALL);
        sp4.setSizeFull();
        sp3.setSecondComponent(sp4);
        l.addComponent(sp3);

        lockCheckBox.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                sp.setLocked((Boolean) event.getProperty().getValue());
                sp2.setLocked((Boolean) event.getProperty().getValue());
                sp3.setLocked((Boolean) event.getProperty().getValue());
                sp4.setLocked((Boolean) event.getProperty().getValue());
            }
        });

        return l;
    }

    Layout buildAccordions() {
        final GridLayout l = new GridLayout(2, 1);
        l.setCaption("Accordions");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("700px");

        Accordion a = new Accordion();
        a.setWidth("100%");
        a.setHeight("170px");
        l.addComponent(a);

        for (int i = 1; i < 5; i++) {
            Tab t = a.addTab(new Label(), "Sheet " + i);
            if (i % 2 == 0) {
                t.setIcon(ICON_GLOBE);
            }
            if (i == 2) {
                t.setEnabled(false);
            }
        }

        return l;
    }

    Layout buildSliders() {
        final GridLayout l = new GridLayout(2, 1);
        l.setCaption("Sliders");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("400px");
        l.setColumnExpandRatio(0, 1);

        l.addComponent(new Label("Horizontal Slider", ContentMode.HTML));
        Slider s = new Slider();
        s.setWidth("200px");
        try {
            s.setValue(50.0);
        } catch (ValueOutOfBoundsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        l.addComponent(s);

        l.addComponent(new Label("Vertical Slider", ContentMode.HTML));
        s = new Slider();
        s.setOrientation(SliderOrientation.VERTICAL);
        s.setHeight("200px");
        try {
            s.setValue(50.0);
        } catch (ValueOutOfBoundsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        l.addComponent(s);

        return l;
    }

    Layout buildTrees() {
        final GridLayout l = new GridLayout(1, 1);
        l.setMargin(true);
        l.setCaption("Trees");

        Tree tree = new Tree();
        l.addComponent(tree);
        tree.addItem("Item 1");
        tree.setItemIcon("Item 1", ICON_GLOBE);
        tree.addItem("Child 1");
        tree.setItemIcon("Child 1", ICON_GLOBE);
        tree.setParent("Child 1", "Item 1");
        tree.addItem("Child 2");
        tree.setParent("Child 2", "Item 1");
        tree.addItem("Child 3");
        tree.setChildrenAllowed("Child 3", false);
        tree.setItemIcon("Child 3", ICON_GLOBE);
        tree.setParent("Child 3", "Item 1");
        tree.addItem("Child 4");
        tree.setChildrenAllowed("Child 4", false);
        tree.setParent("Child 4", "Item 1");
        tree.addItem("Item 2");
        tree.addItem("Item 3");
        tree.setItemIcon("Item 3", ICON_GLOBE);
        tree.setChildrenAllowed("Item 3", false);
        tree.addItem("Item 4");
        tree.setChildrenAllowed("Item 4", false);

        tree.addActionHandler(handler);

        return l;
    }

    Layout buildNotifications() {
        final GridLayout l = new GridLayout(2, 1);
        l.setCaption("Notifications");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("400px");
        l.setColumnExpandRatio(0, 1);

        final TextField title = new TextField("Notification caption");
        title.setValue("Brown Fox!");
        final TextField message = new TextField("Notification description");
        message.setValue("Jumped over the lazy dog.");
        message.setWidth("15em");

        l.addComponent(new Label("<h3>Type</h3>", ContentMode.HTML));
        l.addComponent(new Label("<h3>Preview</h3>", ContentMode.HTML));

        l.addComponent(new Label("Humanized", ContentMode.HTML));
        Button show = new Button("Humanized Notification",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification notification = new Notification(
                                title.getValue(), message.getValue());
                        notification.setHtmlContentAllowed(true);
                        notification.show(Page.getCurrent());
                    }
                });
        l.addComponent(show);

        l.addComponent(new Label("Warning", ContentMode.HTML));
        show = new Button("Warning Notification", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                new Notification(title.getValue(), message.getValue(),
                        Notification.TYPE_WARNING_MESSAGE, true).show(Page
                        .getCurrent());

            }
        });
        l.addComponent(show);

        l.addComponent(new Label("Error", ContentMode.HTML));
        show = new Button("Error Notification", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                new Notification(title.getValue(), message.getValue(),
                        Notification.TYPE_ERROR_MESSAGE, true).show(Page
                        .getCurrent());

            }
        });
        l.addComponent(show);

        l.addComponent(new Label("Tray", ContentMode.HTML));
        show = new Button("Tray Notification", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                new Notification(title.getValue(), message.getValue(),
                        Notification.TYPE_TRAY_NOTIFICATION, true).show(Page
                        .getCurrent());

            }
        });
        l.addComponent(show);

        l.addComponent(title);
        l.addComponent(message);

        return l;
    }

    Layout buildPopupViews() {
        final GridLayout l = new GridLayout(1, 1);
        l.setCaption("PopupViews");
        l.setMargin(true);
        l.setSpacing(true);
        l.setWidth("400px");

        Label content = new Label(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        content.setWidth("200px");

        PopupView pw = new PopupView("Click me!", content);
        l.addComponent(pw);

        return l;
    }

    MenuBar getTopMenu() {
        MenuBar menubar = new MenuBar();
        menubar.setWidth("100%");
        final MenuBar.MenuItem file = menubar.addItem("File", null);
        final MenuBar.MenuItem newItem = file.addItem("New", null);
        file.addItem("Open file...", new ThemeResource(
                "../runo/icons/16/folder.png"), null);
        file.addSeparator();

        newItem.addItem("File", null);
        newItem.addItem("Folder", null);
        newItem.addItem("Project...", null);

        file.addItem("Close", null);
        file.addItem("Close All", null);
        file.addSeparator();

        file.addItem("Save", null);
        file.addItem("Save As...", null);
        file.addItem("Save All", null);

        final MenuBar.MenuItem edit = menubar.addItem("Edit", null);
        edit.addItem("Undo", null);
        edit.addItem("Redo", null).setEnabled(false);
        edit.addSeparator();

        edit.addItem("Cut", null);
        edit.addItem("Copy", null);
        edit.addItem("Paste", null);
        edit.addSeparator();

        final MenuBar.MenuItem find = edit.addItem("Find/Replace", null);

        find.addItem("Google Search", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                getMainWindow().open(
                        new ExternalResource("http://www.google.com"));
            }
        });
        find.addSeparator();
        find.addItem("Find/Replace...", null);
        find.addItem("Find Next", null);
        find.addItem("Find Previous", null);

        final MenuBar.MenuItem view = menubar.addItem("View",
                new ThemeResource("../runo/icons/16/user.png"), null);
        MenuItem statusBarItem = view.addItem("Show/Hide Status Bar", null);
        statusBarItem.setCheckable(true);
        statusBarItem.setChecked(true);
        view.addItem("Customize Toolbar...", null);
        view.addSeparator();

        view.addItem("Actual Size", null);
        view.addItem("Zoom In", null);
        view.addItem("Zoom Out", null);

        menubar.addItem("Help", null).setEnabled(false);

        return menubar;
    }
}
