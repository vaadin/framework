package com.vaadin.tests.tickets;

import java.util.Map;

import com.vaadin.Application;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket1921 extends Application implements ParameterHandler {

    int state = -1;
    int round = 1;
    Button button;
    VerticalLayout outer, inner;

    @Override
    public void init() {

        outer = new VerticalLayout();
        setMainWindow(new Window("#1921", outer));
        setTheme("tests-tickets");
        inner = new VerticalLayout();
        outer.addComponent(inner);
        button = new Button("foo", this, "newState");
        inner.addComponent(button);

        outer.setStyleName("red");
        inner.setStyleName("blue");

        newState();

        getMainWindow().addParameterHandler(this);
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

    public void handleParameters(Map<String, String[]> parameters) {
        String[] s = parameters.get("state");
        if (s == null || s.length != 1) {
            return;
        }
        String v[] = s[0].split("\\.");
        if (v == null || v.length != 2) {
            return;
        }
        try {
            int rr = Integer.parseInt(v[0]);
            int rs = Integer.parseInt(v[1]);
            if (rr < round || (rr == round && rs < state)) {
                getMainWindow().showNotification(
                        "Already past requested " + s[0]);
                return;
            }
            while (round < rr || state < rs) {
                newState();
            }
        } catch (NumberFormatException ignored) {
        }
    }
}
