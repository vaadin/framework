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

package com.vaadin.terminal.gwt.server;

import java.util.EventListener;

import com.vaadin.Application;

/**
 * Listener that gets notified when a new {@link Application} has been started.
 * Add-ons can use this listener to automatically integrate with API tied to the
 * Application API.
 * 
 * @see AddonContext#addApplicationStartedListener(ApplicationStartedListener)
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface ApplicationStartedListener extends EventListener {
    /**
     * Tells the listener that an application has been started (meaning that
     * {@link Application#init()} has been invoked.
     * 
     * @param event
     *            details about the event
     */
    public void applicationStarted(ApplicationStartedEvent event);
}
