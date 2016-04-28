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

import static com.vaadin.tools.CvalChecker.LINE;
import static com.vaadin.tools.CvalChecker.computeMajorVersion;
import static com.vaadin.tools.CvalChecker.getErrorMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.vaadin.client.metadata.ConnectorBundleLoader.CValUiInfo;
import com.vaadin.tools.CvalChecker.CvalInfo;
import com.vaadin.tools.CvalChecker.CvalServer;
import com.vaadin.tools.CvalChecker.InvalidCvalException;
import com.vaadin.tools.CvalChecker.UnreachableCvalServerException;

/**
 * This class is able to visit all MANIFEST.MF files present in the classpath,
 * filter by name, and check if the user has a valid license.
 *
 * Manifest files should have a few attributes indicating the license type of
 * the addon:
 * <ul>
 * <li>Implementation-Version: 4.x.x
 * <li>AdVaaName: addon_name
 * <li>AdVaaLicen: cval, agpl, empty
 * <li>AdVaaPkg: package of the widgets in this addon
 * </ul>
 *
 * The class also have a method to check just one product.
 *
 * @since 7.3
 */
public final class CvalAddonsChecker {

    // Manifest attributes
    public static final String VAADIN_ADDON_LICENSE = "AdVaaLicen";
    public static final String VAADIN_ADDON_NAME = "AdVaaName";
    public static final String VAADIN_ADDON_WIDGETSET = "Vaadin-Widgetsets";
    public static final String VAADIN_ADDON_VERSION = "Implementation-Version";
    public static final String VAADIN_ADDON_TITLE = "Implementation-Title";

    // License types
    public static final String VAADIN_AGPL = "agpl";
    public static final String VAADIN_CVAL = "cval";

    private CvalChecker cvalChecker = new CvalChecker();
    private String filterPattern;

    /**
     * The constructor.
     */
    public CvalAddonsChecker() {
        setLicenseProvider(new CvalServer());
        setFilter(".*vaadin.*");
    }

    /**
     * Visit all MANIFEST.MF files in the classpath validating licenses.
     * 
     * Return a list of Cval licensed products in order to have enough info to
     * generate nag messages in the UI.
     */
    public List<CValUiInfo> run() throws InvalidCvalException {
        List<CValUiInfo> ret = new ArrayList<CValUiInfo>();
        try {
            // Visit all MANIFEST in our classpath
            Enumeration<URL> manifests = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(JarFile.MANIFEST_NAME);
            while (manifests.hasMoreElements()) {
                try {
                    URL url = manifests.nextElement();
                    // Discard manifests whose name does not match the filter
                    // pattern
                    if (!url.getPath().matches(filterPattern)) {
                        continue;
                    }
                    InputStream is = url.openStream();
                    // Should never happen, but we don't want a NPE here
                    if (is == null) {
                        continue;
                    }
                    // Read manifest attributes
                    Manifest manifest = new Manifest(is);
                    Attributes attribs = manifest.getMainAttributes();
                    String license = attribs.getValue(VAADIN_ADDON_LICENSE);
                    String name = attribs.getValue(VAADIN_ADDON_NAME);
                    String vers = attribs.getValue(VAADIN_ADDON_VERSION) == null ? ""
                            : attribs.getValue(VAADIN_ADDON_VERSION);
                    String title = attribs.getValue(VAADIN_ADDON_TITLE) == null ? name
                            : attribs.getValue(VAADIN_ADDON_TITLE);

                    String widgetsets = attribs
                            .getValue(VAADIN_ADDON_WIDGETSET) == null ? name
                            : attribs.getValue(VAADIN_ADDON_WIDGETSET);

                    if (name == null || license == null) {
                        continue;
                    }
                    if (VAADIN_AGPL.equals(license)) {
                        // For agpl version we print an info message
                        printAgplLicense(title, vers);
                    } else if (VAADIN_CVAL.equals(license)) {
                        // We only check cval licensed products
                        CvalInfo info;
                        try {
                            info = cvalChecker.validateProduct(name, vers,
                                    title);
                            printValidLicense(info, title, vers);
                        } catch (UnreachableCvalServerException e) {
                            info = CvalChecker.parseJson("{'product':{'name':'"
                                    + name + "'}}");
                            printServerUnreachable(title, vers);
                        }
                        for (String w : widgetsets.split("[, ]+")) {
                            ret.add(new CValUiInfo(title, String
                                    .valueOf(computeMajorVersion(vers)), w,
                                    info.getType()));
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
        return ret;
    }

    /**
     * Set the filter regexp of .jar names which we have to consider.
     * 
     * default is '.*touchkit.*'
     */
    public CvalAddonsChecker setFilter(String regexp) {
        filterPattern = regexp;
        return this;
    }

    /*
     * Change the license provider, only used in tests.
     */
    protected CvalAddonsChecker setLicenseProvider(CvalServer p) {
        cvalChecker.setLicenseProvider(p);
        return this;
    }

    private void printAgplLicense(String name, String version) {
        System.out.println(LINE + "\n"
                + getErrorMessage("agpl", name, computeMajorVersion(version))
                + "\n" + LINE);
    }

    private void printServerUnreachable(String name, String version) {
        System.out.println(LINE
                + "\n"
                + getErrorMessage("unreachable", name,
                        computeMajorVersion(version)) + "\n" + LINE);
    }

    private void printValidLicense(CvalInfo info, String title, String version) {
        String msg = info.getMessage();
        if (msg == null) {
            String key = "evaluation".equals(info.getType()) ? "evaluation"
                    : "valid";
            msg = getErrorMessage(key, title, computeMajorVersion(version),
                    info.getLicensee());
        }
        System.out.println("\n" + LINE + "\n" + msg + "\n" + LINE + "\n");
    }
}
