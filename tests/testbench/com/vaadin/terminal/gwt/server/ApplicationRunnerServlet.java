/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Root;

@SuppressWarnings("serial")
public class ApplicationRunnerServlet extends AbstractApplicationServlet {

    /**
     * Internal implementation of an application with a dynamically selected
     * Root implementation;
     */
    private static class RootRunnerApplication extends Application {
        private final Class<?> runnableClass;

        private RootRunnerApplication(Class<?> runnableClass) {
            this.runnableClass = runnableClass;
        }

        @Override
        protected String getRootClassName(WrappedRequest request) {
            return runnableClass.getCanonicalName();
        }
    }

    private static final Logger logger = Logger
            .getLogger(ApplicationRunnerServlet.class.getName());

    /**
     * The name of the application class currently used. Only valid within one
     * request.
     */
    private LinkedHashSet<String> defaultPackages = new LinkedHashSet<String>();

    private final ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        String initParameter = servletConfig
                .getInitParameter("defaultPackages");
        if (initParameter != null) {
            Collections.addAll(defaultPackages, initParameter.split(","));
        }
        String str = TestBase.class.getName().replace('.', '/') + ".class";
        URL url = getClassLoader().getResource(str);
        if ("file".equals(url.getProtocol())) {
            File comVaadinTests = new File(url.getPath()).getParentFile()
                    .getParentFile();
            addDirectories(comVaadinTests, defaultPackages, "com.vaadin.tests");

        }
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
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {

        // Creates a new application instance
        try {
            final Class<?> classToRun = getClassToRun();
            if (Root.class.isAssignableFrom(classToRun)) {
                return new RootRunnerApplication(classToRun);
            } else if (Application.class.isAssignableFrom(classToRun)) {
                return (Application) classToRun.newInstance();
            } else {
                throw new ServletException(classToRun.getCanonicalName()
                        + " is neither an Application nor a Root");
            }
        } catch (final IllegalAccessException e) {
            throw new ServletException(e);
        } catch (final InstantiationException e) {
            throw new ServletException(e);
        } catch (final ClassNotFoundException e) {
            throw new ServletException(
                    new InstantiationException(
                            "Failed to load application class: "
                                    + getApplicationRunnerApplicationClassName(request)));
        }

    }

    private String getApplicationRunnerApplicationClassName(
            HttpServletRequest request) {
        return getApplicationRunnerURIs(request).applicationClassname;
    }

    private static class URIS {
        String staticFilesPath;
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
        String context = null;
        // String runner = null;
        URIS uris = new URIS();
        String applicationClassname = null;
        String contextPath = request.getContextPath();
        if (urlParts[1].equals(contextPath.replaceAll("\\/", ""))) {
            // class name comes after web context and runner application
            context = urlParts[1];
            // runner = urlParts[2];
            if (urlParts.length == 3) {
                throw new IllegalArgumentException("No application specified");
            }
            applicationClassname = urlParts[3];

            uris.staticFilesPath = "/" + context;
            // uris.applicationURI = "/" + context + "/" + runner + "/"
            // + applicationClassname;
            // uris.context = context;
            // uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        } else {
            // no context
            context = "";
            // runner = urlParts[1];
            if (urlParts.length == 2) {
                throw new IllegalArgumentException("No application specified");
            }
            applicationClassname = urlParts[2];

            uris.staticFilesPath = "/";
            // uris.applicationURI = "/" + runner + "/" + applicationClassname;
            // uris.context = context;
            // uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        }
        return uris;
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException {
        Class<?> classToRun = getClassToRun();
        if (Root.class.isAssignableFrom(classToRun)) {
            return RootRunnerApplication.class;
        } else if (Application.class.isAssignableFrom(classToRun)) {
            return classToRun.asSubclass(Application.class);
        } else {
            throw new ClassCastException(classToRun.getCanonicalName()
                    + " is not an Application nor a Root");
        }
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
                    logger.log(Level.FINE, "Failed to find application class "
                            + pkg + "." + baseName, e2);
                }
                if (appClass != null) {
                    return appClass;
                }
            }

        }

        throw new ClassNotFoundException();
    }

    @Override
    protected String getRequestPathInfo(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null) {
            return null;
        }

        path = path.substring(1 + getApplicationRunnerApplicationClassName(
                request).length());
        return path;
    }

    @Override
    protected String getStaticFilesLocation(HttpServletRequest request) {
        URIS uris = getApplicationRunnerURIs(request);
        String staticFilesPath = uris.staticFilesPath;
        if (staticFilesPath.equals("/")) {
            staticFilesPath = "";
        }

        return staticFilesPath;
    }

    @Override
    protected WrappedHttpServletRequest createWrappedRequest(
            HttpServletRequest request) {
        return new WrappedHttpServletRequest(request,
                getDeploymentConfiguration()) {
            @Override
            public String getRequestPathInfo() {
                return ApplicationRunnerServlet.this.getRequestPathInfo(this);
            }
        };
    }

}
