/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedEvent;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Focusable;
import com.vaadin.client.Paintable;
import com.vaadin.client.ResourceLoader;
import com.vaadin.client.ResourceLoader.ResourceLoadEvent;
import com.vaadin.client.ResourceLoader.ResourceLoadListener;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ValueMap;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.extensions.DragSourceExtensionConnector;
import com.vaadin.client.extensions.DropTargetExtensionConnector;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.ShortcutActionHandler;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.VUI;
import com.vaadin.client.ui.VWindow;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.client.ui.window.WindowOrderEvent;
import com.vaadin.client.ui.window.WindowOrderHandler;
import com.vaadin.server.Page.Styles;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Version;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.WindowOrderRpc;
import com.vaadin.shared.ui.ui.DebugWindowClientRpc;
import com.vaadin.shared.ui.ui.DebugWindowServerRpc;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.shared.ui.ui.ScrollClientRpc;
import com.vaadin.shared.ui.ui.UIClientRpc;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIServerRpc;
import com.vaadin.shared.ui.ui.UIState;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.UI;

import elemental.client.Browser;

@Connect(value = UI.class, loadStyle = LoadStyle.EAGER)
public class UIConnector extends AbstractSingleComponentContainerConnector
        implements Paintable, MayScrollChildren {

    private HandlerRegistration childStateChangeHandlerRegistration;

    private String activeTheme = null;

    private HandlerRegistration windowOrderRegistration;

    /*
     * Used to workaround IE bug related to popstate events and certain fragment
     * only changes
     */
    private String currentLocation;

    private final StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        @Override
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            // TODO Should use a more specific handler that only reacts to
            // size changes
            onChildSizeChange();
        }
    };

    private WindowOrderHandler windowOrderHandler = new WindowOrderHandler() {

        @Override
        public void onWindowOrderChange(WindowOrderEvent event) {
            VWindow[] windows = event.getWindows();
            HashMap<Integer, Connector> orders = new HashMap<>();
            boolean hasEventListener = hasEventListener(EventId.WINDOW_ORDER);
            for (VWindow window : windows) {
                Connector connector = Util.findConnectorFor(window);
                orders.put(window.getWindowOrder(), connector);
                if (connector instanceof AbstractConnector
                        && ((AbstractConnector) connector)
                                .hasEventListener(EventId.WINDOW_ORDER)) {
                    hasEventListener = true;
                }
            }
            if (hasEventListener) {
                getRpcProxy(WindowOrderRpc.class).windowOrderChanged(orders);
            }
        }
    };

    @Override
    protected void init() {
        super.init();
        windowOrderRegistration = VWindow
                .addWindowOrderHandler(windowOrderHandler);
        registerRpc(PageClientRpc.class, new PageClientRpc() {

            @Override
            public void reload() {
                Window.Location.reload();

            }

            @Override
            public void initializeMobileHtml5DndPolyfill() {
                initializeMobileDndPolyfill();
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
                VConsole.printLayoutProblems(getValueMap(json),
                        getConnection());
            }

            private native ValueMap getValueMap(String json)
            /*-{
                return JSON.parse(json);
            }-*/;
        });

        getWidget().addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                getRpcProxy(UIServerRpc.class).resize(event.getWidth(),
                        event.getHeight(), Window.getClientWidth(),
                        Window.getClientHeight());
                getConnection().getServerRpcQueue().flush();
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

        Browser.getWindow().setOnpopstate(evt -> {
            final String newLocation = Browser.getWindow().getLocation()
                    .toString();
            getRpcProxy(UIServerRpc.class).popstate(newLocation);
            currentLocation = newLocation;
        });
        // IE doesn't fire popstate correctly with certain hash changes.
        // Simulate the missing event with History handler.
        if (BrowserInfo.get().isIE()) {
            History.addValueChangeHandler(evt -> {
                final String newLocation = Browser.getWindow().getLocation()
                        .toString();
                if (!newLocation.equals(currentLocation)) {
                    currentLocation = newLocation;
                    getRpcProxy(UIServerRpc.class).popstate(
                            Browser.getWindow().getLocation().toString());
                }
            });
            currentLocation = Browser.getWindow().getLocation().toString();
        }

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

        getWidget().resizeLazy = uidl.hasAttribute(UIConstants.RESIZE_LAZY);
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
            final String url = client
                    .translateVaadinUri(open.getStringAttribute("src"));
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
                        if (open.getStringAttribute("border")
                                .equals("minimal")) {
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
                    ComponentConnector connector = (ComponentConnector) uidl
                            .getPaintableAttribute("focused", getConnection());

                    if (connector == null) {
                        // Do not try to focus invisible components which not
                        // present in UIDL
                        return;
                    }

                    final Widget toBeFocused = connector.getWidget();
                    /*
                     * Two types of Widgets can be focused, either implementing
                     * GWT Focusable of a thinner Vaadin specific Focusable
                     * interface.
                     */
                    if (toBeFocused instanceof com.google.gwt.user.client.ui.Focusable) {
                        final com.google.gwt.user.client.ui.Focusable toBeFocusedWidget = (com.google.gwt.user.client.ui.Focusable) toBeFocused;
                        toBeFocusedWidget.setFocus(true);
                    } else if (toBeFocused instanceof Focusable) {
                        ((Focusable) toBeFocused).focus();
                    } else {
                        getLogger()
                                .severe("Server is trying to set focus to the widget of connector "
                                        + Util.getConnectorString(connector)
                                        + " but it is not focusable. The widget should implement either "
                                        + com.google.gwt.user.client.ui.Focusable.class
                                                .getName()
                                        + " or " + Focusable.class.getName());
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

        if (uidl.hasAttribute(UIConstants.ATTRIBUTE_PUSH_STATE)) {
            Browser.getWindow().getHistory().pushState(null, "",
                    uidl.getStringAttribute(UIConstants.ATTRIBUTE_PUSH_STATE));
        }
        if (uidl.hasAttribute(UIConstants.ATTRIBUTE_REPLACE_STATE)) {
            Browser.getWindow().getHistory().replaceState(null, "", uidl
                    .getStringAttribute(UIConstants.ATTRIBUTE_REPLACE_STATE));
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

        /*
         * Search the UIDL stream for CSS resources and strings to be injected.
         */
        for (Iterator<?> it = uidl.getChildIterator(); it.hasNext();) {
            UIDL cssInjectionsUidl = (UIDL) it.next();

            // Check if we have resources to inject
            if (cssInjectionsUidl.getTag().equals("css-resource")) {
                String url = getWidget().connection.translateVaadinUri(
                        cssInjectionsUidl.getStringAttribute("url"));
                LinkElement link = LinkElement
                        .as(DOM.createElement(LinkElement.TAG));
                link.setRel("stylesheet");
                link.setHref(url);
                link.setType("text/css");
                getHead().appendChild(link);
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

    /**
     * Internal helper to get the <head> tag of the page
     *
     * @since 7.3
     * @return the head element
     */
    private HeadElement getHead() {
        return HeadElement.as(Document.get()
                .getElementsByTagName(HeadElement.TAG).getItem(0));
    }

    /**
     * Internal helper for removing any stylesheet with the given URL
     *
     * @since 7.3
     * @param url
     *            the url to match with existing stylesheets
     */
    private void removeStylesheet(String url) {
        NodeList<Element> linkTags = getHead()
                .getElementsByTagName(LinkElement.TAG);
        for (int i = 0; i < linkTags.getLength(); i++) {
            LinkElement link = LinkElement.as(linkTags.getItem(i));
            if (!"stylesheet".equals(link.getRel())) {
                continue;
            }
            if (!"text/css".equals(link.getType())) {
                continue;
            }
            if (url.equals(link.getHref())) {
                getHead().removeChild(link);
            }
        }
    }

    public void init(String rootPanelId,
            ApplicationConnection applicationConnection) {
        Widget shortcutContextWidget = getWidget();
        if (applicationConnection.getConfiguration().isStandalone()) {
            // Listen to body for standalone apps (#19392)
            shortcutContextWidget = RootPanel.get(); // document body
        }

        shortcutContextWidget.addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (VWindow.isModalWindowOpen()) {
                    return;
                }
                if (getWidget().actionHandler != null) {
                    Element target = Element
                            .as(event.getNativeEvent().getEventTarget());
                    if (target == Document.get().getBody()
                            || getWidget().getElement().isOrHasChild(target)) {
                        // Only react to body and elements inside the UI
                        getWidget().actionHandler.handleKeyboardEvent(
                                (Event) event.getNativeEvent().cast());
                    }

                }
            }
        }, KeyDownEvent.getType());

        DOM.sinkEvents(getWidget().getElement(), Event.ONSCROLL);

        RootPanel root = RootPanel.get(rootPanelId);

        // Remove the v-app-loading or any splash screen added inside the div by
        // the user
        root.getElement().setInnerHTML("");

        // Activate the initial theme by only adding the class name. Not calling
        // activateTheme here as it will also cause a full layout and updates to
        // the overlay container which has not yet been created at this point
        activeTheme = applicationConnection.getConfiguration().getThemeName();
        root.addStyleName(activeTheme);

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
        if (child.isRelativeHeight()) {
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
        ArrayList<WindowConnector> windows = new ArrayList<>();
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
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
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
                VWindow window = wc.getWidget();
                if (!window.isAttached()) {

                    // Attach so that all widgets inside the Window are attached
                    // when their onStateChange is run

                    // Made invisible here for legacy reasons and made visible
                    // at the end of stateChange. This dance could probably be
                    // removed
                    window.setVisible(false);
                    window.show();
                }

            }
        }

        setWindowOrderAndPosition();

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
            getConnection().getVTooltip()
                    .setOpenDelay(getState().tooltipConfiguration.openDelay);
            getConnection().getVTooltip().setQuickOpenDelay(
                    getState().tooltipConfiguration.quickOpenDelay);
            getConnection().getVTooltip().setQuickOpenTimeout(
                    getState().tooltipConfiguration.quickOpenTimeout);
            getConnection().getVTooltip()
                    .setMaxWidth(getState().tooltipConfiguration.maxWidth);
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
            getConnection().getMessageSender().setPushEnabled(
                    getState().pushConfiguration.mode.isEnabled());
        }
        if (stateChangeEvent
                .hasPropertyChanged("reconnectDialogConfiguration")) {
            getConnection().getConnectionStateHandler().configurationUpdated();
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
                    getConnection().getServerRpcQueue().flush();
                }
            };
            pollTimer.scheduleRepeating(getState().pollInterval);
        } else {
            // Ensure no more polls are sent as polling has been disabled
            getConnection().getServerRpcQueue()
                    .removeMatching(new MethodInvocation(getConnectorId(),
                            UIServerRpc.class.getName(), "poll"));
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
        getRpcProxy(DebugWindowServerRpc.class)
                .showServerDebugInfo(serverConnector);
    }

    /**
     * Sends a request to the server to print a design to the console for the
     * given component.
     *
     * @since 7.5
     * @param connector
     *            the component connector to output a declarative design for
     */
    public void showServerDesign(ServerConnector connector) {
        getRpcProxy(DebugWindowServerRpc.class).showServerDesign(connector);
    }

    @OnStateChange("theme")
    void onThemeChange() {
        final String oldTheme = activeTheme;
        final String newTheme = getState().theme;
        final String oldThemeUrl = getThemeUrl(oldTheme);
        final String newThemeUrl = getThemeUrl(newTheme);

        if (SharedUtil.equals(oldTheme, newTheme)) {
            // This should only happen on the initial load when activeTheme has
            // been updated in init.

            if (newTheme == null) {
                return;
            }

            // For the embedded case we cannot be 100% sure that the theme has
            // been loaded and that the style names have been set.

            if (findStylesheetTag(oldThemeUrl) == null) {
                // If there is no style tag, load it the normal way (the class
                // name will be added when theme has been loaded)
                replaceTheme(null, newTheme, null, newThemeUrl);
            } else if (!getWidget().getParent().getElement()
                    .hasClassName(newTheme)) {
                // If only the class name is missing, add that
                activateTheme(newTheme);
            }
            return;
        }

        getLogger().info("Changing theme from " + oldTheme + " to " + newTheme);
        replaceTheme(oldTheme, newTheme, oldThemeUrl, newThemeUrl);
    }

    /**
     * Loads the new theme and removes references to the old theme
     *
     * @since 7.4.3
     * @param oldTheme
     *            The name of the old theme
     * @param newTheme
     *            The name of the new theme
     * @param oldThemeUrl
     *            The url of the old theme
     * @param newThemeUrl
     *            The url of the new theme
     */
    protected void replaceTheme(final String oldTheme, final String newTheme,
            String oldThemeUrl, final String newThemeUrl) {

        LinkElement tagToReplace = null;

        if (oldTheme != null) {
            tagToReplace = findStylesheetTag(oldThemeUrl);

            if (tagToReplace == null) {
                getLogger()
                        .warning("Did not find the link tag for the old theme ("
                                + oldThemeUrl
                                + "), adding a new stylesheet for the new theme ("
                                + newThemeUrl + ")");
            }
        }

        if (newTheme != null) {
            loadTheme(newTheme, newThemeUrl, tagToReplace);
        } else {
            if (tagToReplace != null) {
                tagToReplace.getParentElement().removeChild(tagToReplace);
            }

            activateTheme(null);
        }

    }

    private void updateVaadinFavicon(String newTheme) {
        NodeList<Element> iconElements = querySelectorAll(
                "link[rel~=\"icon\"]");
        for (int i = 0; i < iconElements.getLength(); i++) {
            Element iconElement = iconElements.getItem(i);

            String href = iconElement.getAttribute("href");
            if (href != null && href.contains("VAADIN/themes")
                    && href.endsWith("/favicon.ico")) {
                href = href.replaceFirst("VAADIN/themes/.+?/favicon.ico",
                        "VAADIN/themes/" + newTheme + "/favicon.ico");
                iconElement.setAttribute("href", href);
            }
        }
    }

    private static native NodeList<Element> querySelectorAll(String selector)
    /*-{
        return $doc.querySelectorAll(selector);
    }-*/;

    /**
     * Finds a link tag for a style sheet with the given URL
     *
     * @since 7.3
     * @param url
     *            the URL of the style sheet
     * @return the link tag or null if no matching link tag was found
     */
    private LinkElement findStylesheetTag(String url) {
        NodeList<Element> linkTags = getHead()
                .getElementsByTagName(LinkElement.TAG);
        for (int i = 0; i < linkTags.getLength(); i++) {
            final LinkElement link = LinkElement.as(linkTags.getItem(i));
            if ("stylesheet".equals(link.getRel())
                    && "text/css".equals(link.getType())
                    && url.equals(link.getHref())) {
                return link;
            }
        }
        return null;
    }

    /**
     * Loads the given theme and replaces the given link element with the new
     * theme link element.
     *
     * @param newTheme
     *            The name of the new theme
     * @param newThemeUrl
     *            The url of the new theme
     * @param tagToReplace
     *            The link element to replace. If null, then the new link
     *            element is added at the end.
     */
    private void loadTheme(final String newTheme, final String newThemeUrl,
            final LinkElement tagToReplace) {
        LinkElement newThemeLinkElement = Document.get().createLinkElement();
        newThemeLinkElement.setRel("stylesheet");
        newThemeLinkElement.setType("text/css");
        newThemeLinkElement.setHref(newThemeUrl);
        ResourceLoader.addOnloadHandler(newThemeLinkElement,
                new ResourceLoadListener() {

                    @Override
                    public void onLoad(ResourceLoadEvent event) {
                        getLogger().info("Loading of " + newTheme + " from "
                                + newThemeUrl + " completed");

                        if (tagToReplace != null) {
                            tagToReplace.getParentElement()
                                    .removeChild(tagToReplace);
                        }
                        activateTheme(newTheme);
                    }

                    @Override
                    public void onError(ResourceLoadEvent event) {
                        getLogger().warning("Could not load theme from "
                                + getThemeUrl(newTheme));
                    }
                }, null);

        if (tagToReplace != null) {
            getHead().insertBefore(newThemeLinkElement, tagToReplace);
        } else {
            getHead().appendChild(newThemeLinkElement);
        }
    }

    /**
     * Activates the new theme. Assumes the theme has been loaded and taken into
     * use in the browser.
     *
     * @since 7.4.3
     * @param newTheme
     *            The name of the new theme
     */
    protected void activateTheme(String newTheme) {
        if (activeTheme != null) {
            getWidget().getParent().removeStyleName(activeTheme);
            VOverlay.getOverlayContainer(getConnection())
                    .removeClassName(activeTheme);
        }

        String oldThemeBase = getConnection().translateVaadinUri("theme://");

        activeTheme = newTheme;

        if (newTheme != null) {
            getWidget().getParent().addStyleName(newTheme);
            VOverlay.getOverlayContainer(getConnection())
                    .addClassName(activeTheme);

            updateVaadinFavicon(newTheme);

        }

        // Request a full resynchronization from the server to deal with legacy
        // components
        getConnection().getMessageSender().resynchronize();

        // Immediately update state and do layout while waiting for the resync
        forceStateChangeRecursively(UIConnector.this);
        getLayoutManager().forceLayout();
    }

    /**
     * Force a full recursive recheck of every connector's state variables.
     *
     * @see #forceStateChange()
     *
     * @since 7.3
     */
    protected static void forceStateChangeRecursively(
            AbstractConnector connector) {
        connector.forceStateChange();

        for (ServerConnector child : connector.getChildren()) {
            if (child instanceof AbstractConnector) {
                forceStateChangeRecursively((AbstractConnector) child);
            } else {
                getLogger().warning(
                        "Could not force state change for unknown connector type: "
                                + child.getClass().getName());
            }
        }

    }

    /**
     * Internal helper to get the theme URL for a given theme
     *
     * @since 7.3
     * @param theme
     *            the name of the theme
     * @return The URL the theme can be loaded from
     */
    private String getThemeUrl(String theme) {
        String themeUrl = getConnection()
                .translateVaadinUri(ApplicationConstants.VAADIN_PROTOCOL_PREFIX
                        + "themes/" + theme + "/styles" + ".css");
        // Parameter appended to bypass caches after version upgrade.
        themeUrl += "?v=" + Version.getFullVersion();
        return themeUrl;

    }

    /**
     * Returns the name of the theme currently in used by the UI
     *
     * @since 7.3
     * @return the theme name used by this UI
     */
    public String getActiveTheme() {
        return activeTheme;
    }

    /**
     * Returns whether HTML5 DnD extensions {@link DragSourceExtensionConnector}
     * and {@link DropTargetExtensionConnector} and alike should be enabled for
     * mobile devices.
     * <p>
     * By default, it is disabled.
     *
     * @return {@code true} if enabled, {@code false} if not
     * @since 8.1
     */
    public boolean isMobileHTML5DndEnabled() {
        return getState().enableMobileHTML5DnD;
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIConnector.class.getName());
    }

    private void setWindowOrderAndPosition() {
        if (windowOrderRegistration != null) {
            windowOrderRegistration.removeHandler();
        }
        WindowOrderCollector collector = new WindowOrderCollector();
        HandlerRegistration registration = VWindow
                .addWindowOrderHandler(collector);
        for (ComponentConnector c : getChildComponents()) {
            if (c instanceof WindowConnector) {
                WindowConnector wc = (WindowConnector) c;
                wc.setWindowOrderAndPosition();
            }
        }
        windowOrderHandler.onWindowOrderChange(
                new WindowOrderEvent(collector.getWindows()));
        registration.removeHandler();
        windowOrderRegistration = VWindow
                .addWindowOrderHandler(windowOrderHandler);
    }

    private static class WindowOrderCollector
            implements WindowOrderHandler, Comparator<VWindow> {

        private HashSet<VWindow> windows = new HashSet<>();

        @Override
        public void onWindowOrderChange(WindowOrderEvent event) {
            windows.addAll(Arrays.asList(event.getWindows()));
        }

        @Override
        public int compare(VWindow window1, VWindow window2) {
            if (window1.getWindowOrder() == window2.getWindowOrder()) {
                return 0;
            }
            return window1.getWindowOrder() > window2.getWindowOrder() ? 1 : -1;
        }

        ArrayList<VWindow> getWindows() {
            ArrayList<VWindow> result = new ArrayList<>();
            result.addAll(windows);
            Collections.sort(result, this);
            return result;
        }
    }

    private static native void initializeMobileDndPolyfill()
    /*-{
         var conf = new Object();
         // this is needed or the drag image will offset to weird place in for grid rows
         conf['dragImageCenterOnTouch'] = true;
         $wnd.DragDropPolyfill.Initialize(conf);
    }-*/;
}
