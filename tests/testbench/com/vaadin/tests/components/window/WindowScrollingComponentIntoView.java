package com.vaadin.tests.components.window;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowScrollingComponentIntoView extends AbstractTestCase {

    @Override
    protected String getDescription() {
        return "Scroll down, click 'up' and the view should scroll to the top";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4206;
    }

    @Override
    public void init() {
        Table table = new Table();
        table.setPageLength(50);

        setMainWindow(new Root(""));
        getMainWindow().getContent().setSizeUndefined();

        Component l2 = null;
        for (int i = 0; i < 10; i++) {
            l2 = l("X" + i);
            getMainWindow().addComponent(l2);
        }

        final Component x9 = l2;

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Component l = null;
        for (int i = 0; i < 10; i++) {
            l = l("Y" + i);
            horizontalLayout.addComponent(l);
        }

        getMainWindow().addComponent(horizontalLayout);
        final Component y9 = l;

        final Window window = new Window();
        window.setHeight("500px");
        window.setWidth("500px");
        window.setPositionX(200);
        window.setPositionY(200);

        window.addComponent(new Button("Scroll mainwin to X9",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getMainWindow().scrollIntoView(x9);

                    }
                }));
        window.addComponent(new Button("Scroll mainwin to Y9",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        getMainWindow().scrollIntoView(y9);

                    }
                }));

        Panel panel = new Panel("scrollable panel");
        panel.setHeight(400, Panel.UNITS_PIXELS);
        panel.setScrollable(true);
        panel.setScrollLeft(50);
        panel.setScrollTop(50);
        panel.getContent().setSizeUndefined();
        window.addComponent(l("Spacer", 500, 500));

        l2 = null;
        for (int i = 0; i < 10; i++) {
            l2 = l("X" + i);
            panel.addComponent(l2);
        }

        final Component x29 = l2;

        horizontalLayout = new HorizontalLayout();

        l = null;
        for (int i = 0; i < 10; i++) {
            l = l("Y" + i);
            horizontalLayout.addComponent(l);
        }
        panel.addComponent(horizontalLayout);
        final Component y29 = l;

        ((VerticalLayout) getMainWindow().getContent()).addComponent(
                new Button("Scroll win to X9", new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        throw new RuntimeException("Currently not implemented");
                        // window.scrollIntoView(x29);
                    }
                }), 0);
        ((VerticalLayout) getMainWindow().getContent()).addComponent(
                new Button("Scroll win to Y9", new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        throw new RuntimeException("Currently not implemented");
                        // window.scrollIntoView(y29);
                    }
                }), 0);

        window.addComponent(panel);
        getMainWindow().addWindow(window);

    }

    private Component l(String string) {
        return l(string, 200, 350);
    }

    private Component l(String string, int h, int w) {
        Label label = new Label(string);
        label.setHeight(h, Label.UNITS_PIXELS);
        label.setWidth(w, Label.UNITS_PIXELS);
        return label;
    }
}
