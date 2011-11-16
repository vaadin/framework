/**
 * 
 */
package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

/**
 * @author marc
 * 
 */
public class TestComponentAddAndRecursion extends CustomComponent {
    Panel p;
    Panel p2;
    Label l;
    Label l2;
    Panel p3;

    public TestComponentAddAndRecursion() {

        VerticalLayout main = new VerticalLayout();
        setCompositionRoot(main);

        l = new Label("A");
        l2 = new Label("B");
        p = new Panel("p");
        p.addComponent(l);
        p.addComponent(l2);
        main.addComponent(p);
        p2 = new Panel("p2");
        p2.addComponent(l);
        main.addComponent(p2);
        p3 = new Panel("p3");
        p2.addComponent(p3);

        Button b = new Button("use gridlayout", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                p.setContent(new GridLayout());
                p2.setContent(new GridLayout());
                p3.setContent(new GridLayout());
            }

        });
        main.addComponent(b);
        b = new Button("use orderedlayout", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                p.setContent(new VerticalLayout());
                p2.setContent(new VerticalLayout());
                p3.setContent(new VerticalLayout());
            }

        });
        main.addComponent(b);
        b = new Button("move B", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                p2.addComponent(l2);
            }

        });
        main.addComponent(b);
        b = new Button("move p", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                p3.addComponent(p);
            }

        });
        main.addComponent(b);
        b = new Button("add to both", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                Label l = new Label("both");
                p.addComponent(l);
                p2.addComponent(l);
            }

        });
        main.addComponent(b);
        b = new Button("recurse", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    p3.addComponent(p2);
                    Root.getCurrentRoot().showNotification("ERROR",
                            "This should have failed",
                            Notification.TYPE_ERROR_MESSAGE);
                } catch (Exception e) {
                    Root.getCurrentRoot().showNotification("OK",
                            "threw, as expected",
                            Notification.TYPE_ERROR_MESSAGE);
                }
            }

        });
        main.addComponent(b);
        b = new Button("recurse2", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                Panel p = new Panel("dynamic");
                p.addComponent(p2);
                try {
                    p3.addComponent(p);
                    Root.getCurrentRoot().showNotification("ERROR",
                            "This should have failed",
                            Notification.TYPE_ERROR_MESSAGE);
                } catch (Exception e) {
                    Root.getCurrentRoot().showNotification("OK",
                            "threw, as expected",
                            Notification.TYPE_ERROR_MESSAGE);
                }
            }

        });
        main.addComponent(b);
        /*
         * And that's it! The framework will display the main window and its
         * contents when the application is accessed with the terminal.
         */
    }
}
