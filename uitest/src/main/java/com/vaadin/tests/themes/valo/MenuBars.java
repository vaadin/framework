package com.vaadin.tests.themes.valo;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class MenuBars extends VerticalLayout implements View {
    public MenuBars() {
        setMargin(true);
        setSpacing(true);

        Label h1 = new Label("Menu Bars");
        h1.addStyleName(ValoTheme.LABEL_H1);
        addComponent(h1);

        MenuBar menuBar = getMenuBar();
        menuBar.setCaption("Normal style");
        addComponent(menuBar);

        menuBar = getMenuBar();
        menuBar.setCaption("Small style");
        menuBar.addStyleName(ValoTheme.MENUBAR_SMALL);
        addComponent(menuBar);

        menuBar = getMenuBar();
        menuBar.setCaption("Borderless style");
        menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        addComponent(menuBar);

        menuBar = getMenuBar();
        menuBar.setCaption("Small borderless style");
        menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        menuBar.addStyleName(ValoTheme.MENUBAR_SMALL);
        addComponent(menuBar);

        Label h2 = new Label("Drop Down Button");
        h2.addStyleName(ValoTheme.LABEL_H2);
        addComponent(h2);

        HorizontalLayout wrap = new HorizontalLayout();
        wrap.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        wrap.setSpacing(true);
        addComponent(wrap);

        wrap.addComponent(getMenuButton("Normal", false));

        MenuBar split = getMenuButton("Small", false);
        split.addStyleName(ValoTheme.MENUBAR_SMALL);
        wrap.addComponent(split);

        split = getMenuButton("Borderless", false);
        split.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        wrap.addComponent(split);

        split = getMenuButton("Themed", false);
        split.addStyleName("color1");
        wrap.addComponent(split);

        split = getMenuButton("Small", false);
        split.addStyleName("color1");
        split.addStyleName(ValoTheme.MENUBAR_SMALL);
        wrap.addComponent(split);

        h2 = new Label("Split Button");
        h2.addStyleName(ValoTheme.LABEL_H2);
        addComponent(h2);

        wrap = new HorizontalLayout();
        wrap.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        wrap.setSpacing(true);
        addComponent(wrap);

        wrap.addComponent(getMenuButton("Normal", true));

        split = getMenuButton("Small", true);
        split.addStyleName(ValoTheme.MENUBAR_SMALL);
        wrap.addComponent(split);

        split = getMenuButton("Borderless", true);
        split.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        wrap.addComponent(split);

        split = getMenuButton("Themed", true);
        split.addStyleName("color1");
        wrap.addComponent(split);

        split = getMenuButton("Small", true);
        split.addStyleName("color1");
        split.addStyleName(ValoTheme.MENUBAR_SMALL);
        wrap.addComponent(split);
    }

    static MenuBar getMenuBar() {
        Command click = new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                Notification.show("Clicked " + selectedItem.getText());
            }
        };

        MenuBar menubar = new MenuBar();
        menubar.setWidth("100%");
        final MenuBar.MenuItem file = menubar.addItem("File", null);
        final MenuBar.MenuItem newItem = file.addItem("New", null);
        file.addItem("Open file...", click);
        file.addSeparator();

        newItem.addItem("File", click);
        newItem.addItem("Folder", click);
        newItem.addItem("Project...", click);

        file.addItem("Close", click);
        file.addItem("Close All", click);
        file.addSeparator();

        file.addItem("Save", click);
        file.addItem("Save As...", click);
        file.addItem("Save All", click);

        final MenuBar.MenuItem edit = menubar.addItem("Edit", null);
        edit.addItem("Undo", click);
        edit.addItem("Redo", click).setEnabled(false);
        edit.addSeparator();

        edit.addItem("Cut", click);
        edit.addItem("Copy", click);
        edit.addItem("Paste", click);
        edit.addSeparator();

        final MenuBar.MenuItem find = edit.addItem("Find/Replace", null);

        find.addItem("Google Search", click);
        find.addSeparator();
        find.addItem("Find/Replace...", click);
        find.addItem("Find Next", click);
        find.addItem("Find Previous", click);

        Command check = new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                Notification.show(
                        selectedItem.isChecked() ? "Checked" : "Unchecked");
            }
        };

        final MenuBar.MenuItem view = menubar.addItem("View", null);
        view.addItem("Show Status Bar", check).setCheckable(true);
        MenuItem title = view.addItem("Show Title Bar", check);
        title.setCheckable(true);
        title.setChecked(true);
        view.addItem("Customize Toolbar...", click);
        view.addSeparator();

        view.addItem("Actual Size", click);
        view.addItem("Zoom In", click);
        view.addItem("Zoom Out", click);

        TestIcon testIcon = new TestIcon(50);

        MenuItem fav = menubar.addItem("", check);
        fav.setIcon(testIcon.get());
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        fav.setChecked(true);

        fav = menubar.addItem("", check);
        fav.setIcon(testIcon.get());
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        fav.setCheckable(true);

        menubar.addItem("Attach", click).setIcon(VaadinIcons.PAPERCLIP);
        menubar.addItem("Undo", click).setIcon(VaadinIcons.ROTATE_LEFT);
        MenuItem redo = menubar.addItem("Redo", click);
        redo.setIcon(VaadinIcons.ROTATE_RIGHT);
        redo.setEnabled(false);
        menubar.addItem("Upload", click).setIcon(VaadinIcons.UPLOAD);

        return menubar;
    }

    static MenuBar getToolBar() {
        MenuBar menubar = new MenuBar();
        menubar.setWidth("100%");

        MenuItem fav = menubar.addItem("", null);
        fav.setIcon(VaadinIcons.ALIGN_LEFT);
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        // fav.setChecked(true);

        fav = menubar.addItem("", null);
        fav.setIcon(VaadinIcons.ALIGN_CENTER);
        fav.setStyleName("icon-only");
        fav.setCheckable(true);

        fav = menubar.addItem("", null);
        fav.setIcon(VaadinIcons.ALIGN_RIGHT);
        fav.setStyleName("icon-only");
        fav.setCheckable(true);

        MenuItem clip = menubar.addItem("", null);
        clip.setIcon(VaadinIcons.PAPERCLIP);
        clip.setStyleName("icon-only");

        MenuItem undo = menubar.addItem("", null);
        undo.setIcon(VaadinIcons.ROTATE_LEFT);
        undo.setStyleName("icon-only");

        MenuItem redo = menubar.addItem("", null);
        redo.setIcon(VaadinIcons.ROTATE_RIGHT);
        redo.setEnabled(false);
        redo.setStyleName("icon-only");

        return menubar;
    }

    static MenuBar getMenuButton(String caption, boolean splitButton) {
        MenuBar split = new MenuBar();
        MenuBar.MenuItem dropdown = split.addItem(caption, null);
        if (splitButton) {
            dropdown = split.addItem("", null);
        }
        dropdown.addItem("Another Action", null);
        dropdown.addItem("Secondary Action", null);
        dropdown.addSeparator();
        dropdown.addItem("Last Action", null);

        return split;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
