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
package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

/**
 * Test UI for H1 and P elements styles.
 * 
 * @author Vaadin Ltd
 */
@Theme("valo")
public class NotificationStyle extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("Show notification with h1",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification notification = new Notification(
                                "<p>Caption</p>",
                                "<div style='display:inline-block;'><h1>Description</h1>"
                                        + "<p class='tested-p'>tested p</p></div>");
                        notification.setHtmlContentAllowed(true);
                        notification.setDelayMsec(50000);
                        notification.show(getPage());
                    }
                });
        addComponent(button);
        button = new Button("Show notification with p",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification notification = new Notification(
                                "<p>Caption</p>",
                                "Description text<p class='tested-p'>tested p text</p>");
                        notification.setHtmlContentAllowed(true);
                        notification.setDelayMsec(50000);
                        notification.show(getPage());
                    }
                });
        addComponent(button);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14872;
    }

    @Override
    protected String getTestDescription() {
        return "Notification styles should be scoped more eagerly.";
    }

}
