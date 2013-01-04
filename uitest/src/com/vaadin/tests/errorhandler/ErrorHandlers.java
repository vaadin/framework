package com.vaadin.tests.errorhandler;

import java.lang.reflect.InvocationTargetException;

import com.vaadin.event.ListenerMethod.MethodException;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class ErrorHandlers extends AbstractTestUI {

    public static class NotificationErrorHandler implements ErrorHandler {

        @Override
        public void error(com.vaadin.server.ErrorEvent event) {
            Notification.show(getErrorMessage(event), Type.ERROR_MESSAGE);
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(runtimeExceptionOnClick(new Button("Standard button")));
        addComponent(npeOnClick(new Button("Standard button with NPE")));
        Button customErrorButton = notificationErrorHandler(new Button(
                "Button with notification error handler"));
        addComponent(runtimeExceptionOnClick(customErrorButton));

        final VerticalLayout layoutWithErrorHandler = new VerticalLayout(
                runtimeExceptionOnClick(new Button("Error handler on parent")));
        ErrorHandler e = new ErrorHandler() {

            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                layoutWithErrorHandler.addComponent(new Label("Layout error: "
                        + getErrorMessage(event)));
            }

        };
        layoutWithErrorHandler.setErrorHandler(e);
        layoutWithErrorHandler
                .addComponent(notificationErrorHandler(npeOnClick(new Button(
                        "Error handler on button and parent"))));
        addComponent(layoutWithErrorHandler);
    }

    private Button notificationErrorHandler(Button button) {
        button.setErrorHandler(new NotificationErrorHandler());
        return button;
    }

    protected static String getErrorMessage(com.vaadin.server.ErrorEvent event) {
        Component c = DefaultErrorHandler.findAbstractComponent(event);
        String errorMsg = "Error: '" + getMessage(event) + "' in ";
        errorMsg += c.getClass().getSimpleName() + " with caption '"
                + c.getCaption() + "'";
        return errorMsg;
    }

    private static String getMessage(com.vaadin.server.ErrorEvent event) {
        Throwable e = getUserCodeException(event);
        if (e.getMessage() != null) {
            return e.getMessage();
        } else {
            return e.getClass().getSimpleName();
        }
    }

    private static Throwable getUserCodeException(
            com.vaadin.server.ErrorEvent event) {
        Throwable t = event.getThrowable();
        if (t instanceof RpcInvocationException) {
            t = t.getCause();
        }
        if (t instanceof InvocationTargetException) {
            t = t.getCause();
        }
        if (t instanceof MethodException) {
            t = t.getCause();
        }

        return t;

    }

    private Button runtimeExceptionOnClick(Button customErrorButton) {
        customErrorButton.setCaption("RE: " + customErrorButton.getCaption());

        customErrorButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                throw new RuntimeException("Fail in click event");
            }
        });
        return customErrorButton;
    }

    private Button npeOnClick(Button customErrorButton) {
        customErrorButton.setCaption("NPE: " + customErrorButton.getCaption());
        customErrorButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Integer i = null;
                i += 2;
            }
        });
        return customErrorButton;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
