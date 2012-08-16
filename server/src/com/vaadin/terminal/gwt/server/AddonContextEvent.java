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

import java.util.EventObject;

/**
 * Event used when an {@link AddonContext} is created and destroyed.
 * 
 * @see AddonContextListener
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class AddonContextEvent extends EventObject {

    /**
     * Creates a new event for the given add-on context.
     * 
     * @param source
     *            the add-on context that created the event
     */
    public AddonContextEvent(AddonContext source) {
        super(source);
    }

    /**
     * Gets the add-on context that created this event.
     * 
     * @return the add-on context that created this event.
     */
    public AddonContext getAddonContext() {
        return (AddonContext) getSource();
    }

}
