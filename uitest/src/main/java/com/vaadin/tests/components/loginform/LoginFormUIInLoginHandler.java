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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.UI;

public class LoginFormUIInLoginHandler extends TestBase {

    @Override
    protected void setup() {
        LoginForm lf = new LoginForm();
        lf.addLoginListener(new LoginListener() {

            @Override
            public void onLogin(LoginEvent event) {
                UI r1 = UI.getCurrent();
                if (r1 != null) {
                    addComponent(
                            new Label("UI.getCurrent().data: " + r1.getData()));
                } else {
                    addComponent(new Label("UI.getCurrent() is null"));
                }
                UI r2 = event.getSource().getUI();
                if (r2 != null) {
                    addComponent(new Label(
                            "event.getSource().data: " + r2.getData()));
                } else {
                    addComponent(
                            new Label("event.getSource().getRoot() is null"));
                }
            }
        });
        addComponent(lf);
        getLayout().getUI().setData("This UI");
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
