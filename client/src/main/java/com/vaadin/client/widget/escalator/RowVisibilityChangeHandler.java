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

package com.vaadin.client.widget.escalator;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler that gets notified when the range of visible rows changes e.g.
 * because of scrolling.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface RowVisibilityChangeHandler extends EventHandler {

    /**
     * Called when the range of visible rows changes e.g. because of scrolling.
     * 
     * @param event
     *            the row visibility change event describing the change
     */
    void onRowVisibilityChange(RowVisibilityChangeEvent event);

}
