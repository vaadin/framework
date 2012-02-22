/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.VFilterSelect.FilterSelectSuggestion;

public class VFilterSelectPaintable extends VAbstractPaintableWidget {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getId();

        getWidgetForPaintable().readonly = getState().isReadOnly();
        getWidgetForPaintable().enabled = !uidl
                .hasAttribute(ATTRIBUTE_DISABLED);

        getWidgetForPaintable().tb.setEnabled(getWidgetForPaintable().enabled);
        getWidgetForPaintable().updateReadOnly();

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Inverse logic here to make the default case (text input enabled)
        // work without additional UIDL messages
        boolean noTextInput = uidl
                .hasAttribute(VFilterSelect.ATTR_NO_TEXT_INPUT)
                && uidl.getBooleanAttribute(VFilterSelect.ATTR_NO_TEXT_INPUT);
        getWidgetForPaintable().setTextInputEnabled(!noTextInput);

        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            getWidgetForPaintable().tb.setTabIndex(uidl
                    .getIntAttribute("tabindex"));
        }

        if (uidl.hasAttribute("filteringmode")) {
            getWidgetForPaintable().filteringmode = uidl
                    .getIntAttribute("filteringmode");
        }

        getWidgetForPaintable().immediate = getState().isImmediate();

        getWidgetForPaintable().nullSelectionAllowed = uidl
                .hasAttribute("nullselect");

        getWidgetForPaintable().nullSelectItem = uidl
                .hasAttribute("nullselectitem")
                && uidl.getBooleanAttribute("nullselectitem");

        getWidgetForPaintable().currentPage = uidl.getIntVariable("page");

        if (uidl.hasAttribute("pagelength")) {
            getWidgetForPaintable().pageLength = uidl
                    .getIntAttribute("pagelength");
        }

        if (uidl.hasAttribute(VFilterSelect.ATTR_INPUTPROMPT)) {
            // input prompt changed from server
            getWidgetForPaintable().inputPrompt = uidl
                    .getStringAttribute(VFilterSelect.ATTR_INPUTPROMPT);
        } else {
            getWidgetForPaintable().inputPrompt = "";
        }

        getWidgetForPaintable().suggestionPopup.updateStyleNames(uidl);

        getWidgetForPaintable().allowNewItem = uidl
                .hasAttribute("allownewitem");
        getWidgetForPaintable().lastNewItemString = null;

        getWidgetForPaintable().currentSuggestions.clear();
        if (!getWidgetForPaintable().waitingForFilteringResponse) {
            /*
             * Clear the current suggestions as the server response always
             * includes the new ones. Exception is when filtering, then we need
             * to retain the value if the user does not select any of the
             * options matching the filter.
             */
            getWidgetForPaintable().currentSuggestion = null;
            /*
             * Also ensure no old items in menu. Unless cleared the old values
             * may cause odd effects on blur events. Suggestions in menu might
             * not necessary exist in select at all anymore.
             */
            getWidgetForPaintable().suggestionPopup.menu.clearItems();

        }

        final UIDL options = uidl.getChildUIDL(0);
        if (uidl.hasAttribute("totalMatches")) {
            getWidgetForPaintable().totalMatches = uidl
                    .getIntAttribute("totalMatches");
        } else {
            getWidgetForPaintable().totalMatches = 0;
        }

        // used only to calculate minimum popup width
        String captions = Util.escapeHTML(getWidgetForPaintable().inputPrompt);

        for (final Iterator<?> i = options.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            final FilterSelectSuggestion suggestion = getWidgetForPaintable().new FilterSelectSuggestion(
                    optionUidl);
            getWidgetForPaintable().currentSuggestions.add(suggestion);
            if (optionUidl.hasAttribute("selected")) {
                if (!getWidgetForPaintable().waitingForFilteringResponse
                        || getWidgetForPaintable().popupOpenerClicked) {
                    String newSelectedOptionKey = Integer.toString(suggestion
                            .getOptionKey());
                    if (!newSelectedOptionKey
                            .equals(getWidgetForPaintable().selectedOptionKey)
                            || suggestion.getReplacementString().equals(
                                    getWidgetForPaintable().tb.getText())) {
                        // Update text field if we've got a new selection
                        // Also update if we've got the same text to retain old
                        // text selection behavior
                        getWidgetForPaintable().setPromptingOff(
                                suggestion.getReplacementString());
                        getWidgetForPaintable().selectedOptionKey = newSelectedOptionKey;
                    }
                }
                getWidgetForPaintable().currentSuggestion = suggestion;
                getWidgetForPaintable().setSelectedItemIcon(
                        suggestion.getIconUri());
            }

            // Collect captions so we can calculate minimum width for textarea
            if (captions.length() > 0) {
                captions += "|";
            }
            captions += Util.escapeHTML(suggestion.getReplacementString());
        }

        if ((!getWidgetForPaintable().waitingForFilteringResponse || getWidgetForPaintable().popupOpenerClicked)
                && uidl.hasVariable("selected")
                && uidl.getStringArrayVariable("selected").length == 0) {
            // select nulled
            if (!getWidgetForPaintable().waitingForFilteringResponse
                    || !getWidgetForPaintable().popupOpenerClicked) {
                if (!getWidgetForPaintable().focused) {
                    /*
                     * client.updateComponent overwrites all styles so we must
                     * ALWAYS set the prompting style at this point, even though
                     * we think it has been set already...
                     */
                    getWidgetForPaintable().prompting = false;
                    getWidgetForPaintable().setPromptingOn();
                } else {
                    // we have focus in field, prompting can't be set on,
                    // instead just clear the input
                    getWidgetForPaintable().tb.setValue("");
                }
            }
            getWidgetForPaintable().setSelectedItemIcon(null);
            getWidgetForPaintable().selectedOptionKey = null;
        }

        if (getWidgetForPaintable().waitingForFilteringResponse
                && getWidgetForPaintable().lastFilter.toLowerCase().equals(
                        uidl.getStringVariable("filter"))) {
            getWidgetForPaintable().suggestionPopup.showSuggestions(
                    getWidgetForPaintable().currentSuggestions,
                    getWidgetForPaintable().currentPage,
                    getWidgetForPaintable().totalMatches);
            getWidgetForPaintable().waitingForFilteringResponse = false;
            if (!getWidgetForPaintable().popupOpenerClicked
                    && getWidgetForPaintable().selectPopupItemWhenResponseIsReceived != VFilterSelect.Select.NONE) {
                // we're paging w/ arrows
                if (getWidgetForPaintable().selectPopupItemWhenResponseIsReceived == VFilterSelect.Select.LAST) {
                    getWidgetForPaintable().suggestionPopup.menu
                            .selectLastItem();
                } else {
                    getWidgetForPaintable().suggestionPopup.menu
                            .selectFirstItem();
                }

                // This is used for paging so we update the keyboard selection
                // variable as well.
                MenuItem activeMenuItem = getWidgetForPaintable().suggestionPopup.menu
                        .getSelectedItem();
                getWidgetForPaintable().suggestionPopup.menu
                        .setKeyboardSelectedItem(activeMenuItem);

                // Update text field to contain the correct text
                getWidgetForPaintable()
                        .setTextboxText(activeMenuItem.getText());
                getWidgetForPaintable().tb.setSelectionRange(
                        getWidgetForPaintable().lastFilter.length(),
                        activeMenuItem.getText().length()
                                - getWidgetForPaintable().lastFilter.length());

                getWidgetForPaintable().selectPopupItemWhenResponseIsReceived = VFilterSelect.Select.NONE; // reset
            }
            if (getWidgetForPaintable().updateSelectionWhenReponseIsReceived) {
                getWidgetForPaintable().suggestionPopup.menu
                        .doPostFilterSelectedItemAction();
            }
        }

        // Calculate minumum textarea width
        getWidgetForPaintable().suggestionPopupMinWidth = getWidgetForPaintable()
                .minWidth(captions);

        getWidgetForPaintable().popupOpenerClicked = false;

        if (!getWidgetForPaintable().initDone) {
            getWidgetForPaintable().updateRootWidth();
        }

        // Focus dependent style names are lost during the update, so we add
        // them here back again
        if (getWidgetForPaintable().focused) {
            getWidgetForPaintable().addStyleDependentName("focus");
        }

        getWidgetForPaintable().initDone = true;
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VFilterSelect.class);
    }

    @Override
    public VFilterSelect getWidgetForPaintable() {
        return (VFilterSelect) super.getWidgetForPaintable();
    }
}
