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
package com.vaadin.event.handler;

import java.io.Serializable;

/**
 * Generic interface for event handlers. Event handlers are removed using a
 * {@link Registration} object.
 *
 * @since
 * @see Event
 * @see Registration
 * @param <T>
 *            event pay load type
 * 
 */
public interface Handler<T> extends Serializable {

    /**
     * 
     * 
     * @param event
     */
    public void handleEvent(Event<T> event);
}
