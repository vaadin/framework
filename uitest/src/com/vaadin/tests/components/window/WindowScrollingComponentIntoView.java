package com.vaadin.tests.components.window;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
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

        setMainWindow(new LegacyWindow(""));
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

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        final Window window = new Window();
        window.setHeight("500px");
        window.setWidth("500px");
        window.setPositionX(200);
        window.setPositionY(200);

        layout.addComponent(new Button("Scroll mainwin to X9",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getMainWindow().scrollIntoView(x9);

                    }
                }));
        layout.addComponent(new Button("Scroll mainwin to Y9",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getMainWindow().scrollIntoView(y9);

                    }
                }));

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel("scrollable panel", panelLayout);
        panel.setHeight(400, Panel.UNITS_PIXELS);
        panel.setScrollLeft(50);
        panel.setScrollTop(50);
        panelLayout.setSizeUndefined();
        layout.addComponent(l("Spacer", 500, 500));

        l2 = null;
        for (int i = 0; i < 10; i++) {
            l2 = l("X" + i);
            panelLayout.addComponent(l2);
        }

        final Component x29 = l2;

        horizontalLayout = new HorizontalLayout();

        l = null;
        for (int i = 0; i < 10; i++) {
            l = l("Y" + i);
            horizontalLayout.addComponent(l);
        }
        panelLayout.addComponent(horizontalLayout);
        final Component y29 = l;

        ((VerticalLayout) getMainWindow().getContent()).addComponent(
                new Button("Scroll win to X9", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        throw new RuntimeException("Currently not implemented");
                        // window.scrollIntoView(x29);
                    }
                }), 0);
        ((VerticalLayout) getMainWindow().getContent()).addComponent(
                new Button("Scroll win to Y9", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        throw new RuntimeException("Currently not implemented");
                        // window.scrollIntoView(y29);
                    }
                }), 0);

        layout.addComponent(panel);
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
