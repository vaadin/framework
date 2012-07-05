/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.extensions;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class BasicExtensionTest extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        Label label = new Label();
        addComponent(label);

        new BasicExtension().extend(this);
        new BasicExtension().extend(label);
    }

    @Override
    protected String getTestDescription() {
        return "Simple test for extending components";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6690);
    }

}
