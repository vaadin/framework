/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.annotations.Theme;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Defining%20the%20theme%20for%20a%20Root
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Theme("hello-theme")
public class DefineRootTheme extends Root {

    @Override
    protected void init(WrappedRequest request) {
        VerticalLayout view = new VerticalLayout();
        view.addComponent(new Label("Hello Vaadin"));
        setContent(view);
    }

}
