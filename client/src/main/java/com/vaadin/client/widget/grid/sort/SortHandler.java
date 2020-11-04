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
package com.vaadin.client.widget.grid.sort;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for a Grid sort event, called when the Grid needs its data source to
 * provide data sorted in a specific manner.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface SortHandler<T> extends EventHandler {

    /**
     * Handle sorting of the Grid. This method is called when a re-sorting of
     * the Grid's data is requested.
     *
     * @param event
     *            the sort event
     */
    public void sort(SortEvent<T> event);

}
