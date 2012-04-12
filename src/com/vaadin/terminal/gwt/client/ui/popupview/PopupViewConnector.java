/*
 @VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.popupview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VCaptionWrapper;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.ui.PopupView;

@Component(PopupView.class)
public class PopupViewConnector extends AbstractComponentContainerConnector
        implements Paintable, PostLayoutListener {

    private boolean centerAfterLayout = false;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    /**
     * 
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentConnector#updateFromUIDL(com.vaadin.terminal.gwt.client.UIDL,
     *      com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
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
                final StringBuffer styleBuf = new StringBuffer();
                final String primaryName = getWidget().popup
                        .getStylePrimaryName();
                styleBuf.append(primaryName);
                for (String style : getState().getStyles()) {
                    styleBuf.append(" ");
                    styleBuf.append(primaryName);
                    styleBuf.append("-");
                    styleBuf.append(style);
                }
                getWidget().popup.setStyleName(styleBuf.toString());
            } else {
                getWidget().popup.setStyleName(getWidget().popup
                        .getStylePrimaryName());
            }
            getWidget().showPopup(getWidget().popup);
            centerAfterLayout = true;

            // The popup shouldn't be visible, try to hide it.
        } else {
            getWidget().popup.hide();
        }
    }// updateFromUIDL

    public void updateCaption(ComponentConnector component) {
        if (VCaption.isNeeded(component.getState())) {
            if (getWidget().popup.captionWrapper != null) {
                getWidget().popup.captionWrapper.updateCaption();
            } else {
                getWidget().popup.captionWrapper = new VCaptionWrapper(
                        component, getConnection());
                getWidget().popup.setWidget(getWidget().popup.captionWrapper);
                getWidget().popup.captionWrapper.updateCaption();
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

    public void postLayout() {
        if (centerAfterLayout) {
            centerAfterLayout = false;
            getWidget().center();
        }
    }

}
