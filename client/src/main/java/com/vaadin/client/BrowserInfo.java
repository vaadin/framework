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

package com.vaadin.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.shared.VBrowserDetails;

/**
 * Class used to query information about web browser.
 * 
 * Browser details are detected only once and those are stored in this singleton
 * class.
 * 
 */
public class BrowserInfo {

    private static final String BROWSER_OPERA = "op";
    private static final String BROWSER_IE = "ie";
    private static final String BROWSER_EDGE = "edge";
    private static final String BROWSER_FIREFOX = "ff";
    private static final String BROWSER_SAFARI = "sa";

    public static final String ENGINE_GECKO = "gecko";
    public static final String ENGINE_WEBKIT = "webkit";
    public static final String ENGINE_PRESTO = "presto";
    public static final String ENGINE_TRIDENT = "trident";

    private static final String OS_WINDOWS = "win";
    private static final String OS_LINUX = "lin";
    private static final String OS_MACOSX = "mac";
    private static final String OS_ANDROID = "android";
    private static final String OS_IOS = "ios";

    // Common CSS class for all touch devices
    private static final String UI_TOUCH = "touch";

    private static BrowserInfo instance;

    private static String cssClass = null;

    static {
        // Add browser dependent v-* classnames to body to help css hacks
        String browserClassnames = get().getCSSClass();
        RootPanel.get().addStyleName(browserClassnames);
    }

    /**
     * Singleton method to get BrowserInfo object.
     * 
     * @return instance of BrowserInfo object
     */
    public static BrowserInfo get() {
        if (instance == null) {
            instance = new BrowserInfo();
        }
        return instance;
    }

    private VBrowserDetails browserDetails;
    private boolean touchDevice;

    private BrowserInfo() {
        browserDetails = new VBrowserDetails(getBrowserString());
        if (browserDetails.isIE()) {
            // Use document mode instead user agent to accurately detect how we
            // are rendering
            int documentMode = getIEDocumentMode();
            if (documentMode != -1) {
                browserDetails.setIEMode(documentMode);
            }
        }

        if (browserDetails.isChrome()) {
            touchDevice = detectChromeTouchDevice();
        } else if (browserDetails.isIE()) {
            touchDevice = detectIETouchDevice();
        } else {
            // PhantomJS pretends to be a touch device which breaks some UI
            // tests
            touchDevice = !browserDetails.isPhantomJS() && detectTouchDevice();
        }
    }

    private native boolean detectTouchDevice()
    /*-{
        try { document.createEvent("TouchEvent");return true;} catch(e){return false;};
    }-*/;

    private native boolean detectChromeTouchDevice()
    /*-{
        return ("ontouchstart" in window);
    }-*/;

    private native boolean detectIETouchDevice()
    /*-{
        return !!navigator.msMaxTouchPoints;
    }-*/;

    private native int getIEDocumentMode()
    /*-{
    	var mode = $wnd.document.documentMode;
    	if (!mode)
    		 return -1;
    	return mode;
    }-*/;

    /**
     * Returns a string representing the browser in use, for use in CSS
     * classnames. The classnames will be space separated abbreviations,
     * optionally with a version appended.
     * 
     * Abbreviations: Firefox: ff Internet Explorer: ie Safari: sa Opera: op
     * 
     * Browsers that CSS-wise behave like each other will get the same
     * abbreviation (this usually depends on the rendering engine).
     * 
     * This is quite simple at the moment, more heuristics will be added when
     * needed.
     * 
     * Examples: Internet Explorer 6: ".v-ie .v-ie6 .v-ie60", Firefox 3.0.4:
     * ".v-ff .v-ff3 .v-ff30", Opera 9.60: ".v-op .v-op9 .v-op960", Opera 10.10:
     * ".v-op .v-op10 .v-op1010"
     * 
     * @return
     */
    public String getCSSClass() {
        String prefix = "v-";

        if (cssClass == null) {
            String browserIdentifier = "";
            String majorVersionClass = "";
            String minorVersionClass = "";
            String browserEngineClass = "";

            if (browserDetails.isFirefox()) {
                browserIdentifier = BROWSER_FIREFOX;
                majorVersionClass = browserIdentifier
                        + getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = ENGINE_GECKO;
            } else if (browserDetails.isChrome()) {
                // TODO update when Chrome is more stable
                browserIdentifier = BROWSER_SAFARI;
                majorVersionClass = "ch";
                browserEngineClass = ENGINE_WEBKIT;
            } else if (browserDetails.isSafari()) {
                browserIdentifier = BROWSER_SAFARI;
                majorVersionClass = browserIdentifier
                        + getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = ENGINE_WEBKIT;
            } else if (browserDetails.isIE()) {
                browserIdentifier = BROWSER_IE;
                majorVersionClass = browserIdentifier
                        + getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = ENGINE_TRIDENT;
            } else if (browserDetails.isEdge()) {
                browserIdentifier = BROWSER_EDGE;
                majorVersionClass = browserIdentifier
                        + getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = "";
            } else if (browserDetails.isOpera()) {
                browserIdentifier = BROWSER_OPERA;
                majorVersionClass = browserIdentifier
                        + getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = ENGINE_PRESTO;
            }

            cssClass = prefix + browserIdentifier;
            if (!"".equals(majorVersionClass)) {
                cssClass = cssClass + " " + prefix + majorVersionClass;
            }
            if (!"".equals(minorVersionClass)) {
                cssClass = cssClass + " " + prefix + minorVersionClass;
            }
            if (!"".equals(browserEngineClass)) {
                cssClass = cssClass + " " + prefix + browserEngineClass;
            }
            String osClass = getOperatingSystemClass();
            if (osClass != null) {
                cssClass = cssClass + " " + osClass;
            }
            if (isTouchDevice()) {
                cssClass = cssClass + " " + prefix + UI_TOUCH;
            }
        }

        return cssClass;
    }

    private String getOperatingSystemClass() {
        String prefix = "v-";

        if (browserDetails.isAndroid()) {
            return prefix + OS_ANDROID;
        } else if (browserDetails.isIOS()) {
            String iosClass = prefix + OS_IOS;
            return iosClass + " " + iosClass + getOperatingSystemMajorVersion();
        } else if (browserDetails.isWindows()) {
            return prefix + OS_WINDOWS;
        } else if (browserDetails.isLinux()) {
            return prefix + OS_LINUX;
        } else if (browserDetails.isMacOSX()) {
            return prefix + OS_MACOSX;
        }
        // Unknown OS
        return null;
    }

    public boolean isIE() {
        return browserDetails.isIE();
    }

    public boolean isEdge() {
        return browserDetails.isEdge();
    }

    public boolean isFirefox() {
        return browserDetails.isFirefox();
    }

    public boolean isSafari() {
        return browserDetails.isSafari();
    }

    public boolean isIE8() {
        return isIE() && getBrowserMajorVersion() == 8;
    }

    public boolean isIE9() {
        return isIE() && getBrowserMajorVersion() == 9;
    }

    public boolean isIE10() {
        return isIE() && getBrowserMajorVersion() == 10;
    }

    public boolean isIE11() {
        return isIE() && getBrowserMajorVersion() == 11;
    }

    public boolean isChrome() {
        return browserDetails.isChrome();
    }

    public boolean isGecko() {
        return browserDetails.isGecko();
    }

    public boolean isWebkit() {
        return browserDetails.isWebKit();
    }

    /**
     * Returns the Gecko version if the browser is Gecko based. The Gecko
     * version for Firefox 2 is 1.8 and 1.9 for Firefox 3.
     * 
     * @return The Gecko version or -1 if the browser is not Gecko based
     */
    public float getGeckoVersion() {
        if (!browserDetails.isGecko()) {
            return -1;
        }

        return browserDetails.getBrowserEngineVersion();
    }

    /**
     * Returns the WebKit version if the browser is WebKit based. The WebKit
     * version returned is the major version e.g., 523.
     * 
     * @return The WebKit version or -1 if the browser is not WebKit based
     */
    public float getWebkitVersion() {
        if (!browserDetails.isWebKit()) {
            return -1;
        }

        return browserDetails.getBrowserEngineVersion();
    }

    public float getIEVersion() {
        if (!browserDetails.isIE()) {
            return -1;
        }

        return getBrowserMajorVersion();
    }

    public float getOperaVersion() {
        if (!browserDetails.isOpera()) {
            return -1;
        }

        return getBrowserMajorVersion();
    }

    public boolean isOpera() {
        return browserDetails.isOpera();
    }

    public boolean isOpera10() {
        return browserDetails.isOpera() && getBrowserMajorVersion() == 10;
    }

    public boolean isOpera11() {
        return browserDetails.isOpera() && getBrowserMajorVersion() == 11;
    }

    public native static String getBrowserString()
    /*-{
    	return $wnd.navigator.userAgent;
    }-*/;

    public native int getScreenWidth()
    /*-{
    	return $wnd.screen.width;
    }-*/;

    public native int getScreenHeight()
    /*-{
    	return $wnd.screen.height;
    }-*/;

    /**
     * @return true if the browser runs on a touch based device.
     */
    public boolean isTouchDevice() {
        return touchDevice;
    }

    /**
     * Indicates whether the browser might require juggling to properly update
     * sizes inside elements with overflow: auto.
     * 
     * @return <code>true</code> if the browser requires the workaround,
     *         otherwise <code>false</code>
     */
    public boolean requiresOverflowAutoFix() {
        return (getWebkitVersion() > 0 || getOperaVersion() >= 11
                || getIEVersion() >= 10 || isFirefox())
                && WidgetUtil.getNativeScrollbarSize() > 0;
    }

    /**
     * Indicates whether the browser might require juggling to properly update
     * sizes inside elements with overflow: auto when adjusting absolutely
     * positioned elements.
     * <p>
     * See https://bugs.webkit.org/show_bug.cgi?id=123958 and
     * http://code.google.com/p/chromium/issues/detail?id=316549
     * 
     * @since 7.1.8
     * @return <code>true</code> if the browser requires the workaround,
     *         otherwise <code>false</code>
     */
    public boolean requiresPositionAbsoluteOverflowAutoFix() {
        return (getWebkitVersion() > 0)
                && WidgetUtil.getNativeScrollbarSize() > 0;
    }

    /**
     * Checks if the browser is run on iOS
     * 
     * @return true if the browser is run on iOS, false otherwise
     */
    public boolean isIOS() {
        return browserDetails.isIOS();
    }

    /**
     * Checks if the browser is run on iOS 6.
     * 
     * @since 7.1.1
     * @return true if the browser is run on iOS 6, false otherwise
     */
    public boolean isIOS6() {
        return isIOS() && getOperatingSystemMajorVersion() == 6;
    }

    /**
     * Checks if the browser is run on Android
     * 
     * @return true if the browser is run on Android, false otherwise
     */
    public boolean isAndroid() {
        return browserDetails.isAndroid();
    }

    /**
     * Checks if the browser is capable of handling scrolling natively or if a
     * touch scroll helper is needed for scrolling.
     * 
     * @return true if browser needs a touch scroll helper, false if the browser
     *         can handle scrolling natively
     */
    public boolean requiresTouchScrollDelegate() {
        if (!isTouchDevice()) {
            return false;
        }
        // TODO Should test other Android browsers, especially Chrome
        if (isAndroid() && isWebkit() && getWebkitVersion() >= 534) {
            return false;
        }
        // iOS 6+ Safari supports native scrolling; iOS 5 suffers from #8792
        // TODO Should test other iOS browsers
        if (isIOS() && isWebkit() && getOperatingSystemMajorVersion() >= 6) {
            return false;
        }

        if (isIE()) {
            return false;
        }

        return true;
    }

    /**
     * Tests if this is an Android devices with a broken scrollTop
     * implementation
     * 
     * @return true if scrollTop cannot be trusted on this device, false
     *         otherwise
     */
    public boolean isAndroidWithBrokenScrollTop() {
        return isAndroid()
                && (getOperatingSystemMajorVersion() == 3 || getOperatingSystemMajorVersion() == 4);
    }

    public boolean isAndroid23() {
        return isAndroid() && getOperatingSystemMajorVersion() == 2
                && getOperatingSystemMinorVersion() == 3;
    }

    private int getOperatingSystemMajorVersion() {
        return browserDetails.getOperatingSystemMajorVersion();
    }

    private int getOperatingSystemMinorVersion() {
        return browserDetails.getOperatingSystemMinorVersion();
    }

    /**
     * Returns the browser major version e.g., 3 for Firefox 3.5, 4 for Chrome
     * 4, 8 for Internet Explorer 8.
     * <p>
     * Note that Internet Explorer 8 and newer will return the document mode so
     * IE8 rendering as IE7 will return 7.
     * </p>
     * 
     * @return The major version of the browser.
     */
    public int getBrowserMajorVersion() {
        return browserDetails.getBrowserMajorVersion();
    }

    /**
     * Returns the browser minor version e.g., 5 for Firefox 3.5.
     * 
     * @see #getBrowserMajorVersion()
     * 
     * @return The minor version of the browser, or -1 if not known/parsed.
     */
    public int getBrowserMinorVersion() {
        return browserDetails.getBrowserMinorVersion();
    }

    /**
     * Checks if the browser version is newer or equal to the given major+minor
     * version.
     * 
     * @param majorVersion
     *            The major version to check for
     * @param minorVersion
     *            The minor version to check for
     * @return true if the browser version is newer or equal to the given
     *         version
     */
    public boolean isBrowserVersionNewerOrEqual(int majorVersion,
            int minorVersion) {
        if (getBrowserMajorVersion() == majorVersion) {
            // Same major
            return (getBrowserMinorVersion() >= minorVersion);
        }

        // Older or newer major
        return (getBrowserMajorVersion() > majorVersion);
    }
}
