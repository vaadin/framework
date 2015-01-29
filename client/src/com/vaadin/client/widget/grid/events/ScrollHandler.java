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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * A handler that gets called whenever a scrollbar bundle is scrolled
 * 
 * @author Vaadin Ltd
 * @since 7.4
 */
public interface ScrollHandler extends EventHandler {
    /**
     * A callback method that is called once a scrollbar bundle has been
     * scrolled.
     * 
     * @param event
     *            the scroll event
     */
    public void onScroll(ScrollEvent event);
}
