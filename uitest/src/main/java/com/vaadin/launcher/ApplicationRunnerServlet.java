package com.vaadin.launcher;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.LegacyVaadinServlet;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@SuppressWarnings("serial")
public class ApplicationRunnerServlet extends LegacyVaadinServlet {

    private static class ApplicationRunnerRedirectException
            extends RuntimeException {
        private final String target;

        public ApplicationRunnerRedirectException(String target) {
            this.target = target;
        }

        public String getTarget() {
            return target;
        }

        public static String extractRedirectTarget(ServletException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ServiceException) {
                ServiceException se = (ServiceException) cause;
                Throwable serviceCause = se.getCause();
                if (serviceCause instanceof ApplicationRunnerRedirectException) {
                    ApplicationRunnerRedirectException redirect = (ApplicationRunnerRedirectException) serviceCause;
                    return redirect.getTarget();
                }
            }

            return null;
        }
    }

    public static String CUSTOM_SYSTEM_MESSAGES_PROPERTY = "custom-"
            + SystemMessages.class.getName();

    /**
     * The name of the application class currently used. Only valid within one
     * request.
     */
    private LinkedHashSet<String> defaultPackages = new LinkedHashSet<>();

    private transient final ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

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
        getService().addSessionInitListener(event -> onVaadinSessionStarted(
                event.getRequest(), event.getSession()));
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
        } catch (ServletException e) {
            String redirectTarget = ApplicationRunnerRedirectException
                    .extractRedirectTarget(e);
            if (redirectTarget != null) {
                response.sendRedirect(redirectTarget + "?restartApplication");
            } else {
                // Not the exception we were looking for, just rethrow
                throw e;
            }
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
                session.addUIProvider(
                        new ApplicationRunnerUIProvider(classToRun));
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
            throw new ServiceException(new InstantiationException(
                    "Failed to load application class: "
                            + getApplicationRunnerApplicationClassName(
                                    (VaadinServletRequest) request)));
        }
    }

    private String getApplicationRunnerApplicationClassName(
            HttpServletRequest request) {
        return getApplicationRunnerURIs(request).applicationClassname;
    }

    private static final class ProxyDeploymentConfiguration
            implements InvocationHandler, Serializable {
        private final DeploymentConfiguration originalConfiguration;

        private ProxyDeploymentConfiguration(
                DeploymentConfiguration originalConfiguration) {
            this.originalConfiguration = originalConfiguration;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            if (method.getDeclaringClass() == DeploymentConfiguration.class) {
                // Find the configuration instance to delegate to
                DeploymentConfiguration configuration = findDeploymentConfiguration(
                        originalConfiguration);

                return method.invoke(configuration, args);
            } else {
                return method.invoke(proxy, args);
            }
        }
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

        @Override
        public UI createInstance(UICreateEvent event) {
            event.getRequest().setAttribute(ApplicationConstants.UI_ROOT_PATH,
                    "/" + event.getUIClass().getName());
            return super.createInstance(event);
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
        final String[] urlParts = request.getRequestURI().split("\\/");
        // String runner = null;
        URIS uris = new URIS();
        String applicationClassname = null;
        String contextPath = request.getContextPath();
        if (urlParts[1].equals(contextPath.replaceAll("\\/", ""))) {
            // class name comes after web context and runner application
            // runner = urlParts[2];
            if (urlParts.length == 3) {
                throw new ApplicationRunnerRedirectException(
                        findLastModifiedApplication());
            } else {
                applicationClassname = urlParts[3];
            }

            // uris.applicationURI = "/" + context + "/" + runner + "/"
            // + applicationClassname;
            // uris.context = context;
            // uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        } else {
            // no context
            // runner = urlParts[1];
            if (urlParts.length == 2) {
                throw new ApplicationRunnerRedirectException(
                        findLastModifiedApplication());
            } else {
                applicationClassname = urlParts[2];
            }

            // uris.applicationURI = "/" + runner + "/" + applicationClassname;
            // uris.context = context;
            // uris.runner = runner;
            uris.applicationClassname = applicationClassname;
        }
        return uris;
    }

    private static String findLastModifiedApplication() {
        String lastModifiedClassName = null;

        File uitestDir = new File("src/main/java");
        if (uitestDir.isDirectory()) {
            LinkedList<File> stack = new LinkedList<>();
            stack.add(uitestDir);

            long lastModifiedTimestamp = Long.MIN_VALUE;
            while (!stack.isEmpty()) {
                File file = stack.pop();
                if (file.isDirectory()) {
                    stack.addAll(Arrays.asList(file.listFiles()));
                } else if (file.getName().endsWith(".java")) {
                    if (lastModifiedTimestamp < file.lastModified()) {
                        String className = file.getPath()
                                .substring(uitestDir.getPath().length() + 1)
                                .replace(File.separatorChar, '.');
                        className = className.substring(0,
                                className.length() - ".java".length());
                        if (isSupportedClass(className)) {
                            lastModifiedTimestamp = file.lastModified();
                            lastModifiedClassName = className;
                        }
                    }
                }
            }
        }

        if (lastModifiedClassName == null) {
            throw new IllegalArgumentException("No application specified");
        } else {
            return lastModifiedClassName;
        }
    }

    private static boolean isSupportedClass(String className) {
        try {
            Class<?> type = Class.forName(className, false,
                    ApplicationRunnerServlet.class.getClassLoader());

            if (UI.class.isAssignableFrom(type)) {
                return true;
            } else if (LegacyApplication.class.isAssignableFrom(type)) {
                return true;
            } else if (UIProvider.class.isAssignableFrom(type)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Class<?> getClassToRun() throws ClassNotFoundException {
        // TODO use getClassLoader() ?

        Class<?> appClass = null;

        String baseName = getApplicationRunnerApplicationClassName(
                request.get());
        try {
            appClass = getClass().getClassLoader().loadClass(baseName);
            return appClass;
        } catch (Exception e) {
            //
            for (String pkg : defaultPackages) {
                try {
                    appClass = getClass().getClassLoader()
                            .loadClass(pkg + "." + baseName);
                } catch (ClassNotFoundException ee) {
                    // Ignore as this is expected for many packages
                } catch (Exception e2) {
                    // TODO: handle exception
                    getLogger().log(Level.FINE,
                            "Failed to find application class " + pkg + "."
                                    + baseName,
                            e2);
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

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        // Get the original configuration from the super class
        final DeploymentConfiguration originalConfiguration = super.createDeploymentConfiguration(
                initParameters);

        // And then create a proxy instance that delegates to the original
        // configuration or a customized version
        return (DeploymentConfiguration) Proxy.newProxyInstance(
                DeploymentConfiguration.class.getClassLoader(),
                new Class[] { DeploymentConfiguration.class },
                new ProxyDeploymentConfiguration(originalConfiguration));
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        VaadinServletService service = super.createServletService(
                deploymentConfiguration);
        final SystemMessagesProvider provider = service
                .getSystemMessagesProvider();
        service.setSystemMessagesProvider(systemMessagesInfo -> {
            if (systemMessagesInfo.getRequest() == null) {
                return provider.getSystemMessages(systemMessagesInfo);
            }
            Object messages = systemMessagesInfo.getRequest()
                    .getAttribute(CUSTOM_SYSTEM_MESSAGES_PROPERTY);
            if (messages instanceof SystemMessages) {
                return (SystemMessages) messages;
            }
            return provider.getSystemMessages(systemMessagesInfo);
        });
        return service;
    }

    private static DeploymentConfiguration findDeploymentConfiguration(
            DeploymentConfiguration originalConfiguration) throws Exception {
        // First level of cache
        DeploymentConfiguration configuration = CurrentInstance
                .get(DeploymentConfiguration.class);

        if (configuration == null) {
            // Not in cache, try to find a VaadinSession to get it from
            VaadinSession session = VaadinSession.getCurrent();

            if (session == null) {
                /*
                 * There's no current session, request or response when serving
                 * static resources, but there's still the current request
                 * maintained by AppliationRunnerServlet, and there's most
                 * likely also a HttpSession containing a VaadinSession for that
                 * request.
                 */

                HttpServletRequest currentRequest = VaadinServletService
                        .getCurrentServletRequest();
                if (currentRequest != null) {
                    HttpSession httpSession = currentRequest.getSession(false);
                    if (httpSession != null) {
                        Map<Class<?>, CurrentInstance> oldCurrent = CurrentInstance
                                .setCurrent((VaadinSession) null);
                        try {
                            VaadinServletService service = (VaadinServletService) VaadinService
                                    .getCurrent();
                            session = service.findVaadinSession(
                                    new VaadinServletRequest(currentRequest,
                                            service));
                        } finally {
                            /*
                             * Clear some state set by findVaadinSession to
                             * avoid accidentally depending on it when coding on
                             * e.g. static request handling.
                             */
                            CurrentInstance.restoreInstances(oldCurrent);
                            currentRequest.removeAttribute(
                                    VaadinSession.class.getName());
                        }
                    }
                }
            }

            if (session != null) {
                String name = ApplicationRunnerServlet.class.getName()
                        + ".deploymentConfiguration";
                try {
                    session.lock();
                    configuration = (DeploymentConfiguration) session
                            .getAttribute(name);

                    if (configuration == null) {
                        ApplicationRunnerServlet servlet = (ApplicationRunnerServlet) VaadinServlet
                                .getCurrent();
                        Class<?> classToRun;
                        try {
                            classToRun = servlet.getClassToRun();
                        } catch (ClassNotFoundException e) {
                            /*
                             * This happens e.g. if the UI class defined in the
                             * URL is not found or if this servlet just serves
                             * static resources while there's some other servlet
                             * that serves the UI (e.g. when using /run-push/).
                             */
                            return originalConfiguration;
                        }

                        CustomDeploymentConfiguration customDeploymentConfiguration = classToRun
                                .getAnnotation(
                                        CustomDeploymentConfiguration.class);
                        if (customDeploymentConfiguration != null) {
                            Properties initParameters = new Properties(
                                    originalConfiguration.getInitParameters());

                            for (Conf entry : customDeploymentConfiguration
                                    .value()) {
                                initParameters.put(entry.name(), entry.value());
                            }

                            configuration = new DefaultDeploymentConfiguration(
                                    servlet.getClass(), initParameters);
                        } else {
                            configuration = originalConfiguration;
                        }

                        session.setAttribute(name, configuration);
                    }
                } finally {
                    session.unlock();
                }

                CurrentInstance.set(DeploymentConfiguration.class,
                        configuration);

            } else {
                configuration = originalConfiguration;
            }
        }
        return configuration;
    }
}
