package com.vaadin.tests.tickets;

import java.io.IOException;
import java.util.Map;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket1921 extends LegacyApplication implements RequestHandler {

    int state = -1;
    int round = 1;
    Button button;
    VerticalLayout outer, inner;

    @Override
    public void init() {

        outer = new VerticalLayout();
        setMainWindow(new LegacyWindow("#1921", outer));
        setTheme("tests-tickets");
        inner = new VerticalLayout();
        outer.addComponent(inner);
        button = new Button("foo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                newState();
            }
        });
        inner.addComponent(button);

        outer.setStyleName("red");
        inner.setStyleName("blue");

        newState();

        VaadinSession.getCurrent().addRequestHandler(this);
    }

    public void newState() {

        if (state >= 6) {
            state = 0;
            round++;
        } else {
            state++;
        }

        button.setCaption("state " + round + "." + state);

        switch (state) {

        case 0:
            outer.setMargin(true);
            inner.setMargin(true);
            inner.setSizeFull();
            outer.setSizeFull();
            button.setSizeFull();
            break;

        case 1:
            button.setSizeUndefined();
            break;

        case 2:
            inner.setMargin(false);
            break;

        case 3:
            outer.setMargin(false);
            break;

        case 4:
            inner.setMargin(true);
            break;

        case 5:
            inner.addComponent(new Label("Added at " + button.getCaption()));
            break;

        case 6:
            outer.addComponent(new Label("Added at " + button.getCaption()));
            break;

        }
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        Map<String, String[]> parameters = request.getParameterMap();
        String[] s = parameters.get("state");
        if (s == null || s.length != 1) {
            return false;
        }
        String v[] = s[0].split("\\.");
        if (v == null || v.length != 2) {
            return false;
        }
        try {
            int rr = Integer.parseInt(v[0]);
            int rs = Integer.parseInt(v[1]);
            if (rr < round || (rr == round && rs < state)) {
                getMainWindow().showNotification(
                        "Already past requested " + s[0]);
                return false;
            }
            while (round < rr || state < rs) {
                newState();
            }
        } catch (NumberFormatException ignored) {
        }
        return false;
    }
}
