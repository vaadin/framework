/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.root;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.ClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler;
import com.vaadin.terminal.gwt.client.ui.layout.MayScrollChildren;
import com.vaadin.terminal.gwt.client.ui.notification.VNotification;
import com.vaadin.terminal.gwt.client.ui.window.WindowConnector;
import com.vaadin.ui.Root;

@Connect(value = Root.class, loadStyle = LoadStyle.EAGER)
public class RootConnector extends AbstractComponentContainerConnector
        implements Paintable, MayScrollChildren {

    private RootServerRPC rpc = RpcProxy.create(RootServerRPC.class, this);

    private HandlerRegistration childStateChangeHandlerRegistration;

    private final StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            // TODO Should use a more specific handler that only reacts to
            // size changes
            onChildSizeChange();
        }
    };

    @Override
    protected void init() {
        super.init();
    }

    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        ConnectorMap paintableMap = ConnectorMap.get(getConnection());
        getWidget().rendering = true;
        getWidget().id = getConnectorId();
        boolean firstPaint = getWidget().connection == null;
        getWidget().connection = client;

        getWidget().immediate = getState().isImmediate();
        getWidget().resizeLazy = uidl.hasAttribute(VRoot.RESIZE_LAZY);
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
        if (getState().hasStyles()) {
            for (String style : getState().getStyles()) {
                styles += style + " ";
            }
        }
        if (!client.getConfiguration().isStandalone()) {
            styles += getWidget().getStylePrimaryName() + "-embedded";
        }
        getWidget().setStyleName(styles.trim());

        clickEventHandler.handleEventHandlerRegistration();

        if (!getWidget().isEmbedded() && getState().getCaption() != null) {
            // only change window title if we're in charge of the whole page
            com.google.gwt.user.client.Window.setTitle(getState().getCaption());
        }

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
                    public void execute() {
                        VRoot.goTo(url);
                    }
                });
            } else if ("_self".equals(target)) {
                // This window is closing (for sure). Only other opens are
                // relevant in this change. See #3558, #2144
                isClosed = true;
                VRoot.goTo(url);
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
            } else if (tag == "execJS") {
                String script = childUidl.getStringAttribute("script");
                VRoot.eval(script);
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

        if (uidl.hasAttribute(VRoot.FRAGMENT_VARIABLE)) {
            getWidget().currentFragment = uidl
                    .getStringAttribute(VRoot.FRAGMENT_VARIABLE);
            if (!getWidget().currentFragment.equals(History.getToken())) {
                History.newItem(getWidget().currentFragment, true);
            }
        } else {
            // Initial request for which the server doesn't yet have a fragment
            // (and haven't shown any interest in getting one)
            getWidget().currentFragment = History.getToken();

            // Include current fragment in the next request
            client.updateVariable(getWidget().id, VRoot.FRAGMENT_VARIABLE,
                    getWidget().currentFragment, false);
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

        root.addStyleName("v-theme-"
                + applicationConnection.getConfiguration().getThemeName());

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
            rpc.click(mouseDetails);
        }

    };

    public void updateCaption(ComponentConnector component) {
        // NOP The main view never draws caption for its layout
    }

    @Override
    public VRoot getWidget() {
        return (VRoot) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VRoot.class);
    }

    protected ComponentConnector getContent() {
        return (ComponentConnector) getState().getContent();
    }

    protected void onChildSizeChange() {
        ComponentConnector child = getContent();
        Style childStyle = child.getWidget().getElement().getStyle();
        /*
         * Must set absolute position if the child has relative height and
         * there's a chance of horizontal scrolling as some browsers will
         * otherwise not take the scrollbar into account when calculating the
         * height. Assuming v-view does not have an undefined width for now, see
         * #8460.
         */
        if (child.isRelativeHeight() && !BrowserInfo.get().isIE9()) {
            childStyle.setPosition(Position.ABSOLUTE);
        } else {
            childStyle.clearPosition();
        }
    }

    /**
     * Checks if the given sub window is a child of this Root Connector
     * 
     * @deprecated Should be replaced by a more generic mechanism for getting
     *             non-ComponentConnector children
     * @param wc
     * @return
     */
    @Deprecated
    public boolean hasSubWindow(WindowConnector wc) {
        return getChildren().contains(wc);
    }

    /**
     * Return an iterator for current subwindows. This method is meant for
     * testing purposes only.
     * 
     * @return
     */
    public List<WindowConnector> getSubWindows() {
        ArrayList<WindowConnector> windows = new ArrayList<WindowConnector>();
        for (ComponentConnector child : getChildren()) {
            if (child instanceof WindowConnector) {
                windows.add((WindowConnector) child);
            }
        }
        return windows;
    }

    @Override
    public RootState getState() {
        return (RootState) super.getState();
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

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
            getWidget().setWidget(newChild.getWidget());
            childStateChangeHandlerRegistration = newChild
                    .addStateChangeHandler(childStateChangeHandler);
            // Must handle new child here as state change events are already
            // fired
            onChildSizeChange();
        }

        for (ComponentConnector c : getChildren()) {
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
            public void execute() {
                componentConnector.getWidget().getElement().scrollIntoView();
            }
        });
    }

}
