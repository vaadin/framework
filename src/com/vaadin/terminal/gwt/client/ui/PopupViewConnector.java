/*
 @VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VCaptionWrapper;
import com.vaadin.terminal.gwt.client.ComponentConnector;

public class PopupViewConnector extends AbstractComponentContainerConnector {

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    /**
     * 
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentConnector#updateFromUIDL(com.vaadin.terminal.gwt.client.UIDL,
     *      com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and don't let the containing layout manage caption.
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        // These are for future server connections
        getWidget().client = client;
        getWidget().uidlId = uidl.getId();

        getWidget().hostPopupVisible = uidl
                .getBooleanVariable("popupVisibility");

        getWidget().setHTML(uidl.getStringAttribute("html"));

        if (uidl.hasAttribute("hideOnMouseOut")) {
            getWidget().popup.setHideOnMouseOut(uidl
                    .getBooleanAttribute("hideOnMouseOut"));
        }

        // Render the popup if visible and show it.
        if (getWidget().hostPopupVisible) {
            UIDL popupUIDL = uidl.getChildUIDL(0);

            // showPopupOnTop(popup, hostReference);
            getWidget().preparePopup(getWidget().popup);
            getWidget().popup.updateFromUIDL(popupUIDL, client);
            if (getState().hasStyles()) {
                final String[] styles = getState().getStyle().split(" ");
                final StringBuffer styleBuf = new StringBuffer();
                final String primaryName = getWidget().popup
                        .getStylePrimaryName();
                styleBuf.append(primaryName);
                for (int i = 0; i < styles.length; i++) {
                    styleBuf.append(" ");
                    styleBuf.append(primaryName);
                    styleBuf.append("-");
                    styleBuf.append(styles[i]);
                }
                getWidget().popup.setStyleName(styleBuf.toString());
            } else {
                getWidget().popup
                        .setStyleName(getWidget().popup
                                .getStylePrimaryName());
            }
            getWidget().showPopup(getWidget().popup);

            // The popup shouldn't be visible, try to hide it.
        } else {
            getWidget().popup.hide();
        }
    }// updateFromUIDL

    public void updateCaption(ComponentConnector component, UIDL uidl) {
        if (VCaption.isNeeded(uidl, component.getState())) {
            if (getWidget().popup.captionWrapper != null) {
                getWidget().popup.captionWrapper
                        .updateCaption(uidl);
            } else {
                getWidget().popup.captionWrapper = new VCaptionWrapper(
                        component, getConnection());
                getWidget().popup
                        .setWidget(getWidget().popup.captionWrapper);
                getWidget().popup.captionWrapper
                        .updateCaption(uidl);
            }
        } else {
            if (getWidget().popup.captionWrapper != null) {
                getWidget().popup
                        .setWidget(getWidget().popup.popupComponentWidget);
            }
        }
    }

    @Override
    public VPopupView getWidget() {
        return (VPopupView) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPopupView.class);
    }

}
