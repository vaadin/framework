package com.vaadin.tests.components.ui;

import com.vaadin.server.Page.FragmentChangedEvent;
import com.vaadin.server.Page.FragmentChangedListener;
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
        getPage().addFragmentChangedListener(new FragmentChangedListener() {

            public void fragmentChanged(FragmentChangedEvent source) {
                String newFragment = source.getFragment();
                log.log("Fragment changed from " + lastKnownFragment + " to "
                        + newFragment);
                lastKnownFragment = newFragment;
            }
        });
        addComponent(log);
        addComponent(new Button("Set fragment to 'foo'",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        setFragment("foo");
                    }
                }));
        addComponent(new Button("Set fragment to 'bar'",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        setFragment("bar");
                    }
                }));
    }

    protected void setFragment(String fragment) {
        getPage().setFragment(fragment);
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
