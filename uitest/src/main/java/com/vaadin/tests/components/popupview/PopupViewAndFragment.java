package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

public class PopupViewAndFragment extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final PopupView pw = new PopupView("Open", new Label("Oh, hi"));
        addComponent(pw);

        final Button button = new Button("Open and change fragment", event -> {
            pw.setPopupVisible(true);
            getPage()
                    .setUriFragment(String.valueOf(System.currentTimeMillis()));
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Changing frament should not automatically close PopupView";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10530;
    }

}
