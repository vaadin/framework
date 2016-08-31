package com.vaadin.tests.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.HasRequired;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Layout.SpacingHandler;
import com.vaadin.v7.ui.Field;

public abstract class AbstractComponentTestCase<T extends AbstractComponent>
        extends AbstractTestUI {

    protected static final ThemeResource ICON_16_HELP_PNG_CACHEABLE = cacheableThemeResource(
            "../runo/icons/16/help.png");
    protected static final ThemeResource ICON_16_FOLDER_PNG_CACHEABLE = cacheableThemeResource(
            "../runo/icons/16/folder.png");
    protected static final ThemeResource ICON_16_ERROR_PNG_CACHEABLE = cacheableThemeResource(
            "../runo/icons/16/error.png");
    protected static final ThemeResource ICON_16_USER_PNG_CACHEABLE = cacheableThemeResource(
            "../runo/icons/16/user.png");
    protected static final ThemeResource ICON_16_USER_PNG_UNCACHEABLE = uncacheableThemeResource(
            "../runo/icons/16/user.png");
    protected static final ThemeResource ICON_32_ATTENTION_PNG_CACHEABLE = cacheableThemeResource(
            "../runo/icons/32/attention.png");
    protected static final ThemeResource ICON_32_ATTENTION_PNG_UNCACHEABLE = uncacheableThemeResource(
            "../runo/icons/32/attention.png");
    protected static final ThemeResource ICON_64_EMAIL_REPLY_PNG_CACHEABLE = cacheableThemeResource(
            "../runo/icons/64/email-reply.png");
    protected static final ThemeResource ICON_64_EMAIL_REPLY_PNG_UNCACHEABLE = uncacheableThemeResource(
            "../runo/icons/64/email-reply.png");

    private List<T> testComponents = new ArrayList<>();

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
    protected void setup(VaadinRequest request) {
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

    private String errorMessage = null;

    /* COMMANDS */

    protected Command<T, String> widthCommand = (t, value, data) -> t.setWidth(
            value);
    protected Command<T, String> heightCommand = (t, value, data) -> t
            .setHeight(value);

    protected Command<T, Boolean> enabledCommand = (c, enabled, data) -> c
            .setEnabled(enabled);

    protected Command<T, Boolean> immediateCommand = (c, immediate, data) -> c
            .setImmediate(immediate);

    protected Command<T, Boolean> errorIndicatorCommand = (c, enabled,
            data) -> {
        if (enabled) {
            c.setComponentError(new UserError(errorMessage));
        } else {
            c.setComponentError(null);
        }
    };

    protected Command<T, String> errorMessageCommand = (c, value, data) -> {
        errorMessage = value;
        if (c.getComponentError() != null) {
            errorIndicatorCommand.execute(c, true, null);
        }

    };

    // TODO Move to AbstractFieldTestCase
    protected Command<T, Boolean> requiredCommand = (c, enabled, data) -> {
        if (c instanceof HasRequired) {
            ((HasRequired) c).setRequired(enabled);
        } else {
            throw new IllegalArgumentException(c.getClass().getName()
                    + " is not a field and cannot be set to required");
        }
    };
    protected Command<T, String> requiredErrorMessageCommand = (c, value,
            data) -> ((Field<?>) c).setRequiredError(value);

    protected Command<T, String> descriptionCommand = (c, value, data) -> c
            .setDescription(value);

    protected Command<T, Boolean> readonlyCommand = (c, enabled, data) -> c
            .setReadOnly(enabled);

    protected Command<T, Boolean> visibleCommand = (c, enabled, data) -> c
            .setVisible(enabled);

    protected Command<T, Resource> iconCommand = (c, value, data) -> c.setIcon(
            value);
    protected Command<T, String> captionCommand = (c, value, data) -> c
            .setCaption(value);

    protected Command<T, Locale> localeCommand = (c, value, data) -> c
            .setLocale(value);

    protected <VALUET> void doCommand(Command<T, VALUET> command,
            VALUET value) {
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

    protected Command<T, String> styleNameCommand = (c, value, data) -> c
            .setStyleName(value);

    protected Command<T, String> primaryStyleNameCommand = (c, value, data) -> c
            .setPrimaryStyleName(value);

    @Override
    protected String getTestDescription() {
        return "Generic test case for " + getTestClass().getSimpleName();
    }
}
