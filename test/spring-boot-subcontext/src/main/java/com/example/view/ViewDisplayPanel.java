package com.example.view;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

@UIScope
@SpringViewDisplay
public class ViewDisplayPanel extends Panel implements ViewDisplay {

    @PostConstruct
    void init() {
        setSizeFull();
    }

    @Override
    public void showView(View view) {
        setContent((Component) view);
    }

}
