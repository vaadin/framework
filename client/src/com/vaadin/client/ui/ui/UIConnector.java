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
package com.vaadin.client.ui.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedEvent;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Focusable;
import com.vaadin.client.Paintable;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.ValueMap;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VUI;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.server.Page.Styles;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.ui.DebugWindowClientRpc;
import com.vaadin.shared.ui.ui.DebugWindowServerRpc;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.shared.ui.ui.ScrollClientRpc;
import com.vaadin.shared.ui.ui.UIClientRpc;
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
            public void reload() {
                Window.Location.reload();

            }
        });
        registerRpc(ScrollClientRpc.class, new ScrollClientRpc() {
            @Override
            public void setScrollTop(int scrollTop) {
                getWidget().getElement().setScrollTop(scrollTop);
            }

            @Override
            public void setScrollLeft(int scrollLeft) {
                getWidget().getElement().setScrollLeft(scrollLeft);
            }
        });
        registerRpc(UIClientRpc.class, new UIClientRpc() {
            @Override
            public void uiClosed(final boolean sessionExpired) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        // Only notify user if we're still running and not eg.
                        // navigating away (#12298)
                        if (getConnection().isApplicationRunning()) {
                            if (sessionExpired) {
                                getConnection().showSessionExpiredError(null);
                            } else {
                                getState().enabled = false;
                                updateEnabledState(getState().enabled);
                            }
                            getConnection().setApplicationRunning(false);
                        }
                    }
                });
            }
        });
        registerRpc(DebugWindowClientRpc.class, new DebugWindowClientRpc() {

            @Override
            public void reportLayoutProblems(String json) {
                VConsole.printLayoutProblems(getValueMap(json), getConnection());
            }

            private native ValueMap getValueMap(String json)
            /*-{
                return JSON.parse(json);
            }-*/;
        });

        getWidget().addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                getRpcProxy(UIServerRpc.class).resize(event.getHeight(),
                        event.getWidth(), Window.getClientWidth(),
                        Window.getClientHeight());
                if (getState().immediate || getPageState().hasResizeListeners) {
                    getConnection().sendPendingVariableChanges();
                }
            }
        });
        getWidget().addScrollHandler(new ScrollHandler() {
            private int lastSentScrollTop = Integer.MAX_VALUE;
            private int lastSentScrollLeft = Integer.MAX_VALUE;

            @Override
            public void onScroll(ScrollEvent event) {
                Element element = getWidget().getElement();
                int newScrollTop = element.getScrollTop();
                int newScrollLeft = element.getScrollLeft();
                if (newScrollTop != lastSentScrollTop
                        || newScrollLeft != lastSentScrollLeft) {
                    lastSentScrollTop = newScrollTop;
                    lastSentScrollLeft = newScrollLeft;
                    getRpcProxy(UIServerRpc.class).scroll(newScrollTop,
                            newScrollLeft);
                }
            }
        });
    }

    private native void open(String url, String name)
    /*-{
        $wnd.open(url, name);
     }-*/;

    @Override
    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
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
                boolean alwaysAsPopup = true;
                if (open.hasAttribute("popup")) {
                    alwaysAsPopup = open.getBooleanAttribute("popup");
                }
                if (alwaysAsPopup) {
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
                } else {
                    open(url, target);
                }
            }
            childIndex++;
        }
        if (isClosed) {
            // We're navigating away, so stop the application.
            client.setApplicationRunning(false);
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
            } else if (tag == "css-injections") {
                injectCSS(childUidl);
            }
        }

        if (uidl.hasAttribute("focused")) {
            // set focused component when render phase is finished
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    ComponentConnector paintable = (ComponentConnector) uidl
                            .getPaintableAttribute("focused", getConnection());

                    if (paintable == null) {
                        // Do not try to focus invisible components which not
                        // present in UIDL
                        return;
                    }

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

        if (uidl.hasAttribute("scrollTo")) {
            final ComponentConnector connector = (ComponentConnector) uidl
                    .getPaintableAttribute("scrollTo", getConnection());
            scrollIntoView(connector);
        }

        if (uidl.hasAttribute(UIConstants.LOCATION_VARIABLE)) {
            String location = uidl
                    .getStringAttribute(UIConstants.LOCATION_VARIABLE);
            String newFragment;

            int fragmentIndex = location.indexOf('#');
            if (fragmentIndex >= 0) {
                // Decode fragment to avoid double encoding (#10769)
                newFragment = URL.decodePathSegment(location
                        .substring(fragmentIndex + 1));

                if (newFragment.isEmpty()
                        && Location.getHref().indexOf('#') == -1) {
                    // Ensure there is a trailing # even though History and
                    // Location.getHash() treat null and "" the same way.
                    Location.assign(Location.getHref() + "#");
                }
            } else {
                // No fragment in server-side location, but can't completely
                // remove the browser fragment since that would reload the page
                newFragment = "";
            }

            getWidget().currentFragment = newFragment;

            if (!newFragment.equals(History.getToken())) {
                History.newItem(newFragment, true);
            }
        }

        if (firstPaint) {
            // Queue the initial window size to be sent with the following
            // request.
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    getWidget().sendClientResized();
                }
            });
        }
    }

    /**
     * Reads CSS strings and resources injected by {@link Styles#inject} from
     * the UIDL stream.
     * 
     * @param uidl
     *            The uidl which contains "css-resource" and "css-string" tags
     */
    private void injectCSS(UIDL uidl) {

        final HeadElement head = HeadElement.as(Document.get()
                .getElementsByTagName(HeadElement.TAG).getItem(0));

        /*
         * Search the UIDL stream for CSS resources and strings to be injected.
         */
        for (Iterator<?> it = uidl.getChildIterator(); it.hasNext();) {
            UIDL cssInjectionsUidl = (UIDL) it.next();

            // Check if we have resources to inject
            if (cssInjectionsUidl.getTag().equals("css-resource")) {
                String url = getWidget().connection
                        .translateVaadinUri(cssInjectionsUidl
                                .getStringAttribute("url"));
                LinkElement link = LinkElement.as(DOM
                        .createElement(LinkElement.TAG));
                link.setRel("stylesheet");
                link.setHref(url);
                link.setType("text/css");
                head.appendChild(link);

                // Check if we have CSS string to inject
            } else if (cssInjectionsUidl.getTag().equals("css-string")) {
                for (Iterator<?> it2 = cssInjectionsUidl.getChildIterator(); it2
                        .hasNext();) {
                    StyleInjector.injectAtEnd((String) it2.next());
                    StyleInjector.flush();
                }
            }
        }
    }

    public void init(String rootPanelId,
            ApplicationConnection applicationConnection) {
        DOM.sinkEvents(getWidget().getElement(), Event.ONKEYDOWN
                | Event.ONSCROLL);

        RootPanel root = RootPanel.get(rootPanelId);

        // Remove the v-app-loading or any splash screen added inside the div by
        // the user
        root.getElement().setInnerHTML("");

        String themeName = applicationConnection.getConfiguration()
                .getThemeName();
        root.addStyleName(themeName);

        root.add(getWidget());

        // Set default tab index before focus call. State change handler
        // will update this later if needed.
        getWidget().setTabIndex(1);

        if (applicationConnection.getConfiguration().isStandalone()) {
            // set focus to iview element by default to listen possible keyboard
            // shortcuts. For embedded applications this is unacceptable as we
            // don't want to steal focus from the main page nor we don't want
            // side-effects from focusing (scrollIntoView).
            getWidget().getElement().focus();
        }

        applicationConnection.addHandler(
                ApplicationConnection.ApplicationStoppedEvent.TYPE,
                new ApplicationConnection.ApplicationStoppedHandler() {

                    @Override
                    public void onApplicationStopped(
                            ApplicationStoppedEvent event) {
                        // Stop any polling
                        if (pollTimer != null) {
                            pollTimer.cancel();
                            pollTimer = null;
                        }
                    }
                });
    }

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this) {

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(UIServerRpc.class).click(mouseDetails);
        }

    };

    private Timer pollTimer = null;

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP The main view never draws caption for its layout
    }

    @Override
    public VUI getWidget() {
        return (VUI) super.getWidget();
    }

    @Override
    protected ComponentConnector getContent() {
        ComponentConnector connector = super.getContent();
        // VWindow (WindowConnector is its connector)is also a child component
        // but it's never a content widget
        if (connector instanceof WindowConnector) {
            return null;
        } else {
            return connector;
        }
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

    /**
     * Returns the state of the Page associated with the UI.
     * <p>
     * Note that state is considered an internal part of the connector. You
     * should not rely on the state object outside of the connector who owns it.
     * If you depend on the state of other connectors you should use their
     * public API instead of their state object directly. The page state might
     * not be an independent state object but can be embedded in UI state.
     * </p>
     * 
     * @since 7.1
     * @return state object of the page
     */
    public PageState getPageState() {
        return getState().pageState;
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

    @Override
    public boolean hasTooltip() {
        /*
         * Always return true so there's always top level tooltip handler that
         * takes care of hiding tooltips whenever the mouse is moved somewhere
         * else.
         */
        return true;
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

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        if (stateChangeEvent.hasPropertyChanged("tooltipConfiguration")) {
            getConnection().getVTooltip().setCloseTimeout(
                    getState().tooltipConfiguration.closeTimeout);
            getConnection().getVTooltip().setOpenDelay(
                    getState().tooltipConfiguration.openDelay);
            getConnection().getVTooltip().setQuickOpenDelay(
                    getState().tooltipConfiguration.quickOpenDelay);
            getConnection().getVTooltip().setQuickOpenTimeout(
                    getState().tooltipConfiguration.quickOpenTimeout);
            getConnection().getVTooltip().setMaxWidth(
                    getState().tooltipConfiguration.maxWidth);
        }

        if (stateChangeEvent
                .hasPropertyChanged("loadingIndicatorConfiguration")) {
            getConnection().getLoadingIndicator().setFirstDelay(
                    getState().loadingIndicatorConfiguration.firstDelay);
            getConnection().getLoadingIndicator().setSecondDelay(
                    getState().loadingIndicatorConfiguration.secondDelay);
            getConnection().getLoadingIndicator().setThirdDelay(
                    getState().loadingIndicatorConfiguration.thirdDelay);
        }

        if (stateChangeEvent.hasPropertyChanged("pollInterval")) {
            configurePolling();
        }

        if (stateChangeEvent.hasPropertyChanged("pageState.title")) {
            String title = getState().pageState.title;
            if (title != null) {
                com.google.gwt.user.client.Window.setTitle(title);
            }
        }

        if (stateChangeEvent.hasPropertyChanged("pushConfiguration")) {
            getConnection().setPushEnabled(
                    getState().pushConfiguration.mode.isEnabled());
        }

        if (stateChangeEvent.hasPropertyChanged("overlayContainerLabel")) {
            VOverlay.setOverlayContainerLabel(getConnection(),
                    getState().overlayContainerLabel);
        }
    }

    private void configurePolling() {
        if (pollTimer != null) {
            pollTimer.cancel();
            pollTimer = null;
        }
        if (getState().pollInterval >= 0) {
            pollTimer = new Timer() {
                @Override
                public void run() {
                    if (getState().pollInterval < 0) {
                        // Polling has been cancelled server side
                        pollTimer.cancel();
                        pollTimer = null;
                        return;
                    }
                    getRpcProxy(UIServerRpc.class).poll();
                    // Send changes even though poll is @Delayed
                    getConnection().sendPendingVariableChanges();
                }
            };
            pollTimer.scheduleRepeating(getState().pollInterval);
        } else {
            // Ensure no more polls are sent as polling has been disabled
            getConnection().removePendingInvocations(
                    new MethodInvocation(getConnectorId(), UIServerRpc.class
                            .getName(), "poll"));
        }
    }

    /**
     * Invokes the layout analyzer on the server
     * 
     * @since 7.1
     */
    public void analyzeLayouts() {
        getRpcProxy(DebugWindowServerRpc.class).analyzeLayouts();
    }

    /**
     * Sends a request to the server to print details to console that will help
     * the developer to locate the corresponding server-side connector in the
     * source code.
     * 
     * @since 7.1
     * @param serverConnector
     *            the connector to locate
     */
    public void showServerDebugInfo(ServerConnector serverConnector) {
        getRpcProxy(DebugWindowServerRpc.class).showServerDebugInfo(
                serverConnector);
    }
}
