/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.extensions;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class HelloWorldExtensionTest extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        final HelloWorldExtension extension = new HelloWorldExtension();
        extension.setGreeting("Kind words");
        addExtension(extension);

        addComponent(new Button("Greet again", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                extension.greetAgain();
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Testing basic Extension";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
