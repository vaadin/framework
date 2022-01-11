/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
 * <p>
 * Event handler that gets notified when the size of the Escalator changes.
 *
 * @author Vaadin Ltd
 */
public interface EscalatorSizeChangeHandler extends EventHandler {

    /**
     * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
     * <p>
     * Called when the size of the Escalator changes.
     *
     * @param event
     *            the row visibility change event describing the change
     */
    void onEscalatorSizeChange(EscalatorSizeChangeEvent event);

    /**
     * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
     * <p>
     * Event fired when the Escalator size changes.
     *
     * @author Vaadin Ltd
     */
    public class EscalatorSizeChangeEvent
            extends GwtEvent<EscalatorSizeChangeHandler> {
        /**
         * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
         * <p>
         * The type of this event.
         */
        public static final Type<EscalatorSizeChangeHandler> TYPE = new Type<>();

        /**
         * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
         * <p>
         * Creates a new Escalator size change event.
         *
         */
        public EscalatorSizeChangeEvent() {
            // NOP
        }

        /*
         * (non-Javadoc)
         *
         * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
         */
        @Override
        public Type<EscalatorSizeChangeHandler> getAssociatedType() {
            return TYPE;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.
         * shared .EventHandler)
         */
        @Override
        protected void dispatch(EscalatorSizeChangeHandler handler) {
            handler.onEscalatorSizeChange(this);
        }
    }
}