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
package com.vaadin.client.ui.window;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.window.WindowServerRpc;
import com.vaadin.shared.ui.window.WindowState;

@Connect(value = com.vaadin.ui.Window.class)
public class WindowConnector extends AbstractComponentContainerConnector
        implements Paintable, BeforeShortcutActionListener,
        SimpleManagedLayout, PostLayoutListener, MayScrollChildren {

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(WindowServerRpc.class).click(mouseDetails);
        }
    };

    boolean minWidthChecked = false;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    };

    @Override
    protected void init() {
        super.init();

        getLayoutManager().registerDependency(this,
                getWidget().contentPanel.getElement());
        getLayoutManager().registerDependency(this, getWidget().header);
        getLayoutManager().registerDependency(this, getWidget().footer);

        getWidget().setOwner(getConnection().getUIConnector().getWidget());
    }

    @Override
    public void onUnregister() {
        LayoutManager lm = getLayoutManager();
        VWindow window = getWidget();
        lm.unregisterDependency(this, window.contentPanel.getElement());
        lm.unregisterDependency(this, window.header);
        lm.unregisterDependency(this, window.footer);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().id = getConnectorId();
        getWidget().client = client;

        // Workaround needed for Testing Tools (GWT generates window DOM
        // slightly different in different browsers).
        DOM.setElementProperty(getWidget().closeBox, "id", getConnectorId()
                + "_window_close");

        if (isRealUpdate(uidl)) {
            if (getState().modal != getWidget().vaadinModality) {
                getWidget().setVaadinModality(!getWidget().vaadinModality);
            }
            if (!getWidget().isAttached()) {
                getWidget().setVisible(false); // hide until
                // possible centering
                getWidget().show();
            }
            if (getState().resizable != getWidget().resizable) {
                getWidget().setResizable(getState().resizable);
            }
            getWidget().resizeLazy = getState().resizeLazy;

            getWidget().setDraggable(getState().draggable);

            // Caption must be set before required header size is measured. If
            // the caption attribute is missing the caption should be cleared.
            String iconURL = null;
            if (getIcon() != null) {
                iconURL = getIcon();
            }
            getWidget().setCaption(getState().caption, iconURL);
        }

        getWidget().visibilityChangesDisabled = true;
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().visibilityChangesDisabled = false;

        clickEventHandler.handleEventHandlerRegistration();

        getWidget().immediate = getState().immediate;

        getWidget().setClosable(!isReadOnly());

        // Initialize the position form UIDL
        int positionx = getState().positionX;
        int positiony = getState().positionY;
        if (positionx >= 0 || positiony >= 0) {
            if (positionx < 0) {
                positionx = 0;
            }
            if (positiony < 0) {
                positiony = 0;
            }
            getWidget().setPopupPosition(positionx, positiony);
        }

        int childIndex = 0;

        // we may have actions
        for (int i = 0; i < uidl.getChildCount(); i++) {
            UIDL childUidl = uidl.getChildUIDL(i);
            if (childUidl.getTag().equals("actions")) {
                if (getWidget().shortcutHandler == null) {
                    getWidget().shortcutHandler = new ShortcutActionHandler(
                            getConnectorId(), client);
                }
                getWidget().shortcutHandler.updateActionMap(childUidl);
            }

        }

        // setting scrollposition must happen after children is rendered
        getWidget().contentPanel.setScrollPosition(getState().scrollTop);
        getWidget().contentPanel
                .setHorizontalScrollPosition(getState().scrollLeft);

        // Center this window on screen if requested
        // This had to be here because we might not know the content size before
        // everything is painted into the window

        // centered is this is unset on move/resize
        getWidget().centered = getState().centered;
        getWidget().setVisible(true);

        // ensure window is not larger than browser window
        if (getWidget().getOffsetWidth() > Window.getClientWidth()) {
            getWidget().setWidth(Window.getClientWidth() + "px");
        }
        if (getWidget().getOffsetHeight() > Window.getClientHeight()) {
            getWidget().setHeight(Window.getClientHeight() + "px");
        }

        if (uidl.hasAttribute("bringToFront")) {
            /*
             * Focus as a side-effect. Will be overridden by
             * ApplicationConnection if another component was focused by the
             * server side.
             */
            getWidget().contentPanel.focus();
            getWidget().bringToFrontSequence = uidl
                    .getIntAttribute("bringToFront");
            VWindow.deferOrdering();
        }
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP, window has own caption, layout caption not rendered
    }

    @Override
    public void onBeforeShortcutAction(Event e) {
        // NOP, nothing to update just avoid workaround ( causes excess
        // blur/focus )
    }

    @Override
    public VWindow getWidget() {
        return (VWindow) super.getWidget();
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        // We always have 1 child, unless the child is hidden
        Widget newChildWidget = null;
        ComponentConnector newChild = null;
        if (getChildComponents().size() == 1) {
            newChild = getChildComponents().get(0);
            newChildWidget = newChild.getWidget();
        }

        getWidget().layout = newChild;
        getWidget().contentPanel.setWidget(newChildWidget);
    }

    @Override
    public void layout() {
        LayoutManager lm = getLayoutManager();
        VWindow window = getWidget();
        ComponentConnector layout = window.layout;
        Element contentElement = window.contentPanel.getElement();

        if (!minWidthChecked) {
            boolean needsMinWidth = !isUndefinedWidth()
                    || layout.isRelativeWidth();
            int minWidth = window.getMinWidth();
            if (needsMinWidth && lm.getInnerWidth(contentElement) < minWidth) {
                minWidthChecked = true;
                // Use minimum width if less than a certain size
                window.setWidth(minWidth + "px");
            }
            minWidthChecked = true;
        }

        boolean needsMinHeight = !isUndefinedHeight()
                || layout.isRelativeHeight();
        int minHeight = window.getMinHeight();
        if (needsMinHeight && lm.getInnerHeight(contentElement) < minHeight) {
            // Use minimum height if less than a certain size
            window.setHeight(minHeight + "px");
        }

        Style contentStyle = window.contents.getStyle();

        int headerHeight = lm.getOuterHeight(window.header);
        contentStyle.setPaddingTop(headerHeight, Unit.PX);
        contentStyle.setMarginTop(-headerHeight, Unit.PX);

        int footerHeight = lm.getOuterHeight(window.footer);
        contentStyle.setPaddingBottom(footerHeight, Unit.PX);
        contentStyle.setMarginBottom(-footerHeight, Unit.PX);

        /*
         * Must set absolute position if the child has relative height and
         * there's a chance of horizontal scrolling as some browsers will
         * otherwise not take the scrollbar into account when calculating the
         * height.
         */
        Element layoutElement = layout.getWidget().getElement();
        Style childStyle = layoutElement.getStyle();
        if (layout.isRelativeHeight() && !BrowserInfo.get().isIE9()) {
            childStyle.setPosition(Position.ABSOLUTE);

            Style wrapperStyle = contentElement.getStyle();
            if (window.getElement().getStyle().getWidth().length() == 0
                    && !layout.isRelativeWidth()) {
                /*
                 * Need to lock width to make undefined width work even with
                 * absolute positioning
                 */
                int contentWidth = lm.getOuterWidth(layoutElement);
                wrapperStyle.setWidth(contentWidth, Unit.PX);
            } else {
                wrapperStyle.clearWidth();
            }
        } else {
            childStyle.clearPosition();
        }
    }

    @Override
    public void postLayout() {
        minWidthChecked = false;
        VWindow window = getWidget();
        if (window.centered) {
            window.center();
        }
        window.positionOrSizeUpdated();
    }

    @Override
    public WindowState getState() {
        return (WindowState) super.getState();
    }

    /**
     * Gives the WindowConnector an order number. As a side effect, moves the
     * window according to its order number so the windows are stacked. This
     * method should be called for each window in the order they should appear.
     */
    public void setWindowOrderAndPosition() {
        getWidget().setWindowOrderAndPosition();
    }
}
