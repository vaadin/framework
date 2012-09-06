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

import java.util.EventObject;

import com.vaadin.ui.UI;

/**
 * Base class providing common functionality used in different bootstrap
 * modification events.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public abstract class BootstrapResponse extends EventObject {
    private final WrappedRequest request;
    private final VaadinSession session;
    private final Class<? extends UI> uiClass;

    /**
     * Creates a new bootstrap event.
     * 
     * @param handler
     *            the bootstrap handler that is firing the event
     * @param request
     *            the wrapped request for which the bootstrap page should be
     *            generated
     * @param session
     *            the session for which the bootstrap page should be generated
     * @param uiClass
     *            the class of the UI that will be displayed on the page
     */
    public BootstrapResponse(BootstrapHandler handler, WrappedRequest request,
            VaadinSession session, Class<? extends UI> uiClass) {
        super(handler);
        this.request = request;
        this.session = session;
        this.uiClass = uiClass;
    }

    /**
     * Gets the bootstrap handler that fired this event
     * 
     * @return the bootstrap handler that fired this event
     */
    public BootstrapHandler getBootstrapHandler() {
        return (BootstrapHandler) getSource();
    }

    /**
     * Gets the request for which the generated bootstrap HTML will be the
     * response. This can be used to read request headers and other additional
     * information. Please note that {@link WrappedRequest#getBrowserDetails()}
     * will not be available because the bootstrap page is generated before the
     * bootstrap javascript has had a chance to send any information back to the
     * server.
     * 
     * @return the wrapped request that is being handled
     */
    public WrappedRequest getRequest() {
        return request;
    }

    /**
     * Gets the vaadin session to which the rendered view belongs.
     * 
     * @return the vaadin session
     */
    public VaadinSession getVaadinSession() {
        return session;
    }

    /**
     * Gets the class of the UI that will be displayed on the generated
     * bootstrap page.
     * 
     * @return the class of the UI
     */
    public Class<? extends UI> getUiClass() {
        return uiClass;
    }

}
