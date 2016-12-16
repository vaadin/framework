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
package com.vaadin.shared.ui.grid.editor;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ui.grid.AbstractGridExtensionState;
import com.vaadin.shared.ui.grid.GridConstants;

/**
 * State object for Editor in Grid.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class EditorState extends AbstractGridExtensionState {

    {
        // Disable editor by default.
        enabled = false;
    }

    /** Map from Column id to Component connector id. */
    public Map<String, String> columnFields = new HashMap<>();

    /** Buffer mode state. */
    public boolean buffered = true;

    /** The caption for the save button in the editor. */
    public String saveCaption = GridConstants.DEFAULT_SAVE_CAPTION;

    /** The caption for the cancel button in the editor. */
    public String cancelCaption = GridConstants.DEFAULT_CANCEL_CAPTION;

}
