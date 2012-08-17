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
package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;

import com.google.gwt.event.shared.EventHandler;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;

public class StateChangeEvent extends
        AbstractServerConnectorEvent<StateChangeHandler> {
    /**
     * Type of this event, used by the event bus.
     */
    public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();

    @Override
    public Type<StateChangeHandler> getAssociatedType() {
        return TYPE;
    }

    public StateChangeEvent() {
    }

    @Override
    public void dispatch(StateChangeHandler listener) {
        listener.onStateChanged(this);
    }

    public interface StateChangeHandler extends Serializable, EventHandler {
        public void onStateChanged(StateChangeEvent stateChangeEvent);
    }
}
