package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public abstract class TestBase extends Application {

    @Override
    public final void init() {
        window = new Window(getClass().getName());
        setMainWindow(window);
        window.getContent().setSizeFull();

        Label label = new Label(getDescription(), Label.CONTENT_XHTML);
        label.setWidth("100%");
        window.getContent().addComponent(label);

        layout = new VerticalLayout();
        window.getContent().addComponent(layout);
        ((VerticalLayout) window.getContent()).setExpandRatio(layout, 1);

        setup();
    }

    private Window window;
    private Layout layout;

    public TestBase() {

    }

    protected Layout getLayout() {
        return layout;
    }

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

    protected abstract void setup();

    protected void addComponent(Component c) {
        getLayout().addComponent(c);
    }

}
