package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class WindowStyleNames extends TestBase {

    @Override
    protected String getDescription() {
        return "Click 'add style' to add a 'new' style to the window. The 'old' style should disappear and only the 'new' style should be set. Verify using e.g. firebug";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3059;
    }

    @Override
    protected void setup() {
        setWindowStyle("old");
        addComponent(new Button("Set style to 'new'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setWindowStyle("new");
            }

        }));

        addComponent(new Button("Set style to 'custom'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setWindowStyle("custom");
            }

        }));

        addComponent(new Button("Add 'foo' style", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().addStyleName("foo");
            }

        }));

    }

    protected void setWindowStyle(String string) {
        getMainWindow().setStyleName(string);

    }

}
