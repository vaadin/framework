package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20Components%
 * 20in%20the%20shared%20state
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class ComponentInStateRoot extends UI {
    @Override
    protected void init(WrappedRequest request) {
        ComponentInStateComponent component = new ComponentInStateComponent();
        component.setOtherComponent(this);
        addComponent(component);
        addComponent(new Label("Server-side type of other component: "
                + component.getOtherComponent().getClass().getName()));
    }
}
