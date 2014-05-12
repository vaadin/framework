package com.vaadin.tests.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.AbstractColorPicker.PopupStyle;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

@Theme("valo")
@Title("Valo Theme Test")
public class ValoThemeTest extends UI implements Handler {

    CssLayout root = new CssLayout();
    CssLayout components = new CssLayout();
    CssLayout menu = new CssLayout();

    HashMap<Category, ArrayList<Link>> menuItems = new HashMap<Category, ArrayList<Link>>();

    enum Category {
        Common, Basic_Components, Component_Containers, Selection_Components, Inputs, Feedback, Other
    }

    Container tableData = generateContainer(200, false);

    static final String CAPTION_PROPERTY = "caption";
    static final String DESCRIPTION_PROPERTY = "description";
    static final String ICON_PROPERTY = "icon";
    static final String INDEX_PROPERTY = "index";

    @SuppressWarnings("unchecked")
    Container generateContainer(final int size, final boolean hierarchical) {
        IndexedContainer container = hierarchical ? new HierarchicalContainer()
                : new IndexedContainer();

        container.addContainerProperty(CAPTION_PROPERTY, String.class, null);
        container.addContainerProperty(ICON_PROPERTY, Resource.class, null);
        container.addContainerProperty(INDEX_PROPERTY, Integer.class, null);
        container
                .addContainerProperty(DESCRIPTION_PROPERTY, String.class, null);
        for (int i = 1; i < size + 1; i++) {
            Item item = container.addItem(i);
            item.getItemProperty(CAPTION_PROPERTY).setValue(nextString(true));
            item.getItemProperty(INDEX_PROPERTY).setValue(i);
            item.getItemProperty(DESCRIPTION_PROPERTY).setValue(
                    nextString(false));
            item.getItemProperty(ICON_PROPERTY).setValue(icon(false));
        }
        container.getItem(container.getIdByIndex(0))
                .getItemProperty(ICON_PROPERTY).setValue(icon(false));

        if (hierarchical) {
            for (int i = 1; i < size + 1; i++) {
                for (int j = 1; j < 5; j++) {
                    String id = i + " -> " + j;
                    Item child = container.addItem(id);
                    child.getItemProperty(CAPTION_PROPERTY).setValue(
                            getCaption());
                    ((Hierarchical) container).setChildrenAllowed(id, true);
                    ((Hierarchical) container).setParent(id, i);
                }
            }
        }
        return container;
    }

    @Override
    protected void init(VaadinRequest request) {
        // Show .v-app-loading badge
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        container = generateContainer(200, true);
        Collection<?> itemIds = container.getItemIds();
        firstItemId = itemIds.iterator().next();

        getPage().setTitle("Valo Theme Test");
        setContent(root);
        root.setWidth("100%");

        root.addComponent(commonParts());

        components.setWidth("100%");
        root.addComponent(components);

        for (int i = 1; i <= 1; i++) {
            components.addComponent(components(i));
        }
        components.addStyleName("components-root");

        // root.addComponentAsFirst(buildMenu());

    }

    CssLayout buildMenu() {
        menu.setPrimaryStyleName("valo-menu");

        Label title = new Label("Valo Theme");
        title.setSizeUndefined();
        title.setPrimaryStyleName("valo-menu-title");
        menu.addComponent(title);

        final CheckBox enabled = new CheckBox("Enabled", true);
        enabled.setPrimaryStyleName("valo-menu-enabled");
        menu.addComponent(enabled);
        enabled.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                components.setEnabled(enabled.getValue());
            }
        });

        for (Entry<Category, ArrayList<Link>> entry : menuItems.entrySet()) {
            Label caption = new Label(entry.getKey().toString()
                    .replaceAll("_", " "));
            caption.setSizeUndefined();
            caption.setPrimaryStyleName("valo-menu-category");
            menu.addComponent(caption);

            for (Link link : entry.getValue()) {
                menu.addComponent(link);
                link.setPrimaryStyleName("valo-menu-item");
            }
        }

        return menu;
    }

    VerticalLayout commonParts() {
        final VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.addStyleName("common-parts");

        /**
         * Loading incidator
         */
        HorizontalLayout row = addSection(
                root,
                "Loading Indicator",
                Category.Common,
                "You can test the loading indicator by pressing the buttons. The theme also provides a mixin that you can use to include a spinner anywhere in your application.");

        Button loading = new Button("Loading (800ms)…");
        loading.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                }
            }
        });
        row.addComponent(loading);

        Button delay = new Button("Task Delayed (3s)…");
        delay.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            }
        });
        row.addComponent(delay);

        Button wait = new Button("Please Wait (15s)…");
        wait.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                }
            }
        });
        row.addComponent(wait);

        /**
         * Notifications
         */

        row = addSection(root, "Notifications", Category.Common, null);

        final Notification notification = new Notification("Notification Title");

        final Notification notification2 = new Notification(
                "Notification Title");
        notification2
                .setDescription("A more informative message about what has happened. Nihil hic munitissimus habendi senatus locus, nihil horum? Inmensae subtilitatis, obscuris et malesuada fames. Hi omnes lingua, institutis, legibus inter se differunt.");

        CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);
        Button notify = new Button("Humanized", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification.setPosition(Position.MIDDLE_CENTER);
                notification.setDelayMsec(-1);
                notification.setStyleName("humanized");
                notification.show(getPage());
            }
        });
        group.addComponent(notify);

        notify = new Button("+ description", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification2.setPosition(Position.TOP_LEFT);
                notification2.setDelayMsec(-1);
                notification2.setStyleName("humanized closable");
                notification2.show(getPage());
            }
        });
        group.addComponent(notify);

        group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);
        notify = new Button("Tray", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification.setPosition(Position.TOP_RIGHT);
                notification.setDelayMsec(-1);
                notification.setStyleName("tray");
                notification.show(getPage());
            }
        });
        group.addComponent(notify);

        notify = new Button("+ description", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification2.setPosition(Position.BOTTOM_RIGHT);
                notification2.setDelayMsec(-1);
                notification2.setStyleName("tray closable");
                notification2.show(getPage());
            }
        });
        group.addComponent(notify);

        group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);
        notify = new Button("Warning", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification.setPosition(Position.MIDDLE_CENTER);
                notification.setDelayMsec(1500);
                notification.setStyleName("warning");
                notification.show(getPage());
            }
        });
        group.addComponent(notify);

        notify = new Button("+ description", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification2.setPosition(Position.TOP_CENTER);
                notification2.setDelayMsec(-1);
                notification2.setStyleName("warning closable");
                notification2.show(getPage());
            }
        });
        group.addComponent(notify);

        notify = new Button("No caption", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification n3 = new Notification(null,
                        "Just a simple description for this warning message.");
                n3.setPosition(Position.TOP_CENTER);
                n3.setDelayMsec(-1);
                n3.setStyleName("warning closable");
                n3.show(getPage());
            }
        });
        group.addComponent(notify);

        group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);
        notify = new Button("Error", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification.setPosition(Position.MIDDLE_CENTER);
                notification.setDelayMsec(2000);
                notification.setStyleName("error");
                notification.show(getPage());
            }
        });
        group.addComponent(notify);

        notify = new Button("+ description", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                notification2.setPosition(Position.BOTTOM_CENTER);
                notification2.setDelayMsec(-1);
                notification2.setStyleName("error closable");
                notification2.show(getPage());
            }
        });
        group.addComponent(notify);

        notify = new Button("System Message", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification n = new Notification(
                        "Session Expired",
                        "A really long description this time, to test how well this will wrap to new lines. Morbi fringilla convallis sapien, id pulvinar odio volutpat. Sed haec quis possit intrepidus aestimare tellus.");
                n.setDelayMsec(-1);
                n.setPosition(Position.TOP_LEFT);
                n.setStyleName("system closable");
                n.show(getPage());
            }
        });
        row.addComponent(notify);

        /**
         * Windows
         */
        row = addSection(root, "Windows", Category.Component_Containers, null);

        final Window win = new Window("Window Caption");
        win.setWidth("320px");
        win.setContent(windowContents(false));

        group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);

        Button button = new Button("Fixed footer", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                win.close();
                win.setContent(windowContents(true));
                win.setHeight("240px");
                win.setResizable(true);
                UI.getCurrent().addWindow(win);
                win.setPositionX(Page.getCurrent().getBrowserWindowWidth() / 3);
                win.setPositionY(Page.getCurrent().getBrowserWindowHeight() / 3);
            }
        });
        group.addComponent(button);

        button = new Button("Auto height", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                win.close();
                win.setContent(windowContents(false));
                win.setHeight(null);
                win.setResizable(false);
                UI.getCurrent().addWindow(win);
                win.setPositionX(Page.getCurrent().getBrowserWindowWidth() / 3);
                win.setPositionY(Page.getCurrent().getBrowserWindowHeight() / 3);
            }
        });
        group.addComponent(button);

        group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);

        button = new Button("Toggle caption", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (win.getCaption().equals("")) {
                    win.setCaption("Window Caption");
                } else {
                    win.setCaption("");
                }
            }
        });
        group.addComponent(button);

        button = new Button("Toggle close", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                win.setClosable(!win.isClosable());
            }
        });
        group.addComponent(button);

        button = new Button("Toggle maximize", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                win.setResizable(!win.isResizable());
            }
        });
        group.addComponent(button);

        /**
         * Tooltips
         */
        row = addSection(root, "Tooltips", Category.Common, null);

        Label label = new Label("Simple");
        label.setDescription("Simple tooltip message");
        row.addComponent(label);

        label = new Label("Long");
        label.setDescription("Long tooltip message. Inmensae subtilitatis, obscuris et malesuada fames. Salutantibus vitae elit libero, a pharetra augue.");
        row.addComponent(label);

        label = new Label("HTML");
        label.setDescription("<div><h1>Ut enim ad minim veniam, quis nostrud exercitation</h1><p><span>Morbi fringilla convallis sapien, id pulvinar odio volutpat.</span> <span>Vivamus sagittis lacus vel augue laoreet rutrum faucibus.</span> <span>Donec sed odio operae, eu vulputate felis rhoncus.</span> <span>At nos hinc posthac, sitientis piros Afros.</span> <span>Tu quoque, Brute, fili mi, nihil timor populi, nihil!</span></p><p><span>Gallia est omnis divisa in partes tres, quarum.</span> <span>Praeterea iter est quasdam res quas ex communi.</span> <span>Cum ceteris in veneratione tui montes, nascetur mus.</span> <span>Quam temere in vitiis, legem sancimus haerentia.</span> <span>Idque Caesaris facere voluntate liceret: sese habere.</span></p></div>");
        row.addComponent(label);

        label = new Label("w/ Error message");
        label.setDescription("Simple tooltip message");
        label.setComponentError(new UserError("Something terrible has happened"));
        row.addComponent(label);

        label = new Label("w/ Long error message");
        label.setDescription("Simple tooltip message");
        label.setComponentError(new UserError(
                "<h2>Contra legem facit qui id facit quod lex prohibet <span>Tityre, tu patulae recubans sub tegmine fagi  dolor.</span> <span>Tityre, tu patulae recubans sub tegmine fagi  dolor.</span> <span>Prima luce, cum quibus mons aliud  consensu ab eo.</span> <span>Quid securi etiam tamquam eu fugiat nulla pariatur.</span> <span>Fabio vel iudice vincam, sunt in culpa qui officia.</span> <span>Nihil hic munitissimus habendi senatus locus, nihil horum?</span></p><p><span>Plura mihi bona sunt, inclinet, amari petere vellent.</span> <span>Integer legentibus erat a ante historiarum dapibus.</span> <span>Quam diu etiam furor iste tuus nos eludet?</span> <span>Nec dubitamus multa iter quae et nos invenerat.</span> <span>Quisque ut dolor gravida, placerat libero vel, euismod.</span> <span>Quae vero auctorem tractata ab fiducia dicuntur.</span></h2>",
                AbstractErrorMessage.ContentMode.HTML, ErrorLevel.CRITICAL));
        row.addComponent(label);

        label = new Label("Error message only");
        label.setComponentError(new UserError("Something terrible has happened"));
        row.addComponent(label);

        return root;
    }

    VerticalLayout components(int num) {
        menuItems.clear();

        final VerticalLayout root = new VerticalLayout();
        root.setWidth("33.333%");
        root.setMargin(true);
        root.addStyleName("components");
        root.addStyleName("color-context" + num);

        labels(root);
        buttonsAndLinks(root);
        textfields(root);
        textareas(root);
        comboboxes(root);
        menubars(root);
        checkboxes(root);
        optiongroups(root);
        datefields(root);
        panels(root);
        trees(root);
        tables(root);
        treetables(root);
        sliders(root);
        splitpanels(root);
        tabsheets(root);
        colorpickers(root);
        accordions(root);
        nativeselects(root);

        return root;
    }

    void nativeselects(VerticalLayout root) {
        HorizontalLayout row = addSection(root, "Native Selects",
                Category.Selection_Components, null);

        NativeSelect select = new NativeSelect("Drop Down Select");
        row.addComponent(select);

        ListSelect list = new ListSelect("List Select");
        list.setNewItemsAllowed(true);
        row.addComponent(list);

        TwinColSelect tcs = new TwinColSelect("TwinCol Select");
        tcs.setLeftColumnCaption("Left Column");
        tcs.setRightColumnCaption("Right Column");
        tcs.setNewItemsAllowed(true);
        row.addComponent(tcs);

        TwinColSelect tcs2 = new TwinColSelect("Sized TwinCol Select");
        tcs2.setLeftColumnCaption("Left Column");
        tcs2.setRightColumnCaption("Right Column");
        tcs2.setNewItemsAllowed(true);
        tcs2.setWidth("280px");
        tcs2.setHeight("200px");
        row.addComponent(tcs2);

        for (int i = 1; i <= 10; i++) {
            select.addItem("Option " + i);
            list.addItem("Option " + i);
            tcs.addItem("Option " + i);
            tcs2.addItem("Option " + i);
        }
    }

    void accordions(VerticalLayout root) {
        HorizontalLayout row = addSection(root, "Accordions",
                Category.Component_Containers, null);
        row.setWidth("100%");

        Accordion ac = new Accordion();
        row.addComponent(ac);
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                addComponent(new Label(
                        "Fabio vel iudice vincam, sunt in culpa qui officia. Ut enim ad minim veniam, quis nostrud exercitation."));
            }
        }, "First Caption", icon(false));
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                addComponent(new Label(
                        "Gallia est omnis divisa in partes tres, quarum."));
            }
        }, "Second Caption", icon(false));
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                addComponent(new Label(
                        "Nihil hic munitissimus habendi senatus locus, nihil horum? Sed haec quis possit intrepidus aestimare tellus."));
            }
        }, "Third Caption", icon(false));
        ac.addTab(new VerticalLayout() {
            {
                setMargin(true);
                addComponent(new Label(
                        "Inmensae subtilitatis, obscuris et malesuada fames. Quisque ut dolor gravida, placerat libero vel, euismod."));
            }
        }, "Custom Caption Style", icon(false)).setStyleName("color1");
    }

    void tabsheets(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Tabs", Category.Component_Containers, null);
        row.setWidth("100%");

        final VerticalLayout wrap = new VerticalLayout();
        wrap.setSpacing(true);
        row.addComponent(wrap);

        final CheckBox closable = new CheckBox("Closable");
        closable.setImmediate(true);
        wrap.addComponent(closable);

        final CheckBox overflow = new CheckBox("Overflow");
        overflow.setImmediate(true);
        wrap.addComponent(overflow);

        final CheckBox icon = new CheckBox("Icons");
        icon.setImmediate(true);
        wrap.addComponent(icon);

        ValueChangeListener update = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                wrap.removeAllComponents();

                wrap.addComponents(closable, overflow, icon);

                wrap.addComponent(getTabSheet("Normal", null,
                        closable.getValue(), overflow.getValue(),
                        icon.getValue()));
                wrap.addComponent(getTabSheet("Centered tabs", "centered-tabs",
                        closable.getValue(), overflow.getValue(),
                        icon.getValue()));
                wrap.addComponent(getTabSheet("Equal-width tabs",
                        "equal-width-tabs", closable.getValue(),
                        overflow.getValue(), icon.getValue()));
                wrap.addComponent(getTabSheet("Icons on top + padded tabbar",
                        "icons-on-top padded-tabbar", closable.getValue(),
                        overflow.getValue(), icon.getValue()));
                wrap.addComponent(getTabSheet("Only selected tab is closable",
                        "selected-closable", closable.getValue(),
                        overflow.getValue(), icon.getValue()));
            }
        };
        closable.addValueChangeListener(update);
        overflow.addValueChangeListener(update);
        icon.addValueChangeListener(update);

        // Generate initial view
        icon.setValue(true);
    }

    void splitpanels(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Split Panels", Category.Component_Containers,
                null);

        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setWidth("200px");
        sp.setHeight(null);
        sp.setFirstComponent(new Label(
                "Fictum,  deserunt mollit anim laborum astutumque!"));
        sp.setSecondComponent(new Label(
                "Non equidem invideo, miror magis posuere velit aliquet."));
        row.addComponent(sp);

        VerticalSplitPanel sp2 = new VerticalSplitPanel();
        sp2.setWidth("200px");
        sp2.setHeight("6em");
        sp2.setFirstComponent(new Label(
                "Fictum,  deserunt mollit anim laborum astutumque!"));
        sp2.setSecondComponent(new Label(
                "Non equidem invideo, miror magis posuere velit aliquet."));
        row.addComponent(sp2);

        sp = new HorizontalSplitPanel();
        sp.setWidth("200px");
        sp.setHeight("200px");
        sp.addStyleName("small");
        sp.setFirstComponent(new Label(
                "Fictum,  deserunt mollit anim laborum astutumque!"));
        sp.setSecondComponent(new Label(
                "Non equidem invideo, miror magis posuere velit aliquet."));
        row.addComponent(sp);

        sp2 = new VerticalSplitPanel();
        sp2.setWidth("200px");
        sp2.setHeight("6em");
        sp2.addStyleName("small");
        sp2.setFirstComponent(new Label(
                "Fictum,  deserunt mollit anim laborum astutumque!"));
        sp2.setSecondComponent(new Label(
                "Non equidem invideo, miror magis posuere velit aliquet."));
        row.addComponent(sp2);
    }

    void sliders(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Sliders", Category.Basic_Components, null);

        Slider slider = new Slider("Horizontal");
        slider.setValue(50.0);
        row.addComponent(slider);

        slider = new Slider("Horizontal, sized");
        slider.setValue(50.0);
        slider.setWidth("200px");
        row.addComponent(slider);

        slider = new Slider("Vertical");
        slider.setValue(50.0);
        slider.setOrientation(SliderOrientation.VERTICAL);
        row.addComponent(slider);

        slider = new Slider("Vertical, sized");
        slider.setValue(50.0);
        slider.setOrientation(SliderOrientation.VERTICAL);
        slider.setHeight("200px");
        row.addComponent(slider);

        slider = new Slider("Disabled");
        slider.setValue(50.0);
        slider.setEnabled(false);
        row.addComponent(slider);
    }

    void tables(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Tables", Category.Selection_Components, null);
        row.setWidth("100%");

        VerticalLayout wrap = new VerticalLayout();
        wrap.setSpacing(true);
        row.addComponent(wrap);

        Table table = getTable("Normal");
        wrap.addComponent(table);

        table = getTable("Sized & Footer");
        table.setWidth("100%");
        table.setColumnExpandRatio("Lorem", 1.0f);
        table.setColumnExpandRatio("Foo", 1.0f);
        table.setColumnExpandRatio("Bar", 1.0f);
        table.setFooterVisible(true);
        table.setColumnFooter("Lorem", "lorem");
        table.setColumnFooter("Foo", "foo");
        table.setColumnFooter("Bar", "bar");
        wrap.addComponent(table);
    }

    void treetables(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "TreeTables", Category.Selection_Components,
                null);
        row.setWidth("100%");

        VerticalLayout wrap = new VerticalLayout();
        wrap.setSpacing(true);
        row.addComponent(wrap);

        TreeTable table = getTreeTable("Normal");
        wrap.addComponent(table);
    }

    void trees(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Tree", Category.Selection_Components, null);
        Tree tree = new Tree();
        tree.setSelectable(true);
        tree.setMultiSelect(true);
        tree.setContainerDataSource(generateContainer(10, true));
        tree.setDragMode(TreeDragMode.NODE);
        row.addComponent(tree);
        tree.setItemCaptionPropertyId(CAPTION_PROPERTY);
        tree.setItemIconPropertyId(ICON_PROPERTY);
        tree.expandItem(firstItemId);
        // TODO fix the issue that tree selection focuses the component (and
        // then scrolls to that component)
        // tree.select(firstItemId);
        tree.setItemIcon(firstItemId, icon(false));

        tree.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Notification.show(event.getTransferable().toString());
            }
        });

        // Add actions (context menu)
        tree.addActionHandler(this);
    }

    void panels(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Panels & Layout panels",
                Category.Component_Containers, null);

        Panel panel = new Panel("Normal");
        panel.setIcon(icon(false));
        panel.setContent(panelContent());
        row.addComponent(panel);

        panel = new Panel("Sized");
        panel.setIcon(icon(false));
        panel.setWidth("10em");
        panel.setHeight("250px");
        panel.setContent(panelContent());
        row.addComponent(panel);

        panel = new Panel("Custom Caption");
        panel.setIcon(icon(false));
        panel.addStyleName("color1");
        panel.setContent(panelContent());
        row.addComponent(panel);

        panel = new Panel("Custom Caption");
        panel.setIcon(icon(false));
        panel.addStyleName("color2");
        panel.setContent(panelContent());
        row.addComponent(panel);

        panel = new Panel("Custom Caption");
        panel.setIcon(icon(false));
        panel.addStyleName("color3");
        panel.setContent(panelContent());
        row.addComponent(panel);

        panel = new Panel("Borderless style");
        panel.setIcon(icon(false));
        panel.addStyleName("borderless");
        panel.setContent(panelContent());
        row.addComponent(panel);

        panel = new Panel("Well style");
        panel.setIcon(icon(false));
        panel.addStyleName("well");
        panel.setContent(panelContent());
        row.addComponent(panel);

        CssLayout layout = new CssLayout();
        layout.setIcon(icon(false));
        layout.setCaption("Panel style layout");
        layout.addStyleName("card");
        layout.addComponent(panelContent());
        row.addComponent(layout);

        layout = new CssLayout();
        layout.setIcon(icon(false));
        layout.setCaption("Well style layout");
        layout.addStyleName("well");
        layout.addComponent(panelContent());
        row.addComponent(layout);
    }

    void datefields(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Date Fields", Category.Inputs, null);

        DateField date = new DateField("Default resolution");
        date.setValue(new Date());
        row.addComponent(date);

        date = new DateField("Default resolution, explicit size");
        date.setValue(new Date());
        row.addComponent(date);
        date.setWidth("260px");
        date.setHeight("60px");

        date = new DateField("Second resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.SECOND);
        row.addComponent(date);

        date = new DateField("Minute resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.MINUTE);
        row.addComponent(date);

        date = new DateField("Hour resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.HOUR);
        row.addComponent(date);

        date = new DateField("Disabled");
        date.setValue(new Date());
        date.setResolution(Resolution.HOUR);
        date.setEnabled(false);
        row.addComponent(date);

        date = new DateField("Day resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        row.addComponent(date);

        date = new DateField("Month resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.MONTH);
        row.addComponent(date);

        date = new DateField("Year resolution");
        date.setValue(new Date());
        date.setResolution(Resolution.YEAR);
        row.addComponent(date);

        date = new DateField("Custom color");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("color1");
        row.addComponent(date);

        date = new DateField("Custom color");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("color2");
        row.addComponent(date);

        date = new DateField("Custom color");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("color3");
        row.addComponent(date);

        date = new DateField("Small");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("small");
        row.addComponent(date);

        date = new DateField("Large");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.addStyleName("large");
        row.addComponent(date);

        date = new DateField("Week numbers");
        date.setValue(new Date());
        date.setResolution(Resolution.DAY);
        date.setLocale(new Locale("fi", "fi"));
        date.setShowISOWeekNumbers(true);
        row.addComponent(date);

        date = new DateField("US locale");
        date.setValue(new Date());
        date.setResolution(Resolution.SECOND);
        date.setLocale(new Locale("en", "US"));
        row.addComponent(date);

        date = new DateField("Custom format");
        date.setValue(new Date());
        date.setDateFormat("E dd/MM/yyyy");
        row.addComponent(date);
    }

    void optiongroups(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Option Groups", Category.Selection_Components,
                null);

        OptionGroup options = new OptionGroup("Choose one, explicit width");
        options.setWidth("200px");
        options.addItem("Option One");
        Item two = options
                .addItem("Option Two, with a longer caption that should wrap when the components width is explicitly set.");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", icon(false));
        options.setItemIcon(two, icon(false));
        options.setItemIcon("Option Three", icon(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, explicit width");
        options.setMultiSelect(true);
        options.setWidth("200px");
        options.addItem("Option One");
        two = options
                .addItem("Option Two, with a longer caption that should wrap when the components width is explicitly set.");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", icon(false));
        options.setItemIcon(two, icon(false));
        options.setItemIcon("Option Three", icon(true));
        row.addComponent(options);
    }

    void checkboxes(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Check Boxes", Category.Inputs, null);

        CheckBox check = new CheckBox("Checked", true);
        row.addComponent(check);

        check = new CheckBox(
                "Checked, explicit width, so that the caption should wrap",
                true);
        row.addComponent(check);
        check.setWidth("150px");

        check = new CheckBox("Not checked");
        row.addComponent(check);

        check = new CheckBox(null, true);
        check.setDescription("No caption");
        row.addComponent(check);

        check = new CheckBox("Custom color", true);
        check.addStyleName("color1");
        row.addComponent(check);

        check = new CheckBox("Custom color", true);
        check.addStyleName("color2");
        check.setIcon(icon(false));
        row.addComponent(check);

        check = new CheckBox("With Icon", true);
        check.setIcon(icon(false));
        row.addComponent(check);

        check = new CheckBox();
        check.setIcon(icon(true));
        row.addComponent(check);

        check = new CheckBox("Small", true);
        check.addStyleName("small");
        row.addComponent(check);

        check = new CheckBox("Large", true);
        check.addStyleName("large");
        row.addComponent(check);
    }

    void menubars(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Menu Bars", Category.Basic_Components, null);
        row.setWidth("100%");
        row.addComponent(getMenuBar());
    }

    void comboboxes(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Combo Boxes", Category.Selection_Components,
                null);

        Container generatedContainer = generateContainer(200, false);
        ComboBox combo = new ComboBox("Normal");
        combo.setInputPrompt("You can type here");
        combo.setContainerDataSource(generatedContainer);
        combo.setItemCaptionPropertyId(CAPTION_PROPERTY);
        combo.setItemIconPropertyId(ICON_PROPERTY);
        row.addComponent(combo);

        combo = new ComboBox("Explicit size");
        combo.setInputPrompt("You can type here");
        combo.addItem("Option One");
        combo.addItem("Option Two");
        combo.addItem("Option Three");
        combo.setWidth("260px");
        combo.setHeight("60px");
        row.addComponent(combo);

        combo = new ComboBox("Disabled");
        combo.setInputPrompt("You can type here");
        combo.addItem("Option One");
        combo.addItem("Option Two");
        combo.addItem("Option Three");
        combo.setEnabled(false);
        row.addComponent(combo);

        combo = new ComboBox("Custom color");
        combo.setInputPrompt("You can type here");
        combo.setContainerDataSource(generatedContainer);
        combo.setItemCaptionPropertyId(CAPTION_PROPERTY);
        combo.setItemIconPropertyId(ICON_PROPERTY);
        combo.addStyleName("color1");
        row.addComponent(combo);

        combo = new ComboBox("Custom color");
        combo.setInputPrompt("You can type here");
        combo.setContainerDataSource(generatedContainer);
        combo.setItemCaptionPropertyId(CAPTION_PROPERTY);
        combo.setItemIconPropertyId(ICON_PROPERTY);
        combo.addStyleName("color2");
        row.addComponent(combo);

        combo = new ComboBox("Custom color");
        combo.setInputPrompt("You can type here");
        combo.setContainerDataSource(generatedContainer);
        combo.setItemCaptionPropertyId(CAPTION_PROPERTY);
        combo.setItemIconPropertyId(ICON_PROPERTY);
        combo.addStyleName("color3");
        row.addComponent(combo);

        combo = new ComboBox("Small");
        combo.setInputPrompt("You can type here");
        combo.addItem("Option One");
        combo.addItem("Option Two");
        combo.addItem("Option Three");
        combo.addStyleName("small");
        row.addComponent(combo);

        combo = new ComboBox("Large");
        combo.setInputPrompt("You can type here");
        combo.addItem("Option One");
        combo.addItem("Option Two");
        combo.addItem("Option Three");
        combo.addStyleName("large");
        row.addComponent(combo);
    }

    void textareas(final VerticalLayout root) {
        HorizontalLayout row;
        row = addSection(root, "Text Areas", Category.Inputs, null);

        TextArea ta = new TextArea("Normal");
        ta.setInputPrompt("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Focused");
        ta.setInputPrompt("Write your comment…");
        ta.addStyleName("focused");
        row.addComponent(ta);

        ta = new TextArea("Custom color");
        ta.addStyleName("color1");
        ta.setInputPrompt("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Custom color, read-only");
        ta.addStyleName("color2");
        ta.setValue("Field value, spanning multiple lines of text");
        ta.setReadOnly(true);
        row.addComponent(ta);

        ta = new TextArea("Custom color, inline icon");
        ta.addStyleName("color3");
        ta.setValue("Inline icon not really working…");
        ta.addStyleName("inline-icon");
        ta.setIcon(icon(false));
        row.addComponent(ta);

        ta = new TextArea("Small");
        ta.addStyleName("small");
        ta.setInputPrompt("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Large");
        ta.addStyleName("large");
        ta.setInputPrompt("Write your comment…");
        row.addComponent(ta);

        ta = new TextArea("Borderless");
        ta.addStyleName("borderless");
        ta.setInputPrompt("Write your comment…");
        row.addComponent(ta);
    }

    void textfields(final VerticalLayout root) {
        HorizontalLayout row = addSection(root, "Text Fields", Category.Inputs,
                null);

        TextField tf = new TextField("Normal");
        tf.setInputPrompt("First name");
        tf.setIcon(icon(false));
        row.addComponent(tf);

        tf = new TextField("Focused");
        tf.setInputPrompt("Last name");
        tf.addStyleName("focused");
        row.addComponent(tf);

        tf = new TextField("Custom color");
        tf.setInputPrompt("Email");
        tf.addStyleName("color1");
        row.addComponent(tf);

        tf = new TextField("User Color");
        tf.setInputPrompt("Gender");
        tf.addStyleName("color2");
        row.addComponent(tf);

        tf = new TextField("Themed");
        tf.setInputPrompt("Age");
        tf.addStyleName("color3");
        row.addComponent(tf);

        tf = new TextField("Read-only");
        tf.setInputPrompt("Nationality");
        tf.setValue("Finnish");
        tf.setReadOnly(true);
        row.addComponent(tf);

        tf = new TextField("Small");
        tf.setValue("Field value");
        tf.addStyleName("small");
        row.addComponent(tf);

        tf = new TextField("Large");
        tf.setValue("Field value");
        tf.addStyleName("large");
        tf.setIcon(icon(true));
        row.addComponent(tf);

        tf = new TextField("Icon inside");
        tf.setInputPrompt("Ooh, an icon");
        tf.addStyleName("inline-icon");
        tf.setIcon(icon(false));
        row.addComponent(tf);

        tf = new TextField("16px supported by default");
        tf.setInputPrompt("Image icon");
        tf.addStyleName("inline-icon");
        tf.setIcon(icon(true, 16));
        row.addComponent(tf);

        tf = new TextField();
        tf.setValue("Font, no caption");
        tf.addStyleName("inline-icon");
        tf.setIcon(icon(false));
        row.addComponent(tf);

        tf = new TextField();
        tf.setValue("Image, no caption");
        tf.addStyleName("inline-icon");
        tf.setIcon(icon(true, 16));
        row.addComponent(tf);

        CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);

        tf = new TextField();
        tf.setInputPrompt("Grouped with a button");
        tf.addStyleName("inline-icon");
        tf.setIcon(icon(false));
        tf.setWidth("260px");
        group.addComponent(tf);

        Button button = new Button("Do It");
        // button.addStyleName("primary");
        group.addComponent(button);

        tf = new TextField("Borderless");
        tf.setInputPrompt("Write here…");
        tf.addStyleName("inline-icon");
        tf.addStyleName("borderless");
        tf.setIcon(icon(false));
        row.addComponent(tf);
    }

    void buttonsAndLinks(final VerticalLayout root) {
        HorizontalLayout row = addSection(root, "Buttons",
                Category.Basic_Components, null);

        Button button = new Button("Normal");
        row.addComponent(button);

        button = new Button("Focused");
        button.addStyleName("focus");
        row.addComponent(button);

        button = new Button("Pressed");
        button.addStyleName("active");
        button.addStyleName("focus");
        row.addComponent(button);

        button = new Button("Disabled");
        button.setEnabled(false);
        row.addComponent(button);

        button = new Button("Primary");
        button.addStyleName("primary");
        row.addComponent(button);

        button = new Button("Friendly");
        button.addStyleName("friendly");
        row.addComponent(button);

        button = new Button("Danger");
        button.addStyleName("danger");
        row.addComponent(button);

        // button = new Button("Custom");
        // button.addStyleName("color2");
        // row.addComponent(button);
        //
        // button = new Button("User Color");
        // button.addStyleName("color3");
        // row.addComponent(button);
        //
        // button = new Button("Themed");
        // button.addStyleName("color4");
        // row.addComponent(button);
        //
        // button = new Button("Alternate");
        // button.addStyleName("color5");
        // row.addComponent(button);
        //
        // button = new Button("Other");
        // button.addStyleName("color6");
        // row.addComponent(button);

        button = new Button("Small");
        button.addStyleName("small");
        button.setIcon(icon(false));
        row.addComponent(button);

        button = new Button("Large");
        button.addStyleName("large");
        button.addStyleName("icon-align-right");
        button.setIcon(icon(false));
        row.addComponent(button);

        button = new Button("Image icon");
        button.setIcon(icon(true, 16));
        row.addComponent(button);

        button = new Button("Image icon");
        button.addStyleName("icon-align-right");
        button.setIcon(icon(true));
        row.addComponent(button);

        button = new Button("Photos");
        button.setIcon(icon(false));
        row.addComponent(button);

        button = new Button();
        button.setIcon(icon(false));
        button.addStyleName("icon");
        row.addComponent(button);

        button = new Button("Borderless");
        button.setIcon(icon(false));
        button.addStyleName("borderless");
        row.addComponent(button);

        button = new Button("Link style");
        button.setIcon(icon(false));
        button.addStyleName("link");
        row.addComponent(button);

        button = new Button("Icon on right");
        button.setIcon(icon(false));
        button.addStyleName("icon-align-right");
        row.addComponent(button);

        CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        row.addComponent(group);

        button = new Button("One");
        group.addComponent(button);
        button = new Button("Two");
        group.addComponent(button);
        button = new Button("Three");
        group.addComponent(button);

        NativeButton nbutton = new NativeButton("Native");
        row.addComponent(nbutton);

        Link link = new Link("Link to vaadin.com", new ExternalResource(
                "https://vaadin.com"));
        row.addComponent(link);

        link = new Link("Custom color", new ExternalResource(
                "https://vaadin.com"));
        link.addStyleName("color3");
        link.setIcon(icon(false));
        row.addComponent(link);

        link = new Link("Small", new ExternalResource("https://vaadin.com"));
        link.addStyleName("small");
        row.addComponent(link);

        link = new Link("Large", new ExternalResource("https://vaadin.com"));
        link.addStyleName("large");
        row.addComponent(link);

        link = new Link(null, new ExternalResource("https://vaadin.com"));
        link.setIcon(icon(false));
        row.addComponent(link);
    }

    void colorpickers(final VerticalLayout root) {
        HorizontalLayout row = addSection(root, "Color Pickers",
                Category.Inputs, null);

        ColorPicker cp = new ColorPicker();
        cp.setDefaultCaptionEnabled(true);
        cp.setIcon(icon(false));
        cp.setComponentError(new UserError("Test error"));
        row.addComponent(cp);

        cp = new ColorPicker();
        cp.setPopupStyle(PopupStyle.POPUP_SIMPLE);
        cp.setTextfieldVisibility(true);
        row.addComponent(cp);
    }

    void labels(final VerticalLayout root) {
        addSection(root, "Labels", Category.Basic_Components, null);

        Label large = new Label(
                "Large type for introductory text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        large.addStyleName("large");
        root.addComponent(large);

        Label h2 = new Label("Subtitle");
        h2.addStyleName("h2");
        root.addComponent(h2);

        Label normal = new Label(
                "Normal type for plain text, with a <a href=\"https://vaadin.com\">regular link</a>. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.",
                ContentMode.HTML);
        root.addComponent(normal);

        Label h3 = new Label("Small Title");
        h3.addStyleName("h3");
        root.addComponent(h3);

        Label small = new Label(
                "Small type for additional text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        small.addStyleName("small");
        root.addComponent(small);

        Label h4 = new Label("Section Title");
        h4.addStyleName("h4");
        root.addComponent(h4);

        normal = new Label(
                "Normal type for plain text. Etiam at risus et justo dignissim congue. Donec congue lacinia dui, a porttitor lectus condimentum laoreet. Nunc eu.");
        root.addComponent(normal);

        h4 = new Label("Spinner");
        h4.addStyleName("h4");
        root.addComponent(h4);

        Label spinnerDesc = new Label(
                "Below is a Label with a custom style name, for which the spinner mixin is added.");
        spinnerDesc.addStyleName("small");
        root.addComponent(spinnerDesc);

        Label spinner = new Label();
        spinner.addStyleName("spinner");
        root.addComponent(spinner);
    }

    HorizontalLayout addSection(VerticalLayout root, String title,
            Category category, String description) {
        String id = title.toLowerCase().replace(" ", "-");

        Label h1 = new Label(title);
        h1.addStyleName("h1");
        h1.setId("" + id);
        root.addComponent(h1);

        if (description != null) {
            Label desc = new Label(description + "<br><br>", ContentMode.HTML);
            root.addComponent(desc);
        }

        Link link = new Link(title, new ExternalResource("#" + id));

        ArrayList<Link> items = menuItems.get(category);
        if (items == null) {
            items = new ArrayList<Link>();
            menuItems.put(category, items);
        }
        items.add(link);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        root.addComponent(row);
        return row;
    }

    private Component windowContents(boolean scrollable) {
        VerticalLayout root = new VerticalLayout();

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName("v-window-bottom-toolbar");

        Label footerText = new Label("Footer text");
        footerText.setSizeUndefined();

        Button ok = new Button("OK");
        ok.addStyleName("primary");

        Button cancel = new Button("Cancel");

        footer.addComponents(footerText, ok, cancel);
        footer.setExpandRatio(footerText, 1);

        Component content = null;
        if (scrollable) {
            Panel panel = new Panel();
            panel.setSizeFull();
            panel.addStyleName("borderless");
            VerticalLayout l = new VerticalLayout();
            l.addComponent(new Label(
                    "<h2>Subtitle</h2><p>Quam diu etiam furor iste tuus nos eludet? Petierunt uti sibi concilium totius Galliae in diem certam indicere. Ut enim ad minim veniam, quis nostrud exercitation. Quae vero auctorem tractata ab fiducia dicuntur.</p><p>Quisque ut dolor gravida, placerat libero vel, euismod. Etiam habebis sem dicantur magna mollis euismod. Nihil hic munitissimus habendi senatus locus, nihil horum? Curabitur est gravida et libero vitae dictum. Ullamco laboris nisi ut aliquid ex ea commodi consequat. Morbi odio eros, volutpat ut pharetra vitae, lobortis sed nibh.</p>",
                    ContentMode.HTML));
            l.setMargin(true);
            panel.setContent(l);
            content = panel;
        } else {
            content = new Label(
                    "<h2>Subtitle</h2><p>Normal type for plain text. Etiam at risus et justo dignissim congue.</p>",
                    ContentMode.HTML);
        }
        root.addComponents(content, footer);
        if (scrollable) {
            root.setSizeFull();
            root.setExpandRatio(content, 1);
        }
        root.setMargin(!scrollable);

        return root;
    }

    Component panelContent() {
        // return new Button("Panel content");
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        Label content = new Label(
                "Suspendisse dictum feugiat nisl ut dapibus. Mauris iaculis porttitor posuere. Praesent id metus massa, ut blandit odio.");
        content.setWidth("10em");
        layout.addComponent(content);
        Button button = new Button("Button");
        button.setSizeFull();
        layout.addComponent(button);
        return layout;
    }

    Table getTable(String caption) {
        Table table = new Table(caption);
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setSortEnabled(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setPageLength(6);
        table.addActionHandler(this);
        table.setDragMode(TableDragMode.MULTIROW);
        table.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Notification.show(event.getTransferable().toString());
            }
        });
        table.setContainerDataSource(tableData);
        table.select(tableData.getItemIds().iterator().next());
        table.setSortContainerPropertyId(CAPTION_PROPERTY);
        table.setItemIconPropertyId(ICON_PROPERTY);
        table.setColumnAlignment(DESCRIPTION_PROPERTY, Align.RIGHT);

        return table;
    }

    TreeTable getTreeTable(String caption) {
        TreeTable table = new TreeTable(caption);
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setSortEnabled(true);
        table.setAnimationsEnabled(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setPageLength(6);
        table.addActionHandler(this);
        table.setDragMode(TableDragMode.MULTIROW);
        table.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Notification.show(event.getTransferable().toString());
            }
        });
        table.setContainerDataSource(container);
        table.select(tableData.getItemIds().iterator().next());
        table.setItemIconPropertyId(ICON_PROPERTY);

        return table;
    }

    // Actions for the context menu
    private static final Action ACTION_ONE = new Action("Action One");
    private static final Action ACTION_TWO = new Action("Action Two");
    private static final Action ACTION_THREE = new Action("Action Three");
    private static final Action[] ACTIONS = new Action[] { ACTION_ONE,
            ACTION_TWO, ACTION_THREE };
    private Container container;
    private Object firstItemId;

    @Override
    public Action[] getActions(Object target, Object sender) {
        return ACTIONS;
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        Notification.show(action.getCaption());
    }

    MenuBar getMenuBar() {
        MenuBar menubar = new MenuBar();
        menubar.setWidth("100%");
        final MenuBar.MenuItem file = menubar.addItem("File", null);
        final MenuBar.MenuItem newItem = file.addItem("New", null);
        file.addItem("Open file...", null);
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

        find.addItem("Google Search", null);
        find.addSeparator();
        find.addItem("Find/Replace...", null);
        find.addItem("Find Next", null);
        find.addItem("Find Previous", null);

        Command check = new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                Notification.show(selectedItem.isChecked() ? "Checked"
                        : "Unchecked");
            }
        };

        final MenuBar.MenuItem view = menubar.addItem("View", null);
        view.addItem("Show Status Bar", check).setCheckable(true);
        MenuItem title = view.addItem("Show Title Bar", check);
        title.setCheckable(true);
        title.setChecked(true);
        view.addItem("Customize Toolbar...", null);
        view.addSeparator();

        view.addItem("Actual Size", null);
        view.addItem("Zoom In", null);
        view.addItem("Zoom Out", null);

        MenuItem fav = menubar.addItem("", check);
        fav.setIcon(icon(false));
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        fav.setChecked(true);

        fav = menubar.addItem("", check);
        fav.setIcon(icon(false));
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        fav.setCheckable(true);

        menubar.addItem("Attach", null).setIcon(FontAwesome.PAPERCLIP);
        menubar.addItem("Undo", null).setIcon(FontAwesome.UNDO);
        menubar.addItem("Redo", null).setIcon(FontAwesome.REPEAT);
        menubar.addItem("Upload", null).setIcon(FontAwesome.UPLOAD);

        return menubar;
    }

    public static Resource icon(boolean isImage) {
        return icon(isImage, 32);
    }

    public static Resource icon(boolean isImage, int imageSize) {
        if (!isImage) {
            if (++iconCount >= SIZE) {
                iconCount = 0;
            }
            return ICONS.get(iconCount);
        }
        return new ThemeResource("../runo/icons/" + imageSize + "/document.png");
    }

    static List<FontAwesome> ICONS = Collections.unmodifiableList(Arrays
            .asList(FontAwesome.values()));
    static final int SIZE = ICONS.size();
    static int iconCount = 110;

    TabSheet getTabSheet(String caption, String style, boolean closable,
            boolean scrolling, boolean icon) {
        TabSheet ts = new TabSheet();
        ts.addStyleName(style);
        ts.setCaption(caption);

        for (int i = 0; i < (scrolling ? 10 : 3); i++) {
            String tabcaption = nextString(true);
            if (i == 0 && icon) {
                tabcaption = null;
            }
            Tab t = ts.addTab(new Label(" "), tabcaption);
            t.setClosable(closable);

            if (icon) {
                t.setIcon(icon(i == 2));
            }
        }
        return ts;
    }

    static String[] strings = new String[] { "lorem", "ipsum", "dolor", "sit",
            "amet", "consectetur" };
    static int stringCount = -1;

    String nextString(boolean capitalize) {
        if (++stringCount >= strings.length) {
            stringCount = 0;
        }
        return capitalize ? strings[stringCount].substring(0, 1).toUpperCase()
                + strings[stringCount].substring(1) : strings[stringCount];
    }
}