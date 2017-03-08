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
package com.vaadin.tests.server.component.loginform;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class LoginFormTest {

    private LoginForm loginForm;

    @Before
    public void setup() {
        loginForm = new LoginForm();
    }

    @Test
    public void defaultCaptions() {
        Assert.assertEquals("Username", loginForm.getUsernameCaption());
        Assert.assertEquals("Password", loginForm.getPasswordCaption());
        Assert.assertEquals("Login", loginForm.getLoginButtonCaption());
    }

    @Test
    public void changeCaptionsBeforeAttach() {
        loginForm.setUsernameCaption("u");
        loginForm.setPasswordCaption("p");
        loginForm.setLoginButtonCaption("l");

        Assert.assertEquals("u", loginForm.getUsernameCaption());
        Assert.assertEquals("p", loginForm.getPasswordCaption());
        Assert.assertEquals("l", loginForm.getLoginButtonCaption());
    }

    @Test
    public void changeCaptionsAfterAttach() {
        UI ui = new MockUI();
        ui.setContent(loginForm);
        loginForm.setUsernameCaption("u");
        loginForm.setPasswordCaption("p");
        loginForm.setLoginButtonCaption("l");

        Assert.assertEquals("u", loginForm.getUsernameCaption());
        Assert.assertEquals("p", loginForm.getPasswordCaption());
        Assert.assertEquals("l", loginForm.getLoginButtonCaption());
    }

    @Test
    public void changeCaptionsBeforeAndAfterAttach() {
        loginForm.setUsernameCaption("a");
        loginForm.setPasswordCaption("a");
        loginForm.setLoginButtonCaption("a");

        UI ui = new MockUI();
        ui.setContent(loginForm);
        loginForm.setUsernameCaption("u");
        loginForm.setPasswordCaption("p");
        loginForm.setLoginButtonCaption("l");

        Assert.assertEquals("u", loginForm.getUsernameCaption());
        Assert.assertEquals("p", loginForm.getPasswordCaption());
        Assert.assertEquals("l", loginForm.getLoginButtonCaption());
    }

    @Test
    public void customizedFields() {
        LoginForm customForm = new LoginForm() {
            @Override
            protected Button createLoginButton() {
                return new NativeButton("Do it");
            }

            @Override
            protected TextField createUsernameField() {
                TextField tf = new TextField("Username caption");
                tf.setPlaceholder("Name goes here");
                return tf;
            }

            @Override
            protected PasswordField createPasswordField() {
                PasswordField pf = new PasswordField("Password caption");
                pf.setPlaceholder("Secret goes here");
                return pf;
            }
        };

        UI ui = new MockUI();
        ui.setContent(customForm);
        Iterator<Component> i = ((HasComponents) customForm.iterator().next())
                .iterator();
        Assert.assertEquals("Username caption", i.next().getCaption());
        Assert.assertEquals("Password caption", i.next().getCaption());
        Assert.assertEquals("Do it", i.next().getCaption());
    }
}
