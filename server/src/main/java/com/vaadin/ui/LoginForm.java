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
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.StreamResource;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.loginform.LoginFormConstants;
import com.vaadin.shared.ui.loginform.LoginFormRpc;
import com.vaadin.shared.ui.loginform.LoginFormState;
import com.vaadin.util.ReflectTools;

/**
 * Login form with auto-completion and auto-fill for all major browsers. You can
 * derive from this class and implement the
 * {@link #createContent(com.vaadin.ui.TextField, com.vaadin.ui.PasswordField, com.vaadin.ui.Button)}
 * method to build the layout using the text fields and login button that are
 * passed to that method. The supplied components are specially treated so that
 * they work with password managers.
 * <p>
 * To customize the fields or to replace them with your own implementations, you
 * can override {@link #createUsernameField()}, {@link #createPasswordField()}
 * and {@link #createLoginButton()}. These methods are called automatically and
 * cannot be called by your code. Captions can be reset by overriding
 * {@link #getUsernameCaption()}, {@link #getPasswordCaption()} and
 * {@link #getLoginButtonCaption()}.
 * <p>
 * Note that the API of LoginForm changed significantly in Vaadin 7.7.
 *
 * @since 5.3
 */
public class LoginForm extends AbstractSingleComponentContainer {

    /**
     * Event sent when the login form is submitted.
     */
    public static class LoginEvent extends Component.Event {

        private final Map<String, String> params;

        /**
         * Creates a login event using the given source and the given
         * parameters.
         *
         * @param source
         *            the source of the event
         * @param params
         */
        private LoginEvent(LoginForm source, Map<String, String> params) {
            super(source);
            this.params = params;
        }

        @Override
        public LoginForm getSource() {
            return (LoginForm) super.getSource();
        }

        /**
         * Gets the login parameter with the given name.
         *
         * @param name
         *            the name of the parameter
         * @return the value of the parameter or null if no such parameter is
         *         present
         */
        public String getLoginParameter(String name) {
            return params.get(name);
        }
    }

    /**
     * Listener triggered when a login occurs in a {@link LoginForm}.
     */
    @FunctionalInterface
    public interface LoginListener extends Serializable {
        /**
         * Event method invoked when the login button is pressed in a login
         * form.
         *
         * @param event
         *            the login event
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
            return new ByteArrayInputStream(
                    "<html>Success</html>".getBytes(StandardCharsets.UTF_8));
        }
    }

    private static final Method ON_LOGIN_METHOD = ReflectTools
            .findMethod(LoginListener.class, "onLogin", LoginEvent.class);

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
    protected TextField createUsernameField() {
        throwIfInitialized();
        TextField field = new TextField(getUsernameCaption());
        field.focus();
        return field;
    }

    /**
     * Gets the caption set with {@link #setUsernameCaption(String)}. Note that
     * this method might not match what is shown to the user if
     * {@link #createUsernameField()} has been overridden.
     *
     * @return the user name field caption
     */
    public String getUsernameCaption() {
        return usernameCaption;
    }

    /**
     * Sets the caption of the user name field. Note that the caption can only
     * be set with this method before the login form has been initialized
     * (attached).
     * <p>
     * As an alternative to calling this method, the method
     * {@link #createUsernameField()} can be overridden.
     *
     * @param usernameCaption
     *            the caption to set for the user name field
     */
    public void setUsernameCaption(String usernameCaption) {
        this.usernameCaption = usernameCaption;
    }

    /**
     * Customize the password field. Only for overriding, do not call.
     *
     * @return the password field
     * @since 7.7
     */
    protected PasswordField createPasswordField() {
        throwIfInitialized();
        return new PasswordField(getPasswordCaption());
    }

    /**
     * Gets the caption set with {@link #setPasswordCaption(String)}. Note that
     * this method might not match what is shown to the user if
     * {@link #createPasswordField()} has been overridden.
     *
     *
     * @return the password field caption
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
     * @param passwordCaption
     *            the caption for the password field
     */
    public void setPasswordCaption(String passwordCaption) {
        this.passwordCaption = passwordCaption;
    }

    /**
     * Customize the login button. Only for overriding, do not call.
     *
     * @return the login button
     * @since 7.7
     */
    protected Button createLoginButton() {
        throwIfInitialized();
        return new Button(getLoginButtonCaption());
    }

    /**
     * Gets the caption set with {@link #setLoginButtonCaption(String)}. Note
     * that this method might not match what is shown to the user if
     * {@link #createLoginButton()} has been overridden.
     *
     * @return the login button caption
     */
    public String getLoginButtonCaption() {
        return loginButtonCaption;
    }

    /**
     * Sets the caption of the login button. Note that the caption can only be
     * set with this method before the login form has been initialized
     * (attached).
     * <p>
     * As an alternative to calling this method, the method
     * {@link #createLoginButton()} can be overridden.
     *
     * @param loginButtonCaption
     *            new caption
     */
    public void setLoginButtonCaption(String loginButtonCaption) {
        this.loginButtonCaption = loginButtonCaption;
    }

    @Override
    protected LoginFormState getState() {
        return (LoginFormState) super.getState();
    }

    @Override
    protected LoginFormState getState(boolean markAsDirty) {
        return (LoginFormState) super.getState(markAsDirty);
    }

    @Override
    public void attach() {
        super.attach();
        init();
    }

    private void throwIfInitialized() {
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
     * {@link #getUsernameCaption()}, {@link #getPasswordCaption()} and
     * {@link #getLoginButtonCaption()}. You do not have to use the login button
     * in your layout.
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
    protected Component createContent(TextField userNameField,
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
        resource.setMIMEType(ApplicationConstants.CONTENT_TYPE_TEXT_HTML_UTF_8);
        resource.setCacheTime(-1);
        setResource(LoginFormConstants.LOGIN_RESOURCE_NAME, resource);

        registerRpc((LoginFormRpc) this::login);

        initialized = true;

        setContent(createContent(getUsernameField(), getPasswordField(),
                getLoginButton()));
    }

    private TextField getUsernameField() {
        assert initialized;
        return (TextField) getState(false).userNameFieldConnector;
    }

    private PasswordField getPasswordField() {
        assert initialized;
        return (PasswordField) getState(false).passwordFieldConnector;
    }

    private Button getLoginButton() {
        assert initialized;
        return (Button) getState(false).loginButtonConnector;
    }

    /**
     * Handles the login.
     * <p>
     * In deferred mode, this method is called after the dummy POST request that
     * triggers the password manager has been completed. In direct mode (the
     * default setting), it is called directly when the user hits the enter key
     * or clicks on the login button. In the latter case, you cannot change the
     * URL in the method or the password manager will not be triggered.
     */
    private void login() {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", getUsernameField().getValue());
        params.put("password", getPasswordField().getValue());
        LoginEvent event = new LoginEvent(LoginForm.this, params);
        fireEvent(event);
    }

    /**
     * Adds a {@link LoginListener}.
     * <p>
     * The listener is called when the user presses the login button.
     *
     * @param listener
     *            the listener to add
     * @return a registration object for removing the listener
     * @since 8.0
     */
    public Registration addLoginListener(LoginListener listener) {
        return addListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

    /**
     * Removes a {@link LoginListener}.
     *
     * @param listener
     *            the listener to remove
     * @deprecated As of 8.0, replaced by {@link Registration#remove()} in the
     *             registration object returned from
     *             {@link #addLoginListener(LoginListener)}.
     */
    @Deprecated
    public void removeLoginListener(LoginListener listener) {
        removeListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

}
