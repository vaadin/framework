/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.ui.components.grid.selection;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.ui.components.grid.Grid;

/**
 * A default implementation for a {@link SelectionModel.None}
 * 
 * @since 7.4.0
 * @author Vaadin Ltd
 */
public class NoSelectionModel implements SelectionModel.None {
    @Override
    public void setGrid(final Grid grid) {
        // NOOP, not needed for anything
    }

    @Override
    public boolean isSelected(final Object itemId) {
        return false;
    }

    @Override
    public Collection<Object> getSelectedRows() {
        return Collections.emptyList();
    }

    /**
     * Semantically resets the selection model.
     * <p>
     * Effectively a no-op.
     */
    @Override
    public void reset() {
        // NOOP
    }
}
