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

import com.vaadin.client.Profiler;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.connectors.data.HasDataSource;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.HasErrorIndicator;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VComboBox;
import com.vaadin.client.ui.VComboBox.DataReceivedHandler;
import com.vaadin.shared.EventId;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.combobox.ComboBoxConstants;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.ui.ComboBox;

import elemental.json.JsonObject;

@Connect(ComboBox.class)
public class ComboBoxConnector
        extends AbstractListingConnector<SelectionModel.Single<?>>
        implements HasDataSource, SimpleManagedLayout, HasErrorIndicator {

    private ComboBoxServerRpc rpc = getRpcProxy(ComboBoxServerRpc.class);
    private SelectionServerRpc selectionRpc = getRpcProxy(
            SelectionServerRpc.class);

    private FocusAndBlurServerRpc focusAndBlurRpc = getRpcProxy(
            FocusAndBlurServerRpc.class);

    private Registration dataChangeHandlerRegistration;

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

        // not a FocusWidget -> needs own tabindex handling
        getWidget().tb.setTabIndex(getState().tabIndex);

        getWidget().suggestionPopup.updateStyleNames(getState());

        getWidget().nullSelectionAllowed = getState().emptySelectionAllowed;
        // TODO having this true would mean that the empty selection item comes
        // from the data source so none needs to be added - currently
        // unsupported
        getWidget().nullSelectItem = false;

        // make sure the input prompt is updated
        getWidget().updatePlaceholder();

        getDataReceivedHandler().serverReplyHandled();

        Profiler.leave("ComboBoxConnector.onStateChanged update content");
    }

    @OnStateChange({ "selectedItemKey", "selectedItemCaption" })
    private void onSelectionChange() {
        getDataReceivedHandler().updateSelectionFromServer(
                getState().selectedItemKey, getState().selectedItemCaption);
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
    public void setFilter(String filter) {
        if (filter != getWidget().lastFilter) {
            getDataReceivedHandler().clearPendingNavigation();
        }
        rpc.setFilter(filter);
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
     */
    public void requestPage(int page) {
        if (page < 0) {
            if (getState().scrollToSelectedItem) {
                getDataSource().ensureAvailability(0, 10000);
                return;
            } else {
                page = 0;
            }
        }
        int adjustment = (getWidget().nullSelectionAllowed
                && "".equals(getWidget().lastFilter)) ? 1 : 0;
        int startIndex = Math.max(0,
                page * getWidget().pageLength - adjustment);
        int pageLength = getWidget().pageLength > 0 ? getWidget().pageLength
                : 10000;
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
                .addDataChangeHandler(range -> {
                    // try to find selected item if requested
                    if (getState().scrollToSelectedItem
                            && getState().pageLength > 0
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
                                        getWidget().currentPage = i
                                                / getState().pageLength;
                                    }
                                    break;
                                }
                            }
                        }
                    } else if (getWidget().currentPage < 0) {
                        getWidget().currentPage = 0;
                    }

                    getWidget().currentSuggestions.clear();

                    int start = getWidget().currentPage
                            * getWidget().pageLength;
                    int end = getWidget().pageLength > 0
                            ? start + getWidget().pageLength
                            : getDataSource().size();

                    if (getWidget().nullSelectionAllowed
                            && "".equals(getWidget().lastFilter)) {
                        // add special null selection item...
                        if (getWidget().currentPage == 0) {
                            getWidget().currentSuggestions
                                    .add(getWidget().new ComboBoxSuggestion("",
                                            "", null, null));
                        } else {
                            // ...or leave space for it
                            start = start - 1;
                        }
                        // in either case, the last item to show is
                        // shifted by one
                        end = end - 1;
                    }

                    for (int i = start; i < end; ++i) {
                        JsonObject row = getDataSource().getRow(i);

                        if (row != null) {
                            String key = getRowKey(row);
                            String caption = row
                                    .getString(DataCommunicatorConstants.NAME);
                            String style = row
                                    .getString(ComboBoxConstants.STYLE);
                            String untranslatedIconUri = row
                                    .getString(ComboBoxConstants.ICON);
                            getWidget().currentSuggestions
                                    .add(getWidget().new ComboBoxSuggestion(key,
                                            caption, style,
                                            untranslatedIconUri));
                        }
                    }
                    getWidget().totalMatches = getDataSource().size()
                            + (getState().emptySelectionAllowed ? 1 : 0);

                    getDataReceivedHandler().dataReceived();
                });
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        dataChangeHandlerRegistration.remove();
    }

}
