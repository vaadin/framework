/**
 * 
 */
package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

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

        OrderedLayout main = new OrderedLayout();
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
                p.setLayout(new GridLayout());
                p2.setLayout(new GridLayout());
                p3.setLayout(new GridLayout());
            }

        });
        main.addComponent(b);
        b = new Button("use orderedlayout", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                p.setLayout(new OrderedLayout());
                p2.setLayout(new OrderedLayout());
                p3.setLayout(new OrderedLayout());
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
                    getWindow().showNotification("ERROR",
                            "This should have failed",
                            Window.Notification.TYPE_ERROR_MESSAGE);
                } catch (Exception e) {
                    getWindow().showNotification("OK", "threw, as expected",
                            Window.Notification.TYPE_ERROR_MESSAGE);
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
                    getWindow().showNotification("ERROR",
                            "This should have failed",
                            Window.Notification.TYPE_ERROR_MESSAGE);
                } catch (Exception e) {
                    getWindow().showNotification("OK", "threw, as expected",
                            Window.Notification.TYPE_ERROR_MESSAGE);
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
