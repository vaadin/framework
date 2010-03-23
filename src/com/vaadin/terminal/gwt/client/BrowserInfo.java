/* 
@ITMillApache2LicenseForJavaFiles@
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

    private BrowserInfo() {
        try {
            browserDetails = new VBrowserDetails(getBrowserString());
            if (browserDetails.isIE()
                    && browserDetails.getBrowserMajorVersion() == 8
                    && isIE8InIE7CompatibilityMode()) {
                browserDetails.setIE8InCompatibilityMode();
            }

        } catch (Exception e) {
            ClientExceptionHandler.displayError(e);
        }
    }

    private native boolean isIE8InIE7CompatibilityMode()
    /*-{
        var mode = $wnd.document.documentMode;
        if (!mode)
            return false;
        return (mode == 7);
    }-*/;

    /**
     * Returns a string representing the browser in use, for use in CSS
     * classnames. The classnames will be space separated abbrevitaions,
     * optionally with a version appended.
     * 
     * Abbreviaions: Firefox: ff Internet Explorer: ie Safari: sa Opera: op
     * 
     * Browsers that CSS-wise behave like each other will get the same
     * abbreviation (this usually depends on the rendering engine).
     * 
     * This is quite simple at the moment, more heuristics will be added when
     * needed.
     * 
     * Examples: Internet Explorer 6: ".v-ie .v-ie6", Firefox 3.0.4:
     * ".v-ff .v-ff3", Opera 9.60: ".v-op .v-op96"
     * 
     * @return
     */
    public String getCSSClass() {
        String prefix = "v-";

        if (cssClass == null) {
            String b = "";
            String v = "";
            if (browserDetails.isFirefox()) {
                b = "ff";
                v = b + browserDetails.getBrowserMajorVersion();
            } else if (browserDetails.isChrome()) {
                // TODO update when Chrome is more stable
                b = "sa";
                v = "ch";
            } else if (browserDetails.isSafari()) {
                b = "sa";
                v = b + browserDetails.getBrowserMajorVersion();
            } else if (browserDetails.isIE()) {
                b = "ie";
                v = b + browserDetails.getBrowserMajorVersion();
            } else if (browserDetails.isOpera()) {
                b = "op";
                v = b + browserDetails.getBrowserMajorVersion();
            }
            cssClass = prefix + b + " " + prefix + v;
        }

        return cssClass;
    }

    public boolean isIE() {
        return browserDetails.isIE();
    }

    public boolean isSafari() {
        return browserDetails.isSafari();
    }

    public boolean isIE6() {
        return isIE() && browserDetails.getBrowserMajorVersion() == 6;
    }

    public boolean isIE7() {
        return isIE() && browserDetails.getBrowserMajorVersion() == 7;
    }

    public boolean isIE8() {
        return isIE() && browserDetails.getBrowserMajorVersion() == 8;
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

    public boolean isFF2() {
        // FIXME: Should use browserVersion
        return browserDetails.isFirefox()
                && browserDetails.getBrowserEngineVersion() == 1.8;
    }

    public boolean isFF3() {
        // FIXME: Should use browserVersion
        return browserDetails.isFirefox()
                && browserDetails.getBrowserEngineVersion() == 1.9;
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

    public boolean isOpera() {
        return browserDetails.isOpera();
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

}
