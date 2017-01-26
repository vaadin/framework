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
package com.vaadin.shared.ui.nativeselect;

import com.vaadin.shared.ui.AbstractSingleSelectState;

/**
 * Shared state for {@code NativeSelect}.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
public class NativeSelectState extends AbstractSingleSelectState {

    /**
     * The default primary style name for {@code NativeSelect}.
     */
    public static final String STYLE_NAME = "v-select";

    /**
     * True to allow selecting nothing (a special empty selection item is shown
     * at the beginning of the list), false not to allow empty selection by the
     * user.
     */
    public boolean emptySelectionAllowed = true;

    /**
     * Caption for item which represents empty selection.
     */
    public String emptySelectionCaption = "";

    {
        primaryStyleName = STYLE_NAME;
    }
}
