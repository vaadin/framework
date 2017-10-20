package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewContextStrategy.ViewName;

@CDIView(value = "name", contextStrategy = ViewName.class)
public class NameBasedView extends GreetingView {
}
