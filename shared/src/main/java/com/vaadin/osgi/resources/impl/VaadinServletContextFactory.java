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
