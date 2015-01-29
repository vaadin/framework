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
package com.vaadin.client.widget.grid;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link DataAvailableEvent}s.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface DataAvailableHandler extends EventHandler {

    /**
     * Called when DataSource has data available. Supplied with row range.
     * 
     * @param availableRows
     *            Range of rows available in the DataSource
     * @return true if the command was successfully completed, false to call
     *         again the next time new data is available
     */
    public void onDataAvailable(DataAvailableEvent event);
}
