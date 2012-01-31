package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VCaptionWrapper;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VPopupViewPaintable extends VAbstractPaintableWidgetContainer {

    /**
     * 
     * 
     * @see com.vaadin.terminal.gwt.client.VPaintableWidget#updateFromUIDL(com.vaadin.terminal.gwt.client.UIDL,
     *      com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and don't let the containing layout manage caption.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        // These are for future server connections
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().uidlId = uidl.getId();

        getWidgetForPaintable().hostPopupVisible = uidl
                .getBooleanVariable("popupVisibility");

        getWidgetForPaintable().setHTML(uidl.getStringAttribute("html"));

        if (uidl.hasAttribute("hideOnMouseOut")) {
            getWidgetForPaintable().popup.setHideOnMouseOut(uidl
                    .getBooleanAttribute("hideOnMouseOut"));
        }

        // Render the popup if visible and show it.
        if (getWidgetForPaintable().hostPopupVisible) {
            UIDL popupUIDL = uidl.getChildUIDL(0);

            // showPopupOnTop(popup, hostReference);
            getWidgetForPaintable().preparePopup(getWidgetForPaintable().popup);
            getWidgetForPaintable().popup.updateFromUIDL(popupUIDL, client);
            if (uidl.hasAttribute("style")) {
                final String[] styles = uidl.getStringAttribute("style").split(
                        " ");
                final StringBuffer styleBuf = new StringBuffer();
                final String primaryName = getWidgetForPaintable().popup
                        .getStylePrimaryName();
                styleBuf.append(primaryName);
                for (int i = 0; i < styles.length; i++) {
                    styleBuf.append(" ");
                    styleBuf.append(primaryName);
                    styleBuf.append("-");
                    styleBuf.append(styles[i]);
                }
                getWidgetForPaintable().popup.setStyleName(styleBuf.toString());
            } else {
                getWidgetForPaintable().popup
                        .setStyleName(getWidgetForPaintable().popup
                                .getStylePrimaryName());
            }
            getWidgetForPaintable().showPopup(getWidgetForPaintable().popup);

            // The popup shouldn't be visible, try to hide it.
        } else {
            getWidgetForPaintable().popup.hide();
        }
    }// updateFromUIDL

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        if (VCaption.isNeeded(uidl)) {
            if (getWidgetForPaintable().popup.captionWrapper != null) {
                getWidgetForPaintable().popup.captionWrapper
                        .updateCaption(uidl);
            } else {
                getWidgetForPaintable().popup.captionWrapper = new VCaptionWrapper(
                        component, getConnection());
                getWidgetForPaintable().popup
                        .setWidget(getWidgetForPaintable().popup.captionWrapper);
                getWidgetForPaintable().popup.captionWrapper
                        .updateCaption(uidl);
            }
        } else {
            if (getWidgetForPaintable().popup.captionWrapper != null) {
                getWidgetForPaintable().popup
                        .setWidget(getWidgetForPaintable().popup.popupComponentWidget);
            }
        }
    }

    @Override
    public VPopupView getWidgetForPaintable() {
        return (VPopupView) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPopupView.class);
    }

}
