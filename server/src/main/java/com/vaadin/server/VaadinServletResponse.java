/*
 * Copyright 2000-2022 Vaadin Ltd.
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

/**
 * Wrapper for {@link HttpServletResponse}.
 *
 * @author Vaadin Ltd.
 * @since 7.0
 *
 * @see VaadinResponse
 * @see VaadinServletRequest
 */
public class VaadinServletResponse extends HttpServletResponseWrapper
        implements VaadinResponse {

    private final VaadinServletService vaadinService;

    /**
     * Wraps a http servlet response and an associated vaadin service.
     *
     * @param response
     *            the http servlet response to wrap
     * @param vaadinService
     *            the associated vaadin service
     */
    public VaadinServletResponse(HttpServletResponse response,
            VaadinServletService vaadinService) {
        super(response);
        this.vaadinService = vaadinService;
    }

    /**
     * Gets the original unwrapped <code>HttpServletResponse</code>.
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

    // Implementation shared with VaadinPortletResponse
    static void doSetCacheTime(VaadinResponse response, long milliseconds) {
        if (milliseconds <= 0) {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        } else {
            response.setHeader("Cache-Control",
                    "max-age=" + milliseconds / 1000);
            response.setDateHeader("Expires",
                    System.currentTimeMillis() + milliseconds);
            // Required to apply caching in some Tomcats
            response.setHeader("Pragma", "cache");
        }
    }

    @Override
    public VaadinServletService getService() {
        return vaadinService;
    }

    /**
     * Gets the currently processed Vaadin servlet response. The current
     * response is automatically defined when the request is started. The
     * current response can not be used in e.g. background threads because of
     * the way server implementations reuse response instances.
     *
     * @return the current Vaadin servlet response instance if available,
     *         otherwise <code>null</code>
     * @since 8.1
     */
    public static VaadinServletResponse getCurrent() {
        VaadinResponse currentResponse = VaadinResponse.getCurrent();
        if (currentResponse instanceof VaadinServletResponse) {
            return (VaadinServletResponse) currentResponse;
        } else {
            return null;
        }
    }

}
