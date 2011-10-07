package com.vaadin.tests.integration;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;

public class FlagSeResource extends ClassResource {

    public FlagSeResource(Application application) {
        super("/"
                + FlagSeResource.class
                        .getName()
                        .replace('.', '/')
                        .replaceAll(FlagSeResource.class.getSimpleName() + "$",
                                "") + "/se.gif", application);
    }
}
