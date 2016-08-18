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
import com.vaadin.client.ui.VFilterSelect.DataReceivedHandler;
import com.vaadin.client.ui.VFilterSelect.FilterSelectSuggestion;
import com.vaadin.shared.EventId;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.v7.ui.ComboBox;

@Connect(ComboBox.class)
public class ComboBoxConnector extends AbstractFieldConnector
        implements Paintable, SimpleManagedLayout {

    protected ComboBoxServerRpc rpc = RpcProxy.create(ComboBoxServerRpc.class,
            this);

    protected FocusAndBlurServerRpc focusAndBlurRpc = RpcProxy
            .create(FocusAndBlurServerRpc.class, this);

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

        getWidget().setTextInputEnabled(getState().textInputAllowed);

        if (getState().inputPrompt != null) {
            getWidget().inputPrompt = getState().inputPrompt;
        } else {
            getWidget().inputPrompt = "";
        }

        getWidget().pageLength = getState().pageLength;

        getWidget().filteringmode = getState().filteringMode;

        getWidget().suggestionPopupWidth = getState().suggestionPopupWidth;

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

        // oldSuggestionTextMatchTheOldSelection is used to detect when it's
        // safe to update textbox text by a changed item caption.
        boolean oldSuggestionTextMatchesTheOldSelection = false;

        if (suggestionsChanged) {
            oldSuggestionTextMatchesTheOldSelection = isWidgetsCurrentSelectionTextInTextBox();
            getWidget().currentSuggestions.clear();

            if (!getDataReceivedHandler().isWaitingForFilteringResponse()) {
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

            // single selected key (can be empty string) or empty array for null
            // selection
            String[] selectedKeys = uidl.getStringArrayVariable("selected");
            String selectedKey = null;
            if (selectedKeys.length == 1) {
                selectedKey = selectedKeys[0];
            }
            // selected item caption in case it is not on the current page
            String selectedCaption = null;
            if (uidl.hasAttribute("selectedCaption")) {
                selectedCaption = uidl.getStringAttribute("selectedCaption");
            }

            getDataReceivedHandler().updateSelectionFromServer(selectedKey,
                    selectedCaption, oldSuggestionTextMatchesTheOldSelection);
        }

        // TODO even this condition should probably be moved to the handler
        if ((getDataReceivedHandler().isWaitingForFilteringResponse()
                && getWidget().lastFilter.toLowerCase()
                        .equals(uidl.getStringVariable("filter")))
                || popupOpenAndCleared) {
            getDataReceivedHandler().dataReceived();
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
        getDataReceivedHandler().serverReplyHandled();
    }

    private boolean isWidgetsCurrentSelectionTextInTextBox() {
        return getWidget().currentSuggestion != null
                && getWidget().currentSuggestion.getReplacementString()
                        .equals(getWidget().tb.getText());
    }

    @Override
    public VFilterSelect getWidget() {
        return (VFilterSelect) super.getWidget();
    }

    private DataReceivedHandler getDataReceivedHandler() {
        return getWidget().getDataReceivedHandler();
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
     * Called after any request to server.
     */
    private void afterSendRequestToServer() {
        getDataReceivedHandler().anyRequestSentToServer();
    }

}
