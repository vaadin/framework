package com.vaadin.tests.components.urifragmentutilty;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

public class InitialFragmentEvent extends TestBase {

    private String lastKnownFragment = "\"no event received\"";
    private Log log = new Log(5);
    private UriFragmentUtility frag;

    @Override
    protected void setup() {
        frag = new UriFragmentUtility();
        frag.addListener(new FragmentChangedListener() {

            public void fragmentChanged(FragmentChangedEvent source) {
                String newFragment = source.getUriFragmentUtility()
                        .getFragment();
                log.log("Fragment changed from " + lastKnownFragment + " to "
                        + newFragment);
                lastKnownFragment = newFragment;
            }
        });
        addComponent(log);
        addComponent(frag);
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
        frag.setFragment(fragment);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
