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

import java.util.EventListener;

/**
 * Listener that gets notified then the {@link AddonContext} is initialized,
 * allowing an add-on to add listeners to various parts of the framework. In a
 * default configuration, add-ons can register their listeners by including a
 * file named META-INF/services/com.vaadin.server.AddonContextListener
 * containing the fully qualified class names of classes implementing this
 * interface.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface AddonContextListener extends EventListener {
    /**
     * Notifies the listener that the add-on context has been created and
     * initialized. An add-on can use this method to get access to an
     * {@link AddonContext} object to which listeners can be added.
     * 
     * @param event
     *            the add-on context event
     */
    public void contextCreated(AddonContextEvent event);

    /**
     * Notifies the listener that the add-on context has been closed. An add-on
     * can use this method to e.g. close resources that have been opened in
     * {@link #contextCreated(AddonContextEvent)}.
     * 
     * @param event
     *            the add-on context event
     */
    public void contextDestoryed(AddonContextEvent event);
}
