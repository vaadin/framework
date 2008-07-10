package com.itmill.toolkit.tests.tickets;

import java.util.Map;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket1921 extends Application implements ParameterHandler {

    int state = -1;
    int round = 1;
    Button button;
    OrderedLayout outer, inner;

    public void init() {

        outer = new OrderedLayout();
        setMainWindow(new Window("#1921", outer));
        setTheme("tests-tickets");
        inner = new OrderedLayout();
        outer.addComponent(inner);
        button = new Button("foo", this, "newState");
        inner.addComponent(button);

        outer.setStyleName("red");
        inner.setStyleName("blue");

        newState();

        getMainWindow().addParameterHandler(this);
    }

    public void newState() {

        if (state >= 8) {
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
            inner
                    .setOrientation(inner.getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL ? OrderedLayout.ORIENTATION_VERTICAL
                            : OrderedLayout.ORIENTATION_HORIZONTAL);
            getMainWindow()
                    .showNotification(
                            "inner swithed to "
                                    + (inner.getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL ? "horizontal"
                                            : "vertical"));
            break;

        case 7:
            outer.addComponent(new Label("Added at " + button.getCaption()));
            break;

        case 8:
            outer
                    .setOrientation(outer.getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL ? OrderedLayout.ORIENTATION_VERTICAL
                            : OrderedLayout.ORIENTATION_HORIZONTAL);
            getMainWindow()
                    .showNotification(
                            "outer swithed to "
                                    + (outer.getOrientation() == OrderedLayout.ORIENTATION_HORIZONTAL ? "horizontal"
                                            : "vertical"));
            break;
        }
    }

    public void handleParameters(Map parameters) {
        String[] s = (String[]) parameters.get("state");
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
