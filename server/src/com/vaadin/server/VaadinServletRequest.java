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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;


/**
 * Wrapper for {@link HttpServletRequest}.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see VaadinRequest
 * @see VaadinServletResponse
 */
public class VaadinServletRequest extends HttpServletRequestWrapper implements
        VaadinRequest {

    private final VaadinServletService vaadinService;

    /**
     * Wraps a http servlet request and associates with a vaadin service
     * 
     * @param request
     *            the http servlet request to wrap
     * @param vaadinService
     *            the associated vaadin service
     */
    public VaadinServletRequest(HttpServletRequest request,
            VaadinServletService vaadinService) {
        super(request);
        this.vaadinService = vaadinService;
    }

    @Override
    public String getRequestPathInfo() {
        return getPathInfo();
    }

    @Override
    public WrappedSession getWrappedSession() {
        return getWrappedSession(true);
    }

    @Override
    public WrappedSession getWrappedSession(boolean allowSessionCreation) {
        HttpSession session = getSession(allowSessionCreation);
        if (session != null) {
            return new WrappedHttpSession(session);
        } else {
            return null;
        }
    }

    /**
     * Gets the original, unwrapped HTTP servlet request.
     * 
     * @return the servlet request
     */
    public HttpServletRequest getHttpServletRequest() {
        return this;
    }

    @Override
    public VaadinServletService getVaadinService() {
        return vaadinService;
    }

    @Override
    public BrowserDetails getBrowserDetails() {
        return new BrowserDetails() {
            @Override
            public String getUriFragment() {
                return null;
            }

            @Override
            public String getWindowName() {
                return null;
            }

            @Override
            public WebBrowser getWebBrowser() {
                return VaadinSession.getCurrent().getBrowser();
            }
        };
    }

    /**
     * Helper method to get a {@link VaadinServletRequest} from a
     * {@link VaadinRequest}. Aside from casting, this method also takes care of
     * situations where there's another level of wrapping.
     * 
     * @param request
     *            a Vaadin request
     * @return a Vaadin http servlet request
     * @throws ClassCastException
     *             if the Vaadin request doesn't wrap a http servlet request
     */
    public static VaadinServletRequest cast(VaadinRequest request) {
        if (request instanceof CombinedRequest) {
            CombinedRequest combinedRequest = (CombinedRequest) request;
            request = combinedRequest.getSecondRequest();
        }
        return (VaadinServletRequest) request;
    }

}