package com.vaadin.tests.components.loginform;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Root;

public class LoginFormRootInLoginHandler extends TestBase {

    @Override
    protected void setup() {
        LoginForm lf = new LoginForm();
        lf.addListener(new LoginListener() {

            @Override
            public void onLogin(LoginEvent event) {
                Root r1 = Root.getCurrent();
                if (r1 != null) {
                    addComponent(new Label("Root.getCurrent().data: "
                            + r1.getData()));
                } else {
                    addComponent(new Label("Root.getCurrent() is null"));
                }
                Root r2 = ((LoginForm) event.getSource()).getRoot();
                if (r2 != null) {
                    addComponent(new Label("event.getSource().data: "
                            + r2.getData()));
                } else {
                    addComponent(new Label(
                            "event.getSource().getRoot() is null"));
                }
            }
        });
        addComponent(lf);
        getLayout().getRoot().setData("This root");
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
