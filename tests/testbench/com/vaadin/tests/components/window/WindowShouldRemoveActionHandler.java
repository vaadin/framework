package com.vaadin.tests.components.window;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;

public class WindowShouldRemoveActionHandler extends TestBase {

    @Override
    protected String getDescription() {
        return "Adding action handlers to the window should make them appear on the client side. Removing the action handlers should remove them also from the client side, also if all action handlers are removed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2941;
    }

    @Override
    protected void setup() {
        addComponent(new TextField());
        Button add = new Button("Add an action handler",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        add();
                    }

                });
        Button addAnother = new Button("Add another action handler",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        addAnother();
                    }

                });
        Button remove = new Button("Remove an action handler",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        remove();
                    }

                });

        addComponent(add);
        addComponent(addAnother);
        addComponent(remove);
    }

    public void remove() {
        getMainWindow().setCaption(
                getMainWindow().getCaption() + " - Removed handler");
        getMainWindow().removeActionHandler(
                actionHandlers.remove(actionHandlers.size() - 1));
    }

    private List<Handler> actionHandlers = new ArrayList<Handler>();

    public void add() {
        getMainWindow().setCaption(
                getMainWindow().getCaption() + " - Added handler");
        Handler actionHandler = new Handler() {

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new ShortcutAction("Ctrl+Left",
                        ShortcutAction.KeyCode.ARROW_LEFT,
                        new int[] { ModifierKey.CTRL }) };
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                getMainWindow().showNotification(
                        "Handling action " + action.getCaption());
            }

        };

        addHandler(actionHandler);
    }

    public void addAnother() {
        Handler actionHandler = new Handler() {

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new ShortcutAction("Ctrl+Right",
                        ShortcutAction.KeyCode.ARROW_RIGHT,
                        new int[] { ModifierKey.CTRL }) };
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                getMainWindow().showNotification(
                        "Handling action " + action.getCaption());
            }

        };

        addHandler(actionHandler);
    }

    private void addHandler(Handler actionHandler) {
        actionHandlers.add(actionHandler);
        getMainWindow().addActionHandler(actionHandler);
        getMainWindow().setCaption(
                "A panel with " + actionHandlers.size() + " action handlers");

    }
}
