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
package com.vaadin.client.widget.grid.selection;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link SelectionEvent}s.
 * 
 * @author Vaadin Ltd
 * @param <T>
 *            The row data type
 * @since 7.4
 */
public interface SelectionHandler<T> extends EventHandler {

    /**
     * Called when a selection model's selection state is changed.
     * 
     * @param event
     *            a selection event, containing info about rows that have been
     *            added to or removed from the selection.
     */
    public void onSelect(SelectionEvent<T> event);

}
