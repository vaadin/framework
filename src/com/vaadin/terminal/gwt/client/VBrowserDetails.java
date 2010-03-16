package com.vaadin.terminal.gwt.client;

/**
 * Class that parses the user agent string from the browser and provides
 * information about the browser.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 6.3
 */
public class VBrowserDetails {

    private boolean isGecko;
    private boolean isWebKit;
    private boolean isPresto;

    private boolean isSafari;
    private boolean isChrome;
    private boolean isFirefox;
    private boolean isOpera;
    private boolean isIE;

    private float browserEngineVersion = -1;
    private int browserMajorVersion = -1;

    /**
     * Create an instance based on the given user agent.
     * 
     * @param userAgent
     *            User agent as provided by the browser.
     */
    public VBrowserDetails(String userAgent) {
        userAgent = userAgent.toLowerCase();

        // browser engine name
        isGecko = userAgent.indexOf("gecko") != -1
                && userAgent.indexOf("webkit") == -1;
        isWebKit = userAgent.indexOf("applewebkit") != -1;
        isPresto = userAgent.indexOf(" presto/") != -1;

        // browser name
        isChrome = userAgent.indexOf(" chrome/") != -1;
        isSafari = !isChrome && userAgent.indexOf("safari") != -1;
        isOpera = userAgent.indexOf("opera") != -1;
        isIE = userAgent.indexOf("msie") != -1 && !isOpera
                && (userAgent.indexOf("webtv") == -1);
        isFirefox = userAgent.indexOf(" firefox/") != -1;

        // Rendering engine version
        if (isGecko) {
            String tmp = userAgent.substring(userAgent.indexOf("rv:") + 3);
            tmp = tmp.replaceFirst("(\\.[0-9]+).+", "$1");
            browserEngineVersion = Float.parseFloat(tmp);
        } else if (isWebKit) {
            String tmp = userAgent.substring(userAgent.indexOf("webkit/") + 7);
            tmp = tmp.replaceFirst("([0-9]+)[^0-9].+", "$1");
            browserEngineVersion = Float.parseFloat(tmp);
        }

        // Browser version
        if (isIE) {
            String ieVersionString = userAgent.substring(userAgent
                    .indexOf("msie ") + 5);
            ieVersionString = ieVersionString.substring(0, ieVersionString
                    .indexOf(";"));
            // FIXME parseInt
            browserMajorVersion = (int) Float.parseFloat(ieVersionString);
        } else if (isFirefox) {
            int i = userAgent.indexOf(" firefox/") + 9;
            browserMajorVersion = Integer.parseInt(userAgent
                    .substring(i, i + 1));
        } else if (isChrome) {
            int i = userAgent.indexOf(" chrome/") + 8;
            browserMajorVersion = Integer.parseInt(userAgent
                    .substring(i, i + 1));
        } else if (isSafari) {
            int i = userAgent.indexOf(" version/") + 9;
            browserMajorVersion = Integer.parseInt(userAgent
                    .substring(i, i + 1));
        } else if (isOpera) {
            int i = userAgent.indexOf(" version/");
            if (i != -1) {
                // Version present in Opera 10 and newer
                i += 9; // " version/".length
            } else {
                i = userAgent.indexOf("opera/") + 6;
            }

            browserMajorVersion = Integer.parseInt(userAgent
                    .substring(i, i + 2).replace(".", ""));
        }

    }

    /**
     * Tests if the browser is Firefox.
     * 
     * @return true if it is Firefox, false otherwise
     */
    public boolean isFirefox() {
        return isFirefox;
    }

    /**
     * Tests if the browser is using the Gecko engine
     * 
     * @return true if it is Gecko, false otherwise
     */
    public boolean isGecko() {
        return isGecko;
    }

    /**
     * Tests if the browser is using the WebKit engine
     * 
     * @return true if it is WebKit, false otherwise
     */
    public boolean isWebKit() {
        return isWebKit;
    }

    /**
     * Tests if the browser is using the Presto engine
     * 
     * @return true if it is Presto, false otherwise
     */
    public boolean isPresto() {
        return isPresto;
    }

    /**
     * Tests if the browser is Safari.
     * 
     * @return true if it is Safari, false otherwise
     */
    public boolean isSafari() {
        return isSafari;
    }

    /**
     * Tests if the browser is Chrome.
     * 
     * @return true if it is Chrome, false otherwise
     */
    public boolean isChrome() {
        return isChrome;
    }

    /**
     * Tests if the browser is Opera.
     * 
     * @return true if it is Opera, false otherwise
     */
    public boolean isOpera() {
        return isOpera;
    }

    /**
     * Tests if the browser is Internet Explorer.
     * 
     * @return true if it is Internet Explorer, false otherwise
     */
    public boolean isIE() {
        return isIE;
    }

    /**
     * Returns the version of the browser engine. For WebKit this is an integer
     * e.g., 532.0. For gecko it is a float e.g., 1.8 or 1.9.
     * 
     * @return The version of the browser engine
     */
    public float getBrowserEngineVersion() {
        return browserEngineVersion;
    }

    /**
     * Returns the browser major version e.g., 3 for Firefox 3.5, 4 for Chrome
     * 4, 8 for Internet Explorer 8.
     * 
     * <pre>
     * Note that Internet Explorer 8 in compatibility mode will return 7.
     * </pre>
     * 
     * @return The major version of the browser.
     */
    public final int getBrowserMajorVersion() {
        return browserMajorVersion;
    }

    /**
     * Marks that IE8 is used in compatibility mode. This forces the browser
     * version to 7 even if it otherwise was detected as 8.
     * 
     */
    public void setIE8InCompatibilityMode() {
        if (isIE && browserMajorVersion == 8) {
            browserMajorVersion = 7;
        }
    }

}
