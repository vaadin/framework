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

import java.util.EventObject;


/**
 * Event used by
 * {@link ApplicationStartedListener#applicationStarted(ApplicationStartedEvent)}
 * .
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class ApplicationStartedEvent extends EventObject {
    private final VaadinSession application;

    /**
     * Creates a new event.
     * 
     * @param context
     *            the add-on context that will fire the event
     * @param application
     *            the application that has been started
     */
    public ApplicationStartedEvent(AddonContext context, VaadinSession application) {
        super(context);
        this.application = application;
    }

    /**
     * Gets the add-on context from which this event originated.
     * 
     * @return the add-on context that fired the
     */
    public AddonContext getContext() {
        return (AddonContext) getSource();
    }

    /**
     * Gets the newly started Application.
     * 
     * @return the newly created application
     */
    public VaadinSession getApplication() {
        return application;
    }

}
