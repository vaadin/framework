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

package com.vaadin.terminal.gwt.server;

import java.util.EventObject;

import com.vaadin.Application;
import com.vaadin.UIRequiresMoreInformationException;
import com.vaadin.terminal.WrappedRequest;
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
    private final Application application;
    private final Integer rootId;

    /**
     * Creates a new bootstrap event.
     * 
     * @param handler
     *            the bootstrap handler that is firing the event
     * @param request
     *            the wrapped request for which the bootstrap page should be
     *            generated
     * @param application
     *            the application for which the bootstrap page should be
     *            generated
     * @param rootId
     *            the generated id of the UI that will be displayed on the
     *            page
     */
    public BootstrapResponse(BootstrapHandler handler, WrappedRequest request,
            Application application, Integer rootId) {
        super(handler);
        this.request = request;
        this.application = application;
        this.rootId = rootId;
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
     * Gets the application to which the rendered view belongs.
     * 
     * @return the application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Gets the root id that has been generated for this response. Please note
     * that if {@link Application#isRootPreserved()} is enabled, a previously
     * created UI with a different id might eventually end up being used.
     * 
     * @return the root id
     */
    public Integer getRootId() {
        return rootId;
    }

    /**
     * Gets the UI for which this page is being rendered, if available. Some
     * features of the framework will postpone the UI selection until after
     * the bootstrap page has been rendered and required information from the
     * browser has been sent back. This method will return <code>null</code> if
     * no UI instance is yet available.
     * 
     * @see Application#isRootPreserved()
     * @see Application#getRoot(WrappedRequest)
     * @see UIRequiresMoreInformationException
     * 
     * @return The UI that will be displayed in the page being generated, or
     *         <code>null</code> if all required information is not yet
     *         available.
     */
    public UI getRoot() {
        return UI.getCurrent();
    }
}
