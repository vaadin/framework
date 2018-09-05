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
package com.vaadin.client.ui.window;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VWindow;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.shared.ui.window.WindowServerRpc;
import com.vaadin.shared.ui.window.WindowState;

@Connect(value = com.vaadin.ui.Window.class)
public class WindowConnector extends AbstractSingleComponentContainerConnector
        implements Paintable, SimpleManagedLayout, PostLayoutListener,
        MayScrollChildren, WindowMoveHandler {

    private Node windowClone;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(WindowServerRpc.class).click(mouseDetails);
        }
    };

    abstract class WindowEventHandler
            implements ClickHandler, DoubleClickHandler {
    }

    private WindowEventHandler maximizeRestoreClickHandler = new WindowEventHandler() {

        @Override
        public void onClick(ClickEvent event) {
            final Element target = event.getNativeEvent().getEventTarget()
                    .cast();
            if (target == getWidget().maximizeRestoreBox) {
                // Click on maximize/restore box
                onMaximizeRestore();
            }
        }

        @Override
        public void onDoubleClick(DoubleClickEvent event) {
            final Element target = event.getNativeEvent().getEventTarget()
                    .cast();
            if (getWidget().header.isOrHasChild(target)) {
                // Double click on header
                onMaximizeRestore();
            }
        }
    };

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        VWindow window = getWidget();
        window.id = getConnectorId();
        window.client = getConnection();
        window.connector = this;

        getLayoutManager().registerDependency(this,
                window.contentPanel.getElement());
        getLayoutManager().registerDependency(this, window.header);
        getLayoutManager().registerDependency(this, window.footer);

        window.addHandler(maximizeRestoreClickHandler, ClickEvent.getType());
        window.addHandler(maximizeRestoreClickHandler,
                DoubleClickEvent.getType());

        window.setOwner(getConnection().getUIConnector().getWidget());

        window.addMoveHandler(this);
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

        VWindow window = getWidget();
        String connectorId = getConnectorId();

        // Workaround needed for Testing Tools (GWT generates window DOM
        // slightly different in different browsers).
        window.closeBox.setId(connectorId + "_window_close");
        window.maximizeRestoreBox
                .setId(connectorId + "_window_maximizerestore");

        window.visibilityChangesDisabled = true;
        if (!isRealUpdate(uidl)) {
            return;
        }
        window.visibilityChangesDisabled = false;

        // we may have actions
        for (int i = 0; i < uidl.getChildCount(); i++) {
            UIDL childUidl = uidl.getChildUIDL(i);
            if (childUidl.getTag().equals("actions")) {
                if (window.shortcutHandler == null) {
                    window.shortcutHandler = new ShortcutActionHandler(
                            connectorId, client);
                }
                window.shortcutHandler.updateActionMap(childUidl);
            }

        }

        if (uidl.hasAttribute("bringToFront")) {
            /*
             * Focus as a side-effect. Will be overridden by
             * ApplicationConnection if another component was focused by the
             * server side.
             */
            window.contentPanel.focus();
            window.bringToFrontSequence = uidl.getIntAttribute("bringToFront");
            VWindow.deferOrdering();
        }
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP, window has own caption, layout caption not rendered
    }

    @Override
    public VWindow getWidget() {
        return (VWindow) super.getWidget();
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        // We always have 1 child, unless the child is hidden
        getWidget().contentPanel.setWidget(getContentWidget());

        if (getParent() == null && windowClone != null) {
            // If the window is removed from the UI, add the copy of the
            // contents to the window (in case of an 'out-animation')
            getWidget().getElement().removeAllChildren();
            getWidget().getElement().appendChild(windowClone);

            // Clean reference
            windowClone = null;
        }

    }

    @Override
    public void layout() {
        LayoutManager lm = getLayoutManager();
        VWindow window = getWidget();

        // ensure window is not larger than browser window
        if (window.getOffsetWidth() > Window.getClientWidth()) {
            window.setWidth(Window.getClientWidth() + "px");
            lm.setNeedsMeasure(getContent());
        }
        if (window.getOffsetHeight() > Window.getClientHeight()) {
            window.setHeight(Window.getClientHeight() + "px");
            lm.setNeedsMeasure(getContent());
        }

        ComponentConnector content = getContent();
        boolean hasContent = content != null;
        Element contentElement = window.contentPanel.getElement();

        Style contentStyle = window.contents.getStyle();

        int headerHeight = lm.getOuterHeight(window.header);
        contentStyle.setPaddingTop(headerHeight, Unit.PX);
        contentStyle.setMarginTop(-headerHeight, Unit.PX);

        int footerHeight = lm.getOuterHeight(window.footer);
        contentStyle.setPaddingBottom(footerHeight, Unit.PX);
        contentStyle.setMarginBottom(-footerHeight, Unit.PX);

        int minWidth = lm.getOuterWidth(window.header)
                - lm.getInnerWidth(window.header);
        int minHeight = footerHeight + headerHeight;

        getWidget().getElement().getStyle().setPropertyPx("minWidth", minWidth);
        getWidget().getElement().getStyle().setPropertyPx("minHeight",
                minHeight);

        /*
         * Must set absolute position if the child has relative height and
         * there's a chance of horizontal scrolling as some browsers will
         * otherwise not take the scrollbar into account when calculating the
         * height.
         */
        if (hasContent) {
            Element layoutElement = content.getWidget().getElement();
            Style childStyle = layoutElement.getStyle();

            if (content.isRelativeHeight()) {
                childStyle.setPosition(Position.ABSOLUTE);

                Style wrapperStyle = contentElement.getStyle();
                if (window.getElement().getStyle().getWidth().isEmpty()
                        && !content.isRelativeWidth()) {
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

    }

    @Override
    public void postLayout() {
        VWindow window = getWidget();

        if (!window.isAttached()) {
            Logger.getLogger(WindowConnector.class.getName())
                    .warning("Called postLayout to detached Window.");
            return;
        }
        if (window.centered && getState().windowMode != WindowMode.MAXIMIZED) {
            window.center();
        }
        window.positionOrSizeUpdated();

        if (getParent() != null) {
            // Take a copy of the contents, since the server will detach all
            // children of this window when it's closed, and the window will be
            // emptied during the following hierarchy update (we need to keep
            // the contents visible for the duration of a possible
            // 'out-animation')

            // Fix for #14645 and #14785 - as soon as we clone audio and video
            // tags, they start fetching data, and playing immediately in
            // background, in case autoplay attribute is present. Therefore we
            // have to replace them with stubs in the clone. And we can't just
            // erase them, because there are corresponding player widgets to
            // animate
            windowClone = cloneNodeFilteringMedia(
                    getWidget().getElement().getFirstChild());
        }
    }

    private Node cloneNodeFilteringMedia(Node node) {
        if (node instanceof Element) {
            Element old = (Element) node;
            if ("audio".equalsIgnoreCase(old.getTagName())
                    || "video".equalsIgnoreCase(old.getTagName())) {
                if (!old.hasAttribute("controls")
                        && "audio".equalsIgnoreCase(old.getTagName())) {
                    // nothing to animate, so we won't add this to
                    // the clone
                    return null;
                }
                Element newEl = DOM.createElement(old.getTagName());
                if (old.hasAttribute("controls")) {
                    newEl.setAttribute("controls",
                            old.getAttribute("controls"));
                }
                if (old.hasAttribute("style")) {
                    newEl.setAttribute("style", old.getAttribute("style"));
                }
                if (old.hasAttribute("class")) {
                    newEl.setAttribute("class", old.getAttribute("class"));
                }
                return newEl;
            }
        }
        Node res = node.cloneNode(false);
        if (node.hasChildNodes()) {
            NodeList<Node> nl = node.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node clone = cloneNodeFilteringMedia(nl.getItem(i));
                if (clone != null) {
                    res.appendChild(clone);
                }
            }
        }
        return res;
    }

    @Override
    public WindowState getState() {
        return (WindowState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        VWindow window = getWidget();
        WindowState state = getState();

        if (state.modal != window.vaadinModality) {
            window.setVaadinModality(!window.vaadinModality);
        }
        boolean resizeable = state.resizable
                && state.windowMode == WindowMode.NORMAL;
        window.setResizable(resizeable);

        window.resizeLazy = state.resizeLazy;

        window.setDraggable(
                state.draggable && state.windowMode == WindowMode.NORMAL);

        window.updateMaximizeRestoreClassName(state.resizable,
                state.windowMode);

        // Caption must be set before required header size is measured. If
        // the caption attribute is missing the caption should be cleared.
        String iconURL = null;
        if (getIconUri() != null) {
            iconURL = getIconUri();
        }

        window.setAssistivePrefix(state.assistivePrefix);
        window.setAssistivePostfix(state.assistivePostfix);
        window.setCaption(state.caption, iconURL, getState().captionAsHtml);

        window.setWaiAriaRole(getState().role);
        window.setAssistiveDescription(state.contentDescription);

        window.setTabStopEnabled(getState().assistiveTabStop);
        window.setTabStopTopAssistiveText(getState().assistiveTabStopTopText);
        window.setTabStopBottomAssistiveText(
                getState().assistiveTabStopBottomText);

        clickEventHandler.handleEventHandlerRegistration();

        window.setClosable(state.closable);
        // initialize position from state
        updateWindowPosition();

        // setting scrollposition must happen after children is rendered
        window.contentPanel.setScrollPosition(state.scrollTop);
        window.contentPanel.setHorizontalScrollPosition(state.scrollLeft);

        // Center this window on screen if requested
        // This had to be here because we might not know the content size before
        // everything is painted into the window

        // centered is this is unset on move/resize
        window.centered = state.centered;
        // Ensure centering before setting visible (#16486)
        if (window.centered && getState().windowMode != WindowMode.MAXIMIZED) {
            Scheduler.get().scheduleFinally(() -> getWidget().center());
        }
        window.setVisible(true);
    }

    // Need to override default because of window mode
    @Override
    protected void updateComponentSize() {
        if (getState().windowMode == WindowMode.NORMAL) {
            super.updateComponentSize();
        } else if (getState().windowMode == WindowMode.MAXIMIZED) {
            super.updateComponentSize("100%", "100%");
        }
    }

    protected void updateWindowPosition() {
        VWindow window = getWidget();
        WindowState state = getState();
        if (state.windowMode == WindowMode.NORMAL) {
            // if centered, position handled in postLayout()
            if (!state.centered
                    && (state.positionX >= 0 || state.positionY >= 0)) {
                // If both positions are negative, then
                // setWindowOrderAndPosition has already taken care of
                // positioning the window so it stacks with other windows
                window.setPopupPosition(state.positionX, state.positionY);
            }
        } else if (state.windowMode == WindowMode.MAXIMIZED) {
            window.setPopupPositionNoUpdate(0, 0);
        }
    }

    protected void updateWindowMode() {
        VWindow window = getWidget();
        WindowState state = getState();

        // update draggable on widget
        window.setDraggable(
                state.draggable && state.windowMode == WindowMode.NORMAL);
        // update resizable on widget
        window.setResizable(
                state.resizable && state.windowMode == WindowMode.NORMAL);
        updateComponentSize();
        updateWindowPosition();
        window.updateMaximizeRestoreClassName(state.resizable,
                state.windowMode);
        window.updateContentsSize();
    }

    protected void onMaximizeRestore() {
        WindowState state = getState();
        if (state.resizable) {
            if (state.windowMode == WindowMode.MAXIMIZED) {
                state.windowMode = WindowMode.NORMAL;
            } else {
                state.windowMode = WindowMode.MAXIMIZED;
            }
            updateWindowMode();

            VWindow window = getWidget();
            window.bringToFront();

            getRpcProxy(WindowServerRpc.class)
                    .windowModeChanged(state.windowMode);
        }
    }

    /**
     * Gives the WindowConnector an order number. As a side effect, moves the
     * window according to its order number so the windows are stacked. This
     * method should be called for each window in the order they should appear.
     */
    public void setWindowOrderAndPosition() {
        getWidget().setWindowOrderAndPosition();
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Tooltip event handler always needed on the window widget to make sure
         * tooltips are properly hidden. (#11448)
         */
        return true;
    }

    @Override
    public void onWindowMove(WindowMoveEvent event) {
        getRpcProxy(WindowServerRpc.class).windowMoved(event.getNewX(),
                event.getNewY());
    }
}
