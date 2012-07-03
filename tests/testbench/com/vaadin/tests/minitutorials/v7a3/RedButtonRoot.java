/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public class RedButtonRoot extends Root {
    @Override
    protected void init(WrappedRequest request) {
        addComponent(new RedButton("My red button"));
    }
}