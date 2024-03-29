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
package com.vaadin.client.ui.dd;

import com.vaadin.client.UIDL;
import com.vaadin.shared.ui.dnd.DropTargetRpc;

/**
 *
 * @author Vaadin Ltd
 * @deprecated Replaced in 8.1 {@link DropTargetRpc}
 */
@Deprecated
public interface VDragEventServerCallback {

    /**
     * Handle the server response for drag and drop.
     *
     * @param accepted
     *            {@code true} if the target accepts the transferable
     * @param response
     *            DnD data within the server response
     */
    public void handleResponse(boolean accepted, UIDL response);

}
