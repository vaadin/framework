package com.vaadin.tests.components.ui;

import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class UriFragment extends AbstractTestUI {

    private final Label fragmentLabel = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        fragmentLabel.setId("fragmentLabel");
        addComponent(fragmentLabel);
        updateLabel();
        getPage().addListener(new Page.UriFragmentChangedListener() {
            @Override
            public void uriFragmentChanged(UriFragmentChangedEvent event) {
                updateLabel();
            }
        });

        addComponent(createButton("test", "Navigate to #test", "test"));
        addComponent(createButton("empty", "Navigate to #", ""));
        addComponent(createButton("null", "setUriFragment(null)", null));
    }

    private Button createButton(String id, String caption, final String fragment) {
        Button button = new Button(caption, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getPage().setUriFragment(fragment);
            }
        });

        button.setId(id);

        return button;
    }

    private void updateLabel() {
        String fragment = getPage().getUriFragment();
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
