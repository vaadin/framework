package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket1834PanelScrolling extends
        com.vaadin.server.LegacyApplication {

    private static final int ROWS = 50;

    private Label state = new Label("State");

    private Panel p;
    private VerticalLayout pl;

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        HorizontalLayout currentState = new HorizontalLayout();

        currentState.addComponent(state);
        Button b = new Button("update");
        currentState.addComponent(b);
        b.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                state.setValue("ScrollTop: " + p.getScrollTop()
                        + " ScrollLeft: " + p.getScrollLeft());
            }
        });
        main.addComponent(currentState);

        b = new Button("ScrollBy 50px");
        b.addListener(new ClickListener() {
            @Override
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

            @Override
            public void buttonClick(ClickEvent event) {
                pl.addComponent(new Label("new Row" + ++i));
            }
        });

        main.addComponent(b);

        b = new Button("Repaint Panel");
        b.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                p.markAsDirty();
            }
        });

        main.addComponent(b);

        pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("TestPanel", pl);

        for (int i = 0; i < ROWS; i++) {
            pl.addComponent(new Label(
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
