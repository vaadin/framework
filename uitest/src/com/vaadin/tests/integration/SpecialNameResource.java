package com.vaadin.tests.integration;

import com.vaadin.server.ClassResource;

public class SpecialNameResource extends ClassResource {
    public SpecialNameResource() {
        super("spe=cial%res&ource.gif");
    }
}
