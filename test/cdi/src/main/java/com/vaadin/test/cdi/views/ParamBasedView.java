package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.viewcontextstrategy.ViewContextByNameAndParameters;

@CDIView(value = "param")
@ViewContextByNameAndParameters
public class ParamBasedView extends GreetingView {
}
