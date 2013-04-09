/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * Class representing the loading indicator for Vaadin applications. The loading
 * indicator has four states: "triggered", "initial", "delay" and "wait". When
 * {@link #trigger()} is called the indicator moves to its "triggered" state and
 * then transitions from one state to the next when the timeouts specified using
 * the set*StateDelay methods occur.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class VLoadingIndicator {

    private static final String PRIMARY_STYLE_NAME = "v-loading-indicator";

    private ApplicationConnection connection;

    private int initialStateDelay = 300;
    private int delayStateDelay = 1500;
    private int waitStateDelay = 5000;

    /**
     * Timer with method for checking if it has been cancelled. This class is a
     * workaround for a IE8 problem which causes a timer to be fired even if it
     * has been cancelled.
     * 
     * @author Vaadin Ltd
     * @since 7.1
     */
    private abstract static class LoadingIndicatorTimer extends Timer {
        private boolean cancelled = false;

        @Override
        public void cancel() {
            super.cancel();
            cancelled = true;
        }

        @Override
        public void schedule(int delayMillis) {
            super.schedule(delayMillis);
            cancelled = false;
        }

        @Override
        public void scheduleRepeating(int periodMillis) {
            super.scheduleRepeating(periodMillis);
            cancelled = false;
        }

        /**
         * Checks if this timer has been cancelled.
         * 
         * @return true if the timer has been cancelled, false otherwise
         */
        public boolean isCancelled() {
            return cancelled;
        }
    }

    private Timer initialTimer = new LoadingIndicatorTimer() {
        @Override
        public void run() {
            if (isCancelled()) {
                // IE8 does not properly cancel the timer in all cases.
                return;
            }
            show();
        }
    };
    private Timer delayStateTimer = new LoadingIndicatorTimer() {
        @Override
        public void run() {
            if (isCancelled()) {
                // IE8 does not properly cancel the timer in all cases.
                return;
            }
            getElement().setClassName(PRIMARY_STYLE_NAME + "-delay");
        }
    };
    private Timer waitStateTimer = new LoadingIndicatorTimer() {
        @Override
        public void run() {
            if (isCancelled()) {
                // IE8 does not properly cancel the timer in all cases.
                return;
            }
            getElement().setClassName(PRIMARY_STYLE_NAME + "-wait");
        }
    };

    private Element element;

    /**
     * Returns the delay (in ms) which must pass before the loading indicator
     * moves into the "initial" state and is shown to the user
     * 
     * @return The delay (in ms) until moving into the "initial" state. Counted
     *         from when {@link #trigger()} is called.
     */
    public int getInitialStateDelay() {
        return initialStateDelay;
    }

    /**
     * Sets the delay (in ms) which must pass before the loading indicator moves
     * into the "initial" state and is shown to the user
     * 
     * @param initialStateDelay
     *            The delay (in ms) until moving into the "initial" state.
     *            Counted from when {@link #trigger()} is called.
     */
    public void setInitialStateDelay(int initialStateDelay) {
        this.initialStateDelay = initialStateDelay;
    }

    /**
     * Returns the delay (in ms) which must pass before the loading indicator
     * moves to its "delay" state.
     * 
     * @return The delay (in ms) until the loading indicator moves into its
     *         "delay" state. Counted from when {@link #trigger()} is called.
     */
    public int getDelayStateDelay() {
        return delayStateDelay;
    }

    /**
     * Sets the delay (in ms) which must pass before the loading indicator moves
     * to its "delay" state.
     * 
     * @param delayStateDelay
     *            The delay (in ms) until the loading indicator moves into its
     *            "delay" state. Counted from when {@link #trigger()} is called.
     */
    public void setDelayStateDelay(int delayStateDelay) {
        this.delayStateDelay = delayStateDelay;
    }

    /**
     * Returns the delay (in ms) which must pass before the loading indicator
     * moves to its "wait" state.
     * 
     * @return The delay (in ms) until the loading indicator moves into its
     *         "wait" state. Counted from when {@link #trigger()} is called.
     */
    public int getWaitStateDelay() {
        return waitStateDelay;
    }

    /**
     * Sets the delay (in ms) which must pass before the loading indicator moves
     * to its "wait" state.
     * 
     * @param loadingIndicatorThirdDelay
     *            The delay (in ms) from the event until changing the loading
     *            indicator into its "wait" state. Counted from when
     *            {@link #trigger()} is called.
     */
    public void setWaitStateDelay(int loadingIndicatorThirdDelay) {
        waitStateDelay = loadingIndicatorThirdDelay;
    }

    /**
     * Triggers displaying of this loading indicator. The loading indicator will
     * actually be shown by {@link #show()} when the initial delay (as specified
     * by {@link #getInitialStateDelay()}) has passed.
     * <p>
     * The loading indicator will be hidden if shown when calling this method.
     * </p>
     */
    public void trigger() {
        hide();
        initialTimer.schedule(getInitialStateDelay());
    }

    /**
     * Shows the loading indicator in its standard state and triggers timers for
     * transitioning into the "delay" and "wait" states.
     */
    public void show() {
        // Reset possible style name and display mode
        getElement().setClassName(PRIMARY_STYLE_NAME);
        getElement().getStyle().setDisplay(Display.BLOCK);

        // Schedule the "delay" loading indicator
        int delayStateTimerDelay = getDelayStateDelay()
                - getInitialStateDelay();
        if (delayStateTimerDelay >= 0) {
            delayStateTimer.schedule(delayStateTimerDelay);
        }

        // Schedule the "wait" loading indicator
        int waitStateTimerDelay = getWaitStateDelay() - getInitialStateDelay();
        if (waitStateTimerDelay >= 0) {
            waitStateTimer.schedule(waitStateTimerDelay);
        }
    }

    /**
     * Returns the {@link ApplicationConnection} which uses this loading
     * indicator
     * 
     * @return The ApplicationConnection for this loading indicator
     */
    public ApplicationConnection getConnection() {
        return connection;
    }

    /**
     * Sets the {@link ApplicationConnection} which uses this loading indicator.
     * Only used internally.
     * 
     * @param connection
     *            The ApplicationConnection for this loading indicator
     */
    void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    /**
     * Hides the loading indicator (if visible). Cancels any possibly running
     * timers.
     */
    public void hide() {
        initialTimer.cancel();
        delayStateTimer.cancel();
        waitStateTimer.cancel();

        getElement().getStyle().setDisplay(Display.NONE);
    }

    /**
     * Returns whether or not the loading indicator is showing.
     * 
     * @return true if the loading indicator is visible, false otherwise
     */
    public boolean isVisible() {
        if (getElement().getStyle().getDisplay()
                .equals(Display.NONE.getCssName())) {
            return false;
        }

        return true;
    }

    /**
     * Returns the root element of the loading indicator
     * 
     * @return The loading indicator DOM element
     */
    public Element getElement() {
        if (element == null) {
            element = DOM.createDiv();
            element.getStyle().setPosition(Position.ABSOLUTE);
            getConnection().getUIConnector().getWidget().getElement()
                    .appendChild(element);
        }
        return element;
    }

}
