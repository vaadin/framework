/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.ClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.terminal.gwt.client.ui.layout.MayScrollChildren;

@Connect(value = com.vaadin.ui.Window.class)
public class WindowConnector extends AbstractComponentContainerConnector
        implements Paintable, BeforeShortcutActionListener,
        SimpleManagedLayout, PostLayoutListener, MayScrollChildren {

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            rpc.click(mouseDetails);
        }
    };

    private WindowServerRpc rpc;

    boolean minWidthChecked = false;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    };

    @Override
    protected void init() {
        super.init();
        rpc = RpcProxy.create(WindowServerRpc.class, this);

        getLayoutManager().registerDependency(this,
                getWidget().contentPanel.getElement());
        getLayoutManager().registerDependency(this, getWidget().header);
        getLayoutManager().registerDependency(this, getWidget().footer);
    }

    @Override
    public void onUnregister() {
        LayoutManager lm = getLayoutManager();
        VWindow window = getWidget();
        lm.unregisterDependency(this, window.contentPanel.getElement());
        lm.unregisterDependency(this, window.header);
        lm.unregisterDependency(this, window.footer);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().id = getConnectorId();
        getWidget().client = client;

        // Workaround needed for Testing Tools (GWT generates window DOM
        // slightly different in different browsers).
        DOM.setElementProperty(getWidget().closeBox, "id", getConnectorId()
                + "_window_close");

        if (isRealUpdate(uidl)) {
            if (getState().isModal() != getWidget().vaadinModality) {
                getWidget().setVaadinModality(!getWidget().vaadinModality);
            }
            if (!getWidget().isAttached()) {
                getWidget().setVisible(false); // hide until
                                               // possible centering
                getWidget().show();
            }
            if (getState().isResizable() != getWidget().resizable) {
                getWidget().setResizable(getState().isResizable());
            }
            getWidget().resizeLazy = getState().isResizeLazy();

            getWidget().setDraggable(getState().isDraggable());

            // Caption must be set before required header size is measured. If
            // the caption attribute is missing the caption should be cleared.
            String iconURL = null;
            if (getState().getIcon() != null) {
                iconURL = getState().getIcon().getURL();
            }
            getWidget().setCaption(getState().getCaption(), iconURL);
        }

        getWidget().visibilityChangesDisabled = true;
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().visibilityChangesDisabled = false;

        clickEventHandler.handleEventHandlerRegistration();

        getWidget().immediate = getState().isImmediate();

        getWidget().setClosable(!isReadOnly());

        // Initialize the position form UIDL
        int positionx = getState().getPositionX();
        int positiony = getState().getPositionY();
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
        getWidget().contentPanel.setScrollPosition(getState().getScrollTop());
        getWidget().contentPanel.setHorizontalScrollPosition(getState()
                .getScrollLeft());

        // Center this window on screen if requested
        // This had to be here because we might not know the content size before
        // everything is painted into the window

        // centered is this is unset on move/resize
        getWidget().centered = getState().isCentered();
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

    public void updateCaption(ComponentConnector component) {
        // NOP, window has own caption, layout caption not rendered
    }

    public void onBeforeShortcutAction(Event e) {
        // NOP, nothing to update just avoid workaround ( causes excess
        // blur/focus )
    }

    @Override
    public VWindow getWidget() {
        return (VWindow) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VWindow.class);
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        // We always have 1 child, unless the child is hidden
        Widget newChildWidget = null;
        ComponentConnector newChild = null;
        if (getChildren().size() == 1) {
            newChild = getChildren().get(0);
            newChildWidget = newChild.getWidget();
        }

        getWidget().layout = newChild;
        getWidget().contentPanel.setWidget(newChildWidget);
    }

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

    public void postLayout() {
        minWidthChecked = false;
        VWindow window = getWidget();
        if (window.centered) {
            window.center();
        }
        window.updateShadowSizeAndPosition();
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
