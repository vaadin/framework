package com.example.ui;

import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;

@PushStateNavigation
@SpringUI(path = SubPathUI.SUBPATH)
public class SubPathUI extends AbstractSpringUI {

    public static final String SUBPATH = "subpath";

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Label label = new Label("SubPathUI");
        label.setId(SUBPATH);
        navigationBar.addComponent(label);
    }
}