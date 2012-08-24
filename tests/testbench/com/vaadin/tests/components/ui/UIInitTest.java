/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.components.ui;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class UIInitTest extends AbstractTestUI {

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new Label("Hello UI"));
    }

    @Override
    public String getTestDescription() {
        return "Testing basic UI creation";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(3067);
    }
}
