package com.vaadin.tests.util;

import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinService;

public class AlwaysLockedVaadinSession extends MockVaadinSession {

    public AlwaysLockedVaadinSession(VaadinService service) {
        super(service);
        lock();
    }

}
