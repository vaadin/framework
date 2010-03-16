package com.vaadin.tests.server;

import junit.framework.TestCase;

import com.vaadin.terminal.gwt.client.VBrowserDetails;

public class BrowserUserAgentParser extends TestCase {

    private static final String FIREFOX30 = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.0.6) Gecko/2009011913 Firefox/3.0.6";
    private static final String FIREFOX35 = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.8) Gecko/20100202 Firefox/3.5.8 (.NET CLR 3.5.30729) FirePHP/0.4";
    private static final String FIREFOX36 = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6 (.NET CLR 3.5.30729)";

    private static final String IE6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)";
    private static final String IE7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
    private static final String IE8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)";
    private static final String IE8_IN_IE7_MODE = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)";

    // "Version/" was added in 10.00
    private static final String OPERA960 = "Opera/9.64(Windows NT 5.1; U; en) Presto/2.1.1";
    private static final String OPERA1010 = "Opera/9.80 (Windows NT 5.1; U; en) Presto/2.2.15 Version/10.10";
    private static final String OPERA1050 = "Opera/9.80 (Windows NT 5.1; U; en) Presto/2.5.22 Version/10.50";

    private static final String CHROME3 = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-US) AppleWebKit/532.0 (KHTML, like Gecko) Chrome/3.0.198 Safari/532.0";
    private static final String CHROME4 = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.5 (KHTML, like Gecko) Chrome/4.0.249.89 Safari/532.5";

    private static final String SAFARI3 = "Mozilla/5.0 (Windows; U; Windows NT 5.1; cs-CZ) AppleWebKit/525.28.3 (KHTML, like Gecko) Version/3.2.3 Safari/525.29";
    private static final String SAFARI4 = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; en-us) AppleWebKit/531.22.7 (KHTML, like Gecko) Version/4.0.5 Safari/531.22.7";

    public void testSafari3() {
        VBrowserDetails bd = new VBrowserDetails(SAFARI3);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 3);
        assertEngineVersion(bd, 525.0f);
    }

    public void testSafari4() {
        VBrowserDetails bd = new VBrowserDetails(SAFARI4);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertEngineVersion(bd, 531f);
    }

    public void testChrome3() {
        VBrowserDetails bd = new VBrowserDetails(CHROME3);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 3);
        assertEngineVersion(bd, 532.0f);
    }

    public void testChrome4() {
        VBrowserDetails bd = new VBrowserDetails(CHROME4);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 4);
        assertEngineVersion(bd, 532f);
    }

    public void testFirefox3() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX30);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertEngineVersion(bd, 1.9f);
    }

    public void testFirefox35() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX35);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertEngineVersion(bd, 1.9f);
    }

    public void testFirefox36() {
        VBrowserDetails bd = new VBrowserDetails(FIREFOX36);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertEngineVersion(bd, 1.9f);
    }

    public void testOpera960() {
        VBrowserDetails bd = new VBrowserDetails(OPERA960);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 9);
    }

    public void testOpera1010() {
        VBrowserDetails bd = new VBrowserDetails(OPERA1010);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 10);
    }

    public void testOpera1050() {
        VBrowserDetails bd = new VBrowserDetails(OPERA1050);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 10);
    }

    public void testIE6() {
        VBrowserDetails bd = new VBrowserDetails(IE6);
        // assertTrident(bd);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 6);
    }

    public void testIE7() {
        VBrowserDetails bd = new VBrowserDetails(IE7);
        // assertTrident(bd);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 7);
    }

    public void testIE8() {
        VBrowserDetails bd = new VBrowserDetails(IE8);
        // assertTrident(bd);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 8);
    }

    public void testIE8CompatibilityMode() {
        VBrowserDetails bd = new VBrowserDetails(IE8_IN_IE7_MODE);
        bd.setIE8InCompatibilityMode();

        // assertTrident(bd);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 7);
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

    private void assertGecko(VBrowserDetails browserDetails) {
        // Engine
        assertTrue(browserDetails.isGecko());
        assertFalse(browserDetails.isWebKit());
        assertFalse(browserDetails.isPresto());
    }

    private void assertPresto(VBrowserDetails browserDetails) {
        // Engine
        assertFalse(browserDetails.isGecko());
        assertFalse(browserDetails.isWebKit());
        assertTrue(browserDetails.isPresto());
    }

    private void assertWebKit(VBrowserDetails browserDetails) {
        // Engine
        assertFalse(browserDetails.isGecko());
        assertTrue(browserDetails.isWebKit());
        assertFalse(browserDetails.isPresto());
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
}
