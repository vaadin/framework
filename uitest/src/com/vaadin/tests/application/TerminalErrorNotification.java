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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class TerminalErrorNotification extends TestBase {

    @Override
    protected void setup() {
        Button button = new Button("Throw exception",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        throw new RuntimeException("You asked for it");
                    }
                });

        addComponent(button);
    }

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        event.getThrowable().printStackTrace();

        UI mainWindow = getMainWindow();
        if (mainWindow != null) {
            Throwable throwable = event.getThrowable();

            // Find the root cause
            while (throwable.getCause() != null) {
                throwable = throwable.getCause();
            }

            Notification.show("Got an exception: " + throwable.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
        } else {
            System.out.println("No main window found");
        }
    }

    @Override
    protected String getDescription() {
        return "Showing a notification in the terminalError method should make the notification appear in the browser.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8778);
    }

}
