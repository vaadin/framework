/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.shared.communication.ClientRpc;

public interface TestGridClientRpc extends ClientRpc {
    void insertRows(int offset, int amount);

    void removeRows(int offset, int amount);

    void insertColumns(int offset, int amount);

    void removeColumns(int offset, int amount);

    void scrollToRow(int index, String destination, int padding);

    void scrollToColumn(int index, String destination, int padding);

    void setFrozenColumns(int frozenColumns);

    void insertHeaders(int index, int amount);

    void removeHeaders(int index, int amount);

    void insertFooters(int index, int amount);

    void removeFooters(int index, int amount);

    void setColumnWidth(int index, int px);

    void calculateColumnWidths();

    void randomRowHeight();
}
