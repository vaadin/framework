package com.vaadin.tests.server.component.loginform;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

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
        assertEquals("Username", loginForm.getUsernameCaption());
        assertEquals("Password", loginForm.getPasswordCaption());
        assertEquals("Login", loginForm.getLoginButtonCaption());
    }

    @Test
    public void changeCaptionsBeforeAttach() {
        loginForm.setUsernameCaption("u");
        loginForm.setPasswordCaption("p");
        loginForm.setLoginButtonCaption("l");

        assertEquals("u", loginForm.getUsernameCaption());
        assertEquals("p", loginForm.getPasswordCaption());
        assertEquals("l", loginForm.getLoginButtonCaption());
    }

    @Test
    public void changeCaptionsAfterAttach() {
        UI ui = new MockUI();
        ui.setContent(loginForm);
        loginForm.setUsernameCaption("u");
        loginForm.setPasswordCaption("p");
        loginForm.setLoginButtonCaption("l");

        assertEquals("u", loginForm.getUsernameCaption());
        assertEquals("p", loginForm.getPasswordCaption());
        assertEquals("l", loginForm.getLoginButtonCaption());
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

        assertEquals("u", loginForm.getUsernameCaption());
        assertEquals("p", loginForm.getPasswordCaption());
        assertEquals("l", loginForm.getLoginButtonCaption());
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
        assertEquals("Username caption", i.next().getCaption());
        assertEquals("Password caption", i.next().getCaption());
        assertEquals("Do it", i.next().getCaption());
    }
}
