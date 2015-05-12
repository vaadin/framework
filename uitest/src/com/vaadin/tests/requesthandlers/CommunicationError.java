/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.requesthandlers;

import com.vaadin.launcher.ApplicationRunnerServlet;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

    public static class CommunicationErrorUI extends AbstractTestUI {

        @Override
        protected void setup(VaadinRequest request) {
            Button button = new Button("Send bad request",
                    new Button.ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            VaadinService.getCurrentResponse().setStatus(400);
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
