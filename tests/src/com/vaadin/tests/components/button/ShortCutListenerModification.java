package com.vaadin.tests.components.button;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ShortCutListenerModification extends TestBase implements
        ClickListener {

    @Override
    protected String getDescription() {
        return "Modifiying listeners in shortcuthandler should succeed. Hitting CTRL-C should close windows one by one.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5350;
    }

    @Override
    protected void setup() {

        Button prev = null;

        for (int j = 0; j < 20; j++) {

            Window window = new Window();
            getMainWindow().addWindow(window);

            Button button1 = new Button("b1 (CTRL-C)");
            Button button2 = new Button("b2 (CTRL-V)");

            button1.addListener(this);
            button2.addListener(this);

            button1.setClickShortcut(KeyCode.C, ModifierKey.CTRL);
            button2.setClickShortcut(KeyCode.V, ModifierKey.CTRL);

            window.addComponent(button1);
            window.addComponent(button2);
            button1.focus();
            button1.setData(prev);
            prev = button1;
        }

    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        super.terminalError(event);
        getMainWindow().showNotification("Failed!",
                Notification.TYPE_ERROR_MESSAGE);

    }

    public void buttonClick(ClickEvent event) {
        Window window2 = event.getButton().getWindow();
        window2.getParent().removeWindow(window2);
        Button prev = (Button) event.getButton().getData();
        if (prev != null) {
            prev.focus();
        }
    }

}
