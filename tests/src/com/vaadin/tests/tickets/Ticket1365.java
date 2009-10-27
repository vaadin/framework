package com.vaadin.tests.tickets;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket1365 extends com.vaadin.Application implements
        Handler {

    TextField f = new TextField();

    Label status = new Label("ENTER and CTRL-S fires shortcut action.");

    @Override
    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        main.addComponent(f);
        main.addComponent(status);
        main.addActionHandler(this);
        f.focus();

    }

    final static private Action[] actions = new Action[] {
            new ShortcutAction("Enter", ShortcutAction.KeyCode.ENTER,
                    new int[] {}),
            new ShortcutAction("CTRL-S", ShortcutAction.KeyCode.S,
                    new int[] { ShortcutAction.ModifierKey.CTRL }), };

    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        status.setValue("Pressed " + action.getCaption()
                + " to fire shortcut. Texfield value: " + f.getValue());
        f.focus();
    }

}