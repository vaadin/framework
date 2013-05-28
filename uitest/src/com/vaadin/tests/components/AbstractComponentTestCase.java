package com.vaadin.tests.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Layout.SpacingHandler;

public abstract class AbstractComponentTestCase<T extends AbstractComponent>
        extends TestBase {

    protected static final ThemeResource ICON_16_HELP_PNG_CACHEABLE = cacheableThemeResource("../runo/icons/16/help.png");
    protected static final ThemeResource ICON_16_FOLDER_PNG_CACHEABLE = cacheableThemeResource("../runo/icons/16/folder.png");
    protected static final ThemeResource ICON_16_ERROR_PNG_CACHEABLE = cacheableThemeResource("../runo/icons/16/error.png");
    protected static final ThemeResource ICON_16_USER_PNG_CACHEABLE = cacheableThemeResource("../runo/icons/16/user.png");
    protected static final ThemeResource ICON_16_USER_PNG_UNCACHEABLE = uncacheableThemeResource("../runo/icons/16/user.png");
    protected static final ThemeResource ICON_32_ATTENTION_PNG_CACHEABLE = cacheableThemeResource("../runo/icons/32/attention.png");
    protected static final ThemeResource ICON_32_ATTENTION_PNG_UNCACHEABLE = uncacheableThemeResource("../runo/icons/32/attention.png");
    protected static final ThemeResource ICON_64_EMAIL_REPLY_PNG_CACHEABLE = cacheableThemeResource("../runo/icons/64/email-reply.png");
    protected static final ThemeResource ICON_64_EMAIL_REPLY_PNG_UNCACHEABLE = uncacheableThemeResource("../runo/icons/64/email-reply.png");

    private List<T> testComponents = new ArrayList<T>();

    abstract protected Class<T> getTestClass();

    protected static ThemeResource uncacheableThemeResource(
            String resourceLocation) {
        return new ThemeResource(resourceLocation + "?" + new Date().getTime());
    }

    protected static ThemeResource cacheableThemeResource(
            String resourceLocation) {
        return new ThemeResource(resourceLocation);
    }

    abstract protected void initializeComponents();

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

    protected Command<T, Boolean> immediateCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean immediate, Object data) {
            c.setImmediate(immediate);
        }
    };

    protected Command<T, Boolean> errorIndicatorCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean enabled, Object data) {
            if (enabled) {
                c.setComponentError(new UserError(errorMessage));
            } else {
                c.setComponentError(null);

            }
        }
    };
    private String errorMessage = null;

    protected Command<T, String> errorMessageCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String value, Object data) {
            errorMessage = value;
            if (c.getComponentError() != null) {
                errorIndicatorCommand.execute(c, true, null);
            }

        }

    };

    // TODO Move to AbstractFieldTestCase
    protected Command<T, Boolean> requiredCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean enabled, Object data) {
            if (c instanceof Field) {
                ((Field<?>) c).setRequired(enabled);
            } else {
                throw new IllegalArgumentException(c.getClass().getName()
                        + " is not a field and cannot be set to required");
            }
        }
    };
    protected Command<T, String> requiredErrorMessageCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String value, Object data) {
            ((Field<?>) c).setRequiredError(value);
        }

    };

    protected Command<T, String> descriptionCommand = new Command<T, String>() {
        @Override
        public void execute(T c, String value, Object data) {
            c.setDescription(value);
        }
    };

    protected Command<T, Boolean> readonlyCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean enabled, Object data) {
            c.setReadOnly(enabled);
        }
    };

    protected Command<T, Boolean> visibleCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean enabled, Object data) {
            c.setVisible(enabled);
        }
    };

    protected Command<T, Resource> iconCommand = new Command<T, Resource>() {

        @Override
        public void execute(T c, Resource value, Object data) {
            c.setIcon(value);
        }

    };
    protected Command<T, String> captionCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String value, Object data) {
            c.setCaption(value);
        }

    };

    protected Command<T, Locale> localeCommand = new Command<T, Locale>() {

        @Override
        public void execute(T c, Locale value, Object data) {
            c.setLocale(value);
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
        doCommand(commandName, command, value, null);
    }

    protected <VALUET> void doCommand(String commandName,
            Command<T, VALUET> command, VALUET value, Object data) {
        doCommand(command, value, data);
    }

    protected Command<T, String> styleNameCommand = new Command<T, String>() {
        @Override
        public void execute(T c, String value, Object data) {
            c.setStyleName(value);
        }
    };

    protected Command<T, String> primaryStyleNameCommand = new Command<T, String>() {
        @Override
        public void execute(T c, String value, Object data) {
            c.setPrimaryStyleName(value);
        }
    };

    @Override
    protected String getDescription() {
        return "Generic test case for " + getTestClass().getSimpleName();
    }

}
