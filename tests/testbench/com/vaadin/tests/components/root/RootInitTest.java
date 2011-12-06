/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.components.root;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Label;

public class RootInitTest extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new Label("Hello root"));
    }

    @Override
    public String getTestDescription() {
        return "Testing basic root creation";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(3067);
    }
}
