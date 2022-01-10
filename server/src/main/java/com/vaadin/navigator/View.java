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

import java.io.Serializable;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

/**
 * Interface for all views controlled by the navigator.
 *
 * Each view added to the navigator must implement this interface. Typically, a
 * view is a {@link Component}, if it is not then you should override
 * {@link #getViewComponent()} to define the component to show for the view.
 *
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface View extends Serializable {

    /**
     * Called before the view is shown on screen.
     * <p>
     * The event object contains information about parameters used when showing
     * the view, in addition to references to the old view and the new view.
     * <p>
     * Override this method to perform initialization of your view.
     * <p>
     * By default does nothing.
     *
     * @param event
     *            an event object containing information about the parameters
     *            given by the user and references to the old view (if any)
     */
    public default void enter(ViewChangeEvent event) {
    }

    /**
     * Called when the user is requesting navigation away from the view.
     * <p>
     * This method allows the view to accept or prevent navigation away from the
     * view or optionally delay navigation away until a later stage. For
     * navigation to take place, the {@link ViewBeforeLeaveEvent#navigate()}
     * method must be called either directly when handling this event or later
     * to perform delayed navigation.
     * <p>
     * The default implementation calls {@link ViewBeforeLeaveEvent#navigate()}
     * directly. If you override this and do nothing, the user will never be
     * able to leave the view.
     * <p>
     * This method is triggered before any methods in any added
     * {@link ViewChangeListener ViewChangeListeners}. Whenever you call
     * {@link ViewBeforeLeaveEvent#navigate()}, any {@link ViewChangeListener}s
     * will be triggered. They will be handled normally and might also prevent
     * navigation.
     *
     * @since 8.1
     * @param event
     *            an event object providing information about the event and
     *            containing the {@link ViewBeforeLeaveEvent#navigate()} method
     *            needed to perform navigation
     */
    public default void beforeLeave(ViewBeforeLeaveEvent event) {
        event.navigate();
    }

    /**
     * Gets the component to show when navigating to the view.
     *
     * By default casts this View to a {@link Component} if possible, otherwise
     * throws an IllegalStateException.
     *
     * @since 8.1
     * @return the component to show, by default the view instance itself
     */
    public default Component getViewComponent() {
        if (!(this instanceof Component)) {
            throw new IllegalStateException(
                    "View is not a Component. Override getViewComponent() to return the root view component");
        }
        return (Component) this;
    }
}
