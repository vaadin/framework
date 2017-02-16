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
package com.vaadin.shared.ui.combobox;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.ui.AbstractSingleSelectState;

/**
 * Shared state for the ComboBox component.
 *
 * @since 7.0
 */
public class ComboBoxState extends AbstractSingleSelectState {
    {
        // TODO ideally this would be v-combobox, but that would affect a lot of
        // themes
        primaryStyleName = "v-filterselect";
    }

    /**
     * If text input is not allowed, the ComboBox behaves like a pretty
     * NativeSelect - the user can not enter any text and clicking the text
     * field opens the drop down with options.
     *
     * @since 8.0
     */
    @DelegateToWidget
    public boolean textInputAllowed = true;

    /**
     * The prompt to display in an empty field. Null when disabled.
     */
    @DelegateToWidget
    @NoLayout
    public String placeholder = null;

    /**
     * Number of items to show per page or 0 to disable paging.
     */
    @DelegateToWidget
    public int pageLength = 10;

    /**
     * Suggestion pop-up's width as a CSS string. By using relative units (e.g.
     * "50%") it's possible to set the popup's width relative to the ComboBox
     * itself.
     */
    @DelegateToWidget
    public String suggestionPopupWidth = "100%";

    /**
     * True to allow the user to send new items to the server, false to only
     * select among existing items.
     */
    @DelegateToWidget
    public boolean allowNewItems = false;

    /**
     * True to allow selecting nothing (a special empty selection item is shown
     * at the beginning of the list), false not to allow empty selection by the
     * user.
     */
    public boolean emptySelectionAllowed = true;

    /**
     * True to automatically scroll the ComboBox to show the selected item,
     * false not to search for it in the results.
     */
    public boolean scrollToSelectedItem = false;

    /**
     * The caption of the currently selected item or {@code null} if no item is
     * selected.
     */
    public String selectedItemCaption;

    /**
     * Caption for item which represents empty selection.
     */
    public String emptySelectionCaption = "";

    /**
     * Selected item icon uri.
     *
     * @since 8.0
     */
    public String selectedItemIcon;

}
