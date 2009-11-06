package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket3589 extends Application {

    @Override
    public void init() {
        final Window mainWindow = new Window("Test");
        setMainWindow(mainWindow);

        mainWindow.addComponent(new Button("alert('foo')",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        mainWindow.executeJavaScript("alert('foo');");
                    }
                }));

        final Label label = new Label("Label");
        final TextField textfield = new TextField("TestField");
        mainWindow.addComponent(label);
        mainWindow.addComponent(textfield);

        final String script = "$1.style.backgroundColor='yellow';$2.style.borderColor='red';";
        mainWindow.addComponent(new Button(script, new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                mainWindow.executeJavaScript(script, label, textfield);
            }
        }));

    }
}
