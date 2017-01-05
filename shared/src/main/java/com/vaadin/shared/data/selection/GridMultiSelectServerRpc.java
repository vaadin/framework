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
package com.vaadin.shared.data.selection;

/**
 * Transmits selection events for grid multiselection model to server side.
 *
 * @since 8.0
 * @author Vaadin Ltd.
 *
 */
public interface GridMultiSelectServerRpc extends SelectionServerRpc {
    /**
     * All rows in grid have been selected with the select all checkbox in
     * header.
     */
    public void selectAll();

    /**
     * All rows in grid have been deselected with the select all checkbox in
     * header.
     * <p>
     * This can happen only if the all the rows were previously selected.
     */
    public void deselectAll();
}
