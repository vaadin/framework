package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;

public class PanelConcurrentModificationException extends TestBase {

    private final ComponentContainer panel = new Panel();

    @Override
    protected void setup() {
        addComponent(new Button("Click here for exception",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.addComponent(new Label("Label"));
                    }
                }));
        addComponent(new Button("Or click here first",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification
                                .show("It is now safe to click the other button");
                    }
                }));
        addComponent(panel);
    }

    @Override
    protected String getDescription() {
        return "Modifying Panel content causes Internal Error (ConcurrentModificationException)";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
