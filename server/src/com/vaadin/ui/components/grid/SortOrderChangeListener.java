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
package com.vaadin.ui.components.grid;

import java.io.Serializable;

/**
 * Listener for sort order change events from {@link Grid}.
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface SortOrderChangeListener extends Serializable {
    /**
     * Called when the sort order has changed.
     * 
     * @param event
     *            the sort order change event
     */
    public void sortOrderChange(SortOrderChangeEvent event);
}
