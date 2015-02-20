package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class WebBrowserSize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Label screenSizeLabel = new Label("n/a");
        screenSizeLabel.setCaption("Screen size");

        final Label browserSizeLabel = new Label("n/a");
        browserSizeLabel.setCaption("Client (browser window) size");

        final Button update = new Button("Refresh", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                screenSizeLabel.setValue(getBrowser().getScreenWidth() + " x "
                        + getBrowser().getScreenHeight());
                browserSizeLabel.setValue(getPage().getBrowserWindowWidth()
                        + " x " + getPage().getBrowserWindowHeight());
            }
        });

        addComponent(update);
        addComponent(screenSizeLabel);
        addComponent(browserSizeLabel);

    }

    @Override
    protected String getTestDescription() {
        return "Verifies that browser sizes are reported correctly. Note that client width differs depending on browser decorations.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5655;
    }

}
