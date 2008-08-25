package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket677 extends Application {

    private static final Object P1 = new Object();
    private static final Object P2 = new Object();
    private static final Object P3 = new Object();

    private Panel panel;
    private Form form;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-ticket");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        createPanel(layout);
        createForm(layout);

        layout.addComponent(new Button("Enable", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                panel.setEnabled(true);
                form.setEnabled(true);
            }

        }));

        layout.addComponent(new Button("Disable", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                panel.setEnabled(false);
                form.setEnabled(false);
            }

        }));

    }

    private void createForm(GridLayout layout) {
        form = new Form();
        form.addField(P1, new TextField());
        form.addField(P2, new DateField());
        form.addField(P3, new DateField());

        layout.addComponent(form);
    }

    private void createPanel(GridLayout layout) {
        panel = new Panel("panel caption");
        layout.addComponent(panel);

        panel.addComponent(new Label("Label 1"));

        OrderedLayout innerLayout1 = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        innerLayout1.setSpacing(true);
        panel.addComponent(innerLayout1);

        TextField tf = new TextField("TextField inside orderedLayout");
        tf.setImmediate(true);
        innerLayout1.addComponent(tf);
        innerLayout1.addComponent(new Button("Button inside orderedLayout",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        System.out.println("Clicked "
                                + event.getButton().getCaption());
                    }

                }));
        innerLayout1.addComponent(new Label("Label inside orderedLayout"));

        panel.addComponent(new Label("Label 2"));

        GridLayout innerLayout2 = new GridLayout(3, 3);
        innerLayout2.setSpacing(true);
        tf = new TextField("TextField inside gridLayout");
        tf.setImmediate(true);
        innerLayout2.addComponent(tf);
        innerLayout2.addComponent(new Button("Button inside gridLayout"));
        innerLayout2.addComponent(new Label("Label inside gridLayout"));
        panel.addComponent(innerLayout2);

    }
}
