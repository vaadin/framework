package com.vaadin.tests.extensions;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class HelloWorldExtensionTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HelloWorldExtension extension = new HelloWorldExtension();
        extension.setGreeting("Kind words");
        addExtension(extension);

        addComponent(
                new Button("Greet again", event -> extension.greetAgain()));
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
