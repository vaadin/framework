package com.example.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.ViewGreeter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@UIScope
@SpringView(name = UIScopedView.VIEW_NAME)
public class UIScopedView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "ui-scoped";

    @Autowired
    ViewGreeter service;

    @PostConstruct
    void init() {
        setId(VIEW_NAME);
        setMargin(true);
        setSpacing(true);
        addComponents(new Label("This is a UI scoped view."),
                new Label(service.sayHello()));
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}