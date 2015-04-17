package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.MenuBar;

public class MenuItemStyleRemoved extends AbstractTestUI {

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
        addButton("Add styles", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                JavaScript.getCurrent().execute(
                        "var x=document.getElementsByClassName('v-menubar-menuitem');" +
                        " var i; for(i=0; i < x.length; i++)" +
                        " {x[i].className += ' custom-menu-item'};");
            }
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 17242;
    }

    @Override
    public String getDescription() {
        return "MenuItem's custom class names removed when hovering";
    }
}
