/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.terminal.Page.FragmentChangedEvent;
import com.vaadin.terminal.Page.FragmentChangedListener;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20URI%20fragments
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class UsingUriFragments extends Root {

    @Override
    protected void init(WrappedRequest request) {
        Label label = new Label("Hello, your fragment is "
                + request.getBrowserDetails().getUriFragment());
        getContent().addComponent(label);

        // React to fragment changes
        addListener(new FragmentChangedListener() {
            public void fragmentChanged(FragmentChangedEvent source) {
                handleFragment(source.getFragment());
            }
        });

        // Handle the fragment received in the initial request
        handleFragment(request.getBrowserDetails().getUriFragment());

        addComponent(new Button("Show and set fragment",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        handleFragment(getFragment());
                        setFragment("customFragment");
                    }
                }));
    }

    private void handleFragment(String uriFragment) {
        addComponent(new Label("Got new fragment: " + uriFragment));
    }

}
