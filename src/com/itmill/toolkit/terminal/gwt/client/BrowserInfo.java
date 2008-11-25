/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

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

    private boolean isGecko;
    private boolean isAppleWebKit;
    private boolean isSafari;
    private boolean isOpera;
    private boolean isIE;
    private float version = -1;

    private BrowserInfo() {
        try {
            String ua = getBrowserString().toLowerCase();
            // browser engine name
            isGecko = ua.indexOf("gecko") != -1 && ua.indexOf("webkit") == -1;
            isAppleWebKit = ua.indexOf("applewebkit") != -1;

            // browser name
            isSafari = ua.indexOf("safari") != -1;
            isOpera = ua.indexOf("opera") != -1;
            isIE = ua.indexOf("msie") != -1 && !isOpera
                    && (ua.indexOf("webtv") == -1);

            if (isGecko) {
                String tmp = ua.substring(ua.indexOf("rv:") + 3);
                tmp = tmp.replaceFirst("(\\.[0-9]+).+", "$1");
                version = Float.parseFloat(tmp);
            }
            if (isAppleWebKit) {
                String tmp = ua.substring(ua.indexOf("webkit/") + 7);
                tmp = tmp.replaceFirst("([0-9]+)[^0-9].+", "$1");
                version = Float.parseFloat(tmp);

            }

            if (isIE) {
                String ieVersionString = ua.substring(ua.indexOf("msie ") + 5);
                ieVersionString = ieVersionString.substring(0, ieVersionString
                        .indexOf(";"));
                version = Float.parseFloat(ieVersionString);
            }
        } catch (Exception e) {
            ClientExceptionHandler.displayError(e);
        }
    }

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
     * Examples: Internet Explorer 6: ".i-ie .i-ie6", Firefox 3.0.4:
     * ".i-ff .i-ff3", Opera 9.60: ".i-op .i-op96"
     * 
     * @param prefix
     *            a prefix to add to the classnames
     * @return
     */
    public String getCSSClass() {
        String prefix = "i-";
        if (cssClass == null) {
            String bs = getBrowserString().toLowerCase();
            String b = "";
            String v = "";
            if (bs.indexOf(" firefox/") != -1) {
                b = "ff";
                int i = bs.indexOf(" firefox/") + 9;
                v = b + bs.substring(i, i + 1);
            } else if (bs.indexOf(" chrome/") != -1) {
                // TODO update when Chrome is more stable
                b = "sa";
                v = "ch";
            } else if (bs.indexOf(" safari") != -1) {
                b = "sa";
                int i = bs.indexOf(" version/") + 9;
                v = b + bs.substring(i, i + 1);
            } else if (bs.indexOf(" msie ") != -1) {
                b = "ie";
                int i = bs.indexOf(" msie ") + 6;
                v = b + bs.substring(i, i + 1);
            } else if (bs.indexOf("opera/") != -1) {
                b = "op";
                int i = bs.indexOf("opera/") + 6;
                v = b + bs.substring(i, i + 3).replace(".", "");
            }
            cssClass = prefix + b + " " + prefix + v;
        }

        return cssClass;
    }

    public boolean isIE() {
        return isIE;
    }

    public boolean isSafari() {
        return isSafari;
    }

    public boolean isIE6() {
        return isIE && version == 6;
    }

    public boolean isIE7() {
        return isIE && version == 7;
    }

    public boolean isGecko() {
        return isGecko;
    }

    public boolean isFF2() {
        return isGecko && version == 1.8;
    }

    public boolean isFF3() {
        return isGecko && version == 1.9;
    }

    public float getGeckoVersion() {
        return (isGecko ? version : -1);
    }

    public float getWebkitVersion() {
        return (isAppleWebKit ? version : -1);
    }

    public float getIEVersion() {
        return (isIE ? version : -1);
    }

    public boolean isOpera() {
        return isOpera;
    }

    public native static String getBrowserString()
    /*-{
        return $wnd.navigator.userAgent;
    }-*/;

    public static void test() {
        Console c = ApplicationConnection.getConsole();

        c.log("getBrowserString() " + getBrowserString());
        c.log("isIE() " + get().isIE());
        c.log("isIE6() " + get().isIE6());
        c.log("isIE7() " + get().isIE7());
        c.log("isFF2() " + get().isFF2());
        c.log("isSafari() " + get().isSafari());
        c.log("getGeckoVersion() " + get().getGeckoVersion());
        c.log("getWebkitVersion() " + get().getWebkitVersion());
        c.log("getIEVersion() " + get().getIEVersion());
        c.log("isIE() " + get().isIE());
        c.log("isIE() " + get().isIE());
    }

}
