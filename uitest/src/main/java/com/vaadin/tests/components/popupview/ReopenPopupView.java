package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.PopupView;

public class ReopenPopupView extends AbstractReindeerTestUI {
    private final Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        addComponent(new PopupView("PopupView",
                new Button("Button", event -> log.log("Button clicked"))));
    }

    @Override
    protected String getTestDescription() {
        return "Clicking a button in a PopupView should work every time";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8804);
    }

}
