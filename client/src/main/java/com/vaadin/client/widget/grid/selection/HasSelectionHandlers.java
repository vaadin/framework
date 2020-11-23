/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.widget.grid.selection;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Marker interface for widgets that fires selection events.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public interface HasSelectionHandlers<T> {

    /**
     * Register a selection change handler.
     * <p>
     * This handler is called whenever a
     * {@link com.vaadin.ui.components.grid.selection.SelectionModel
     * SelectionModel} detects a change in selection state.
     *
     * @param handler
     *            a {@link SelectionHandler}
     * @return a handler registration object, which can be used to remove the
     *         handler.
     */
    public HandlerRegistration addSelectionHandler(SelectionHandler<T> handler);

}
