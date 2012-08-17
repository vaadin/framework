/*
  * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui.popupview;

import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VCaptionWrapper;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.ui.PopupView;

@Connect(PopupView.class)
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
    @Override
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

    @Override
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
    public void postLayout() {
        if (centerAfterLayout) {
            centerAfterLayout = false;
            getWidget().center();
        }
    }

}
