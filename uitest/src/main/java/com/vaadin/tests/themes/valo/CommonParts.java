package com.vaadin.tests.themes.valo;

import java.util.Locale;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class CommonParts extends VerticalLayout implements View {
    public CommonParts() {
        setSpacing(false);

        Label h1 = new Label("Common UI Elements");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        GridLayout row = new GridLayout(2, 3);
        row.setWidth("100%");
        row.setSpacing(true);
        addComponent(row);

        row.addComponent(loadingIndicators());
        row.addComponent(notifications(), 1, 0, 1, 2);
        row.addComponent(windows());
        row.addComponent(tooltips());

    }

    Panel loadingIndicators() {
        Panel p = new Panel("Loading Indicator");
        VerticalLayout content = new VerticalLayout();
        p.setContent(content);
        content.addComponent(new Label(
                "You can test the loading indicator by pressing the buttons."));

        CssLayout group = new CssLayout();
        group.setCaption("Show the loading indicator for…");
        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        content.addComponent(group);
        Button loading = new Button("0.8");
        loading.addClickListener(event -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
            }
        });
        group.addComponent(loading);

        Button delay = new Button("3");
        delay.addClickListener(event -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        });
        group.addComponent(delay);

        Button wait = new Button("15");
        wait.addClickListener(event -> {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
            }
        });
        wait.addStyleName("last");
        group.addComponent(wait);
        Label label = new Label("&nbsp;&nbsp; seconds", ContentMode.HTML);
        label.setSizeUndefined();
        group.addComponent(label);

        Label spinnerDesc = new Label(
                "The theme also provides a mixin that you can use to include a spinner anywhere in your application. Below is a Label with a custom style name, for which the spinner mixin is added.");
        spinnerDesc.setWidth("100%");
        spinnerDesc.addStyleName(ValoTheme.LABEL_SMALL);
        spinnerDesc.setCaption("Spinner");
        content.addComponent(spinnerDesc);

        if (!ValoThemeUI.isTestMode()) {
            Label spinner = new Label();
            spinner.addStyleName(ValoTheme.LABEL_SPINNER);
            content.addComponent(spinner);
        }

        return p;
    }

    Panel notifications() {
        Panel p = new Panel("Notifications");
        VerticalLayout content = new VerticalLayout() {
            Notification notification = new Notification("");
            TextField title = new TextField("Title");
            TextArea description = new TextArea("Description");
            MenuBar style = new MenuBar();
            MenuBar type = new MenuBar();
            String typeString = "";
            String styleString = "";
            TextField delay = new TextField();
            {
                title.setPlaceholder("Title for the notification");
                title.addValueChangeListener(event -> {
                    if (title.getValue() == null
                            || title.getValue().isEmpty()) {
                        notification.setCaption(null);
                    } else {
                        notification.setCaption(title.getValue());
                    }
                });
                title.setValue("Notification Title");
                title.setWidth("100%");
                addComponent(title);

                description.setPlaceholder("Description for the notification");
                description.addStyleName(ValoTheme.TEXTAREA_SMALL);
                description.addValueChangeListener(listener -> {
                    if (description.getValue() == null
                            || description.getValue().isEmpty()) {
                        notification.setDescription(null);
                    } else {
                        notification.setDescription(description.getValue());
                    }
                });
                description.setValue(
                        "A more informative message about what has happened. Nihil hic munitissimus habendi senatus locus, nihil horum? Inmensae subtilitatis, obscuris et malesuada fames. Hi omnes lingua, institutis, legibus inter se differunt.");
                description.setWidth("100%");
                addComponent(description);

                Command typeCommand = selectedItem -> {
                    if (selectedItem.getText().equals("Humanized")) {
                        typeString = "";
                        notification.setStyleName(styleString.trim());
                    } else {
                        typeString = selectedItem.getText()
                                .toLowerCase(Locale.ROOT);
                        notification.setStyleName(
                                (typeString + " " + styleString.trim()).trim());
                    }
                    for (MenuItem item : type.getItems()) {
                        item.setChecked(false);
                    }
                    selectedItem.setChecked(true);
                };

                type.setCaption("Type");
                MenuItem humanized = type.addItem("Humanized", typeCommand);
                humanized.setCheckable(true);
                humanized.setChecked(true);
                type.addItem("Tray", typeCommand).setCheckable(true);
                type.addItem("Warning", typeCommand).setCheckable(true);
                type.addItem("Error", typeCommand).setCheckable(true);
                type.addItem("System", typeCommand).setCheckable(true);
                addComponent(type);
                type.addStyleName(ValoTheme.MENUBAR_SMALL);

                Command styleCommand = selectedItem -> {
                    styleString = "";
                    for (MenuItem item : style.getItems()) {
                        if (item.isChecked()) {
                            styleString += " "
                                    + item.getText().toLowerCase(Locale.ROOT);
                        }
                    }
                    if (!styleString.trim().isEmpty()) {
                        notification.setStyleName(
                                (typeString + " " + styleString.trim()).trim());
                    } else if (!typeString.isEmpty()) {
                        notification.setStyleName(typeString.trim());
                    } else {
                        notification.setStyleName(null);
                    }
                };

                style.setCaption("Additional style");
                style.addItem("Dark", styleCommand).setCheckable(true);
                style.addItem("Success", styleCommand).setCheckable(true);
                style.addItem("Failure", styleCommand).setCheckable(true);
                style.addItem("Bar", styleCommand).setCheckable(true);
                style.addItem("Small", styleCommand).setCheckable(true);
                style.addItem("Closable", styleCommand).setCheckable(true);
                addComponent(style);
                style.addStyleName(ValoTheme.MENUBAR_SMALL);

                CssLayout group = new CssLayout();
                group.setCaption("Fade delay");
                group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
                addComponent(group);

                delay.setPlaceholder("Infinite");
                delay.addStyleName(ValoTheme.TEXTFIELD_ALIGN_RIGHT);
                delay.addStyleName(ValoTheme.TEXTFIELD_SMALL);
                delay.setWidth("7em");
                delay.addValueChangeListener(event -> {
                    try {
                        notification.setDelayMsec(
                                Integer.parseInt(delay.getValue()));
                    } catch (Exception e) {
                        notification.setDelayMsec(-1);
                        delay.setValue("");
                    }
                });
                delay.setValue("1000");
                group.addComponent(delay);

                Button clear = new Button("", event -> delay.setValue(""));
                clear.setIcon(VaadinIcons.CLOSE_CIRCLE);
                clear.addStyleName("last");
                clear.addStyleName(ValoTheme.BUTTON_SMALL);
                clear.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
                group.addComponent(clear);
                group.addComponent(new Label("&nbsp; msec", ContentMode.HTML));

                GridLayout grid = new GridLayout(3, 3);
                grid.setCaption("Show in position");
                addComponent(grid);
                grid.setSpacing(true);

                Button pos = new Button("", event -> {
                    notification.setPosition(Position.TOP_LEFT);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.TOP_CENTER);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.TOP_RIGHT);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.MIDDLE_LEFT);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.MIDDLE_CENTER);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.MIDDLE_RIGHT);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.BOTTOM_LEFT);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.BOTTOM_CENTER);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);

                pos = new Button("", event -> {
                    notification.setPosition(Position.BOTTOM_RIGHT);
                    notification.show(Page.getCurrent());
                });
                pos.addStyleName(ValoTheme.BUTTON_SMALL);
                grid.addComponent(pos);
            }
        };
        p.setContent(content);

        return p;
    }

    Panel tooltips() {
        Panel p = new Panel("Tooltips");
        HorizontalLayout content = new HorizontalLayout() {
            {
                setMargin(true);
                addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

                addComponent(new Label(
                        "Try out different tooltips/descriptions by hovering over the labels."));

                Label label = new Label("Simple");
                label.addStyleName(ValoTheme.LABEL_BOLD);
                label.setDescription("Simple tooltip message");
                addComponent(label);

                label = new Label("Long");
                label.addStyleName(ValoTheme.LABEL_BOLD);
                label.setDescription(
                        "Long tooltip message. Inmensae subtilitatis, obscuris et malesuada fames. Salutantibus vitae elit libero, a pharetra augue.");
                addComponent(label);

                label = new Label("HTML tooltip");
                label.addStyleName(ValoTheme.LABEL_BOLD);
                label.setDescription(
                        "<div><h1>Ut enim ad minim veniam, quis nostrud exercitation</h1><p><span>Morbi fringilla convallis sapien, id pulvinar odio volutpat.</span> <span>Vivamus sagittis lacus vel augue laoreet rutrum faucibus.</span> <span>Donec sed odio operae, eu vulputate felis rhoncus.</span> <span>At nos hinc posthac, sitientis piros Afros.</span> <span>Tu quoque, Brute, fili mi, nihil timor populi, nihil!</span></p><p><span>Gallia est omnis divisa in partes tres, quarum.</span> <span>Praeterea iter est quasdam res quas ex communi.</span> <span>Cum ceteris in veneratione tui montes, nascetur mus.</span> <span>Quam temere in vitiis, legem sancimus haerentia.</span> <span>Idque Caesaris facere voluntate liceret: sese habere.</span></p></div>");
                addComponent(label);

                label = new Label("With an error message");
                label.addStyleName(ValoTheme.LABEL_BOLD);
                label.setDescription("Simple tooltip message");
                label.setComponentError(
                        new UserError("Something terrible has happened"));
                addComponent(label);

                label = new Label("With a long error message");
                label.addStyleName(ValoTheme.LABEL_BOLD);
                label.setDescription("Simple tooltip message");
                label.setComponentError(new UserError(
                        "<h2>Contra legem facit qui id facit quod lex prohibet <span>Tityre, tu patulae recubans sub tegmine fagi  dolor.</span> <span>Tityre, tu patulae recubans sub tegmine fagi  dolor.</span> <span>Prima luce, cum quibus mons aliud  consensu ab eo.</span> <span>Quid securi etiam tamquam eu fugiat nulla pariatur.</span> <span>Fabio vel iudice vincam, sunt in culpa qui officia.</span> <span>Nihil hic munitissimus habendi senatus locus, nihil horum?</span></p><p><span>Plura mihi bona sunt, inclinet, amari petere vellent.</span> <span>Integer legentibus erat a ante historiarum dapibus.</span> <span>Quam diu etiam furor iste tuus nos eludet?</span> <span>Nec dubitamus multa iter quae et nos invenerat.</span> <span>Quisque ut dolor gravida, placerat libero vel, euismod.</span> <span>Quae vero auctorem tractata ab fiducia dicuntur.</span></h2>",
                        AbstractErrorMessage.ContentMode.HTML,
                        ErrorLevel.CRITICAL));
                addComponent(label);

                label = new Label("Error message only");
                label.addStyleName(ValoTheme.LABEL_BOLD);
                label.setComponentError(
                        new UserError("Something terrible has happened"));
                addComponent(label);
            }
        };
        p.setContent(content);
        return p;

    }

    Panel windows() {
        Panel p = new Panel("Dialogs");
        VerticalLayout content = new VerticalLayout() {
            final Window win = new Window("Window Caption");
            String prevHeight = "300px";
            boolean footerVisible = true;
            boolean autoHeight = false;
            boolean tabsVisible = false;
            boolean toolbarVisible = false;
            boolean footerToolbar = false;
            boolean toolbarLayout = false;
            String toolbarStyle = null;

            VerticalLayout windowContent() {
                VerticalLayout root = new VerticalLayout();

                if (toolbarVisible) {
                    MenuBar menuBar = MenuBars.getToolBar();
                    menuBar.setSizeUndefined();
                    menuBar.setStyleName(toolbarStyle);
                    Component toolbar = menuBar;
                    if (toolbarLayout) {
                        menuBar.setWidth(null);
                        HorizontalLayout toolbarLayout = new HorizontalLayout();
                        toolbarLayout.setWidth("100%");
                        toolbarLayout.setSpacing(true);
                        Label label = new Label("Tools");
                        label.setSizeUndefined();
                        toolbarLayout.addComponents(label, menuBar);
                        toolbarLayout.setExpandRatio(menuBar, 1);
                        toolbarLayout.setComponentAlignment(menuBar,
                                Alignment.TOP_RIGHT);
                        toolbar = toolbarLayout;
                    }
                    toolbar.addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
                    root.addComponent(toolbar);
                }

                Component content = null;

                if (tabsVisible) {
                    TabSheet tabs = new TabSheet();
                    tabs.setSizeFull();
                    VerticalLayout l = new VerticalLayout();
                    l.addComponent(new Label(
                            "<h2>Subtitle</h2><p>Normal type for plain text. Etiam at risus et justo dignissim congue. Phasellus laoreet lorem vel dolor tempus vehicula.</p><p>Quisque ut dolor gravida, placerat libero vel, euismod. Etiam habebis sem dicantur magna mollis euismod. Nihil hic munitissimus habendi senatus locus, nihil horum? Curabitur est gravida et libero vitae dictum. Ullamco laboris nisi ut aliquid ex ea commodi consequat. Morbi odio eros, volutpat ut pharetra vitae, lobortis sed nibh.</p>",
                            ContentMode.HTML));
                    l.setMargin(true);
                    tabs.addTab(l, "Selected");
                    tabs.addTab(new Label("&nbsp;", ContentMode.HTML),
                            "Another");
                    tabs.addTab(new Label("&nbsp;", ContentMode.HTML),
                            "One more");
                    tabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
                    tabs.addSelectedTabChangeListener(event -> {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    content = tabs;
                } else if (!autoHeight) {
                    Panel p = new Panel();
                    p.setSizeFull();
                    p.addStyleName(ValoTheme.PANEL_BORDERLESS);
                    if (!toolbarVisible || !toolbarLayout) {
                        p.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);
                    }
                    VerticalLayout l = new VerticalLayout();
                    l.addComponent(new Label(
                            "<h2>Subtitle</h2><p>Normal type for plain text. Etiam at risus et justo dignissim congue. Phasellus laoreet lorem vel dolor tempus vehicula.</p><p>Quisque ut dolor gravida, placerat libero vel, euismod. Etiam habebis sem dicantur magna mollis euismod. Nihil hic munitissimus habendi senatus locus, nihil horum? Curabitur est gravida et libero vitae dictum. Ullamco laboris nisi ut aliquid ex ea commodi consequat. Morbi odio eros, volutpat ut pharetra vitae, lobortis sed nibh.</p>",
                            ContentMode.HTML));
                    l.setMargin(true);
                    p.setContent(l);
                    content = p;
                } else {
                    content = new Label(
                            "<h2>Subtitle</h2><p>Normal type for plain text. Etiam at risus et justo dignissim congue. Phasellus laoreet lorem vel dolor tempus vehicula.</p><p>Quisque ut dolor gravida, placerat libero vel, euismod. Etiam habebis sem dicantur magna mollis euismod. Nihil hic munitissimus habendi senatus locus, nihil horum? Curabitur est gravida et libero vitae dictum. Ullamco laboris nisi ut aliquid ex ea commodi consequat. Morbi odio eros, volutpat ut pharetra vitae, lobortis sed nibh.</p>",
                            ContentMode.HTML);
                    root.setMargin(true);
                }

                root.addComponent(content);

                if (footerVisible) {
                    HorizontalLayout footer = new HorizontalLayout();
                    footer.setWidth("100%");
                    footer.setSpacing(true);
                    footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

                    Label footerText = new Label("Footer text");
                    footerText.setSizeUndefined();

                    Button ok = new Button("OK");
                    ok.addStyleName(ValoTheme.BUTTON_PRIMARY);

                    Button cancel = new Button("Cancel");

                    footer.addComponents(footerText, ok, cancel);
                    footer.setExpandRatio(footerText, 1);

                    if (footerToolbar) {
                        MenuBar menuBar = MenuBars.getToolBar();
                        menuBar.setStyleName(toolbarStyle);
                        menuBar.setWidth(null);
                        footer.removeAllComponents();
                        footer.addComponent(menuBar);
                    }

                    root.addComponent(footer);
                }

                if (!autoHeight) {
                    root.setSizeFull();
                    root.setExpandRatio(content, 1);
                }

                return root;
            }

            {
                setSpacing(true);
                setMargin(true);
                win.setWidth("380px");
                win.setHeight(prevHeight);
                win.setClosable(false);
                win.setResizable(false);
                win.setContent(windowContent());
                win.setCloseShortcut(KeyCode.ESCAPE, null);

                Command optionsCommand = selectedItem -> {
                    if (selectedItem.getText().equals("Footer")) {
                        footerVisible = selectedItem.isChecked();
                    }
                    if (selectedItem.getText().equals("Auto Height")) {
                        autoHeight = selectedItem.isChecked();
                        if (!autoHeight) {
                            win.setHeight(prevHeight);
                        } else {
                            prevHeight = win.getHeight()
                                    + win.getHeightUnits().toString();
                            win.setHeight(null);
                        }
                    }
                    if (selectedItem.getText().equals("Tabs")) {
                        tabsVisible = selectedItem.isChecked();
                    }

                    if (selectedItem.getText().equals("Top Toolbar")) {
                        toolbarVisible = selectedItem.isChecked();
                    }

                    if (selectedItem.getText().equals("Footer Toolbar")) {
                        footerToolbar = selectedItem.isChecked();
                    }

                    if (selectedItem.getText().equals("Top Toolbar layout")) {
                        toolbarLayout = selectedItem.isChecked();
                    }

                    if (selectedItem.getText().equals("Borderless Toolbars")) {
                        toolbarStyle = selectedItem.isChecked()
                                ? ValoTheme.MENUBAR_BORDERLESS
                                : null;
                    }

                    win.setContent(windowContent());
                };

                MenuBar options = new MenuBar();
                options.setCaption("Content");
                options.addItem("Auto Height", optionsCommand)
                        .setCheckable(true);
                options.addItem("Tabs", optionsCommand).setCheckable(true);
                MenuItem option = options.addItem("Footer", optionsCommand);
                option.setCheckable(true);
                option.setChecked(true);
                options.addStyleName(ValoTheme.MENUBAR_SMALL);
                addComponent(options);

                options = new MenuBar();
                options.setCaption("Toolbars");
                options.addItem("Footer Toolbar", optionsCommand)
                        .setCheckable(true);
                options.addItem("Top Toolbar", optionsCommand)
                        .setCheckable(true);
                options.addItem("Top Toolbar layout", optionsCommand)
                        .setCheckable(true);
                options.addItem("Borderless Toolbars", optionsCommand)
                        .setCheckable(true);
                options.addStyleName(ValoTheme.MENUBAR_SMALL);
                addComponent(options);

                Command optionsCommand2 = selectedItem -> {
                    if (selectedItem.getText().equals("Caption")) {
                        win.setCaption(
                                selectedItem.isChecked() ? "Window Caption"
                                        : null);
                    } else if (selectedItem.getText().equals("Closable")) {
                        win.setClosable(selectedItem.isChecked());
                    } else if (selectedItem.getText().equals("Resizable")) {
                        win.setResizable(selectedItem.isChecked());
                    } else if (selectedItem.getText().equals("Modal")) {
                        win.setModal(selectedItem.isChecked());
                    }
                };

                options = new MenuBar();
                options.setCaption("Options");
                MenuItem caption = options.addItem("Caption", optionsCommand2);
                caption.setCheckable(true);
                caption.setChecked(true);
                options.addItem("Closable", optionsCommand2).setCheckable(true);
                options.addItem("Resizable", optionsCommand2)
                        .setCheckable(true);
                options.addItem("Modal", optionsCommand2).setCheckable(true);
                options.addStyleName(ValoTheme.MENUBAR_SMALL);
                addComponent(options);

                final Button show = new Button("Open Window", event -> {
                    getUI().addWindow(win);
                    win.center();
                    win.focus();
                    event.getButton().setEnabled(false);
                });
                show.addStyleName(ValoTheme.BUTTON_PRIMARY);
                addComponent(show);

                final CheckBox hidden = new CheckBox("Hidden");
                hidden.addValueChangeListener(
                        event -> win.setVisible(!hidden.getValue()));
                addComponent(hidden);

                win.addCloseListener(event -> show.setEnabled(true));
            }
        };
        p.setContent(content);
        return p;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
