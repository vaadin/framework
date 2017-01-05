package com.vaadin.tests.components.textfield;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

public class MultipleTextChangeEvents extends TestBase {

    private final Log log = new Log(5);

    @Override
    public void setup() {
        TextField tf = new TextField();
        tf.setValueChangeMode(ValueChangeMode.TIMEOUT);
        tf.setValueChangeTimeout(500);
        tf.addValueChangeListener(
                listener -> log.log("TextChangeEvent: " + listener.getValue()));
        getMainWindow().addActionHandler(new MyHandler());

        addComponent(log);
        addComponent(tf);
    }

    class MyHandler implements Handler {
        private static final long serialVersionUID = 1L;
        Action actionenter = new ShortcutAction("Enter",
                ShortcutAction.KeyCode.ENTER, null);

        @Override
        public Action[] getActions(Object theTarget, Object theSender) {
            return new Action[] { actionenter };
        }

        @Override
        public void handleAction(Action theAction, Object theSender,
                Object theTarget) {
            log.log("Enter");
        }
    }

    @Override
    protected String getDescription() {
        return "Entering something into the textfield and quickly pressing enter should only send one TextChangeEvent";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8035);
    }
}
