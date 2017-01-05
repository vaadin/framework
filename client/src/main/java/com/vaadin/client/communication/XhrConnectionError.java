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
package com.vaadin.client.communication;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import elemental.json.JsonObject;

/**
 * XhrConnectionError provides detail about an error which occured during an XHR
 * request to the server
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class XhrConnectionError {

    private Throwable exception;
    private Request request;
    private Response response;
    private JsonObject payload;

    /**
     * Constructs an event from the given request, payload and exception
     *
     * @param request
     *            the request which failed
     * @param payload
     *            the payload which was going to the server
     * @param exception
     *            the exception describing the problem
     */
    public XhrConnectionError(Request request, JsonObject payload,
            Throwable exception) {
        this.request = request;
        this.exception = exception;
        this.payload = payload;
    }

    /**
     * Constructs an event from the given request, response and payload
     *
     * @param request
     *            the request which failed
     * @param payload
     *            the payload which was going to the server
     * @param response
     *            the response for the request
     */
    public XhrConnectionError(Request request, JsonObject payload,
            Response response) {
        this.request = request;
        this.response = response;
        this.payload = payload;
    }

    /**
     * Returns the exception which caused the problem, if available
     *
     * @return the exception which caused the problem, or null if not available
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Returns the request for which the problem occurred
     *
     * @return the request where the problem occurred
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Returns the received response, if available
     *
     * @return the received response, or null if not available
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Returns the payload which was sent to the server
     *
     * @return the payload which was sent, never null
     */
    public JsonObject getPayload() {
        return payload;
    }
}