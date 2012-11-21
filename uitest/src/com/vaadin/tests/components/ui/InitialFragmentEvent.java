package com.vaadin.tests.components.ui;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class InitialFragmentEvent extends AbstractTestUI {

    private String lastKnownFragment = "\"no event received\"";
    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        getPage().addUriFragmentChangedListener(
                new UriFragmentChangedListener() {

                    @Override
                    public void uriFragmentChanged(
                            UriFragmentChangedEvent source) {
                        String newFragment = source.getUriFragment();
                        log.log("Fragment changed from " + lastKnownFragment
                                + " to " + newFragment);
                        lastKnownFragment = newFragment;
                    }
                });
        addComponent(log);
        addComponent(new Button("Set fragment to 'foo'",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        setFragment("foo");
                    }
                }));
        addComponent(new Button("Set fragment to 'bar'",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        setFragment("bar");
                    }
                }));
    }

    protected void setFragment(String fragment) {
        getPage().setUriFragment(fragment);
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

}
