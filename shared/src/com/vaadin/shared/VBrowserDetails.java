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
package com.vaadin.shared;

import java.io.Serializable;

/**
 * Class that parses the user agent string from the browser and provides
 * information about the browser. Used internally by
 * {@link com.vaadin.client.BrowserInfo} and
 * {@link com.vaadin.server.WebBrowser}. Should not be used directly.
 * 
 * @author Vaadin Ltd.
 * @since 6.3
 */
public class VBrowserDetails implements Serializable {

    private boolean isGecko = false;
    private boolean isWebKit = false;
    private boolean isPresto = false;
    private boolean isTrident = false;

    private boolean isChromeFrameCapable = false;
    private boolean isChromeFrame = false;

    private boolean isSafari = false;
    private boolean isChrome = false;
    private boolean isFirefox = false;
    private boolean isOpera = false;
    private boolean isIE = false;

    private boolean isWindowsPhone;
    private boolean isIPad;
    private boolean isIPhone;

    private OperatingSystem os = OperatingSystem.UNKNOWN;

    public enum OperatingSystem {
        UNKNOWN, WINDOWS, MACOSX, LINUX, IOS, ANDROID;
    }

    private float browserEngineVersion = -1;
    private int browserMajorVersion = -1;
    private int browserMinorVersion = -1;

    private int osMajorVersion = -1;
    private int osMinorVersion = -1;

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
                && userAgent.indexOf("webkit") == -1
                && userAgent.indexOf("trident/") == -1;
        isPresto = userAgent.indexOf(" presto/") != -1;
        isTrident = userAgent.indexOf("trident/") != -1;
        isWebKit = !isTrident && userAgent.indexOf("applewebkit") != -1;

        // browser name
        isChrome = userAgent.indexOf(" chrome/") != -1;
        isOpera = userAgent.indexOf("opera") != -1;
        isIE = userAgent.indexOf("msie") != -1 && !isOpera
                && (userAgent.indexOf("webtv") == -1);
        // IE 11 no longer contains MSIE in the user agent
        isIE = isIE || isTrident;

        isSafari = !isChrome && !isIE && userAgent.indexOf("safari") != -1;
        isFirefox = userAgent.indexOf(" firefox/") != -1;

        // chromeframe
        isChromeFrameCapable = userAgent.indexOf("chromeframe") != -1;
        isChromeFrame = isChromeFrameCapable && !isChrome;

        // Rendering engine version
        try {
            if (isGecko) {
                int rvPos = userAgent.indexOf("rv:");
                if (rvPos >= 0) {
                    String tmp = userAgent.substring(rvPos + 3);
                    tmp = tmp.replaceFirst("(\\.[0-9]+).+", "$1");
                    browserEngineVersion = Float.parseFloat(tmp);
                }
            } else if (isWebKit) {
                String tmp = userAgent
                        .substring(userAgent.indexOf("webkit/") + 7);
                tmp = tmp.replaceFirst("([0-9]+)[^0-9].+", "$1");
                browserEngineVersion = Float.parseFloat(tmp);
            } else if (isIE) {
                int tridentPos = userAgent.indexOf("trident/");
                if (tridentPos >= 0) {
                    String tmp = userAgent.substring(tridentPos
                            + "Trident/".length());
                    tmp = tmp.replaceFirst("([0-9]+\\.[0-9]+).*", "$1");
                    browserEngineVersion = Float.parseFloat(tmp);
                }
            }
        } catch (Exception e) {
            // Browser engine version parsing failed
            System.err.println("Browser engine version parsing failed for: "
                    + userAgent);
        }

        // Browser version
        try {
            if (isIE) {
                if (userAgent.indexOf("msie") == -1) {
                    // IE 11+
                    int rvPos = userAgent.indexOf("rv:");
                    if (rvPos >= 0) {
                        String tmp = userAgent.substring(rvPos + 3);
                        tmp = tmp.replaceFirst("(\\.[0-9]+).+", "$1");
                        parseVersionString(tmp);
                    }
                } else {
                    String ieVersionString = userAgent.substring(userAgent
                            .indexOf("msie ") + 5);
                    ieVersionString = safeSubstring(ieVersionString, 0,
                            ieVersionString.indexOf(";"));
                    parseVersionString(ieVersionString);
                }
            } else if (isFirefox) {
                int i = userAgent.indexOf(" firefox/") + 9;
                parseVersionString(safeSubstring(userAgent, i, i + 5));
            } else if (isChrome) {
                int i = userAgent.indexOf(" chrome/") + 8;
                parseVersionString(safeSubstring(userAgent, i, i + 5));
            } else if (isSafari) {
                int i = userAgent.indexOf(" version/") + 9;
                parseVersionString(safeSubstring(userAgent, i, i + 5));
            } else if (isOpera) {
                int i = userAgent.indexOf(" version/");
                if (i != -1) {
                    // Version present in Opera 10 and newer
                    i += 9; // " version/".length
                } else {
                    i = userAgent.indexOf("opera/") + 6;
                }
                parseVersionString(safeSubstring(userAgent, i, i + 5));
            }
        } catch (Exception e) {
            // Browser version parsing failed
            System.err.println("Browser version parsing failed for: "
                    + userAgent);
        }

        // Operating system
        if (userAgent.contains("windows ")) {
            os = OperatingSystem.WINDOWS;
            isWindowsPhone = userAgent.contains("windows phone");
        } else if (userAgent.contains("android")) {
            os = OperatingSystem.ANDROID;
            parseAndroidVersion(userAgent);
        } else if (userAgent.contains("linux")) {
            os = OperatingSystem.LINUX;
        } else if (userAgent.contains("macintosh")
                || userAgent.contains("mac osx")
                || userAgent.contains("mac os x")) {
            isIPad = userAgent.contains("ipad");
            isIPhone = userAgent.contains("iphone");
            if (isIPad || userAgent.contains("ipod") || isIPhone) {
                os = OperatingSystem.IOS;
                parseIOSVersion(userAgent);
            } else {
                os = OperatingSystem.MACOSX;
            }
        }
    }

    private void parseAndroidVersion(String userAgent) {
        // Android 5.1;
        if (!userAgent.contains("android")) {
            return;
        }

        String osVersionString = safeSubstring(userAgent,
                userAgent.indexOf("android ") + "android ".length(),
                userAgent.length());
        osVersionString = safeSubstring(osVersionString, 0,
                osVersionString.indexOf(";"));
        String[] parts = osVersionString.split("\\.");
        parseOsVersion(parts);
    }

    private void parseIOSVersion(String userAgent) {
        // OS 5_1 like Mac OS X
        if (!userAgent.contains("os ") || !userAgent.contains(" like mac")) {
            return;
        }

        String osVersionString = safeSubstring(userAgent,
                userAgent.indexOf("os ") + 3, userAgent.indexOf(" like mac"));
        String[] parts = osVersionString.split("_");
        parseOsVersion(parts);
    }

    private void parseOsVersion(String[] parts) {
        osMajorVersion = -1;
        osMinorVersion = -1;

        if (parts.length >= 1) {
            try {
                osMajorVersion = Integer.parseInt(parts[0]);
            } catch (Exception e) {
            }
        }
        if (parts.length >= 2) {
            try {
                osMinorVersion = Integer.parseInt(parts[1]);
            } catch (Exception e) {
            }
            // Some Androids report version numbers as "2.1-update1"
            if (osMinorVersion == -1 && parts[1].contains("-")) {
                try {
                    osMinorVersion = Integer.parseInt(parts[1].substring(0,
                            parts[1].indexOf('-')));
                } catch (Exception ee) {
                }
            }
        }

    }

    private void parseVersionString(String versionString) {
        int idx = versionString.indexOf('.');
        if (idx < 0) {
            idx = versionString.length();
        }
        browserMajorVersion = Integer.parseInt(safeSubstring(versionString, 0,
                idx));

        int idx2 = versionString.indexOf('.', idx + 1);
        if (idx2 < 0) {
            idx2 = versionString.length();
        }
        try {
            browserMinorVersion = Integer.parseInt(safeSubstring(versionString,
                    idx + 1, idx2).replaceAll("[^0-9].*", ""));
        } catch (NumberFormatException e) {
            // leave the minor version unmodified (-1 = unknown)
        }
    }

    private String safeSubstring(String string, int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex < 0 || endIndex > string.length()) {
            endIndex = string.length();
        }
        return string.substring(beginIndex, endIndex);
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
     * Tests if the browser is using the Trident engine
     * 
     * @since 7.1.7
     * @return true if it is Trident, false otherwise
     */
    public boolean isTrident() {
        return isTrident;
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
     * Tests if the browser is capable of running ChromeFrame.
     * 
     * @return true if it has ChromeFrame, false otherwise
     */
    public boolean isChromeFrameCapable() {
        return isChromeFrameCapable;
    }

    /**
     * Tests if the browser is running ChromeFrame.
     * 
     * @return true if it is ChromeFrame, false otherwise
     */
    public boolean isChromeFrame() {
        return isChromeFrame;
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
     * <p>
     * Note that Internet Explorer 8 and newer will return the document mode so
     * IE8 rendering as IE7 will return 7.
     * </p>
     * 
     * @return The major version of the browser.
     */
    public final int getBrowserMajorVersion() {
        return browserMajorVersion;
    }

    /**
     * Returns the browser minor version e.g., 5 for Firefox 3.5.
     * 
     * @see #getBrowserMajorVersion()
     * 
     * @return The minor version of the browser, or -1 if not known/parsed.
     */
    public final int getBrowserMinorVersion() {
        return browserMinorVersion;
    }

    /**
     * Sets the version for IE based on the documentMode. This is used to return
     * the correct the correct IE version when the version from the user agent
     * string and the value of the documentMode property do not match.
     * 
     * @param documentMode
     *            The current document mode
     */
    public void setIEMode(int documentMode) {
        browserMajorVersion = documentMode;
        browserMinorVersion = 0;
    }

    /**
     * Tests if the browser is run on Windows.
     * 
     * @return true if run on Windows, false otherwise
     */
    public boolean isWindows() {
        return os == OperatingSystem.WINDOWS;
    }

    /**
     * Tests if the browser is run on Windows Phone.
     * 
     * @return true if run on Windows Phone, false otherwise
     * @since 7.3.2
     */
    public boolean isWindowsPhone() {
        return isWindowsPhone;
    }

    /**
     * Tests if the browser is run on Mac OSX.
     * 
     * @return true if run on Mac OSX, false otherwise
     */
    public boolean isMacOSX() {
        return os == OperatingSystem.MACOSX;
    }

    /**
     * Tests if the browser is run on Linux.
     * 
     * @return true if run on Linux, false otherwise
     */
    public boolean isLinux() {
        return os == OperatingSystem.LINUX;
    }

    /**
     * Tests if the browser is run on Android.
     * 
     * @return true if run on Android, false otherwise
     */
    public boolean isAndroid() {
        return os == OperatingSystem.ANDROID;
    }

    /**
     * Tests if the browser is run in iOS.
     * 
     * @return true if run in iOS, false otherwise
     */
    public boolean isIOS() {
        return os == OperatingSystem.IOS;
    }

    /**
     * Tests if the browser is run on iPhone.
     * 
     * @return true if run on iPhone, false otherwise
     * @since 7.3.3
     */
    public boolean isIPhone() {
        return isIPhone;
    }

    /**
     * Tests if the browser is run on iPad.
     * 
     * @return true if run on iPad, false otherwise
     * @since 7.3.3
     */
    public boolean isIPad() {
        return isIPad;
    }

    /**
     * Returns the major version of the operating system. Currently only
     * supported for mobile devices (iOS/Android)
     * 
     * @return The major version or -1 if unknown
     */
    public int getOperatingSystemMajorVersion() {
        return osMajorVersion;
    }

    /**
     * Returns the minor version of the operating system. Currently only
     * supported for mobile devices (iOS/Android)
     * 
     * @return The minor version or -1 if unknown
     */
    public int getOperatingSystemMinorVersion() {
        return osMinorVersion;
    }

    /**
     * Checks if the browser is so old that it simply won't work with a Vaadin
     * application. NOTE that the browser might still be capable of running
     * Crome Frame, so you might still want to check
     * {@link #isChromeFrameCapable()} if this returns true.
     * 
     * @return true if the browser won't work, false if not the browser is
     *         supported or might work
     */
    public boolean isTooOldToFunctionProperly() {
        // Check Trident version to detect compatibility mode
        if (isIE() && getBrowserMajorVersion() < 8
                && getBrowserEngineVersion() < 4) {
            return true;
        }
        // Webkit 533 in Safari 4.1+, Android 2.2+, iOS 4+
        if (isSafari() && getBrowserEngineVersion() < 533) {
            return true;
        }
        if (isFirefox() && getBrowserMajorVersion() < 4) {
            return true;
        }
        if (isOpera() && getBrowserMajorVersion() < 11) {
            return true;
        }

        return false;
    }

}
