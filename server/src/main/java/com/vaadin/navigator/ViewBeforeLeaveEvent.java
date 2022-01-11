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
package com.vaadin.navigator;

import java.util.EventObject;

/**
 * Event sent to the View instance before navigating away from it.
 * <p>
 * Provides a {@link #navigate()} method which must be called for the navigation
 * to take place.
 *
 * @since 8.1
 */
public class ViewBeforeLeaveEvent extends EventObject {

    private ViewLeaveAction action;
    private boolean navigateRun = false;

    /**
     * Creates a new event instance for the given navigator.
     *
     * @param navigator
     *            the navigator instance
     * @param action
     *            the command to execute when calling {@link #navigate()}
     */
    public ViewBeforeLeaveEvent(Navigator navigator, ViewLeaveAction action) {
        super(navigator);
        this.action = action;
    }

    /**
     * Performs the navigation which triggered the event in the first place.
     */
    public void navigate() {
        if (navigateRun) {
            throw new IllegalStateException(
                    "navigate() can only be called once");
        }
        action.run();
        navigateRun = true;
    }

    /**
     * Checks if the navigate command has been executed.
     *
     * @return <code>true</code> if {@link #navigate()} has been called,
     *         <code>false</code> otherwise
     */
    protected boolean isNavigateRun() {
        return navigateRun;
    }
}
