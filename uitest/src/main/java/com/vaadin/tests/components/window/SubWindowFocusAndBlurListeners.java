package com.vaadin.tests.components.window;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.TextField;

public class SubWindowFocusAndBlurListeners extends TestBase {

    @Override
    protected String getDescription() {
        return "Focus and blur listeners should work. Note the "
                + "side efect (focusing) when callintg bring to front.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5039;
    }

    @Override
    protected void setup() {

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        final Window window = new Window("Focus test window", layout);
        layout.setSizeUndefined();

        layout.addComponent(new TextField());
        window.addFocusListener(event -> Notification.show("Focused window"));

        window.addBlurListener(event -> Notification.show("Blurred window"));

        window.addActionHandler(new Handler() {

            private Action[] s = { new ShortcutAction("^Save") };

            @Override
            public Action[] getActions(Object target, Object sender) {
                return s;
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                Notification.show("Action!");
            }
        });

        UI main = getLayout().getUI();

        main.addWindow(window);

        ((ComponentContainer) main.getContent()).addComponent(new TextField());

        Button button = new Button("Bring to front (should focus too)",
                event -> window.bringToFront());
        ((ComponentContainer) main.getContent()).addComponent(button);

        Window window2 = new Window("Another window for testing");
        main.addWindow(window2);
        window2.setPositionX(50);

    }
}
