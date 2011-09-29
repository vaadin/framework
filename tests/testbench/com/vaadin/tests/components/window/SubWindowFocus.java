package com.vaadin.tests.components.window;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class SubWindowFocus extends TestBase {

    @Override
    protected String getDescription() {
        return "A subwindow that listens to ESC and SPACE can be opened. It "
                + "should receive focus and thus receive keyboard events when "
                + "anything within the window is clicked. It should be last in"
                + "the tabbing order. The window can only be closed using ESC.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3498;
    }

    @Override
    protected void setup() {

        // some fields with tabindex
        for (int i = 1; i < 4; i++) {
            TextField tf = new TextField();
            tf.setTabIndex(i);
            tf.setInputPrompt("Tab index " + i);
            addComponent(tf);
        }
        // field with tabindex 0
        TextField tf = new TextField();
        tf.setTabIndex(0);
        tf.setInputPrompt("Tab index 0");
        addComponent(tf);

        Button b = new Button("new", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                final Window win = new Window("Subwin");
                win.getContent().setWidth(null);
                win.center();
                win.setClosable(false);
                getMainWindow().addWindow(win);
                win.addComponent(new Label("SPACE notifies, ESC closes."));

                win.addActionHandler(new Action.Handler() {

                    ShortcutAction esc = new ShortcutAction("Close",
                            ShortcutAction.KeyCode.ESCAPE, null);
                    ShortcutAction spc = new ShortcutAction("Space",
                            ShortcutAction.KeyCode.SPACEBAR, null);

                    public Action[] getActions(Object target, Object sender) {
                        return new Action[] { esc, spc };
                    }

                    public void handleAction(Action action, Object sender,
                            Object target) {
                        if (action == esc) {
                            getMainWindow().removeWindow(win);
                        } else {
                            getMainWindow().showNotification(
                                    action.getCaption());
                        }

                    }

                });

                win.addComponent(new TextField());
            }

        });
        addComponent(b);

    }
}
