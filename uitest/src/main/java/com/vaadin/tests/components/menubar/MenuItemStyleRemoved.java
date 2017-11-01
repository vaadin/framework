package com.vaadin.tests.components.menubar;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.MenuBar;

public class MenuItemStyleRemoved extends AbstractReindeerTestUI {

    protected static final String MENUITEM_CLASS = "v-menubar-menuitem";

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();

        MenuBar.MenuItem first = menuBar.addItem("first", null, null);
        first.addItem("first sub-item 1", null, null);
        first.addItem("first sub-item 2", null, null);
        MenuBar.MenuItem second = menuBar.addItem("second", null, null);
        second.addItem("second sub-item 2", null, null);
        second.addItem("second sub-item 2", null, null);

        addComponent(menuBar);
        addButton("Add styles", event -> {
            String method = "getElementsByClassName('" + MENUITEM_CLASS + "')";
            WebBrowser webBrowser = Page.getCurrent().getWebBrowser();
            if (webBrowser.isIE() && webBrowser.getBrowserMajorVersion() == 8) {
                method = "querySelectorAll('." + MENUITEM_CLASS + "')";
            }
            JavaScript.getCurrent()
                    .execute("var x=document." + method + ";"
                            + " var i; for (i=0; i < x.length; i++)"
                            + " {x[i].className += ' custom-menu-item'};");
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 17242;
    }

    @Override
    protected String getTestDescription() {
        return "MenuItem's custom class names removed when hovering";
    }
}
