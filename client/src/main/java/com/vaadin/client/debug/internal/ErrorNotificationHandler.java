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

package com.vaadin.client.debug.internal;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.logging.client.TextLogFormatter;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VNotification;

/**
 * Log message handler that shows big red notification for {@link Level#SEVERE}
 * messages that have a throwable.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class ErrorNotificationHandler extends Handler {
    public ErrorNotificationHandler() {
        setLevel(Level.SEVERE);
        setFormatter(new TextLogFormatter(true) {
            @Override
            protected String getRecordInfo(LogRecord event, String newline) {
                return "";
            }
        });
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record) || record.getThrown() == null) {
            return;
        }

        try {
            String exceptionText = getFormatter().format(record);

            Widget owner = null;

            if (!ApplicationConfiguration.getRunningApplications().isEmpty()) {
                /*
                 * Make a wild guess and use the first available
                 * ApplicationConnection. This is better than than leaving the
                 * exception completely unstyled...
                 */
                ApplicationConnection connection = ApplicationConfiguration
                        .getRunningApplications().get(0);
                owner = connection.getUIConnector().getWidget();
            }
            VNotification n = VNotification.createNotification(
                    VNotification.DELAY_FOREVER, owner);
            n.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            n.show("<h1>Uncaught client side exception</h1><br />"
                    + exceptionText.replace("\n", "<br/>\n"),
                    VNotification.CENTERED, "error");
        } catch (Exception e2) {
            // Just swallow this exception
        }
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public void flush() {
        // Nothing todo
    }
}
