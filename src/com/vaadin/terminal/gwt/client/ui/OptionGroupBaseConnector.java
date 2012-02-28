/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class OptionGroupBaseConnector extends
        AbstractComponentConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().selectedKeys = uidl.getStringArrayVariableAsSet("selected");

        getWidget().readonly = getState().isReadOnly();
        getWidget().disabled = getState().isDisabled();
        getWidget().multiselect = "multi".equals(uidl
                .getStringAttribute("selectmode"));
        getWidget().immediate = getState().isImmediate();
        getWidget().nullSelectionAllowed = uidl
                .getBooleanAttribute("nullselect");
        getWidget().nullSelectionItemAvailable = uidl
                .getBooleanAttribute("nullselectitem");

        if (uidl.hasAttribute("cols")) {
            getWidget().cols = uidl.getIntAttribute("cols");
        }
        if (uidl.hasAttribute("rows")) {
            getWidget().rows = uidl.getIntAttribute("rows");
        }

        final UIDL ops = uidl.getChildUIDL(0);

        if (getWidget().getColumns() > 0) {
            getWidget().container.setWidth(getWidget().getColumns() + "em");
            if (getWidget().container != getWidget().optionsContainer) {
                getWidget().optionsContainer.setWidth("100%");
            }
        }

        getWidget().buildOptions(ops);

        if (uidl.getBooleanAttribute("allownewitem")) {
            if (getWidget().newItemField == null) {
                getWidget().newItemButton = new VNativeButton();
                getWidget().newItemButton.setText("+");
                getWidget().newItemButton.addClickHandler(getWidget());
                getWidget().newItemField = new VTextField();
                getWidget().newItemField.addKeyPressHandler(getWidget());
            }
            getWidget().newItemField.setEnabled(!getWidget().disabled
                    && !getWidget().readonly);
            getWidget().newItemButton.setEnabled(!getWidget().disabled
                    && !getWidget().readonly);

            if (getWidget().newItemField == null
                    || getWidget().newItemField.getParent() != getWidget().container) {
                getWidget().container.add(getWidget().newItemField);
                getWidget().container.add(getWidget().newItemButton);
                final int w = getWidget().container.getOffsetWidth()
                        - getWidget().newItemButton.getOffsetWidth();
                getWidget().newItemField.setWidth(Math.max(w, 0) + "px");
            }
        } else if (getWidget().newItemField != null) {
            getWidget().container.remove(getWidget().newItemField);
            getWidget().container.remove(getWidget().newItemButton);
        }

        getWidget().setTabIndex(
                uidl.hasAttribute("tabindex") ? uidl
                        .getIntAttribute("tabindex") : 0);

    }

    @Override
    public VOptionGroupBase getWidget() {
        return (VOptionGroupBase) super.getWidget();
    }
}
