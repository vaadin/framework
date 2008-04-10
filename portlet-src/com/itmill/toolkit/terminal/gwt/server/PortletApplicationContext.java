/**
 * 
 */
package com.itmill.toolkit.terminal.gwt.server;

import java.io.File;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.ApplicationContext;

/**
 * @author marc
 * 
 */
public class PortletApplicationContext implements ApplicationContext {

    private final PortletSession session;

    private final Map portletInfoMap;

    PortletApplicationContext(PortletSession session) {
        this.session = session;
        portletInfoMap = new HashMap();
    }

    static public PortletApplicationContext getApplicationContext(
            PortletSession session) {
        PortletApplicationContext cx = (PortletApplicationContext) session
                .getAttribute(PortletApplicationContext.class.getName());
        if (cx == null) {
            cx = new PortletApplicationContext(session);
            session.setAttribute(PortletApplicationContext.class.getName(), cx,
                    PortletSession.APPLICATION_SCOPE);
        }
        return cx;
    }

    static public PortletApplicationContext getApplicationContext(
            HttpSession session) {
        PortletApplicationContext cx = (PortletApplicationContext) session
                .getAttribute(PortletApplicationContext.class.getName());
        return cx;
    }

    public PortletSession getPortletSession() {
        return session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#addTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
     */
    public void addTransactionListener(TransactionListener listener) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx != null) {
            cx.addTransactionListener(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#getApplications()
     */
    public Collection getApplications() {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx != null) {
            return cx.getApplications();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#getBaseDirectory()
     */
    public File getBaseDirectory() {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx != null) {
            return cx.getBaseDirectory();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.service.ApplicationContext#removeTransactionListener(com.itmill.toolkit.service.ApplicationContext.TransactionListener)
     */
    public void removeTransactionListener(TransactionListener listener) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx != null) {
            cx.removeTransactionListener(listener);
        }
    }

    PortletInfo setPortletInfo(String path, PortletMode mode,
            WindowState state, Principal userPrincipal, Map userInfo,
            PortletConfig config) {
        System.err.println("SETTING PI: " + path);
        PortletInfo pi = (PortletInfo) portletInfoMap.get(path);
        if (pi == null) {
            pi = new PortletInfo(mode, state, userPrincipal, userInfo, config);
            portletInfoMap.put(path, pi);
        } else {
            pi.setInfo(mode, state, userPrincipal, userInfo, config);
        }
        return pi;
    }

    public PortletInfo getPortletInfo(Application app) {
        if (app != null && app.getURL() != null) {
            // TODO remove
            System.err.println("GETTING PI: " + app.getURL().getPath());
            return (PortletInfo) portletInfoMap.get(app.getURL().getPath());
        }
        return null;
    }

    public class PortletInfo {

        PortletMode mode;
        WindowState state;
        Principal userPrincipal;
        Map userInfo;
        PortletConfig config;

        public PortletInfo(PortletMode mode, WindowState state,
                Principal userPrincipal, Map userInfo, PortletConfig config) {
            this.mode = mode;
            this.state = state;
            this.userPrincipal = userPrincipal;
            this.userInfo = userInfo;
            this.config = config;
        }

        private void setInfo(PortletMode mode, WindowState state,
                Principal userPrincipal, Map userInfo, PortletConfig config) {
            this.mode = mode;
            this.state = state;
            this.userPrincipal = userPrincipal;
            this.userInfo = userInfo;
            this.config = config;
        }

        /**
         * Gets the current portlet mode, VIEW / EDIT / HELP
         * 
         * @return the current portlet mode
         */
        public PortletMode getPortletMode() {
            return mode;
        }

        /**
         * Gets the current window state, NORMAL / MAXIMIZED / MINIMIZED
         * 
         * @return the current window state
         */
        public WindowState getWindowState() {
            return state;
        }

        /**
         * Gets the current UserPrincipal
         * 
         * @return current UserPrincipal, null if not logged in
         */
        public Principal getUserPrincipal() {
            return userPrincipal;
        }

        /**
         * Gets the PortletConfig for this portlet
         * 
         * @return the PortletConfig
         */
        public PortletConfig getConfig() {
            return config;
        }

        /**
         * Gets the user info for this portlet, as retreived from
         * request.getAttribute(PortletRequest.USER_INFO);
         * 
         * @return the user info Map
         */
        public Map getUserInfo() {
            return userInfo;
        }

        public String toString() {
            return "PortletMode: " + getPortletMode() + " WindowState: "
                    + getWindowState() + " UserPrincipal: "
                    + getUserPrincipal() + " User info: " + getUserInfo();
        }
    }

    public interface PortletInfoReceiver {
        public void receivePortletInfo(PortletInfo info);
    }
}
