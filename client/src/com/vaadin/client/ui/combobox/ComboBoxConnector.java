/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.ui.combobox;

import java.util.Iterator;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VFilterSelect;
import com.vaadin.client.ui.VFilterSelect.FilterSelectSuggestion;
import com.vaadin.client.ui.menubar.MenuItem;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;

@Connect(ComboBox.class)
public class ComboBoxConnector extends AbstractFieldConnector implements
        Paintable, SimpleManagedLayout {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Paintable#updateFromUIDL(com.vaadin.client.UIDL,
     * com.vaadin.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        getWidget().readonly = isReadOnly();
        getWidget().enabled = isEnabled();

        getWidget().tb.setEnabled(getWidget().enabled);
        getWidget().updateReadOnly();

        if (!isRealUpdate(uidl)) {
            return;
        }

        // Inverse logic here to make the default case (text input enabled)
        // work without additional UIDL messages
        boolean noTextInput = uidl
                .hasAttribute(ComboBoxConstants.ATTR_NO_TEXT_INPUT)
                && uidl.getBooleanAttribute(ComboBoxConstants.ATTR_NO_TEXT_INPUT);
        getWidget().setTextInputEnabled(!noTextInput);

        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            getWidget().tb.setTabIndex(uidl.getIntAttribute("tabindex"));
        }

        if (uidl.hasAttribute("filteringmode")) {
            getWidget().filteringmode = FilteringMode.valueOf(uidl
                    .getStringAttribute("filteringmode"));
        }

        getWidget().immediate = getState().immediate;

        getWidget().nullSelectionAllowed = uidl.hasAttribute("nullselect");

        getWidget().nullSelectItem = uidl.hasAttribute("nullselectitem")
                && uidl.getBooleanAttribute("nullselectitem");

        getWidget().currentPage = uidl.getIntVariable("page");

        if (uidl.hasAttribute("pagelength")) {
            getWidget().pageLength = uidl.getIntAttribute("pagelength");
        }

        if (uidl.hasAttribute(ComboBoxConstants.ATTR_INPUTPROMPT)) {
            // input prompt changed from server
            getWidget().inputPrompt = uidl
                    .getStringAttribute(ComboBoxConstants.ATTR_INPUTPROMPT);
        } else {
            getWidget().inputPrompt = "";
        }

        getWidget().suggestionPopup.updateStyleNames(uidl, getState());

        getWidget().allowNewItem = uidl.hasAttribute("allownewitem");
        getWidget().lastNewItemString = null;

        getWidget().currentSuggestions.clear();
        if (!getWidget().waitingForFilteringResponse) {
            /*
             * Clear the current suggestions as the server response always
             * includes the new ones. Exception is when filtering, then we need
             * to retain the value if the user does not select any of the
             * options matching the filter.
             */
            getWidget().currentSuggestion = null;
            /*
             * Also ensure no old items in menu. Unless cleared the old values
             * may cause odd effects on blur events. Suggestions in menu might
             * not necessary exist in select at all anymore.
             */
            getWidget().suggestionPopup.menu.clearItems();

        }

        final UIDL options = uidl.getChildUIDL(0);
        if (uidl.hasAttribute("totalMatches")) {
            getWidget().totalMatches = uidl.getIntAttribute("totalMatches");
        } else {
            getWidget().totalMatches = 0;
        }

        // used only to calculate minimum popup width
        String captions = Util.escapeHTML(getWidget().inputPrompt);

        for (final Iterator<?> i = options.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            final FilterSelectSuggestion suggestion = getWidget().new FilterSelectSuggestion(
                    optionUidl);
            getWidget().currentSuggestions.add(suggestion);
            if (optionUidl.hasAttribute("selected")) {
                if (!getWidget().waitingForFilteringResponse
                        || getWidget().popupOpenerClicked) {
                    String newSelectedOptionKey = Integer.toString(suggestion
                            .getOptionKey());
                    if (!newSelectedOptionKey
                            .equals(getWidget().selectedOptionKey)
                            || suggestion.getReplacementString().equals(
                                    getWidget().tb.getText())) {
                        // Update text field if we've got a new selection
                        // Also update if we've got the same text to retain old
                        // text selection behavior
                        getWidget().setPromptingOff(
                                suggestion.getReplacementString());
                        getWidget().selectedOptionKey = newSelectedOptionKey;
                    }
                }
                getWidget().currentSuggestion = suggestion;
                getWidget().setSelectedItemIcon(suggestion.getIconUri());
            }

            // Collect captions so we can calculate minimum width for textarea
            if (captions.length() > 0) {
                captions += "|";
            }
            captions += Util.escapeHTML(suggestion.getReplacementString());
        }

        if ((!getWidget().waitingForFilteringResponse || getWidget().popupOpenerClicked)
                && uidl.hasVariable("selected")
                && uidl.getStringArrayVariable("selected").length == 0) {
            // select nulled
            if (!getWidget().waitingForFilteringResponse
                    || !getWidget().popupOpenerClicked) {
                if (!getWidget().focused) {
                    /*
                     * client.updateComponent overwrites all styles so we must
                     * ALWAYS set the prompting style at this point, even though
                     * we think it has been set already...
                     */
                    getWidget().prompting = false;
                    getWidget().setPromptingOn();
                } else {
                    // we have focus in field, prompting can't be set on,
                    // instead just clear the input
                    getWidget().tb.setValue("");
                }
            }
            getWidget().setSelectedItemIcon(null);
            getWidget().selectedOptionKey = null;
        }

        if (getWidget().waitingForFilteringResponse
                && getWidget().lastFilter.toLowerCase().equals(
                        uidl.getStringVariable("filter"))) {
            getWidget().suggestionPopup.showSuggestions(
                    getWidget().currentSuggestions, getWidget().currentPage,
                    getWidget().totalMatches);
            getWidget().waitingForFilteringResponse = false;
            if (!getWidget().popupOpenerClicked
                    && getWidget().selectPopupItemWhenResponseIsReceived != VFilterSelect.Select.NONE) {
                // we're paging w/ arrows
                if (getWidget().selectPopupItemWhenResponseIsReceived == VFilterSelect.Select.LAST) {
                    getWidget().suggestionPopup.menu.selectLastItem();
                } else {
                    getWidget().suggestionPopup.menu.selectFirstItem();
                }

                // This is used for paging so we update the keyboard selection
                // variable as well.
                MenuItem activeMenuItem = getWidget().suggestionPopup.menu
                        .getSelectedItem();
                getWidget().suggestionPopup.menu
                        .setKeyboardSelectedItem(activeMenuItem);

                // Update text field to contain the correct text
                getWidget().setTextboxText(activeMenuItem.getText());
                getWidget().tb.setSelectionRange(
                        getWidget().lastFilter.length(),
                        activeMenuItem.getText().length()
                                - getWidget().lastFilter.length());

                getWidget().selectPopupItemWhenResponseIsReceived = VFilterSelect.Select.NONE; // reset
            }
            if (getWidget().updateSelectionWhenReponseIsReceived) {
                getWidget().suggestionPopup.menu
                        .doPostFilterSelectedItemAction();
            }
        }

        // Calculate minumum textarea width
        getWidget().suggestionPopupMinWidth = getWidget().minWidth(captions);

        getWidget().popupOpenerClicked = false;

        if (!getWidget().initDone) {
            getWidget().updateRootWidth();
        }

        // Focus dependent style names are lost during the update, so we add
        // them here back again
        if (getWidget().focused) {
            getWidget().addStyleDependentName("focus");
        }

        getWidget().initDone = true;
    }

    @Override
    public VFilterSelect getWidget() {
        return (VFilterSelect) super.getWidget();
    }

    @Override
    public ComboBoxState getState() {
        return (ComboBoxState) super.getState();
    }

    @Override
    public void layout() {
        VFilterSelect widget = getWidget();
        if (widget.initDone) {
            widget.updateRootWidth();
        }
    }
}
