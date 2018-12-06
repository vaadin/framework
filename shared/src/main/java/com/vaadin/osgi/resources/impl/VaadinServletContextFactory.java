package com.vaadin.osgi.resources.impl;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;

public class VaadinServletContextFactory
        implements ServiceFactory<ServletContextHelper> {
    @Override
    public ServletContextHelper getService(final Bundle bundle,
            final ServiceRegistration<ServletContextHelper> registration) {
        return new VaadinServletContext(bundle);
    }

    @Override
    public void ungetService(final Bundle bundle,
            final ServiceRegistration<ServletContextHelper> registration,
            final ServletContextHelper service) {
        // nothing to do
    }

    private static class VaadinServletContext extends ServletContextHelper {
        private final Bundle bundle;

        public VaadinServletContext(Bundle bundle) {
            super(bundle);
            this.bundle = bundle;
        }

        // we want to load the resources from the classpath
        @Override
        public URL getResource(String name) {
            if ((name != null) && (bundle != null)) {
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }

                return this.bundle.getResource(name);
            }
            return null;
        }
    }
}
