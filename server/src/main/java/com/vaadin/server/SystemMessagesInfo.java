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
package com.vaadin.server;

import java.io.Serializable;
import java.util.Locale;

public class SystemMessagesInfo implements Serializable {

    private Locale locale;
    private VaadinRequest request;
    private VaadinService service;

    /**
     * The locale of the UI related to the {@link SystemMessages} request.
     * 
     * @return The Locale or null if the locale is not known
     */
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the request currently in progress.
     * 
     * @return The request currently in progress or null if no request is in
     *         progress.
     */
    public VaadinRequest getRequest() {
        return request;
    }

    public void setRequest(VaadinRequest request) {
        this.request = request;
    }

    /**
     * Returns the service this SystemMessages request comes from.
     * 
     * @return The service which triggered this request or null of not triggered
     *         from a service.
     */
    public VaadinService getService() {
        return service;
    }

    public void setService(VaadinService service) {
        this.service = service;
    }

}
