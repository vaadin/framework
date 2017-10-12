package com.example.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;

@SpringUI
public class RootPathUI extends AbstractSpringUI {

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Label label = new Label("RootPathUI");
        label.setId("rootpath");
        navigationBar.addComponent(label);
    }
}