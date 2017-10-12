package com.example.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.ThankYouService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";

    @Autowired
    private ThankYouService service;

    @PostConstruct
    void init() {
        setId("default-view");
        Button button = new Button("Click Me!",
                e -> Notification.show(service.getText()));
        addComponent(button);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}