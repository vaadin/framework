package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2060 extends Application {

    private Button button1;
    private Button button2;
    private Button button3;

    @Override
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
