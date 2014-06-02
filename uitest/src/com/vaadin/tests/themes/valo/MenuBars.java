/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class MenuBars extends VerticalLayout implements View {
    public MenuBars() {
        setMargin(true);
        setSpacing(true);

        Label h1 = new Label("Menu Bars");
        h1.addStyleName("h1");
        addComponent(h1);

        MenuBar menuBar = getMenuBar();
        menuBar.setCaption("Normal style");
        addComponent(menuBar);

        menuBar = getMenuBar();
        menuBar.setCaption("Small style");
        menuBar.addStyleName("small");
        addComponent(menuBar);

        menuBar = getMenuBar();
        menuBar.setCaption("Borderless style");
        menuBar.addStyleName("borderless");
        addComponent(menuBar);
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
        fav.setIcon(TestIcon.get());
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        fav.setChecked(true);

        fav = menubar.addItem("", check);
        fav.setIcon(TestIcon.get());
        fav.setStyleName("icon-only");
        fav.setCheckable(true);
        fav.setCheckable(true);

        menubar.addItem("Attach", null).setIcon(FontAwesome.PAPERCLIP);
        menubar.addItem("Undo", null).setIcon(FontAwesome.UNDO);
        MenuItem redo = menubar.addItem("Redo", null);
        redo.setIcon(FontAwesome.REPEAT);
        redo.setEnabled(false);
        menubar.addItem("Upload", null).setIcon(FontAwesome.UPLOAD);

        return menubar;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
