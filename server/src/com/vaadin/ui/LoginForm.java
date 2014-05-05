/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.server.ConnectorResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.shared.ApplicationConstants;

/**
 * LoginForm is a Vaadin component to handle common problem among Ajax
 * applications: browsers password managers don't fill dynamically created forms
 * like all those UI elements created by Vaadin.
 * <p>
 * For developer it is easy to use: add component to a desired place in you UI
 * and add LoginListener to validate form input. Behind the curtain LoginForm
 * creates an iframe with static html that browsers detect.
 * <p>
 * Login form is by default 100% width and height, so consider using it inside a
 * sized {@link Panel} or {@link Window}.
 * <p>
 * Login page html can be overridden by replacing protected getLoginHTML method.
 * As the login page is actually an iframe, styles must be handled manually. By
 * default component tries to guess the right place for theme css.
 * 
 * @since 5.3
 * @deprecated As of 7.0. This component no longer fulfills its duty reliably in
 *             the supported browsers and a {@link VerticalLayout} with two
 *             {@link TextField}s can be used instead.
 */
@Deprecated
public class LoginForm extends CustomComponent {

    private String usernameCaption = "Username";
    private String passwordCaption = "Password";
    private String loginButtonCaption = "Login";

    private Embedded iframe = new Embedded();

    @Override
    public boolean handleConnectorRequest(final VaadinRequest request,
            final VaadinResponse response, String path) throws IOException {
        if (!path.equals("login")) {
            return super.handleConnectorRequest(request, response, path);
        }
        final StringBuilder responseBuilder = new StringBuilder();

        getUI().accessSynchronously(new Runnable() {
            @Override
            public void run() {
                String method = VaadinServletService.getCurrentServletRequest()
                        .getMethod();
                if (method.equalsIgnoreCase("post")) {
                    responseBuilder.append(handleLogin(request));
                } else {
                    responseBuilder.append(getLoginHTML());
                }
            }
        });

        if (responseBuilder.length() > 0) {
            response.setContentType("text/html; charset=utf-8");
            response.setCacheTime(-1);
            response.getWriter().write(responseBuilder.toString());
            return true;
        } else {
            return false;
        }
    }

    private String handleLogin(VaadinRequest request) {
        // Ensure UI.getCurrent() works in listeners

        Map<String, String[]> parameters = VaadinService.getCurrentRequest()
                .getParameterMap();

        HashMap<String, String> params = new HashMap<String, String>();
        // expecting single params
        for (Iterator<String> it = parameters.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            String value = (parameters.get(key))[0];
            params.put(key, value);
        }
        LoginEvent event = new LoginEvent(LoginForm.this, params);
        fireEvent(event);

        return "<html><body>Login form handled."
                + "<script type='text/javascript'>parent.parent.vaadin.forceSync();"
                + "</script></body></html>";
    }

    public LoginForm() {
        iframe.setType(Embedded.TYPE_BROWSER);
        iframe.setSizeFull();
        setSizeFull();
        setCompositionRoot(iframe);
        addStyleName("v-loginform");
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        // Generate magic URL now when UI id and connectorId are known
        iframe.setSource(new ExternalResource(
                ApplicationConstants.APP_PROTOCOL_PREFIX
                        + ApplicationConstants.APP_PATH + '/'
                        + ConnectorResource.CONNECTOR_PATH + '/'
                        + getUI().getUIId() + '/' + getConnectorId() + "/login"));
        super.beforeClientResponse(initial);
    }

    /**
     * Returns byte array containing login page html. If you need to override
     * the login html, use the default html as basis. Login page sets its target
     * with javascript.
     * 
     * @return byte array containing login page html
     */
    protected String getLoginHTML() {
        return "<!DOCTYPE html>\n"
                + "<html>"
                + "<head><script type='text/javascript'>"
                + "var setTarget = function() {"
                + "var uri = window.location;"
                + "var f = document.getElementById('loginf');"
                + "document.forms[0].action = uri;document.forms[0].username.focus();};"
                + ""
                + "var styles = window.parent.document.styleSheets;"
                + "for(var j = 0; j < styles.length; j++) {\n"
                + "if(styles[j].href) {"
                + "var stylesheet = document.createElement('link');\n"
                + "stylesheet.setAttribute('rel', 'stylesheet');\n"
                + "stylesheet.setAttribute('type', 'text/css');\n"
                + "stylesheet.setAttribute('href', styles[j].href);\n"
                + "document.getElementsByTagName('head')[0].appendChild(stylesheet);\n"
                + "}"
                + "}\n"
                + "function submitOnEnter(e) { var keycode = e.keyCode || e.which;"
                + " if (keycode == 13) {document.forms[0].submit();}  } \n"
                + "</script>"
                + "</head><body onload='setTarget();' style='margin:0;padding:0; background:transparent;' class=\""
                + ApplicationConstants.GENERATED_BODY_CLASSNAME
                + "\">"
                + "<div class='v-app v-app-loginpage "
                + getUIThemeClassName()
                + "' style=\"background:transparent;\">"
                + "<iframe name='logintarget' style='width:0;height:0;"
                + "border:0;margin:0;padding:0;display:block'></iframe>"
                + "<form id='loginf' target='logintarget' onkeypress=\"submitOnEnter(event)\" method=\"post\">"
                + "<div>"
                + usernameCaption
                + "</div><div >"
                + "<input class='v-textfield v-widget' style='display:block;' type='text' name='username'></div>"
                + "<div>"
                + passwordCaption
                + "</div>"
                + "<div><input class='v-textfield v-widget' style='display:block;' type='password' name='password'></div>"
                + "<div><div onclick=\"document.forms[0].submit();\" tabindex=\"0\" class=\"v-button\" role=\"button\" ><span class=\"v-button-wrap\"><span class=\"v-button-caption\">"
                + loginButtonCaption
                + "</span></span></div></div></form></div>" + "</body></html>";
    }

    private String getUIThemeClassName() {
        if (getUI() != null) {
            return getUI().getTheme();
        }
        return "";
    }

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
         */
        public void onLogin(LoginForm.LoginEvent event);
    }

    private static final Method ON_LOGIN_METHOD;

    private static final String UNDEFINED_HEIGHT = "140px";
    private static final String UNDEFINED_WIDTH = "200px";

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

    @Override
    public void setWidth(float width, Unit unit) {
        super.setWidth(width, unit);
        if (iframe != null) {
            if (width < 0) {
                iframe.setWidth(UNDEFINED_WIDTH);
            } else {
                iframe.setWidth("100%");
            }
        }
    }

    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit);
        if (iframe != null) {
            if (height < 0) {
                iframe.setHeight(UNDEFINED_HEIGHT);
            } else {
                iframe.setHeight("100%");
            }
        }
    }

    /**
     * Returns the caption for the user name field.
     * 
     * @return String
     */
    public String getUsernameCaption() {
        return usernameCaption;
    }

    /**
     * Sets the caption to show for the user name field. The caption cannot be
     * changed after the form has been shown to the user.
     * 
     * @param usernameCaption
     */
    public void setUsernameCaption(String usernameCaption) {
        this.usernameCaption = usernameCaption;
    }

    /**
     * Returns the caption for the password field.
     * 
     * @return String
     */
    public String getPasswordCaption() {
        return passwordCaption;
    }

    /**
     * Sets the caption to show for the password field. The caption cannot be
     * changed after the form has been shown to the user.
     * 
     * @param passwordCaption
     */
    public void setPasswordCaption(String passwordCaption) {
        this.passwordCaption = passwordCaption;
    }

    /**
     * Returns the caption for the login button.
     * 
     * @return String
     */
    public String getLoginButtonCaption() {
        return loginButtonCaption;
    }

    /**
     * Sets the caption (button text) to show for the login button. The caption
     * cannot be changed after the form has been shown to the user.
     * 
     * @param loginButtonCaption
     */
    public void setLoginButtonCaption(String loginButtonCaption) {
        this.loginButtonCaption = loginButtonCaption;
    }

}
