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
package com.vaadin.client.ui.panel;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.panel.PanelServerRpc;
import com.vaadin.shared.ui.panel.PanelState;
import com.vaadin.ui.Panel;

@Connect(Panel.class)
public class PanelConnector extends AbstractComponentContainerConnector
        implements Paintable, SimpleManagedLayout, PostLayoutListener,
        MayScrollChildren {

    private Integer uidlScrollTop;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            rpc.click(mouseDetails);
        }
    };

    private Integer uidlScrollLeft;

    private PanelServerRpc rpc;

    @Override
    public void init() {
        super.init();
        rpc = RpcProxy.create(PanelServerRpc.class, this);
        VPanel panel = getWidget();
        LayoutManager layoutManager = getLayoutManager();

        layoutManager.registerDependency(this, panel.captionNode);
        layoutManager.registerDependency(this, panel.bottomDecoration);
        layoutManager.registerDependency(this, panel.contentNode);
    }

    @Override
    public void onUnregister() {
        VPanel panel = getWidget();
        LayoutManager layoutManager = getLayoutManager();

        layoutManager.unregisterDependency(this, panel.captionNode);
        layoutManager.unregisterDependency(this, panel.bottomDecoration);
        layoutManager.unregisterDependency(this, panel.contentNode);
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (isRealUpdate(uidl)) {

            // Handle caption displaying and style names, prior generics.
            // Affects size calculations

            // Restore default stylenames
            getWidget().contentNode.setClassName(VPanel.CLASSNAME + "-content");
            getWidget().bottomDecoration.setClassName(VPanel.CLASSNAME
                    + "-deco");
            getWidget().captionNode.setClassName(VPanel.CLASSNAME + "-caption");
            boolean hasCaption = false;
            if (getState().caption != null && !"".equals(getState().caption)) {
                getWidget().setCaption(getState().caption);
                hasCaption = true;
            } else {
                getWidget().setCaption("");
                getWidget().captionNode.setClassName(VPanel.CLASSNAME
                        + "-nocaption");
            }

            // Add proper stylenames for all elements. This way we can prevent
            // unwanted CSS selector inheritance.
            final String captionBaseClass = VPanel.CLASSNAME
                    + (hasCaption ? "-caption" : "-nocaption");
            final String contentBaseClass = VPanel.CLASSNAME + "-content";
            final String decoBaseClass = VPanel.CLASSNAME + "-deco";
            String captionClass = captionBaseClass;
            String contentClass = contentBaseClass;
            String decoClass = decoBaseClass;
            if (ComponentStateUtil.hasStyles(getState())) {
                for (String style : getState().styles) {
                    captionClass += " " + captionBaseClass + "-" + style;
                    contentClass += " " + contentBaseClass + "-" + style;
                    decoClass += " " + decoBaseClass + "-" + style;
                }
            }
            getWidget().captionNode.setClassName(captionClass);
            getWidget().contentNode.setClassName(contentClass);
            getWidget().bottomDecoration.setClassName(decoClass);

            getWidget().makeScrollable();
        }

        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration();

        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (getIcon() != null) {
            getWidget().setIconUri(getIcon(), client);
        } else {
            getWidget().setIconUri(null, client);
        }

        getWidget().setErrorIndicatorVisible(
null != getState().errorMessage);

        // We may have actions attached to this panel
        if (uidl.getChildCount() > 0) {
            final int cnt = uidl.getChildCount();
            for (int i = 0; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (getWidget().shortcutHandler == null) {
                        getWidget().shortcutHandler = new ShortcutActionHandler(
                                getConnectorId(), client);
                    }
                    getWidget().shortcutHandler.updateActionMap(childUidl);
                }
            }
        }

        if (getState().scrollTop != getWidget().scrollTop) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollTop = getState().scrollTop;
        }

        if (getState().scrollLeft != getWidget().scrollLeft) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollLeft = getState().scrollLeft;
        }

        // And apply tab index
        getWidget().contentNode.setTabIndex(getState().tabIndex);
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP: layouts caption, errors etc not rendered in Panel
    }

    @Override
    public VPanel getWidget() {
        return (VPanel) super.getWidget();
    }

    @Override
    public void layout() {
        updateSizes();
    }

    void updateSizes() {
        VPanel panel = getWidget();

        LayoutManager layoutManager = getLayoutManager();
        int top = layoutManager.getOuterHeight(panel.captionNode);
        int bottom = layoutManager.getInnerHeight(panel.bottomDecoration);

        Style style = panel.getElement().getStyle();
        panel.captionNode.getParentElement().getStyle()
                .setMarginTop(-top, Unit.PX);
        panel.bottomDecoration.getStyle().setMarginBottom(-bottom, Unit.PX);
        style.setPaddingTop(top, Unit.PX);
        style.setPaddingBottom(bottom, Unit.PX);

        // Update scroll positions
        panel.contentNode.setScrollTop(panel.scrollTop);
        panel.contentNode.setScrollLeft(panel.scrollLeft);
        // Read actual value back to ensure update logic is correct
        panel.scrollTop = panel.contentNode.getScrollTop();
        panel.scrollLeft = panel.contentNode.getScrollLeft();
    }

    @Override
    public void postLayout() {
        VPanel panel = getWidget();
        if (uidlScrollTop != null) {
            panel.contentNode.setScrollTop(uidlScrollTop.intValue());
            // Read actual value back to ensure update logic is correct
            // TODO Does this trigger reflows?
            panel.scrollTop = panel.contentNode.getScrollTop();
            uidlScrollTop = null;
        }

        if (uidlScrollLeft != null) {
            panel.contentNode.setScrollLeft(uidlScrollLeft.intValue());
            // Read actual value back to ensure update logic is correct
            // TODO Does this trigger reflows?
            panel.scrollLeft = panel.contentNode.getScrollLeft();
            uidlScrollLeft = null;
        }
    }

    @Override
    public PanelState getState() {
        return (PanelState) super.getState();
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);
        // We always have 1 child, unless the child is hidden
        Widget newChildWidget = null;
        if (getChildComponents().size() == 1) {
            ComponentConnector newChild = getChildComponents().get(0);
            newChildWidget = newChild.getWidget();
        }

        getWidget().setWidget(newChildWidget);
    }

}
