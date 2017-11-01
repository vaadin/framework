package com.vaadin.tests.components.ui;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public class UriFragment extends AbstractReindeerTestUI {

    private final Label fragmentLabel = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        fragmentLabel.setId("fragmentLabel");
        addComponent(fragmentLabel);
        updateLabel();
        getPage().addUriFragmentChangedListener(event -> updateLabel());

        addComponent(createButton("test", "Navigate to #test", "test"));
        addComponent(createButton("empty", "Navigate to #", ""));
        addComponent(createButton("null", "setUriFragment(null)", null));

        Link link = new Link("Navigate to #linktest",
                new ExternalResource("#linktest"));
        link.setId("link");
        addComponent(link);

    }

    private Button createButton(String id, String caption,
            final String fragment) {
        Button button = new Button(caption,
                event -> getPage().setUriFragment(fragment));

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
