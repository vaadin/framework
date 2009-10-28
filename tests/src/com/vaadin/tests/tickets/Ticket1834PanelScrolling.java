package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket1834PanelScrolling extends com.vaadin.Application {

    private static final int ROWS = 50;

    private Label state = new Label("State");

    private Panel p;

    @Override
    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        OrderedLayout currentState = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        currentState.addComponent(state);
        Button b = new Button("update");
        currentState.addComponent(b);
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                state.setValue("ScrollTop: " + p.getScrollTop()
                        + " ScrollLeft: " + p.getScrollLeft());
            }
        });
        main.addComponent(currentState);

        b = new Button("ScrollBy 50px");
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                p.setScrollLeft(p.getScrollLeft() + 50);
                p.setScrollTop(p.getScrollTop() + 50);
                state.setValue("ScrollTop: " + p.getScrollTop()
                        + " ScrollLeft: " + p.getScrollLeft());
            }
        });

        main.addComponent(b);

        b = new Button("Add row");
        b.addListener(new ClickListener() {
            int i = 0;

            public void buttonClick(ClickEvent event) {
                p.addComponent(new Label("new Row" + ++i));
            }
        });

        main.addComponent(b);

        b = new Button("Repaint Panel");
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                p.requestRepaint();
            }
        });

        main.addComponent(b);

        p = new Panel("TestPanel");
        p.setScrollable(true);

        for (int i = 0; i < ROWS; i++) {
            p
                    .addComponent(new Label(
                            "Label"
                                    + i
                                    + "................................................................................................................."));
        }

        p.setHeight("300px");
        p.setWidth("250px");

        p.setScrollTop(100);
        p.setScrollLeft(100);

        main.addComponent(p);

    }
}