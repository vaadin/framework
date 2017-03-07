/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.notification;

import com.vaadin.server.Page;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

public class NotificationsHtmlAllowed extends TestBase
        implements ClickListener {

    private TextArea messageField;
    private CheckBox htmlAllowedBox;
    private TextField captionField;

    @Override
    protected void setup() {
        captionField = new TextField("Caption", "Hello <u>world</u>");
        addComponent(captionField);
        captionField.focus();

        messageField = new TextArea("Message",
                "Hello <i>world</i>\nWith a newline <br/>And a html line break");
        messageField.setRows(10);
        addComponent(messageField);

        htmlAllowedBox = new CheckBox("Html content allowed", true);
        addComponent(htmlAllowedBox);

        Button showNotification = new Button("Show notification", this);
        addComponent(showNotification);
    }

    @Override
    protected String getDescription() {
        return "Test case for htmlAllowed in notifications";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6097;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Notification n = makeNotification();
        n.show(Page.getCurrent());
    }

    private Notification makeNotification() {
        Notification n = new Notification(captionField.getValue(),
                messageField.getValue(), Notification.TYPE_HUMANIZED_MESSAGE,
                htmlAllowedBox.getValue());
        return n;
    }
}
