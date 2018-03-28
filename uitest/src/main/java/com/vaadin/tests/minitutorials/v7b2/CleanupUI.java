package com.vaadin.tests.minitutorials.v7b2;

import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class CleanupUI extends UI implements DetachListener {
    @Override
    protected void init(VaadinRequest request) {
        addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                releaseSomeResources();
            }
        });

        // ...
        addDetachListener(this);
    }

    @Override
    public void detach(DetachEvent event) {
        releaseMoreResources();
    }

    private void releaseSomeResources() {
        // ...
    }

    private void releaseMoreResources() {
        // ...
    }
}
