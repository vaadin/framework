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
package com.vaadin.client.ui.csslayout;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.FastStringMap;
import com.vaadin.client.Profiler;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.shared.ui.csslayout.CssLayoutServerRpc;
import com.vaadin.shared.ui.csslayout.CssLayoutState;
import com.vaadin.ui.CssLayout;

/**
 * Connects the server side widget {@link CssLayout} with the client side
 * counterpart {@link VCssLayout}
 */
@Connect(CssLayout.class)
public class CssLayoutConnector extends AbstractLayoutConnector {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(
                com.google.gwt.user.client.Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(CssLayoutServerRpc.class);
        }
    };

    private final FastStringMap<VCaption> childIdToCaption = FastStringMap
            .create();

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#init()
     */
    @Override
    protected void init() {
        super.init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractLayoutConnector#getState()
     */
    @Override
    public CssLayoutState getState() {
        return (CssLayoutState) super.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin
     * .client.communication.StateChangeEvent)
     */
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        clickEventHandler.handleEventHandlerRegistration();

        for (ComponentConnector child : getChildComponents()) {
            if (!getState().childCss.containsKey(child)) {
                continue;
            }
            String css = getState().childCss.get(child);
            Style style = child.getWidget().getElement().getStyle();
            // should we remove styles also? How can we know what we have added
            // as it is added directly to the child component?
            String[] cssRules = css.split(";");
            for (String cssRule : cssRules) {
                String parts[] = cssRule.split(":", 2);
                if (parts.length == 2) {
                    style.setProperty(makeCamelCase(parts[0].trim()),
                            parts[1].trim());
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentContainerConnector#
     * onConnectorHierarchyChange
     * (com.vaadin.client.ConnectorHierarchyChangeEvent)
     */
    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        Profiler.enter("CssLayoutConnector.onConnectorHierarchyChange");
        Profiler.enter("CssLayoutConnector.onConnectorHierarchyChange add children");
        int index = 0;
        for (ComponentConnector child : getChildComponents()) {
            VCaption childCaption = childIdToCaption
                    .get(child.getConnectorId());
            if (childCaption != null) {
                getWidget().addOrMove(childCaption, index++);
            }
            getWidget().addOrMove(child.getWidget(), index++);
        }
        Profiler.leave("CssLayoutConnector.onConnectorHierarchyChange add children");

        // Detach old child widgets and possibly their caption
        Profiler.enter("CssLayoutConnector.onConnectorHierarchyChange remove old children");
        for (ComponentConnector child : event.getOldChildren()) {
            if (child.getParent() == this) {
                // Skip current children
                continue;
            }
            getWidget().remove(child.getWidget());
            VCaption vCaption = childIdToCaption.get(child.getConnectorId());
            if (vCaption != null) {
                childIdToCaption.remove(child.getConnectorId());
                getWidget().remove(vCaption);
            }
        }
        Profiler.leave("CssLayoutConnector.onConnectorHierarchyChange remove old children");
        Profiler.leave("CssLayoutConnector.onConnectorHierarchyChange");
    }

    /**
     * Converts a css property string to CamelCase
     * 
     * @param cssProperty
     *            The property string
     * @return A string converted to camelcase
     */
    private static final String makeCamelCase(String cssProperty) {
        // TODO this might be cleaner to implement with regexp
        while (cssProperty.contains("-")) {
            int indexOf = cssProperty.indexOf("-");
            cssProperty = cssProperty.substring(0, indexOf)
                    + String.valueOf(cssProperty.charAt(indexOf + 1))
                            .toUpperCase() + cssProperty.substring(indexOf + 2);
        }
        if ("float".equals(cssProperty)) {
            if (BrowserInfo.get().isIE()) {
                return "styleFloat";
            } else {
                return "cssFloat";
            }
        }
        return cssProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#getWidget()
     */
    @Override
    public VCssLayout getWidget() {
        return (VCssLayout) super.getWidget();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin
     * .client.ComponentConnector)
     */
    @Override
    public void updateCaption(ComponentConnector child) {
        Widget childWidget = child.getWidget();
        int widgetPosition = getWidget().getWidgetIndex(childWidget);

        String childId = child.getConnectorId();
        VCaption caption = childIdToCaption.get(childId);
        if (VCaption.isNeeded(child.getState())) {
            if (caption == null) {
                caption = new VCaption(child, getConnection());
                childIdToCaption.put(childId, caption);
            }
            if (!caption.isAttached()) {
                // Insert caption at widget index == before widget
                getWidget().insert(caption, widgetPosition);
            }
            caption.updateCaption();
        } else if (caption != null) {
            childIdToCaption.remove(childId);
            getWidget().remove(caption);
        }
    }
}
