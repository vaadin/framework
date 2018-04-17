/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Paintable;
import com.vaadin.client.Profiler;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VPanel;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.panel.PanelServerRpc;
import com.vaadin.shared.ui.panel.PanelState;
import com.vaadin.ui.Panel;

@Connect(Panel.class)
public class PanelConnector extends AbstractSingleComponentContainerConnector
        implements Paintable, SimpleManagedLayout, PostLayoutListener,
        MayScrollChildren {

    private Integer uidlScrollTop;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(PanelServerRpc.class).click(mouseDetails);
        }
    };

    private Integer uidlScrollLeft;

    @Override
    public void init() {
        super.init();
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
        VPanel panel = getWidget();
        if (isRealUpdate(uidl)) {

            // Handle caption displaying and style names, prior generics.
            // Affects size calculations

            // Restore default stylenames
            panel.contentNode.setClassName(VPanel.CLASSNAME + "-content");
            panel.bottomDecoration.setClassName(VPanel.CLASSNAME + "-deco");
            panel.captionNode.setClassName(VPanel.CLASSNAME + "-caption");
            boolean hasCaption = hasCaption();
            if (hasCaption) {
                panel.setCaption(getState().caption);
            } else {
                panel.setCaption("");
                panel.captionNode.setClassName(VPanel.CLASSNAME + "-nocaption");
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
            panel.captionNode.setClassName(captionClass);
            panel.contentNode.setClassName(contentClass);
            panel.bottomDecoration.setClassName(decoClass);

            panel.makeScrollable();
        }

        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration();

        panel.client = client;
        panel.id = uidl.getId();

        if (getIconUri() != null) {
            panel.setIconUri(getIconUri(), client);
        } else {
            panel.setIconUri(null, client);
        }

        // We may have actions attached to this panel
        if (uidl.getChildCount() > 0) {
            final int cnt = uidl.getChildCount();
            for (int i = 0; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (panel.shortcutHandler == null) {
                        panel.shortcutHandler = new ShortcutActionHandler(
                                getConnectorId(), client);
                    }
                    panel.shortcutHandler.updateActionMap(childUidl);
                }
            }
        }

        if (getState().scrollTop != panel.scrollTop) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollTop = getState().scrollTop;
        }

        if (getState().scrollLeft != panel.scrollLeft) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollLeft = getState().scrollLeft;
        }

        // And apply tab index
        panel.contentNode.setTabIndex(getState().tabIndex);
    }

    /**
     * Detects if caption div should be visible.
     *
     * @return {@code true} if caption div should be shown
     */
    protected boolean hasCaption() {
        return getState().caption != null && !getState().caption.isEmpty();
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
        Profiler.enter("PanelConnector.layout getHeights");
        int top = layoutManager.getOuterHeight(panel.captionNode);
        int bottom = layoutManager.getInnerHeight(panel.bottomDecoration);
        Profiler.leave("PanelConnector.layout getHeights");

        Profiler.enter("PanelConnector.layout modify style");
        Style style = panel.getElement().getStyle();
        panel.captionNode.getParentElement().getStyle().setMarginTop(-top,
                Unit.PX);
        panel.bottomDecoration.getStyle().setMarginBottom(-bottom, Unit.PX);
        style.setPaddingTop(top, Unit.PX);
        style.setPaddingBottom(bottom, Unit.PX);
        Profiler.leave("PanelConnector.layout modify style");

        // Update scroll positions
        Profiler.enter("PanelConnector.layout update scroll positions");
        panel.contentNode.setScrollTop(panel.scrollTop);
        panel.contentNode.setScrollLeft(panel.scrollLeft);
        Profiler.leave("PanelConnector.layout update scroll positions");

        // Read actual value back to ensure update logic is correct
        Profiler.enter("PanelConnector.layout read scroll positions");
        panel.scrollTop = panel.contentNode.getScrollTop();
        panel.scrollLeft = panel.contentNode.getScrollLeft();
        Profiler.leave("PanelConnector.layout read scroll positions");
    }

    @Override
    public void postLayout() {
        VPanel panel = getWidget();
        if (uidlScrollTop != null) {
            // IE / Safari fix for when scroll top is set to greater than panel
            // height
            int maxScroll = panel.getWidget().getOffsetHeight();
            if (uidlScrollTop > maxScroll) {
                uidlScrollTop = maxScroll;
            }
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
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        // We always have 1 child, unless the child is hidden
        getWidget().setWidget(getContentWidget());
    }

}
