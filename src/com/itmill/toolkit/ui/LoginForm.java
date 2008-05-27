package com.itmill.toolkit.ui;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ApplicationResource;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.URIHandler;

/**
 * LoginForm is a Toolkit component to handle common problem among Ajax
 * applications: browsers password managers don't fill dynamically created forms
 * like all those UI elements created by IT Mill Toolkit.
 * 
 * For developer it is easy to use: add component to a desired place in you UI
 * and add LoginListener to validate form input. Behind the curtain LoginForm
 * creates an iframe with static html that browsers detect.
 * 
 * Login page html can be overridden by replacing protected getLoginHTML method.
 * As the login page is actually an iframe, styles must be handled manually. By
 * default component tries to guess the right place for theme css.
 * 
 * Note, this is a new Ajax terminal specific component and is likely to change.
 * 
 * @since 5.3
 */
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
                + "<script type='text/javascript'>top.itmill.forceSync();"
                + "</script></body></html>";

        public DownloadStream handleURI(URL context, String relativeUri) {
            if (window != null) {
                window.removeURIHandler(this);
            }
            DownloadStream downloadStream = new DownloadStream(
                    new ByteArrayInputStream(responce.getBytes()), "text/html",
                    "loginSuccesfull");
            downloadStream.setCacheTime(-1);
            return downloadStream;
        }
    };

    private Window window;

    public LoginForm() {
        iframe.setType(Embedded.TYPE_BROWSER);
        iframe.setSizeFull();
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
        String guessedThemeUri = getApplication().getURL() + "ITMILL/themes/"
                + (theme == null ? "default" : theme) + "/styles.css";
        String guessedThemeUri2 = getApplication().getURL()
                + "../ITMILL/themes/" + (theme == null ? "default" : theme)
                + "/styles.css";

        return (""
                + "<html>"
                + "<head><script type='text/javascript'>"
                + "var setTarget = function() {"
                + "var uri = top.location.href;"
                + "uri = uri.replace(/\\\\?.*/, '');"
                + "uri += /loginHandler;"
                + ".action = uri;document.forms[0].username.focus();};"
                + "</script>"
                + "<link rel='stylesheet' href='"
                + guessedThemeUri
                + "'/>"
                + "<link rel='stylesheet' href='"
                + guessedThemeUri2
                + "'/>"
                + "</head><body onload='setTarget();' class='i-app i-app-loginpage' style='margin:0;padding:0;'>"
                + "<form id='loginf' target='logintarget'>"
                + "Username<br/> <input class='i-textfield' type='text' name='username'><br/>"
                + "Password<br/><input class='i-textfield' type='password' name='password'><br/>"
                + "<input class='i-button' type='submit' value='Login'></form>"
                + "<iframe name='logintarget' style='width:0;height:0;" + "border:0;margin:0;padding:0;'></iframe></body></html>")
                .getBytes();
    }

    public void attach() {
        super.attach();
        getApplication().addResource(loginPage);
        getWindow().addParameterHandler(paramHandler);
        iframe.setSource(loginPage);
    }

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

        private static final long serialVersionUID = 1966036438671224308L;

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
    public interface LoginListener {
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
            throw new java.lang.RuntimeException();
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
