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

import javax.portlet.PortletResponse;
import javax.portlet.StateAwareResponse;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinPortletResponse;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

/**
 * Handler which ensures that Action and Event requests are marked as handled
 * and do not cause a 404 to be sent.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class PortletStateAwareRequestHandler implements RequestHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.RequestHandler#handleRequest(com.vaadin.server.
     * VaadinSession, com.vaadin.server.VaadinRequest,
     * com.vaadin.server.VaadinResponse)
     */
    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (!(response instanceof VaadinPortletResponse)) {
            return false;
        }
        PortletResponse portletResponse = ((VaadinPortletResponse) response)
                .getPortletResponse();
        if (portletResponse instanceof StateAwareResponse) {
            // StateAwareResponse is fully handled by listeners through
            // PortletListenerNotifier
            return true;
        }
        return false;
    }

}
