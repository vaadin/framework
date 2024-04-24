/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.osgi.servlet;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import com.vaadin.osgi.resources.VaadinResourceService;
import com.vaadin.server.Constants;
import com.vaadin.server.VaadinServlet;

/**
 * This component tracks {@link VaadinServlet} registrations, configures them
 * with the appropriate path to the Vaadin static resources and registers a
 * {@link Servlet} using the HttpWhiteboard specification.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
@Component
public class VaadinServletRegistration {
    private static final String MISSING_ANNOTATION_MESSAGE_FORMAT = "The property '%s' must be set in a '%s' without the '%s' annotation!";
    private static final String URL_PATTERNS_NOT_SET_MESSAGE_FORMAT = "The property '%s' must be set when the 'urlPatterns' attribute is not set!";

    private static final String SERVLET_PATTERN = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;

    private static final String VAADIN_RESOURCES_PARAM = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX
            + Constants.PARAMETER_VAADIN_RESOURCES;

    private final Map<ServiceReference<VaadinServlet>, ServletRegistration> registeredServlets = Collections
            .synchronizedMap(new LinkedHashMap<>());
    private VaadinResourceService vaadinService;
    private LogService logService;

    @Activate
    void activate(BundleContext bundleContext) throws Exception {
        // see if we have registrations already which are not initialized
        for(ServletRegistration registration : registeredServlets.values()) {
            registration.register(vaadinService);
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, service = VaadinServlet.class, policy = ReferencePolicy.DYNAMIC)
    void bindVaadinServlet(VaadinServlet servlet,
            ServiceReference<VaadinServlet> reference) {
        log(LogService.LOG_INFO, "VaadinServlet Registration");

        Hashtable<String, Object> properties = getProperties(reference);

        WebServlet annotation = servlet.getClass()
                .getAnnotation(WebServlet.class);

        if (!validateSettings(annotation, properties)) {
            return;
        }

        if (annotation != null) {
            properties.put(
                    HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED,
                    Boolean.toString(annotation.asyncSupported()));
        }

        ServletRegistration registration = new ServletRegistration(servlet,
                reference, properties);
        registeredServlets.put(reference, registration);
        // try to register with the vaadin service - the service could be null at this point but we handle that in the register method
        registration.register(this.vaadinService);
    }

    private boolean validateSettings(WebServlet annotation,
            Hashtable<String, Object> properties) {
        if (!properties.containsKey(SERVLET_PATTERN)) {
            if (annotation == null) {
                log(LogService.LOG_ERROR,
                        String.format(MISSING_ANNOTATION_MESSAGE_FORMAT,
                                SERVLET_PATTERN,
                                VaadinServlet.class.getSimpleName(),
                                WebServlet.class.getName()));
                return false;
            } else if (annotation.urlPatterns().length == 0) {
                log(LogService.LOG_ERROR, String.format(
                        URL_PATTERNS_NOT_SET_MESSAGE_FORMAT, SERVLET_PATTERN));
                return false;
            }
        }
        return true;
    }

    private void log(int level, String message) {
        if (logService != null) {
            logService.log(level, message);
        }
    }

    void unbindVaadinServlet(ServiceReference<VaadinServlet> reference) {
        ServletRegistration servletRegistration = registeredServlets
                .remove(reference);
        if (servletRegistration != null) {
            servletRegistration.unregister();
        }
    }

    @Reference
    void setVaadinResourceService(VaadinResourceService vaadinService) {
        this.vaadinService = vaadinService;
    }

    void unsetVaadinResourceService(VaadinResourceService vaadinService) {
        if (this.vaadinService == vaadinService) {
            this.vaadinService = null;
        }
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    void setLogService(LogService logService) {
        this.logService = logService;
    }

    void unsetLogService(LogService logService) {
        if (this.logService == logService) {
            this.logService = null;
        }
    }

    private Hashtable<String, Object> getProperties(
            ServiceReference<VaadinServlet> reference) {
        Hashtable<String, Object> properties = new Hashtable<>();
        for (String key : reference.getPropertyKeys()) {
            properties.put(key, reference.getProperty(key));
        }
        return properties;
    }

    private static class ServletRegistration {
        private final VaadinServlet servlet;
        private final ServiceReference<VaadinServlet> servletRef;
        private final Hashtable<String, Object> properties;
        
        private volatile ServiceRegistration<Servlet> registration;

        public ServletRegistration(VaadinServlet servlet,
                ServiceReference<VaadinServlet> servletRef,
                Hashtable<String, Object> properties) {
            this.servlet = Objects.requireNonNull(servlet);
            this.servletRef = Objects.requireNonNull(servletRef);
            this.properties = properties;
        }

        public void register(VaadinResourceService vaadinService) {
            // we are already registered
            if (this.registration != null)
                return;
            // only register if the vaadin service is not null
            if(vaadinService == null)
                return;

            final String resourcePath = String.format("/%s", vaadinService.getResourcePathPrefix());
            this.properties.put(VAADIN_RESOURCES_PARAM, resourcePath);
            // We register the Http Whiteboard servlet using the context of
            // the bundle which registered the Vaadin Servlet, not our own
            BundleContext bundleContext = this.servletRef.getBundle()
                    .getBundleContext();
            this.registration = bundleContext.registerService(Servlet.class,
                    servlet, properties);
        }

        public void unregister() {
            //we are already deregistered
            if (this.registration == null)
                return;
            try {
                this.registration.unregister();
            } catch (IllegalStateException ise) {
                // This service may have already been unregistered
                // automatically by the OSGi framework if the
                // application bundle is being stopped. This is
                // obviously not a problem for us.
            }
            this.registration = null;
        }
    }
}
