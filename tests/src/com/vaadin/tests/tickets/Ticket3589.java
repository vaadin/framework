package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket3589 extends Application {

    @Override
    public void init() {
        final Window mainWindow = new Window("Test");
        setMainWindow(mainWindow);

        for (final String script : new String[] { "alert('foo');",
                "window.print()", "document.write('foo')" }) {
            Panel p = new Panel("Example: " + script);
            p.addComponent(new Button("Run javascript",
                    new Button.ClickListener() {

                        public void buttonClick(ClickEvent event) {
                            mainWindow.executeJavaScript(script);
                        }
                    }));
            mainWindow.addComponent(p);
        }

        final String script = "$1.style.backgroundColor='yellow';$2.style.borderColor='red';";
        Panel p = new Panel("Example: " + script);
        final Label label = new Label("Label");
        final TextField textfield = new TextField("TestField");
        p.addComponent(label);
        p.addComponent(textfield);
        p.addComponent(new Button("Run javascript", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                mainWindow.executeJavaScript(script, label, textfield);
            }
        }));
        mainWindow.addComponent(p);

        final String script2 = "var w = window.open(); w.document.write($1.outerHTML); w.print();";
        final Panel p2 = new Panel("Example: " + script2);
        p2.addComponent(new Label("Only this panel will be printed..."));
        p2.addComponent(new Button("Run javascript",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        mainWindow.executeJavaScript(script2, p2);
                    }
                }));
        mainWindow.addComponent(p2);

    }
}
