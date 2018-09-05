package com.vaadin.tests.requesthandlers;

import java.io.IOException;
import java.io.PrintWriter;

import com.vaadin.launcher.ApplicationRunnerServlet;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * Test UI provider to check communication error json object null values.
 *
 * @author Vaadin Ltd
 */
public class CommunicationError extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        VaadinServletRequest request = (VaadinServletRequest) event
                .getRequest();
        String currentUrl = request.getRequestURL().toString();
        StringBuilder redirectClass = new StringBuilder(
                CommunicationError.class.getSimpleName());
        redirectClass.append('$');
        redirectClass.append(RedirectedUI.class.getSimpleName());

        String restartApplication = "?restartApplication";
        if (!currentUrl.contains(restartApplication)) {
            redirectClass.append(restartApplication);
        }
        final String url = currentUrl.replace(
                CommunicationError.class.getSimpleName(), redirectClass);

        request.setAttribute(
                ApplicationRunnerServlet.CUSTOM_SYSTEM_MESSAGES_PROPERTY,
                createSystemMessages(url));

        return CommunicationErrorUI.class;
    }

    public static class CommunicationErrorUI extends AbstractReindeerTestUI {

        @Override
        protected void setup(VaadinRequest request) {
            Button button = new Button("Send bad request", event -> {
                try {
                    // An unparseable response will cause
                    // communication error
                    PrintWriter writer = VaadinService.getCurrentResponse()
                            .getWriter();
                    writer.write("for(;;)[{FOOBAR}]");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            addComponent(button);
        }

        @Override
        protected Integer getTicketNumber() {
            return 14594;
        }

        @Override
        protected String getTestDescription() {
            return "Null values should be wrapped into JsonNull objects.";
        }
    }

    public static class RedirectedUI extends UI {

        @Override
        protected void init(VaadinRequest request) {
            Label label = new Label("redirected");
            label.addStyleName("redirected");
            setContent(label);
        }

    }

    private SystemMessages createSystemMessages(String url) {
        CustomizedSystemMessages messages = new CustomizedSystemMessages();
        messages.setCommunicationErrorCaption(null);
        messages.setCommunicationErrorMessage(null);
        messages.setCommunicationErrorURL(url);
        return messages;
    }
}
