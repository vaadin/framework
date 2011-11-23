package com.vaadin.tests.requesthandlers;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.integration.FlagSeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Link;

public class AppResource404 extends TestBase {

    @Override
    protected void setup() {
        // Add one existing resource
        final FlagSeResource resource = new FlagSeResource(this);
        resource.setCacheTime(0);
        addResource(resource);

        addComponent(new Link("Existing resource", resource));
        addComponent(new Link("Non-existing resource", new ExternalResource(
                getURL().toString() + "APP/12341234/")));

        addComponent(new Button("Remove existing resrouce",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        removeResource(resource);
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Accessing the URL of a resource that has been removed or the URL following the resource URL pattern but not currently mapped to a resource should give a 404 error message";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6702);
    }

}
