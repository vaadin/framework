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
package com.vaadin.client.ui.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Focusable;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.VUI;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIServerRpc;
import com.vaadin.shared.ui.ui.UIState;
import com.vaadin.ui.UI;

@Connect(value = UI.class, loadStyle = LoadStyle.EAGER)
public class UIConnector extends AbstractSingleComponentContainerConnector
        implements Paintable, MayScrollChildren {

    private HandlerRegistration childStateChangeHandlerRegistration;

    private final StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        @Override
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            // TODO Should use a more specific handler that only reacts to
            // size changes
            onChildSizeChange();
        }
    };

    @Override
    protected void init() {
        super.init();
        registerRpc(PageClientRpc.class, new PageClientRpc() {
            @Override
            public void setTitle(String title) {
                com.google.gwt.user.client.Window.setTitle(title);
            }
        });
        getWidget().addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                getRpcProxy(UIServerRpc.class).resize(event.getHeight(),
                        event.getWidth(), Window.getClientWidth(),
                        Window.getClientHeight());
                if (getState().immediate) {
                    getConnection().sendPendingVariableChanges();
                }
            }
        });
    }

    @Override
    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        ConnectorMap paintableMap = ConnectorMap.get(getConnection());
        getWidget().rendering = true;
        getWidget().id = getConnectorId();
        boolean firstPaint = getWidget().connection == null;
        getWidget().connection = client;

        getWidget().immediate = getState().immediate;
        getWidget().resizeLazy = uidl.hasAttribute(UIConstants.RESIZE_LAZY);
        String newTheme = uidl.getStringAttribute("theme");
        if (getWidget().theme != null && !newTheme.equals(getWidget().theme)) {
            // Complete page refresh is needed due css can affect layout
            // calculations etc
            getWidget().reloadHostPage();
        } else {
            getWidget().theme = newTheme;
        }
        // this also implicitly removes old styles
        String styles = "";
        styles += getWidget().getStylePrimaryName() + " ";
        if (ComponentStateUtil.hasStyles(getState())) {
            for (String style : getState().styles) {
                styles += style + " ";
            }
        }
        if (!client.getConfiguration().isStandalone()) {
            styles += getWidget().getStylePrimaryName() + "-embedded";
        }
        getWidget().setStyleName(styles.trim());

        getWidget().makeScrollable();

        clickEventHandler.handleEventHandlerRegistration();

        // Process children
        int childIndex = 0;

        // Open URL:s
        boolean isClosed = false; // was this window closed?
        while (childIndex < uidl.getChildCount()
                && "open".equals(uidl.getChildUIDL(childIndex).getTag())) {
            final UIDL open = uidl.getChildUIDL(childIndex);
            final String url = client.translateVaadinUri(open
                    .getStringAttribute("src"));
            final String target = open.getStringAttribute("name");
            if (target == null) {
                // source will be opened to this browser window, but we may have
                // to finish rendering this window in case this is a download
                // (and window stays open).
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        VUI.goTo(url);
                    }
                });
            } else if ("_self".equals(target)) {
                // This window is closing (for sure). Only other opens are
                // relevant in this change. See #3558, #2144
                isClosed = true;
                VUI.goTo(url);
            } else {
                String options;
                if (open.hasAttribute("border")) {
                    if (open.getStringAttribute("border").equals("minimal")) {
                        options = "menubar=yes,location=no,status=no";
                    } else {
                        options = "menubar=no,location=no,status=no";
                    }

                } else {
                    options = "resizable=yes,menubar=yes,toolbar=yes,directories=yes,location=yes,scrollbars=yes,status=yes";
                }

                if (open.hasAttribute("width")) {
                    int w = open.getIntAttribute("width");
                    options += ",width=" + w;
                }
                if (open.hasAttribute("height")) {
                    int h = open.getIntAttribute("height");
                    options += ",height=" + h;
                }

                Window.open(url, target, options);
            }
            childIndex++;
        }
        if (isClosed) {
            // don't render the content, something else will be opened to this
            // browser view
            getWidget().rendering = false;
            return;
        }

        // Handle other UIDL children
        UIDL childUidl;
        while ((childUidl = uidl.getChildUIDL(childIndex++)) != null) {
            String tag = childUidl.getTag().intern();
            if (tag == "actions") {
                if (getWidget().actionHandler == null) {
                    getWidget().actionHandler = new ShortcutActionHandler(
                            getWidget().id, client);
                }
                getWidget().actionHandler.updateActionMap(childUidl);
            } else if (tag == "notifications") {
                for (final Iterator<?> it = childUidl.getChildIterator(); it
                        .hasNext();) {
                    final UIDL notification = (UIDL) it.next();
                    VNotification.showNotification(client, notification);
                }
            }
        }

        if (uidl.hasAttribute("focused")) {
            // set focused component when render phase is finished
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    ComponentConnector paintable = (ComponentConnector) uidl
                            .getPaintableAttribute("focused", getConnection());

                    final Widget toBeFocused = paintable.getWidget();
                    /*
                     * Two types of Widgets can be focused, either implementing
                     * GWT HasFocus of a thinner Vaadin specific Focusable
                     * interface.
                     */
                    if (toBeFocused instanceof com.google.gwt.user.client.ui.Focusable) {
                        final com.google.gwt.user.client.ui.Focusable toBeFocusedWidget = (com.google.gwt.user.client.ui.Focusable) toBeFocused;
                        toBeFocusedWidget.setFocus(true);
                    } else if (toBeFocused instanceof Focusable) {
                        ((Focusable) toBeFocused).focus();
                    } else {
                        VConsole.log("Could not focus component");
                    }
                }
            });
        }

        // Add window listeners on first paint, to prevent premature
        // variablechanges
        if (firstPaint) {
            Window.addWindowClosingHandler(getWidget());
            Window.addResizeHandler(getWidget());
        }

        // finally set scroll position from UIDL
        if (uidl.hasVariable("scrollTop")) {
            getWidget().scrollable = true;
            getWidget().scrollTop = uidl.getIntVariable("scrollTop");
            DOM.setElementPropertyInt(getWidget().getElement(), "scrollTop",
                    getWidget().scrollTop);
            getWidget().scrollLeft = uidl.getIntVariable("scrollLeft");
            DOM.setElementPropertyInt(getWidget().getElement(), "scrollLeft",
                    getWidget().scrollLeft);
        } else {
            getWidget().scrollable = false;
        }

        if (uidl.hasAttribute("scrollTo")) {
            final ComponentConnector connector = (ComponentConnector) uidl
                    .getPaintableAttribute("scrollTo", getConnection());
            scrollIntoView(connector);
        }

        if (uidl.hasAttribute(UIConstants.LOCATION_VARIABLE)) {
            String location = uidl
                    .getStringAttribute(UIConstants.LOCATION_VARIABLE);
            int fragmentIndex = location.indexOf('#');
            if (fragmentIndex >= 0) {
                getWidget().currentFragment = location
                        .substring(fragmentIndex + 1);
            }
            if (!getWidget().currentFragment.equals(History.getToken())) {
                History.newItem(getWidget().currentFragment, true);
            }
        }

        if (firstPaint) {
            // Queue the initial window size to be sent with the following
            // request.
            getWidget().sendClientResized();
        }
        getWidget().rendering = false;
    }

    public void init(String rootPanelId,
            ApplicationConnection applicationConnection) {
        DOM.sinkEvents(getWidget().getElement(), Event.ONKEYDOWN
                | Event.ONSCROLL);

        // iview is focused when created so element needs tabIndex
        // 1 due 0 is at the end of natural tabbing order
        DOM.setElementProperty(getWidget().getElement(), "tabIndex", "1");

        RootPanel root = RootPanel.get(rootPanelId);

        // Remove the v-app-loading or any splash screen added inside the div by
        // the user
        root.getElement().setInnerHTML("");

        String themeName = applicationConnection.getConfiguration()
                .getThemeName();
        // Remove chars that are not suitable for style names
        themeName = themeName.replaceAll("[^a-zA-Z0-9]", "");
        root.addStyleName(themeName);

        root.add(getWidget());

        if (applicationConnection.getConfiguration().isStandalone()) {
            // set focus to iview element by default to listen possible keyboard
            // shortcuts. For embedded applications this is unacceptable as we
            // don't want to steal focus from the main page nor we don't want
            // side-effects from focusing (scrollIntoView).
            getWidget().getElement().focus();
        }
    }

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(UIServerRpc.class).click(mouseDetails);
        }

    };

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP The main view never draws caption for its layout
    }

    @Override
    public VUI getWidget() {
        return (VUI) super.getWidget();
    }

    protected void onChildSizeChange() {
        ComponentConnector child = getContent();
        if (child == null) {
            return;
        }
        Style childStyle = child.getWidget().getElement().getStyle();
        /*
         * Must set absolute position if the child has relative height and
         * there's a chance of horizontal scrolling as some browsers will
         * otherwise not take the scrollbar into account when calculating the
         * height. Assuming v-ui does not have an undefined width for now, see
         * #8460.
         */
        if (child.isRelativeHeight() && !BrowserInfo.get().isIE9()) {
            childStyle.setPosition(Position.ABSOLUTE);
        } else {
            childStyle.clearPosition();
        }
    }

    /**
     * Checks if the given sub window is a child of this UI Connector
     * 
     * @deprecated Should be replaced by a more generic mechanism for getting
     *             non-ComponentConnector children
     * @param wc
     * @return
     */
    @Deprecated
    public boolean hasSubWindow(WindowConnector wc) {
        return getChildComponents().contains(wc);
    }

    /**
     * Return an iterator for current subwindows. This method is meant for
     * testing purposes only.
     * 
     * @return
     */
    public List<WindowConnector> getSubWindows() {
        ArrayList<WindowConnector> windows = new ArrayList<WindowConnector>();
        for (ComponentConnector child : getChildComponents()) {
            if (child instanceof WindowConnector) {
                windows.add((WindowConnector) child);
            }
        }
        return windows;
    }

    @Override
    public UIState getState() {
        return (UIState) super.getState();
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        ComponentConnector oldChild = null;
        ComponentConnector newChild = getContent();

        for (ComponentConnector c : event.getOldChildren()) {
            if (!(c instanceof WindowConnector)) {
                oldChild = c;
                break;
            }
        }

        if (oldChild != newChild) {
            if (childStateChangeHandlerRegistration != null) {
                childStateChangeHandlerRegistration.removeHandler();
                childStateChangeHandlerRegistration = null;
            }
            if (newChild != null) {
                getWidget().setWidget(newChild.getWidget());
                childStateChangeHandlerRegistration = newChild
                        .addStateChangeHandler(childStateChangeHandler);
                // Must handle new child here as state change events are already
                // fired
                onChildSizeChange();
            } else {
                getWidget().setWidget(null);
            }
        }

        for (ComponentConnector c : getChildComponents()) {
            if (c instanceof WindowConnector) {
                WindowConnector wc = (WindowConnector) c;
                wc.setWindowOrderAndPosition();
            }
        }

        // Close removed sub windows
        for (ComponentConnector c : event.getOldChildren()) {
            if (c.getParent() != this && c instanceof WindowConnector) {
                ((WindowConnector) c).getWidget().hide();
            }
        }
    }

    /**
     * Tries to scroll the viewport so that the given connector is in view.
     * 
     * @param componentConnector
     *            The connector which should be visible
     * 
     */
    public void scrollIntoView(final ComponentConnector componentConnector) {
        if (componentConnector == null) {
            return;
        }

        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                componentConnector.getWidget().getElement().scrollIntoView();
            }
        });
    }
}
