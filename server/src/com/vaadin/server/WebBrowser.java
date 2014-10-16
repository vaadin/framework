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

package com.vaadin.server;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.shared.VBrowserDetails;

/**
 * Class that provides information about the web browser the user is using.
 * Provides information such as browser name and version, screen resolution and
 * IP address.
 * 
 * @author Vaadin Ltd.
 */
public class WebBrowser implements Serializable {

    private int screenHeight = -1;
    private int screenWidth = -1;
    private String browserApplication = null;
    private Locale locale;
    private String address;
    private boolean secureConnection;
    private int timezoneOffset = 0;
    private int rawTimezoneOffset = 0;
    private int dstSavings;
    private boolean dstInEffect;
    private boolean touchDevice;

    private VBrowserDetails browserDetails;
    private long clientServerTimeDelta;

    /**
     * Gets the height of the screen in pixels. This is the full screen
     * resolution and not the height available for the application.
     * 
     * @return the height of the screen in pixels.
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Gets the width of the screen in pixels. This is the full screen
     * resolution and not the width available for the application.
     * 
     * @return the width of the screen in pixels.
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
     * Tests whether the user is using Chrome Frame.
     * 
     * @return true if the user is using Chrome Frame, false if the user is not
     *         using Chrome or if no information on the browser is present
     */
    public boolean isChromeFrame() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isChromeFrame();
    }

    /**
     * Tests whether the user's browser is Chrome Frame capable.
     * 
     * @return true if the user can use Chrome Frame, false if the user can not
     *         or if no information on the browser is present
     */
    public boolean isChromeFrameCapable() {
        if (browserDetails == null) {
            return false;
        }

        return browserDetails.isChromeFrameCapable();
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

    /**
     * Tests whether the user is using Windows Phone.
     * 
     * @return true if the user is using Windows Phone, false if the user is not
     *         using Windows Phone or if no information on the browser is
     *         present
     * @since 7.3.2
     */
    public boolean isWindowsPhone() {
        return browserDetails.isWindowsPhone();
    }

    /**
     * Tests if the browser is run on Android.
     * 
     * @return true if run on Android false if the user is not using Android or
     *         if no information on the browser is present
     */
    public boolean isAndroid() {
        return browserDetails.isAndroid();
    }

    /**
     * Tests if the browser is run in iOS.
     * 
     * @return true if run in iOS false if the user is not using iOS or if no
     *         information on the browser is present
     */
    public boolean isIOS() {
        return browserDetails.isIOS();
    }

    /**
     * Tests if the browser is run on IPhone.
     * 
     * @return true if run on IPhone false if the user is not using IPhone or if
     *         no information on the browser is present
     * @since 7.3.3
     */
    public boolean isIPhone() {
        return browserDetails.isIPhone();
    }

    /**
     * Tests if the browser is run on IPad.
     * 
     * @return true if run on IPad false if the user is not using IPad or if no
     *         information on the browser is present
     * @since 7.3.3
     */
    public boolean isIPad() {
        return browserDetails.isIPad();
    }

    /**
     * Returns the browser-reported TimeZone offset in milliseconds from GMT.
     * This includes possible daylight saving adjustments, to figure out which
     * TimeZone the user actually might be in, see
     * {@link #getRawTimezoneOffset()}.
     * 
     * @see WebBrowser#getRawTimezoneOffset()
     * @return timezone offset in milliseconds, 0 if not available
     */
    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    /**
     * Returns the browser-reported TimeZone offset in milliseconds from GMT
     * ignoring possible daylight saving adjustments that may be in effect in
     * the browser.
     * <p>
     * You can use this to figure out which TimeZones the user could actually be
     * in by calling {@link TimeZone#getAvailableIDs(int)}.
     * </p>
     * <p>
     * If {@link #getRawTimezoneOffset()} and {@link #getTimezoneOffset()}
     * returns the same value, the browser is either in a zone that does not
     * currently have daylight saving time, or in a zone that never has daylight
     * saving time.
     * </p>
     * 
     * @return timezone offset in milliseconds excluding DST, 0 if not available
     */
    public int getRawTimezoneOffset() {
        return rawTimezoneOffset;
    }

    /**
     * Returns the offset in milliseconds between the browser's GMT TimeZone and
     * DST.
     * 
     * @return the number of milliseconds that the TimeZone shifts when DST is
     *         in effect
     */
    public int getDSTSavings() {
        return dstSavings;
    }

    /**
     * Returns whether daylight saving time (DST) is currently in effect in the
     * region of the browser or not.
     * 
     * @return true if the browser resides at a location that currently is in
     *         DST
     */
    public boolean isDSTInEffect() {
        return dstInEffect;
    }

    /**
     * Returns the current date and time of the browser. This will not be
     * entirely accurate due to varying network latencies, but should provide a
     * close-enough value for most cases. Also note that the returned Date
     * object uses servers default time zone, not the clients.
     * <p>
     * To get the actual date and time shown in the end users computer, you can
     * do something like:
     * 
     * <pre>
     * WebBrowser browser = ...;
     * SimpleTimeZone timeZone = new SimpleTimeZone(browser.getTimezoneOffset(), "Fake client time zone");
     * DateFormat format = DateFormat.getDateTimeInstance();
     * format.setTimeZone(timeZone);
     * myLabel.setValue(format.format(browser.getCurrentDate()));
     * </pre>
     * 
     * @return the current date and time of the browser.
     * @see #isDSTInEffect()
     * @see #getDSTSavings()
     * @see #getTimezoneOffset()
     */
    public Date getCurrentDate() {
        return new Date(new Date().getTime() + clientServerTimeDelta);
    }

    /**
     * @return true if the browser is detected to support touch events
     */
    public boolean isTouchDevice() {
        return touchDevice;
    }

    /**
     * For internal use by VaadinServlet/VaadinPortlet only. Updates all
     * properties in the class according to the given information.
     * 
     * @param sw
     *            Screen width
     * @param sh
     *            Screen height
     * @param tzo
     *            TimeZone offset in minutes from GMT
     * @param rtzo
     *            raw TimeZone offset in minutes from GMT (w/o DST adjustment)
     * @param dstSavings
     *            the difference between the raw TimeZone and DST in minutes
     * @param dstInEffect
     *            is DST currently active in the region or not?
     * @param curDate
     *            the current date in milliseconds since the epoch
     * @param touchDevice
     */
    void updateClientSideDetails(String sw, String sh, String tzo, String rtzo,
            String dstSavings, String dstInEffect, String curDate,
            boolean touchDevice) {
        if (sw != null) {
            try {
                screenHeight = Integer.parseInt(sh);
                screenWidth = Integer.parseInt(sw);
            } catch (final NumberFormatException e) {
                screenHeight = screenWidth = -1;
            }
        }
        if (tzo != null) {
            try {
                // browser->java conversion: min->ms, reverse sign
                timezoneOffset = -Integer.parseInt(tzo) * 60 * 1000;
            } catch (final NumberFormatException e) {
                timezoneOffset = 0; // default gmt+0
            }
        }
        if (rtzo != null) {
            try {
                // browser->java conversion: min->ms, reverse sign
                rawTimezoneOffset = -Integer.parseInt(rtzo) * 60 * 1000;
            } catch (final NumberFormatException e) {
                rawTimezoneOffset = 0; // default gmt+0
            }
        }
        if (dstSavings != null) {
            try {
                // browser->java conversion: min->ms
                this.dstSavings = Integer.parseInt(dstSavings) * 60 * 1000;
            } catch (final NumberFormatException e) {
                this.dstSavings = 0; // default no savings
            }
        }
        if (dstInEffect != null) {
            this.dstInEffect = Boolean.parseBoolean(dstInEffect);
        }
        if (curDate != null) {
            try {
                long curTime = Long.parseLong(curDate);
                clientServerTimeDelta = curTime - new Date().getTime();
            } catch (final NumberFormatException e) {
                clientServerTimeDelta = 0;
            }
        }
        this.touchDevice = touchDevice;

    }

    /**
     * For internal use by VaadinServlet/VaadinPortlet only. Updates all
     * properties in the class according to the given information.
     * 
     * @param request
     *            the Vaadin request to read the information from
     */
    public void updateRequestDetails(VaadinRequest request) {
        locale = request.getLocale();
        address = request.getRemoteAddr();
        secureConnection = request.isSecure();
        String agent = request.getHeader("user-agent");

        if (agent != null) {
            browserApplication = agent;
            browserDetails = new VBrowserDetails(agent);
        }

        if (request.getParameter("v-sw") != null) {
            updateClientSideDetails(request.getParameter("v-sw"),
                    request.getParameter("v-sh"),
                    request.getParameter("v-tzo"),
                    request.getParameter("v-rtzo"),
                    request.getParameter("v-dstd"),
                    request.getParameter("v-dston"),
                    request.getParameter("v-curdate"),
                    request.getParameter("v-td") != null);
        }
    }

    /**
     * Checks if the browser is so old that it simply won't work with a Vaadin
     * application. Can be used to redirect to an alternative page, show
     * alternative content or similar.
     * 
     * When this method returns true chances are very high that the browser
     * won't work and it does not make sense to direct the user to the Vaadin
     * application.
     * 
     * @return true if the browser won't work, false if not the browser is
     *         supported or might work
     */
    public boolean isTooOldToFunctionProperly() {
        if (browserDetails == null) {
            // Don't know, so assume it will work
            return false;
        }

        return browserDetails.isTooOldToFunctionProperly();
    }

}
