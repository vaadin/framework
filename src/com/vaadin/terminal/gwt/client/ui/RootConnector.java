/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;

public class RootConnector extends AbstractComponentContainerConnector {

    private static final String CLICK_EVENT_IDENTIFIER = PanelConnector.CLICK_EVENT_IDENTIFIER;

    @Override
    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        getWidget().rendering = true;
        // As VView is not created in the same way as all other paintables we
        // have to set the id here
        setId(uidl.getId());
        getWidget().id = uidl.getId();
        boolean firstPaint = getWidget().connection == null;
        getWidget().connection = client;

        getWidget().immediate = getState().isImmediate();
        getWidget().resizeLazy = uidl.hasAttribute(VView.RESIZE_LAZY);
        String newTheme = uidl.getStringAttribute("theme");
        if (getWidget().theme != null && !newTheme.equals(getWidget().theme)) {
            // Complete page refresh is needed due css can affect layout
            // calculations etc
            getWidget().reloadHostPage();
        } else {
            getWidget().theme = newTheme;
        }
        // this also implicitly removes old styles
        getWidget()
                .setStyleName(
                        getWidget().getStylePrimaryName() + " "
                                + getState().getStyle());

        clickEventHandler.handleEventHandlerRegistration(client);

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
                        VView.goTo(url);
                    }
                });
            } else if ("_self".equals(target)) {
                // This window is closing (for sure). Only other opens are
                // relevant in this change. See #3558, #2144
                isClosed = true;
                VView.goTo(url);
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

        // Draw this application level window
        UIDL childUidl = uidl.getChildUIDL(childIndex);
        final ComponentConnector lo = client.getPaintable(childUidl);

        if (getWidget().layout != null) {
            if (getWidget().layout != lo) {
                // remove old
                client.unregisterPaintable(getWidget().layout);
                // add new
                getWidget().setWidget(lo.getWidget());
                getWidget().layout = lo;
            }
        } else {
            getWidget().setWidget(lo.getWidget());
            getWidget().layout = lo;
        }

        getWidget().layout.updateFromUIDL(childUidl, client);

        // Save currently open subwindows to track which will need to be closed
        final HashSet<VWindow> removedSubWindows = new HashSet<VWindow>(
                getWidget().subWindows);

        // Handle other UIDL children
        while ((childUidl = uidl.getChildUIDL(++childIndex)) != null) {
            String tag = childUidl.getTag().intern();
            if (tag == "actions") {
                if (getWidget().actionHandler == null) {
                    getWidget().actionHandler = new ShortcutActionHandler(
                            getWidget().id, client);
                }
                getWidget().actionHandler.updateActionMap(childUidl);
            } else if (tag == "execJS") {
                String script = childUidl.getStringAttribute("script");
                VView.eval(script);
            } else if (tag == "notifications") {
                for (final Iterator<?> it = childUidl.getChildIterator(); it
                        .hasNext();) {
                    final UIDL notification = (UIDL) it.next();
                    VNotification.showNotification(client, notification);
                }
            } else {
                // subwindows
                final WindowConnector w = (WindowConnector) client
                        .getPaintable(childUidl);
                VWindow windowWidget = w.getWidget();
                if (getWidget().subWindows.contains(windowWidget)) {
                    removedSubWindows.remove(windowWidget);
                } else {
                    getWidget().subWindows.add(windowWidget);
                }
                w.updateFromUIDL(childUidl, client);
            }
        }

        // Close old windows which where not in UIDL anymore
        for (final Iterator<VWindow> rem = removedSubWindows.iterator(); rem
                .hasNext();) {
            final VWindow w = rem.next();
            client.unregisterPaintable(ConnectorMap.get(getConnection())
                    .getConnector(w));
            getWidget().subWindows.remove(w);
            w.hide();
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

        getWidget().onResize();

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

        // Safari workaround must be run after scrollTop is updated as it sets
        // scrollTop using a deferred command.
        if (BrowserInfo.get().isSafari()) {
            Util.runWebkitOverflowAutoFix(getWidget().getElement());
        }

        getWidget().scrollIntoView(uidl);

        if (uidl.hasAttribute(VView.FRAGMENT_VARIABLE)) {
            getWidget().currentFragment = uidl
                    .getStringAttribute(VView.FRAGMENT_VARIABLE);
            if (!getWidget().currentFragment.equals(History.getToken())) {
                History.newItem(getWidget().currentFragment, true);
            }
        } else {
            // Initial request for which the server doesn't yet have a fragment
            // (and haven't shown any interest in getting one)
            getWidget().currentFragment = History.getToken();

            // Include current fragment in the next request
            client.updateVariable(getWidget().id, VView.FRAGMENT_VARIABLE,
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
        // For backwards compatibility with static index pages only.
        // No longer added by AbstractApplicationServlet/Portlet
        root.removeStyleName("v-app-loading");

        String themeUri = applicationConnection.getConfiguration()
                .getThemeUri();
        String themeName = themeUri.substring(themeUri.lastIndexOf('/'));
        themeName = themeName.replaceAll("[^a-zA-Z0-9]", "");
        root.addStyleName("v-theme-" + themeName);

        root.add(getWidget());

        if (applicationConnection.getConfiguration().isStandalone()) {
            // set focus to iview element by default to listen possible keyboard
            // shortcuts. For embedded applications this is unacceptable as we
            // don't want to steal focus from the main page nor we don't want
            // side-effects from focusing (scrollIntoView).
            getWidget().getElement().focus();
        }
    }

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    public void updateCaption(ComponentConnector component, UIDL uidl) {
        // NOP The main view never draws caption for its layout
    }

    @Override
    public VView getWidget() {
        return (VView) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VView.class);
    }

}
