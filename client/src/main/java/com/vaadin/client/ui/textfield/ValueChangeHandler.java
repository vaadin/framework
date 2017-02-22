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
package com.vaadin.client.ui.textfield;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ComponentConnector;
import com.vaadin.shared.ui.ValueChangeMode;

/**
 * Helper for dealing with scheduling value change events based on a given mode
 * and possibly timeout.
 * 
 * @since 8.0
 */
public class ValueChangeHandler {

    /**
     * Must be implemented by any user of a ValueChangeHandler.
     */
    public interface Owner extends ComponentConnector {
        /**
         * Sends the current value to the server, if it has changed.
         */
        void sendValueChange();
    }

    private Owner owner;

    private boolean scheduled;

    private Timer valueChangeTrigger = new Timer() {
        @Override
        public void run() {
            Scheduler.get().scheduleDeferred(() -> {
                owner.sendValueChange();
                scheduled = false;
            });
        }
    };

    private int valueChangeTimeout = -1;

    private ValueChangeMode valueChangeMode;

    /**
     * Creates a value change handler for the given owner.
     *
     * @param owner
     *            the owner connector
     */
    public ValueChangeHandler(Owner owner) {
        this.owner = owner;
    }

    /**
     * Called whenever a change in the value has been detected. Schedules a
     * value change to be sent to the server, depending on the current value
     * change mode.
     * <p>
     * Note that this method does not consider the {@link ValueChangeMode#BLUR}
     * mode but assumes that {@link #sendValueChange()} is called directly for
     * this mode.
     */
    public void scheduleValueChange() {
        switch (valueChangeMode) {
        case LAZY:
            lazyTextChange();
            break;
        case TIMEOUT:
            timeoutTextChange();
            break;
        case EAGER:
            eagerTextChange();
            break;
        case BLUR:
            // Nothing to schedule for this mode
            break;
        default:
            throw new IllegalStateException("Unknown mode: " + valueChangeMode);
        }
    }

    private void lazyTextChange() {
        scheduled = true;
        valueChangeTrigger.schedule(valueChangeTimeout);
    }

    private void timeoutTextChange() {
        if (valueChangeTrigger.isRunning()) {
            return;
        }
        scheduled = true;
        valueChangeTrigger.schedule(valueChangeTimeout);
    }

    private void eagerTextChange() {
        scheduled = true;
        valueChangeTrigger.run();
    }

    /**
     * Sets the value change mode to use.
     *
     * @see ValueChangeMode
     *
     * @param valueChangeMode
     *            the value change mode to use
     */
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.valueChangeMode = valueChangeMode;
    }

    /**
     * Sets the value change timeout to use.
     *
     * @see ValueChangeMode
     *
     * @param valueChangeTimeout
     *            the value change timeout
     */
    public void setValueChangeTimeout(int valueChangeTimeout) {
        this.valueChangeTimeout = valueChangeTimeout;
    }

    /**
     * Checks whether the value change is scheduled for sending.
     *
     * @since 8.0
     *
     * @return {@code true} if value change is scheduled for sending,
     *         {@code false} otherwise
     */
    public boolean isScheduled() {
        return scheduled;
    }
}
