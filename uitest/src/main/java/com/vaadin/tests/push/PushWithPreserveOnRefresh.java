package com.vaadin.tests.push;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Label;

@PreserveOnRefresh
@Push
public class PushWithPreserveOnRefresh extends AbstractReindeerTestUI {

    private Log log = new Log(5);
    private int times = 0;

    @Override
    protected void setup(VaadinRequest request) {
        // Internal parameter sent by vaadinBootstrap.js,
        addComponent(new Label("window.name: " + request.getParameter("v-wn")));
        addComponent(new Label("UI id: " + getUIId()));
        addComponent(log);

        addButton("click me", event -> log
                .log("Button has been clicked " + (++times) + " times"));
    }

    @Override
    protected String getTestDescription() {
        return "Refreshing the browser window should preserve the state and push should continue to work";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13620);
    }
}
