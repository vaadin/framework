package com.vaadin.test.cdi.views;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewContextStrategy.Always;
import com.vaadin.navigator.View;
import com.vaadin.test.cdi.ThankYouService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

@CDIView(value = "", contextStrategy = Always.class)
public class DefaultView implements View {

    @Inject
    private ThankYouService service;

    @Override
    public Component getViewComponent() {
        return new Button("Click Me",
                e -> Notification.show(service.getText()));
    }
}
