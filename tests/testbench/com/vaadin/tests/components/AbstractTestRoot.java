package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractTestRoot extends Root {

    @Override
    public void init(WrappedRequest request) {
        setCaption(getClass().getName());

        Label label = new Label(getDescription(), Label.CONTENT_XHTML);
        label.setWidth("100%");

        layout = new VerticalLayout();

        getContent().addComponent(label);
        getContent().addComponent(layout);
        ((VerticalLayout) getContent()).setExpandRatio(label, 1);

        setup();
    }

    private VerticalLayout layout;

    protected VerticalLayout getLayout() {
        return layout;
    }

    protected abstract void setup();

    @Override
    public void addComponent(Component c) {
        getLayout().addComponent(c);
    }

    @Override
    public void removeComponent(Component c) {
        getLayout().removeComponent(c);
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        getLayout().replaceComponent(oldComponent, newComponent);
    }

    public abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        ApplicationContext context = Application.getCurrentApplication()
                .getContext();
        if (context instanceof AbstractWebApplicationContext) {
            AbstractWebApplicationContext webContext = (AbstractWebApplicationContext) context;
            return webContext.getBrowser();
        }

        return null;
    }

}
