package com.vaadin.tests.actions;

import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ActionsWithoutKeyCode extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        TextField tf = new TextField();
        tf.setWidth("100%");
        tf.setInputPrompt("Enter text with å,ä or ä or press windows key while textfield is focused");
        addComponent(tf);

        addActionHandler(new Action.Handler() {

            private Action[] actions;
            {
                actions = new Action[] { new Action("test1") };
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return actions;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                log("action " + action.getCaption() + " triggered by "
                        + sender.getClass().getSimpleName() + " on "
                        + target.getClass().getSimpleName());
            }
        });
    }

}