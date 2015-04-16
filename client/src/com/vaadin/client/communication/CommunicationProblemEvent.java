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
package com.vaadin.client.communication;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import elemental.json.JsonObject;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class CommunicationProblemEvent {

    private Throwable exception;
    private Request request;
    private Response response;
    private String uri;
    private JsonObject payload;

    /**
     * @param exception
     */
    public CommunicationProblemEvent(Request request, String uri,
            JsonObject payload, Throwable exception) {
        this.request = request;
        this.exception = exception;
        this.uri = uri;
    }

    /**
     * @param request
     * @param statusCode
     */
    public CommunicationProblemEvent(Request request, String uri,
            JsonObject payload, Response response) {
        this.request = request;
        this.response = response;
        this.uri = uri;
    }

    /**
     * @return the exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * @return the request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * @return the response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return the payload
     */
    public JsonObject getPayload() {
        return payload;
    }
}