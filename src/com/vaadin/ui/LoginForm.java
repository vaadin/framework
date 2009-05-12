package com.vaadin.ui;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;

/**
 * LoginForm is a Toolkit component to handle common problem among Ajax
 * applications: browsers password managers don't fill dynamically created forms
 * like all those UI elements created by IT Mill Toolkit.
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
 * <p>
 * Note, this is a new Ajax terminal specific component and is likely to change.
 * 
 * @since 5.3
 */
@SuppressWarnings("serial")
public class LoginForm extends CustomComponent {

    private Embedded iframe = new Embedded();

    private ApplicationResource loginPage = new ApplicationResource() {

        public Application getApplication() {
            return LoginForm.this.getApplication();
        }

        public int getBufferSize() {
            return getLoginHTML().length;
        }

        public long getCacheTime() {
            return -1;
        }

        public String getFilename() {
            return "login";
        }

        public DownloadStream getStream() {
            return new DownloadStream(new ByteArrayInputStream(getLoginHTML()),
                    getMIMEType(), getFilename());
        }

        public String getMIMEType() {
            return "text/html";
        }
    };

    private ParameterHandler paramHandler = new ParameterHandler() {

        public void handleParameters(Map parameters) {
            if (parameters.containsKey("username")) {
                getWindow().addURIHandler(uriHandler);

                HashMap params = new HashMap();
                // expecting single params
                for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
                    String key = (String) it.next();
                    String value = ((String[]) parameters.get(key))[0];
                    params.put(key, value);
                }
                LoginEvent event = new LoginEvent(params);
                fireEvent(event);
            }
        }
    };

    private URIHandler uriHandler = new URIHandler() {
        private final String responce = "<html><body>Login form handeled."
                + "<script type='text/javascript'>top.vaadin.forceSync();"
                + "</script></body></html>";

        public DownloadStream handleURI(URL context, String relativeUri) {
            if (relativeUri != null && relativeUri.contains("loginHandler")) {
                if (window != null) {
                    window.removeURIHandler(this);
                }
                DownloadStream downloadStream = new DownloadStream(
                        new ByteArrayInputStream(responce.getBytes()),
                        "text/html", "loginSuccesfull");
                downloadStream.setCacheTime(-1);
                return downloadStream;
            } else {
                return null;
            }
        }
    };

    private Window window;

    public LoginForm() {
        iframe.setType(Embedded.TYPE_BROWSER);
        iframe.setSizeFull();
        setSizeFull();
        setCompositionRoot(iframe);
    }

    /**
     * Returns byte array containing login page html. If you need to override
     * the login html, use the default html as basis. Login page sets its target
     * with javascript.
     * 
     * @return byte array containing login page html
     */
    protected byte[] getLoginHTML() {

        String theme = getApplication().getMainWindow().getTheme();
        String guessedThemeUri = getApplication().getURL() + "VAADIN/themes/"
                + (theme == null ? "default" : theme) + "/styles.css";
        String guessedThemeUri2 = getApplication().getURL()
                + "../VAADIN/themes/" + (theme == null ? "default" : theme)
                + "/styles.css";

        String appUri = getApplication().getURL().toString();

        return ("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                + "XHTML 1.0 Transitional//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/"
                + "DTD/xhtml1-transitional.dtd\">\n" + "<html>"
                + "<head><script type='text/javascript'>"
                + "var setTarget = function() {" + "var uri = '"
                + appUri
                + "loginHandler"
                + "'; var f = document.getElementById('loginf');"
                + "document.forms[0].action = uri;document.forms[0].username.focus();};"
                + "</script>"
                + "<link rel='stylesheet' href='"
                + guessedThemeUri
                + "'/>"
                + "<link rel='stylesheet' href='"
                + guessedThemeUri2
                + "'/>"
                + "</head><body onload='setTarget();' style='margin:0;padding:0;'>"
                + "<div class='v-app v-app-loginpage'>"
                + "<iframe name='logintarget' style='width:0;height:0;"
                + "border:0;margin:0;padding:0;'></iframe>"
                + "<form id='loginf' target='logintarget'>"
                + "<div>Username</div><div >"
                + "<input class='v-textfield' style='display:block;' type='text' name='username'></div>"
                + "<div>Password</div>"
                + "<div><input class='v-textfield' style='display:block;' type='password' name='password'></div>"
                + "<div><input class='v-button' type='submit' value='Login'></div></form></div>" + "</body></html>")
                .getBytes();
    }

    @Override
    public void attach() {
        super.attach();
        getApplication().addResource(loginPage);
        getWindow().addParameterHandler(paramHandler);
        iframe.setSource(loginPage);
    }

    @Override
    public void detach() {
        getApplication().removeResource(loginPage);
        getWindow().removeParameterHandler(paramHandler);
        // store window temporary to properly remove uri handler once
        // response is handled. (May happen if login handler removes login
        // form
        window = getWindow();
        if (window.getParent() != null) {
            window = (Window) window.getParent();
        }
        super.detach();
    }

    /**
     * This event is sent when login form is submitted.
     */
    public class LoginEvent extends Event {

        private Map params;

        private LoginEvent(Map params) {
            super(LoginForm.this);
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
                return (String) params.get(name);
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
    public void addListener(LoginListener listener) {
        addListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

    /**
     * Removes LoginListener
     * 
     * @param listener
     */
    public void removeListener(LoginListener listener) {
        removeListener(LoginEvent.class, listener, ON_LOGIN_METHOD);
    }

}
