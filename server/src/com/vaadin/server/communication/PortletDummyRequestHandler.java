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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinPortletResponse;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

/**
 * Request handler which provides a dummy HTML response to any resource request
 * with the resource id DUMMY.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PortletDummyRequestHandler implements RequestHandler {

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        if (!isDummyRequest(request)) {
            return false;
        }

        /*
         * This dummy page is used by action responses to redirect to, in order
         * to prevent the boot strap code from being rendered into strange
         * places such as iframes.
         */
        PortletResponse portletResponse = ((VaadinPortletResponse) response)
                .getPortletResponse();
        if (portletResponse instanceof ResourceResponse) {
            ((ResourceResponse) portletResponse).setContentType("text/html");
        }

        final OutputStream out = ((ResourceResponse) response)
                .getPortletOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print("<html><body>dummy page</body></html>");
        outWriter.close();

        return true;
    }

    public static boolean isDummyRequest(VaadinRequest request) {
        ResourceRequest resourceRequest = PortletUIInitHandler
                .getResourceRequest(request);
        if (resourceRequest == null) {
            return false;
        }

        return resourceRequest.getResourceID() != null
                && resourceRequest.getResourceID().equals("DUMMY");
    }

}
