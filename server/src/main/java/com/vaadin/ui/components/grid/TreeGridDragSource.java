/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import com.vaadin.shared.ui.treegrid.TreeGridDragSourceState;
import com.vaadin.ui.TreeGrid;

/**
 * Makes a TreeGrid's rows draggable for HTML5 drag and drop functionality.
 * <p>
 * When dragging a selected row, all the visible selected rows are dragged. Note
 * that ONLY visible rows are taken into account and the subtree belonging to a
 * selected row is not dragged either.
 *
 * @param <T>
 *            The TreeGrid bean type.
 * @author Vaadin Ltd.
 * @since 8.1
 */
public class TreeGridDragSource<T> extends GridDragSource<T> {

    /**
     * Extends a TreeGrid and makes it's rows draggable.
     *
     * @param target
     *            TreeGrid to be extended.
     */
    public TreeGridDragSource(TreeGrid<T> target) {
        super(target);
    }

    @Override
    protected TreeGridDragSourceState getState() {
        return (TreeGridDragSourceState) super.getState();
    }

    @Override
    protected TreeGridDragSourceState getState(boolean markAsDirty) {
        return (TreeGridDragSourceState) super.getState(markAsDirty);
    }
}
