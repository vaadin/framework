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
package com.vaadin.client.ui.combobox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.Profiler;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VFilterSelect;
import com.vaadin.client.ui.VFilterSelect.FilterSelectSuggestion;
import com.vaadin.shared.EventId;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.ui.ComboBox;

@Connect(ComboBox.class)
public class ComboBoxConnector extends AbstractFieldConnector implements
        Paintable, SimpleManagedLayout {

    protected ComboBoxServerRpc rpc = RpcProxy.create(ComboBoxServerRpc.class,
            this);

    protected FocusAndBlurServerRpc focusAndBlurRpc = RpcProxy.create(
            FocusAndBlurServerRpc.class, this);

    // oldSuggestionTextMatchTheOldSelection is used to detect when it's safe to
    // update textbox text by a changed item caption.
    private boolean oldSuggestionTextMatchTheOldSelection;

    private boolean immediate;

    @Override
    protected void init() {
        super.init();
        getWidget().connector = this;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        Profiler.enter("ComboBoxConnector.onStateChanged update content");

        getWidget().readonly = isReadOnly();
        getWidget().updateReadOnly();

        immediate = getState().immediate;

        getWidget().setTextInputEnabled(getState().textInputAllowed);

        if (getState().inputPrompt != null) {
            getWidget().inputPrompt = getState().inputPrompt;
        } else {
            getWidget().inputPrompt = "";
        }

        getWidget().pageLength = getState().pageLength;

        getWidget().filteringmode = getState().filteringMode;

        Profiler.leave("ComboBoxConnector.onStateChanged update content");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Paintable#updateFromUIDL(com.vaadin.client.UIDL,
     * com.vaadin.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        // not a FocusWidget -> needs own tabindex handling
        getWidget().tb.setTabIndex(getState().tabIndex);

        getWidget().nullSelectionAllowed = uidl.hasAttribute("nullselect");

        getWidget().nullSelectItem = uidl.hasAttribute("nullselectitem")
                && uidl.getBooleanAttribute("nullselectitem");

        getWidget().currentPage = uidl.getIntVariable("page");

        if (uidl.hasAttribute("suggestionPopupWidth")) {
            getWidget().suggestionPopupWidth = uidl
                    .getStringAttribute("suggestionPopupWidth");
        } else {
            getWidget().suggestionPopupWidth = null;
        }

        getWidget().suggestionPopup.updateStyleNames(getState());

        getWidget().allowNewItem = uidl.hasAttribute("allownewitem");
        getWidget().lastNewItemString = null;

        final UIDL options = uidl.getChildUIDL(0);
        if (uidl.hasAttribute("totalMatches")) {
            getWidget().totalMatches = uidl.getIntAttribute("totalMatches");
        } else {
            getWidget().totalMatches = 0;
        }

        List<FilterSelectSuggestion> newSuggestions = new ArrayList<FilterSelectSuggestion>();

        for (final Iterator<?> i = options.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            String key = optionUidl.getStringAttribute("key");
            String caption = optionUidl.getStringAttribute("caption");
            String style = optionUidl.getStringAttribute("style");

            String untranslatedIconUri = null;
            if (optionUidl.hasAttribute("icon")) {
                untranslatedIconUri = optionUidl.getStringAttribute("icon");
            }

            final FilterSelectSuggestion suggestion = getWidget().new FilterSelectSuggestion(
                    key, caption, style, untranslatedIconUri);
            newSuggestions.add(suggestion);
        }

        // only close the popup if the suggestions list has actually changed
        boolean suggestionsChanged = !getWidget().initDone
                || !newSuggestions.equals(getWidget().currentSuggestions);

        // An ItemSetChangeEvent on server side clears the current suggestion
        // popup. Popup needs to be repopulated with suggestions from UIDL.
        boolean popupOpenAndCleared = false;

        oldSuggestionTextMatchTheOldSelection = false;

        if (suggestionsChanged) {
            oldSuggestionTextMatchTheOldSelection = isWidgetsCurrentSelectionTextInTextBox();
            getWidget().currentSuggestions.clear();

            if (!getWidget().isWaitingForFilteringResponse()) {
                /*
                 * Clear the current suggestions as the server response always
                 * includes the new ones. Exception is when filtering, then we
                 * need to retain the value if the user does not select any of
                 * the options matching the filter.
                 */
                getWidget().currentSuggestion = null;
                /*
                 * Also ensure no old items in menu. Unless cleared the old
                 * values may cause odd effects on blur events. Suggestions in
                 * menu might not necessary exist in select at all anymore.
                 */
                getWidget().suggestionPopup.menu.clearItems();
                popupOpenAndCleared = getWidget().suggestionPopup.isAttached();

            }

            for (FilterSelectSuggestion suggestion : newSuggestions) {
                getWidget().currentSuggestions.add(suggestion);
            }
        }

        // handle selection (null or a single value)
        if (uidl.hasVariable("selected")

        // In case we're switching page no need to update the selection as the
        // selection process didn't finish.
        // && getWidget().selectPopupItemWhenResponseIsReceived ==
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
            } else if (!getWidget().isWaitingForFilteringResponse()
                    && uidl.hasAttribute("selectedCaption")) {
                // scrolling to correct page is disabled, caption is passed as a
                // special parameter
                getWidget().tb.setText(uidl
                        .getStringAttribute("selectedCaption"));
            } else {
                resetSelection();
            }
        }

        // TODO even this condition should probably be moved to the handler
        if ((getWidget().isWaitingForFilteringResponse() && getWidget().lastFilter
                .toLowerCase().equals(uidl.getStringVariable("filter")))
                || popupOpenAndCleared) {
            getWidget().getDataReceivedHandler().dataReceived();
        }

        // Calculate minimum textarea width
        getWidget().updateSuggestionPopupMinWidth();

        /*
         * if this is our first time we need to recalculate the root width.
         */
        if (!getWidget().initDone) {

            getWidget().updateRootWidth();
        }

        // Focus dependent style names are lost during the update, so we add
        // them here back again
        if (getWidget().focused) {
            getWidget().addStyleDependentName("focus");
        }

        getWidget().initDone = true;

        // TODO this should perhaps be moved to be a part of dataReceived()
        getWidget().getDataReceivedHandler().serverReplyHandled();
    }

    private void performSelection(String selectedKey) {
        // some item selected
        for (FilterSelectSuggestion suggestion : getWidget().currentSuggestions) {
            String suggestionKey = suggestion.getOptionKey();
            if (!suggestionKey.equals(selectedKey)) {
                continue;
            }
            if (!getWidget().isWaitingForFilteringResponse()
                    || getWidget().getDataReceivedHandler()
                            .isPopupOpenerClicked()) {
                if (!suggestionKey.equals(getWidget().selectedOptionKey)
                        || suggestion.getReplacementString().equals(
                                getWidget().tb.getText())
                        || oldSuggestionTextMatchTheOldSelection) {
                    // Update text field if we've got a new
                    // selection
                    // Also update if we've got the same text to
                    // retain old text selection behavior
                    // OR if selected item caption is changed.
                    getWidget().setPromptingOff(
                            suggestion.getReplacementString());
                    getWidget().selectedOptionKey = suggestionKey;
                }
            }
            getWidget().currentSuggestion = suggestion;
            getWidget().setSelectedItemIcon(suggestion.getIconUri());
            // only a single item can be selected
            break;
        }
    }

    private boolean isWidgetsCurrentSelectionTextInTextBox() {
        return getWidget().currentSuggestion != null
                && getWidget().currentSuggestion.getReplacementString().equals(
                        getWidget().tb.getText());
    }

    private void resetSelection() {
        if (!getWidget().isWaitingForFilteringResponse()
                || getWidget().getDataReceivedHandler().isPopupOpenerClicked()) {
            // select nulled
            if (!getWidget().focused) {
                /*
                 * client.updateComponent overwrites all styles so we must
                 * ALWAYS set the prompting style at this point, even though we
                 * think it has been set already...
                 */
                getWidget().setPromptingOff("");
                if (getWidget().enabled && !getWidget().readonly) {
                    getWidget().setPromptingOn();
                }
            } else {
                // we have focus in field, prompting can't be set on, instead
                // just clear the input if the value has changed from something
                // else to null
                if (getWidget().selectedOptionKey != null
                        || (getWidget().allowNewItem && !getWidget().tb
                                .getValue().isEmpty())) {
                    getWidget().tb.setValue("");
                }
            }
            getWidget().currentSuggestion = null; // #13217
            getWidget().setSelectedItemIcon(null);
            getWidget().selectedOptionKey = null;
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

    /*
     * These methods exist to move communications out of VFilterSelect, and may
     * be refactored/removed in the future
     */

    /**
     * Send a message about a newly created item to the server.
     * 
     * This method is for internal use only and may be removed in future
     * versions.
     * 
     * @since
     * @param itemValue
     *            user entered string value for the new item
     */
    public void sendNewItem(String itemValue) {
        rpc.createNewItem(itemValue);
        afterSendRequestToServer();
    }

    /**
     * Send a message to the server to request the first page of items without
     * filtering or selection.
     * 
     * This method is for internal use only and may be removed in future
     * versions.
     * 
     * @since
     */
    public void requestFirstPage() {
        sendSelection(null);
        requestPage("", 0);
    }

    /**
     * Send a message to the server to request a page of items with a given
     * filter.
     * 
     * This method is for internal use only and may be removed in future
     * versions.
     * 
     * @since
     * @param filter
     *            the current filter string
     * @param page
     *            the page number to get
     */
    public void requestPage(String filter, int page) {
        rpc.requestPage(filter, page);
        afterSendRequestToServer();
    }

    /**
     * Send a message to the server updating the current selection.
     * 
     * This method is for internal use only and may be removed in future
     * versions.
     * 
     * @since
     * @param selection
     *            the current selection
     */
    public void sendSelection(String selection) {
        rpc.setSelectedItem(selection);
        afterSendRequestToServer();
    }

    /**
     * Notify the server that the combo box received focus.
     * 
     * For timing reasons, ConnectorFocusAndBlurHandler is not used at the
     * moment.
     * 
     * This method is for internal use only and may be removed in future
     * versions.
     * 
     * @since
     */
    public void sendFocusEvent() {
        boolean registeredListeners = hasEventListener(EventId.FOCUS);
        if (registeredListeners) {
            focusAndBlurRpc.focus();
            afterSendRequestToServer();
        }
    }

    /**
     * Notify the server that the combo box lost focus.
     * 
     * For timing reasons, ConnectorFocusAndBlurHandler is not used at the
     * moment.
     * 
     * This method is for internal use only and may be removed in future
     * versions.
     * 
     * @since
     */
    public void sendBlurEvent() {
        boolean registeredListeners = hasEventListener(EventId.BLUR);
        if (registeredListeners) {
            focusAndBlurRpc.blur();
            afterSendRequestToServer();
        }
    }

    /*
     * Anything that should be set after the client updates the server.
     */
    private void afterSendRequestToServer() {
        // We need this here to be consistent with the all the calls.
        // Then set your specific selection type only after
        // a server request method call.
        getWidget().getDataReceivedHandler().anyRequestSentToServer();
    }

}
