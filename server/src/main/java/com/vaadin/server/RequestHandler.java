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

import java.io.IOException;
import java.io.Serializable;

import com.vaadin.ui.UI;

/**
 * Handler for producing a response to HTTP requests. Handlers can be either
 * added on a {@link VaadinService service} level, common for all users, or on a
 * {@link VaadinSession session} level for only a single user.
 */
public interface RequestHandler extends Serializable {

    /**
     * Called when a request needs to be handled. If a response is written, this
     * method should return <code>true</code> to indicate that no more request
     * handlers should be invoked for the request.
     * <p>
     * Note that request handlers by default do not lock the session. If you are
     * using VaadinSession or anything inside the VaadinSession you must ensure
     * the session is locked. This can be done by extending
     * {@link SynchronizedRequestHandler} or by using
     * {@link VaadinSession#accessSynchronously(Runnable)} or
     * {@link UI#accessSynchronously(Runnable)}.
     * </p>
     * 
     * @param session
     *            The session for the request
     * @param request
     *            The request to handle
     * @param response
     *            The response object to which a response can be written.
     * @return true if a response has been written and no further request
     *         handlers should be called, otherwise false
     * @throws IOException
     *             If an IO error occurred
     */
    boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException;

}
