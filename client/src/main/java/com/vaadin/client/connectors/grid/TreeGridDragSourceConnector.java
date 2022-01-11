/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.connectors.grid;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.treegrid.TreeGridDragSourceState;
import com.vaadin.ui.components.grid.TreeGridDragSource;

/**
 * Adds HTML5 drag and drop functionality to a TreeGrid's rows. This is the
 * client side counterpart of {@link TreeGridDragSource}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(TreeGridDragSource.class)
public class TreeGridDragSourceConnector extends GridDragSourceConnector {

    @Override
    public TreeGridDragSourceState getState() {
        return (TreeGridDragSourceState) super.getState();
    }

}
