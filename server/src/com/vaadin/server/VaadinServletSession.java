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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Web application context for Vaadin applications.
 * 
 * This is automatically added as a {@link HttpSessionBindingListener} when
 * added to a {@link HttpSession}.
 * 
 * @author Vaadin Ltd.
 * @since 3.1
 * 
 * @deprecated might be refactored or removed before 7.0.0
 */
@Deprecated
@SuppressWarnings("serial")
public class VaadinServletSession extends VaadinServiceSession {

    /**
     * Create a servlet service session for the given servlet service
     * 
     * @param service
     *            the servlet service to which the new session belongs
     */
    public VaadinServletSession(VaadinServletService service) {
        super(service);
    }

    /**
     * Gets the http-session application is running in.
     * 
     * @return HttpSession this application context resides in.
     */
    public HttpSession getHttpSession() {
        WrappedSession session = getSession();
        return ((WrappedHttpSession) session).getHttpSession();
    }

}
