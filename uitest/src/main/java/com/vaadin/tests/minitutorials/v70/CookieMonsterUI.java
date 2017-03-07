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
package com.vaadin.tests.minitutorials.v70;

import javax.servlet.http.Cookie;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

/**
 * Tutorial example for
 * https://vaadin.com/wiki/-/wiki/Main/Setting%20and%20reading%20Cookies
 */
public class CookieMonsterUI extends UI {

    private static final String NAME_COOKIE = "name";

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final TextField nameField = new TextField();
        layout.addComponent(nameField);

        // Read previously stored cookie value
        Cookie nameCookie = getCookieByName(NAME_COOKIE);
        if (getCookieByName(NAME_COOKIE) != null) {
            nameField.setValue(nameCookie.getValue());
        }

        Button button = new Button("Store name in cookie");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                String name = nameField.getValue();

                // See if name cookie is already set
                Cookie nameCookie = getCookieByName(NAME_COOKIE);

                if (nameCookie != null) {
                    String oldName = nameCookie.getValue();

                    nameCookie.setValue(name);

                    Notification.show("Updated name in cookie from " + oldName
                            + " to " + name);

                } else {
                    // Create a new cookie
                    nameCookie = new Cookie(NAME_COOKIE, name);
                    nameCookie.setComment(
                            "Cookie for storing the name of the user");

                    Notification.show("Stored name " + name + " in cookie");
                }

                // Make cookie expire in 2 minutes
                nameCookie.setMaxAge(120);

                // Set the cookie path.
                nameCookie.setPath(
                        VaadinService.getCurrentRequest().getContextPath());

                // Save cookie
                VaadinService.getCurrentResponse().addCookie(nameCookie);
            }
        });
        layout.addComponent(button);

    }

    private Cookie getCookieByName(String name) {
        // Fetch all cookies from the request
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();

        // Iterate to find a cookie by its name
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }
}
