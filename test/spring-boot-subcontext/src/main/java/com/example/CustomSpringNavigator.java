package com.example;

import org.springframework.stereotype.Component;

import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.UI;

@UIScope
@Component
public class CustomSpringNavigator extends SpringNavigator {

    @Override
    public void init(UI ui, ViewDisplay display) {
        // FIXME: Should be in Spring plug-in
        init(ui, null, display);
    }
}
