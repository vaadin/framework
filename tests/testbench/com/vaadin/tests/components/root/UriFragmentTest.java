package com.vaadin.tests.components.root;

import com.vaadin.terminal.Page;
import com.vaadin.terminal.Page.FragmentChangedEvent;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class UriFragmentTest extends AbstractTestRoot {

    private final Label fragmentLabel = new Label();

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(fragmentLabel);
        updateLabel();
        getPage().addListener(new Page.FragmentChangedListener() {
            @Override
            public void fragmentChanged(FragmentChangedEvent event) {
                updateLabel();
            }
        });
        addComponent(new Button("Navigate to #test",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getPage().setFragment("test");
                    }
                }));
    }

    private void updateLabel() {
        String fragment = getPage().getFragment();
        if (fragment == null) {
            fragmentLabel.setValue("No URI fragment set");
        } else {
            fragmentLabel.setValue("Current URI fragment: " + fragment);
        }
    }

    @Override
    public String getTestDescription() {
        return "URI fragment status should be known when the page is loaded and retained while navigating to different fragments or using the back and forward buttons.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8048);
    }

}
