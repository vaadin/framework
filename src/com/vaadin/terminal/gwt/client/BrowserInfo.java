/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.RootPanel;

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
        } else {
            touchDevice = detectTouchDevice();
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
                        + browserDetails.getBrowserMajorVersion();
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
                        + browserDetails.getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = ENGINE_WEBKIT;
            } else if (browserDetails.isIE()) {
                browserIdentifier = BROWSER_IE;
                majorVersionClass = browserIdentifier
                        + browserDetails.getBrowserMajorVersion();
                minorVersionClass = majorVersionClass
                        + browserDetails.getBrowserMinorVersion();
                browserEngineClass = ENGINE_TRIDENT;
            } else if (browserDetails.isOpera()) {
                browserIdentifier = BROWSER_OPERA;
                majorVersionClass = browserIdentifier
                        + browserDetails.getBrowserMajorVersion();
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
                cssClass = cssClass + " " + prefix + osClass;
            }

        }

        return cssClass;
    }

    private String getOperatingSystemClass() {
        if (browserDetails.isAndroid()) {
            return OS_ANDROID;
        } else if (browserDetails.isIOS()) {
            return OS_IOS;
        } else if (browserDetails.isWindows()) {
            return OS_WINDOWS;
        } else if (browserDetails.isLinux()) {
            return OS_LINUX;
        } else if (browserDetails.isMacOSX()) {
            return OS_MACOSX;
        }
        // Unknown OS
        return null;
    }

    public boolean isIE() {
        return browserDetails.isIE();
    }

    public boolean isFirefox() {
        return browserDetails.isFirefox();
    }

    public boolean isSafari() {
        return browserDetails.isSafari();
    }

    public boolean isIE8() {
        return isIE() && browserDetails.getBrowserMajorVersion() == 8;
    }

    public boolean isIE9() {
        return isIE() && browserDetails.getBrowserMajorVersion() == 9;
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

        return browserDetails.getBrowserMajorVersion();
    }

    public float getOperaVersion() {
        if (!browserDetails.isOpera()) {
            return -1;
        }

        return browserDetails.getBrowserMajorVersion();
    }

    public boolean isOpera() {
        return browserDetails.isOpera();
    }

    public boolean isOpera10() {
        return browserDetails.isOpera()
                && browserDetails.getBrowserMajorVersion() == 10;
    }

    public boolean isOpera11() {
        return browserDetails.isOpera()
                && browserDetails.getBrowserMajorVersion() == 11;
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
        return (getWebkitVersion() > 0 || getOperaVersion() >= 11)
                && Util.getNativeScrollbarSize() > 0;
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

        if (isAndroid() && isWebkit() && getWebkitVersion() < 534) {
            return true;
        }
        // if (isIOS() && isWebkit() && getWebkitVersion() < ???) {
        // return true;
        // }

        return false;
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

    private int getOperatingSystemMajorVersion() {
        return browserDetails.getOperatingSystemMajorVersion();
    }
}
