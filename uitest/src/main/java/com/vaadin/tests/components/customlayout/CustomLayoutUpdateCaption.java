package com.vaadin.tests.components.customlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class CustomLayoutUpdateCaption extends UI {
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        CustomLayout content = new CustomLayout();
        content.setTemplateContents("<div>\n"
                + "        <div location=\"test1\"></div>\n"
                + "        <div location=\"test2\"></div>\n"
                + "        <div location=\"okbutton\"></div>\n" + "</div>");
        content.setSizeUndefined();
        setContent(content);

        Button loginButton = new Button("Test");
        final TextField username1 = new TextField();
        final TextField username2 = new TextField();
        username1.setCaption("initial");
        username2.setCaption("initial");
        content.addComponent(username1, "test1");
        content.addComponent(new VerticalLayout(username2), "test2");
        content.addComponent(loginButton, "okbutton");

        loginButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                username1.setCaption("updated");
                username2.setCaption("updated");
            }
        });
    }
}
