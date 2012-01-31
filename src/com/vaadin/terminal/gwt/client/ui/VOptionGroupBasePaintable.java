/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VOptionGroupBasePaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Save details
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getId();

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        getWidgetForPaintable().selectedKeys = uidl
                .getStringArrayVariableAsSet("selected");

        getWidgetForPaintable().readonly = uidl.getBooleanAttribute("readonly");
        getWidgetForPaintable().disabled = uidl.getBooleanAttribute("disabled");
        getWidgetForPaintable().multiselect = "multi".equals(uidl
                .getStringAttribute("selectmode"));
        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");
        getWidgetForPaintable().nullSelectionAllowed = uidl
                .getBooleanAttribute("nullselect");
        getWidgetForPaintable().nullSelectionItemAvailable = uidl
                .getBooleanAttribute("nullselectitem");

        if (uidl.hasAttribute("cols")) {
            getWidgetForPaintable().cols = uidl.getIntAttribute("cols");
        }
        if (uidl.hasAttribute("rows")) {
            getWidgetForPaintable().rows = uidl.getIntAttribute("rows");
        }

        final UIDL ops = uidl.getChildUIDL(0);

        if (getWidgetForPaintable().getColumns() > 0) {
            getWidgetForPaintable().container.setWidth(getWidgetForPaintable()
                    .getColumns() + "em");
            if (getWidgetForPaintable().container != getWidgetForPaintable().optionsContainer) {
                getWidgetForPaintable().optionsContainer.setWidth("100%");
            }
        }

        getWidgetForPaintable().buildOptions(ops);

        if (uidl.getBooleanAttribute("allownewitem")) {
            if (getWidgetForPaintable().newItemField == null) {
                getWidgetForPaintable().newItemButton = new VNativeButton();
                getWidgetForPaintable().newItemButton.setText("+");
                getWidgetForPaintable().newItemButton
                        .addClickHandler(getWidgetForPaintable());
                getWidgetForPaintable().newItemField = new VTextField();
                getWidgetForPaintable().newItemField
                        .addKeyPressHandler(getWidgetForPaintable());
            }
            getWidgetForPaintable().newItemField
                    .setEnabled(!getWidgetForPaintable().disabled
                            && !getWidgetForPaintable().readonly);
            getWidgetForPaintable().newItemButton
                    .setEnabled(!getWidgetForPaintable().disabled
                            && !getWidgetForPaintable().readonly);

            if (getWidgetForPaintable().newItemField == null
                    || getWidgetForPaintable().newItemField.getParent() != getWidgetForPaintable().container) {
                getWidgetForPaintable().container
                        .add(getWidgetForPaintable().newItemField);
                getWidgetForPaintable().container
                        .add(getWidgetForPaintable().newItemButton);
                final int w = getWidgetForPaintable().container
                        .getOffsetWidth()
                        - getWidgetForPaintable().newItemButton
                                .getOffsetWidth();
                getWidgetForPaintable().newItemField.setWidth(Math.max(w, 0)
                        + "px");
            }
        } else if (getWidgetForPaintable().newItemField != null) {
            getWidgetForPaintable().container
                    .remove(getWidgetForPaintable().newItemField);
            getWidgetForPaintable().container
                    .remove(getWidgetForPaintable().newItemButton);
        }

        getWidgetForPaintable().setTabIndex(
                uidl.hasAttribute("tabindex") ? uidl
                        .getIntAttribute("tabindex") : 0);

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VOptionGroupBase.class);
    }

    @Override
    public VOptionGroupBase getWidgetForPaintable() {
        return (VOptionGroupBase) super.getWidgetForPaintable();
    }
}
