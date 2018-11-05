package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByNavigation;

@CDIView(value = "new")
@ViewContextByNavigation
public class AlwaysNewView extends GreetingView {
}
