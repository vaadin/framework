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
package com.vaadin.client.ui.popupview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.VCaption;
import com.vaadin.client.VCaptionWrapper;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.PopupView;

@Connect(PopupView.class)
public class PopupViewConnector extends AbstractComponentContainerConnector
        implements PostLayoutListener, VisibilityChangeEventHandler {

    private boolean centerAfterLayout = false;

    private final PopupViewServerRpc rpc = RpcProxy.create(
            PopupViewServerRpc.class, this);
    private final List<HandlerRegistration> handlerRegistration = new ArrayList<HandlerRegistration>();

    @Override
    protected void init() {
        super.init();
        
        handlerRegistration.add(getWidget().addVisibilityChangeEventHandler(
                this));
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
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
        if (!getChildren().isEmpty()) {
            getWidget().preparePopup(getWidget().popup);
            getWidget().popup
                    .setPopupConnector((ComponentConnector) getChildren()
                            .get(0));
            if (ComponentStateUtil.hasStyles(getState())) {
                final StringBuffer styleBuf = new StringBuffer();
                final String primaryName = getWidget().popup
                        .getStylePrimaryName();
                styleBuf.append(primaryName);
                for (String style : getState().styles) {
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
    }

    @Override
    public void onVisibilityChangeEvent(VisibilityChangeEvent event) {
        rpc.setPopupVisibility(event.getValue());
    }

}