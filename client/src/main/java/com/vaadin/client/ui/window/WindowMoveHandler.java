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
package com.vaadin.client.ui.window;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link WindowMoveEvent}s
 * 
 * @since 7.1.9
 * @author Vaadin Ltd
 */
public interface WindowMoveHandler extends EventHandler {

    /**
     * Called when the VWindow was moved by the user.
     * 
     * @param event
     *            Contains new coordinates for the VWindow
     */
    public void onWindowMove(WindowMoveEvent event);
}
