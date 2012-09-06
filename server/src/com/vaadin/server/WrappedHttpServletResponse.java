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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.vaadin.server.VaadinServlet.ServletService;

/**
 * Wrapper for {@link HttpServletResponse}.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see WrappedResponse
 * @see WrappedHttpServletRequest
 */
public class WrappedHttpServletResponse extends HttpServletResponseWrapper
        implements WrappedResponse {

    private ServletService vaadinService;

    /**
     * Wraps a http servlet response and an associated deployment configuration
     * 
     * @param response
     *            the http servlet response to wrap
     * @param vaadinService
     *            the associated vaadin service
     */
    public WrappedHttpServletResponse(HttpServletResponse response,
            ServletService vaadinService) {
        super(response);
        this.vaadinService = vaadinService;
    }

    /**
     * Gets the original unwrapped <code>HttpServletResponse</code>
     * 
     * @return the unwrapped response
     */
    public HttpServletResponse getHttpServletResponse() {
        return this;
    }

    @Override
    public void setCacheTime(long milliseconds) {
        doSetCacheTime(this, milliseconds);
    }

    // Implementation shared with WrappedPortletResponse
    static void doSetCacheTime(WrappedResponse response, long milliseconds) {
        if (milliseconds <= 0) {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        } else {
            response.setHeader("Cache-Control", "max-age=" + milliseconds
                    / 1000);
            response.setDateHeader("Expires", System.currentTimeMillis()
                    + milliseconds);
            // Required to apply caching in some Tomcats
            response.setHeader("Pragma", "cache");
        }
    }

    @Override
    public ServletService getVaadinService() {
        return vaadinService;
    }
}