package com.vaadin.tests.integration;

import com.vaadin.server.ClassResource;

public class FlagSeResource extends ClassResource {

    public FlagSeResource() {
        super("/"
                + FlagSeResource.class
                        .getName()
                        .replace('.', '/')
                        .replaceAll(FlagSeResource.class.getSimpleName() + "$",
                                "") + "se.gif");

    }
}
