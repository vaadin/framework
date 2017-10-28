package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;

public class InitialFragmentEvent extends AbstractTestUIWithLog {

    private String lastKnownFragment = "\"no event received\"";

    @Override
    protected void setup(VaadinRequest request) {
        getPage().addUriFragmentChangedListener(
                event -> {
                    String newFragment = event.getUriFragment();
                    log("Fragment changed from " + lastKnownFragment + " to "
                            + newFragment);
                    lastKnownFragment = newFragment;
                });
        addButton("Set fragment to 'foo'", event -> setFragment("foo"));
        addButton("Set fragment to 'bar'", event -> setFragment("bar"));
    }

    protected void setFragment(String fragment) {
        getPage().setUriFragment(fragment);
    }

    @Override
    protected Integer getTicketNumber() {
        return 9558;
    }

    @Override
    protected String getTestDescription() {
        return "URI fragment handling should fire for initial fragment change";
    }

}
