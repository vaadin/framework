package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.ViewContextStrategy.Always;

@UIScoped
@CDIView(value = "persisting", contextStrategy = Always.class)
public class UIScopedView extends GreetingView {
}
