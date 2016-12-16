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
package com.vaadin.server;

import java.io.Serializable;
import java.util.EventListener;
import java.util.ServiceLoader;

/**
 * Listener for {@link VaadinService} initialization events. The listener can
 * add listeners and request handlers the service.
 * <p>
 * Listener instances are by default discovered and instantiated using
 * {@link ServiceLoader}. This means that all implementations must have a
 * zero-argument constructor and the fully qualified name of the implementation
 * class must be listed on a separate line in a
 * META-INF/services/com.vaadin.server.VaadinServiceInitListener file present in
 * the jar file containing the implementation class.
 * <p>
 * Integrations for specific runtime environments, such as OSGi or Spring, might
 * also provide other ways of discovering listeners.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
@FunctionalInterface
public interface VaadinServiceInitListener extends EventListener, Serializable {
    /**
     * Run when a {@link VaadinService} instance is initialized.
     *
     * @param event
     *            the service initialization event
     */
    void serviceInit(ServiceInitEvent event);
}
