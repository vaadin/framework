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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;

/**
 * Event fired to {@link VaadinServiceInitListener} when a {@link VaadinService}
 * is being initialized.
 * <p>
 * This event can also be used to add {@link RequestHandler}s that will be used
 * by the {@code VaadinService} for handling all requests.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class ServiceInitEvent extends EventObject {

    private List<RequestHandler> addedRequestHandlers = new ArrayList<>();
    private List<DependencyFilter> addedDependencyFilters = new ArrayList<>();

    /**
     * Creates a new service init event for a given {@link VaadinService} and
     * the {@link RequestHandler} that will be used by the service.
     *
     * @param service
     *            the Vaadin service of this request
     */
    public ServiceInitEvent(VaadinService service) {
        super(service);
    }

    /**
     * Adds a new request handler that will be used by this service. The added
     * handler will be run before any of the framework's own request handlers,
     * but the ordering relative to other custom handlers is not guaranteed.
     *
     * @param requestHandler
     *            the request handler to add, not <code>null</code>
     */
    public void addRequestHandler(RequestHandler requestHandler) {
        Objects.requireNonNull(requestHandler,
                "Request handler cannot be null");

        addedRequestHandlers.add(requestHandler);
    }

    /**
     * Gets an unmodifiable list of all custom request handlers that have been
     * added for the service.
     *
     * @return the current list of added request handlers
     */
    public List<RequestHandler> getAddedRequestHandlers() {
        return Collections.unmodifiableList(addedRequestHandlers);
    }

    /**
     * Adds a new dependency filter that will be used by this service.
     *
     * @param dependencyFilter
     *            the dependency filter to add, not <code>null</code>
     *
     * @since 8.1
     */
    public void addDependencyFilter(DependencyFilter dependencyFilter) {
        Objects.requireNonNull(dependencyFilter,
                "Dependency filter cannot be null");

        addedDependencyFilters.add(dependencyFilter);
    }

    /**
     * Gets an unmodifiable list of all dependency filters that have been added
     * for the service.
     *
     * @return the current list of added dependency filters.
     *
     * @since 8.1
     */
    public List<DependencyFilter> getAddedDependencyFilters() {
        return Collections.unmodifiableList(addedDependencyFilters);
    }

    @Override
    public VaadinService getSource() {
        return (VaadinService) super.getSource();
    }

}
