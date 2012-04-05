package com.vaadin.tests.components.customcomponent;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class CustomLayoutUsingTheme extends TestBase implements ClickListener {

    private CustomLayout layout;

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        layout = new CustomLayout("Ticket1775.html");
        addComponent(layout);
        layout.addComponent(new TextField("Username"), "loginUser");
        layout.addComponent(new TextField("Password"), "loginPassword");
        layout.addComponent(new Button("Login"), "loginButton");

        VerticalLayout menu = new VerticalLayout();
        menu.addComponent(new Button("Set main to label", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label(LoremIpsum.get(200)), "main");
            }
        }));
        menu.addComponent(new Button("Set main to huge NativeButton",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        layout.addComponent(new NativeButton(
                                "This is it, the main!"), "main");
                    }
                }));
        layout.addComponent(menu, "menu");
    }

    @Override
    protected String getDescription() {
        return "Test for using a CustomLayout with a template read from an input stream and passed through the state";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    public void buttonClick(ClickEvent event) {
        layout.addComponent(new TextField("A text field!"), "location2");
    }

}
