package com.vaadin.tests.components;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class BinderFormValidate extends AbstractTestUI {

    public class LoginDto {

        private String username;
        private String password;

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Binder<LoginDto> binder = new Binder<>();
        binder.setBean(new LoginDto());

        TextField username = new TextField("Username");
        username.setId("username");

        PasswordField password = new PasswordField("Password");
        password.setId("password");

        binder.forField(username)
                .asRequired("Username is required")
                .bind(LoginDto::getUsername, LoginDto::setUsername);

        binder.forField(password)
                .asRequired("Password is required")
                .bind(LoginDto::getPassword, LoginDto::setPassword);

        addComponents(username, password);

    }
}
