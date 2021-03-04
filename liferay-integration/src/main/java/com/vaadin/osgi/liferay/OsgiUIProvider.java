/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.osgi.liferay;

import java.util.Optional;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * Vaadin {@link com.vaadin.server.UIProvider} that provides a single {@link UI}
 * class provided through the registration of a {@link UI} as an OSGi service.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
@SuppressWarnings("serial")
public class OsgiUIProvider extends UIProvider {
    private Class<UI> uiClass;
    private ServiceObjects<UI> serviceObjects;
    private boolean prototype;
    private Optional<LogService> logService;

    @SuppressWarnings("unchecked")
    public OsgiUIProvider(ServiceObjects<UI> serviceObjects,
            Optional<LogService> logService) {
        super();
        this.serviceObjects = serviceObjects;
        this.logService = logService;

        UI ui = serviceObjects.getService();

        ServiceReference<UI> reference = serviceObjects.getServiceReference();
        Object property = reference.getProperty(Constants.SERVICE_SCOPE);

        prototype = Constants.SCOPE_PROTOTYPE.equals(property);

        uiClass = (Class<UI>) ui.getClass();

        serviceObjects.ungetService(ui);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return uiClass;
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        if (prototype) {
            UI ui = serviceObjects.getService();
            ui.addDetachListener(event2 -> serviceObjects.ungetService(ui));
            return ui;
        }
        logService.ifPresent(log -> log.log(LogService.LOG_WARNING,
                "UI services should have a prototype scope! Creating UI instance using the default constructor!"));
        return super.createInstance(event);
    }

    public String getDefaultPortletName() {
        return uiClass.getName();
    }

    public String getDefaultDisplayName() {
        return uiClass.getSimpleName();
    }

}
