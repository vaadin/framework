package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2060 extends Application {

    private Button button1;
    private Button button2;
    private Button button3;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        OrderedLayout buttonLayout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        button1 = new Button("Button which is 50px wide");
        button1.setWidth("50px");
        button2 = new Button("Button without width");
        button3 = new Button("Click to repaint buttons", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                button1.requestRepaint();
                button2.requestRepaint();
                button3.requestRepaint();

            }

        });

        buttonLayout.addComponent(button1);
        buttonLayout.addComponent(button2);
        buttonLayout.addComponent(button3);

        layout.addComponent(buttonLayout);

    }
}
