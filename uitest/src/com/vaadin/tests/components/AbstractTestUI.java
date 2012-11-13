package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractTestUI extends UI {

    @Override
    public void init(VaadinRequest request) {
        getPage().setTitle(getClass().getName());

        Label label = new Label(getTestDescription(), ContentMode.HTML);
        label.setWidth("100%");

        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setMargin(true);
        setContent(rootLayout);

        layout = new VerticalLayout();

        rootLayout.addComponent(label);
        rootLayout.addComponent(layout);
        ((VerticalLayout) getContent()).setExpandRatio(layout, 1);

        setup(request);
    }

    private VerticalLayout layout;

    protected VerticalLayout getLayout() {
        return layout;
    }

    protected abstract void setup(VaadinRequest request);

    public void addComponent(Component c) {
        getLayout().addComponent(c);
    }

    public void removeComponent(Component c) {
        getLayout().removeComponent(c);
    }

    public void replaceComponent(Component oldComponent, Component newComponent) {
        getLayout().replaceComponent(oldComponent, newComponent);
    }

    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        return getSession().getBrowser();
    }

}
