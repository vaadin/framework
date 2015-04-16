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
package com.vaadin.server.communication;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.atmosphere.cpr.AtmosphereFramework;

import com.vaadin.server.VaadinServlet;

/**
 * Initializer class for JSR 356 websockets.
 * <p>
 * Websocket specification says that initialization of websocket end points
 * should be done in the servlet context initialization phase. Some servers
 * implement this strictly so that end points cannot be registered after the
 * context initialization phase.
 * <p>
 * Note that {@link WebListener} is Servlet 3.0 API so this will not be run for
 * older servers (unless added to web.xml), but these servers do not support JSR
 * 356 websockets either.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
@WebListener
public class JSR356WebsocketInitializer implements ServletContextListener {

    private static boolean atmosphereAvailable = false;
    static {
        try {
            org.atmosphere.util.Version.getRawVersion();
            atmosphereAvailable = true;
        } catch (NoClassDefFoundError e) {
        }
    }

    /**
     * "ServletConfig" which only provides information from a
     * {@link ServletRegistration} and its {@link ServletContext}
     */
    public static class FakeServletConfig implements ServletConfig {

        private ServletRegistration servletRegistration;
        private ServletContext servletContext;

        public FakeServletConfig(ServletRegistration servletRegistration,
                ServletContext servletContext) {
            this.servletContext = servletContext;
            this.servletRegistration = servletRegistration;
        }

        @Override
        public String getServletName() {
            return servletRegistration.getName();
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public String getInitParameter(String name) {
            return servletRegistration.getInitParameter(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(servletRegistration
                    .getInitParameters().keySet());
        }

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        if (servletContext.getMajorVersion() < 3) {
            return;
        }

        if (!atmosphereAvailable) {
            return;
        }

        Map<String, ? extends ServletRegistration> regs = servletContext
                .getServletRegistrations();
        for (String servletName : regs.keySet()) {
            ServletRegistration servletRegistration = regs.get(servletName);

            if (isVaadinServlet(servletRegistration)) {
                try {
                    initAtmosphereForVaadinServlet(servletRegistration,
                            servletContext);
                } catch (Exception e) {
                    getLogger().log(
                            Level.WARNING,
                            "Failed to initialize Atmosphere for "
                                    + servletName, e);
                }
            }
        }
    }

    /**
     * Initializes Atmosphere for use with the given Vaadin servlet
     * <p>
     * For JSR 356 websockets to work properly, the initialization must be done
     * in the servlet context initialization phase.
     * 
     * @param servletRegistration
     *            The servlet registration info for the servlet
     * @param servletContext
     */
    public static void initAtmosphereForVaadinServlet(
            ServletRegistration servletRegistration,
            ServletContext servletContext) {
        String servletName = servletRegistration.getName();
        String attributeName = getAttributeName(servletName);

        if (servletContext.getAttribute(attributeName) != null) {
            // Already initialized
            getLogger().warning("Atmosphere already initialized");
            return;
        }
        getLogger().finer("Creating AtmosphereFramework for " + servletName);
        AtmosphereFramework framework = PushRequestHandler
                .initAtmosphere(new FakeServletConfig(servletRegistration,
                        servletContext));
        servletContext.setAttribute(attributeName, framework);
        getLogger().finer("Created AtmosphereFramework for " + servletName);

    }

    /**
     * Returns the name of the attribute in the servlet context where the
     * pre-initialized Atmosphere object is stored
     * 
     * @param servletName
     *            The name of the servlet
     * @return The attribute name which contains the initialized Atmosphere
     *         object
     */
    public static String getAttributeName(String servletName) {
        return JSR356WebsocketInitializer.class.getName() + "." + servletName;
    }

    /**
     * Tries to determine if the given servlet registration refers to a Vaadin
     * servlet.
     * 
     * @param servletRegistration
     *            The servlet registration info for the servlet
     * @return false if the servlet is definitely not a Vaadin servlet, true
     *         otherwise
     */
    protected boolean isVaadinServlet(ServletRegistration servletRegistration) {
        try {
            String servletClassName = servletRegistration.getClassName();
            if (servletClassName.equals("com.ibm.ws.wsoc.WsocServlet")) {
                // Websphere servlet which implements websocket endpoints,
                // dynamically added
                return false;
            }
            Class<?> servletClass = Class.forName(servletClassName);
            return VaadinServlet.class.isAssignableFrom(servletClass);
        } catch (Exception e) {
            // This will fail in OSGi environments, assume everything is a
            // VaadinServlet
            return true;
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(JSR356WebsocketInitializer.class.getName());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do here
    }

}
