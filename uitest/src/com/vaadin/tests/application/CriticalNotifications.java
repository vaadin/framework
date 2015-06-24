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
package com.vaadin.tests.application;

import java.io.IOException;

import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.JsonConstants;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;

public class CriticalNotifications extends AbstractTestUI {

    private SystemMessages systemMessages;
    private CheckBox includeDetails;

    @Override
    protected void setup(VaadinRequest request) {
        systemMessages = VaadinService.getCurrent().getSystemMessages(
                getLocale(), request);

        includeDetails = new CheckBox("Include details");
        addComponent(includeDetails);

        Button sessionExpired = new Button("Session expired");
        addComponent(sessionExpired);
        sessionExpired.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCriticalNotification(
                        systemMessages.getSessionExpiredCaption(),
                        systemMessages.getSessionExpiredMessage(),
                        getDetailsMessage(),
                        systemMessages.getSessionExpiredURL());

            }
        });

        Button authenticationError = new Button("Authentication error");
        addComponent(authenticationError);
        authenticationError.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCriticalNotification(
                        systemMessages.getAuthenticationErrorCaption(),
                        systemMessages.getAuthenticationErrorMessage(),
                        getDetailsMessage(),
                        systemMessages.getAuthenticationErrorURL());

            }
        });

        Button communicationError = new Button("Communication error");
        addComponent(communicationError);
        communicationError.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCriticalNotification(
                        systemMessages.getCommunicationErrorCaption(),
                        systemMessages.getCommunicationErrorMessage(),
                        getDetailsMessage(),
                        systemMessages.getCommunicationErrorURL());

            }
        });

        Button internalError = new Button("Internal error");
        addComponent(internalError);
        internalError.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCriticalNotification(
                        systemMessages.getInternalErrorCaption(),
                        systemMessages.getInternalErrorMessage(),
                        getDetailsMessage(),
                        systemMessages.getInternalErrorURL());

            }
        });

        Button cookiesDisabled = new Button("Cookies disabled");
        addComponent(cookiesDisabled);
        cookiesDisabled.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCriticalNotification(
                        systemMessages.getCookiesDisabledCaption(),
                        systemMessages.getCookiesDisabledMessage(),
                        getDetailsMessage(),
                        systemMessages.getCookiesDisabledURL());

            }
        });
        Button custom = new Button("Custom");
        addComponent(custom);
        custom.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showCriticalNotification("Custom caption", "Custom message",
                        "Custom details", "custom url");

            }
        });
    }

    protected String getDetailsMessage() {
        if (includeDetails.getValue()) {
            return "Some details for the error";
        } else {
            return null;
        }
    }

    protected void showCriticalNotification(String caption, String message,
            String details, String url) {
        VaadinService service = VaadinService.getCurrent();
        VaadinResponse response = VaadinService.getCurrentResponse();

        try {
            service.writeStringResponse(response,
                    JsonConstants.JSON_CONTENT_TYPE, VaadinService
                            .createCriticalNotificationJSON(caption, message,
                                    details, url));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
