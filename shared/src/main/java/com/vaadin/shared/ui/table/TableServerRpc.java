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
package com.vaadin.shared.ui.table;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.table.TableConstants.Section;

/**
 * Client-to-server RPC interface for the Table component
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface TableServerRpc extends ServerRpc {

    /**
     * Informs the server that a context click happened inside of Table
     */
    public void contextClick(String rowKey, String colKey, Section section,
            MouseEventDetails details);

}
