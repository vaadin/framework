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
package com.vaadin.tests.components.customlayout;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CustomLayoutUpdateCaption extends UI {
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        CustomLayout content = new CustomLayout();
        content.setTemplateContents("<div>\n"
                + "        <div location=\"test1\"></div>\n"
                + "        <div location=\"test2\"></div>\n"
                + "        <div location=\"okbutton\"></div>\n" + "</div>");
        content.setSizeUndefined();
        setContent(content);

        Button loginButton = new Button("Test");
        final TextField username1 = new TextField();
        final TextField username2 = new TextField();
        username1.setCaption("initial");
        username2.setCaption("initial");
        content.addComponent(username1, "test1");
        content.addComponent(new VerticalLayout(username2), "test2");
        content.addComponent(loginButton, "okbutton");

        loginButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                username1.setCaption("updated");
                username2.setCaption("updated");
            }
        });
    }
}
