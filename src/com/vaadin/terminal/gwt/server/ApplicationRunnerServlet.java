package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;

@SuppressWarnings("serial")
public class ApplicationRunnerServlet extends AbstractApplicationServlet {

    /**
     * The name of the application class currently used. Only valid within one
     * request.
     */
    private String[] defaultPackages;
    private HttpServletRequest request;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        String initParameter = servletConfig
                .getInitParameter("defaultPackages");
        if (initParameter != null) {
            defaultPackages = initParameter.split(",");
        }
    }

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        super.service(request, response);
        this.request = null;
    }

    @Override
    URL getApplicationUrl(HttpServletRequest request)
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
            final Application application = (Application) getApplicationClass()
                    .newInstance();
            return application;
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
        String applicationURI;
        String context;
        String runner;
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
        final String[] urlParts = request.getRequestURI().toString().split(
                "\\/");
        String context = null;
        String runner = null;
        URIS uris = new URIS();
        String applicationClassname = null;
        String contextPath = request.getContextPath();
        if (urlParts[1].equals(contextPath.replaceAll("\\/", ""))) {
            // class name comes after web context and runner application
            context = urlParts[1];
            runner = urlParts[2];
            if (urlParts.length == 3) {
                throw new IllegalArgumentException("No application specified");
            }
            applicationClassname = urlParts[3];

            uris.staticFilesPath = "/" + context;
            uris.applicationURI = "/" + context + "/" + runner + "/"
                    + applicationClassname;
            uris.context = context;
            uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        } else {
            // no context
            context = "";
            runner = urlParts[1];
            if (urlParts.length == 2) {
                throw new IllegalArgumentException("No application specified");
            }
            applicationClassname = urlParts[2];

            uris.staticFilesPath = "/";
            uris.applicationURI = "/" + runner + "/" + applicationClassname;
            uris.context = context;
            uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        }
        return uris;
    }

    // @Override
    @Override
    protected Class getApplicationClass() throws ClassNotFoundException {
        // TODO use getClassLoader() ?

        Class<? extends Application> appClass = null;

        String baseName = getApplicationRunnerApplicationClassName(request);
        try {
            appClass = (Class<? extends Application>) getClass()
                    .getClassLoader().loadClass(baseName);
            return appClass;
        } catch (Exception e) {
            //
            for (int i = 0; i < defaultPackages.length; i++) {
                try {
                    appClass = (Class<? extends Application>) getClass()
                            .getClassLoader().loadClass(
                                    defaultPackages[i] + "." + baseName);
                } catch (Exception e2) {
                    // TODO: handle exception
                }
                if (appClass != null) {
                    return appClass;
                }
            }
        }

        throw new ClassNotFoundException();
    }

    @Override
    String getRequestPathInfo(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null) {
            return null;
        }

        path = path.substring(1 + getApplicationRunnerApplicationClassName(
                request).length());
        return path;
    }

    @Override
    String getStaticFilesLocation(HttpServletRequest request) {
        URIS uris = getApplicationRunnerURIs(request);
        String staticFilesPath = uris.staticFilesPath;
        if (staticFilesPath.equals("/")) {
            staticFilesPath = "";
        }

        return staticFilesPath;
    }

}
