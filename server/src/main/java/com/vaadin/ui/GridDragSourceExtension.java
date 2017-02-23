/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.shared.ui.grid.GridDragSourceExtensionState;

/**
 * Adds HTML5 drag and drop functionality to a {@link Grid}'s rows.
 */
public class GridDragSourceExtension extends DragSourceExtension<Grid> {

    /**
     * Extends a Grid and makes it's rows draggable.
     * @param target Grid to be extended.
     */
    public GridDragSourceExtension(Grid target) {
        super(target);
    }

    @Override
    protected GridDragSourceExtensionState getState() {
        return (GridDragSourceExtensionState) super.getState();
    }

    @Override
    protected GridDragSourceExtensionState getState(boolean markAsDirty) {
        return (GridDragSourceExtensionState) super.getState(markAsDirty);
    }
}
