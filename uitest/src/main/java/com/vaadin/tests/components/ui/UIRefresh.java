package com.vaadin.tests.components.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

@PreserveOnRefresh
public class UIRefresh extends AbstractReindeerTestUI {

    public static final String REINIT_ID = "reinit";

    @Override
    protected void setup(VaadinRequest request) {
    }

    @Override
    protected void refresh(VaadinRequest request) {
        Label l = new Label("Reinit!");
        l.setId(REINIT_ID);
        addComponent(l);
    }

    @Override
    public String getTestDescription() {
        return "UI reinit after refresh";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12191);
    }
}
