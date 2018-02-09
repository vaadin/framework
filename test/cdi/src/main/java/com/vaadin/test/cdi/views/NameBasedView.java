package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByName;

@CDIView(value = "name")
@ViewContextByName
public class NameBasedView extends GreetingView {
}
