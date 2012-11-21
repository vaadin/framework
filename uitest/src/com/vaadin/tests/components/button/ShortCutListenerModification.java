package com.vaadin.tests.components.button;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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

            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            Window window = new Window();
            window.setContent(layout);
            getMainWindow().addWindow(window);

            Button button1 = new Button("b1 (CTRL-C)");
            Button button2 = new Button("b2 (CTRL-V)");

            button1.addListener(this);
            button2.addListener(this);

            button1.setClickShortcut(KeyCode.C, ModifierKey.CTRL);
            button2.setClickShortcut(KeyCode.V, ModifierKey.CTRL);

            layout.addComponent(button1);
            layout.addComponent(button2);
            button1.focus();
            button1.setData(prev);
            prev = button1;
        }

    }

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        super.error(event);
        getMainWindow().showNotification("Failed!",
                Notification.TYPE_ERROR_MESSAGE);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        Component c = event.getButton();
        while (!(c instanceof Window)) {
            c = c.getParent();
        }
        ((Window) c).close();

        Button prev = (Button) event.getButton().getData();
        if (prev != null) {
            prev.focus();
        }
    }

}
