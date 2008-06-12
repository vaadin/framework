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
    private float ieVersion = -1;
    private float geckoVersion = -1;
    private float appleWebKitVersion = -1;

    private BrowserInfo() {
        try {
            String ua = getBrowserString().toLowerCase();
            // browser engine name
            isGecko = ua.indexOf("gecko") != -1 && ua.indexOf("safari") == -1;
            isAppleWebKit = ua.indexOf("applewebkit") != -1;

            // browser name
            isSafari = ua.indexOf("safari") != -1;
            isOpera = ua.indexOf("opera") != -1;
            isIE = ua.indexOf("msie") != -1 && !isOpera
                    && (ua.indexOf("webtv") == -1);

            if (isGecko) {
                String tmp = ua.substring(ua.indexOf("rv:") + 3);
                tmp = tmp.replaceFirst("(\\.[0-9]+).+", "$1");
                geckoVersion = Float.parseFloat(tmp);
            }
            if (isAppleWebKit) {
                String tmp = ua.substring(ua.indexOf("webkit/") + 7);
                tmp = tmp.replaceFirst("([0-9]+)[^0-9].+", "$1");
                appleWebKitVersion = Float.parseFloat(tmp);

            }

            if (isIE) {
                String ieVersionString = ua.substring(ua.indexOf("msie ") + 5);
                ieVersionString = ieVersionString.substring(0, ieVersionString
                        .indexOf(";"));
                ieVersion = Float.parseFloat(ieVersionString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationConnection.getConsole().error(e.getMessage());
        }
    }

    public boolean isIE() {
        return isIE;
    }

    public boolean isSafari() {
        return isSafari;
    }

    public boolean isIE6() {
        return isIE && ieVersion == 6;
    }

    public boolean isIE7() {
        return isIE && ieVersion == 7;
    }

    public boolean isGecko() {
        return isGecko;
    }

    public boolean isFF2() {
        return isGecko && geckoVersion == 1.8;
    }

    public float getGeckoVersion() {
        return geckoVersion;
    }

    public float getWebkitVersion() {
        return appleWebKitVersion;
    }

    public float getIEVersion() {
        return ieVersion;
    }

    public native static String getBrowserString()
    /*-{
        return $wnd.navigator.userAgent;
    }-*/;

    public static void test() {
        Console c = ApplicationConnection.getConsole();

        c.log("getBrowserString() " + get().getBrowserString());
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
