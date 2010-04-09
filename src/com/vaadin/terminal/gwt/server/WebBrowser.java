/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.util.Locale;

import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.client.VBrowserDetails;

/**
 * Class that provides information about the web browser the user is using.
 * Provides information such as browser name and version, screen resolution and
 * IP address.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 */
public class WebBrowser implements Terminal {

    private int screenHeight = 0;
    private int screenWidth = 0;
    private String browserApplication = null;
    private Locale locale;
    private String address;
    private boolean secureConnection;

    private VBrowserDetails browserDetails;

    /**
     * There is no default-theme for this terminal type.
     * 
     * @return Always returns null.
     */
    public String getDefaultTheme() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Terminal#getScreenHeight()
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.Terminal#getScreenWidth()
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Get the browser user-agent string.
     * 
     * @return The raw browser userAgent string
     */
    public String getBrowserApplication() {
        return browserApplication;
    }

    /**
     * For internal use by AbstractApplicationServlet/AbstractApplicationPortlet
     * only. Updates all properties in the class according to the given
     * information.
     * 
     * @param locale
     *            The browser primary locale
     * @param address
     *            The browser ip address
     * @param secureConnection
     *            true if using an https connection
     * @param agent
     *            Raw userAgent string from the browser
     * @param sw
     *            Screen width
     * @param sh
     *            Screen height
     */
    void updateBrowserProperties(Locale locale, String address,
            boolean secureConnection, String agent, String sw, String sh) {
        this.locale = locale;
        this.address = address;
        this.secureConnection = secureConnection;
        if (agent != null) {
            browserApplication = agent;
            browserDetails = new VBrowserDetails(agent);
        }

        if (sw != null) {
            try {
                screenHeight = Integer.parseInt(sh);
                screenWidth = Integer.parseInt(sw);
            } catch (final NumberFormatException e) {
                screenHeight = screenWidth = 0;
            }
        }
    }

    /**
     * Gets the IP-address of the web browser. If the application is running
     * inside a portlet, this method will return null.
     * 
     * @return IP-address in 1.12.123.123 -format
     */
    public String getAddress() {
        return address;
    }

    /** Get the default locate of the browser. */
    public Locale getLocale() {
        return locale;
    }

    /** Is the connection made using HTTPS? */
    public boolean isSecureConnection() {
        return secureConnection;
    }

    /**
     * Tests whether the user is using Firefox.
     * 
     * @return true if the user is using Firefox, false if the user is not using
     *         Firefox or if no information on the browser is present
     */
    public boolean isFirefox() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isFirefox();
    }

    /**
     * Tests whether the user is using Internet Explorer.
     * 
     * @return true if the user is using Internet Explorer, false if the user is
     *         not using Internet Explorer or if no information on the browser
     *         is present
     */
    public boolean isIE() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isIE();
    }

    /**
     * Tests whether the user is using Safari.
     * 
     * @return true if the user is using Safari, false if the user is not using
     *         Safari or if no information on the browser is present
     */
    public boolean isSafari() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isSafari();
    }

    /**
     * Tests whether the user is using Opera.
     * 
     * @return true if the user is using Opera, false if the user is not using
     *         Opera or if no information on the browser is present
     */
    public boolean isOpera() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isOpera();
    }

    /**
     * Tests whether the user is using Chrome.
     * 
     * @return true if the user is using Chrome, false if the user is not using
     *         Chrome or if no information on the browser is present
     */
    public boolean isChrome() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isChrome();
    }

    /**
     * Gets the major version of the browser the user is using.
     * 
     * <p>
     * Note that Internet Explorer in IE7 compatibility mode might return 8 in
     * some cases even though it should return 7.
     * </p>
     * 
     * @return The major version of the browser or -1 if not known.
     */
    public int getBrowserMajorVersion() {
        if (browserDetails == null) {
            return -1;
        }

        return browserDetails.getBrowserMajorVersion();
    }

    /**
     * Gets the minor version of the browser the user is using.
     * 
     * @see #getBrowserMajorVersion()
     * 
     * @return The minor version of the browser or -1 if not known.
     */
    public int getBrowserMinorVersion() {
        if (browserDetails == null) {
            return -1;
        }

        return browserDetails.getBrowserMinorVersion();
    }

    /**
     * Tests whether the user is using Linux.
     * 
     * @return true if the user is using Linux, false if the user is not using
     *         Linux or if no information on the browser is present
     */
    public boolean isLinux() {
        return browserDetails.isLinux();
    }

    /**
     * Tests whether the user is using Mac OS X.
     * 
     * @return true if the user is using Mac OS X, false if the user is not
     *         using Mac OS X or if no information on the browser is present
     */
    public boolean isMacOSX() {
        return browserDetails.isMacOSX();
    }

    /**
     * Tests whether the user is using Windows.
     * 
     * @return true if the user is using Windows, false if the user is not using
     *         Windows or if no information on the browser is present
     */
    public boolean isWindows() {
        return browserDetails.isWindows();
    }

}
