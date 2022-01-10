/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.v7.client.ui.combobox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.AbstractFieldConnector;
import com.vaadin.v7.client.ui.VFilterSelect;
import com.vaadin.v7.client.ui.VFilterSelect.FilterSelectSuggestion;
import com.vaadin.v7.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.v7.shared.ui.combobox.ComboBoxState;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

@Connect(ComboBox.class)
public class ComboBoxConnector extends AbstractFieldConnector
        implements Paintable, SimpleManagedLayout {

    // oldSuggestionTextMatchTheOldSelection is used to detect when it's safe to
    // update textbox text by a changed item caption.
    private boolean oldSuggestionTextMatchTheOldSelection;

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.Paintable#updateFromUIDL(com.vaadin.client.UIDL,
     * com.vaadin.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VFilterSelect widget = getWidget();
        // Save details
        widget.client = client;
        widget.paintableId = uidl.getId();

        widget.readonly = isReadOnly();
        widget.updateReadOnly();

        if (!isRealUpdate(uidl)) {
            return;
        }

        // Inverse logic here to make the default case (text input enabled)
        // work without additional UIDL messages
        boolean noTextInput = uidl
                .hasAttribute(ComboBoxConstants.ATTR_NO_TEXT_INPUT)
                && uidl.getBooleanAttribute(
                        ComboBoxConstants.ATTR_NO_TEXT_INPUT);
        widget.setTextInputEnabled(!noTextInput);

        // not a FocusWidget -> needs own tabindex handling
        widget.tb.setTabIndex(getState().tabIndex);

        if (uidl.hasAttribute("filteringmode")) {
            widget.filteringmode = FilteringMode
                    .valueOf(uidl.getStringAttribute("filteringmode"));
        }

        widget.immediate = getState().immediate;

        widget.nullSelectionAllowed = uidl.hasAttribute("nullselect");

        widget.nullSelectItem = uidl.hasAttribute("nullselectitem")
                && uidl.getBooleanAttribute("nullselectitem");

        widget.currentPage = uidl.getIntVariable("page");

        if (uidl.hasAttribute("pagelength")) {
            widget.pageLength = uidl.getIntAttribute("pagelength");
        }

        if (uidl.hasAttribute(ComboBoxConstants.ATTR_INPUTPROMPT)) {
            // input prompt changed from server
            widget.inputPrompt = uidl
                    .getStringAttribute(ComboBoxConstants.ATTR_INPUTPROMPT);
        } else {
            widget.inputPrompt = "";
        }

        if (uidl.hasAttribute("suggestionPopupWidth")) {
            widget.suggestionPopupWidth = uidl
                    .getStringAttribute("suggestionPopupWidth");
        } else {
            widget.suggestionPopupWidth = null;
        }

        if (uidl.hasAttribute("suggestionPopupWidth")) {
            widget.suggestionPopupWidth = uidl
                    .getStringAttribute("suggestionPopupWidth");
        } else {
            widget.suggestionPopupWidth = null;
        }

        widget.suggestionPopup.updateStyleNames(uidl, getState());

        widget.allowNewItem = uidl.hasAttribute("allownewitem");
        widget.lastNewItemString = null;

        final UIDL options = uidl.getChildUIDL(0);
        if (uidl.hasAttribute("totalMatches")) {
            widget.totalMatches = uidl.getIntAttribute("totalMatches");
        } else {
            widget.totalMatches = 0;
        }

        List<FilterSelectSuggestion> newSuggestions = new ArrayList<FilterSelectSuggestion>();

        for (final Object child : options) {
            final UIDL optionUidl = (UIDL) child;
            final FilterSelectSuggestion suggestion = widget.new FilterSelectSuggestion(
                    optionUidl);
            newSuggestions.add(suggestion);
        }

        // only close the popup if the suggestions list has actually changed
        boolean suggestionsChanged = !widget.initDone
                || !newSuggestions.equals(widget.currentSuggestions);

        // An ItemSetChangeEvent on server side clears the current suggestion
        // popup. Popup needs to be repopulated with suggestions from UIDL.
        boolean popupOpenAndCleared = false;

        oldSuggestionTextMatchTheOldSelection = false;

        if (suggestionsChanged) {
            oldSuggestionTextMatchTheOldSelection = isWidgetsCurrentSelectionTextInTextBox();
            widget.currentSuggestions.clear();

            if (!widget.waitingForFilteringResponse) {
                /*
                 * Clear the current suggestions as the server response always
                 * includes the new ones. Exception is when filtering, then we
                 * need to retain the value if the user does not select any of
                 * the options matching the filter.
                 */
                widget.currentSuggestion = null;
                /*
                 * Also ensure no old items in menu. Unless cleared the old
                 * values may cause odd effects on blur events. Suggestions in
                 * menu might not necessary exist in select at all anymore.
                 */
                widget.suggestionPopup.menu.clearItems();
                popupOpenAndCleared = widget.suggestionPopup.isAttached();

            }

            for (FilterSelectSuggestion suggestion : newSuggestions) {
                widget.currentSuggestions.add(suggestion);
            }
        }

        // handle selection (null or a single value)
        if (uidl.hasVariable("selected")

        // In case we're switching page no need to update the selection as the
        // selection process didn't finish.
        // && widget.selectPopupItemWhenResponseIsReceived ==
        // VFilterSelect.Select.NONE
        //
        ) {

            String[] selectedKeys = uidl.getStringArrayVariable("selected");

            // when filtering with empty filter, server sets the selected key
            // to "", which we don't select here. Otherwise we won't be able to
            // reset back to the item that was selected before filtering
            // started.
            if (selectedKeys.length > 0 && !selectedKeys[0].equals("")) {
                performSelection(selectedKeys[0]);
                // if selected key is available, assume caption is know based on
                // it as well and clear selected caption
                widget.setSelectedCaption(null);

            } else if (!widget.waitingForFilteringResponse
                    && uidl.hasAttribute("selectedCaption")) {
                // scrolling to correct page is disabled, caption is passed as a
                // special parameter
                widget.setSelectedCaption(
                        uidl.getStringAttribute("selectedCaption"));
            } else {
                resetSelection();
            }
        }

        if ((widget.waitingForFilteringResponse
                && widget.lastFilter.toLowerCase(Locale.ROOT)
                        .equals(uidl.getStringVariable("filter")))
                || popupOpenAndCleared) {

            widget.suggestionPopup.showSuggestions(widget.currentSuggestions,
                    widget.currentPage, widget.totalMatches);

            widget.waitingForFilteringResponse = false;

            if (!widget.popupOpenerClicked
                    && widget.selectPopupItemWhenResponseIsReceived != VFilterSelect.Select.NONE) {

                // we're paging w/ arrows
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        navigateItemAfterPageChange();
                    }
                });
            }

            if (widget.updateSelectionWhenReponseIsReceived) {
                widget.suggestionPopup.menu
                        .doPostFilterSelectedItemAction();
            }
        }

        // Calculate minimum textarea width
        widget.updateSuggestionPopupMinWidth();

        widget.popupOpenerClicked = false;

        /*
         * if this is our first time we need to recalculate the root width.
         */
        if (!widget.initDone) {

            widget.updateRootWidth();
        }

        // Focus dependent style names are lost during the update, so we add
        // them here back again
        if (widget.focused) {
            widget.addStyleDependentName("focus");
        }

        widget.initDone = true;
    }

    /*
     * This method navigates to the proper item in the combobox page. This
     * should be executed after setSuggestions() method which is called from
     * vFilterSelect.showSuggestions(). ShowSuggestions() method builds the page
     * content. As far as setSuggestions() method is called as deferred,
     * navigateItemAfterPageChange method should be also be called as deferred.
     * #11333
     */
    private void navigateItemAfterPageChange() {
        if (getWidget().selectPopupItemWhenResponseIsReceived == VFilterSelect.Select.LAST) {
            getWidget().suggestionPopup.selectLastItem();
        } else {
            getWidget().suggestionPopup.selectFirstItem();
        }

        // If you're in between 2 requests both changing the page back and
        // forth, you don't want this here, instead you need it before any
        // other request.
        // getWidget().selectPopupItemWhenResponseIsReceived =
        // VFilterSelect.Select.NONE; // reset
    }

    private void performSelection(String selectedKey) {
        VFilterSelect widget = getWidget();
        // some item selected
        for (FilterSelectSuggestion suggestion : widget.currentSuggestions) {
            String suggestionKey = suggestion.getOptionKey();
            if (!suggestionKey.equals(selectedKey)) {
                continue;
            }
            if (!widget.waitingForFilteringResponse
                    || widget.popupOpenerClicked) {
                if (!suggestionKey.equals(widget.selectedOptionKey)
                        || suggestion.getReplacementString()
                                .equals(widget.tb.getText())
                        || oldSuggestionTextMatchTheOldSelection) {
                    // Update text field if we've got a new
                    // selection
                    // Also update if we've got the same text to
                    // retain old text selection behavior
                    // OR if selected item caption is changed.
                    widget
                            .setPromptingOff(suggestion.getReplacementString());
                    widget.selectedOptionKey = suggestionKey;
                }
            }
            widget.currentSuggestion = suggestion;
            widget.setSelectedItemIcon(suggestion.getIconUri());
            // only a single item can be selected
            break;
        }
    }

    private boolean isWidgetsCurrentSelectionTextInTextBox() {
        return getWidget().currentSuggestion != null
                && getWidget().currentSuggestion.getReplacementString()
                        .equals(getWidget().tb.getText());
    }

    private void resetSelection() {
        VFilterSelect widget = getWidget();
        if (!widget.waitingForFilteringResponse || widget.popupOpenerClicked) {
            // select nulled
            if (!widget.focused) {
                /*
                 * client.updateComponent overwrites all styles so we must
                 * ALWAYS set the prompting style at this point, even though we
                 * think it has been set already...
                 */
                widget.setPromptingOff("");
                if (widget.enabled && !widget.readonly) {
                    widget.setPromptingOn();
                }
            } else {
                // we have focus in field, prompting can't be set on, instead
                // just clear the input if the value has changed from something
                // else to null
                if (widget.selectedOptionKey != null || (widget.allowNewItem
                        && !widget.tb.getValue().isEmpty())) {

                    boolean openedPopupWithNonScrollingMode = (widget.popupOpenerClicked
                            && widget.getSelectedCaption() != null);
                    if (!openedPopupWithNonScrollingMode) {
                        widget.tb.setValue("");
                    } else {
                        widget.tb.setValue(widget.getSelectedCaption());
                        widget.tb.selectAll();
                    }
                }
            }
            widget.currentSuggestion = null; // #13217
            widget.setSelectedItemIcon(null);
            widget.selectedOptionKey = null;
        }
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

    @Override
    public void setWidgetEnabled(boolean widgetEnabled) {
        super.setWidgetEnabled(widgetEnabled);
        getWidget().enabled = widgetEnabled;
        getWidget().tb.setEnabled(widgetEnabled);
    }

}
