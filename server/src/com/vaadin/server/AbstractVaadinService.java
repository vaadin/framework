/* 
 * Copyright 2011 Vaadin Ltd.
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

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Abstract implementation of VaadinService that takes care of those parts that
 * are common to both servlets and portlets.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public abstract class AbstractVaadinService implements VaadinService {

    private AddonContext addonContext;
    private final DeploymentConfiguration deploymentConfiguration;

    /**
     * Creates a new vaadin service based on a deployment configuration
     * 
     * @param deploymentConfiguration
     *            the deployment configuration for the service
     */
    public AbstractVaadinService(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
    }

    @Override
    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    @Override
    public ClassLoader getClassLoader() {
        final String classLoaderName = getDeploymentConfiguration()
                .getApplicationOrSystemProperty("ClassLoader", null);
        ClassLoader classLoader;
        if (classLoaderName == null) {
            classLoader = getClass().getClassLoader();
        } else {
            try {
                final Class<?> classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor<?> c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                classLoader = (ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() });
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Could not find specified class loader: "
                                + classLoaderName, e);
            }
        }
        return classLoader;
    }

    @Override
    public Iterator<AddonContextListener> getAddonContextListeners() {
        // Called once for init and then no more, so there's no point in caching
        // the instance
        ServiceLoader<AddonContextListener> contextListenerLoader = ServiceLoader
                .load(AddonContextListener.class, getClassLoader());
        return contextListenerLoader.iterator();
    }

    @Override
    public void setAddonContext(AddonContext addonContext) {
        this.addonContext = addonContext;
    }

    @Override
    public AddonContext getAddonContext() {
        return addonContext;
    }

    @Override
    public VaadinSession getVaadinSession(WrappedRequest request) {
        return (VaadinSession) request.getAttribute(VaadinSession.class
                .getName());
    }

}
