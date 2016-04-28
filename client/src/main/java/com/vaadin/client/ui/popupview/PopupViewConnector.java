/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.ui.popupview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.VCaption;
import com.vaadin.client.VCaptionWrapper;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VPopupView;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.popupview.PopupViewServerRpc;
import com.vaadin.shared.ui.popupview.PopupViewState;
import com.vaadin.ui.PopupView;

@Connect(PopupView.class)
public class PopupViewConnector extends AbstractHasComponentsConnector
        implements PostLayoutListener, VisibilityChangeHandler {

    private boolean centerAfterLayout = false;

    private final List<HandlerRegistration> handlerRegistration = new ArrayList<HandlerRegistration>();

    @Override
    protected void init() {
        super.init();

        handlerRegistration.add(getWidget().addVisibilityChangeHandler(this));
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setHTML(getState().html);
        getWidget().popup.setHideOnMouseOut(getState().hideOnMouseOut);
    }

    @Override
    public PopupViewState getState() {
        return (PopupViewState) super.getState();
    }

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

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // Render the popup if visible and show it.
        if (!getChildComponents().isEmpty()) {
            getWidget().preparePopup(getWidget().popup);
            getWidget().popup.setPopupConnector(getChildComponents().get(0));

            final StringBuffer styleBuf = new StringBuffer();
            final String primaryName = getWidget().popup.getStylePrimaryName();
            styleBuf.append(primaryName);

            // Add "animate-in" class back if already present
            boolean isAnimatingIn = getWidget().popup.getStyleName().contains(
                    VOverlay.ADDITIONAL_CLASSNAME_ANIMATE_IN);

            if (isAnimatingIn) {
                styleBuf.append(" ");
                styleBuf.append(primaryName);
                styleBuf.append("-");
                styleBuf.append(VOverlay.ADDITIONAL_CLASSNAME_ANIMATE_IN);
            }

            if (ComponentStateUtil.hasStyles(getState())) {
                for (String style : getState().styles) {
                    styleBuf.append(" ");
                    styleBuf.append(primaryName);
                    styleBuf.append("-");
                    styleBuf.append(style);
                }
            }

            getWidget().popup.setStyleName(styleBuf.toString());
            getWidget().showPopup(getWidget().popup);
            centerAfterLayout = true;

        } else {
            // The popup shouldn't be visible, try to hide it.
            getWidget().popup.hide();
        }
    }

    @Override
    public void onVisibilityChange(VisibilityChangeEvent event) {
        getRpcProxy(PopupViewServerRpc.class).setPopupVisibility(
                event.isVisible());
    }

}
