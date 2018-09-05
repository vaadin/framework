package com.vaadin.tools;

import static com.vaadin.tools.CvalAddonsChecker.VAADIN_AGPL;
import static com.vaadin.tools.CvalAddonsChecker.VAADIN_CVAL;
import static com.vaadin.tools.CvalChecker.GRACE_DAYS_MSECS;
import static com.vaadin.tools.CvalChecker.computeLicenseName;
import static com.vaadin.tools.CvalChecker.deleteCache;
import static com.vaadin.tools.CvalCheckerTest.VALID_KEY;
import static com.vaadin.tools.CvalCheckerTest.addLicensedJarToClasspath;
import static com.vaadin.tools.CvalCheckerTest.cacheExists;
import static com.vaadin.tools.CvalCheckerTest.captureSystemOut;
import static com.vaadin.tools.CvalCheckerTest.productNameAgpl;
import static com.vaadin.tools.CvalCheckerTest.productNameApache;
import static com.vaadin.tools.CvalCheckerTest.productNameCval;
import static com.vaadin.tools.CvalCheckerTest.readSystemOut;
import static com.vaadin.tools.CvalCheckerTest.saveCache;
import static com.vaadin.tools.CvalCheckerTest.unreachableLicenseProvider;
import static com.vaadin.tools.CvalCheckerTest.validLicenseProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.client.metadata.ConnectorBundleLoader.CValUiInfo;
import com.vaadin.tools.CvalChecker.InvalidCvalException;

/**
 * The CvalAddonsChecker test.
 */
public class CvalAddonsCheckerTest {

    CvalAddonsChecker addonChecker;
    private String licenseName;

    @Before
    public void setup() {
        addonChecker = new CvalAddonsChecker()
                .setLicenseProvider(validLicenseProvider).setFilter(".*test.*");
        licenseName = computeLicenseName(productNameCval);

        deleteCache(productNameCval);
        System.getProperties().remove(licenseName);

        // Set up a new URLClassLoader for the thread
        Thread thread = Thread.currentThread();
        thread.setContextClassLoader(new URLClassLoader(new URL[0], null));
    }

    @Test
    public void testRunChecker() throws Exception {
        // Create a product .jar with a cval license non required and add to our
        // classpath
        addLicensedJarToClasspath(productNameCval, VAADIN_CVAL);
        // Remove other products in case other tests added them previously
        addLicensedJarToClasspath(productNameAgpl, null);
        addLicensedJarToClasspath(productNameApache, null);

        // No license
        // -> Break compilation
        System.getProperties().remove(licenseName);
        addonChecker.setLicenseProvider(validLicenseProvider);
        try {
            addonChecker.run();
            fail();
        } catch (InvalidCvalException expected) {
        }
        assertFalse(cacheExists(productNameCval));

        // We have a license that has never been validated from the server and
        // we are offline
        // -> Show a message on compile time (“Your license for TouchKit 4 has
        // not been validated.")
        System.setProperty(licenseName, VALID_KEY);
        addonChecker.setLicenseProvider(unreachableLicenseProvider);
        captureSystemOut();
        addonChecker.run();
        assertTrue(readSystemOut().contains("has not been validated"));
        assertFalse(cacheExists(productNameCval));

        // Valid license has previously been validated from the server and we
        // are offline
        // -> Use the cached server response
        System.setProperty(licenseName, VALID_KEY);
        addonChecker.setLicenseProvider(validLicenseProvider);
        captureSystemOut();
        addonChecker.run();
        assertTrue(cacheExists(productNameCval));
        addonChecker.setLicenseProvider(unreachableLicenseProvider);
        addonChecker.run();

        // Expired license and we are offline
        // -> If it has expired less than 14 days ago, just work with no nag
        // messages
        System.setProperty(licenseName, VALID_KEY);
        addonChecker.setLicenseProvider(unreachableLicenseProvider);
        setCacheFileTs(System.currentTimeMillis() - (GRACE_DAYS_MSECS / 2),
                "normal");
        captureSystemOut();
        addonChecker.run();

        // Expired license and we are offline
        // -> After 14 days, interpret it as expired license
        setCacheFileTs(System.currentTimeMillis() - (GRACE_DAYS_MSECS * 2),
                "normal");
        try {
            addonChecker.run();
            fail();
        } catch (InvalidCvalException expected) {
        }

        // Invalid evaluation license
        // -> Fail compilation with a message
        // "Your evaluation license for TouchKit 4 is not valid"
        System.setProperty(licenseName, VALID_KEY);
        addonChecker.setLicenseProvider(unreachableLicenseProvider);
        setCacheFileTs(System.currentTimeMillis() - (GRACE_DAYS_MSECS / 2),
                "evaluation");
        try {
            addonChecker.run();
            fail();
        } catch (InvalidCvalException expected) {
            assertTrue(expected.getMessage().contains("expired"));
        }

        // Valid evaluation license
        // -> The choice on whether to show the message is generated in
        // widgetset
        // compilation phase. No license checks are done in application runtime.
        System.setProperty(licenseName, VALID_KEY);
        addonChecker.setLicenseProvider(unreachableLicenseProvider);
        setCacheFileTs(System.currentTimeMillis() + GRACE_DAYS_MSECS,
                "evaluation");
        List<CValUiInfo> uiInfo = addonChecker.run();
        assertEquals(1, uiInfo.size());
        assertEquals("Test " + productNameCval, uiInfo.get(0).product);
        assertEquals("evaluation", uiInfo.get(0).type);

        // Valid real license
        // -> Work as expected
        // -> Show info message “Using TouchKit 4 license
        // 312-312321-321312-3-12-312-312
        // licensed to <licensee> (1 developer license)"
        System.setProperty(licenseName, VALID_KEY);
        addonChecker.setLicenseProvider(validLicenseProvider);
        captureSystemOut();
        addonChecker.run();
        assertTrue(readSystemOut().contains("valid"));
    }

    @Test
    public void validateMultipleLicenses() throws Exception {
        addLicensedJarToClasspath(productNameCval, VAADIN_CVAL);
        addLicensedJarToClasspath(productNameAgpl, VAADIN_AGPL);
        addLicensedJarToClasspath(productNameApache, "apache");

        // We have a valid license for all products
        System.setProperty(licenseName, VALID_KEY);
        captureSystemOut();
        addonChecker.run();
        String out = readSystemOut();
        assertTrue(out.contains("valid"));
        assertTrue(out.contains("AGPL"));
        assertTrue(cacheExists(productNameCval));
    }

    private void setCacheFileTs(long expireTs, String type) {
        saveCache(productNameCval, null, false, expireTs, type);
    }

}
