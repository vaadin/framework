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
package com.vaadin.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link PointerMoveEvent} events.
 * 
 * @since 7.2
 */
public interface PointerMoveHandler extends EventHandler {

    /**
     * Called when PointerMoveEvent is fired.
     * 
     * @param event
     *            the {@link PointerMoveEvent} that was fired
     */
    void onPointerMove(PointerMoveEvent event);
}
