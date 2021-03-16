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

package com.vaadin.shared.ui.button;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.ui.TabIndexState;

/**
 * Shared state for {@link com.vaadin.ui.Button} and
 * {@link com.vaadin.ui.NativeButton}.
 *
 * @see AbstractComponentState
 *
 * @since 7.0
 */
public class ButtonState extends TabIndexState {
    {
        primaryStyleName = "v-button";
    }
    @NoLayout
    public boolean disableOnClick = false;
    @NoLayout
    public int clickShortcutKeyCode = 0;
    @NoLayout
    public String iconAltText = "";
}
