package com.itmill.toolkit.tests.tickets;

import java.util.UUID;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2014 extends Application {

    private OrderedLayout innerLayout1;
    private Button b1;
    private Panel panel;

    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);
        // setTheme("tests-ticket");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        createPanel(layout);

        layout.addComponent(new Button("Change class name",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        b1.setStyleName(UUID.randomUUID().toString());
                    }

                }));

    }

    private void createPanel(GridLayout layout) {
        panel = new Panel("panel caption");
        layout.addComponent(panel);

        innerLayout1 = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        innerLayout1.setSpacing(true);
        panel.addComponent(innerLayout1);

        b1 = new Button("Button inside orderedLayout", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                System.out.println("Clicked " + event.getButton().getCaption());
            }

        });

        innerLayout1.addComponent(b1);

    }
}
