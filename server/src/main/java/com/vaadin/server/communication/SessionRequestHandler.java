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
package com.vaadin.server.communication;

import java.io.IOException;
import java.util.ArrayList;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

/**
 * Handles a request by passing it to each registered {@link RequestHandler} in
 * the session in turn until one produces a response. This method is used for
 * requests that have not been handled by any specific functionality in the
 * servlet/portlet.
 * <p>
 * The request handlers are invoked in the reverse order in which they were
 * added to the session until a response has been produced. This means that the
 * most recently added handler is used first and the first request handler that
 * was added to the session is invoked towards the end unless any previous
 * handler has already produced a response.
 * </p>
 * <p>
 * The session is not locked during execution of the request handlers. The
 * request handler can itself decide if it needs to lock the session or not.
 * </p>
 * 
 * @see VaadinSession#addRequestHandler(RequestHandler)
 * @see RequestHandler
 * 
 * @since 7.1
 */
public class SessionRequestHandler implements RequestHandler {

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        // Use a copy to avoid ConcurrentModificationException
        session.lock();
        ArrayList<RequestHandler> requestHandlers;
        try {
            requestHandlers = new ArrayList<RequestHandler>(
                    session.getRequestHandlers());
        } finally {
            session.unlock();
        }
        for (RequestHandler handler : requestHandlers) {
            if (handler.handleRequest(session, request, response)) {
                return true;
            }
        }
        // If not handled
        return false;
    }
}
