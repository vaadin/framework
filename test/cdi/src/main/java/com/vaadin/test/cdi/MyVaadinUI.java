package com.vaadin.test.cdi;

import javax.inject.Inject;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@Theme("valo")
@CDIUI("")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    @Inject
    private ThankYouService service;

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Button("Click Me",
                e -> Notification.show(service.getText())));
    }

}
