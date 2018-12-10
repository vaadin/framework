/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.vaadin.client.Profiler;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VComboBox;
import com.vaadin.client.ui.VComboBox.ComboBoxSuggestion;
import com.vaadin.client.ui.VComboBox.DataReceivedHandler;
import com.vaadin.shared.EventId;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.combobox.ComboBoxClientRpc;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.ui.ComboBox;

import elemental.json.JsonObject;

@Connect(ComboBox.class)
public class ComboBoxConnector extends AbstractListingConnector
        implements SimpleManagedLayout {

    private ComboBoxServerRpc rpc = getRpcProxy(ComboBoxServerRpc.class);
    private SelectionServerRpc selectionRpc = getRpcProxy(
            SelectionServerRpc.class);

    private FocusAndBlurServerRpc focusAndBlurRpc = getRpcProxy(
            FocusAndBlurServerRpc.class);

    private Registration dataChangeHandlerRegistration;

    /**
     * new item value that has been sent to server but selection handling hasn't
     * been performed for it yet
     */
    private String pendingNewItemValue = null;

    @Override
    protected void init() {
        super.init();
        getWidget().connector = this;
        registerRpc(ComboBoxClientRpc.class, new ComboBoxClientRpc() {
            @Override
            public void newItemNotAdded(String itemValue) {
                if (itemValue != null && itemValue.equals(pendingNewItemValue)
                        && isNewItemStillPending()) {
                    // handled but not added, perform (de-)selection handling
                    // immediately
                    completeNewItemHandling();
                }
            }
        });
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        Profiler.enter("ComboBoxConnector.onStateChanged update content");

        VComboBox widget = getWidget();
        widget.readonly = isReadOnly();
        widget.updateReadOnly();

        // not a FocusWidget -> needs own tabindex handling
        widget.tb.setTabIndex(getState().tabIndex);

        widget.suggestionPopup.updateStyleNames(getState());

        // TODO if the pop up is opened, the actual item should be removed from
        // the popup (?)
        widget.nullSelectionAllowed = getState().emptySelectionAllowed;
        // TODO having this true would mean that the empty selection item comes
        // from the data source so none needs to be added - currently
        // unsupported
        widget.nullSelectItem = false;

        // make sure the input prompt is updated
        widget.updatePlaceholder();

        getDataReceivedHandler().serverReplyHandled();

        // all updates except options have been done
        widget.initDone = true;

        Profiler.leave("ComboBoxConnector.onStateChanged update content");
    }

    @OnStateChange("emptySelectionCaption")
    private void onEmptySelectionCaptionChange() {
        List<ComboBoxSuggestion> suggestions = getWidget().currentSuggestions;
        if (!suggestions.isEmpty() && isFirstPage()) {
            suggestions.remove(0);
            addEmptySelectionItem();
        }
        getWidget().setEmptySelectionCaption(getState().emptySelectionCaption);
    }

    @OnStateChange({ "selectedItemKey", "selectedItemCaption",
            "selectedItemIcon" })
    private void onSelectionChange() {
        if (getWidget().selectedOptionKey != getState().selectedItemKey) {
            getWidget().selectedOptionKey = null;
            getWidget().currentSuggestion = null;
        }
        if (isNewItemStillPending()
                && pendingNewItemValue == getState().selectedItemCaption) {
            // no automated selection handling required
            clearNewItemHandling();
        }
        getDataReceivedHandler().updateSelectionFromServer(
                getState().selectedItemKey, getState().selectedItemCaption,
                getState().selectedItemIcon);
    }

    @Override
    public VComboBox getWidget() {
        return (VComboBox) super.getWidget();
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
        VComboBox widget = getWidget();
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
     * These methods exist to move communications out of VComboBox, and may be
     * refactored/removed in the future
     */

    /**
     * Send a message about a newly created item to the server.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     *
     * @since 8.0
     * @param itemValue
     *            user entered string value for the new item
     */
    public void sendNewItem(String itemValue) {
        if (itemValue != null && !itemValue.equals(pendingNewItemValue)) {
            // clear any previous handling as outdated
            clearNewItemHandling();
        }
        pendingNewItemValue = itemValue;
        rpc.createNewItem(itemValue);
        getDataReceivedHandler().clearPendingNavigation();
    }

    /**
     * Send a message to the server set the current filter.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     *
     * @since 8.0
     * @param filter
     *            the current filter string
     */
    protected void setFilter(String filter) {
        if (!Objects.equals(filter, getState().currentFilterText)) {
            getDataReceivedHandler().clearPendingNavigation();

            rpc.setFilter(filter);
        }
    }

    /**
     * Confirm with the widget that the pending new item value is still pending.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     *
     * @return {@code true} if the value is still pending, {@code false} if
     *         there is no pending value or it doesn't match
     */
    private boolean isNewItemStillPending() {
        return getDataReceivedHandler().isPending(pendingNewItemValue);
    }

    /**
     * Send a message to the server to request a page of items with the current
     * filter.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     *
     * @since 8.0
     * @param page
     *            the page number to get or -1 to let the server/connector
     *            decide based on current selection (possibly loading more data
     *            from the server)
     * @param filter
     *            the filter to apply, never {@code null}
     */
    public void requestPage(int page, String filter) {
        setFilter(filter);

        if (page < 0) {
            if (getState().scrollToSelectedItem) {
                // TODO this should be optimized not to try to fetch everything
                getDataSource().ensureAvailability(0, getDataSource().size());
                return;
            }
            page = 0;
        }
        VComboBox widget = getWidget();
        int adjustment = widget.nullSelectionAllowed && filter.isEmpty() ? 1
                : 0;
        int startIndex = Math.max(0, page * widget.pageLength - adjustment);
        int pageLength = widget.pageLength > 0 ? widget.pageLength
                : getDataSource().size();
        getDataSource().ensureAvailability(startIndex, pageLength);
    }

    /**
     * Send a message to the server updating the current selection.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     *
     * @since 8.0
     * @param selectionKey
     *            the current selected item key
     */
    public void sendSelection(String selectionKey) {
        // map also the special empty string option key (from data change
        // handler below) to null
        selectionRpc.select("".equals(selectionKey) ? null : selectionKey);
        getDataReceivedHandler().clearPendingNavigation();
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
     * @since 8.0
     */
    public void sendFocusEvent() {
        boolean registeredListeners = hasEventListener(EventId.FOCUS);
        if (registeredListeners) {
            focusAndBlurRpc.focus();
            getDataReceivedHandler().clearPendingNavigation();
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
     * @since 8.0
     */
    public void sendBlurEvent() {
        boolean registeredListeners = hasEventListener(EventId.BLUR);
        if (registeredListeners) {
            focusAndBlurRpc.blur();
            getDataReceivedHandler().clearPendingNavigation();
        }
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);
        dataChangeHandlerRegistration = dataSource
                .addDataChangeHandler(new PagedDataChangeHandler(dataSource));
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        dataChangeHandlerRegistration.remove();
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return getState().required && !isReadOnly();
    }

    private void refreshData() {
        updateCurrentPage();

        int start = getWidget().currentPage * getWidget().pageLength;
        int end = getWidget().pageLength > 0 ? start + getWidget().pageLength
                : getDataSource().size();

        getWidget().currentSuggestions.clear();

        if (getWidget().getNullSelectionItemShouldBeVisible()) {
            // add special null selection item...
            if (isFirstPage()) {
                addEmptySelectionItem();
            } else {
                // ...or leave space for it
                start = start - 1;
            }
            // in either case, the last item to show is
            // shifted by one, unless no paging is used
            if (getState().pageLength != 0) {
                end = end - 1;
            }
        }

        updateSuggestions(start, end);
        getWidget().setTotalSuggestions(getDataSource().size());

        getDataReceivedHandler().dataReceived();
    }

    private void updateSuggestions(int start, int end) {
        for (int i = start; i < end; ++i) {
            JsonObject row = getDataSource().getRow(i);
            if (row != null) {
                String key = getRowKey(row);
                String caption = row.getString(DataCommunicatorConstants.NAME);
                String style = row.getString(ComboBoxConstants.STYLE);
                String untranslatedIconUri = row
                        .getString(ComboBoxConstants.ICON);
                ComboBoxSuggestion suggestion = getWidget().new ComboBoxSuggestion(
                        key, caption, style, untranslatedIconUri);
                getWidget().currentSuggestions.add(suggestion);
            } else {
                // there is not enough options to fill the page
                return;
            }
        }
    }

    private boolean isFirstPage() {
        return getWidget().currentPage == 0;
    }

    private void addEmptySelectionItem() {
        if (isFirstPage()) {
            getWidget().currentSuggestions.add(0,
                    getWidget().new ComboBoxSuggestion("",
                            getState().emptySelectionCaption, null, null));
        }
    }

    private void updateCurrentPage() {
        // try to find selected item if requested
        if (getState().scrollToSelectedItem && getState().pageLength > 0
                && getWidget().currentPage < 0
                && getWidget().selectedOptionKey != null) {
            // search for the item with the selected key
            getWidget().currentPage = 0;
            for (int i = 0; i < getDataSource().size(); ++i) {
                JsonObject row = getDataSource().getRow(i);
                if (row != null) {
                    String key = getRowKey(row);
                    if (getWidget().selectedOptionKey.equals(key)) {
                        if (getWidget().nullSelectionAllowed) {
                            getWidget().currentPage = (i + 1)
                                    / getState().pageLength;
                        } else {
                            getWidget().currentPage = i / getState().pageLength;
                        }
                        break;
                    }
                }
            }
        } else if (getWidget().currentPage < 0) {
            getWidget().currentPage = 0;
        }
    }

    /**
     * If previous calls to refreshData haven't sorted out the selection yet,
     * enforce it.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     */
    private void completeNewItemHandling() {
        // ensure the widget hasn't got a new selection in the meantime
        if (isNewItemStillPending()) {
            // mark new item for selection handling on the widget
            getWidget().suggestionPopup.menu
                    .markNewItemsHandled(pendingNewItemValue);
            // clear pending value
            pendingNewItemValue = null;
            // trigger the final selection handling
            refreshData();
        } else {
            clearNewItemHandling();
        }
    }

    /**
     * Clears the pending new item value if the widget's pending value no longer
     * matches.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     */
    private void clearNewItemHandling() {
        // never clear pending value before it has been handled
        if (!isNewItemStillPending()) {
            pendingNewItemValue = null;
        }
    }

    /**
     * Clears the new item handling variables if the given value matches the
     * pending value.
     *
     * This method is for internal use only and may be removed in future
     * versions.
     *
     * @param value
     *            already handled value
     */
    public void clearNewItemHandlingIfMatch(String value) {
        if (value != null && value.equals(pendingNewItemValue)) {
            pendingNewItemValue = null;
        }
    }

    private static final Logger LOGGER = Logger
            .getLogger(ComboBoxConnector.class.getName());

    private class PagedDataChangeHandler implements DataChangeHandler {

        private final DataSource<?> dataSource;

        public PagedDataChangeHandler(DataSource<?> dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void dataUpdated(int firstRowIndex, int numberOfRows) {
            // NOOP since dataAvailable is always triggered afterwards
        }

        @Override
        public void dataRemoved(int firstRowIndex, int numberOfRows) {
            // NOOP since dataAvailable is always triggered afterwards
        }

        @Override
        public void dataAdded(int firstRowIndex, int numberOfRows) {
            // NOOP since dataAvailable is always triggered afterwards
        }

        @Override
        public void dataAvailable(int firstRowIndex, int numberOfRows) {
            refreshData();
        }

        @Override
        public void resetDataAndSize(int estimatedNewDataSize) {
            if (getState().pageLength == 0) {
                if (getWidget().suggestionPopup.isShowing()) {
                    dataSource.ensureAvailability(0, estimatedNewDataSize);
                }
                // else lets just wait till the popup is opened before
                // everything is fetched to it. this could be optimized later on
                // to fetch everything if in-memory data is used.
            } else {
                // reset data: clear any current options, set page to 0
                getWidget().currentPage = 0;
                getWidget().currentSuggestions.clear();
                dataSource.ensureAvailability(0, getState().pageLength);
            }
        }

    }
}
