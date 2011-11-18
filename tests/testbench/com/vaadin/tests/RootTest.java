/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

public class RootTest extends Root {
    @Override
    public void init(WrappedRequest request) {
        getContent().addComponent(new Label("Hello root"));
    }
}
