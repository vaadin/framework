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

package com.vaadin.tools;

import static com.vaadin.tools.CvalAddonsChecker.VAADIN_ADDON_LICENSE;
import static com.vaadin.tools.CvalAddonsChecker.VAADIN_ADDON_NAME;
import static com.vaadin.tools.CvalAddonsChecker.VAADIN_ADDON_TITLE;
import static com.vaadin.tools.CvalAddonsChecker.VAADIN_ADDON_VERSION;
import static com.vaadin.tools.CvalChecker.GRACE_DAYS_MSECS;
import static com.vaadin.tools.CvalChecker.cacheLicenseInfo;
import static com.vaadin.tools.CvalChecker.deleteCache;
import static com.vaadin.tools.CvalChecker.parseJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.prefs.Preferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.tools.CvalChecker.CvalInfo;
import com.vaadin.tools.CvalChecker.CvalServer;
import com.vaadin.tools.CvalChecker.InvalidCvalException;
import com.vaadin.tools.CvalChecker.UnreachableCvalServerException;

/**
 * The CvalChecker test.
 */
public class CvalCheckerTest {

    static final String productNameCval = "test.cval";
    static final String productTitleCval = "Vaadin Test";
    static final String productNameAgpl = "test.agpl";
    static final String productTitleAgpl = "Vaadin Test";
    static final String productNameApache = "test.apache";
    static final String VALID_KEY = "valid";
    static final String INVALID_KEY = "invalid";

    static final String responseJson = "{'licenseKey':'" + VALID_KEY + "',"
            + "'licensee':'Test User','type':'normal',"
            + "'expiredEpoch':'1893511225000'," + "'product':{'name':'"
            + productNameCval + "', 'version': 2}}";

    private static ByteArrayOutputStream outContent;

    // A provider returning a valid license if productKey is valid or null if
    // invalid
    static final CvalServer validLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return VALID_KEY.equals(productKey) ? responseJson : null;
        }
    };
    // A provider returning a valid evaluation license
    static final CvalServer validEvaluationLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return responseJson.replace("normal", "evaluation");
        }
    };
    // A provider returning an expired license with a server message
    static final CvalServer expiredLicenseProviderWithMessage = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return responseJson
                    .replace("'expired",
                            "'message':'Custom\\\\nServer\\\\nMessage','expired':true,'expired");
        }
    };
    // A provider returning an expired license with a server message
    static final CvalServer expiredLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return responseJson.replace("'expired", "'expired':true,'expired");
        }
    };
    // A provider returning an expired epoch license
    static final CvalServer expiredEpochLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            long ts = System.currentTimeMillis() - GRACE_DAYS_MSECS - 1000;
            return responseJson.replace("1893511225000", "" + ts);
        }
    };
    // A provider returning an unlimited license
    static final CvalServer unlimitedLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return responseJson.replaceFirst("1893511225000", "");
        }
    };
    // An unreachable provider
    static final CvalServer unreachableLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout)
                throws IOException {
            // Normally there is no route for this ip in public routers, so we
            // should get a timeout.
            licenseUrl = "http://localhost:9999/";
            return super.askServer(productName, productKey, 1000);
        }
    };

    private CvalChecker licenseChecker;
    private String licenseName;

    @Before
    public void setup() {
        licenseChecker = new CvalChecker()
                .setLicenseProvider(validLicenseProvider);
        licenseName = CvalChecker.computeLicenseName(productNameCval);
        System.getProperties().remove(licenseName);
        deleteCache(productNameCval);
    }

    @Test
    public void testValidateProduct() throws Exception {
        deleteCache(productNameCval);

        // If the license key in our environment is null, throw an exception
        try {
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertFalse(cacheExists(productNameCval));

        // If the license key is empty, throw an exception
        System.setProperty(licenseName, "");
        try {
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertFalse(cacheExists(productNameCval));

        // If license key is invalid, throw an exception
        System.setProperty(licenseName, "invalid");
        try {
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertFalse(cacheExists(productNameCval));

        // Fail if version is bigger
        System.setProperty(licenseName, VALID_KEY);
        try {
            licenseChecker.validateProduct(productNameCval, "3.0",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertFalse(cacheExists(productNameCval));

        // Success if license key and version are valid
        System.setProperty(licenseName, VALID_KEY);
        licenseChecker
                .validateProduct(productNameCval, "2.1", productTitleCval);
        Assert.assertTrue(cacheExists(productNameCval));

        // Success if license and cache file are valid, although the license
        // server is offline
        licenseChecker.setLicenseProvider(unreachableLicenseProvider);
        licenseChecker
                .validateProduct(productNameCval, "2.1", productTitleCval);
        Assert.assertTrue(cacheExists(productNameCval));

        // Fail if license key changes although cache file were validated
        // previously and it is ok, we are offline
        try {
            System.setProperty(licenseName, INVALID_KEY);
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.fail();
        } catch (UnreachableCvalServerException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertFalse(cacheExists(productNameCval));

        // Fail with unreachable exception if license has never verified and
        // server is offline
        try {
            System.setProperty(licenseName, VALID_KEY);
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.fail();
        } catch (UnreachableCvalServerException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertFalse(cacheExists(productNameCval));

        // Fail when expired flag comes in the server response, although the
        // expired is valid.
        deleteCache(productNameCval);
        licenseChecker.setLicenseProvider(expiredLicenseProviderWithMessage);
        try {
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            assertEquals(productNameCval, expected.name);
            // Check that we use server customized message if it comes
            Assert.assertTrue(expected.getMessage().contains("Custom"));
        }
        Assert.assertTrue(cacheExists(productNameCval));

        // Check an unlimited license
        licenseChecker.setLicenseProvider(unlimitedLicenseProvider);
        licenseChecker
                .validateProduct(productNameCval, "2.1", productTitleCval);
        Assert.assertTrue(cacheExists(productNameCval));

        // Fail if expired flag does not come, but expired epoch is in the past
        System.setProperty(licenseName, VALID_KEY);
        deleteCache(productNameCval);
        licenseChecker.setLicenseProvider(expiredEpochLicenseProvider);
        try {
            licenseChecker.validateProduct(productNameCval, "2.1",
                    productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            assertEquals(productNameCval, expected.name);
        }
        Assert.assertTrue(cacheExists(productNameCval));
    }

    /*
     * Creates a new .jar file with a MANIFEST.MF with all vaadin license info
     * attributes set, and add the .jar to the classpath
     */
    static void addLicensedJarToClasspath(String productName, String licenseType)
            throws Exception {
        // Create a manifest with Vaadin CVAL license
        Manifest testManifest = new Manifest();
        testManifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        testManifest.getMainAttributes().putValue(VAADIN_ADDON_LICENSE,
                licenseType);
        testManifest.getMainAttributes().putValue(VAADIN_ADDON_NAME,
                productName);
        testManifest.getMainAttributes().putValue(VAADIN_ADDON_TITLE,
                "Test " + productName);
        testManifest.getMainAttributes().putValue(VAADIN_ADDON_VERSION, "2");

        // Create a temporary Jar
        String tmpDir = System.getProperty("java.io.tmpdir");
        File testJarFile = new File(tmpDir + "vaadin." + productName + ".jar");
        testJarFile.deleteOnExit();
        JarOutputStream target = new JarOutputStream(new FileOutputStream(
                testJarFile), testManifest);
        target.close();

        // Add the new jar to our classpath (use reflection)
        URL url = new URL("file://" + testJarFile.getAbsolutePath());
        final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",
                new Class[] { URL.class });
        addURL.setAccessible(true);
        final URLClassLoader urlClassLoader = (URLClassLoader) Thread
                .currentThread().getContextClassLoader();
        addURL.invoke(urlClassLoader, new Object[] { url });
    }

    static boolean cacheExists(String productName) {
        return cachedPreferences(productName) != null;
    }

    static String cachedPreferences(String productName) {
        // ~/Library/Preferences/com.apple.java.util.prefs.plist
        // .java/.userPrefs/com/google/gwt/dev/shell/prefs.xml
        // HKEY_CURRENT_USER\SOFTWARE\JavaSoft\Prefs
        Preferences p = Preferences.userNodeForPackage(CvalInfo.class);
        return p.get(productName, null);
    }

    static void saveCache(String productName, String key, Boolean expired,
            Long expireTs, String type) {
        String json = responseJson.replace(productNameCval, productName);
        if (expired != null && expired) {
            expireTs = System.currentTimeMillis() - GRACE_DAYS_MSECS - 1000;
        }
        if (expireTs != null) {
            json = json.replace("1893511225000", "" + expireTs);
        }
        if (key != null) {
            json = json.replace(VALID_KEY, key);
        }
        if (type != null) {
            json = json.replace("normal", type);

        }
        cacheLicenseInfo(parseJson(json));
    }

    static void captureSystemOut() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    static String readSystemOut() {
        restoreSystemOut();
        return outContent.toString();
    }

    static void restoreSystemOut() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @Test(expected = FileNotFoundException.class)
    public void testReadKeyFromFile_NonexistingLicenseFile() throws Exception {
        licenseChecker.readKeyFromFile(new URL("file:///foobar.baz"), 4);
    }

    @Test
    public void testReadKeyFromFile_LicenseFileEmpty() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");

        assertNull(licenseChecker.readKeyFromFile(tmpLicenseFile.toURI()
                .toURL(), 4));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasSingleUnidentifiedKey()
            throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("this-is-a-license");
        out.close();

        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 4));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasSingleIdentifiedKey()
            throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4=this-is-a-license");
        out.close();

        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 4));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasMultipleKeys()
            throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4=this-is-a-license");
        out.println("5=this-is-another-license");
        out.close();

        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 4));
        assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 5));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasMultipleKeysWithWhitespace()
            throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4 = this-is-a-license");
        out.println("5 = this-is-another-license");
        out.close();

        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 4));
        assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 5));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_RequestedVersionMissing() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4 = this-is-a-license");
        out.println("5 = this-is-another-license");
        out.close();

        assertNull(licenseChecker.readKeyFromFile(tmpLicenseFile.toURI()
                .toURL(), 3));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_FallbackToDefaultKey() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("this-is-a-license");
        out.println("5 = this-is-another-license");
        out.close();

        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI()
                .toURL(), 3));
        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI()
                .toURL(), 4));
        assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 5));

        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_FallbackToDefaultKeyReversed() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("5 = this-is-another-license");
        out.println("this-is-a-license");
        out.close();

        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI()
                .toURL(), 3));
        assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI()
                .toURL(), 4));
        assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(
                tmpLicenseFile.toURI().toURL(), 5));

        tmpLicenseFile.delete();
    }
}
