package com.vaadin.tests.server.component.loginform;

import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;

public class LoginFormListeners extends AbstractListenerMethodsTest {
    public void testLoginListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LoginForm.class, LoginEvent.class,
                LoginListener.class);
    }
}
