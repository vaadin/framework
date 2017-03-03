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

import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.grid.GridDropTargetExtensionState;

/**
 * Makes Grid an HTML5 drop target. This is the server side counterpart of
 * GridDropTargetExtensionConnector.
 *
 * @author Vaadin Ltd
 * @since
 */
public class GridDropTargetExtension extends DropTargetExtension<Grid> {
    public GridDropTargetExtension(Grid target) {
        super(target);
    }

    @Override
    protected GridDropTargetExtensionState getState() {
        return (GridDropTargetExtensionState) super.getState();
    }

    @Override
    protected GridDropTargetExtensionState getState(boolean markAsDirty) {
        return (GridDropTargetExtensionState) super.getState(markAsDirty);
    }
}
