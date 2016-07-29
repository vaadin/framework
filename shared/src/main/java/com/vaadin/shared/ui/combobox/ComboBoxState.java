/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.shared.ui.combobox;

import com.vaadin.shared.ui.select.AbstractSelectState;

/**
 * Shared state for the ComboBox component.
 * 
 * @since 7.0
 */
public class ComboBoxState extends AbstractSelectState {
    {
        primaryStyleName = "v-filterselect";
    }

    /**
     * If text input is not allowed, the ComboBox behaves like a pretty
     * NativeSelect - the user can not enter any text and clicking the text
     * field opens the drop down with options.
     * 
     * @since
     */
    public boolean textInputAllowed = true;

    /**
     * A textual prompt that is displayed when the select would otherwise be
     * empty, to prompt the user for input.
     * 
     * @since
     */
    public String inputPrompt = null;

    /**
     * Number of items to show per page or 0 to disable paging.
     */
    public int pageLength = 10;

    /**
     * Current filtering mode (look for match of the user typed string in the
     * beginning of the item caption or anywhere in the item caption).
     */
    public FilteringMode filteringMode = FilteringMode.STARTSWITH;

    /**
     * Suggestion pop-up's width as a CSS string. By using relative units (e.g.
     * "50%") it's possible to set the popup's width relative to the ComboBox
     * itself.
     */
    public String suggestionPopupWidth = null;

}
