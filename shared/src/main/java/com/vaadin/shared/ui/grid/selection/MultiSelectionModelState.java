/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui.grid.selection;

import com.vaadin.shared.communication.SharedState;

/**
 * SharedState object for MultiSelectionModel.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class MultiSelectionModelState extends SharedState {

    /* Select All -checkbox status */
    public boolean allSelected;
    public boolean userSelectionAllowed = true;

}
