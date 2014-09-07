package com.vaadin.client;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.shared.VBrowserDetails;

public class TestVBrowserDetailsUserAgentParser extends TestCase {

    private static final String FIREFOX30_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.0.6) Gecko/2009011913 Firefox/3.0.6";
    private static final String FIREFOX30_LINUX = "Mozilla/5.0 (X11; U; Linux x86_64; es-ES; rv:1.9.0.12) Gecko/2009070811 Ubuntu/9.04 (jaunty) Firefox/3.0.12";
    private static final String FIREFOX35_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.8) Gecko/20100202 Firefox/3.5.8 (.NET CLR 3.5.30729) FirePHP/0.4";
    private static final String FIREFOX36_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6 (.NET CLR 3.5.30729)";
    private static final String FIREFOX36B_MAC = "UAString mozilla/5.0 (macintosh; u; intel mac os x 10.6; en-us; rv:1.9.2) gecko/20100115 firefox/3.6";
    private static final String FIREFOX_30B5_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9b5) Gecko/2008032619 Firefox/3.0b5";
    private static final String FIREFOX_40B7_WIN = "Mozilla/5.0 (Windows NT 5.1; rv:2.0b7) Gecko/20100101 Firefox/4.0b7";
    private static final String FIREFOX_40B11_WIN = "Mozilla/5.0 (Windows NT 5.1; rv:2.0b11) Gecko/20100101 Firefox/4.0b11";
    private static final String KONQUEROR_LINUX = "Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.5 (like Gecko) (Exabot-Thumbnails)";

    private static final String IE6_WINDOWS = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)";
    private static final String IE7_WINDOWS = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";

    private static final String IE8_WINDOWS = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)";
    private static final String IE8_IN_IE7_MODE_WINDOWS = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)";

    private static final String IE9_IN_IE7_MODE_WINDOWS_7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)";
    private static final String IE9_BETA_IN_IE8_MODE_WINDOWS_7 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)";
    private static final String IE9_BETA_WINDOWS_7 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";

    private static final String IE10_WINDOWS_8 = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)";
    private static final String IE11_WINDOWS_7 = "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; rv:11.0) like Gecko";
    private static final String IE11_WINDOWS_PHONE_8_1_UPDATE = "Mozilla/5.0 (Mobile; Windows Phone 8.1; Android 4.0; ARM; Trident/7.0; Touch; rv:11.0; IEMobile/11.0; NOKIA; Lumia 920) Like iPhone OS 7_0_3 Mac OS X AppleWebKit/537 (KHTML, like Gecko) Mobile Safari/537";

    // "Version/" was added in 10.00
    private static final String OPERA964_WINDOWS = "Opera/9.64(Windows NT 5.1; U; en) Presto/2.1.1";
    private static final String OPERA1010_WINDOWS = "Opera/9.80 (Windows NT 5.1; U; en) Presto/2.2.15 Version/10.10";
    private static final String OPERA1050_WINDOWS = "Opera/9.80 (Windows NT 5.1; U; en) Presto/2.5.22 Version/10.50";

    private static final String CHROME3_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-US) AppleWebKit/532.0 (KHTML, like Gecko) Chrome/3.0.198 Safari/532.0";
    private static final String CHROME4_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.5 (KHTML, like Gecko) Chrome/4.0.249.89 Safari/532.5";

    private static final String SAFARI3_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; cs-CZ) AppleWebKit/525.28.3 (KHTML, like Gecko) Version/3.2.3 Safari/525.29";
    private static final String SAFARI4_MAC = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; en-us) AppleWebKit/531.22.7 (KHTML, like Gecko) Version/4.0.5 Safari/531.22.7";

    private static final String IPHONE_IOS_5_1 = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B179 Safari/7534.48.3";
    private static final String IPHONE_IOS_4_0 = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
    private static final String IPAD_IOS_4_3_1 = "Mozilla/5.0 (iPad; U; CPU OS 4_3_1 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8G4 Safari/6533.18.5";

    // application on the home screen, without Safari in user agent
    private static final String IPHONE_IOS_6_1_HOMESCREEN_SIMULATOR = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_1 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Mobile/10B141";

    private static final String ANDROID_HTC_2_1 = "Mozilla/5.0 (Linux; U; Android 2.1-update1; en-us; ADR6300 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";
    private static final String ANDROID_GOOGLE_NEXUS_2_2 = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    private static final String ANDROID_MOTOROLA_3_0 = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";
    private static final String ANDROID_GALAXY_NEXUS_4_0_4_CHROME = "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19";

    public void testSafari3() {
        VBrowserDetails bd = new VBrowserDetails(SAFARI3_WINDOWS);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 2);
        assertEngineVersion(bd, 525.0f);
        assertWindows(bd);
    }

    public void testSafari4() {
        VBrowserDetails bd = new VBrowserDetails(SAFARI4_MAC);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 531f);
        assertMacOSX(bd);
    }

    public void testIPhoneIOS6Homescreen() {
        VBrowserDetails bd = new VBrowserDetails(
                IPHONE_IOS_6_1_HOMESCREEN_SIMULATOR);
        assertWebKit(bd);
        // not identified as Safari, no browser version available
        // assertSafari(bd);
        // assertBrowserMajorVersion(bd, 6);
        // assertBrowserMinorVersion(bd, 1);
        assertEngineVersion(bd, 536f);
        assertIOS(bd, 6, 1);
        assertIPhone(bd);
    }

    public void testIPhoneIOS5() {
        VBrowserDetails bd = new VBrowserDetails(IPHONE_IOS_5_1);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 5);
        assertBrowserMinorVersion(bd, 1);
        assertEngineVersion(bd, 534f);
        assertIOS(bd, 5, 1);
        assertIPhone(bd);
    }

    public void testIPhoneIOS4() {
        VBrowserDetails bd = new VBrowserDetails(IPHONE_IOS_4_0);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 532f);
        assertIOS(bd, 4, 0);
        assertIPhone(bd);
    }

    public void testIPadIOS4() {
        VBrowserDetails bd = new VBrowserDetails(IPAD_IOS_4_3_1);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 5);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 533f);
        assertIOS(bd, 4, 3);
        assertIPad(bd);
    }

    public void testAndroid21() {
        VBrowserDetails bd = new VBrowserDetails(ANDROID_HTC_2_1);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 530f);
        assertAndroid(bd, 2, 1);

    }

    public void testAndroid22() {
        VBrowserDetails bd = new VBrowserDetails(ANDROID_GOOGLE_NEXUS_2_2);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 533f);
        assertAndroid(bd, 2, 2);
    }

    public void testAndroid30() {
        VBrowserDetails bd = new VBrowserDetails(ANDROID_MOTOROLA_3_0);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 534f);
        assertAndroid(bd, 3, 0);
    }

    public void testAndroid40Chrome() {
        VBrowserDetails bd = new VBrowserDetails(
                ANDROID_GALAXY_NEXUS_4_0_4_CHROME);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 18);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 535f);
        assertAndroid(bd, 4, 0);
    }

    private void assertOSMajorVersion(VBrowserDetails bd, int i) {
        assertEquals(i, bd.getOperatingSystemMajorVersion());
    }

    private void assertOSMinorVersion(VBrowserDetails bd, int i) {
        assertEquals(i, bd.getOperatingSystemMinorVersion());
    }

    public void testChrome3() {
        VBrowserDetails bd = new VBrowserDetails(CHROME3_MAC);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 532.0f);
        assertMacOSX(bd);
    }

    public void testChrome4() {
        VBrowserDetails bd = new VBrowserDetails(CHROME4_WINDOWS);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 532f);
        assertWindows(bd);
    }

    public void testFirefox3() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX30_WINDOWS);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 1.9f);
        assertWindows(bd);

        bd = new VBrowserDetails(FIREFOX30_LINUX);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 1.9f);
        assertLinux(bd);
    }

    public void testFirefox35() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX35_WINDOWS);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 5);
        assertEngineVersion(bd, 1.9f);
        assertWindows(bd);
    }

    public void testFirefox36() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX36_WINDOWS);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 6);
        assertEngineVersion(bd, 1.9f);
        assertWindows(bd);
    }

    public void testFirefox30b5() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX_30B5_MAC);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 1.9f);
        assertMacOSX(bd);
    }

    public void testFirefox40b11() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX_40B11_WIN);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 2.0f);
        assertWindows(bd);
    }

    public void testFirefox40b7() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX_40B7_WIN);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 2.0f);
        assertWindows(bd);
    }

    public void testKonquerorLinux() {
        // Just ensure detection does not crash
        VBrowserDetails bd = new VBrowserDetails(KONQUEROR_LINUX);
        assertLinux(bd);
    }

    public void testFirefox36b() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX36B_MAC);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 6);
        assertEngineVersion(bd, 1.9f);
        assertMacOSX(bd);
    }

    public void testOpera964() {
        VBrowserDetails bd = new VBrowserDetails(OPERA964_WINDOWS);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 9);
        assertBrowserMinorVersion(bd, 64);
        assertWindows(bd);
    }

    public void testOpera1010() {
        VBrowserDetails bd = new VBrowserDetails(OPERA1010_WINDOWS);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 10);
        assertBrowserMinorVersion(bd, 10);
        assertWindows(bd);
    }

    public void testOpera1050() {
        VBrowserDetails bd = new VBrowserDetails(OPERA1050_WINDOWS);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 10);
        assertBrowserMinorVersion(bd, 50);
        assertWindows(bd);
    }

    public void testIE6() {
        VBrowserDetails bd = new VBrowserDetails(IE6_WINDOWS);
        assertEngineVersion(bd, -1);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 6);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd);
    }

    public void testIE7() {
        VBrowserDetails bd = new VBrowserDetails(IE7_WINDOWS);
        assertEngineVersion(bd, -1);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 7);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd);
    }

    public void testIE8() {
        VBrowserDetails bd = new VBrowserDetails(IE8_WINDOWS);
        assertTrident(bd);
        assertEngineVersion(bd, 4);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 8);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd);
    }

    public void testIE8CompatibilityMode() {
        VBrowserDetails bd = new VBrowserDetails(IE8_IN_IE7_MODE_WINDOWS);
        bd.setIEMode(7);

        assertTrident(bd);
        assertEngineVersion(bd, 4);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 7);
        assertBrowserMinorVersion(bd, 0);

        assertWindows(bd);
    }

    public void testIE9() {
        VBrowserDetails bd = new VBrowserDetails(IE9_BETA_WINDOWS_7);
        assertTrident(bd);
        assertEngineVersion(bd, 5);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 9);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd);
    }

    public void testIE9InIE7CompatibilityMode() {
        VBrowserDetails bd = new VBrowserDetails(IE9_IN_IE7_MODE_WINDOWS_7);
        // bd.setIE8InCompatibilityMode();

        assertTrident(bd);
        assertEngineVersion(bd, 5);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 7);
        assertBrowserMinorVersion(bd, 0);

        assertWindows(bd);
    }

    public void testIE9InIE8CompatibilityMode() {
        VBrowserDetails bd = new VBrowserDetails(IE9_BETA_IN_IE8_MODE_WINDOWS_7);
        // bd.setIE8InCompatibilityMode();

        /*
         * Trident/4.0 in example user agent string based on beta even though it
         * should be Trident/5.0 in real (non-beta) user agent strings
         */
        assertTrident(bd);
        assertEngineVersion(bd, 4);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 8);
        assertBrowserMinorVersion(bd, 0);

        assertWindows(bd);
    }

    public void testIE10() {
        VBrowserDetails bd = new VBrowserDetails(IE10_WINDOWS_8);
        assertTrident(bd);
        assertEngineVersion(bd, 6);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 10);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd);
    }

    public void testIE11() {
        VBrowserDetails bd = new VBrowserDetails(IE11_WINDOWS_7);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd);
    }

    public void testIE11WindowsPhone81Update() {
        VBrowserDetails bd = new VBrowserDetails(IE11_WINDOWS_PHONE_8_1_UPDATE);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertWindows(bd, true);
    }

    /*
     * Helper methods below
     */

    private void assertEngineVersion(VBrowserDetails browserDetails,
            float version) {
        assertEquals(version, browserDetails.getBrowserEngineVersion());

    }

    private void assertBrowserMajorVersion(VBrowserDetails browserDetails,
            int version) {
        assertEquals(version, browserDetails.getBrowserMajorVersion());

    }

    private void assertBrowserMinorVersion(VBrowserDetails browserDetails,
            int version) {
        assertEquals(version, browserDetails.getBrowserMinorVersion());

    }

    private void assertGecko(VBrowserDetails browserDetails) {
        // Engine
        assertTrue(browserDetails.isGecko());
        assertFalse(browserDetails.isWebKit());
        assertFalse(browserDetails.isPresto());
        assertFalse(browserDetails.isTrident());
    }

    private void assertPresto(VBrowserDetails browserDetails) {
        // Engine
        assertFalse(browserDetails.isGecko());
        assertFalse(browserDetails.isWebKit());
        assertTrue(browserDetails.isPresto());
        assertFalse(browserDetails.isTrident());
    }

    private void assertTrident(VBrowserDetails browserDetails) {
        // Engine
        assertFalse(browserDetails.isGecko());
        assertFalse(browserDetails.isWebKit());
        assertFalse(browserDetails.isPresto());
        assertTrue(browserDetails.isTrident());
    }

    private void assertWebKit(VBrowserDetails browserDetails) {
        // Engine
        assertFalse(browserDetails.isGecko());
        assertTrue(browserDetails.isWebKit());
        assertFalse(browserDetails.isPresto());
        assertFalse(browserDetails.isTrident());
    }

    private void assertFirefox(VBrowserDetails browserDetails) {
        // Browser
        assertTrue(browserDetails.isFirefox());
        assertFalse(browserDetails.isChrome());
        assertFalse(browserDetails.isIE());
        assertFalse(browserDetails.isOpera());
        assertFalse(browserDetails.isSafari());
    }

    private void assertChrome(VBrowserDetails browserDetails) {
        // Browser
        assertFalse(browserDetails.isFirefox());
        assertTrue(browserDetails.isChrome());
        assertFalse(browserDetails.isIE());
        assertFalse(browserDetails.isOpera());
        assertFalse(browserDetails.isSafari());
    }

    private void assertIE(VBrowserDetails browserDetails) {
        // Browser
        assertFalse(browserDetails.isFirefox());
        assertFalse(browserDetails.isChrome());
        assertTrue(browserDetails.isIE());
        assertFalse(browserDetails.isOpera());
        assertFalse(browserDetails.isSafari());
    }

    private void assertOpera(VBrowserDetails browserDetails) {
        // Browser
        assertFalse(browserDetails.isFirefox());
        assertFalse(browserDetails.isChrome());
        assertFalse(browserDetails.isIE());
        assertTrue(browserDetails.isOpera());
        assertFalse(browserDetails.isSafari());
    }

    private void assertSafari(VBrowserDetails browserDetails) {
        // Browser
        assertFalse(browserDetails.isFirefox());
        assertFalse(browserDetails.isChrome());
        assertFalse(browserDetails.isIE());
        assertFalse(browserDetails.isOpera());
        assertTrue(browserDetails.isSafari());
    }

    private void assertMacOSX(VBrowserDetails browserDetails) {
        assertFalse(browserDetails.isLinux());
        assertFalse(browserDetails.isWindows());
        assertTrue(browserDetails.isMacOSX());
        assertFalse(browserDetails.isAndroid());
    }

    private void assertAndroid(VBrowserDetails browserDetails,
            int majorVersion, int minorVersion) {
        assertFalse(browserDetails.isLinux());
        assertFalse(browserDetails.isWindows());
        assertFalse(browserDetails.isMacOSX());
        assertFalse(browserDetails.isIOS());
        assertTrue(browserDetails.isAndroid());

        assertOSMajorVersion(browserDetails, majorVersion);
        assertOSMinorVersion(browserDetails, minorVersion);
    }

    private void assertIOS(VBrowserDetails browserDetails, int majorVersion,
            int minorVersion) {
        assertFalse(browserDetails.isLinux());
        assertFalse(browserDetails.isWindows());
        assertFalse(browserDetails.isMacOSX());
        assertTrue(browserDetails.isIOS());
        assertFalse(browserDetails.isAndroid());

        assertOSMajorVersion(browserDetails, majorVersion);
        assertOSMinorVersion(browserDetails, minorVersion);
    }

    private void assertIPhone(VBrowserDetails browserDetails) {
        assertTrue(browserDetails.isIPhone());
        assertFalse(browserDetails.isIPad());
    }

    private void assertIPad(VBrowserDetails browserDetails) {
        assertFalse(browserDetails.isIPhone());
        assertTrue(browserDetails.isIPad());
    }

    private void assertWindows(VBrowserDetails browserDetails) {
        assertWindows(browserDetails, false);
    }

    private void assertWindows(VBrowserDetails browserDetails,
            boolean isWindowsPhone) {
        assertFalse(browserDetails.isLinux());
        assertTrue(browserDetails.isWindows());
        assertFalse(browserDetails.isMacOSX());
        assertFalse(browserDetails.isIOS());
        assertFalse(browserDetails.isAndroid());
        Assert.assertEquals(isWindowsPhone, browserDetails.isWindowsPhone());
    }

    private void assertLinux(VBrowserDetails browserDetails) {
        assertTrue(browserDetails.isLinux());
        assertFalse(browserDetails.isWindows());
        assertFalse(browserDetails.isMacOSX());
        assertFalse(browserDetails.isIOS());
        assertFalse(browserDetails.isAndroid());
    }

}
