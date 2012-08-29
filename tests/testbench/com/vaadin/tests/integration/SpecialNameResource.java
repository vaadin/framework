package com.vaadin.tests.integration;

import com.vaadin.Application;
import com.vaadin.server.ClassResource;

public class SpecialNameResource extends ClassResource {
    public SpecialNameResource(Application application) {
        super("spe=cial%res&ource.gif", application);
    }
}
