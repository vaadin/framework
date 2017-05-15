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
package com.vaadin.osgi.resources;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.vaadin.osgi.resources.impl.VaadinResourceServiceImpl;

/**
 * {@link BundleActivator} used to provide access to the
 * {@link VaadinResourceService} singleton for publishing themes, widgetsets and
 * other necessary resources.
 * 
 * @author Vaadin Ltd.
 * 
 * @since 8.1
 */
public class OsgiVaadinResources implements BundleActivator {

    /**
     * Thrown if a method is called when the Resource bundle is not active.
     * 
     * @author Vaadin Ltd.
     *
     * @since 8.1
     */
    @SuppressWarnings("serial")
    public static class ResourceBundleInactiveException extends Exception {
        public ResourceBundleInactiveException(String message) {
            super(message);
        }
    }

    private static OsgiVaadinResources instance;

    private VaadinResourceServiceImpl service;
    private Version version;

    /**
     * Returns the {@link VaadinResourceService} instance. Always returns
     * non-null.
     * 
     * @return the {@link VaadinResourceService resource service} to use for
     *         publishing themes, widgetsets and other necessary resources
     * @throws ResourceBundleInactiveException
     *             if the bundle is not active
     */
    public static VaadinResourceService getService()
            throws ResourceBundleInactiveException {
        if (instance == null) {
            throw new ResourceBundleInactiveException(
                    "Vaadin Shared is not active!");
        }
        return instance.service;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        version = context.getBundle().getVersion();
        service = new VaadinResourceServiceImpl();
        service.setBundleVersion(version.toString());
        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        service = null;
        version = null;
    }
}
