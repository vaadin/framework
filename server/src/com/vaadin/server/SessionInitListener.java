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

/**
 * Event listener that can be registered to a {@link VaadinService} to get an
 * event when a new Vaadin service session is initialized for that service.
 * <p>
 * Because of the way different service instances share the same session, the
 * listener is not necessarily notified immediately when the session is created
 * but only when the first request for that session is handled by a specific
 * service.
 * 
 * @see VaadinService#addSessionInitListener(SessionInitListener)
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface SessionInitListener extends Serializable {
    /**
     * Invoked when a new Vaadin service session is initialized for that
     * service.
     * <p>
     * Because of the way different service instances share the same session,
     * the listener is not necessarily notified immediately when the session is
     * created but only when the first request for that session is handled by a
     * specific service.
     * 
     * @param event
     *            the initialization event
     * @throws ServiceException
     *             a problem occurs when processing the event
     */
    public void sessionInit(SessionInitEvent event) throws ServiceException;
}
