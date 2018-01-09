package com.vaadin.test.cdi.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewContextStrategy.ViewNameAndParameters;

@CDIView(value = "param", contextStrategy = ViewNameAndParameters.class)
public class ParamBasedView extends GreetingView {
}
