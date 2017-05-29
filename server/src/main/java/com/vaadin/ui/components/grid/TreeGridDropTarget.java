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
package com.vaadin.ui.components.grid;

import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.grid.TreeGridDropTargetState;
import com.vaadin.ui.TreeGrid;

/**
 * Makes the rows of a TreeGrid HTML5 drop targets. This is the server side
 * counterpart of GridDropTargetExtensionConnector.
 *
 * @param <T>
 *            Type of the TreeGrid bean.
 * @author Vaadin Ltd
 * @since 8.1
 */
public class TreeGridDropTarget<T> extends GridDropTarget<T> {

    /**
     * Extends a TreeGrid and makes it's rows drop targets for HTML5 drag and drop.
     *
     * @param target
     *            TreeGrid to be extended.
     * @param dropMode
     *            Drop mode that describes the allowed drop locations within the
     *            TreeGrid's row.
     */
    public TreeGridDropTarget(TreeGrid<T> target, DropMode dropMode) {
        super(target, dropMode);
    }

    @Override
    protected TreeGridDropTargetState getState() {
        return (TreeGridDropTargetState) super.getState();
    }

    @Override
    protected TreeGridDropTargetState getState(boolean markAsDirty) {
        return (TreeGridDropTargetState) super.getState(markAsDirty);
    }
}
