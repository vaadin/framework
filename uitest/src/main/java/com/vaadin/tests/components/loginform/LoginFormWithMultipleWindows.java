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
package com.vaadin.tests.components.loginform;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;

@SuppressWarnings("serial")
public class LoginFormWithMultipleWindows extends LegacyApplication {

    @Override
    public void init() {
        setMainWindow(new LoginFormWindow());
    }

    public static class LoginFormWindow extends LegacyWindow {
        public LoginFormWindow() {
            super();

            LoginForm loginForm = new LoginForm();
            loginForm.setSizeUndefined();

            loginForm.addLoginListener(new LoginListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void onLogin(LoginEvent event) {
                    showNotification(event.getLoginParameter("username") + ":"
                            + event.getLoginParameter("password"));

                }
            });

            addComponent(loginForm);
        }
    }

}
