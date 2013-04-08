package com.vaadin.tests.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

public class AlwaysLockedVaadinSession extends VaadinSession {

    private ReentrantLock lock;

    public AlwaysLockedVaadinSession(VaadinService service) {
        super(service);
        lock = new ReentrantLock();
        lock.lock();
    }

    @Override
    public Lock getLockInstance() {
        return lock;
    }
}
