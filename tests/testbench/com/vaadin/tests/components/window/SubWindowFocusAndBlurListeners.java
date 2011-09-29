package com.vaadin.tests.components.window;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

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

        final Window window = new Window("Focus test window");
        window.getContent().setSizeUndefined();

        window.addComponent(new TextField());
        window.addListener(new FocusListener() {
            public void focus(FocusEvent event) {
                event.getComponent().getWindow()
                        .showNotification("Focused window");
            }
        });

        window.addListener(new BlurListener() {
            public void blur(BlurEvent event) {
                event.getComponent().getWindow()
                        .showNotification("Blurred window");
            }
        });

        window.addActionHandler(new Handler() {

            private Action[] s = new Action[] { new ShortcutAction("^Save") };

            public Action[] getActions(Object target, Object sender) {
                return s;
            }

            public void handleAction(Action action, Object sender, Object target) {
                window.showNotification("Action!");
            }
        });

        Window main = getLayout().getWindow();

        main.addWindow(window);

        main.addComponent(new TextField());

        Button button = new Button("Bring to front (should focus too)",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        window.bringToFront();
                    }
                });
        main.addComponent(button);

        Window window2 = new Window("Another window for testing");
        main.addWindow(window2);
        window2.setPositionX(50);

    }
}
