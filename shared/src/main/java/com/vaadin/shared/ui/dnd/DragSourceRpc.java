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
package com.vaadin.shared.ui.dnd;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC for firing server side event when client side dragstart event happens on
 * drag source.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public interface DragSourceRpc extends ServerRpc {

    /**
     * Called when dragstart event happens on client side.
     */
    public void dragStart();

    /**
     * Called when dragend event happens on client side.
     *
     * @param dropEffect
     *         Drop effect of the dragend event, extracted from {@code
     *         DataTransfer.dropEffect} parameter.
     */
    public void dragEnd(DropEffect dropEffect);
}
