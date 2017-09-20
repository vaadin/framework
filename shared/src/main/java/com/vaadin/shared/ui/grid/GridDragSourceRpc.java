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
package com.vaadin.shared.ui.grid;

import java.util.List;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.dnd.DropEffect;

/**
 * RPC for firing server side drag start and drag end events when the
 * corresponding client side events happen on the drag source Grid.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
public interface GridDragSourceRpc extends ServerRpc {

    /**
     * Called when dragstart event happens on client side.
     *
     * @param draggedItemKeys
     *            Keys of the items in Grid being dragged.
     */
    public void dragStart(List<String> draggedItemKeys);

    /**
     * Called when dragend event happens on client side.
     *
     * @param dropEffect
     *            Drop effect of the dragend event, extracted from {@code
     *         DataTransfer.dropEffect} parameter.
     * @param draggedItemKeys
     *            Keys of the items in Grid having been dragged.
     */
    public void dragEnd(DropEffect dropEffect, List<String> draggedItemKeys);
}
