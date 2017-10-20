package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewContextStrategy.Always;

@CDIView(value = "new", contextStrategy = Always.class)
public class AlwaysNewView extends GreetingView {
}
