/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20basic%20application
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class BasicApplication extends Root {

    @Override
    protected void init(WrappedRequest request) {
        VerticalLayout view = new VerticalLayout();
        view.addComponent(new Label("Hello Vaadin!"));
        setContent(view);
    }
}
