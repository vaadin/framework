package com.vaadin.tests.components.menubar;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class MenuBarSmallWidth extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        MenuBar barmenu = new MenuBar();
        barmenu.setWidth("50px");
        addComponent(barmenu);
        // A top-level menu item that opens a submenu
        MenuBar.MenuItem drinks = barmenu.addItem("Beverages", null, null);

        // Submenu item with a sub-submenu
        MenuBar.MenuItem hots = drinks.addItem("Hot", null, null);
        hots.addItem("Tea", new ThemeResource("icons/tea-16px.png"), null);
        hots.addItem("Coffee", new ThemeResource("icons/coffee-16px.png"),
                null);

        // Another submenu item with a sub-submenu
        MenuBar.MenuItem colds = drinks.addItem("Cold", null, null);
        colds.addItem("Milk", null, null);
        colds.addItem("Weissbier", null, null);

        // Another top-level item
        MenuBar.MenuItem snacks = barmenu.addItem("Snacks", null, null);
        snacks.addItem("Weisswurst", null, null);
        snacks.addItem("Bratwurst", null, null);
        snacks.addItem("Currywurst", null, null);
    }
}
