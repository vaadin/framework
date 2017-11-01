package com.vaadin.tests.components.window;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.TextField;

public class WindowShouldRemoveActionHandler extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "Adding action handlers to the window should make them appear on the client side. Removing the action handlers should remove them also from the client side, also if all action handlers are removed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2941;
    }

    private Label state;

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setMargin(new MarginInfo(true, false, false, false));
        state = new Label("An UI with no action handlers.");
        state.setId("state");
        addComponents(state, new TextField());

        addButton("Add an action handler", event -> add());
        addButton("Add another action handler", event -> addAnother());
        addButton("Remove an action handler", event -> remove());
    }

    public void remove() {
        state.setValue(state.getValue() + " - Removed handler");
        removeActionHandler(actionHandlers.remove(actionHandlers.size() - 1));
    }

    private List<Handler> actionHandlers = new ArrayList<>();

    public void add() {
        Handler actionHandler = new Handler() {

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new ShortcutAction("Ctrl+Left",
                        ShortcutAction.KeyCode.ARROW_LEFT,
                        new int[] { ModifierKey.CTRL }) };
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                Notification.show("Handling action " + action.getCaption());
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
            public void handleAction(Action action, Object sender,
                    Object target) {
                Notification.show("Handling action " + action.getCaption());
            }

        };

        addHandler(actionHandler);
    }

    private void addHandler(Handler actionHandler) {
        actionHandlers.add(actionHandler);
        addActionHandler(actionHandler);
        state.setValue(
                "An UI with " + actionHandlers.size() + " action handlers");

    }
}
