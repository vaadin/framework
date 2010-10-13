package com.vaadin.tests.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.UserError;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Layout.SpacingHandler;

public abstract class AbstractComponentTestCase<T extends AbstractComponent>
        extends TestBase {

    private List<T> testComponents = new ArrayList<T>();

    abstract protected Class<T> getTestClass();

    abstract protected void initializeComponents();

    private Log log = null;

    @Override
    protected void setup() {
        ((SpacingHandler) getLayout()).setSpacing(true);

        // Create Components
        initializeComponents();
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    protected void addTestComponent(T c) {
        testComponents.add(c);
        addComponent(c);
    }

    protected List<T> getTestComponents() {
        return testComponents;
    }

    public interface Command<T, VALUETYPE extends Object> {
        public void execute(T c, VALUETYPE value, Object data);
    }

    // public interface Command<T, VALUETYPE extends Object> {
    // public void execute(T c, VALUETYPE value, Object data);
    // }

    /* COMMANDS */

    protected Command<T, String> widthCommand = new Command<T, String>() {

        @Override
        public void execute(T t, String value, Object data) {
            t.setWidth(value);
        }
    };
    protected Command<T, String> heightCommand = new Command<T, String>() {

        @Override
        public void execute(T t, String value, Object data) {
            t.setHeight(value);
        }
    };

    protected Command<T, Boolean> enabledCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean enabled, Object data) {
            c.setEnabled(enabled);
        }
    };

    protected Command<T, Boolean> requiredCommand = new Command<T, Boolean>() {
        @Override
        public void execute(T c, Boolean enabled, Object data) {
            if (c instanceof Field) {
                ((Field) c).setRequired(enabled);
            } else {
                throw new IllegalArgumentException(c.getClass().getName()
                        + " is not a field and cannot be set to required");
            }
        }
    };

    protected Command<T, Boolean> errorIndicatorCommand = new Command<T, Boolean>() {
        @Override
        public void execute(T c, Boolean enabled, Object data) {
            if (enabled) {
                c.setComponentError(new UserError("It failed!"));
            } else {
                c.setComponentError(null);

            }
        }
    };

    protected Command<T, Boolean> readonlyCommand = new Command<T, Boolean>() {
        @Override
        public void execute(T c, Boolean enabled, Object data) {
            c.setReadOnly(enabled);
        }
    };

    protected <VALUET> void doCommand(Command<T, VALUET> command, VALUET value) {
        doCommand(command, value, null);
    }

    protected <VALUET> void doCommand(Command<T, VALUET> command, VALUET value,
            Object data) {
        for (T c : getTestComponents()) {
            command.execute(c, value, data);
        }
    }

    protected <VALUET> void doCommand(String commandName,
            Command<T, VALUET> command, VALUET value) {
        doCommand(command, value, null);
        if (hasLog()) {
            log(commandName + ": " + value);
        }
    }

    protected <VALUET> void doCommand(String commandName,
            Command<T, VALUET> command, VALUET value, Object data) {
        doCommand(command, value, data);
        if (hasLog()) {
            log(commandName + ": " + value);
        }
    }

    protected boolean hasLog() {
        return log != null;
    }

    @Override
    protected String getDescription() {
        return "Generic test case for " + getTestClass().getSimpleName();
    }

    protected void enableLog() {
        if (log == null) {
            log = new Log(5).setNumberLogRows(true);
            getLayout().addComponent(log, 1);
        }

    }

    protected void log(String msg) {
        if (log == null) {
            throw new IllegalStateException(
                    "Use enableLog() before calling log()");
        }
        log.log(msg);
    }
}
