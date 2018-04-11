/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.shared.ui.grid.selection;

import com.vaadin.shared.communication.SharedState;

/**
 * SharedState object for SingleSelectionModel.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class SingleSelectionModelState extends SharedState {

    /* Allow deselecting rows */
    public boolean deselectAllowed = true;
    public boolean userSelectionAllowed = true;
}
