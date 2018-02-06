package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByNavigation;

@UIScoped
@CDIView(value = "persisting")
@ViewContextByNavigation
public class UIScopedView extends GreetingView {
}
