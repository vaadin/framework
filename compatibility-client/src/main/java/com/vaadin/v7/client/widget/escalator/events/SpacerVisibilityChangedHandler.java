/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.client.widget.escalator.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for a spacer visibility changed event.
 *
 * @author Vaadin Ltd
 * @since 7.7.13
 */
public interface SpacerVisibilityChangedHandler extends EventHandler {

    /**
     * Called when a spacer visibility changed event is fired, when a spacer's
     * visibility changes.
     *
     * @param event
     *            the spacer visibility changed event
     */
    public void onSpacerVisibilityChanged(SpacerVisibilityChangedEvent event);
}
