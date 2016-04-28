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

import java.io.Serializable;
import java.util.EventListener;

import javax.portlet.RenderResponse;

/**
 * Event listener notified when the bootstrap HTML is about to be generated and
 * send to the client. The bootstrap HTML is first constructed as an in-memory
 * DOM representation which registered listeners can modify before the final
 * HTML is generated.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface BootstrapListener extends EventListener, Serializable {
    /**
     * Lets this listener make changes to the fragment that makes up the actual
     * Vaadin application. In a typical Servlet deployment, this is the contents
     * of the HTML body tag. In a typical Portlet deployment, this is the HTML
     * that will be returned in a {@link RenderResponse}.
     * 
     * @param response
     *            the bootstrap response that can modified to cause changes in
     *            the generated HTML.
     */
    public void modifyBootstrapFragment(BootstrapFragmentResponse response);

    /**
     * Lets this listener make changes to the overall HTML document that will be
     * used as the initial HTML page in a typical Servlet deployment as well as
     * the HTTP headers in the response serving the initial HTML. In cases where
     * a full HTML document is not generated, this method will not be invoked.
     * <p>
     * If a full page is being generated, this method is invoked after
     * {@link #modifyBootstrapFragment(BootstrapFragmentResponse)} has been
     * invoked for all registered listeners.
     * 
     * @param response
     *            the bootstrap response that can be modified to cause change in
     *            the generate HTML and in the HTTP headers of the response.
     */
    public void modifyBootstrapPage(BootstrapPageResponse response);
}
