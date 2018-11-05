package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20simple%20component,
 * https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20simple%20component,
 * https://vaadin.com/wiki/-/wiki/Main/Sending%
 * 20events%20from%20the%20client%20to%20the%20server%20using%20RPC,
 * https://vaadin
 * .com/wiki/-/wiki/Main/Using%20RPC%20to%20send%20events%20to%20the%20client
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class MyComponentUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        MyComponent component = new MyComponent();

        component.setText("My component text");

        setContent(component);
    }

}
