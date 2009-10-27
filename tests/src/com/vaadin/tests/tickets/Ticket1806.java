package com.vaadin.tests.tickets;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket1806 extends com.vaadin.Application {

    @Override
    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        final ObjectProperty prop = new ObjectProperty("");
        final TextField tf1 = new TextField(
                "Buffered TextField bound to ObjectProperty");
        tf1.setWriteThrough(false);
        tf1.setReadThrough(false);
        tf1.setPropertyDataSource(prop);
        main.addComponent(tf1);
        main
                .addComponent(new Button(
                        "This button does nothing (but flushes queued variable changes)"));
        main.addComponent(new Button("Commit the field to property",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        tf1.commit();
                    }
                }));
        main.addComponent(new Button("Show property value",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        main.showNotification("'" + prop.getValue() + "'");
                    }
                }));
        main.addComponent(new Button("Show field value",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        main.showNotification("'" + tf1.getValue() + "'");
                    }
                }));
    }
}