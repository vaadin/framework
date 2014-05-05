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
package com.vaadin.launcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.LegacyVaadinServlet;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class ApplicationRunnerServlet extends LegacyVaadinServlet {

    /**
     * The name of the application class currently used. Only valid within one
     * request.
     */
    private LinkedHashSet<String> defaultPackages = new LinkedHashSet<String>();

    private transient final ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        String initParameter = servletConfig
                .getInitParameter("defaultPackages");
        if (initParameter != null) {
            Collections.addAll(defaultPackages, initParameter.split(","));
        }
        String str = TestBase.class.getName().replace('.', '/') + ".class";
        URL url = getService().getClassLoader().getResource(str);
        if ("file".equals(url.getProtocol())) {
            String path = url.getPath();
            try {
                path = new URI(path).getPath();
            } catch (URISyntaxException e) {
                getLogger().log(Level.FINE, "Failed to decode url", e);
            }
            File comVaadinTests = new File(path).getParentFile()
                    .getParentFile();
            addDirectories(comVaadinTests, defaultPackages, "com.vaadin.tests");

        }
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                onVaadinSessionStarted(event.getRequest(), event.getSession());
            }
        });
    }

    private void addDirectories(File parent, LinkedHashSet<String> packages,
            String parentPackage) {
        packages.add(parentPackage);

        for (File f : parent.listFiles()) {
            if (f.isDirectory()) {
                String newPackage = parentPackage + "." + f.getName();
                addDirectories(f, packages, newPackage);
            }
        }
    }

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        this.request.set(request);
        try {
            super.service(request, response);
        } finally {
            this.request.set(null);
        }
    }

    @Override
    protected URL getApplicationUrl(HttpServletRequest request)
            throws MalformedURLException {
        URL url = super.getApplicationUrl(request);

        String path = url.toString();
        path += getApplicationRunnerApplicationClassName(request);
        path += "/";

        return new URL(path);
    }

    @Override
    protected Class<? extends LegacyApplication> getApplicationClass()
            throws ClassNotFoundException {
        return getClassToRun().asSubclass(LegacyApplication.class);
    }

    @Override
    protected boolean shouldCreateApplication(HttpServletRequest request)
            throws ServletException {
        try {
            return LegacyApplication.class.isAssignableFrom(getClassToRun());
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }

    protected void onVaadinSessionStarted(VaadinRequest request,
            VaadinSession session) throws ServiceException {
        try {
            final Class<?> classToRun = getClassToRun();
            if (UI.class.isAssignableFrom(classToRun)) {
                session.addUIProvider(new ApplicationRunnerUIProvider(
                        classToRun));
            } else if (LegacyApplication.class.isAssignableFrom(classToRun)) {
                // Avoid using own UIProvider for legacy Application
            } else if (UIProvider.class.isAssignableFrom(classToRun)) {
                session.addUIProvider((UIProvider) classToRun.newInstance());
            } else {
                throw new ServiceException(classToRun.getCanonicalName()
                        + " is neither an Application nor a UI");
            }
        } catch (final IllegalAccessException e) {
            throw new ServiceException(e);
        } catch (final InstantiationException e) {
            throw new ServiceException(e);
        } catch (final ClassNotFoundException e) {
            throw new ServiceException(
                    new InstantiationException(
                            "Failed to load application class: "
                                    + getApplicationRunnerApplicationClassName((VaadinServletRequest) request)));
        }
    }

    private String getApplicationRunnerApplicationClassName(
            HttpServletRequest request) {
        return getApplicationRunnerURIs(request).applicationClassname;
    }

    private static final class ApplicationRunnerUIProvider extends UIProvider {
        private final Class<?> classToRun;

        private ApplicationRunnerUIProvider(Class<?> classToRun) {
            this.classToRun = classToRun;
        }

        @Override
        public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
            return (Class<? extends UI>) classToRun;
        }
    }

    // TODO Don't need to use a data object now that there's only one field
    private static class URIS {
        // String staticFilesPath;
        // String applicationURI;
        // String context;
        // String runner;
        String applicationClassname;

    }

    /**
     * Parses application runner URIs.
     * 
     * If request URL is e.g.
     * http://localhost:8080/vaadin/run/com.vaadin.demo.Calc then
     * <ul>
     * <li>context=vaadin</li>
     * <li>Runner servlet=run</li>
     * <li>Vaadin application=com.vaadin.demo.Calc</li>
     * </ul>
     * 
     * @param request
     * @return string array containing widgetset URI, application URI and
     *         context, runner, application classname
     */
    private static URIS getApplicationRunnerURIs(HttpServletRequest request) {
        final String[] urlParts = request.getRequestURI().toString()
                .split("\\/");
        // String runner = null;
        URIS uris = new URIS();
        String applicationClassname = null;
        String contextPath = request.getContextPath();
        if (urlParts[1].equals(contextPath.replaceAll("\\/", ""))) {
            // class name comes after web context and runner application
            // runner = urlParts[2];
            if (urlParts.length == 3) {
                throw new IllegalArgumentException("No application specified");
            }
            applicationClassname = urlParts[3];

            // uris.applicationURI = "/" + context + "/" + runner + "/"
            // + applicationClassname;
            // uris.context = context;
            // uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        } else {
            // no context
            // runner = urlParts[1];
            if (urlParts.length == 2) {
                throw new IllegalArgumentException("No application specified");
            }
            applicationClassname = urlParts[2];

            // uris.applicationURI = "/" + runner + "/" + applicationClassname;
            // uris.context = context;
            // uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        }
        return uris;
    }

    private Class<?> getClassToRun() throws ClassNotFoundException {
        // TODO use getClassLoader() ?

        Class<?> appClass = null;

        String baseName = getApplicationRunnerApplicationClassName(request
                .get());
        try {
            appClass = getClass().getClassLoader().loadClass(baseName);
            return appClass;
        } catch (Exception e) {
            //
            for (String pkg : defaultPackages) {
                try {
                    appClass = getClass().getClassLoader().loadClass(
                            pkg + "." + baseName);
                } catch (ClassNotFoundException ee) {
                    // Ignore as this is expected for many packages
                } catch (Exception e2) {
                    // TODO: handle exception
                    getLogger().log(
                            Level.FINE,
                            "Failed to find application class " + pkg + "."
                                    + baseName, e2);
                }
                if (appClass != null) {
                    return appClass;
                }
            }

        }

        throw new ClassNotFoundException(baseName);
    }

    private Logger getLogger() {
        return Logger.getLogger(ApplicationRunnerServlet.class.getName());
    }

}
