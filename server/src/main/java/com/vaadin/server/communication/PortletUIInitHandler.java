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

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinRequest;

public class PortletUIInitHandler extends UIInitHandler {

    @Override
    protected boolean isInitRequest(VaadinRequest request) {
        return isUIInitRequest(request);
    }

    public static boolean isUIInitRequest(VaadinRequest request) {
        ResourceRequest resourceRequest = getResourceRequest(request);
        if (resourceRequest == null) {
            return false;
        }

        return UIInitHandler.BROWSER_DETAILS_PARAMETER.equals(resourceRequest
                .getResourceID());
    }

    /**
     * Returns the {@link ResourceRequest} for the given request or null if none
     * could be found.
     * 
     * @param request
     *            The original request, must be a {@link VaadinPortletRequest}
     * @return The resource request from the request parameter or null
     */
    static ResourceRequest getResourceRequest(VaadinRequest request) {
        if (!(request instanceof VaadinPortletRequest)) {
            throw new IllegalArgumentException(
                    "Request must a VaadinPortletRequest");
        }
        PortletRequest portletRequest = ((VaadinPortletRequest) request)
                .getPortletRequest();
        if (!(portletRequest instanceof ResourceRequest)) {
            return null;
        }

        return (ResourceRequest) portletRequest;

    }
}
