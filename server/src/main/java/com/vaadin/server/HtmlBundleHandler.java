/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.Dependency;
import com.vaadin.ui.Dependency.Type;

/**
 * Takes care of rewriting HTML resources into a HTML bundle, if such a bundle
 * is used.
 */
public class HtmlBundleHandler implements Serializable {

    private HtmlBundleHandler() {
        // Util methods only
    }

    /**
     * Processes the given dependencies and replaces any suitable HTML import
     * with the used HTML bundle.
     * <p>
     * If no HTML bundle is defined, tries to auto detect one using
     * {@link #getHtmlBundleName(VaadinSession)}.
     * <p>
     * If no HTML bundle is used, returns the original dependencies.
     *
     * @param deps
     *            the dependencies to transform
     * @param session
     *            the user session
     * @return a rewritten list of dependencies
     */
    public static List<Dependency> replaceWithBundle(List<Dependency> deps,
            VaadinSession session) {
        String bundleName = getHtmlBundleName(session);
        if (bundleName == null) {
            return deps;
        }

        List<Dependency> newDeps = new ArrayList<>();

        boolean bundleAdded = false;
        for (Dependency dep : deps) {
            if (dep.getType() == Type.HTMLIMPORT
                    && dep.getUrl().startsWith("frontend://")) {
                if (!bundleAdded) {
                    newDeps.add(new Dependency(Type.HTMLIMPORT, bundleName));
                    bundleAdded = true;
                }
            } else {
                newDeps.add(dep);
            }

        }
        return newDeps;
    }

    /**
     * Gets the name of the bundle to use for HTML imports.
     *
     * @param session
     *            the vaadin session
     * @return the name of the bundle if a bundle should be used, null if no
     *         bundle should be used.
     */
    private static String getHtmlBundleName(VaadinSession session) {
        VaadinService vaadinService = session.getService();
        DeploymentConfiguration deploymentConfiguration = vaadinService
                .getDeploymentConfiguration();
        String definedBundleName = deploymentConfiguration
                .getApplicationOrSystemProperty(
                        ApplicationConstants.HTML_BUNDLE_NAME, null);
        if (definedBundleName != null) {
            if ("".equals(definedBundleName)) {
                return null;
            } else {
                return definedBundleName;
            }
        }

        // Not defined - use auto detection
        if (!(vaadinService instanceof VaadinServletService)) {
            // Must define bundle in portals
            return null;
        }
        String bundleUrl = BootstrapHandler.resolveFrontendUrl(session)
                + "bundle.html";

        // bundleFileName is used to find the bundle in the web content folder
        // or in the classpath. There "vaadin://" is always "/VAADIN/"
        // The real URL to load from is determined elsewhere as the returned
        // bundleUrl can still contain "vaadin://"
        String bundleFileName = bundleUrl.replace("vaadin://", "/VAADIN/");
        VaadinServlet servlet = ((VaadinServletService) vaadinService)
                .getServlet();
        try {
            URL autoDetected = servlet.findResourceURL(bundleFileName);
            if (autoDetected != null) {
                return bundleUrl;
            }
        } catch (IOException e) {
            getLogger().log(Level.FINE,
                    "Error finding default bundle " + bundleFileName, e);
        }

        return null;
    }

    private static Logger getLogger() {
        return Logger.getLogger(HtmlBundleHandler.class.getName());
    }

}
