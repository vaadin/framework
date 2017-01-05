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
package com.vaadin.v7.shared.ui.tree;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * Client-to-server RPC interface for the Tree component
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface TreeServerRpc extends ServerRpc {

    /**
     * Informs the server that a context click happened inside of Tree
     */
    public void contextClick(String rowKey, MouseEventDetails details);

}