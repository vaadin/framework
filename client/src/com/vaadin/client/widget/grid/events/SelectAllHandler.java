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
 * Handler for a Grid select all event, called when the Grid needs all rows in
 * data source to be selected.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface SelectAllHandler<T> extends EventHandler {

    /**
     * Called when select all value in SelectionColumn header changes value.
     * 
     * @param event
     *            select all event telling that all rows should be selected
     */
    public void onSelectAll(SelectAllEvent<T> event);

}
