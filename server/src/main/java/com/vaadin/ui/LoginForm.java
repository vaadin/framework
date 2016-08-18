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

package com.vaadin.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.loginform.LoginFormConstants;
import com.vaadin.shared.ui.loginform.LoginFormRpc;
import com.vaadin.shared.ui.loginform.LoginFormState;
import com.vaadin.v7.ui.LegacyTextField;

/**
 * Login form with auto-completion and auto-fill for all major browsers. You can
 * derive from this class and implement the
 * {@link #createContent(com.vaadin.v7.ui.LegacyTextField, com.vaadin.ui.PasswordField, com.vaadin.ui.Button)}
 * method to build the layout using the text fields and login button that are
 * passed to that method. The supplied components are specially treated so that
 * they work with password managers.
 * <p>
 * If you need to change the URL as part of the login procedure, call
 * {@link #setLoginMode(LoginMode)} with the argument {@link LoginMode#DEFERRED}
 * in your implementation of
 * {@link #createContent(com.vaadin.v7.ui.LegacyTextField, com.vaadin.ui.PasswordField, com.vaadin.ui.Button)
 * createContent}.
 * <p>
 * To customize the fields or to replace them with your own implementations, you
 * can override {@link #createUsernameField()}, {@link #createPasswordField()}
 * and {@link #createLoginButton()}. These methods are called automatically and
 * cannot be called by your code. Captions can be reset by overriding
 * {@link #getUsernameFieldCaption()}, {@link #getPasswordFieldCaption()} and
 * {@link #getLoginButtonCaption()}.
 * <p>
 * Note that the API of LoginForm changed significantly in Vaadin 7.7.
 *
 * @since 5.3
 */
public class LoginForm extends AbstractSingleComponentContainer {

    /**
     * This event is sent when login form is submitted.
     */
    public static class LoginEvent extends Event {

        private Map<String, String> params;

        private LoginEvent(Component source, Map<String, String> params) {
            super(source);
            this.params = params;
        }

        /**
         * Access method to form values by field names.
         *
         * @param name
         * @return value in given field
         */
        public String getLoginParameter(String name) {
            if (params.containsKey(name)) {
                return params.get(name);
            } else {
                return null;
            }
        }
    }

    /**
     * Login listener is a class capable to listen LoginEvents sent from
     * LoginBox
     */
    public interface LoginListener extends Serializable {
        /**
         * This method is fired on each login form post.
         *
         * @param event
         *            Login event
         */
        public void onLogin(LoginEvent event);
    }

    /**
     * Internal stream source for the login URL - always returns "Success" and
     * ignores the values received.
     */
    private static class LoginStreamSource
            implements StreamResource.StreamSource {
        @Override
        public InputStream getStream() {
            try {
                return new ByteArrayInputStream(
                        "<html>Success</html>".toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return null;
            }

        }
    }

    static {
        try {
            ON_LOGIN_METHOD = LoginListener.class.getDeclaredMethod("onLogin",
                    new Class[] { LoginEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in LoginForm");
        }
    }

    private static final Method ON_LOGIN_METHOD;

    private boolean initialized;

    private String usernameCaption = "Username";
    private String passwordCaption = "Password";
    private String loginButtonCaption = "Login";

    /**
     * Customize the user name field. Only for overriding, do not call.
     *
     * @return the user name field
     * @since 7.7
     */
    protected LegacyTextField createUsernameField() {
        checkInitialized();
        LegacyTextField field = new LegacyTextField(getUsernameCaption());
        field.focus();
        return field;
    }

    /**
     * Returns the caption set with {@link #setUsernameCaption(String)}. Note
     * that this method might not match what is shown to the user if
     * {@link #createUsernameField()} has been overridden.
     *
     * @return user name field caption
     */
    public String getUsernameCaption() {
        return usernameCaption;
    }

    /**
     * Set the caption of the user name field. Note that the caption can only be
     * set with this method before the login form has been initialized
     * (attached).
     * <p>
     * As an alternative to calling this method, the method
     * {@link #createUsernameField()} can be overridden.
     *
     * @param cap
     *            new caption
     */
    public void setUsernameCaption(String cap) {
        usernameCaption = cap;
    }

    /**
     * Customize the password field. Only for overriding, do not call.
     *
     * @return the password field
     * @since 7.7
     */
    protected PasswordField createPasswordField() {
        checkInitialized();
        return new PasswordField(getPasswordCaption());
    }

    /**
     * Returns the caption set with {@link #setPasswordCaption(String)}. Note
     * that this method might not match what is shown to the user if
     * {@link #createPasswordField()} has been overridden.
     *
     * @return password field caption
     */
    public String getPasswordCaption() {
        return passwordCaption;
    }

    /**
     * Set the caption of the password field. Note that the caption can only be
     * set with this method before the login form has been initialized
     * (attached).
     * <p>
     * As an alternative to calling this method, the method
     * {@link #createPasswordField()} can be overridden.
     *
     * @param cap
     *            new caption
     */
    public void setPasswordCaption(String cap) {
        passwordCaption = cap;
        ;
    }

    /**
     * Customize the login button. Only for overriding, do not call.
     *
     * @return the login button
     * @since 7.7
     */
    protected Button createLoginButton() {
        checkInitialized();
        return new Button(getLoginButtonCaption());
    }

    /**
     * Returns the caption set with {@link #setLoginButtonCaption(String)}. Note
     * that this method might not match what is shown to the user if
     * {@link #createLoginButton()} has been overridden.
     *
     * @return login button caption
     */
    public String getLoginButtonCaption() {
        return loginButtonCaption;
    }

    /**
     * Set the caption of the login button. Note that the caption can only be
     * set with this method before the login form has been initialized
     * (attached).
     * <p>
     * As an alternative to calling this method, the method
     * {@link #createLoginButton()} can be overridden.
     *
     * @param cap
     *            new caption
     */
    public void setLoginButtonCaption(String cap) {
        loginButtonCaption = cap;
    }

    @Override
    protected LoginFormState getState() {
        return (LoginFormState) super.getState();
    }

    @Override
    public void attach() {
        super.attach();
        init();
    }

    private void checkInitialized() {
        if (initialized) {
            throw new IllegalStateException(
                    "Already initialized. The create methods may not be called explicitly.");
        }
    }

    /**
     * Create the content for the login form with the supplied user name field,
     * password field and the login button. You cannot use any other text fields
     * or buttons for this purpose. To replace these components with your own
     * implementations, override {@link #createUsernameField()},
     * {@link #createPasswordField()} and {@link #createLoginButton()}. If you
     * only want to change the default captions, override
     * {@link #getUsernameFieldCaption()}, {@link #getPasswordFieldCaption()}
     * and {@link #getLoginButtonCaption()}. You do not have to use the login
     * button in your layout.
     *
     * @param userNameField
     *            the user name text field
     * @param passwordField
     *            the password field
     * @param loginButton
     *            the login button
     * @return content component
     * @since 7.7
     */
    protected Component createContent(LegacyTextField userNameField,
            PasswordField passwordField, Button loginButton) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(userNameField);
        layout.addComponent(passwordField);
        layout.addComponent(loginButton);
        return layout;
    }

    private void init() {
        if (initialized) {
            return;
        }

        LoginFormState state = getState();
        state.userNameFieldConnector = createUsernameField();
        state.passwordFieldConnector = createPasswordField();
        state.loginButtonConnector = createLoginButton();

        StreamResource resource = new StreamResource(new LoginStreamSource(),
                LoginFormConstants.LOGIN_RESOURCE_NAME);
        resource.setMIMEType("text/html; charset=utf-8");
        resource.setCacheTime(-1);
        setResource(LoginFormConstants.LOGIN_RESOURCE_NAME, resource);

        registerRpc(new LoginFormRpc() {
            @Override
            public void submitCompleted() {
                login();
            }
        });

        initialized = true;

        setContent(createContent(getUsernameField(), getPasswordField(),
                getLoginButton()));
    }

    private LegacyTextField getUsernameField() {
        return (LegacyTextField) getState().userNameFieldConnector;
    }

    private PasswordField getPasswordField() {
        return (PasswordField) getState().passwordFieldConnector;
    }

    private Button getLoginButton() {
        return (Button) getState().loginButtonConnector;
    }

    /*
     * (non-Javadoc)
     *
     * Handle the login. In deferred mode, this method is called after the dummy
     * POST request that triggers the password manager has been completed. In
     * direct mode (the default setting), it is called directly when the user
     * hits the enter key or clicks on the login button. In the latter case, you
     * cannot change the URL in the method or the password manager will not be
     * triggered.
     */
    private void login() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", getUsernameField().getValue());
        params.put("password", getPasswordField().getValue());
        LoginEvent event = new LoginEvent(LoginForm.this, params);
        fireEvent(event);
    }

    /**
     * Adds LoginListener to handle login logic
     *
     * @param listener
     */
    public void addLoginListener(LoginListener listener) {
        addListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addLoginListener(LoginListener)}
     **/
    @Deprecated
    public void addListener(LoginListener listener) {
        addLoginListener(listener);
    }

    /**
     * Removes LoginListener
     *
     * @param listener
     */
    public void removeLoginListener(LoginListener listener) {
        removeListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeLoginListener(LoginListener)}
     **/
    @Deprecated
    public void removeListener(LoginListener listener) {
        removeLoginListener(listener);
    }

}
