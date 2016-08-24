/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.event;

import java.util.function.Consumer;

/**
 * A generic interface for connector event listeners.
 * 
 * @author Vaadin Ltd.
 *
 * @param <EVENT>
 *            the event type
 * 
 * @since 8.0
 */
@FunctionalInterface
public interface EventListener<EVENT extends ConnectorEvent>
        extends Consumer<EVENT>, ConnectorEventListener {

    /**
     * Invoked when this listener receives an event from the event source to
     * which it has been added.
     * <p>
     * <strong>Implementation note:</strong>In addition to customizing the
     * Javadoc, this override is needed in all extending interfaces to make
     * ReflectTools.findMethod work as expected. It uses
     * Class.getDeclaredMethod, but even if it used getMethod instead, the
     * superinterface argument type is ConnectorEvent, not the actual event
     * type, after type erasure.
     *
     * @param event
     *            the received event, not null
     */
    @Override
    public void accept(EVENT event);
}
