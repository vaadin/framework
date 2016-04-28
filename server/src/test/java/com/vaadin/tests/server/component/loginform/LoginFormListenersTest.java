package com.vaadin.tests.server.component.loginform;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;

public class LoginFormListenersTest extends AbstractListenerMethodsTestBase {
    public void testLoginListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LoginForm.class, LoginEvent.class,
                LoginListener.class);
    }
}
