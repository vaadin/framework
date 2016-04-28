package com.vaadin.tests.components.ui;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class InitialFragmentEvent extends AbstractTestUIWithLog {

    private String lastKnownFragment = "\"no event received\"";

    @Override
    protected void setup(VaadinRequest request) {
        getPage().addUriFragmentChangedListener(
                new UriFragmentChangedListener() {

                    @Override
                    public void uriFragmentChanged(
                            UriFragmentChangedEvent source) {
                        String newFragment = source.getUriFragment();
                        log("Fragment changed from " + lastKnownFragment
                                + " to " + newFragment);
                        lastKnownFragment = newFragment;
                    }
                });
        addButton("Set fragment to 'foo'", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setFragment("foo");
            }
        });
        addButton("Set fragment to 'bar'", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                setFragment("bar");
            }
        });
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
