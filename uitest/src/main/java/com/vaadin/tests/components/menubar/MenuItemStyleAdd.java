package com.vaadin.tests.components.menubar;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class MenuItemStyleAdd extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar barmenu = new MenuBar();
        MenuBar.MenuItem more = barmenu.addItem("More", null, null);
        more.setStyleName("styleNameMore");
        barmenu.setMoreMenuItem(more);

        MenuBar.MenuItem drinks = barmenu.addItem("Drinks", null, null);
        MenuBar.MenuItem hots = drinks.addItem("Hot", null, null);
        hots.addItem("Tea", null);
        hots.addItem("Coffee", null);
        hots.setStyleName("styleTest");

        MenuBar.MenuItem colds = drinks.addItem("Cold", null, null);
        colds.addItem("Milk", null);
        colds.addItem("Weissbier", null);

        addComponent(barmenu);
    }

}
