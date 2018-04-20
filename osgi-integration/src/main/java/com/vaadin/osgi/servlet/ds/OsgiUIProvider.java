/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.osgi.servlet.ds;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * {@link UIProvider} used to track the {@link UI UIs} registered as OSGi
 * Services and create those using the appropriate {@link ServiceObjects} to
 * obtain service instances. This enables Declarative Service Injection to be
 * used in {@link UI} instances.
 * 
 * @author Vaadin Ltd.
 *
 * @since 8.5
 */
public class OsgiUIProvider extends DefaultUIProvider {
    private Map<Class<? extends UI>, ServiceObjects<UI>> registeredUIs = Collections
            .synchronizedMap(new LinkedHashMap<>());
    private Map<String, Class<? extends UI>> classNameToClassMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private Optional<LogService> logService = Optional.empty();

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        VaadinRequest request = event.getRequest();
        String uiClassName = request.getService().getDeploymentConfiguration().getUIClassName();
        if (uiClassName != null) {
            return classNameToClassMap.get(uiClassName);
        }

        return null;
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        ServiceObjects<UI> serviceObjects = registeredUIs.get(event.getUIClass());
        if (serviceObjects != null) {
            ServiceReference<UI> reference = serviceObjects.getServiceReference();
            Object property = reference.getProperty(Constants.SERVICE_SCOPE);
            if (Constants.SCOPE_PROTOTYPE.equals(property)) {
                UI service = serviceObjects.getService();
                service.addDetachListener(e -> serviceObjects.ungetService(service));
                return service;
            } else {
                logService.ifPresent(log -> log.log(LogService.LOG_WARNING,
                        "UI services should have a prototype scope! Creating UI instance using the default constructor!"));
                return super.createInstance(event);
            }
        }
        return null;
    }

    /**
     * Bind {@link UI} to this provider.
     * 
     * @param ui
     *            the {@link UI} instance to bind
     * @param objects
     *            the {@link ServiceObjects} to use for getting an instance of the
     *            service
     */
    public void bindUI(UI ui, ServiceObjects<UI> objects) {
        synchronized (this) {
            classNameToClassMap.put(ui.getClass().getName(), ui.getClass());
            registeredUIs.put(ui.getClass(), objects);
        }
    }

    /**
     * Unbind {@link UI} from this provider.
     * 
     * @param ui
     *            the previously bound {@link UI}
     */
    public void unbindUI(UI ui) {
        synchronized (this) {
            classNameToClassMap.remove(ui.getClass().getName());
            registeredUIs.remove(ui.getClass());
        }
    }

    /**
     * @param logService
     *            the {@link LogService} to use
     */
    public void setLogService(LogService logService) {
        this.logService = Optional.ofNullable(logService);
    }
}
